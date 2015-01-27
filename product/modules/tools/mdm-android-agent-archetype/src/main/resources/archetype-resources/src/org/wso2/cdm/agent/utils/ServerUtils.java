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
package org.wso2.cdm.agent.utils;

import java.util.Map;

import org.json.JSONObject;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.proxy.APIController;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.proxy.APIUtilities;
import org.wso2.cdm.agent.services.WSO2DeviceAdminReceiver;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ServerUtils {
	
	public static String TAG = ServerUtils.class.getSimpleName();

	/**
	 * calls the secured API
	 * 
	 * @param context
	 *            the Activity which calls an API
	 * @param serverUrl
	 *            the server url
	 * @param endpoint
	 *            the API endpoint
	 * @param apiVersion
	 *            the API version
	 * @param methodType
	 *            the method type
	 * @param apiResultCallBack
	 *            the API result call back object
	 * @param requestCode
	 *            the request code
	 */
	public static void callSecuredAPI(Context context, String endpoint, 
	                                  String methodType, JSONObject requestParams,
			APIResultCallBack apiResultCallBack, int requestCode) {
		
		Log.e("",endpoint);
		APIUtilities apiUtilities = new APIUtilities();
		apiUtilities.setEndPoint(endpoint);
		apiUtilities.setHttpMethod(methodType);
		if (requestParams != null) {
			apiUtilities.setRequestParams(requestParams);
		}
		APIController apiController = new APIController();
		String clientKey=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_client_id));
		String clientSecret=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_client_secret));
		if(!clientKey.equals("") && !clientSecret.equals("")){
			CommonUtilities.CLIENT_ID=clientKey;
			CommonUtilities.CLIENT_SECRET=clientSecret;
			apiController.setClientDetails(clientKey, clientSecret);
		}
		apiController.invokeAPI(apiUtilities, apiResultCallBack, requestCode, context.getApplicationContext());
	}

	public static void clearAppData(Context context) {
		DevicePolicyManager devicePolicyManager;
		ComponentName demoDeviceAdmin;
		try {
			devicePolicyManager = (DevicePolicyManager) context
					.getSystemService(Context.DEVICE_POLICY_SERVICE);
			demoDeviceAdmin = new ComponentName(context,
					WSO2DeviceAdminReceiver.class);
			SharedPreferences mainPref = context.getSharedPreferences(context
					.getResources().getString(R.string.shared_pref_package),
					Context.MODE_PRIVATE);
			Editor editor = mainPref.edit();
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_policy), "");
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_isagreed), "0");
			editor.putString(
					context.getResources()
							.getString(R.string.shared_pref_regId), "");
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_registered), "0");
			editor.putString(
					context.getResources().getString(R.string.shared_pref_ip),
					"");
			editor.putString(
					context.getResources().getString(
							R.string.shared_pref_sender_id), "");
			editor.putString(
					context.getResources().getString(R.string.shared_pref_eula),
					"");
			editor.commit();
			devicePolicyManager.removeActiveAdmin(demoDeviceAdmin);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
