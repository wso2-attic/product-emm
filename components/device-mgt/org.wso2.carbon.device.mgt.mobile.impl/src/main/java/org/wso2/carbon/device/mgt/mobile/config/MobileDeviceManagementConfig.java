/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http:www.apache.orglicensesLICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.config;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents Mobile Device Mgt configuration.
 */
@XmlRootElement(name = "MobileDeviceMgtConfiguration")
public final class MobileDeviceManagementConfig {

    private MobileDeviceManagementRepository mobileDeviceMgtRepository;
    private APIPublisherConfig apiPublisherConfig;

    @XmlElement(name = "ManagementRepository", nillable = false)
    public MobileDeviceManagementRepository getMobileDeviceMgtRepository() {
        return mobileDeviceMgtRepository;
    }

    public void setMobileDeviceMgtRepository(
            MobileDeviceManagementRepository mobileDeviceMgtRepository) {
        this.mobileDeviceMgtRepository = mobileDeviceMgtRepository;
    }

    @XmlElement(name = "APIPublisher")
    public APIPublisherConfig getApiPublisherConfig() {
        return apiPublisherConfig;
    }

    public void setApiPublisherConfig(APIPublisherConfig apiPublisherConfig) {
        this.apiPublisherConfig = apiPublisherConfig;
    }

}
