/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.mdm.api.util;

/**
 * This class holds all the constants used for IOS and Android.
 */
public class MDMAppConstants {

	public static final String USERS = "users";
	public static final String ROLES = "roles";

	public class IOSConstants {

		private IOSConstants() {
			throw new AssertionError();
		}
		public static final String IS_REMOVE_APP = "isRemoveApp";
		public static final String IS_PREVENT_BACKUP = "isPreventBackup";
		public static final String I_TUNES_ID = "iTunesId";
		public static final String LABEL = "label";
		public static final String OPCODE_INSTALL_ENTERPRISE_APPLICATION = "INSTALL_ENTERPRISE_APPLICATION";
		public static final String OPCODE_INSTALL_STORE_APPLICATION = "INSTALL_STORE_APPLICATION";
		public static final String OPCODE_INSTALL_WEB_APPLICATION = "WEB_CLIP";
		public static final String OPCODE_REMOVE_APPLICATION = "REMOVE_APPLICATION";
	}

	public class AndroidConstants {

		private AndroidConstants() {
			throw new AssertionError();
		}
		public static final String OPCODE_INSTALL_APPLICATION = "INSTALL_APPLICATION";
		public static final String OPCODE_UNINSTALL_APPLICATION = "UNINSTALL_APPLICATION";
	}

	public class RegistryConstants {

		private RegistryConstants() {
			throw new AssertionError();
		}
		public static final String GENERAL_CONFIG_RESOURCE_PATH = "general";
	}
}
