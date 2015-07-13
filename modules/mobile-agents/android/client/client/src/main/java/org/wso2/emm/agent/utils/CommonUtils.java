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
package org.wso2.emm.agent.utils;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.beans.UnregisterProfile;
import org.wso2.emm.agent.proxy.APIController;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.services.DynamicClientManager;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * This class represents all the common functions used throughout the application.
 */
public class CommonUtils {

	public static String TAG = CommonUtils.class.getSimpleName();

	/**
	 * Calls the secured API.
	 * @param context           -The Activity which calls an API..
	 * @param endpoint          -The API endpoint.
	 * @param methodType        -The method type.
	 * @param apiResultCallBack -The API result call back object.
	 * @param requestCode       -The request code.
	 */
	public static void callSecuredAPI(Context context, String endpoint, HTTP_METHODS methodType,
									  String requestParams,
									  APIResultCallBack apiResultCallBack, int requestCode) {

		EndPointInfo apiUtilities = new EndPointInfo();
		apiUtilities.setEndPoint(endpoint);
		apiUtilities.setHttpMethod(methodType);
		if (requestParams != null) {
			apiUtilities.setRequestParams(requestParams);
		}
		APIController apiController;
		String clientKey = Preference.getString(context, Constants.CLIENT_ID);
		String clientSecret = Preference.getString(context, Constants.CLIENT_SECRET);
		if (clientKey!=null && !clientKey.isEmpty() && !clientSecret.isEmpty()) {
			apiController = new APIController(clientKey, clientSecret);
			apiController.invokeAPI(apiUtilities, apiResultCallBack, requestCode,
					context.getApplicationContext());
		}

	}

	/**
	 * Clear application data.
	 * @param context - Application context.
	 */
	public static void clearAppData(Context context) {

		Resources resources = context.getResources();
		SharedPreferences mainPref =
				context.getSharedPreferences(context.getResources()
								.getString(R.string.shared_pref_package),
						Context.MODE_PRIVATE
				);

		Editor editor = mainPref.edit();
		editor.putString(context.getResources().getString(R.string.shared_pref_policy),
				resources.getString(R.string.shared_pref_default_string));
		editor.putString(context.getResources().getString(R.string.shared_pref_isagreed),
				resources.getString(R.string.shared_pref_reg_fail));
		editor.putString(context.getResources().getString(R.string.shared_pref_regId),
				resources.getString(R.string.shared_pref_default_string));
		editor.putString(context.getResources().getString(R.string.shared_pref_registered),
				resources.getString(R.string.shared_pref_reg_fail));
		editor.putString(context.getResources().getString(R.string.shared_pref_ip),
				resources.getString(R.string.shared_pref_default_string));
		editor.putString(context.getResources().getString(R.string.shared_pref_sender_id),
				resources.getString(R.string.shared_pref_default_string));
		editor.putString(context.getResources().getString(R.string.shared_pref_eula),
				resources.getString(R.string.shared_pref_default_string));
		editor.putString(resources.getString(R.string.shared_pref_device_active),
				resources.getString(R.string.shared_pref_reg_fail));
		editor.putString(Constants.CLIENT_ID, null);
		editor.putString(Constants.CLIENT_SECRET, null);
		editor.commit();

	}

	/**
	 * Returns network availability status.
	 * @param context - Application context.
	 * @return - Network availability status.
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager =
				(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}

	public static String toJSON (Object obj) throws AndroidAgentException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (JsonMappingException e) {
			String errorMessage = "Error occurred while mapping class to json.";
			Log.e(TAG, errorMessage);
			throw new AndroidAgentException(errorMessage, e);
		} catch (JsonGenerationException e) {
			String errorMessage = "Error occurred while generating json.";
			Log.e(TAG, errorMessage);
			throw new AndroidAgentException(errorMessage, e);
		} catch (IOException e) {
			String errorMessage = "Error occurred while reading the stream.";
			Log.e(TAG, errorMessage);
			throw new AndroidAgentException(errorMessage, e);
		}
	}

	/**
	 * This method is used to initiate the oauth client app unregister process
	 *
	 * @param context Application context
	 * @throws AndroidAgentException
	 */
	public static void unRegisterClientApp(Context context) throws AndroidAgentException {

		String applicationName = Preference.getString(context, Constants.CLIENT_NAME);
		String consumerKey = Preference.getString(context, Constants.CLIENT_ID);
		String userId = Preference.getString(context, Constants.USERNAME);

		UnregisterProfile profile = new UnregisterProfile();
		profile.setApplicationName(applicationName);
		profile.setConsumerKey(consumerKey);
		profile.setUserId(userId);

		String serverIP = Preference.getString(context, Constants.IP);
		ServerConfig utils = new ServerConfig();
		utils.setServerIP(serverIP);

		DynamicClientManager dynamicClientManager = new DynamicClientManager();
		dynamicClientManager.unregisterClient(profile,utils);

	}

	/**
	 * Disable admin privileges.
	 * @param context - Application context.
	 */
	public static void disableAdmin(Context context) {
		DevicePolicyManager devicePolicyManager;
		ComponentName demoDeviceAdmin;
		devicePolicyManager =
				(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		devicePolicyManager.removeActiveAdmin(demoDeviceAdmin);
	}
}
