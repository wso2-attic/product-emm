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


import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.ServerUtils;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

/**
 * This is the component that is responsible for actual device administration.
 * It becomes the receiver when a policy is applied. It is important that we
 * subclass DeviceAdminReceiver class here and to implement its only required
 * method onEnabled().
 */
public class WSO2DeviceAdminReceiver extends DeviceAdminReceiver implements APIResultCallBack {
	static final String TAG = "WSO2DeviceAdminReceiver";
	AsyncTask<Void, Void, Void> mRegisterTask;
	String regId="";
	Operation operation;
	boolean unregState=false;
	/** Called when this application is approved to be a device administrator. */
	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
		String policy;
		JSONArray jArray = null;
		operation = new Operation(context);
		SharedPreferences mainPref = context.getSharedPreferences("com.mdm",
				Context.MODE_PRIVATE);
		Editor editor = mainPref.edit();
		editor.putString(context.getResources().getString(R.string.shared_pref_device_active), "1");
		editor.commit();
		
		ProcessMessage pm=new ProcessMessage(context);
		pm.getOperations(null);
		try {	
			policy = mainPref.getString("policy", "");
			if(policy!=null && !policy.equals("")){
				//operation.executePolicy();
			}
		}catch(Exception ex){
			
		}
		Toast.makeText(context, R.string.device_admin_enabled,
				Toast.LENGTH_LONG).show();
		Log.d(TAG, "onEnabled");
		
	}

	/** Called when this application is no longer the device administrator. */
	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);
		Toast.makeText(context, R.string.device_admin_disabled,
				Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDisabled");
		if(regId == null || regId.equals("")){
			regId = GCMRegistrar.getRegistrationId(context);
		}
		
		if(regId != null || !regId.equals("")){
			startUnRegistration(context);
		}
		
	}
	
	public void startUnRegistration(Context app_context) {
		String regId = CommonUtilities.getPref(app_context, app_context
				.getResources().getString(R.string.shared_pref_regId));

		Map<String, String> requestParams = new HashMap<String, String>();
		requestParams.put("regid", regId);
		ServerUtils.clearAppData(app_context);
		ServerUtils.callSecuredAPI(app_context,
				CommonUtilities.UNREGISTER_ENDPOINT,
				CommonUtilities.POST_METHOD, requestParams,
				WSO2DeviceAdminReceiver.this,
				CommonUtilities.UNREGISTER_REQUEST_CODE);
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		super.onPasswordChanged(context, intent);
		Log.d(TAG, "onPasswordChanged");
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		super.onPasswordFailed(context, intent);
		Log.d(TAG, "onPasswordFailed");
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		super.onPasswordSucceeded(context, intent);
		Log.d(TAG, "onPasswordSucceeded");
	}

	@Override
	public void onReceiveAPIResult(Map<String, String> arg0, int arg1) {
		// We have already cleaned data from device.
		
	}
	
	

}
