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
import org.wso2.cdm.agent.api.ApplicationManager;
import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.api.WiFiConfig;
import org.wso2.cdm.agent.models.PInfo;
import org.wso2.cdm.agent.utils.CommonUtilities;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

public class PolicyTester {

	Context context;
	DevicePolicyManager devicePolicyManager;
	ApplicationManager appList;
	DeviceInfo deviceInfo;
	PhoneState deviceState;
	String policy;
	String usermessage = "";
	String apz = "";
	int appcount = 0;
	JSONObject returnJSON = new JSONObject();
	JSONArray finalArray = new JSONArray();
	boolean IS_ENFORCE = false;
	String ssid, password;
	int POLICY_MONITOR_TYPE_NO_ENFORCE_RETURN = 1;
	int POLICY_MONITOR_TYPE_NO_ENFORCE_MESSAGE_RETURN = 2;
	int POLICY_MONITOR_TYPE_ENFORCE_RETURN = 3;
	
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public PolicyTester(Context context, JSONArray recJArray, int type, String msgID) {
		this.context = context;
		devicePolicyManager = (DevicePolicyManager) context
				.getSystemService(Context.DEVICE_POLICY_SERVICE);
		appList = new ApplicationManager(context);
		deviceInfo = new DeviceInfo(context);
		deviceState = new PhoneState(context);
		
		if(type == POLICY_MONITOR_TYPE_NO_ENFORCE_RETURN){
			IS_ENFORCE = false;
		}else if(type == POLICY_MONITOR_TYPE_NO_ENFORCE_MESSAGE_RETURN){
			IS_ENFORCE = false;
		}else if(type == POLICY_MONITOR_TYPE_ENFORCE_RETURN){
			IS_ENFORCE = true;
		}else{
			IS_ENFORCE = false;
			type = POLICY_MONITOR_TYPE_NO_ENFORCE_MESSAGE_RETURN;
		}

		SharedPreferences mainPref = context.getSharedPreferences("com.mdm",
				Context.MODE_PRIVATE);
		policy = mainPref.getString("policy", "");

		try {
			JSONArray jArray = null;
			if(recJArray!=null){
				jArray = recJArray;
			}else{
				jArray = new JSONArray(policy);
			}
			Log.e("POLICY ARAY : ", jArray.toString());
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject policyObj = (JSONObject) jArray.getJSONObject(i);
				if (policyObj.getString("data") != null
						&& policyObj.getString("data") != "") {
					testPolicy(policyObj.getString("code"),
							policyObj.getString("data"));
				}
			}
			
			JSONObject rootObj = new JSONObject();
			try {
				if(deviceInfo.isRooted()){
					rootObj.put("status", false);
				}else{
					rootObj.put("status", true);
				}
				rootObj.put("code", "notrooted");
				finalArray.put(rootObj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Log.e("MONITOR POLICY : ",policy);
			Log.e("MONITOR USER MESSAGE : ",usermessage);
			//Display an alert to the user about policy violation
			if(policy!=null && policy !=""){
				if(usermessage!=null && usermessage!="" && type == POLICY_MONITOR_TYPE_NO_ENFORCE_MESSAGE_RETURN){
					Intent intent = new Intent(context, AlertActivity.class);
					intent.putExtra("message", usermessage);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
			}
			
			returnJSON.put("code", CommonUtilities.OPERATION_POLICY_MONITOR);
			returnJSON.put("data", finalArray);
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("code", CommonUtilities.OPERATION_POLICY_MONITOR);
			params.put("msgID", msgID);
			params.put("status", "200");
			params.put("data", finalArray.toString());
			
			//ServerUtilities.pushData(params, context);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@SuppressWarnings("static-access")
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public boolean testPolicy(String code, String data) {
		if (code.equals(CommonUtilities.OPERATION_CLEAR_PASSWORD)) {
			ComponentName demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);
			JSONObject jobj = new JSONObject();
			// data = intent.getStringExtra("data");
			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code);
				params.put("status", "200");

				if(IS_ENFORCE){
					devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
							DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
					devicePolicyManager.resetPassword("",
							DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
					devicePolicyManager.lockNow();
					devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
							DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
					jobj.put("status", true);
				}else{
					if(devicePolicyManager.getPasswordQuality(demoDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED){
						jobj.put("status", false);
					}else{
						jobj.put("status", true);
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				jobj.put("code", code);
				
				//finalArray.put(jobj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			

		} else if (code.equals(CommonUtilities.OPERATION_WIFI)) {
			boolean wifistatus = false;
			JSONObject jobjc = new JSONObject();
			WiFiConfig config = new WiFiConfig(context);
			// data = intent.getStringExtra("data");
			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data);
				if(!jobj.isNull("ssid")){
					ssid = (String) jobj.get("ssid");
				}
				if(!jobj.isNull("password")){
					password = (String) jobj.get("password");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map<String, String> inparams = new HashMap<String, String>();
			inparams.put("code", code);
			if(IS_ENFORCE){
			try {
				wifistatus = config.saveWEPConfig(ssid, password);
				jobjc.put("status", true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			
			try {
				if(config.readWEPConfig(ssid)){
					jobjc.put("status", true);
				}else{
					jobjc.put("status", false);
					if(usermessage!=null && usermessage!=""){
						usermessage+="\nYou are not using company WIFI account, please change your WIFI configuration \n";
					}else{
						usermessage+="You are not using company WIFI account, please change your WIFI configuration \n";
					}
					
				}
				jobjc.put("code", code);
				
				finalArray.put(jobjc);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code.equals(CommonUtilities.OPERATION_DISABLE_CAMERA)) {
			ComponentName cameraAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);
			boolean camFunc = false;
			// data = intent.getStringExtra("data");
			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data);
				
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

				
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code);
				params.put("status", "200");
				String cammode = "Disabled";
				if (camFunc) {
					cammode = "Disabled";
				} else {
					cammode = "Enabled";
				}

				if (IS_ENFORCE && (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)) {
					devicePolicyManager.setCameraDisabled(cameraAdmin, camFunc);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jobj = new JSONObject();
			try {
				if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					if(!camFunc){
						if(!devicePolicyManager.getCameraDisabled(cameraAdmin)){
							jobj.put("status", true);
						}else{
							jobj.put("status", false);
						}
					}else{
						if(devicePolicyManager.getCameraDisabled(cameraAdmin)){
							jobj.put("status", true);
						}else{
							jobj.put("status", false);
							/*if(usermessage!=null && usermessage!=""){
								usermessage+="\nYour camera should be deactivated according to the policy, please deactivate your camera\n";
							}else{
								usermessage+="Your camera should be deactivated according to the policy, please deactivate your camera \n";
							}*/
						}
					}
				}else{
					jobj.put("status", false);
				}
				jobj.put("code", code);
				
				finalArray.put(jobj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code.equals(CommonUtilities.OPERATION_ENCRYPT_STORAGE)) {
			boolean encryptFunc = true;
			String pass = "";

			JSONParser jp = new JSONParser();
			try {
				JSONObject jobj = new JSONObject(data);

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
				params.put("code", code);
				
				if(IS_ENFORCE){
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
				}
				if (devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
					params.put("status", "200");
				} else {
					params.put("status", "400");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jobj = new JSONObject();
			try {
				jobj.put("code", code);
				if(encryptFunc){
					if(devicePolicyManager.getStorageEncryptionStatus()!= devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED && devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.ENCRYPTION_STATUS_INACTIVE){
						jobj.put("status", true);
					}else{
						jobj.put("status", false);	
						if(usermessage!=null && usermessage!=""){
							usermessage+="\nYour device should be encrypted according to the policy, please enable device encryption through device settings\n";
						}else{
							usermessage+="Your device should be encrypted according to the policy, please enable device encryption through device settings \n";
						}
					}
				}else{
					if(devicePolicyManager.getStorageEncryptionStatus()== devicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED || devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.ENCRYPTION_STATUS_INACTIVE){
						jobj.put("status", true);
					}else{
						jobj.put("status", false);	
					}
				}
				finalArray.put(jobj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (code.equals(CommonUtilities.OPERATION_MUTE)) {

			try {
				Map<String, String> params = new HashMap<String, String>();
				params.put("code", code);
				params.put("status", "200");
				if(IS_ENFORCE){
					muteDevice();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jobj = new JSONObject();
			try {
				jobj.put("code", code);
				if(isMuted()){
					jobj.put("status", true);
				}else{
					jobj.put("status", false);
					if(usermessage!=null && usermessage!=""){
						usermessage+="\nYour phone should be muted according to the policy, please mute your phone \n";
					}else{
						usermessage+="Your phone should be muted according to the policy, please mute your phone \n";
					}
				}
				finalArray.put(jobj);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		} else if (code.equals(CommonUtilities.OPERATION_PASSWORD_POLICY)) {
			
			ComponentName demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);
			JSONObject jobjx = new JSONObject();
			int attempts, length, history, specialChars;
			String alphanumeric, complex;
			boolean b_alphanumeric=false, b_complex=false, is_comply=true, comply_fac1=true, comply_fac2=true, comply_fac3=true, comply_fac4=true, comply_fac5=true, comply_fac6=true, comply_fac7=true;
			long timout;
			Map<String, String> inparams = new HashMap<String, String>();
			// data = intent.getStringExtra("data");
			JSONParser jp = new JSONParser();
			try {
				JSONObject jobjpass = new JSONObject();
				jobjpass.put("code", CommonUtilities.OPERATION_CHANGE_LOCK_CODE);
				
				if(devicePolicyManager.isActivePasswordSufficient()){
					is_comply=true;
					//finalArray.put(jobjpass);
				}else{
					is_comply=false;
				}
				
				JSONObject jobj = new JSONObject(data);
				if (!jobj.isNull("maxFailedAttempts")
						&& jobj.get("maxFailedAttempts") != null) {
					attempts = Integer.parseInt((String) jobj
							.get("maxFailedAttempts"));
					if(IS_ENFORCE){
					devicePolicyManager.setMaximumFailedPasswordsForWipe(
							demoDeviceAdmin, attempts);
					comply_fac1=true;
					}else{
						if(devicePolicyManager.getMaximumFailedPasswordsForWipe(demoDeviceAdmin) != attempts){
							comply_fac1=false;
						}else{
							comply_fac1=true;
						}
					}
				}

				if (!jobj.isNull("minLength") && jobj.get("minLength") != null) {
					length = Integer.parseInt((String) jobj.get("minLength"));
					if(IS_ENFORCE){
					devicePolicyManager.setPasswordMinimumLength(
							demoDeviceAdmin, length);
					comply_fac2=true;
					}else{
						if(devicePolicyManager.getPasswordMinimumLength(demoDeviceAdmin) != length){
							comply_fac2=false;
						}else{
							comply_fac2=true;
						}
					}
				}

				if (!jobj.isNull("pinHistory")
						&& jobj.get("pinHistory") != null) {
					history = Integer.parseInt((String) jobj.get("pinHistory"));
					if(IS_ENFORCE){
					devicePolicyManager.setPasswordHistoryLength(
							demoDeviceAdmin, history);
					comply_fac3=true;
					}else{
						if(devicePolicyManager.getPasswordHistoryLength(demoDeviceAdmin) != history){
							comply_fac3=false;
						}else{
							comply_fac3=true;
						}
					}
				}

				if (!jobj.isNull("minComplexChars")
						&& jobj.get("minComplexChars") != null) {
					specialChars = Integer.parseInt((String) jobj
							.get("minComplexChars"));
					if(IS_ENFORCE){
					devicePolicyManager.setPasswordMinimumSymbols(
							demoDeviceAdmin, specialChars);
					comply_fac4=true;
					}else{
						if(devicePolicyManager.getPasswordMinimumSymbols(demoDeviceAdmin) != specialChars){
							comply_fac4=false;
						}else{
							comply_fac4=true;
						}
					}
				}

				if (!jobj.isNull("requireAlphanumeric")
						&& jobj.get("requireAlphanumeric") != null) {
					
					if(jobj.get("requireAlphanumeric") instanceof String){
						alphanumeric = (String) jobj.get("requireAlphanumeric").toString();
						if (alphanumeric.equals("true")) {
							b_alphanumeric=true;
						}else{
							b_alphanumeric=false;
						}
					}else if(jobj.get("requireAlphanumeric") instanceof Boolean){
						b_alphanumeric =  jobj.getBoolean("requireAlphanumeric");
					}
					if (b_alphanumeric) {
						if(IS_ENFORCE){
						devicePolicyManager
								.setPasswordQuality(
										demoDeviceAdmin,
										DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
						comply_fac5=true;
						}else{
							if(devicePolicyManager.getPasswordQuality(demoDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC){
								comply_fac5=false;
							}else{
								comply_fac5=true;
							}
						}
					}else{
						if(devicePolicyManager.getPasswordQuality(demoDeviceAdmin) == DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC){
							comply_fac5=false;
						}else{
							comply_fac5=true;
						}
					}
				}

				if (!jobj.isNull("allowSimple")
						&& jobj.get("allowSimple") != null) {
					
					if(jobj.get("allowSimple") instanceof String){
						complex = (String) jobj.get("allowSimple").toString();
						if (complex.equals("true")) {
							b_complex=true;
						}else{
							b_complex=false;
						}
					}else if(jobj.get("allowSimple") instanceof Boolean){
						b_complex =  jobj.getBoolean("allowSimple");
					}

					if (!b_complex) {
						if(IS_ENFORCE){
						devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
								DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
						comply_fac6=true;
						}else{
							if(devicePolicyManager.getPasswordQuality(demoDeviceAdmin) != DevicePolicyManager.PASSWORD_QUALITY_COMPLEX){
								comply_fac6=false;
							}else{
								comply_fac6=true;
							}
						}
					}else{
						if(devicePolicyManager.getPasswordQuality(demoDeviceAdmin) == DevicePolicyManager.PASSWORD_QUALITY_COMPLEX){
							comply_fac6=false;
						}else{
							comply_fac6=true;
						}
					}
				}

				if (!jobj.isNull("maxPINAgeInDays")
						&& jobj.get("maxPINAgeInDays") != null) {
					int daysOfExp = Integer.parseInt((String) jobj
							.get("maxPINAgeInDays"));
					timout = (long) (daysOfExp * 24 * 60 * 60 * 1000);
					if(IS_ENFORCE){
					devicePolicyManager.setPasswordExpirationTimeout(
							demoDeviceAdmin, timout);
					comply_fac7=true;
					}else{
						if(devicePolicyManager.getPasswordExpirationTimeout(demoDeviceAdmin) != timout){							
							comply_fac7=false;
						}else{							
							comply_fac7=true;
						}
					}
				}
				
				if(!is_comply || !comply_fac1 || !comply_fac2 || !comply_fac3 || !comply_fac4 || !comply_fac5 || !comply_fac6 || !comply_fac7){
					jobjx.put("status", false);
					if(usermessage!=null && usermessage!=""){
						 usermessage+="\nYour screen lock password doesn't meet current policy requirement. Please reset your passcode \n";
					}else{
						 usermessage+="Your screen lock password doesn't meet current policy requirement. Please reset your passcode \n";
					}
				}else{
					jobjx.put("status", true);
				}
				
				
				inparams.put("code", code);
				inparams.put("status", "200");

			} catch (Exception e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
			
			try {
				jobjx.put("code", code);
				
				finalArray.put(jobjx);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}else if (code
				.equals(CommonUtilities.OPERATION_BLACKLIST_APPS)) {
			ArrayList<PInfo> apps = appList.getInstalledApps(false); /*
																	 * false =
																	 * no system
																	 * packages
																	 */
			JSONArray jsonArray = new JSONArray();
			int max = apps.size();
			
			Boolean flag = true;
			
			try{
					JSONObject appObj = new JSONObject(data);
					String identity = (String) appObj.get("identity");
					for (int j = 0; j < max; j++) {
						JSONObject jsonObj = new JSONObject();
						try {
							jsonObj.put("name", apps.get(j).appname);
							jsonObj.put("package", apps.get(j).pname);
							if(identity.trim().equals(apps.get(j).pname)){
								jsonObj.put("notviolated", false);
								flag = false;
								jsonObj.put("package", apps.get(j).pname);
								if(apps.get(j).appname!=null){
									appcount++;
									apz = appcount+". "+apps.get(j).appname;									
								}
								
								if(apz!=null || !apz.trim().equals("")){
									if(usermessage!=null && usermessage!=""){
										if(appcount>1){
											usermessage+="\n"+apz;
										}else{
											usermessage+="\nFollowing apps are blacklisted by your MDM Admin, please remove them \n\n"+apz;
										}												
									}else{
										if(appcount>1){
											usermessage+="\n"+apz;
										}else{
											usermessage+="Following apps are blacklisted by your MDM Admin, please remove them \n\n"+apz;
										}
										
									}
								}
																		
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
			
			/*
			 * for(int i=0;i<apps.length;i++){ jsonArray.add(apps[i]); }
			 */
			JSONObject appsObj = new JSONObject();
			try {
				//appsObj.put("data", jsonArray);
				appsObj.put("status", flag);
				appsObj.put("code", code);
				finalArray.put(appsObj);
				
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}
		
		return true;
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
	
	private boolean isMuted(){
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if(audioManager.getStreamVolume(AudioManager.STREAM_RING)!=0){
			return false;
		}else{
			return true;
		}
	}
}
