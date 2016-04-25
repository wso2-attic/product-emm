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

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
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

import org.wso2.emm.agent.services.PolicyOperationsMapper;
import org.wso2.emm.agent.services.PolicyRevokeHandler;

import java.io.IOException;
import java.util.List;

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
		ServerConfig utils = new ServerConfig();
		apiUtilities.setEndPoint(endpoint);
		apiUtilities.setHttpMethod(methodType);
		if (requestParams != null) {
			apiUtilities.setRequestParams(requestParams);
		}
		APIController apiController;

		if (org.wso2.emm.agent.proxy.utils.Constants.Authenticator.AUTHENTICATOR_IN_USE.
				equals(org.wso2.emm.agent.proxy.utils.Constants.Authenticator.
						       MUTUAL_SSL_AUTHENTICATOR)) {
			apiController = new APIController();
			apiController.securedNetworkCall(apiResultCallBack, requestCode, apiUtilities, context);
		} else {
			String clientKey = Preference.getString(context, Constants.CLIENT_ID);
			String clientSecret = Preference.getString(context, Constants.CLIENT_SECRET);
			if (utils.getHostFromPreferences(context) != null
			    && !utils.getHostFromPreferences(context).isEmpty() &&
			    clientKey != null && !clientKey.isEmpty() && !clientSecret.isEmpty()) {
				apiController = new APIController(clientKey, clientSecret);
				apiController.invokeAPI(apiUtilities, apiResultCallBack, requestCode,
				                        context.getApplicationContext());
			}
		}

	}

	/**
	 * Clear application data.
	 * @param context - Application context.
	 */
	public static void clearAppData(Context context) throws AndroidAgentException {
		try {
			revokePolicy(context);
		} catch (SecurityException e) {
			throw new AndroidAgentException("Error occurred while revoking policy", e);
		} finally {
			Resources resources = context.getResources();
			SharedPreferences mainPref = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
			Editor editor = mainPref.edit();
			editor.putBoolean(Constants.PreferenceFlag.IS_AGREED, false);
			editor.putString(Constants.PreferenceFlag.REG_ID, null);
			editor.putBoolean(Constants.PreferenceFlag.REGISTERED, false);
			editor.putString(Constants.PreferenceFlag.IP, null);
			editor.putString(Constants.PreferenceFlag.NOTIFIER_TYPE, null);
			editor.putString(context.getResources().getString(R.string.shared_pref_sender_id),
			                 resources.getString(R.string.shared_pref_default_string));
			editor.putString(context.getResources().getString(R.string.shared_pref_eula),
			                 resources.getString(R.string.shared_pref_default_string));
			editor.putBoolean(Constants.PreferenceFlag.DEVICE_ACTIVE, false);
			editor.commit();
			Preference.clearPreferences(context);
			clearClientCredentials(context);
		}
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

	/**
	 * Convert given object to json formatted string.
	 * @param obj Object to be converted.
	 * @return Json formatted string.
	 * @throws AndroidAgentException
	 */
	public static String toJSON (Object obj) throws AndroidAgentException {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(obj);
		} catch (JsonMappingException e) {
			throw new AndroidAgentException("Error occurred while mapping class to json", e);
		} catch (JsonGenerationException e) {
			throw new AndroidAgentException("Error occurred while generating json", e);
		} catch (IOException e) {
			throw new AndroidAgentException("Error occurred while reading the stream", e);
		}
	}

	/**
	 * This method is used to initiate the oauth client app unregister process.
	 *
	 * @param context Application context
	 * @throws AndroidAgentException
	 */
	public static void unRegisterClientApp(Context context) throws AndroidAgentException {
		String serverIP = Preference.getString(context, Constants.PreferenceFlag.IP);

		if (serverIP != null && !serverIP.isEmpty()) {
			String applicationName = Preference.getString(context, Constants.CLIENT_NAME);
			String consumerKey = Preference.getString(context, Constants.CLIENT_ID);
			String userId = Preference.getString(context, Constants.USERNAME);

			if (applicationName != null && !applicationName.isEmpty() &&
			    consumerKey != null && !consumerKey.isEmpty() &&
			    userId != null && !userId.isEmpty()) {

				UnregisterProfile profile = new UnregisterProfile();
				profile.setApplicationName(applicationName);
				profile.setConsumerKey(consumerKey);
				profile.setUserId(userId);

				ServerConfig utils = new ServerConfig();
				utils.setServerIP(serverIP);

				DynamicClientManager dynamicClientManager = new DynamicClientManager();
				boolean isUnregistered = dynamicClientManager.unregisterClient(profile, utils, context);

				if (!isUnregistered) {
					Log.e(TAG, "Error occurred while removing the OAuth client app");
				}
			} else {
				Log.e(TAG, "Client credential is not available");
			}
		} else {
			Log.e(TAG, "There is no valid IP to contact the server");
		}
	}

	/**
	 * Disable admin privileges.
	 * @param context - Application context.
	 */
	public static void disableAdmin(Context context) {
		DevicePolicyManager devicePolicyManager;

		ComponentName demoDeviceAdmin;
		devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		demoDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		devicePolicyManager.removeActiveAdmin(demoDeviceAdmin);
	}

	/**
	 * Revoke currently enforced policy.
	 * @param context - Application context.
	 */
	public static void revokePolicy(Context context) throws AndroidAgentException {
		String payload = Preference.getString(context, Constants.PreferenceFlag.APPLIED_POLICY);
		PolicyOperationsMapper operationsMapper = new PolicyOperationsMapper();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		PolicyRevokeHandler revokeHandler = new PolicyRevokeHandler(context);

		try {
			if(payload != null) {
				List<org.wso2.emm.agent.beans.Operation> operations = mapper.readValue(
						payload,
						mapper.getTypeFactory().constructCollectionType(List.class,
						                                                org.wso2.emm.agent.beans.Operation.class));
				for (org.wso2.emm.agent.beans.Operation op : operations) {
					op = operationsMapper.getOperation(op);
					revokeHandler.revokeExistingPolicy(op);
				}
				Preference.putString(context, Constants.PreferenceFlag.APPLIED_POLICY, null);
			}
		} catch (IOException e) {
			throw new AndroidAgentException("Error occurred while parsing stream", e);
		}
	}

	/**
	 * Clear client credentials.
	 * @param context - Application context.
	 */
	public static void clearClientCredentials(Context context) {
		SharedPreferences mainPref = context.getSharedPreferences(Constants.PACKAGE_NAME, Context.MODE_PRIVATE);
		Editor editor = mainPref.edit();
		editor.putString(Constants.CLIENT_ID, null);
		editor.putString(Constants.CLIENT_SECRET, null);
		editor.commit();
	}

	/**
	 * Call EMM system app in COPE mode.
	 * @param context - Application context.
	 * @param operation - Operation code.
	 * @param command - Shell command to be executed.
	 */
	public static void callSystemApp(Context context, String operation, String command, String appUri) {
		if(Constants.SYSTEM_APP_ENABLED) {
			Intent intent =  new Intent(Constants.SYSTEM_APP_SERVICE_NAME);
			intent = createExplicitFromImplicitIntent(context,intent);
			intent.putExtra("code", operation);
			intent.setPackage(Constants.PACKAGE_NAME);
			if (command != null) {
				intent.putExtra("command", command);
			}
			if (appUri != null) {
				intent.putExtra("appUri", appUri);
			}
			context.startService(intent);
		} else {
			Log.e(TAG, "System app not enabled.");
		}
	}

	public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
		//Retrieve all services that can match the given intent
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

		//Make sure only one match was found
		if (resolveInfo == null || resolveInfo.size() != 1) {
			return null;
		}

		//Get component info and create ComponentName
		ResolveInfo serviceInfo = resolveInfo.get(0);
		String packageName = serviceInfo.serviceInfo.packageName;
		String className = serviceInfo.serviceInfo.name;
		ComponentName component = new ComponentName(packageName, className);

		//Create a new intent. Use the old one for extras and such reuse
		Intent explicitIntent = new Intent(implicitIntent);

		//Set the component to be explicit
		explicitIntent.setComponent(component);

		return explicitIntent;
	}
}
