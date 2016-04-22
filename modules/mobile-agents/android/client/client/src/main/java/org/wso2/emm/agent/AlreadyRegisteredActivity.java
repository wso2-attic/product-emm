/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.emm.agent;

import java.util.Map;

import android.app.Activity;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.events.EventRegistry;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.services.LocalNotification;
import org.wso2.emm.agent.utils.CommonDialogUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.CommonUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Activity which handles user un-registration from the MDM server.
 */
public class AlreadyRegisteredActivity extends SherlockActivity implements APIResultCallBack {

	private static final String TAG = AlreadyRegisteredActivity.class.getSimpleName();
	private static final int ACTIVATION_REQUEST = 47;
	private String regId;
	private Context context;
	private Resources resources;
	private ProgressDialog progressDialog;
	private Button btnUnregister;
	private TextView txtRegText;
	private static final int TAG_BTN_UNREGISTER = 0;
	private static final int TAG_BTN_RE_REGISTER = 2;
	private boolean freshRegFlag = false;
	private boolean isUnregisterBtnClicked = false;
	private AlertDialog.Builder alertDialog;
	private boolean isPollingStarted;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName cdmDeviceAdmin;
	private DeviceInfo info;
	private RelativeLayout unregisterLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_already_registered);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.custom_sherlock_bar);
		getSupportActionBar().setTitle(Constants.EMPTY_STRING);

		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		cdmDeviceAdmin = new ComponentName(this, AgentDeviceAdminReceiver.class);
		context = this;
		resources = context.getResources();
		info = new DeviceInfo(context);
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			if (extras.containsKey(getResources().getString(R.string.intent_extra_fresh_reg_flag))) {
				freshRegFlag = extras.getBoolean(getResources().getString(R.string.intent_extra_fresh_reg_flag));
			}
		}
		if(!EventRegistry.eventListeningStarted) {
			EventRegistry registerEvent = new EventRegistry(this);
			registerEvent.register();
		}
		String registrationId = Preference.getString(context, Constants.PreferenceFlag.REG_ID);

		if (registrationId != null && !registrationId.isEmpty()) {
			regId = registrationId;
		} else{
			regId = info.getDeviceId();
		}

		if (freshRegFlag) {
			Preference.putBoolean(context, Constants.PreferenceFlag.REGISTERED, true);
			if (!isDeviceAdminActive()) {
				startDeviceAdminPrompt(cdmDeviceAdmin);
			}
			freshRegFlag = false;

		} else if (Preference.getBoolean(context, Constants.PreferenceFlag.REGISTERED)) {
			if (isDeviceAdminActive()) {
				startPolling();
			}
		}

		txtRegText = (TextView) findViewById(R.id.txtRegText);
		btnUnregister = (Button) findViewById(R.id.btnUnreg);
		btnUnregister.setTag(TAG_BTN_UNREGISTER);
		btnUnregister.setOnClickListener(onClickListenerButtonClicked);
		unregisterLayout = (RelativeLayout) findViewById(R.id.unregisterLayout);
		if (Constants.HIDE_UNREGISTER_BUTTON) {
			unregisterLayout.setVisibility(View.GONE);
		}
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					startUnRegistration();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
				default:
					break;
			}
		}
	};

	private OnClickListener onClickListenerButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int iTag = (Integer) view.getTag();

			switch (iTag) {

				case TAG_BTN_UNREGISTER:
					showUnregisterDialog();
					break;

				case TAG_BTN_RE_REGISTER:
					loadServerDetailsActivity();
					break;

				default:
					break;
			}

		}
	};


	/**
	 * Send unregistration request.
	 */
	private void startUnRegistration() {
		final Context context = AlreadyRegisteredActivity.this;
		isUnregisterBtnClicked = true;

		progressDialog = ProgressDialog.show(AlreadyRegisteredActivity.this,
						getResources().getString(R.string.dialog_message_unregistering),
						getResources().getString(R.string.dialog_message_please_wait),
						true);

		if (regId != null && !regId.isEmpty()) {
			if (CommonUtils.isNetworkAvailable(context)) {
				String serverIP = Preference.getString(AlreadyRegisteredActivity.this, Constants.PreferenceFlag.IP);
				if (serverIP != null && !serverIP.isEmpty()) {
					stopPolling();
					ServerConfig utils = new ServerConfig();
					utils.setServerIP(serverIP);

					CommonUtils.callSecuredAPI(AlreadyRegisteredActivity.this,
					                           utils.getAPIServerURL(context) + Constants.UNREGISTER_ENDPOINT + regId,
					                           HTTP_METHODS.DELETE,
					                           null, AlreadyRegisteredActivity.this,
					                           Constants.UNREGISTER_REQUEST_CODE);
				} else {
					Log.e(TAG, "There is no valid IP to contact the server");
					CommonDialogUtils.stopProgressDialog(progressDialog);
					CommonDialogUtils.showNetworkUnavailableMessage(AlreadyRegisteredActivity.this);
				}
			} else {
				Log.e(TAG, "Registration ID is not available");
				CommonDialogUtils.stopProgressDialog(progressDialog);
				CommonDialogUtils.showNetworkUnavailableMessage(AlreadyRegisteredActivity.this);
			}

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		String deviceType = Preference.getString(context, getResources().
				getString(R.string.shared_pref_reg_type));
		if (deviceType != null && !deviceType.isEmpty()) {
			if (Constants.OWNERSHIP_BYOD.equalsIgnoreCase(deviceType)) {
				getSupportMenuInflater().inflate(R.menu.sherlock_menu_byod, menu);
			} else {
				getSupportMenuInflater().inflate(R.menu.sherlock_menu_cope, menu);
			}
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.info_setting:
				loadDeviceInfoActivity();
				return true;
			case R.id.pin_setting:
				loadPinCodeActivity();
				return true;
			case R.id.ip_setting:
				loadServerDetailsActivity();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		loadHomeScreen();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			loadHomeScreen();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			loadHomeScreen();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Calling onResume");
		}

		boolean isRegistered = Preference.getBoolean(context, Constants.PreferenceFlag.REGISTERED);

		if (isRegistered) {
			if (CommonUtils.isNetworkAvailable(context)) {

				String serverIP = Preference.getString(context, Constants.PreferenceFlag.IP);
				regId = Preference.getString(context, Constants.PreferenceFlag.REG_ID);

				if (regId != null) {
					if (regId.isEmpty() && isUnregisterBtnClicked) {
						initiateUnregistration();
					} else if (serverIP != null && !serverIP.isEmpty()) {
						ServerConfig utils = new ServerConfig();
						utils.setServerIP(serverIP);
						if (utils.getHostFromPreferences(context) != null && !utils.getHostFromPreferences(context).isEmpty()) {
							CommonUtils.callSecuredAPI(AlreadyRegisteredActivity.this,
							                           utils.getAPIServerURL(context) + Constants.IS_REGISTERED_ENDPOINT + regId,
							                           HTTP_METHODS.GET,
							                           null, AlreadyRegisteredActivity.this,
							                           Constants.IS_REGISTERED_REQUEST_CODE);
						} else {
							try {
								CommonUtils.clearAppData(context);
							} catch (AndroidAgentException e) {
								String msg = "Device already dis-enrolled.";
								Log.e(TAG, msg, e);
							}
							loadServerDetailsActivity();
						}
					} else {
						Log.e(TAG, "There is no valid IP to contact server");
					}
				}
			} else {
				CommonDialogUtils.showNetworkUnavailableMessage(AlreadyRegisteredActivity.this);
			}
		}
	}

	/**
	 * Displays an internal server error message to the user.
	 */
	private void displayInternalServerError() {
		alertDialog = CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
				getResources().getString(R.string.title_head_connection_error),
				getResources().getString(R.string.error_internal_server),
				getResources().getString(R.string.button_ok),
				null);
		alertDialog.show();
	}

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {

		String responseStatus;
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "onReceiveAPIResult-requestcode: " + requestCode);
		}


		if (requestCode == Constants.UNREGISTER_REQUEST_CODE) {
			stopProgressDialog();
			if (result != null) {
				responseStatus = result.get(Constants.STATUS);
				if (responseStatus != null && Constants.Status.SUCCESSFUL.equals(responseStatus)) {
					stopPolling();
					initiateUnregistration();
				} else if (Constants.Status.INTERNAL_SERVER_ERROR.equals(responseStatus)) {
					startPolling();
					displayInternalServerError();
				} else {
					startPolling();
					loadAuthenticationErrorActivity();
				}
			} else {
				startPolling();
				loadAuthenticationErrorActivity();
			}
		}

		if (requestCode == Constants.IS_REGISTERED_REQUEST_CODE) {
			stopProgressDialog();
			if (result != null) {
				responseStatus = result.get(Constants.STATUS);
				if (Constants.Status.INTERNAL_SERVER_ERROR.equals(responseStatus)) {
					displayInternalServerError();
				} else if (Constants.Status.SUCCESSFUL.equals(responseStatus)) {
					if (Constants.DEBUG_MODE_ENABLED) {
						Log.d(TAG, "Device has already enrolled");
					}
					if (isDeviceAdminActive()) {
						startPolling();
					}
				} else {
					stopPolling();
					initiateUnregistration();
					loadServerDetailsActivity();
				}
			}
		}
	}

	/**
	 * Load device home screen.
	 */

	private void loadHomeScreen() {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(i);
		super.onBackPressed();
	}

	/**
	 * Initiate unregistration.
	 */
	private void initiateUnregistration() {
		txtRegText.setText(R.string.register_text_view_text_unregister);
		btnUnregister.setText(R.string.register_button_text);
		btnUnregister.setTag(TAG_BTN_RE_REGISTER);
		btnUnregister.setOnClickListener(onClickListenerButtonClicked);
		CommonUtils.disableAdmin(context);
	}

	/**
	 * Start device admin activation request.
	 *
	 * @param cdmDeviceAdmin - Device admin component.
	 */
	private void startDeviceAdminPrompt(ComponentName cdmDeviceAdmin) {
		Intent deviceAdminIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cdmDeviceAdmin);
		deviceAdminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
		                           getResources().getString(R.string.device_admin_enable_alert));
		startActivityForResult(deviceAdminIntent, ACTIVATION_REQUEST);
	}

	/**
	 * Display unregistration confirmation dialog.
	 */
	private void showUnregisterDialog() {
		AlertDialog.Builder alertDialog =
				CommonDialogUtils.getAlertDialogWithTwoButtonAndTitle(context,
                      null,
                      getResources().getString(R.string.dialog_unregister),
                      getResources().getString(R.string.yes),
                      getResources().getString(R.string.no),
                      dialogClickListener, dialogClickListener);
		alertDialog.show();
	}

	/**
	 * Load device info activity.
	 */
	private void loadDeviceInfoActivity() {
		Intent intent =
				new Intent(AlreadyRegisteredActivity.this,
						DisplayDeviceInfoActivity.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
				AlreadyRegisteredActivity.class.getSimpleName());
		startActivity(intent);
	}

	/**
	 * Load server details activity.
	 */
	private void loadServerDetailsActivity() {
		Preference.putString(context, Constants.PreferenceFlag.IP, null);
		Intent intent = new Intent(
				AlreadyRegisteredActivity.this,
				ServerDetails.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_regid),
				regId);
		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
				AlreadyRegisteredActivity.class.getSimpleName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 * Load PIN code activity.
	 */
	private void loadPinCodeActivity() {
		Intent intent =
				new Intent(AlreadyRegisteredActivity.this, PinCodeActivity.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
		                AlreadyRegisteredActivity.class.getSimpleName());
		startActivity(intent);
	}

	/**
	 * Loads authentication error activity.
	 */
	private void loadAuthenticationErrorActivity() {
		Intent intent =
				new Intent(AlreadyRegisteredActivity.this,
						AuthenticationErrorActivity.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_regid), regId);
		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
				AlreadyRegisteredActivity.class.getSimpleName());
		startActivity(intent);
	}

	/**
	 * Stops server polling task.
	 */
	private void stopPolling() {
		String notifier = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
		if(Constants.NOTIFIER_LOCAL.equals(notifier)) {
			LocalNotification.stopPolling(context);
		}
	}

	/**
	 * Starts server polling task.
	 */
	private void startPolling() {
		String notifier = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
		if(Constants.NOTIFIER_LOCAL.equals(notifier)) {
			LocalNotification.startPolling(context);
		}
	}

	private void stopProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ACTIVATION_REQUEST) {
			if (resultCode == Activity.RESULT_OK) {
				CommonUtils.callSystemApp(context, null, null, null);
				Log.i("onActivityResult", "Administration enabled!");
			} else {
				Log.i("onActivityResult", "Administration enable FAILED!");
			}
		}
	}

	private boolean isDeviceAdminActive() {
		return devicePolicyManager.isAdminActive(cdmDeviceAdmin);
	}

}
