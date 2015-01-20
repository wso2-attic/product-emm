/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.wso2.cdm.agent.AlertActivity;
import org.wso2.cdm.agent.ServerDetails;
import org.wso2.cdm.agent.api.ApplicationManager;
import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.api.GPSTracker;
import org.wso2.cdm.agent.api.LocationServices;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.api.TrackCallSMS;
import org.wso2.cdm.agent.api.WiFiConfig;
import org.wso2.cdm.agent.models.PInfo;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.LoggerCustom;
import org.wso2.cdm.agent.utils.ServerUtils;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.telephony.SmsManager;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class Operation {

	Context context = null;
	DevicePolicyManager devicePolicyManager;
	ApplicationManager appList;
	DeviceInfo deviceInfo;
	TrackCallSMS conversations;
	PhoneState deviceState;
	String code = null;
	String policy_code = "";
	String token = "";
	String policy_token = "";
	int policy_count = 0;
	String data = "";
	GPSTracker gps;
	String recepient = "";
	int mode = 1;
	LoggerCustom logger = null;
	private static final String TAG = "Operation Handler";
	static final int ACTIVATION_REQUEST = 47;
	static final int REQUEST_CODE_START_ENCRYPTION = 1;
	AsyncTask<Void, Void, Void> mRegisterTask;
	static final int REQUEST_MODE_BUNDLE = 0;
	static final int REQUEST_MODE_NORMAL = 1;
	public static boolean enterpriseWipe=false;
	Intent intent;
	Map<String, String> params = new HashMap<String, String>();
	Map<String, String> bundle_params = new HashMap<String, String>();
	SmsManager smsManager;

	public Operation(Context context) {
		this.context = context;
	}

	public Operation(Context context, int mode, Intent intent) {
		this.context = context;
		this.intent = intent;
		this.mode = mode;
		
		if(CommonUtilities.DEBUG_MODE_ENABLED){
			logger = new LoggerCustom(context);
			Time now = new Time();
			now.setToNow();
			String log_in = logger.readFileAsString("wso2log.txt");
			String to_write="";
        
	        if(log_in!=null && !log_in.equals("") && !log_in.equals("null")){
	        	to_write="<br> SERVER TO AGENT AT "+now.hour+":"+now.minute+" : <br> CODE : "+intent.getStringExtra("message").trim()+"<br> MSG ID : "+intent.getStringExtra("token").trim()+"<br> DATA : "+intent.getStringExtra("data")+"<br>==========================================================<br>"+log_in;
	        }else{
	        	to_write="<br> SERVER TO AGENT AT "+now.hour+":"+now.minute+": <br> CODE : "+intent.getStringExtra("message").trim()+"<br> MSG ID : "+intent.getStringExtra("token").trim()+"<br> DATA : "+intent.getStringExtra("data")+"<br>==========================================================<br>";
	        }
	        logger.writeStringAsFile(to_write, "wso2log.txt");
        }

		if(intent.getStringExtra("message").trim().equals(CommonUtilities.OPERATION_POLICY_MONITOR)){
			policy_token = intent.getStringExtra("token").trim();
			policy_code = intent.getStringExtra("message").trim();
			Log.v("Token", policy_token);
		}else{
			token = intent.getStringExtra("token").trim();
			code = intent.getStringExtra("message").trim();
			Log.v("Code", code);
			Log.v("Token", token);
		}
		

		if (intent.getStringExtra("data") != null) {
			data = intent.getStringExtra("data");
			Log.v("Data", data);
		}

		if (intent.getStringExtra("message").trim().equals(CommonUtilities.OPERATION_POLICY_BUNDLE)) {
			try {
				SharedPreferences mainPrefp = context.getSharedPreferences(
						"com.mdm", Context.MODE_PRIVATE);
				Editor editorp = mainPrefp.edit();
				editorp.putString("policy", "");
				editorp.commit();
				
				SharedPreferences mainPref = context.getSharedPreferences(
						"com.mdm", Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();
				editor.putString("policy", data);
				editor.commit();

				/*if (mainPref.getString("policy_applied", "") == null
						|| mainPref.getString("policy_applied", "").trim()
								.equals("0")
						|| mainPref.getString("policy_applied", "").trim()
								.equals("")) {*/
					//executePolicy();
				//}
				/*
				 * JSONArray jArray = new JSONArray(data); for(int i = 0;
				 * i<jArray.length(); i++){ JSONObject policyObj =
				 * (JSONObject)jArray.getJSONObject(i);
				 * if(policyObj.getString("data")!=null &&
				 * policyObj.getString("data")!=""){
				 * doTask(policyObj.getString("code"),
				 * policyObj.getString("data"), REQUEST_MODE_BUNDLE); } }
				 * doTask(code, "", REQUEST_MODE_NORMAL);
				 */
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if(intent.getStringExtra("message").trim().equals(CommonUtilities.OPERATION_POLICY_MONITOR)) {
			doTask(policy_code, data, REQUEST_MODE_NORMAL);
		}else{
			doTask(code, data, REQUEST_MODE_NORMAL);
		}

	}

	public Operation(Context context, int mode, Map<String, String> params,
			String recepient) {
		this.context = context;
		this.mode = mode;
		this.params = params;
		this.recepient = recepient;
		
		if (params.get("data") != null) {
			data = params.get("data");
			Log.v("Data", data);
		}
		
		if(params.get("code").trim().equals(CommonUtilities.OPERATION_POLICY_MONITOR)){
			policy_token = params.get("token").trim();
			policy_code = params.get("code").trim();
			Log.v("Token", policy_token);
		}else{
			token = params.get("token").trim();
			code = params.get("code").trim();
			Log.v("Code", code);
			Log.v("Token", token);
		}
		
		

		if (params.get("code").trim().equals(CommonUtilities.OPERATION_POLICY_BUNDLE)) {
			try {
				SharedPreferences mainPrefp = context.getSharedPreferences(
						"com.mdm", Context.MODE_PRIVATE);
				Editor editorp = mainPrefp.edit();
				editorp.putString("policy", "");
				editorp.commit();
				
				SharedPreferences mainPref = context.getSharedPreferences(
						"com.mdm", Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();
				editor.putString("policy", data);
				editor.commit();

				executePolicy();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}else if(params.get("code").trim().equals(CommonUtilities.OPERATION_POLICY_MONITOR)) {
			doTask(policy_code, data, REQUEST_MODE_NORMAL);
		}else{
			doTask(code, data, REQUEST_MODE_NORMAL);
		}

	}
	
	
	public Operation(Context context, int mode, String json) {
		this.context = context;
		this.mode = mode;
		this.params = params;
		this.recepient = recepient;

		if (params.get("data") != null) {
			data = params.get("data");
			Log.v("Data", data);
		}

		if (params.get("code").trim()
				.equals(CommonUtilities.OPERATION_POLICY_MONITOR)) {
			policy_token = params.get("token").trim();
			policy_code = params.get("code").trim();
			Log.v("Token", policy_token);
		} else {
			token = params.get("token").trim();
			code = params.get("code").trim();
			Log.v("Code", code);
			Log.v("Token", token);
		}

		if (params.get("code").trim()
				.equals(CommonUtilities.OPERATION_POLICY_BUNDLE)) {
			try {
				SharedPreferences mainPrefp = context.getSharedPreferences(
						"com.mdm", Context.MODE_PRIVATE);
				Editor editorp = mainPrefp.edit();
				editorp.putString("policy", "");
				editorp.commit();

				SharedPreferences mainPref = context.getSharedPreferences(
						"com.mdm", Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();
				editor.putString("policy", data);
				editor.commit();

				executePolicy();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (params.get("code").trim()
				.equals(CommonUtilities.OPERATION_POLICY_MONITOR)) {
			doTask(policy_code, data, REQUEST_MODE_NORMAL);
		} else {
			doTask(code, data, REQUEST_MODE_NORMAL);
		}

	}
	
	public JSONArray operate(String featureCode, JSONObject json) {
		this.mode = mode;
		data=json.toString();
		String featureCodeOp = featureCode;
		Log.e("featureCodeOp","featureCodeOp"+featureCodeOp);
		String data = json.toString();
		Log.e("datadatadatadata","data"+data);
		return doTask(featureCodeOp, data, REQUEST_MODE_NORMAL);
		
		//
	}	

	public void executePolicy() {
		String policy;
		JSONArray jArray = null;
		SharedPreferences mainPref = context.getSharedPreferences("com.mdm",
				Context.MODE_PRIVATE);
//			[{"code" : "500P", "data" : [{"messageId" : "178", "data" : [{"code" : "508A", "data" : {"function" : "Enable"}}, {"code" : "526A", "data" : {"password" : "1234"}}]}]}]
		try {

			policy = mainPref.getString("policy", "");
			if(policy!=null && !policy.equals("")){
    			Log.e("INCOMING POLICY : ",policy);
    			jArray = new JSONArray(policy);
    			for (int i = 0; i < jArray.length(); i++) {
    				if(jArray.getJSONObject(i)!=null){
    					JSONObject policyObj = (JSONObject) jArray.getJSONObject(i);
    					if (policyObj.getString("data") != null
    							&& policyObj.getString("data") != "") {
    						doTask(policyObj.getString("code"),
    								policyObj.getString("data"), REQUEST_MODE_BUNDLE);
    					}
    				}
    			}
    
    			Editor editor = mainPref.edit();
    			editor.putString("policy_applied", "1");
    			editor.commit();
    			this.data = policy;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			Editor editor = mainPref.edit();
			editor.putString("policy_applied", "0");
			editor.commit();
		}
	}
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public void revokePolicy() {
		
		JSONArray jArray = null;
		devicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName cameraAdmin = new ComponentName(context,
				WSO2DeviceAdminReceiver.class);
		SharedPreferences mainPref = context.getSharedPreferences("com.mdm",
				Context.MODE_PRIVATE);

		try {

			String policy = mainPref.getString("policy", "");
			Log.e("policy revoke() ",policy);
//			[{"data":{"function":"Enable"},"code":"508A"},{"data":{"password":"12345"},"code":"526A"}]

			jArray = new JSONArray(policy.trim());
			for (int i = 0; i < jArray.length(); i++) {
				if(jArray.getJSONObject(i)!=null){
					JSONObject policyObj = (JSONObject) jArray.getJSONObject(i);
					

						if(policyObj.getString("code").trim().equals(CommonUtilities.OPERATION_WIFI)){
							JSONObject jobj = new JSONObject(policyObj.getString("data"));
							if (!jobj.isNull("ssid")) {
								String rev_ssid = (String) jobj.get("ssid");
								WiFiConfig config = new WiFiConfig(context);
								config.removeWiFiConfigurationBySSID(rev_ssid);
							}
						}else if(policyObj.getString("code").trim().equals(CommonUtilities.OPERATION_DISABLE_CAMERA)){
							if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
								devicePolicyManager.setCameraDisabled(cameraAdmin, false);
							}
						}else if(policyObj.getString("code").trim().equals(CommonUtilities.OPERATION_ENCRYPT_STORAGE)){
							if (policyObj.getString("data") != null
									&& policyObj.getString("data") != "") {
    							JSONObject jobj = new JSONObject(policyObj.getString("data"));
    							boolean encryptFunc=false;
    							if (!jobj.isNull("function")
    									&& jobj.get("function").toString()
    											.equalsIgnoreCase("encrypt")) {
    								encryptFunc = true;
    							} else if (!jobj.isNull("function")
    									&& jobj.get("function").toString()
    											.equalsIgnoreCase("decrypt")) {
    								encryptFunc = false;
    							} else if (!jobj.isNull("function")) {
    								encryptFunc = Boolean.parseBoolean(jobj.get("function")
    										.toString());
    							}
    							if(encryptFunc){
    								if (devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
    									if (devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_ACTIVE
    											|| devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_ACTIVATING) {
    										if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
    											devicePolicyManager.setStorageEncryption(cameraAdmin,
    													false);
    										}
    									}
    								}
    							}
							}
						}else if(policyObj.getString("code").trim().equals(CommonUtilities.OPERATION_PASSWORD_POLICY)){
							devicePolicyManager.setPasswordQuality(cameraAdmin,
									DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
						}
//							policyObj.getString("code").trim().equals(CommonUtilities.OPERATION_CHANGE_LOCK_CODE)
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public JSONArray doTask(String code_in, String data_in, int req_mode) {
		
		Log.e("doTask","code:"+code_in+"\n"+data_in);
		String data_input=data_in;
		String code_input = code_in;
		String notification = "";
		String ssid = "";
		String password = "";

		devicePolicyManager = (DevicePolicyManager) context.getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
		appList = new ApplicationManager(context.getApplicationContext());
		deviceInfo = new DeviceInfo(context);
		gps = new GPSTracker(context);
		smsManager = SmsManager.getDefault();
		conversations = new TrackCallSMS(context);
		deviceState = new PhoneState(context);
		
		JSONArray resultArr= new JSONArray();
		JSONObject result= new JSONObject();
		if (code_input.equals(CommonUtilities.OPERATION_DEVICE_INFO)) {

			PhoneState phoneState = new PhoneState(context);
			JSONObject obj = new JSONObject();
			JSONObject battery_obj = new JSONObject();
			JSONObject inmemory_obj = new JSONObject();
			JSONObject exmemory_obj = new JSONObject();
			JSONObject location_obj = new JSONObject();
			double latitude = 0;
			double longitude = 0;
			try {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				int batteryLevel = (int)Math.floor(phoneState.getBatteryLevel());

//				int batteryLevel = 40;
				battery_obj.put("level", batteryLevel);

				inmemory_obj.put("total",
						deviceInfo.getTotalInternalMemorySize());
				inmemory_obj.put("available",
						deviceInfo.getAvailableInternalMemorySize());
				exmemory_obj.put("total",
						deviceInfo.getTotalExternalMemorySize());
				exmemory_obj.put("available",
						deviceInfo.getAvailableExternalMemorySize());
				location_obj.put("latitude", latitude);
				location_obj.put("longitude", longitude);

				obj.put("battery", battery_obj);
				obj.put("internal_memory", inmemory_obj);
				obj.put("external_memory", exmemory_obj);
				if(latitude!=0 && longitude!=0){
					obj.put("location_obj", location_obj);
				}
				obj.put("operator", deviceInfo.getNetworkOperatorName());

				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", obj.toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", obj);
				
				Map<String, String> as = new HashMap<String, String>();
				as.put("all", params.toString());

				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
//					ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager
							.sendTextMessage(
									recepient,
									null,
									"Battery Level : "
											+ phoneState.getBatteryLevel()
											+ ", Total Memory : "
											+ deviceInfo.formatSizeGB(deviceInfo
													.getTotalInternalMemorySize()
													+ deviceInfo
															.getTotalExternalMemorySize())
											+ ", Available Memory : "
											+ deviceInfo.formatSizeGB(deviceInfo
													.getAvailableInternalMemorySize()
													+ deviceInfo
															.getAvailableExternalMemorySize()),
									null, null);
				}

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_DEVICE_LOCATION)) {

			LocationServices ls = new LocationServices(context);
			Log.v("Latitude", ls.getLatitude());
			double latitude = 0;
			double longitude = 0;
			JSONObject obj = new JSONObject();
			try {
				latitude = gps.getLatitude();
				longitude = gps.getLongitude();
				obj.put("latitude", latitude);
				obj.put("longitude", longitude);

				Map<String, String> params = new HashMap<String, String>();
				params.put("code", CommonUtilities.OPERATION_DEVICE_LOCATION);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", obj.toString());
				
				
				//for local notification\
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", obj);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager
							.sendTextMessage(recepient, null, "Longitude : "
									+ longitude + ",Latitude : " + latitude,
									null, null);
				}
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code_input
				.equals(CommonUtilities.OPERATION_GET_APPLICATION_LIST)) {
			ArrayList<PInfo> apps = appList.getInstalledApps(false); /*
																	 * false =
																	 * no system
																	 * packages
																	 */

			JSONArray jsonArray = new JSONArray();
			int max = apps.size();

			String apz = "";
			Log.e("APP TOTAL : ", "" + max);
			for (int i = 0; i < max; i++) {
				JSONObject jsonObj = new JSONObject();
				try {
					jsonObj.put("name", Uri.encode(apps.get(i).appname));
					jsonObj.put("package", apps.get(i).pname);
					jsonObj.put("icon", apps.get(i).icon);
					apz += apps.get(i).appname + " ,";
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				jsonArray.put(jsonObj);
			}
			
			JSONObject appsObj = new JSONObject();
			try {
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", jsonArray);
				
				
				
				
				appsObj.put("apps", jsonArray);

				Map<String, String> params = new HashMap<String, String>();

				params.put("code",
						CommonUtilities.OPERATION_GET_APPLICATION_LIST);
				params.put("msgID", token);
				params.put("status", "200");
				//params.put("data", Uri.encode(jsonArray.toString()));
				Log.e("PASSING MSG ID : ",token);
				Log.e("PASSING CODE : ",code_input);
				
				
				
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager
							.sendTextMessage(recepient, null, apz, null, null);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_LOCK_DEVICE)) {

			Log.d(TAG, "Locking device now");
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
						//ServerUtilities.pushData(params, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null,
								"Device Locked Successfully", null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, params.toString());
					
				}
				devicePolicyManager.lockNow();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_WIPE_DATA)) {


			Log.d(TAG,
					"RESETing device now - all user data will be ERASED to factory settings");
			String pin = null;
			SharedPreferences mainPref = context.getSharedPreferences(
					"com.mdm", Context.MODE_PRIVATE);
			String pinSaved = mainPref.getString("pin", "");

			try {
				JSONObject jobj = new JSONObject(data_input);
				pin = (String) jobj.get("pin");
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);

				
				//for local notification
				resultArr.put(result);
				
				
				if (pin.trim().equals(pinSaved.trim())) {
					params.put("status", "200");
					result.put("status", "true");
				} else {
					params.put("status", "400");
					result.put("status", "false");
				}
				
				
				result.put("code", code_input);

				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					if (pin.trim().equals(pinSaved.trim())) {
						smsManager.sendTextMessage(recepient, null,
								"Device Wiped Successfully", null, null);
					} else {
						smsManager.sendTextMessage(recepient, null,
								"Wrong PIN", null, null);
					}
				}
				if (pin.trim().equals(pinSaved.trim())) {
					Toast.makeText(context, "Device is being wiped",
							Toast.LENGTH_LONG).show();
					try {
	    				Thread.sleep(4000);
	    			} catch (InterruptedException e) {
	    				// TODO Auto-generated catch block
	    				e.printStackTrace();
	    			}
					devicePolicyManager.wipeData(ACTIVATION_REQUEST);
				} else {
					Toast.makeText(context,
							"Device wipe failed due to wrong PIN",
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_CLEAR_PASSWORD)) {
			ComponentName demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);

			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);

				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
						//ServerUtilities.pushData(params, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null,
								"Lock code cleared Successfully", null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, params.toString());
				}
				devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
						DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
				devicePolicyManager
						.setPasswordMinimumLength(demoDeviceAdmin, 0);
				devicePolicyManager.resetPassword("",
						DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
				devicePolicyManager.lockNow();
				devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
						DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_NOTIFICATION)) {

			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				if (jobj.get("notification").toString() != null
						|| jobj.get("notification").toString().equals("")) {
					notification = jobj.get("notification").toString();
				} else if (jobj.get("Notification").toString() != null
						|| jobj.get("Notification").toString().equals("")) {
					notification = jobj.get("Notification").toString();
				} else {
					notification = "";
				}

				Log.v("Notification", notification);
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"Notification Receieved Successfully", null, null);
				}

				Intent intent = new Intent(context, AlertActivity.class);
				intent.putExtra("message", notification);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_WIFI)) {
			boolean wifistatus = false;

			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				if (!jobj.isNull("ssid")) {
					ssid = (String) jobj.get("ssid");
				}
				if (!jobj.isNull("password")) {
					password = (String) jobj.get("password");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> inparams = new HashMap<String, String>();
			inparams.put("code", code_input);
			inparams.put("msgID", token);
			WiFiConfig config = new WiFiConfig(context);
			
			
			try {
				//for local notification
				resultArr.put(result);
				result.put("code", code_input);
				
				wifistatus = config.saveWEPConfig(ssid, password);
				if (wifistatus) {
					inparams.put("status", "200");
					result.put("status", "true");
				} else {
					inparams.put("status", "400");
					result.put("status", "false");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
						//ServerUtilities.pushData(inparams, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null,
								"WiFi Configured Successfully", null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, inparams.toString());
				}
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_DISABLE_CAMERA)) {

			boolean camFunc = false;
			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				if (!jobj.isNull("function")
						&& jobj.get("function").toString()
								.equalsIgnoreCase("enable")) {
					camFunc = false;
				} else if (!jobj.isNull("function")
						&& jobj.get("function").toString()
								.equalsIgnoreCase("disable")) {
					camFunc = true;
				} else if (!jobj.isNull("function")) {
					camFunc = Boolean.parseBoolean(jobj.get("function")
							.toString());
				}

				ComponentName cameraAdmin = new ComponentName(context,
						WSO2DeviceAdminReceiver.class);
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				
				String cammode = "Disabled";
				if (camFunc) {
					cammode = "Disabled";
				} else {
					cammode = "Enabled";
				}
				
				

				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
						//ServerUtilities.pushData(params, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null, "Camera "
								+ cammode + " Successfully", null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, params.toString());
				}

				if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					devicePolicyManager.setCameraDisabled(cameraAdmin, camFunc);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input
				.equals(CommonUtilities.OPERATION_INSTALL_APPLICATION)
				|| code_input
						.equals(CommonUtilities.OPERATION_INSTALL_APPLICATION_BUNDLE)) {

			try {
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (code_input
						.equals(CommonUtilities.OPERATION_INSTALL_APPLICATION)) {
					JSONObject jobj = new JSONObject(data_input);
					installApplication(jobj, code_input);
				} else if (code_input
						.equals(CommonUtilities.OPERATION_INSTALL_APPLICATION_BUNDLE)) {
					JSONArray jArray = null;
					jArray = new JSONArray(data_input);
					for (int i = 0; i < jArray.length(); i++) {
						JSONObject appObj = (JSONObject) jArray
								.getJSONObject(i);
						installApplication(appObj, code_input);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input
				.equals(CommonUtilities.OPERATION_UNINSTALL_APPLICATION)) {

			String packageName = "";
			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				packageName = (String) jobj.get("identity");

				Log.v("Package Name : ", packageName);
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"Application uninstalled Successfully", null, null);
				}
				appList.unInstallApplication(packageName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_ENCRYPT_STORAGE)) {
			boolean encryptFunc = true;
			String pass = "";

			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				if (!jobj.isNull("function")
						&& jobj.get("function").toString()
								.equalsIgnoreCase("encrypt")) {
					encryptFunc = true;
				} else if (!jobj.isNull("function")
						&& jobj.get("function").toString()
								.equalsIgnoreCase("decrypt")) {
					encryptFunc = false;
				} else if (!jobj.isNull("function")) {
					encryptFunc = Boolean.parseBoolean(jobj.get("function")
							.toString());
				}

				ComponentName admin = new ComponentName(context,
						WSO2DeviceAdminReceiver.class);
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);

				if (encryptFunc
						&& devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
					if (devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_INACTIVE) {
						if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							devicePolicyManager.setStorageEncryption(admin,
									encryptFunc);
							Intent intent = new Intent(
									DevicePolicyManager.ACTION_START_ENCRYPTION);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						}
					}
				} else if (!encryptFunc
						&& devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
					if (devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_ACTIVE
							|| devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_ACTIVATING) {
						if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							devicePolicyManager.setStorageEncryption(admin,
									encryptFunc);
						}
					}
				}

				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				if (devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
					params.put("status", "200");
					result.put("status", "true");
				} else {
					params.put("status", "400");
					result.put("status", "false");
				}
				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
