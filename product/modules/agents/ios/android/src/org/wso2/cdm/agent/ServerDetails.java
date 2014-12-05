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
import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.proxy.Token;
import org.wso2.cdm.agent.proxy.TokenCallBack;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.Preference;
import org.wso2.cdm.agent.utils.ServerUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ServerDetails extends Activity implements APIResultCallBack, TokenCallBack {

	private String TAG = ServerDetails.class.getSimpleName();

	TextView serverIP;
	Button startRegistration;
	String fromActivity;
	Context context;
	DeviceInfo info;
	TextView incompatibleError;
	String errorMessage = "";

	String senderID = null;
	boolean accessFlag = true;
	ProgressDialog progressDialog;
	String regId;
	AlertDialog.Builder alertDialog;

	boolean alreadyRegisteredActivityFlag = false;
	boolean authenticationActivityFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		context = ServerDetails.this;
		info = new DeviceInfo(ServerDetails.this);
		incompatibleError = (TextView) findViewById(R.id.incompatibleError);
		serverIP = (TextView) findViewById(R.id.etServerIP);
		startRegistration = (Button) findViewById(R.id.startRegistration);
		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			if (extras.containsKey(getResources().getString(R.string.intent_extra_from_activity))) {
				fromActivity =
				               extras.getString(getResources().getString(R.string.intent_extra_from_activity));
			}
		}

		// Checking if the device meets minimum requirements
		if (!info.isCompatible(context).getCode()) {
			accessFlag = false;
			incompatibleError.setText(errorMessage);
			startRegistration.setVisibility(View.GONE);
			serverIP.setVisibility(View.GONE);
			incompatibleError.setVisibility(View.VISIBLE);
			CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
			                                                      errorMessage,
			                                                      getResources().getString(R.string.error_authorization_failed),
			                                                      getResources().getString(R.string.button_ok),
			                                                      null);
		} else {
			startRegistration.setVisibility(View.VISIBLE);
			serverIP.setVisibility(View.VISIBLE);
			incompatibleError.setVisibility(View.GONE);

			String ipSaved =
			                 Preference.get(context,
			                                getResources().getString(R.string.shared_pref_ip));
			regId = Preference.get(context, getResources().getString(R.string.shared_pref_regId));

			if (ipSaved != null) {
				serverIP.setText(ipSaved);
				CommonUtilities.setServerURL(ipSaved);
				Intent intent = new Intent(ServerDetails.this, AuthenticationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				serverIP.setText(CommonUtilities.SERVER_IP);
			}

			startRegistration.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ServerDetails.this);
					builder.setMessage(getResources().getString(R.string.dialog_init_confirmation) +
					                           " " +
					                           serverIP.getText().toString() +
					                           " " +
					                           getResources().getString(R.string.dialog_init_end_general))
					       .setPositiveButton(getResources().getString(R.string.info_label_rooted_answer_yes),
					                          dialogClickListener)
					       .setNegativeButton(getResources().getString(R.string.info_label_rooted_answer_no),
					                          dialogClickListener).show();
				}
			});

			// String clientKey =
			// Preference.get(context,
			// getResources().getString(R.string.shared_pref_client_id));
			// String clientSecret =
			// Preference.get(context,
			// getResources().getString(R.string.shared_pref_client_secret));
			// if (!clientKey.equals("") && !clientSecret.equals("")) {
			// CommonUtilities.CLIENT_ID = clientKey;
			// CommonUtilities.CLIENT_SECRET = clientSecret;
			// }
			//
			// try {
			// if (fromActivity == null) {
			// IdentityProxy.getInstance().getToken(this.getApplicationContext(),
			// ServerDetails.this,
			// CommonUtilities.CLIENT_ID,
			// CommonUtilities.CLIENT_SECRET);
			// }
			//
			// } catch (TimeoutException e) {
			// e.printStackTrace();
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
		}
	}

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					if (!serverIP.getText().toString().trim().equals("")) {
						CommonUtilities.setServerURL(serverIP.getText().toString().trim());
						Preference.put(context, getResources().getString(R.string.shared_pref_ip),
						               serverIP.getText().toString().trim());
						Intent intent =
						                new Intent(ServerDetails.this, AuthenticationActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);

					} else {
						Toast.makeText(context,
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

	@Override
	public void onBackPressed() {
		Intent i = new Intent();
		i.setAction(Intent.ACTION_MAIN);
		i.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(i);
		super.onBackPressed();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && fromActivity != null &&
		    fromActivity.equals(AlreadyRegisteredActivity.class.getSimpleName())) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			this.finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK && fromActivity != null &&
		           fromActivity.equals(AuthenticationActivity.class.getSimpleName())) {
			int pid = android.os.Process.myPid();
			android.os.Process.killProcess(pid);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String responseStatus = CommonUtilities.EMPTY_STRING;
		if (result != null) {
			responseStatus = result.get(CommonUtilities.STATUS_KEY);

			if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL) &&
			    requestCode == CommonUtilities.IS_REGISTERED_REQUEST_CODE) {
				Intent intent = null;
				if (progressDialog != null) {
					progressDialog.dismiss();
				}
				intent = new Intent(ServerDetails.this, AlreadyRegisteredActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else if (responseStatus.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
				Log.e(TAG, "The value of status is null in onAPIAccessRecive()");

				String isRegistered =
				                      CommonUtilities.getPref(context,
				                                              context.getResources()
				                                                     .getString(R.string.shared_pref_registered));
				if (isRegistered.equals("1")) {
					Intent intent = null;
					intent = new Intent(ServerDetails.this, AlreadyRegisteredActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					alertDialog =
					              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
					                                                                    getResources().getString(R.string.title_head_connection_error),
					                                                                    getResources().getString(R.string.error_internal_server),
					                                                                    getResources().getString(R.string.button_ok),
					                                                                    null);
					Log.e("null", alertDialog.getClass().getPackage().toString());
					alertDialog.show();
				}
				// ServerUtils.clearAppData(context);
			} else {
				Log.e(TAG, "The value of status is : " + responseStatus);
				ServerUtils.clearAppData(context);

				alertDialog =
				              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
				                                                                    getResources().getString(R.string.title_head_registration_error),
				                                                                    getResources().getString(R.string.error_internal_server),
				                                                                    getResources().getString(R.string.button_ok),
				                                                                    null);
				alertDialog.show();
			}
		} else {
			Log.e(TAG, "The result is null in onReceiveAPIResult()");
			ServerUtils.clearAppData(context);

			alertDialog =
			              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
			                                                                    getResources().getString(R.string.title_head_registration_error),
			                                                                    getResources().getString(R.string.error_for_all_unknown_registration_failures),
			                                                                    getResources().getString(R.string.button_ok),
			                                                                    null);
			alertDialog.show();
		}
	}

	@Override
	public void onReceiveTokenResult(Token token, String status) {
		if (token != null) {
			if (regId != null && !regId.equals("")) {
				// Check registration.
				isRegistered();

				progressDialog =
				                 ProgressDialog.show(ServerDetails.this,
				                                     getResources().getString(R.string.dialog_sender_id),
				                                     getResources().getString(R.string.dialog_please_wait),
				                                     true);
			}
		}
	}

	/**
	 * Checks whether device is registered or NOT.
	 */
	private void isRegistered() {
		Log.e("isReg", "isReg");
		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("regid", regId);
		Log.e("regID", regId);

		// Check network connection availability before calling the API.
		if (PhoneState.isNetworkAvailable(context)) {
			// Call isRegistered API.
			ServerUtils.callSecuredAPI(ServerDetails.this, CommonUtilities.IS_REGISTERED_ENDPOINT,
			                           CommonUtilities.POST_METHOD, requestParams,
			                           ServerDetails.this,
			                           CommonUtilities.IS_REGISTERED_REQUEST_CODE);
		} else {
			CommonDialogUtils.stopProgressDialog(progressDialog);
			CommonDialogUtils.showNetworkUnavailableMessage(ServerDetails.this);
		}

	}

}
