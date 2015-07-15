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

/**
 * This class holds all the constants used throughout the application.
 */
public class Constants {

	public static final boolean DEBUG_MODE_ENABLED = false;
	public static final boolean LOCAL_NOTIFICATIONS_ENABLED = true;
	public static final boolean GCM_ENABLED = false;
    public static final String GCM_PROJECT_NUMBER = "487975211959";
    public static final String REG_ID = "reg_id";

	public static final String SERVER_PORT = "9763";
	public static final String SERVER_PROTOCOL = "http://";
	public static final String API_VERSION = "1.0.0";
	public static final String API_SERVER_PORT = "9763";

	public static final String SERVER_APP_ENDPOINT = "/mdm-android-agent/";
	public static final String SERVER_AUTHENTICATION_ENDPOINT = "register/authenticate/device";
	public static final String LICENSE_ENDPOINT = SERVER_APP_ENDPOINT + "device/license";
	public static final String REGISTER_ENDPOINT = SERVER_APP_ENDPOINT + "enrollment/";

	public static final String OAUTH_ENDPOINT = "/oauth2/token";
	public static final String DEVICE_ENDPOINT = SERVER_APP_ENDPOINT + "device/";
	public static final String IS_REGISTERED_ENDPOINT = REGISTER_ENDPOINT;
	public static final String UNREGISTER_ENDPOINT =  REGISTER_ENDPOINT;
	public static final String NOTIFICATION_ENDPOINT = SERVER_APP_ENDPOINT + "operation/";
	public static final String GOOGLE_PLAY_APP_URI = "market://details?id=";
	public static final String DYNAMIC_CLIENT_REGISTER_ENDPOINT = "/dynamic-client-manager/register";
	public static final String POLICY_ENDPOINT = SERVER_APP_ENDPOINT + "policy/";

	public static final String TRUSTSTORE_PASSWORD = "wso2carbon";
	public static final String EULA_TITLE = "POLICY AGREEMENT";
	public static final String EULA_TEXT = "Test policy agreement.";

	public static final String EMPTY_STRING = "";
	public static final String NULL_STRING = "null";
	public static final String STATUS_KEY = "status";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	public static final String STATUS = "status";
	public static final String RESPONSE = "response";
	public static final String PAYLOAD = "payLoad";
	public static final String CODE = "code";
	public static final String TYPE = "type";
	public static final String ID = "id";
	public static final String TYPE_COMMAND = "COMMAND";
	public static final String TYPE_CONFIG = "CONFIG";
	public static final String TYPE_PROFILE = "PROFILE";
	public static final String ENABLED= "enabled";
	public static final String LOCAL = "LOCAL";
	public static final String LOG_FILE = "wso2log.txt";
    public static final String MESSAGE_TYPE_GCM = "gcm";
	public static final String OWNERSHIP_BYOD = "BYOD";
	public static final String OWNERSHIP_COPE = "COPE";
	public static final String DEVICE_TYPE = "deviceType";
	public static final String CLIENT_ID = "clientId";
	public static final String CLIENT_SECRET = "clientSecret";
	public static final String CLIENT_NAME = "clientName";
	public static final String IP = "ip";
	public static final String GRANT_TYPE = "password";
	public static final String TOKEN_SCOPE = "production";

	/**
	 * Request codes.
	 */
	public static final int REGISTER_REQUEST_CODE = 300;
	public static final int IS_REGISTERED_REQUEST_CODE = 301;
	public static final int SENDER_ID_REQUEST_CODE = 303;
	public static final int LICENSE_REQUEST_CODE = 304;
	public static final int UNREGISTER_REQUEST_CODE = 305;
	public static final int NOTIFICATION_REQUEST_CODE = 306;
	public static final int DEVICE_INFO_REQUEST_CODE = 307;
	public static final int GCM_REGISTRATION_ID_SEND_CODE = 308;
	public static final int POLICY_REQUEST_CODE = 309;

	/**
	 * Google API project id registered to use GCM.
	 */

	public static final String SENDER_ID = "";

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = "WSO2EMM";

