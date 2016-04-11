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
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.services.DeviceInfoPayload;
import org.wso2.emm.agent.utils.CommonDialogUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.CommonUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Activity which handles user enrollment.
 */
public class RegistrationActivity extends Activity implements APIResultCallBack {
	private Context context;
	private ProgressDialog progressDialog;
	private AlertDialog.Builder alertDialog;
	private DeviceInfoPayload deviceInfoBuilder;
	private Resources resources;
	private String deviceIdentifier;
    private String TAG = RegistrationActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;

		progressDialog = CommonDialogUtils.showPrgressDialog(RegistrationActivity.this,
		                                                     getResources().getString(R.string.dialog_enrolling),
		                                                     getResources().getString(R.string.dialog_please_wait),
		                                                     null);
		progressDialog.show();

		deviceInfoBuilder = new DeviceInfoPayload(context);
		resources = context.getResources();
		DeviceInfo deviceInfo = new DeviceInfo(context);
		deviceIdentifier = deviceInfo.getDeviceId();
		Preference.putString(context, Constants.PreferenceFlag.REG_ID, deviceIdentifier);
		registerDevice();
	}

	private void registerDevice() {
		String type = Preference.getString(context,
		                                   context.getResources().getString(R.string.shared_pref_reg_type));
		String username = Preference.getString(context,
		                                       context.getResources().getString(R.string.username));
		try {
			deviceInfoBuilder.build(type, username);
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Error occurred while building the device info payload.", e);
		}

		// Check network connection availability before calling the API.
		if (CommonUtils.isNetworkAvailable(context)) {
			// Call device registration API.
			String ipSaved = Preference.getString(context.getApplicationContext(),Constants.PreferenceFlag.IP);

			if (ipSaved != null && !ipSaved.isEmpty()) {
				ServerConfig utils = new ServerConfig();
				utils.setServerIP(ipSaved);

				CommonUtils.callSecuredAPI(RegistrationActivity.this,
				                           utils.getAPIServerURL(context) + Constants.REGISTER_ENDPOINT,
				                           HTTP_METHODS.POST,
				                           deviceInfoBuilder.getDeviceInfoPayload(),
				                           RegistrationActivity.this,
				                           Constants.REGISTER_REQUEST_CODE);
			} else {
				Log.e(TAG, "There is no valid IP to contact the server");
				CommonDialogUtils.stopProgressDialog(progressDialog);
				CommonDialogUtils.showNetworkUnavailableMessage(RegistrationActivity.this);
			}
		} else {
			CommonDialogUtils.stopProgressDialog(progressDialog);
			CommonDialogUtils.showNetworkUnavailableMessage(RegistrationActivity.this);
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}

	private DialogInterface.OnClickListener registrationFailedOKBtnClickListerner =
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0,
				                    int arg1) {
					loadAuthenticationErrorActivity();
				}
	};
	
	/**
	 * Loads Already registered activity.
	 */
	private void loadAlreadyRegisteredActivity(){
		Intent intent =
				new Intent(RegistrationActivity.this,
				           AlreadyRegisteredActivity.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_fresh_reg_flag), true);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	/**
	 * Display connectivity error.
	 */
	private void displayConnectionError(){
		alertDialog = CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                            getResources().getString(R.string.title_head_connection_error),
                            getResources().getString(R.string.error_internal_server),
                            getResources().getString(R.string.button_ok),
                            registrationFailedOKBtnClickListerner);
		alertDialog.show();
	}
	
	/**
	 * Display internal server error.
	 */
	private void displayInternalServerError(){
		alertDialog = CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                      getResources().getString(R.string.title_head_registration_error),
                      getResources().getString(R.string.error_for_all_unknown_registration_failures),
                      getResources().getString(R.string.button_ok),
                      registrationFailedOKBtnClickListerner);
	}

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		DeviceInfo info = new DeviceInfo(context);
		if (Constants.REGISTER_REQUEST_CODE == requestCode) {
			String responseStatus;
			if (result != null) {
				responseStatus = result.get(Constants.STATUS);
				Preference.putString(context, Constants.PreferenceFlag.REG_ID, info.getDeviceId());
				if (Constants.Status.SUCCESSFUL.equals(responseStatus)) {
					if (Constants.NOTIFIER_GCM.equals(Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE))) {
						registerGCM();
					} else {
						getEffectivePolicy();
					}
				} else {
					displayInternalServerError();
				}
			} else {
				displayConnectionError();
			}
		} else if (Constants.POLICY_REQUEST_CODE == requestCode) {
			CommonDialogUtils.stopProgressDialog(progressDialog);
			loadAlreadyRegisteredActivity();
		} else if (requestCode == Constants.GCM_REGISTRATION_ID_SEND_CODE && result != null) {
			String status = result.get(Constants.STATUS_KEY);
			if (!Constants.Status.SUCCESSFUL.equals(status)) {
				displayConnectionError();
			} else {
				getEffectivePolicy();
			}
		} else {
			CommonDialogUtils.stopProgressDialog(progressDialog);
		}
	}

	/**
     * This will start the GCM flow by registering the device with Google and sending the
     * registration ID to MDM. This is done in a Async task as a network call may be done, and
     * it should be done out side the UI thread. After retrieving the registration Id, it is send
     * to the MDM server so that it can send notifications to the device.
     */
	private void registerGCM() {
		new AsyncTask<Void, Void, String>() {
			String senderId = Preference.getString(context, context.getResources().getString(R.string.shared_pref_sender_id));
			GCMRegistrationManager registrationManager = new GCMRegistrationManager(RegistrationActivity.this, senderId);

			@Override
			protected String doInBackground(Void... params) {
				return registrationManager.registerWithGoogle();
			}

			@Override
			protected void onPostExecute(String regId) {
				Preference.putString(context, Constants.GCM_REG_ID, regId);
				if (regId != null) {
					try {
						sendRegistrationId();
					} catch (AndroidAgentException e) {
						Log.e(TAG, "Error while sending registration Id");
					}
				} else {
					try {
						CommonUtils.clearAppData(context);
						displayInternalServerError();
					} catch (AndroidAgentException e) {
						Log.e(TAG, "Failed to clear app data", e);
					}
				}
			}
		}.execute();
	}

	/**
	 * This is used to send the registration Id to MDM server so that the server
	 * can use it as a reference to identify the device when sending messages to
	 * Google server.
	 *
	 * @throws AndroidAgentException
	 */
	public void sendRegistrationId() throws AndroidAgentException {
		DeviceInfo deviceInfo = new DeviceInfo(context);
		DeviceInfoPayload deviceInfoPayload = new DeviceInfoPayload(context);
		deviceInfoPayload.build();

		String replyPayload = deviceInfoPayload.getDeviceInfoPayload();
		String ipSaved = Preference.getString(context, Constants.PreferenceFlag.IP);

		if (ipSaved != null && !ipSaved.isEmpty()) {
			ServerConfig utils = new ServerConfig();
			utils.setServerIP(ipSaved);

			String url = utils.getAPIServerURL(context) + Constants.DEVICE_ENDPOINT + deviceInfo.getDeviceId();

			CommonUtils.callSecuredAPI(context, url, org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.PUT,
			                           replyPayload, RegistrationActivity.this, Constants.GCM_REGISTRATION_ID_SEND_CODE);
		} else {
			Log.e(TAG, "There is no valid IP to contact the server");
		}
	}

	/**
	 * Loads Authentication error activity.
	 */
	private void loadAuthenticationErrorActivity() {
		Preference.putString(context, Constants.PreferenceFlag.IP, null);
		Intent intent = new Intent(
				RegistrationActivity.this,
				ServerDetails.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
		                RegistrationActivity.class.getSimpleName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	/**
	 *  This method is used to invoke getEffectivePolicy in the backend
	 */
	private void getEffectivePolicy() {
		if (CommonUtils.isNetworkAvailable(context)) {
			String ipSaved = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);

			if (ipSaved != null && !ipSaved.isEmpty()) {
				ServerConfig utils = new ServerConfig();
				utils.setServerIP(ipSaved);

				CommonUtils.callSecuredAPI(RegistrationActivity.this,
				                           utils.getAPIServerURL(context) + Constants.POLICY_ENDPOINT + deviceIdentifier,
				                           HTTP_METHODS.GET,
				                           null,
				                           RegistrationActivity.this,
				                           Constants.POLICY_REQUEST_CODE);
			} else {
				Log.e(TAG, "There is no valid IP to contact the server");
				CommonDialogUtils.stopProgressDialog(progressDialog);
				CommonDialogUtils.showNetworkUnavailableMessage(RegistrationActivity.this);
			}
		} else {
			CommonDialogUtils.stopProgressDialog(progressDialog);
			CommonDialogUtils.showNetworkUnavailableMessage(RegistrationActivity.this);
		}
	}

}
