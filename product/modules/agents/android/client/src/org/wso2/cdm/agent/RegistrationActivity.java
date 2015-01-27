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

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

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
		SharedPreferences mainPref =
		                             RegistrationActivity.this.getSharedPreferences(RegistrationActivity.this.getResources()
		                                                                                                     .getString(R.string.shared_pref_package),
		                                                                            Context.MODE_PRIVATE);
		String type =
		              mainPref.getString(RegistrationActivity.this.getResources()
		                                                          .getString(R.string.shared_pref_reg_type),
		                                 "");
		String username =
	              mainPref.getString(RegistrationActivity.this.getResources()
	                                                          .getString(R.string.username),
	                                 "");

		try {
			jsObject.put("deviceIdentifier", deviceInfo.getMACAddress());
			jsObject.put("description", deviceInfo.getDevice());
			jsObject.put("ownership", type);
			JSONArray propertiesArray=new JSONArray();
			JSONObject property= new JSONObject();			
			property.put("name", "username");
			property.put("value", username);
			propertiesArray.put(property);
			property= new JSONObject();	
			property.put("name",  "device");
			property.put("value", deviceInfo.getDevice());
			propertiesArray.put(property);
			property= new JSONObject();	
			property.put("name",  "imei");
			property.put("value", deviceInfo.getDeviceId());
			propertiesArray.put(property);
			property= new JSONObject();	
			property.put("name",  "imsi");
			property.put("value", deviceInfo.getIMSINumber());
			propertiesArray.put(property);
			property= new JSONObject();	
			property.put("name",  "model");
			property.put("value", deviceInfo.getDeviceModel());
			propertiesArray.put(property);
			property= new JSONObject();	
			property.put("name",  "vendor");
			property.put("value", deviceInfo.getOsVersion());
			propertiesArray.put(property);
			property= new JSONObject();		
			property.put("name",  "osVersion");
			property.put("value", deviceInfo.getOsVersion());
			propertiesArray.put(property);
			jsObject.put("properties", propertiesArray);

			// Check network connection availability before calling the API.
			if (PhoneState.isNetworkAvailable(context)) {
				// Call device registration API.
				ServerUtils.callSecuredAPI(RegistrationActivity.this,
				                           CommonUtilities.API_SERVER_URL + CommonUtilities.REGISTER_ENDPOINT,
				                           CommonUtilities.POST_METHOD, jsObject,
				                           RegistrationActivity.this,
				                           CommonUtilities.REGISTER_REQUEST_CODE);
			} else {
				CommonDialogUtils.stopProgressDialog(progressDialog);
				CommonDialogUtils.showNetworkUnavailableMessage(RegistrationActivity.this);
			}

		} catch (JSONException e) {
			e.printStackTrace();
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

				if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
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
