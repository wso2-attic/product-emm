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
package org.wso2.carbon.device.mgt.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;

/**
 * @scr.component name="org.wso2.carbon.device.manager" immediate="true"
 * @scr.reference name="device.manager.service"
 * interface="org.wso2.carbon.device.mgt.common.spi.DeviceManager" cardinality="1..n"
 * policy="dynamic" bind="setDeviceManagerService" unbind="unsetDeviceManagerService"
 */
public class DeviceMgtServiceComponent {

    private static Log log = LogFactory.getLog(DeviceMgtServiceComponent.class);

    protected void setDeviceManagerService(DeviceManagerService deviceManager) {
        if (log.isDebugEnabled()) {
            log.debug("Setting Device Management Service");
        }
    }

    protected void unsetDeviceManagerService(DeviceManagerService deviceManager) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting Device Management Service");
        }
    }


}
