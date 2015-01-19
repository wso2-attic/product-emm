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
package org.wso2.carbon.device.mgt.core.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents Device Mgt configuration.
 */
@XmlRootElement(name = "DeviceMgtConfiguration")
public final class DeviceManagementConfig {

    private DeviceManagementRepository deviceMgtRepository;

    @XmlElement(name = "ManagementRepository", required = true)
    public DeviceManagementRepository getDeviceMgtRepository() {
        return deviceMgtRepository;
    }

    public void setDeviceMgtRepository(DeviceManagementRepository deviceMgtRepository) {
        this.deviceMgtRepository = deviceMgtRepository;
    }

}
