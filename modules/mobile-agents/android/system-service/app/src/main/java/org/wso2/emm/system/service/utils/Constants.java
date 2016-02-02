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
	public static final String AGENT_APP_LAUNCH_ACTIVITY = "ServerDetails";
	public static final String AGENT_APP_ALERT_ACTIVITY = "AlertActivity";
	public static final String OTA_CONFIG_LOCATION = "/data/system/ota.conf";
	public static final String DEFAULT_OTA_SERVER_ADDRESS = "emm.wso2.com";
	public static final String DEFAULT_OTA_SERVER_PROTOCOL = "http";
	public static final int DEFAULT_OTA_SERVER_PORT = 80;
	public static final String DEFAULT_OTA_BUILD_PROP_FILE = "build.prop";
	public static final String DEFAULT_OTA_ZIP_FILE = ".ota.zip";
	public static final String DEFAULT_UPDATE_PACKAGE_LOACTION = "/cache/update.zip";

	/**
	 * Operation IDs
	 */
	public final class Operation {
		private Operation(){
			throw new AssertionError();
		}
		public static final String UPGRADE_FIRMWARE = "UPGRADE_FIRMWARE";
		public static final String REBOOT = "REBOOT";
		public static final String EXECUTE_SHELL_COMMAND = "SHELL_COMMAND";
		public static final String SILENT_INSTALL_APPLICATION = "SILENT_INSTALL_APPLICATION";
		public static final String SILENT_UNINSTALL_APPLICATION = "SILENT_UNINSTALL_APPLICATION";
	}

}
