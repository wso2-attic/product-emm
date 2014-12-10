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

import org.wso2.cdm.agent.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Helper class providing methods and constants common to other classes in the
 * app.
 */
public class CommonUtilities {
	public static boolean DEBUG_MODE_ENABLED = true;
	public static boolean LOCAL_NOTIFICATIONS_ENABLED = true;
	public static boolean GCM_ENABLED = false;

	public static String SERVER_IP = "";

	public static String SERVER_PORT = "9763";
	public static String SERVER_PROTOCOL = "http://";
	public static String API_VERSION = "1.0.0";
	
	public static String SERVER_APP_ENDPOINT = "/cdm-android-api/";
	public static String SERVER_AUTHENTICATION_ENDPOINT="register/authenticate/device";
	public static String LICENSE_ENDPOINT = "register/authenticate/device/license";
	public static String REGISTER_ENDPOINT = "enrollment/authenticate/device/enroll";
	
	public static String OAUTH_ENDPOINT = "/oauth2/token";
	public static String SENDER_ID_ENDPOINT = "devices/sender_id/";
	public static String IS_REGISTERED_ENDPOINT = "devices/isregistered/";
	public static String UNREGISTER_ENDPOINT = "devices/unregister/";
	public static String NOTIFICATION_ENDPOINT = "notifications/pendingOperations/";
	
	public static String SERVER_URL = SERVER_PROTOCOL + SERVER_IP + ":"
			+ SERVER_PORT + SERVER_APP_ENDPOINT;
	

	public static final String TRUSTSTORE_PASSWORD = "wso2carbon";
	public static final String EULA_TITLE = "POLICY AGREEMENT";
	public static final String EULA_TEXT = "Test policy agreement.";

	/* Added for OAuth implementation */
	public static  String CLIENT_ID = "";
	public static  String CLIENT_SECRET = "";


	public static final String EMPTY_STRING = "";
	public static final String NULL_STRING = "null";
	public static final String STATUS_KEY = "status";
	
	/* Request codes. */
	public static final int REGISTER_REQUEST_CODE = 300;
	public static final int IS_REGISTERED_REQUEST_CODE = 301;
	public static final int SENDER_ID_REQUEST_CODE = 303;
	public static final int LICENSE_REQUEST_CODE = 304;
	public static final int UNREGISTER_REQUEST_CODE = 305;
	public static final int NOTIFICATION_REQUEST_CODE = 306;
	
	/* Method types. */
	public static final String GET_METHOD = "GET";
	public static final String POST_METHOD = "POST";
	

	public static String getSERVER_URL() {
		return SERVER_URL;
	}

	public static void setServerURL(String serverIP) {
		SERVER_IP = serverIP;
		SERVER_URL = SERVER_PROTOCOL + serverIP + ":" + SERVER_PORT
				+SERVER_APP_ENDPOINT;
		
	}

	/**
	 * Google API project id registered to use GCM.
	 */

	public static String SENDER_ID = "";

	public static String getSENDER_ID() {
		return SENDER_ID;
	}

	public static void setSENDER_ID(String sENDER_ID) {
		SENDER_ID = sENDER_ID;
	}

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = "WSO2EMM";

	/**
	 * Intent used to display a message in the screen.
	 */
	public static final String DISPLAY_MESSAGE_ACTION = "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

	/**
	 * Intent's extra that contains the message to be displayed.
	 */
	public static final String EXTRA_MESSAGE = "message";
	public static final int MESSAGE_MODE_GCM = 1;
	public static final int MESSAGE_MODE_SMS = 2;
	public static final int MESSAGE_MODE_LOCAL = 3;
	

	/**
	 * Status codes
	 */
	public static final String REQUEST_SUCCESSFUL = "200";
	public static final String REGISTERATION_SUCCESSFUL = "201";
	public static final String REQUEST_FAILED = "500";
	public static final String AUTHENTICATION_FAILED = "400";
	public static final String UNAUTHORIZED_ACCESS = "401";
	public static final String NOT_FOUND = "404";
	public static final String INTERNAL_SERVER_ERROR = "500";
	

	/**
	 * Operation IDs
	 */
	public static final String OPERATION_DEVICE_INFO = "500A";
	public static final String OPERATION_DEVICE_LOCATION = "501A";
	public static final String OPERATION_GET_APPLICATION_LIST = "502A";
	public static final String OPERATION_LOCK_DEVICE = "503A";
	public static final String OPERATION_WIPE_DATA = "504A";//reset device
	public static final String OPERATION_CLEAR_PASSWORD = "505A";
	public static final String OPERATION_NOTIFICATION = "506A";
	public static final String OPERATION_WIFI = "507A";
	public static final String OPERATION_DISABLE_CAMERA = "508A";
	public static final String OPERATION_INSTALL_APPLICATION = "509A";
	public static final String OPERATION_INSTALL_APPLICATION_BUNDLE = "509B";
	public static final String OPERATION_UNINSTALL_APPLICATION = "510A";
	public static final String OPERATION_ENCRYPT_STORAGE = "511A";
	public static final String OPERATION_APN = "512A";
	public static final String OPERATION_MUTE = "513A";
	public static final String OPERATION_TRACK_CALLS = "514A";
	public static final String OPERATION_TRACK_SMS = "515A";
	public static final String OPERATION_DATA_USAGE = "516A";
	public static final String OPERATION_STATUS = "517A";
	public static final String OPERATION_WEBCLIP = "518A";
	public static final String OPERATION_PASSWORD_POLICY = "519A";
	public static final String OPERATION_EMAIL_CONFIGURATION = "520A";
	public static final String OPERATION_INSTALL_GOOGLE_APP = "522A";
	public static final String OPERATION_CHANGE_LOCK_CODE = "526A";
	public static final String OPERATION_ENTERPRISE_WIPE_DATA = "527A";//unnregister device
	public static final String OPERATION_POLICY_BUNDLE = "500P";
	public static final String OPERATION_POLICY_MONITOR = "501P";
	public static final String OPERATION_BLACKLIST_APPS = "528B";
	public static final String OPERATION_POLICY_REVOKE = "502P";

	/**
	 * Notifies UI to display a message.
	 * <p>
	 * This method is defined in the common helper because it's used both by the
	 * UI and the background service.
	 * 
	 * @param context
	 *            application's context.
	 * @param message
	 *            message to be displayed.
	 */
	public static void displayMessage(Context context, String message) {
		Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
		intent.putExtra(EXTRA_MESSAGE, message);
		context.sendBroadcast(intent);
	}
	
	public static String getPref(Context context, String key) {
		SharedPreferences mainPref = context.getSharedPreferences(context
				.getResources().getString(R.string.shared_pref_package),
				Context.MODE_PRIVATE);
		return mainPref.getString(key, "");
	}

}
