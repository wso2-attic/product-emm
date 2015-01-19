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

package cdm.api.windows.util;


import com.google.gson.JsonObject;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;


/**
 *  WindowsAPIUtil class provides utility function used by Android REST-API classes.
 */
public class WindowsAPIUtil {

	public static Device convertToDeviceObject(JsonObject json){
		Device device = new Device();
		device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		device.setName("Test Device");
		device.setOwner("harshan");
		return device;
	}

	public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId){
		DeviceIdentifier identifier = new DeviceIdentifier();
		identifier.setId(deviceId);
		identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		return identifier;
	}
}