//						//ServerUtilities.pushData(params, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null,
								"Storage Encrypted Successfully", null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, params.toString());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_MUTE)) {

			Log.d(TAG, "Muting Device");
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
//						//ServerUtilities.pushData(params, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null,
								"Device Muted Successfully", null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, params.toString());
				}

				muteDevice();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_TRACK_CALLS)) {
			try {
				Map<String, String> params = new HashMap<String, String>();

				params.put("code", CommonUtilities.OPERATION_TRACK_CALLS);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", conversations.getCallDetails().toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", new JSONObject(conversations.getCallDetails().toString()));
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null, conversations
							.getCallDetails().toString(), null, null);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_TRACK_SMS)) {
			int MESSAGE_TYPE_INBOX = 1;
			int MESSAGE_TYPE_SENT = 2;
			JSONObject smsObj = new JSONObject();

			try {
				smsObj.put("inbox", conversations.getSMS(MESSAGE_TYPE_INBOX));
				smsObj.put("sent", conversations.getSMS(MESSAGE_TYPE_SENT));

				Map<String, String> params = new HashMap<String, String>();

				params.put("code", CommonUtilities.OPERATION_TRACK_SMS);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", smsObj.toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", smsObj);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							smsObj.toString(), null, null);
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_DATA_USAGE)) {
			JSONObject dataObj = new JSONObject();

			try {

				Map<String, String> params = new HashMap<String, String>();

				params.put("code", CommonUtilities.OPERATION_DATA_USAGE);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", deviceState.takeDataUsageSnapShot()
						.toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", new JSONObject(deviceState.takeDataUsageSnapShot()
				          						.toString()));
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							dataObj.toString(), null, null);
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_STATUS)) {
			boolean encryptStatus = false;
			boolean passCodeStatus = false;
			try {
				if (devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
					if (devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_ACTIVE
							|| devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_ACTIVATING) {
						encryptStatus = true;
					} else {
						encryptStatus = false;
					}
				}
				if (devicePolicyManager.isActivePasswordSufficient()) {
					passCodeStatus = true;
				} else {
					passCodeStatus = false;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				passCodeStatus = false;
			}
			JSONObject dataObj = new JSONObject();

			try {
				dataObj.put("encryption", encryptStatus);
				dataObj.put("passcode", passCodeStatus);

				Map<String, String> params = new HashMap<String, String>();

				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", dataObj.toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", dataObj);

				if (req_mode == REQUEST_MODE_NORMAL) {
					if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
						//ServerUtilities.pushData(params, context);
					} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
						smsManager.sendTextMessage(recepient, null,
								dataObj.toString(), null, null);
					}
				} else {
					if (policy_count != 0) {
						policy_count++;
					}
					bundle_params.put("" + policy_count, params.toString());
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_WEBCLIP)) {
			String appUrl = "";
			String title = "";

			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				Log.v("WEBCLIP DATA : ", data.toString());
				appUrl = (String) jobj.get("identity");
				title = (String) jobj.get("title");
				Log.v("Web App URL : ", appUrl);
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"WebClip created Successfully", null, null);
				}
				appList.createWebAppBookmark(appUrl, title);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_PASSWORD_POLICY)) {
			ComponentName demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);

			int attempts, length, history, specialChars;
			String alphanumeric, complex;
			boolean b_alphanumeric, b_complex;
			long timout;
			Map<String, String> inparams = new HashMap<String, String>();

			JSONParser jp = new JSONParser();
			
			//for local notification
			resultArr.put(result);
			
			
			try {
				result.put("code", code_input);
				JSONObject jobj = new JSONObject(data_input);
				if (!jobj.isNull("maxFailedAttempts")
						&& jobj.get("maxFailedAttempts") != null) {
					attempts = Integer.parseInt((String) jobj
							.get("maxFailedAttempts"));
					devicePolicyManager.setMaximumFailedPasswordsForWipe(
							demoDeviceAdmin, attempts);
				}

				if (!jobj.isNull("minLength") && jobj.get("minLength") != null) {
					length = Integer.parseInt((String) jobj.get("minLength"));
					devicePolicyManager.setPasswordMinimumLength(
							demoDeviceAdmin, length);
				}

				if (!jobj.isNull("pinHistory")
						&& jobj.get("pinHistory") != null) {
					history = Integer.parseInt((String) jobj.get("pinHistory"));
					devicePolicyManager.setPasswordHistoryLength(
							demoDeviceAdmin, history);
				}

				if (!jobj.isNull("minComplexChars")
						&& jobj.get("minComplexChars") != null) {
					specialChars = Integer.parseInt((String) jobj
							.get("minComplexChars"));
					devicePolicyManager.setPasswordMinimumSymbols(
							demoDeviceAdmin, specialChars);
				}

				if (!jobj.isNull("requireAlphanumeric")
						&& jobj.get("requireAlphanumeric") != null) {
					if(jobj.get("requireAlphanumeric") instanceof String){
						alphanumeric = (String) jobj.get("requireAlphanumeric");
						if (alphanumeric.equals("true")) {
							devicePolicyManager
									.setPasswordQuality(
											demoDeviceAdmin,
											DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
						}
					}else if(jobj.get("requireAlphanumeric") instanceof Boolean){
						b_alphanumeric =  jobj.getBoolean("requireAlphanumeric");
						if (b_alphanumeric) {
							devicePolicyManager
									.setPasswordQuality(
											demoDeviceAdmin,
											DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
						}
					}
				}

				if (!jobj.isNull("allowSimple")
						&& jobj.get("allowSimple") != null) {
					if(jobj.get("allowSimple") instanceof String){
						complex = (String) jobj.get("allowSimple");
						if (!complex.equals("true")) {
							devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
									DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
						}
					}else if(jobj.get("allowSimple") instanceof Boolean){
						b_complex = jobj.getBoolean("allowSimple");
						if (!b_complex) {
							devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
									DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
						}
					}
				}

				if (!jobj.isNull("maxPINAgeInDays")
						&& jobj.get("maxPINAgeInDays") != null) {
					int daysOfExp = Integer.parseInt((String) jobj
							.get("maxPINAgeInDays"));
					timout = (long) (daysOfExp * 24 * 60 * 60 * 1000);
					devicePolicyManager.setPasswordExpirationTimeout(
							demoDeviceAdmin, timout);
				}
				
				SharedPreferences mainPref = context.getSharedPreferences("com.mdm",
						Context.MODE_PRIVATE);
				String policy = mainPref.getString("policy", "");
				
				
				inparams.put("code", code_input);
				inparams.put("msgID", token);
				inparams.put("status", "200");
				result.put("status", "true");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				params.put("status", "400");
				try {
	                result.put("status", "false");
                } catch (JSONException e1) {
	                // TODO Auto-generated catch block
	                e1.printStackTrace();
                }
				e.printStackTrace();
			} finally {
				try {
					if (req_mode == REQUEST_MODE_NORMAL) {
						if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
							//ServerUtilities.pushData(inparams, context);
						} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
							smsManager.sendTextMessage(recepient, null,
									"Password Policies Successfully Set", null,
									null);
						}
					} else {
						if (policy_count != 0) {
							policy_count++;
						}
						bundle_params.put("" + policy_count,
								inparams.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_EMAIL_CONFIGURATION)) {
			String emailname="", emailtype="", ic_username="", ic_password="", ic_hostname="";
			long timout;
			Map<String, String> inparams = new HashMap<String, String>();
			//for local notification
			resultArr.put(result);
			
			
			
			JSONParser jp = new JSONParser();
			try {
				result.put("code", code_input);
				JSONObject jobj = new JSONObject(data_input);
				if (!jobj.isNull("type")
						&& jobj.get("type") != null) {
					emailtype = (String) jobj
							.get("type");
				}
				
				if (!jobj.isNull("displayname")
						&& jobj.get("displayname") != null) {
					emailname = (String) jobj
							.get("displayname");
				}
				
				if (!jobj.isNull("username")
						&& jobj.get("username") != null) {
					ic_username = (String) jobj
							.get("username");
				}

				if (!jobj.isNull("password")
						&& jobj.get("password") != null) {
					ic_password = (String) jobj
							.get("password");
				}
				
				if(emailtype.trim().equals("GMAIL")){
					ic_hostname = "imap.googlemail.com";
				}else if(emailtype.equals("YAHOO")){
					ic_hostname = "";
				}else if(emailtype.equals("HOTMAIL")){
					ic_hostname = "";
				}
				
				inparams.put("code", code_input);
				inparams.put("msgID", token);
				inparams.put("status", "200");
				result.put("status", "true");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				params.put("status", "400");
				try {
	                result.put("status", "false");
                } catch (JSONException e1) {
	                // TODO Auto-generated catch block
	                e1.printStackTrace();
                }
				e.printStackTrace();
			} finally {
				try {
					if (req_mode == REQUEST_MODE_NORMAL) {
						if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
							//ServerUtilities.pushData(inparams, context);
						} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
							smsManager.sendTextMessage(recepient, null,
									"Email Configured Successfully Set", null,
									null);
						}
					} else {
						if (policy_count != 0) {
							policy_count++;
						}
						bundle_params.put("" + policy_count,
								inparams.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}else if (code_input
				.equals(CommonUtilities.OPERATION_INSTALL_GOOGLE_APP)) {

			String packageName = "";
			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				packageName = (String) jobj.get("package");

				Log.v("Package Name : ", packageName);
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code_input);
				params.put("msgID", token);
				params.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"Application installed Successfully", null, null);
				}
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setData(Uri.parse("market://details?id=" + packageName));
				context.startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (code_input
				.equals(CommonUtilities.OPERATION_CHANGE_LOCK_CODE)) {
			ComponentName demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);
			devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, 3);
			String pass = "";
			Map<String, String> inparams = new HashMap<String, String>();

			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data_input);
				if (!jobj.isNull("password")) {
					pass = (String) jobj.get("password");
				}

				inparams.put("code", code_input);
				inparams.put("msgID", token);
				inparams.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(inparams, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"Lock code changed Successfully", null, null);
				}
				
				if(!pass.equals("")){
				devicePolicyManager.resetPassword(pass,
						DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
				devicePolicyManager.lockNow();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (req_mode == REQUEST_MODE_NORMAL) {
						if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
							//ServerUtilities.pushData(inparams, context);
						} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
							smsManager.sendTextMessage(recepient, null,
									"Lock code changed Successfully", null,
									null);
						}
					} else {
						if (policy_count != 0) {
							policy_count++;
						}
						bundle_params.put("" + policy_count,
								inparams.toString());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		} else if (code_input.equals(CommonUtilities.OPERATION_POLICY_BUNDLE)) {
			Map<String, String> params = new HashMap<String, String>();
			try {
				params.put("code", code);
				params.put("msgID", policy_token);
				params.put("status", "200");
				params.put("data", bundle_params.toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", new JSONObject(bundle_params.toString()));
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"Bundle Executed Successfully", null, null);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_POLICY_MONITOR)) {
			JSONArray sendjArray;
			JSONObject jobj=null;
			try {
				if(this.data!=null && !this.data.trim().equals(""))
					jobj = new JSONObject(this.data);
				
				sendjArray = jobj.getJSONArray("policies");

				int type = Integer.parseInt((String) jobj.get("type")
						.toString().trim());
				
				if(type!=1 && type!=2 && type!=3){
					type = 1;
				}

				Log.e("PASSING MSG ID : ",policy_token);
				Log.e("PASSING CODE : ",code_input);
				Log.e("PASSING TYPE : ",String.valueOf(type));
				PolicyTester tester = new PolicyTester(context, sendjArray,
						type, policy_token);
				//for local notification
				resultArr=tester.finalArray;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (code_input.equals(CommonUtilities.OPERATION_POLICY_REVOKE)) {
			try {
				Map<String, String> inparams = new HashMap<String, String>();

				
				inparams.put("code", code_input);
				inparams.put("msgID", token);
				inparams.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(inparams, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager.sendTextMessage(recepient, null,
							"Lock code changed Successfully", null, null);
				}
				revokePolicy();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if (code_input.equals(CommonUtilities.OPERATION_ENTERPRISE_WIPE_DATA)) {
			try {
				Map<String, String> inparams = new HashMap<String, String>();

				
				inparams.put("code", code_input);
				inparams.put("msgID", token);
				inparams.put("status", "200");
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(inparams, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
//					smsManager.sendTextMessage(recepient, null,
//							"Lock code changed Successfully", null, null);
				}
				enterpriseWipe=true;
				ServerUtils.clearAppData(context);
				Intent intent = new Intent(context, ServerDetails.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    context.startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (code_input
				.equals(CommonUtilities.OPERATION_BLACKLIST_APPS)) {
			ArrayList<PInfo> apps = appList.getInstalledApps(false); /*
																	 * false =
																	 * no system
																	 * packages
																	 */

			JSONArray jsonArray = new JSONArray();
			int max = apps.size();
			if (max > 10) {
				//max = 10;
			}
			String apz = "";
			

			try{

					JSONObject appsObj = new JSONObject(data_input);
					if (!appsObj.isNull("data")) {
						appsObj = (JSONObject) appsObj.get("data");
					}
//					JSONObject appObj = (JSONObject) appsObj.get("data");
					String identity = (String) appsObj.get("identity");
					
					for (int j = 0; j < max; j++) {
						JSONObject jsonObj = new JSONObject();
						try {
							jsonObj.put("name", apps.get(j).appname);
							jsonObj.put("package", apps.get(j).pname);
							if(identity.trim().equals(apps.get(j).pname)){
								jsonObj.put("notviolated", false);
								jsonObj.put("package", apps.get(j).pname);
							}else{
								jsonObj.put("notviolated", true);
							}
							
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						jsonArray.put(jsonObj);
					}
			}catch(Exception ex){
				ex.printStackTrace();
			}
			

			JSONObject appsObj = new JSONObject();
			try {
				appsObj.put("apps", jsonArray);

				Map<String, String> params = new HashMap<String, String>();

				params.put("code",
						CommonUtilities.OPERATION_GET_APPLICATION_LIST);
				params.put("msgID", token);
				params.put("status", "200");
				params.put("data", jsonArray.toString());
				
				//for local notification
				resultArr.put(result);
				result.put("status", "true");
				result.put("code", code_input);
				result.put("data", jsonArray);
				
				if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
					//ServerUtilities.pushData(params, context);
				} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
					smsManager
							.sendTextMessage(recepient, null, apz, null, null);
				}
				SharedPreferences mainPref = context.getSharedPreferences("com.mdm",
						Context.MODE_PRIVATE);
				String policy = mainPref.getString("policy", "");
					if(policy!=null && policy!=""){
						if(apz!=null || !apz.trim().equals("")){

					}
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		return resultArr;
	}


	/**
	 * Install an Application
	 */
	private void installApplication(JSONObject data_input, String code_input) {
		String appUrl = "";
		String type = "enterprise";
		String os = null;
	
		JSONParser jp = new JSONParser();
		try {
			JSONObject jobj = data_input;
			appUrl = (String) jobj.get("identity");
			if (!jobj.isNull("type")) {
				type = (String) jobj.get("type");
			}
			
			if (!jobj.isNull("platform_id")) {
				os = (String) jobj.get("platform_id");
			} else if (!jobj.isNull("os")) {
				os = (String) jobj.get("os");
			}
			
			Log.v("App URL : ", appUrl);
			Map<String, String> params = new HashMap<String, String>();
			params.put("code", code_input);
			params.put("msgID", token);
			params.put("status", "200");
			if (mode == CommonUtilities.MESSAGE_MODE_GCM) {
				//ServerUtilities.pushData(params, context);
			} else if (mode == CommonUtilities.MESSAGE_MODE_SMS) {
				smsManager.sendTextMessage(recepient, null,
						"Application installed Successfully", null, null);
			}

			if (type.equalsIgnoreCase("Enterprise")) {
				if(os != null){
					if(os.equalsIgnoreCase("android")){
						Log.e("Enterprise","android");
						appList.installApp(appUrl);
					}
				}else{
					appList.installApp(appUrl);
				}			
			} else if (type.equalsIgnoreCase("Market")) {
				if(os != null){
					if(os.equalsIgnoreCase("android")){
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://details?id=" + appUrl));
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}
				}else{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=" + appUrl));
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}	
				
			} else {
				if(os != null){
					if(os.equalsIgnoreCase("android")){
						appList.installApp(appUrl);
					}
				}else{
					appList.installApp(appUrl);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Mute the device
	 */
	private void muteDevice() {
		Log.v("MUTING THE DEVICE : ", "MUTING");
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		Log.v("VOLUME : ",
				"" + audioManager.getStreamVolume(AudioManager.STREAM_RING));
		audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
		Log.v("VOLUME AFTER: ",
				"" + audioManager.getStreamVolume(AudioManager.STREAM_RING));

	}

}
