/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cdm.api.android.util;

import com.google.gson.JsonObject;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;

/**
 *  AndroidAPIUtil class provides utility function used by Android REST-API classes.
 */
public class AndroidAPIUtil {

	public static Device convertToDeviceDTO(JsonObject json){
		Device device = new Device();
		return device;
	}

	public static Device convertToDeviceDTO(String deviceId){
		Device device = new Device();
		DeviceType type = new DeviceType();
		device.setId(deviceId);
		type.setName(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		device.setDeviceType(type);
		return device;
	}
}
