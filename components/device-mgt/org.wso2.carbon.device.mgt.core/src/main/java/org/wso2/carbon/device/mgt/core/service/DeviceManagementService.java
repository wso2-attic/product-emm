/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.core.service;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.OperationManager;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.core.DeviceManager;
import org.wso2.carbon.device.mgt.core.internal.DeviceManagementDataHolder;

import java.util.List;

public class DeviceManagementService implements DeviceManager {

	@Override
	public boolean enrollDevice(Device device) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().enrollDevice(device);
	}

	@Override
	public boolean modifyEnrollment(Device device) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().modifyEnrollment(device);
	}

	@Override
	public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager()
		                                 .disenrollDevice(deviceId);
	}

	@Override
	public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().isEnrolled(deviceId);
	}

	@Override
	public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().isActive(deviceId);
	}

	@Override
	public boolean setActive(DeviceIdentifier deviceId, boolean status)
			throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager()
		                                 .setActive(deviceId, status);
	}

	@Override
	public List<Device> getAllDevices(String type) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().getAllDevices(type);
	}

	@Override
	public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().getDevice(deviceId);
	}

	@Override
	public boolean updateDeviceInfo(Device device) throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager().updateDeviceInfo(device);
	}

	@Override
	public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
			throws DeviceManagementException {
		return DeviceManagementDataHolder.getInstance().getDeviceManager()
		                                 .setOwnership(deviceId, ownershipType);
	}

    @Override
    public OperationManager getOperationManager(String type) throws DeviceManagementException {
        return DeviceManagementDataHolder.getInstance().getDeviceManager().
                getOperationManager(type);
    }

}
