/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.cdm.agent;

import java.util.HashMap;
import java.util.Map;

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.services.Operation;
import org.wso2.cdm.agent.services.WSO2DeviceAdminReceiver;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.ServerUtils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class AlreadyRegisteredActivity extends SherlockActivity implements APIResultCallBack {
	
	private String TAG = AlreadyRegisteredActivity.class.getSimpleName();
	
	AsyncTask<Void, Void, Void> mRegisterTask;
	AsyncTask<Void, Void, Void> mCheckRegisterTask;
	static final int ACTIVATION_REQUEST = 47; // identifies our request id
	DevicePolicyManager devicePolicyManager;
	ComponentName demoDeviceAdmin;
	String regId = "";
	Context context = null;
	boolean state = false;
	ProgressDialog progressDialog;
	private Button btnUnregister;
	private TextView txtRegText;
	// private ImageView optionBtn;
	private final int TAG_BTN_UNREGISTER = 0;
	private final int TAG_BTN_OPTIONS = 1;
	private final int TAG_BTN_RE_REGISTER = 2;
	ActionBar actionbar;
	boolean unregState = false;
	boolean freshRegFlag = false;
	boolean isUnregisterBtnClicked = false;
	Operation operation;
	AlertDialog.Builder alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_already_registered);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.custom_sherlock_bar);
		getSupportActionBar().setTitle(R.string.empty_app_title);
		View homeIcon = findViewById(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.id.home
				: R.id.abs__home);
		((View) homeIcon.getParent()).setVisibility(View.GONE);

		devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(this,
				WSO2DeviceAdminReceiver.class);
		operation = new Operation(AlreadyRegisteredActivity.this);
		context = AlreadyRegisteredActivity.this;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(getResources().getString(
					R.string.intent_extra_fresh_reg_flag))) {
				freshRegFlag = extras.getBoolean(getResources().getString(
						R.string.intent_extra_fresh_reg_flag));
			}

		}
		
		
		String regIden=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_regId));
		if(!regIden.equals("")){
			regId=regIden;
		}

		if (freshRegFlag) {
			try {
				SharedPreferences mainPref = AlreadyRegisteredActivity.this
						.getSharedPreferences(
								getResources().getString(
										R.string.shared_pref_package),
								Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();
				Log.e("freshRegFlag","1");
				editor.putString(
						getResources().getString(
								R.string.shared_pref_registered), "1");
				editor.commit();
				
				
				if (!devicePolicyManager.isAdminActive(demoDeviceAdmin)) {
					Intent intent1 = new Intent(
							DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
							demoDeviceAdmin);
					intent1.putExtra(
							DevicePolicyManager.EXTRA_ADD_EXPLANATION,
							getResources().getString(
									R.string.device_admin_enable_alert));
					startActivityForResult(intent1, ACTIVATION_REQUEST);
				}
				//operation.executePolicy();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			freshRegFlag = false;
		}
		txtRegText = (TextView) findViewById(R.id.txtRegText);

		btnUnregister = (Button) findViewById(R.id.btnUnreg);
		btnUnregister.setTag(TAG_BTN_UNREGISTER);
		btnUnregister.setOnClickListener(onClickListener_BUTTON_CLICKED);

	}
	
	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case DialogInterface.BUTTON_POSITIVE:
				dialog.dismiss();
				break;

			case DialogInterface.BUTTON_NEGATIVE:
				startUnRegistration();
				break;
			}
		}
	};

	OnClickListener onClickListener_BUTTON_CLICKED = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int iTag = (Integer) view.getTag();

			switch (iTag) {

			case TAG_BTN_UNREGISTER:
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AlreadyRegisteredActivity.this);
				builder.setMessage(getResources().getString(R.string.dialog_unregister))
						.setNegativeButton(getResources().getString(R.string.yes), dialogClickListener)
						.setPositiveButton(getResources().getString(R.string.no), dialogClickListener).show();
				break;

			case TAG_BTN_OPTIONS:
				break;
			case TAG_BTN_RE_REGISTER:
				Intent intent = new Intent(AlreadyRegisteredActivity.this,
						ServerDetails.class);
				intent.putExtra(
						getResources().getString(R.string.intent_extra_regid),
						regId);
				startActivity(intent);
				finish();
				break;

			default:
				break;
			}

		}
	};
	
	DialogInterface.OnClickListener isRegisteredFailedOKBtnClickListerner = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			
			Intent intent = new Intent(AlreadyRegisteredActivity.this,
					ServerDetails.class);
			intent.putExtra(
					getResources().getString(
							R.string.intent_extra_regid), regId);
			intent.putExtra(
					getResources().getString(
							R.string.intent_extra_from_activity),
					AlreadyRegisteredActivity.class.getSimpleName());
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			
		}
	};

	public void startUnRegistration() {
		final Context context = AlreadyRegisteredActivity.this;
		isUnregisterBtnClicked = true;

		progressDialog = ProgressDialog
				.show(AlreadyRegisteredActivity.this,
						getResources().getString(
								R.string.dialog_message_unregistering),
						getResources().getString(
								R.string.dialog_message_please_wait), true);

		regId = CommonUtilities.getPref(context, context.getResources()
				.getString(R.string.shared_pref_regId));

		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("regid", regId);

		// Check network connection availability before calling the API.
		if (PhoneState.isNetworkAvailable(context)) {
			// Call device unregister API.
			ServerUtils.callSecuredAPI(AlreadyRegisteredActivity.this, CommonUtilities.UNREGISTER_ENDPOINT,
					CommonUtilities.POST_METHOD, requestParams,
					AlreadyRegisteredActivity.this,
					CommonUtilities.UNREGISTER_REQUEST_CODE);
		} else {
			CommonDialogUtils.stopProgressDialog(progressDialog);
			CommonDialogUtils
					.showNetworkUnavailableMessage(AlreadyRegisteredActivity.this);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if(CommonUtilities.DEBUG_MODE_ENABLED){
			getSupportMenuInflater().inflate(R.menu.sherlock_menu_debug, menu);
		}else{
			getSupportMenuInflater().inflate(R.menu.sherlock_menu, menu);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.info_setting:
			Intent intentIN = new Intent(AlreadyRegisteredActivity.this,
					DisplayDeviceInfoActivity.class);
			intentIN.putExtra(
					getResources().getString(
							R.string.intent_extra_from_activity),
					AlreadyRegisteredActivity.class.getSimpleName());
			startActivity(intentIN);
			return true;
		case R.id.pin_setting:
			Intent intentPIN = new Intent(AlreadyRegisteredActivity.this,
					PinCodeActivity.class);
			intentPIN.putExtra(
					getResources().getString(
							R.string.intent_extra_from_activity),
					AlreadyRegisteredActivity.class.getSimpleName());
			startActivity(intentPIN);
			return true;
		case R.id.ip_setting:
			SharedPreferences mainPref = AlreadyRegisteredActivity.this
					.getSharedPreferences("com.mdm", Context.MODE_PRIVATE);
			Editor editor = mainPref.edit();
			editor.putString(getResources().getString(R.string.shared_pref_ip),
					"");
			editor.commit();

			Intent intentIP = new Intent(AlreadyRegisteredActivity.this,
					ServerDetails.class);
			intentIP.putExtra(
					getResources().getString(
							R.string.intent_extra_from_activity),
					AlreadyRegisteredActivity.class.getSimpleName());
			startActivity(intentIP);
			return true;
		case R.id.debug_log:
			Intent intentDebug = new Intent(AlreadyRegisteredActivity.this,
					LogActivity.class);
			startActivity(intentDebug);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(i);
		// finish();
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// finish();
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			// finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!freshRegFlag && !isUnregisterBtnClicked) {
			// Check network connection availability before calling the API.
			if (PhoneState.isNetworkAvailable(context)) {
				// Call isRegistered API.
				Map<String, String> requestParams = new HashMap<String, String>();
				requestParams.put("regid", regId);
				ServerUtils.callSecuredAPI(AlreadyRegisteredActivity.this,
						CommonUtilities.IS_REGISTERED_ENDPOINT,
						CommonUtilities.POST_METHOD, requestParams,
						AlreadyRegisteredActivity.this,
						CommonUtilities.IS_REGISTERED_REQUEST_CODE);
			} else {
				CommonDialogUtils
						.showNetworkUnavailableMessage(AlreadyRegisteredActivity.this);
			}
			
		}

	}

	public void showAlert(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setPositiveButton(getResources().getString(R.string.button_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						//cancelEntry();
						dialog.cancel();
					}
				});
	
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void cancelEntry() {
		Intent intentIP = new Intent(AlreadyRegisteredActivity.this,
				ServerDetails.class);
		intentIP.putExtra(
				getResources().getString(R.string.intent_extra_from_activity),
				AlreadyRegisteredActivity.class.getSimpleName());
		intentIP.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intentIP);

	}

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String responseStatus = "";

		if (requestCode == CommonUtilities.UNREGISTER_REQUEST_CODE) {
			stopProgressDialog();
			if (result != null) {
				responseStatus = result.get(CommonUtilities.STATUS_KEY);
				if (responseStatus != null) {
					if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
						txtRegText
								.setText(R.string.register_text_view_text_unregister);
						btnUnregister.setText(R.string.register_button_text);
						btnUnregister.setTag(TAG_BTN_RE_REGISTER);
						btnUnregister
								.setOnClickListener(onClickListener_BUTTON_CLICKED);
						ServerUtils.clearAppData(context);
			
					} else if (responseStatus
							.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
						Log.e(TAG, "The value of status is : " + responseStatus);
						alertDialog = CommonDialogUtils
								.getAlertDialogWithOneButtonAndTitle(
										context,
										getResources().getString(
												R.string.title_head_connection_error),
										getResources().getString(
												R.string.error_internal_server),
										getResources().getString(R.string.button_ok),
										null);
					} else {
						Log.e(TAG, "The result is : " + result);
						Log.e(TAG, "The responseStatus is : " + responseStatus);
						loadAuthenticationErrorActivity();
					}
				} else {
					Log.e(TAG, "The result is null in onReceiveAPIResult().");
					Log.e(TAG, "The responseStatus is : " + responseStatus);
					loadAuthenticationErrorActivity();
				}
				

			} else {
				Log.e(TAG, "The result is null in onReceiveAPIResult().");
				Log.e(TAG, "The responseStatus is : " + responseStatus);
				loadAuthenticationErrorActivity();
			}

		}
		
		if (requestCode == CommonUtilities.IS_REGISTERED_REQUEST_CODE) {
			stopProgressDialog();
			if (result != null) {
				responseStatus = result.get(CommonUtilities.STATUS_KEY);
				if (responseStatus != null) {
					if (responseStatus
							.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
						Log.e(TAG, "The value of status is : " + responseStatus);
						alertDialog = CommonDialogUtils
								.getAlertDialogWithOneButtonAndTitle(
										context,
										getResources().getString(
												R.string.title_head_connection_error),
										getResources().getString(
												R.string.error_internal_server),
										getResources().getString(R.string.button_ok),
										null);
						alertDialog.show();
					} else if (!responseStatus
							.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
						Log.e(TAG, "The value of status is : " + responseStatus);
						ServerUtils.clearAppData(context);
						
						alertDialog = CommonDialogUtils
								.getAlertDialogWithOneButtonAndTitle(
										context,
										getResources().getString(
												R.string.title_head_registration_error),
										getResources().getString(
												R.string.error_for_all_unknown_registration_failures),
										getResources().getString(R.string.button_ok),
										isRegisteredFailedOKBtnClickListerner);
						alertDialog.show();
						ServerUtils.clearAppData(context);
					}
				}  else {
					Log.e(TAG, "The result is null in onReceiveAPIResult()");
					ServerUtils.clearAppData(context);
					
					alertDialog = CommonDialogUtils
							.getAlertDialogWithOneButtonAndTitle(
									context,
									getResources().getString(
											R.string.title_head_registration_error),
									getResources().getString(
											R.string.error_for_all_unknown_registration_failures),
									getResources().getString(R.string.button_ok),
									isRegisteredFailedOKBtnClickListerner);
					alertDialog.show();
				}
				
				
			} else {
				Log.e(TAG, "The responseStatus is null in onReceiveAPIResult()");
				ServerUtils.clearAppData(context);
				
				alertDialog = CommonDialogUtils
						.getAlertDialogWithOneButtonAndTitle(
								context,
								getResources().getString(
										R.string.title_head_registration_error),
								getResources().getString(
										R.string.error_for_all_unknown_registration_failures),
								getResources().getString(R.string.button_ok),
								null);
				alertDialog.show();
			}

		}
		
	}

	private void loadAuthenticationErrorActivity() {
		Intent intent = new Intent(AlreadyRegisteredActivity.this,
				AuthenticationErrorActivity.class);
		intent.putExtra(
				getResources().getString(
						R.string.intent_extra_regid), regId);
		intent.putExtra(
				getResources().getString(
						R.string.intent_extra_from_activity),
				AlreadyRegisteredActivity.class.getSimpleName());
		startActivity(intent);
	}
	
	private void stopProgressDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

}