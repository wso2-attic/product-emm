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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.Preference;
import org.wso2.cdm.agent.utils.Responce;

/**
 * This the the activity that is used to capture the server's host name.
 */
public class ServerDetails extends Activity {

	TextView serverIP;
	Button startRegistration;
	Context context;
	DialogInterface.OnClickListener dialogClickListener;
	DeviceInfo info;
	TextView severAddressLabel;

	String senderID = null;
	ProgressDialog progressDialog;
	String regId;
	AlertDialog.Builder alertDialog;

	boolean alreadyRegisteredActivityFlag = false;
	boolean authenticationActivityFlag = false;
	private String TAG = ServerDetails.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		context = ServerDetails.this;
		info = new DeviceInfo(ServerDetails.this);
		serverIP = (TextView) findViewById(R.id.etServerIP);
		severAddressLabel = (TextView) findViewById(R.id.severAddressLabel);
		startRegistration = (Button) findViewById(R.id.startRegistration);

		// Checking if the device meets minimum requirements
		Responce compatibility = info.isCompatible();
		if (!compatibility.getCode()) {
			startRegistration.setVisibility(View.GONE);
			severAddressLabel.setVisibility(View.GONE);
			serverIP.setVisibility(View.GONE);
			alertDialog =
			              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context.getApplicationContext(),
			                                                                    getResources().getString(R.string.error_authorization_failed),
			                                                                    getResources().getString(compatibility.getDescriptionResourceID()),
			                                                                    getResources().getString(R.string.button_ok),
			                                                                    onRootedClickListner);
		} else {
			startRegistration.setVisibility(View.VISIBLE);
			serverIP.setVisibility(View.VISIBLE);
			String ipSaved =
			                 Preference.get(context.getApplicationContext(),
			                                getResources().getString(R.string.shared_pref_ip));
			regId = Preference.get(context.getApplicationContext().getApplicationContext(), getResources().getString(R.string.shared_pref_regId));

			//heck if we have the IP saved previously.
			if (ipSaved != null) {
				serverIP.setText(ipSaved);
				CommonUtilities.setServerURL(ipSaved);
				startAuthenticationActivity();
			} else {
				serverIP.setText(CommonUtilities.SERVER_IP);
			}

			// on click handler for start registration
			startRegistration.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ServerDetails.this);
					builder.setMessage(getResources().getString(R.string.dialog_init_confirmation) +
					                           " " +
					                           serverIP.getText().toString() +
					                           " " +
					                           getResources().getString(R.string.dialog_init_end_general))
					       .setPositiveButton(getResources().getString(R.string.yes),
					                          dialogClickListener)
					       .setNegativeButton(getResources().getString(R.string.no),
					                          dialogClickListener).show();
				}
			});

			dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							if (!serverIP.getText().toString().trim().equals("")) {
								CommonUtilities.setServerURL(serverIP.getText().toString().trim());
								Preference.put(context.getApplicationContext(),
								               getResources().getString(R.string.shared_pref_ip),
								               serverIP.getText().toString().trim());
								startAuthenticationActivity();

							} else {
								Toast.makeText(context.getApplicationContext(),
								               getResources().getString(R.string.toast_message_enter_server_address),
								               Toast.LENGTH_LONG).show();
							}
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							dialog.dismiss();
							break;
					}
				}
			};
		}
		Log.d(TAG, "Server details activity started");
	}

	DialogInterface.OnClickListener onRootedClickListner = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	};

	private void startAuthenticationActivity() {
		Intent intent = new Intent(ServerDetails.this, AuthenticationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		//Avoiding memory leaks by destroying context object
		context = null;
		super.onDestroy();
	}

	// Old API manager communication code.
	//
	// Bundle extras = getIntent().getExtras();
	//
	// if (extras != null) {
	// if
	// (extras.containsKey(getResources().getString(R.string.intent_extra_from_activity)))
	// {
	// fromActivity =
	// extras.getString(
	// getResources().getString(R.string.intent_extra_from_activity));
	// }
	// }
	//
	//
	// public class ServerDetails extends Activity implements APIResultCallBack,
	// TokenCallBack {
	// @Override
	// public void onBackPressed() {
	// Intent i = new Intent();
	// i.setAction(Intent.ACTION_MAIN);
	// i.addCategory(Intent.CATEGORY_HOME);
	// this.startActivity(i);
	// super.onBackPressed();
	// }
	//
	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK && fromActivity != null &&
	// fromActivity.equals(AlreadyRegisteredActivity.class.getSimpleName())) {
	// Intent i = new Intent();
	// i.setAction(Intent.ACTION_MAIN);
	// i.addCategory(Intent.CATEGORY_HOME);
	// this.startActivity(i);
	// this.finish();
	// return true;
	// } else if (keyCode == KeyEvent.KEYCODE_BACK && fromActivity != null &&
	// fromActivity.equals(AuthenticationActivity.class.getSimpleName())) {
	// int pid = android.os.Process.myPid();
	// android.os.Process.killProcess(pid);
	// return true;
	// } else if (keyCode == KeyEvent.KEYCODE_BACK) {
	// Intent i = new Intent();
	// i.setAction(Intent.ACTION_MAIN);
	// i.addCategory(Intent.CATEGORY_HOME);
	// this.startActivity(i);
	// this.finish();
	// return true;
	// }
	// return super.onKeyDown(keyCode, event);
	// }
	//
	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.settings, menu);
	// return true;
	// }
	//
	// public void onReceiveAPIResult(Map<String, String> result, int
	// requestCode) {
	// String responseStatus = CommonUtilities.EMPTY_STRING;
	// if (result != null) {
	// responseStatus = result.get(CommonUtilities.STATUS_KEY);
	//
	// if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL) &&
	// requestCode == CommonUtilities.IS_REGISTERED_REQUEST_CODE) {
	// Intent intent = null;
	// if (progressDialog != null) {
	// progressDialog.dismiss();
	// }
	// intent = new Intent(ServerDetails.this, AlreadyRegisteredActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// startActivity(intent);
	//
	// } else if (responseStatus.equals(CommonUtilities.INTERNAL_SERVER_ERROR))
	// {
	// Log.e(TAG, "The value of status is null in onAPIAccessRecive()");
	//
	// String isRegistered =
	// CommonUtilities.getPref(context,
	// context.getResources()
	// .getString(R.string.shared_pref_registered)
	// );
	// if (isRegistered.equals("1")) {
	// Intent intent = null;
	// intent = new Intent(ServerDetails.this, AlreadyRegisteredActivity.class);
	// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// startActivity(intent);
	// } else {
	// alertDialog =
	// CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
	// getResources()
	// .getString(
	// R.string.title_head_connection_error),
	// getResources()
	// .getString(
	// R.string.error_internal_server),
	// getResources()
	// .getString(
	// R.string.button_ok),
	// null);
	// Log.e("null", alertDialog.getClass().getPackage().toString());
	// alertDialog.show();
	// }
	// // ServerUtils.clearAppData(context);
	// } else {
	// Log.e(TAG, "The value of status is : " + responseStatus);
	// ServerUtils.clearAppData(context);
	//
	// alertDialog =
	// CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
	// getResources()
	// .getString(
	// R.string.title_head_registration_error),
	// getResources()
	// .getString(
	// R.string.error_internal_server),
	// getResources()
	// .getString(
	// R.string.button_ok),
	// null);
	// alertDialog.show();
	// }
	// } else {
	// Log.e(TAG, "The result is null in onReceiveAPIResult()");
	// ServerUtils.clearAppData(context);
	//
	// alertDialog =
	// CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
	// getResources().getString(
	// R.string.title_head_registration_error),
	// getResources().getString(
	// R.string.error_for_all_unknown_registration_failures),
	// getResources().getString(
	// R.string.button_ok),
	// null);
	// alertDialog.show();
	// }
	// }
	//
	// @Override
	// public void onReceiveTokenResult(Token token, String status) {
	// if (token != null) {
	// if (regId != null && !regId.equals("")) {
	// // Check registration.
	// isRegistered();
	//
	// progressDialog =
	// ProgressDialog.show(ServerDetails.this,
	// getResources().getString(R.string.dialog_sender_id),
	// getResources().getString(R.string.dialog_please_wait),
	// true);
	// }
	// }
	// }
	//
	// /**
	// * Checks whether device is registered or NOT.
	// */
	// private void isRegistered() {
	// Log.e("isReg", "isReg");
	// Map<String, String> requestParams = new HashMap<String, String>();
	// requestParams.put("regid", regId);
	// Log.e("regID", regId);
	//
	// // Check network connection availability before calling the API.
	// if (PhoneState.isNetworkAvailable(context)) {
	// // Call isRegistered API.
	// ServerUtils.callSecuredAPI(ServerDetails.this,
	// CommonUtilities.IS_REGISTERED_ENDPOINT,
	// CommonUtilities.POST_METHOD, requestParams,
	// ServerDetails.this,
	// CommonUtilities.IS_REGISTERED_REQUEST_CODE);
	// } else {
	// CommonDialogUtils.stopProgressDialog(progressDialog);
	// CommonDialogUtils.showNetworkUnavailableMessage(ServerDetails.this);
	// }
	//
	// }

}
