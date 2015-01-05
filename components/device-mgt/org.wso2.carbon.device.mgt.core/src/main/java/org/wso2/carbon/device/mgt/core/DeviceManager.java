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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.core;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.OperationManager;

import java.util.List;

/**
 * Proxy class for all Device Management related operations that take the corresponding plugin type in
 * and resolve the appropriate plugin implementation
 */
public interface DeviceManager {

    public boolean enrollDevice(Device device) throws DeviceManagementException;

    public boolean modifyEnrollment(Device device) throws DeviceManagementException;

    public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException;

    public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException;

    public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException;

    public boolean setActive(DeviceIdentifier deviceId, boolean status) throws DeviceManagementException;

    public List<Device> getAllDevices(String type) throws DeviceManagementException;

    public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException;

    public boolean updateDeviceInfo(Device device) throws DeviceManagementException;

    public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType) throws DeviceManagementException;

    public OperationManager getOperationManager(String type) throws DeviceManagementException;

}
