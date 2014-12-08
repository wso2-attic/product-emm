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
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.core.DeviceManagementRepository;
import org.wso2.carbon.device.mgt.core.DeviceManager;
import org.wso2.carbon.device.mgt.core.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.core.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.core.config.datasource.DataSourceConfig;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.core.util.DeviceManagementSchemaInitializer;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.carbon.device.manager" immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setRealmService"
 * unbind="unsetRealmService"
 * @scr.reference name="device.manager.service"
 * interface="org.wso2.carbon.device.mgt.common.spi.DeviceManagerService"
 * cardinality="1..n"
 * policy="dynamic"
 * bind="setDeviceManagerService"
 * unbind="unsetDeviceManagerService"
 */
public class DeviceManagementServiceComponent {

    private static Log log = LogFactory.getLog(DeviceManagementServiceComponent.class);
    private DeviceManagementRepository pluginRepository = new DeviceManagementRepository();

    protected void activate(ComponentContext componentContext) {
        try {
            /* Initializing Device Management Configuration */
            DeviceConfigurationManager.getInstance().initConfig();

            DeviceManagementConfig config = DeviceConfigurationManager.getInstance().getDeviceManagementConfig();

            DeviceManager deviceManager = new DeviceManager(config, this.getPluginRepository());
            DeviceManagementDataHolder.getInstance().setDeviceManager(deviceManager);

            DataSourceConfig dsConfig = config.getDeviceMgtRepository().getDataSourceConfig();
            DeviceManagementDAOFactory.init(dsConfig);

            /* If -Dsetup option enabled then create device management database schema */
            String setupOption = System.getProperty("setup");
            if (setupOption != null) {
                if (log.isDebugEnabled()) {
                    log.debug("-Dsetup is enabled. Device management repository schema initialization is about " +
                            "to begin");
                }
                setupDeviceManagementSchema(dsConfig);
            }

            BundleContext bundleContext = componentContext.getBundleContext();
            bundleContext.registerService(DeviceManagementService.class.getName(),
                    new DeviceManagementService(), null);

        } catch (Throwable e) {
            String msg = "Error occurred while initializing device management core bundle";
            log.error(msg, e);
        }
    }

    private void setupDeviceManagementSchema(DataSourceConfig config) throws DeviceManagementException {
        DeviceManagementSchemaInitializer initializer = new DeviceManagementSchemaInitializer(config);
        log.info("Initializing device management repository database schema");
        try {
            initializer.createRegistryDatabase();
        } catch (Exception e) {
            throw new DeviceManagementException("Error occurred while initializing Device Management " +
                    "database schema", e);
        }
    }

    /**
     * Sets Device Manager services
     * @param deviceManager An instance of DeviceManagerService
     */
    protected void setDeviceManagerService(DeviceManagerService deviceManager) {
        if (log.isDebugEnabled()) {
            log.debug("Setting Device Management Service");
        }
        this.getPluginRepository().addDeviceManagementProvider(deviceManager);
    }

    /**
     * Unsets Device Management services
     * @param deviceManager An instance of DeviceManagerService
     */
    protected void unsetDeviceManagerService(DeviceManagerService deviceManager) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting Device Management Service");
        }
    }

    /**
     * Sets Realm Service
     * @param realmService An instance of RealmService
     */
    protected void setRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting Realm Service");
        }
        DeviceManagementDataHolder.getInstance().setRealmService(realmService);
    }

    /**
     * Unsets Realm Service
     * @param realmService An instance of RealmService
     */
    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting Realm Service");
        }
        DeviceManagementDataHolder.getInstance().setRealmService(null);
    }

    private DeviceManagementRepository getPluginRepository() {
        return pluginRepository;
    }

}
