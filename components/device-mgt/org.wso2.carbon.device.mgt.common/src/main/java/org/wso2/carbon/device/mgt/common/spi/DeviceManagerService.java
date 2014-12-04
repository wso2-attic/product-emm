/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.device.mgt.common.spi;

import org.wso2.carbon.device.mgt.common.Device;

import java.util.List;

/**
 * This represents the service provider interface that has to be implemented by any of new
 * device type plugin implementation intended to be managed through CDM.
 */
public interface DeviceManagerService {

    /*
        Method corresponding to enrolling a particular device of type mobile, IoT, etc within CDM
     */
    void enrolDevice(Device device);

    void modifyEnrolment(Device device);

    void disEnrollDevice(String type, String deviceId);

    boolean isRegistered();

    boolean isActive();

    void setActive(boolean status);

    List<Device> getAllDeviceInfo(String type);

    Device getDeviceInfo(String type, String deviceId);

    void setOwnership(String ownershipType);

}
