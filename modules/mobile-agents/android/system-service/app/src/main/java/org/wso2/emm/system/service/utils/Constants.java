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

package org.wso2.emm.system.service.utils;

/**
 * This class holds all the constants used throughout the application.
 */
public class Constants {

	public static final String AGENT_APP_PACKAGE_NAME = "org.wso2.emm.agent";
	public static final String PACKAGE_NAME = "org.wso2.emm.system.service";
	public static final String AGENT_APP_LAUNCH_ACTIVITY = "ServerDetails";
	public static final String DEFAULT_OTA_SERVER_ADDRESS = "emm.wso2.com";
	public static final String DEFAULT_OTA_SERVER_PROTOCOL = "http";
	public static final String DEFAULT_OTA_SERVER_SUB_DIRECTORY = null;
	public static final int DEFAULT_OTA_SERVER_PORT = 80;
	public static final int REQUIRED_BATTERY_LEVEL_TO_FIRMWARE_UPGRADE = 50;
	public static final String DEFAULT_OTA_BUILD_PROP_FILE = "build.prop";
	public static final String DEFAULT_OTA_ZIP_FILE = ".ota.zip";
	public static final String UPDATE_PACKAGE_NAME = "update.zip";
	public static final String STATUS = "status";
	public static final String PAYLOAD = "payload";
	public static final String CODE = "code";
	public static final String OPERATION = "operation";
	public static final String SYSTEM_APP_ACTION_RESPONSE = "org.wso2.emm.system.service.MESSAGE_PROCESSED";
	public static final String AGENT_APP_SERVICE_NAME = "org.wso2.emm.agent.START_SERVICE";
	public static final boolean DEBUG_MODE_ENABLED = false;
	public static final String ADMIN_MESSAGE = "message";
	public static final String IS_LOCKED = "lock";
	public static final int FIRMWARE_UPGRADE_CONNECTIVITY_TIMEOUT = 10000;
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	public static final String FIRMWARE_INSTALL_CONFIRM_ACTION = "FIRMWARE_INSTALL_CONFIRM_ACTION";
	public static final String FIRMWARE_INSTALL_CANCEL_ACTION = "FIRMWARE_INSTALL_CANCEL_ACTION";
	public static final boolean SILENT_FIRMWARE_INSTALLATION = true;
	/**
	 * Read_TIMEOUT = SO timeout(the time since the last byte has been received)
	 */
	public static final int FIRMWARE_UPGRADE_READ_TIMEOUT = 60000;
	public static final int DEFAULT_NOTIFICATION_CODE = 100;

	/**
	 * Operation IDs
	 */
	public final class Operation {
		private Operation(){
			throw new AssertionError();
		}
		public static final String APP_RESTRICTION = "APP-RESTRICTION";
		public static final String UPGRADE_FIRMWARE = "UPGRADE_FIRMWARE";
		public static final String REBOOT = "REBOOT";
		public static final String DEVICE_LOCK = "DEVICE_LOCK";
		public static final String DEVICE_UNLOCK = "DEVICE_UNLOCK";
		public static final String EXECUTE_SHELL_COMMAND = "SHELL_COMMAND";
		public static final String SILENT_INSTALL_APPLICATION = "SILENT_INSTALL_APPLICATION";
		public static final String SILENT_UNINSTALL_APPLICATION = "SILENT_UNINSTALL_APPLICATION";
		public static final String SILENT_UPDATE_APPLICATION = "SILENT_UPDATE_APPLICATION";
		public static final String REMOVE_DEVICE_OWNER = "REMOVE_DEVICE_OWNER";
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
		public static final String GET_FIRMWARE_UPGRADE_PACKAGE_STATUS = "FIRMWARE_UPGRADE_PACKAGE_STATUS";
		public static final String GET_FIRMWARE_UPGRADE_DOWNLOAD_PROGRESS = "FIRMWARE_UPGRADE_DOWNLOAD_PROGRESS";
		public static final String WIPE_DATA = "WIPE_DATA";
		public static final String FAILED_FIRMWARE_UPGRADE_NOTIFICATION = "FAILED_FIRMWARE_UPGRADE_NOTIFICATION";
		public static final String FIRMWARE_UPGRADE_FAILURE = "FIRMWARE_UPGRADE_FAILURE";
		public static final String GET_FIRMWARE_BUILD_DATE = "FIRMWARE_BUILD_DATE";
		public static final String FIRMWARE_INSTALLATION_CANCELED = "FIRMWARE_INSTALLATION_CANCELED";
		public static final String FIRMWARE_UPGRADE_AUTOMATIC_RETRY = "FIRMWARE_UPGRADE_AUTOMATIC_RETRY";
		public static final String LOGCAT = "LOGCAT";
	}

	/**
	 * Sub Status codes
	 */
	public final class Status {
		private Status(){
			throw new AssertionError();
		}

		public static final String SUCCESSFUL = "2000";
		public static final String OTA_UPGRADE_ONGOING = "2001";
		public static final String REQUEST_PLACED = "2002";
		public static final String USER_CANCELED = "3000";
		public static final String NO_HISTORY = "3001";
		public static final String MALFORMED_REQUEST = "4000";
		public static final String BATTERY_LEVEL_INSUFFICIENT_TO_DOWNLOAD = "4001";
		public static final String WIFI_OFF = "4002";
		public static final String NETWORK_UNREACHABLE = "4003";
		public static final String NO_UPGRADE_FOUND = "4004";
		public static final String UPDATE_INFO_NOT_READABLE = "4005";
		public static final String OTA_DOWNLOAD_FAILED = "4006";
		public static final String OTA_IMAGE_INSTALL_FAILED = "4007";
		public static final String LOW_DISK_SPACE = "4008";
		public static final String MALFORMED_OTA_URL = "4009";
		public static final String OTA_UPGRADE_PENDING = "4010";
		public static final String BATTERY_LEVEL_INSUFFICIENT_TO_INSTALL = "4011";
		public static final String OTA_IMAGE_VERIFICATION_FAILED = "4012";
		public static final String CONNECTION_FAILED = "4013";
		public static final String FILE_NOT_FOUND = "4014";
		public static final String FAILED = "5000";
		public static final String INTERNAL_ERROR = "5001";
	}

	/**
	 * Status codes
	 */
	public final class Code {
		private Code(){
			throw new AssertionError();
		}
		public static final String SUCCESS = "200";
		public static final String PENDING = "300";
		public static final String FAILURE = "400";
	}

}
