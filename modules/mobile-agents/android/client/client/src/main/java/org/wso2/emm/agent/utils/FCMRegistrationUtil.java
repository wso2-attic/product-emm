/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * This provides methods necessary to register a device with Google cloud
 * messaging or check if it has been already registered.
 */
public class FCMRegistrationUtil {
	private static final String TAG = FCMRegistrationUtil.class.getSimpleName();

	/**
	 * Check the device to see if it has Google play services installed. If not
	 * prompt user to install.
	 *
	 * @return if Google play services are installed return true, otherwise false.
	 */
	public static boolean isPlayServicesInstalled(Context context) {
		GoogleApiAvailability api = GoogleApiAvailability.getInstance();
		int resultCode = api.isGooglePlayServicesAvailable(context);
		if (resultCode != ConnectionResult.SUCCESS) {
			Log.e(TAG, "GCM registration failed, Google play services not available.");
			return false;
		}
		return true;
	}

}