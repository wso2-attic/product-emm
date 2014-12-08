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
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.core.internal.DeviceManagementDataHolder;

import java.util.List;

public class DeviceManagementService implements DeviceManagerService {

    @Override
    public String getProviderType() {
        return null;  
    }

    @Override
    public void enrollDevice(Device device) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(device.getType());
        dms.enrollDevice(device);
    }

    @Override
    public void modifyEnrollment(Device device) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(device.getType());
        dms.modifyEnrollment(device);
    }

    @Override
    public void disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(deviceId.getType());
        dms.disenrollDevice(deviceId);
    }

    @Override
    public boolean isRegistered(DeviceIdentifier deviceId) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(deviceId.getType());
        return dms.isRegistered(deviceId);
    }

    @Override
    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(deviceId.getType());
        return dms.isActive(deviceId);
    }

    @Override
    public void setActive(DeviceIdentifier deviceId, boolean status) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(deviceId.getType());
        dms.setActive(deviceId, status);
    }

    @Override
    public List<Device> getAllDevices(String type) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(type);
        return dms.getAllDevices(type);
    }

    @Override
    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(deviceId.getType());
        return dms.getDevice(deviceId);
    }

    @Override
    public void updateDeviceInfo(Device device) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(device.getType());
        dms.updateDeviceInfo(device);
    }

    @Override
    public void setOwnership(DeviceIdentifier deviceId, String ownershipType) throws DeviceManagementException {
        DeviceManagerService dms =
                DeviceManagementDataHolder.getInstance().getDeviceManagementRepository().
                        getDeviceManagementProvider(deviceId.getType());
        dms.setOwnership(deviceId, ownershipType);
    }

}
