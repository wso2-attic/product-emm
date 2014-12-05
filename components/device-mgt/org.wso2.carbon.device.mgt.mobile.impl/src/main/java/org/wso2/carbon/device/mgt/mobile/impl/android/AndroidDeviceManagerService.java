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

package org.wso2.carbon.device.mgt.mobile.impl.android;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;

import java.util.List;

/**
 * This represents the Android implementation of DeviceManagerService. *
 */
public class AndroidDeviceManagerService implements DeviceManagerService {

    private static final String DEVICE_MANAGER_ANDROID = "android";

    @Override
    public String getProviderType() {
        return DEVICE_MANAGER_ANDROID;
    }

    @Override
	public void enrollDevice(Device device) throws DeviceManagementException {

	}

	@Override
	public void modifyEnrollment(Device device) throws DeviceManagementException {

	}

    @Override
    public void disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {

    }

    @Override
    public boolean isRegistered(DeviceIdentifier deviceId) throws DeviceManagementException {
        return false;
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
        return false;
    }

    @Override
    public void setActive(boolean status) throws DeviceManagementException {

    }

    @Override
    public List<Device> getAllDevices(String type) throws DeviceManagementException {
        return null;
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        return null;
    }

    @Override
    public void setOwnership(DeviceIdentifier deviceId, String ownershipType) throws DeviceManagementException {

    }

	@Override
	public void updateDeviceInfo(Device device) throws DeviceManagementException{

	}


}
