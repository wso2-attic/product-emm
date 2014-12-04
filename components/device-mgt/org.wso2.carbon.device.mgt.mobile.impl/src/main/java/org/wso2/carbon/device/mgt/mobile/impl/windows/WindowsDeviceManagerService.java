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

package org.wso2.carbon.device.mgt.mobile.impl.windows;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;

import java.util.List;

/**
 * This represents the Windows implementation of DeviceManagerService.
 */
public class WindowsDeviceManagerService implements DeviceManagerService{
	@Override
	public void enrollDevice(Device device) throws DeviceManagementException {

	}

	@Override
	public void modifyEnrollment(Device device) throws DeviceManagementException {

	}

	@Override
	public void disEnrollDevice(String type, String deviceId)
			throws DeviceManagementException {

	}

	@Override
	public boolean isRegistered(String type, String deviceId)
			throws DeviceManagementException {
		return false;
	}

	@Override
	public boolean isActive(String type, String deviceId)
			throws DeviceManagementException {
		return false;
	}

	@Override
	public void setActive(boolean status) throws DeviceManagementException {

	}

	@Override
	public List<Device> getAllDeviceInfo(String type) throws DeviceManagementException {
		return null;
	}

	@Override
	public Device getDeviceInfo(String type, String deviceId)
			throws DeviceManagementException {
		return null;
	}

	@Override
	public void setOwnership(String ownershipType) throws DeviceManagementException {

	}
}
