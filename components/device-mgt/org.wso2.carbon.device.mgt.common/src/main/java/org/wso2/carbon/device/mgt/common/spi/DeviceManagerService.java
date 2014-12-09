/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.device.mgt.common.spi;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;

import java.util.List;

/**
 * This represents the service provider interface that has to be implemented by any of new
 * device type plugin implementation intended to be managed through CDM.
 */
public interface DeviceManagerService {

    /**
     * Method to retrieve the provider type that implements DeviceManagerService interface
     * @return  Returns provider type
     */
    String getProviderType();

    /**
     * Method to enrolling a particular device of type mobile, IoT, etc within CDM.
     *
     * @param device    Metadata corresponding to the device being enrolled
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    boolean enrollDevice(Device device) throws DeviceManagementException;

    /**
     * Method to modify the metadata corresponding to device enrollment
     *
     * @param device    Modified device enrollment related metadata
     * @throws DeviceManagementException If some unusual behaviour is observed while modify the enrollment of a
     *                                   device
     */
    boolean modifyEnrollment(Device device) throws DeviceManagementException;

    /**
     * Method to disenroll a particular device from CDM.
     *
     * @param deviceId  Fully qualified device identifier
     * @throws DeviceManagementException If some unusual behaviour is observed while disenrolling a device
     */
    boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException;

    /**
     * Method to retrieve the status of the registration process of a particular device.
     *
     * @param deviceId  Fully qualified device identifier
     * @return          Status of enrollment
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    boolean isRegistered(DeviceIdentifier deviceId) throws DeviceManagementException;

    /**
     * Method to retrieve the status of a particular device.
     *
     * @param deviceId  Fully qualified device identifier
     * @return          Returns if the device is active
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException;

    /**
     * Method to set the status indicating whether a particular device registered within CDM is enabled at a given
     * moment.
     *
     * @param deviceId  Fully qualified device identifier
     * @param status    Indicates whether the device is active
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    boolean setActive(DeviceIdentifier deviceId, boolean status) throws DeviceManagementException;

    /**
     * Method to retrieve metadata of all devices registered within CDM corresponding to a particular device type.
     *
     * @param type  Device Type
     * @return      List of metadata corresponding to all devices registered within CDM
     */
    List<Device> getAllDevices(String type) throws DeviceManagementException;

    /**
     * Method to retrieve metadata of a device corresponding to a particular type that carries a specific identifier.
     *
     * @param deviceId  Fully qualified device identifier
     * @return          Metadata corresponding to a particular device
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException;

    /**
     * Method to update device information.
     * @param device   Updated device information related data
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    boolean updateDeviceInfo(Device device) throws DeviceManagementException;

    /**
     * Method to set the ownership type of a particular device. i.e. BYOD, COPE
     *
     * @param deviceId          Fully qualified device identifier
     * @param ownershipType     Type of ownership
     * @throws DeviceManagementException If some unusual behaviour is observed while enrolling a device
     */
    boolean setOwnership(DeviceIdentifier deviceId, String ownershipType) throws DeviceManagementException;

}
