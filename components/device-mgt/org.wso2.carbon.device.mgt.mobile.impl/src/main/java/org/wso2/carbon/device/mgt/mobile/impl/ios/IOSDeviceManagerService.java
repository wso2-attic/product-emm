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

package org.wso2.carbon.device.mgt.mobile.impl.ios;

import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;

import java.util.List;

/**
 * This represents the iOS implementation of DeviceManagerService.
 */
public class IOSDeviceManagerService implements DeviceManagerService {

    @Override
    public String getProviderType() {
        return DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_IOS;
    }

    @Override
    public boolean enrollDevice(Device device) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean modifyEnrollment(Device device) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
        return true;
    }

    @Override
    public boolean setActive(DeviceIdentifier deviceId, boolean status)
            throws DeviceManagementException {
        return true;
    }

    @Override
    public List<Device> getAllDevices() throws DeviceManagementException {
        return null;
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        return null;
    }

    @Override
    public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
            throws DeviceManagementException {
        return true;
    }

    @Override
    public OperationManager getOperationManager() throws DeviceManagementException {
        return null;
    }

    @Override
    public boolean updateDeviceInfo(Device device) throws DeviceManagementException {
        return true;
    }

}
