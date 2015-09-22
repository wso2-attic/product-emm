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
package org.wso2.emm.agent.api;

import android.util.Log;
import org.wso2.emm.agent.utils.Constants;
import java.io.File;

/**
 * This class represents all the APIs to check if the device is Rooted or not.
 */
public class Root {
	private static final String[] SU_CHECK_COMMAND = new String[] { "/system/xbin/which", "su" };
	private static final String SU_TAG = "test-keys";
	private static final String SU_APK = "/system/app/Superuser.apk";
	private static final String TAG = Root.class.getSimpleName();

	/**
	 * Returns true if the device is rooted (if any of the root methods returns
	 * true).
	 * @return - Device rooted status.
	 */
	public boolean isDeviceRooted() {
		return checkRootBySuAccess() || checkRootBySuperUserApk() || checkRootByBuildTags();
	}

	/**
	 * Returns true if the OS build tags contains "test-keys".
	 * @return - Device root status by build tags.
	 */
	public boolean checkRootByBuildTags() {

		String buildTags = android.os.Build.TAGS;
		if (buildTags != null && buildTags.contains(SU_TAG)) {
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Build tags are found in the device");
			}
			return true;
		}
		return false;
	}

	/**
	 * Returns true if the device contains SuperUser.apk which is stored into
	 * the device in the rooting process.
	 * @return - Device root status by SU APK.
	 */
	public boolean checkRootBySuperUserApk() {
		File suApk = new File(SU_APK);
		if (suApk.exists()) {
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Super apk is found in the device");
			}
			return true;
		}
		return false;
	}

	/**
	 * Executes a shell command (superuser access with su binary) and returns
	 * true if the command succeeds.
	 * @return - Device root status by shell access.
	 */
	public boolean checkRootBySuAccess() {
		if (new ShellExecutor().executeCommand(SU_CHECK_COMMAND) != null) {
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "su command is enabled");
			}
			return true;
		} else {
			return false;
		}
	}
}