	/**
	 * Intent used to display a message in the screen.
	 */
	public static final String DISPLAY_MESSAGE_ACTION =
			"com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

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
	public final class Status {
		private Status(){
			throw new AssertionError();
		}
		public static final String SUCCESSFUL = "200";
		public static final String ACCEPT = "202";
		public static final String AUTHENTICATION_FAILED = "400";
		public static final String INTERNAL_SERVER_ERROR = "500";
	}


	/**
	 * Operation IDs
	 */
	public final class Operation {
		private Operation(){
			throw new AssertionError();
		}
		public static final String DEVICE_LOCK = "DEVICE_LOCK";
		public static final String DEVICE_LOCATION = "DEVICE_LOCATION";
		public static final String WIFI = "WIFI";
		public static final String CAMERA = "CAMERA";
		public static final String EMAIL = "EMAIL";
		public static final String DEVICE_MUTE = "DEVICE_MUTE";
		public static final String PASSWORD_POLICY = "PASSWORD_POLICY";
		public static final String DEVICE_INFO = "DEVICE_INFO";
		public static final String ENTERPRISE_WIPE = "ENTERPRISE_WIPE";
		public static final String CLEAR_PASSWORD = "CLEAR_PASSWORD";
		public static final String WIPE_DATA = "WIPE_DATA";
		public static final String APPLICATION_LIST = "APPLICATION_LIST";
		public static final String CHANGE_LOCK_CODE = "CHANGE_LOCK_CODE";
		public static final String INSTALL_APPLICATION = "INSTALL_APPLICATION";
		public static final String UNINSTALL_APPLICATION = "UNINSTALL_APPLICATION";
		public static final String BLACKLIST_APPLICATIONS = "BLACKLIST_APPLICATIONS";
		public static final String ENCRYPT_STORAGE = "ENCRYPT_STORAGE";
		public static final String DEVICE_RING = "DEVICE_RING";
		public static final String PASSCODE_POLICY = "PASSCODE_POLICY";
		public static final String NOTIFICATION = "NOTIFICATION";
		public static final String INSTALL_APPLICATION_BUNDLE = "INSTALL_APPLICATION_BUNDLE";
		public static final String WEBCLIP = "WEBCLIP";
		public static final String INSTALL_GOOGLE_APP = "INSTALL_GOOGLE_APP";
		public static final String POLICY_BUNDLE = "POLICY_BUNDLE";
		public static final String POLICY_MONITOR = "POLICY_MONITOR";
		public static final String POLICY_REVOKE = "POLICY_REVOKE";
		public static final String DISENROLL = "DISENROLL";
	}

	/**
	 *  Device specific constants
	 */
	public final class Device {
		private Device() {
			throw new AssertionError();
		}
		public static final String IMEI = "IMEI";
		public static final String IMSI = "IMSI";
		public static final String MODEL = "DEVICE_MODEL";
		public static final String VENDOR = "VENDOR";
		public static final String OS = "OS_VERSION";
		public static final String NAME = "DEVICE_NAME";
		public static final String BATTERY_LEVEL = "BATTERY_LEVEL";
		public static final String MOBILE_DEVICE_LATITUDE = "LATITUDE";
		public static final String MOBILE_DEVICE_LONGITUDE = "LONGITUDE";
		public static final String MEMORY_INFO_INTERNAL_TOTAL = "INTERNAL_TOTAL_MEMORY";
		public static final String MEMORY_INFO_EXTERNAL_TOTAL = "EXTERNAL_TOTAL_MEMORY";
		public static final String MEMORY_INFO_INTERNAL_AVAILABLE = "INTERNAL_AVAILABLE_MEMORY";
		public static final String MEMORY_INFO_EXTERNAL_AVAILABLE = "EXTERNAL_AVAILABLE_MEMORY";
		public static final String NETWORK_OPERATOR = "OPERATOR";
		public static final String INFO = "DEVICE_INFO";
        public static final String GCM_TOKEN = "GCM_TOKEN";
	}

}
