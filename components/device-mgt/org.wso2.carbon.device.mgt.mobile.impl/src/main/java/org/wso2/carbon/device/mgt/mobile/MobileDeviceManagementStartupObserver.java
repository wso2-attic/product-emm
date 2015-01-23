/**
 *  Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.mgt.mobile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.api.APIProvider;
import org.wso2.carbon.apimgt.impl.APIManagerFactory;
import org.wso2.carbon.core.ServerStartupObserver;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.config.APIConfig;
import org.wso2.carbon.device.mgt.mobile.config.MobileDeviceConfigurationManager;
import org.wso2.carbon.device.mgt.mobile.util.DeviceManagementAPIPublisherUtil;

import java.util.List;

public class MobileDeviceManagementStartupObserver implements ServerStartupObserver {

    private static final Log log = LogFactory.getLog(MobileDeviceManagementStartupObserver.class);

    public void completingServerStartup() {

    }

    public void completedServerStartup() {
        try {
            this.initAPIConfigs();
	        /* Publish all mobile device management related JAX-RS services as APIs */
            this.publishAPIs();
        } catch (DeviceManagementException e) {
            log.error("Error occurred while publishing Mobile Device Management related APIs", e);
        }
    }

    private void initAPIConfigs() throws DeviceManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Initializing Mobile Device Management related APIs");
        }
        List<APIConfig> apiConfigs =
                MobileDeviceConfigurationManager.getInstance().getMobileDeviceManagementConfig().
                        getApiPublisherConfig().getAPIs();
        for (APIConfig apiConfig : apiConfigs) {
            try {
                APIProvider provider =
                        APIManagerFactory.getInstance().getAPIProvider(apiConfig.getOwner());
                apiConfig.init(provider);
            } catch (APIManagementException e) {
                throw new DeviceManagementException("Error occurred while initializing API Config '" +
                        apiConfig.getName() + "'", e);
            }
        }
    }

    private void publishAPIs() throws DeviceManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Publishing Mobile Device Management related APIs");
        }
        List<APIConfig> apiConfigs =
                MobileDeviceConfigurationManager.getInstance().getMobileDeviceManagementConfig().
                        getApiPublisherConfig().getAPIs();
        for (APIConfig apiConfig : apiConfigs) {
            DeviceManagementAPIPublisherUtil.publishAPI(apiConfig);
            if (log.isDebugEnabled()) {
                log.debug("Successfully published API '" + apiConfig.getName() + "' with the context '" +
                        apiConfig.getContext() + "' and version '" + apiConfig.getVersion() + "'");
            }
        }
    }

}
