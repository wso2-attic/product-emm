/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.cdmserver.mobileservices.android.util;

/**
 * Defines constants used in Android-REST API bundle.
 */
public final class AndroidConstants {

	public final class DeviceProperties{
		private DeviceProperties() {
			throw new AssertionError();
		}
		public static final String PROPERTY_USER_KEY = "username";
		public static final String PROPERTY_DEVICE_KEY = "device";
	}

	public final class DeviceFeatures{
		private DeviceFeatures() {
			throw new AssertionError();
		}
	}

	public final class DeviceConstants{
		private DeviceConstants() {
			throw new AssertionError();
		}
		public static final String DEVICE_MAC_KEY = "mac";
		public static final String DEVICE_DESCRIPTION_KEY = "description";
		public static final String DEVICE_OWNERSHIP_KEY = "ownership";
		public static final String DEVICE_PROPERTIES_KEY = "properties";
		public static final String DEVICE_FEATURES_KEY = "features";
	}

	public final class Messages{
		private Messages(){
			throw new AssertionError();
		}
		public static final String DEVICE_MANAGER_SERVICE_NOT_AVAILABLE =
				"Device Manager service not available";
	}
}
