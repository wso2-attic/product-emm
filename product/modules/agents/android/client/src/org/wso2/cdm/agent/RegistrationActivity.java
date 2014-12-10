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

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.HTTPConnectorUtils;
import org.wso2.cdm.agent.utils.Preference;
import org.wso2.cdm.agent.utils.ServerUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class RegistrationActivity extends Activity implements APIResultCallBack {

	private String TAG = RegistrationActivity.class.getSimpleName();

	String regId = "";
	String username = "";
	Context context;
	boolean regState = false;
	ProgressDialog progressDialog;
	AlertDialog.Builder alertDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		regId = Preference.get(context,getResources().getString(R.string.shared_pref_regId));
		registerDevice();
	}

	private void registerDevice() {
		progressDialog =
		                 CommonDialogUtils.showPrgressDialog(RegistrationActivity.this,
		                                                     getResources().getString(R.string.dialog_enrolling),
		                                                     getResources().getString(R.string.dialog_please_wait),
		                                                     null);
		progressDialog.show();

		DeviceInfo deviceInfo = new DeviceInfo(RegistrationActivity.this);
		JSONObject jsObject = new JSONObject();
		String osVersion = "";
		SharedPreferences mainPref =
		                             RegistrationActivity.this.getSharedPreferences(RegistrationActivity.this.getResources()
		                                                                                                     .getString(R.string.shared_pref_package),
		                                                                            Context.MODE_PRIVATE);
		String type =
		              mainPref.getString(RegistrationActivity.this.getResources()
		                                                          .getString(R.string.shared_pref_reg_type),
		                                 "");

		osVersion = deviceInfo.getOsVersion();
		try {
			jsObject.put("device", deviceInfo.getDevice());
			jsObject.put("imei", deviceInfo.getDeviceId());
			jsObject.put("imsi", deviceInfo.getIMSINumber());
			jsObject.put("model", deviceInfo.getDeviceModel());

			Map<String, String> requestParams = new HashMap<String, String>();
			requestParams.put("regid", regId);
			requestParams.put("properties", jsObject.toString());
			requestParams.put("osversion", osVersion);
			requestParams.put("username", username);
			requestParams.put("platform", "Android");
			requestParams.put("vendor", deviceInfo.getDeviceManufacturer());
			requestParams.put("type", type);
			requestParams.put("mac", deviceInfo.getMACAddress());

			// Check network connection availability before calling the API.
			if (PhoneState.isNetworkAvailable(context)) {
				// Call device registration API.
				sendDeviceDetails(requestParams);
//				ServerUtils.callSecuredAPI(RegistrationActivity.this,
//				                           CommonUtilities.REGISTER_ENDPOINT,
//				                           CommonUtilities.POST_METHOD, requestParams,
//				                           RegistrationActivity.this,
//				                           CommonUtilities.REGISTER_REQUEST_CODE);
			} else {
				CommonDialogUtils.stopProgressDialog(progressDialog);
				CommonDialogUtils.showNetworkUnavailableMessage(RegistrationActivity.this);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

    private void sendDeviceDetails(final Map<String, String> requestParams) {

		AsyncTask<Void, Void, Map<String, String>> mLicenseTask =
				new AsyncTask<Void, Void, Map<String, String>>() {

					@Override
					protected Map<String, String> doInBackground(Void... params) {
						Map<String, String> response;						
						response =
								HTTPConnectorUtils.postData(context,CommonUtilities.SERVER_URL+CommonUtilities.REGISTER_ENDPOINT,requestParams
								                                );
						return response;
					}

					@Override
					protected void onPreExecute() {};

					@Override
					protected void onPostExecute(Map<String, String> result) {
						CommonDialogUtils.stopProgressDialog(progressDialog);
						manipulateDeviceDetails(result);
					}

				};

		mLicenseTask.execute();

	}
    
    private void manipulateDeviceDetails(Map<String, String> result){
    	CommonDialogUtils.stopProgressDialog(progressDialog);
		String responseStatus = "";
		if (result != null) {
			responseStatus = result.get(CommonUtilities.STATUS_KEY);

				if (responseStatus.equals(CommonUtilities.REGISTERATION_SUCCESSFUL)) {
					Intent intent =
					                new Intent(RegistrationActivity.this,
					                           AlreadyRegisteredActivity.class);
					intent.putExtra(getResources().getString(R.string.intent_extra_fresh_reg_flag),
					                true);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else if (responseStatus.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
					Log.e(TAG, "The value of status is : " + responseStatus);
					alertDialog =
					              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
					                                                                    getResources().getString(R.string.title_head_connection_error),
					                                                                    getResources().getString(R.string.error_internal_server),
					                                                                    getResources().getString(R.string.button_ok),
					                                                                    registrationFailedOKBtnClickListerner);
				} else {
					Log.e(TAG, "The value of status is : " + responseStatus);
					Log.e(TAG, "The responseStatus is : " + responseStatus);
					alertDialog =
					              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
					                                                                    getResources().getString(R.string.title_head_registration_error),
					                                                                    getResources().getString(R.string.error_for_all_unknown_registration_failures),
					                                                                    getResources().getString(R.string.button_ok),
					                                                                    registrationFailedOKBtnClickListerner);
					alertDialog.show();
				}
			
		} else {
			Log.e(TAG, "The result is null in onReceiveAPIResult(). ");
			Log.e(TAG, "The responseStatus is : " + responseStatus);
			alertDialog =
			              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
			                                                                    getResources().getString(R.string.title_head_registration_error),
			                                                                    getResources().getString(R.string.error_for_all_unknown_registration_failures),
			                                                                    getResources().getString(R.string.button_ok),
			                                                                    registrationFailedOKBtnClickListerner);
			alertDialog.show();

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

	DialogInterface.OnClickListener registrationFailedOKBtnClickListerner =
	                                                                        new DialogInterface.OnClickListener() {
		                                                                        @Override
		                                                                        public void onClick(DialogInterface arg0,
		                                                                                            int arg1) {
			                                                                        loadAuthenticationErrorActivity();
		                                                                        }
	                                                                        };

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		CommonDialogUtils.stopProgressDialog(progressDialog);
		String responseStatus = "";
		if (result != null) {
			responseStatus = result.get(CommonUtilities.STATUS_KEY);

				if (responseStatus.equals(CommonUtilities.REGISTERATION_SUCCESSFUL)) {
					Intent intent =
					                new Intent(RegistrationActivity.this,
					                           AlreadyRegisteredActivity.class);
					intent.putExtra(getResources().getString(R.string.intent_extra_fresh_reg_flag),
					                true);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					// finish();
				} else if (responseStatus.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
					Log.e(TAG, "The value of status is : " + responseStatus);
					alertDialog =
					              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
					                                                                    getResources().getString(R.string.title_head_connection_error),
					                                                                    getResources().getString(R.string.error_internal_server),
					                                                                    getResources().getString(R.string.button_ok),
					                                                                    registrationFailedOKBtnClickListerner);
					alertDialog.show();
				} else {
					Log.e(TAG, "The value of status is : " + responseStatus);
					Log.e(TAG, "The responseStatus is : " + responseStatus);
					alertDialog =
					              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
					                                                                    getResources().getString(R.string.title_head_registration_error),
					                                                                    getResources().getString(R.string.error_for_all_unknown_registration_failures),
					                                                                    getResources().getString(R.string.button_ok),
					                                                                    registrationFailedOKBtnClickListerner);
				}
			
		} else {
			Log.e(TAG, "The result is null in onReceiveAPIResult(). ");
			Log.e(TAG, "The responseStatus is : " + responseStatus);
			alertDialog =
			              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
			                                                                    getResources().getString(R.string.title_head_registration_error),
			                                                                    getResources().getString(R.string.error_for_all_unknown_registration_failures),
			                                                                    getResources().getString(R.string.button_ok),
			                                                                    registrationFailedOKBtnClickListerner);
			alertDialog.show();

		}
	}

	/**
	 * Loads Authentication error activity.
	 * 
	 */
	private void loadAuthenticationErrorActivity() {
		Intent intent = new Intent(RegistrationActivity.this, AuthenticationErrorActivity.class);
		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
		                RegistrationActivity.class.getSimpleName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

}
