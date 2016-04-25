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
	public static final boolean SYSTEM_APP_ENABLED = false;
	public static final String SYSTEM_APP_SERVICE_NAME = "org.wso2.emm.system.service.START_SERVICE";
	public static final String GCM_PROJECT_NUMBER = "GOOGLE-API-PROJECT-NUMBER";
	public static final String GCM_REG_ID = "gcm_reg_id";
	public static final String REG_ID = "regId";
	public static final String NOTIFIER_LOCAL = "LOCAL";
	public static final String NOTIFIER_GCM = "GCM";

	public static final String SERVER_PORT = "80";
	public static final String SERVER_PROTOCOL = "http://";
	public static final String API_VERSION = "1.0.0/";
	public static final String API_SERVER_PORT = "80";
	public static final String OWNERSHIP_BYOD = "BYOD";
	public static final String OWNERSHIP_COPE = "COPE";
	// Set DEFAULT_OWNERSHIP to null if no overiding is needed. Other possible values are,
	// OWNERSHIP_BYOD or OWNERSHIP_COPE. If you are using the mutual SSL authentication
	// This value must be set to a value other than null.
	public static final String DEFAULT_OWNERSHIP = null;
	// This is set to override the server host name retrieving screen. If overriding is not
	// needed, set this to null.
	public static final String DEFAULT_HOST = null;


	// This is used to skip the license
	public static final boolean SKIP_LICENSE = false;
	public static final boolean HIDE_LOGIN_UI = false;

	// TODO: Add API_VERSION to server endpoint
	public static final String SERVER_APP_ENDPOINT = "/mdm-android-agent/";
	public static final String SERVER_AUTHENTICATION_ENDPOINT = "register/authenticate/device";
	public static final String LICENSE_ENDPOINT = SERVER_APP_ENDPOINT + "device/license";
	public static final String REGISTER_ENDPOINT = SERVER_APP_ENDPOINT + "enrollment/";
	public static final String CONFIGURATION_ENDPOINT = SERVER_APP_ENDPOINT + "configuration/";

	public static final String OAUTH_ENDPOINT = "/oauth2/token";
	public static final String DEVICE_ENDPOINT = SERVER_APP_ENDPOINT + "device/";
	public static final String IS_REGISTERED_ENDPOINT = REGISTER_ENDPOINT;
	public static final String UNREGISTER_ENDPOINT =  REGISTER_ENDPOINT;
	public static final String NOTIFICATION_ENDPOINT = SERVER_APP_ENDPOINT + "operation/";
	public static final String GOOGLE_PLAY_APP_URI = "market://details?id=";
	public static final String DYNAMIC_CLIENT_REGISTER_ENDPOINT = "/dynamic-client-web/register";
	public static final String POLICY_ENDPOINT = SERVER_APP_ENDPOINT + "policy/";
	public static final String EVENT_ENDPOINT = SERVER_APP_ENDPOINT + "events/";

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
	public static final String DEVICE_TYPE = "deviceType";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String CLIENT_NAME = "client_name";
	public static final String GRANT_TYPE = "password refresh_token";
	public static final String TOKEN_SCOPE = "production";
	public static final String APPLICATION_TYPE = "device";
	public static final String USER_AGENT = "Mozilla/5.0 ( compatible ), Android";
	public static final String PACKAGE_NAME = "org.wso2.emm.agent";
	public static final String ADMIN_MESSAGE = "message";
	public static final String IS_LOCKED = "lock";
	public static final String LOCK_MESSAGE = "lockMessage";
	public static final String OPERATION_ID = "operationId";
	public static final boolean HIDE_UNREGISTER_BUTTON = false;
	public static final String IS_HARD_LOCK_ENABLED = "isHardLockEnabled";

	public static final String SERVICE_PACKAGE_NAME = "org.wso2.emm.system.service";

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
	public static final int CONFIGURATION_REQUEST_CODE = 310;
	public static final int AUTHENTICATION_REQUEST_CODE = 311;
	public static final int EVENT_REQUEST_CODE = 312;

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
		public static final String CREATED = "201";
		public static final String ACCEPT = "202";
		public static final String AUTHENTICATION_FAILED = "400";
		public static final String INTERNAL_SERVER_ERROR = "500";
	}

	public static final String MIME_TYPE = "text/html";
	public static final String ENCODING_METHOD = "utf-8";
	public static final int DEFAILT_REPEAT_COUNT = 0;
	public static int DEFAULT_INTERVAL = 30000;
	public static final int NOTIFIER_CHECK = 2;


	/**
	 * Operation IDs
	 */
	public final class Operation {
		private Operation(){
			throw new AssertionError();
		}
		public static final String DEVICE_LOCK = "DEVICE_LOCK";
		public static final String DEVICE_UNLOCK = "DEVICE_UNLOCK";
		public static final String DEVICE_LOCATION = "DEVICE_LOCATION";
		public static final String WIFI = "WIFI";
		public static final String CAMERA = "CAMERA";
		public static final String EMAIL = "EMAIL";
		public static final String DEVICE_MUTE = "DEVICE_MUTE";
		public static final String PASSWORD_POLICY = "PASSCODE_POLICY";
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
		public static final String POLICY_MONITOR = "MONITOR";
		public static final String POLICY_REVOKE = "POLICY_REVOKE";
		public static final String DISENROLL = "DISENROLL";
		public static final String UPGRADE_FIRMWARE = "UPGRADE_FIRMWARE";
		public static final String REBOOT = "REBOOT";
		public static final String VPN = "VPN";
		public static final String EXECUTE_SHELL_COMMAND = "SHELL_COMMAND";
		public static final String DISALLOW_ADJUST_VOLUME = "DISALLOW_ADJUST_VOLUME";
		public static final String DISALLOW_CONFIG_BLUETOOTH = "DISALLOW_CONFIG_BLUETOOTH";
		public static final String DISALLOW_CONFIG_CELL_BROADCASTS =
				"DISALLOW_CONFIG_CELL_BROADCASTS";
		public static final String DISALLOW_CONFIG_CREDENTIALS = "DISALLOW_CONFIG_CREDENTIALS";
		public static final String DISALLOW_CONFIG_MOBILE_NETWORKS =
				"DISALLOW_CONFIG_MOBILE_NETWORKS";
		public static final String DISALLOW_CONFIG_TETHERING = "DISALLOW_CONFIG_TETHERING";
		public static final String DISALLOW_CONFIG_VPN = "DISALLOW_CONFIG_VPN";
		public static final String DISALLOW_CONFIG_WIFI = "DISALLOW_CONFIG_WIFI";
		public static final String DISALLOW_APPS_CONTROL = "DISALLOW_APPS_CONTROL";
		public static final String DISALLOW_CREATE_WINDOWS = "DISALLOW_CREATE_WINDOWS";
		public static final String DISALLOW_CROSS_PROFILE_COPY_PASTE
				= "DISALLOW_CROSS_PROFILE_COPY_PASTE";
		public static final String DISALLOW_DEBUGGING_FEATURES = "DISALLOW_DEBUGGING_FEATURES";
		public static final String DISALLOW_FACTORY_RESET = "DISALLOW_FACTORY_RESET";
		public static final String DISALLOW_ADD_USER = "DISALLOW_ADD_USER";
		public static final String DISALLOW_INSTALL_APPS = "DISALLOW_INSTALL_APPS";
		public static final String DISALLOW_INSTALL_UNKNOWN_SOURCES
				= "DISALLOW_INSTALL_UNKNOWN_SOURCES";
		public static final String DISALLOW_MODIFY_ACCOUNTS = "DISALLOW_MODIFY_ACCOUNTS";
		public static final String DISALLOW_MOUNT_PHYSICAL_MEDIA = "DISALLOW_MOUNT_PHYSICAL_MEDIA";
		public static final String DISALLOW_NETWORK_RESET = "DISALLOW_NETWORK_RESET";
		public static final String DISALLOW_OUTGOING_BEAM = "DISALLOW_OUTGOING_BEAM";
		public static final String DISALLOW_OUTGOING_CALLS = "DISALLOW_OUTGOING_CALLS";
		public static final String DISALLOW_REMOVE_USER = "DISALLOW_REMOVE_USER";
		public static final String DISALLOW_SAFE_BOOT = "DISALLOW_SAFE_BOOT";
		public static final String DISALLOW_SHARE_LOCATION = "DISALLOW_SHARE_LOCATION";
		public static final String DISALLOW_SMS = "DISALLOW_SMS";
		public static final String DISALLOW_UNINSTALL_APPS = "DISALLOW_UNINSTALL_APPS";
		public static final String DISALLOW_UNMUTE_MICROPHONE = "DISALLOW_UNMUTE_MICROPHONE";
		public static final String DISALLOW_USB_FILE_TRANSFER = "DISALLOW_USB_FILE_TRANSFER";
		public static final String ALLOW_PARENT_PROFILE_APP_LINKING
				= "ALLOW_PARENT_PROFILE_APP_LINKING";
		public static final String ENSURE_VERIFY_APPS = "ENSURE_VERIFY_APPS";
		public static final String AUTO_TIME = "AUTO_TIME";
		public static final String ENABLE_ADMIN = "ENABLE_ADMIN";
		public static final String SET_SCREEN_CAPTURE_DISABLED = "SET_SCREEN_CAPTURE_DISABLED";
		public static final String SET_STATUS_BAR_DISABLED = "SET_STATUS_BAR_DISABLED";
		public static final String SILENT_INSTALL_APPLICATION = "SILENT_INSTALL_APPLICATION";
		public static final String SILENT_UNINSTALL_APPLICATION = "SILENT_UNINSTALL_APPLICATION";
		public static final String WORK_PROFILE= "WORK_PROFILE";
	}

	/**
	 *  Device specific constants
	 */
	public final class Device {
		private Device() {
			throw new AssertionError();
		}
		public static final String SERIAL = "SERIAL";
		public static final String IMEI = "IMEI";
		public static final String MAC = "MAC";
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
		public static final String WIFI_SSID = "WIFI_SSID";
		public static final String WIFI_SIGNAL_STRENGTH = "WIFI_SIGNAL_STRENGTH";
		public static final String NETWORK_INFO = "NETWORK_INFO";
		public static final String CONNECTION_TYPE = "CONNECTION_TYPE";
		public static final String MOBILE_CONNECTION_TYPE = "MOBILE_CONNECTION_TYPE";
		public static final String MOBILE_SIGNAL_STRENGTH = "MOBILE_SIGNAL_STRENGTH";
		public static final String CPU_INFO = "CPU_INFO";
		public static final String RAM_INFO = "RAM_INFO";
		public static final String TOTAL_MEMORY = "TOTAL_MEMORY";
		public static final String LOW_MEMORY = "LOW_MEMORY";
		public static final String THRESHOLD = "THRESHOLD";
		public static final String AVAILABLE_MEMORY = "AVAILABLE_MEMORY";
		public static final String BATTERY_INFO = "BATTERY_INFO";
		public static final String SCALE = "SCALE";
		public static final String BATTERY_VOLTAGE = "BATTERY_VOLTAGE";
		public static final String TEMPERATURE = "TEMPERATURE";
		public static final String CURRENT_AVERAGE = "CURRENT_AVERAGE";
		public static final String TECHNOLOGY = "TECHNOLOGY";
		public static final String HEALTH = "HEALTH";
		public static final String STATUS = "STATUS";
		public static final String PLUGGED = "PLUGGED";
		public static final String APP_MEMORY_INFO = "APP_MEMORY_INFO";
		public static final String USS = "USS";
		public static final String PSS = "PSS";
		public static final String PACKAGE = "PACKAGE";
		public static final String PID = "PID";
		public static final String SHARED_DIRTY = "SHARED_DIRTY";
	}

	// sqlite database related tables
	public final class NotificationTable {
		private NotificationTable() {
			throw new AssertionError();
		}
		public static final String NAME = "notification";
		public static final String ID = "id";
		public static final String MESSAGE = "message";
		public static final String RECEIVED_TIME = "received_time";
		public static final String RESPONSE_TIME = "response_time";
		public static final String STATUS = "status";
	}

	public final class Location {
		private Location() {
			throw new AssertionError();
		}
		public static final String GEO_ENDPOINT = "http://nominatim.openstreetmap.org/reverse";
		public static final String RESULT_FORMAT = "format=json";
		public static final String LONGITUDE = "lon";
		public static final String LATITUDE = "lat";
		public static final String ACCEPT_LANGUAGE = "accept-language";
		public static final String LANGUAGE_CODE = "en-us";
		public static final String ADDRESS = "address";
		public static final String CITY = "city";
		public static final String TOWN = "town";
		public static final String COUNTRY = "country";
		public static final String ZIP = "postcode";
		public static final String STREET1 = "road";
		public static final String STREET2 = "suburb";
		public static final String STATE = "state";
	}

	public final class LocationInfo {
		private LocationInfo() {
			throw new AssertionError();
		}
		public static final String CITY = "city";
		public static final String COUNTRY = "country";
		public static final String ZIP = "zip";
		public static final String STREET1 = "street1";
		public static final String STREET2 = "street2";
		public static final String STATE = "state";
		public static final String LONGITUDE = "longitude";
		public static final String LATITUDE = "latitude";
	}

	public final class EventListners {
		private EventListners(){
			throw new AssertionError();
		}
		public static final boolean EVENT_LISTENING_ENABLED = false;
		public static final boolean APPLICATION_STATE_LISTENER = true;
		public static final String APPLICATION_STATE = "APPLICATION_STATE";
		public static final boolean RUNTIME_STATE_LISTENER = false;
		public static final String RUNTIME_STATE = "RUNTIME_STATE";
		public static final long DEFAULT_START_TIME = 1000;
		public static final long DEFAULT_INTERVAL = 5000;
		public static final int DEFAULT_LISTENER_CODE = 10001;
		public static final String REQUEST_CODE = "requestCode";
	}

	public final class PreferenceFlag {
		private PreferenceFlag() {
			throw new AssertionError();
		}
		public static final String REG_ID = "regId";
		public static final String REGISTERED = "registered";
		public static final String IP = "ip";
		public static final String DEVICE_ACTIVE = "deviceActive";
		public static final String PORT = "serverPort";
		public static final String PROTOCOL = "serverProtocol";
		public static final String APPLIED_POLICY = "appliedPolicy";
		public static final String IS_AGREED = "isAgreed";
		public static final String NOTIFIER_TYPE = "notifierType";
	}

}
