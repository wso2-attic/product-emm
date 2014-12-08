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
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.core.config.DeviceConfigurationManager;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.device.mgt.core.util.DeviceMgtDbCreator;
import org.wso2.carbon.utils.CarbonUtils;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import java.io.File;

/**
 * @scr.component name="org.wso2.carbon.device.manager" immediate="true"
 * @scr.reference name="device.manager.service"
 * interface="org.wso2.carbon.device.mgt.common.spi.DeviceManagerService" cardinality="1..n"
 * policy="dynamic" bind="setDeviceManagerService" unbind="unsetDeviceManagerService"
 */
public class DeviceMgtServiceComponent {

    private static Log log = LogFactory.getLog(DeviceMgtServiceComponent.class);
    private final String deviceMgtSetupSql = CarbonUtils.getCarbonHome() + File.separator + "dbscripts" ;

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

    protected void activate(ComponentContext componentContext) {
        BundleContext bundleContext = componentContext.getBundleContext();


        try {

           /* Looks up for the JNDI registered transaction manager */
            DeviceManagerDataHolder.getInstance().setTransactionManager(this.lookupTransactionManager());
            /* Initializing RSS Configuration */
            DeviceConfigurationManager.getInstance().initConfig();

            String setupOption = System.getProperty("setup");
            //if -Dsetup option specified then create rss manager tables
            if (setupOption != null) {
                log.info("Setup option specified");
                DeviceMgtDbCreator dbCreator = new DeviceMgtDbCreator(DeviceManagerUtil.getDataSource());
                dbCreator.setRssDBScriptDirectory(deviceMgtSetupSql);
                log.info("Creating Meta Data tables");
                dbCreator.createRegistryDatabase();
            }

        } catch (Throwable e) {
            String msg = "Error occurred while initializing RSS Manager core bundle";
            log.error(msg, e);
        }
    }

    private TransactionManager lookupTransactionManager() {
        TransactionManager transactionManager = null;
        try {
            Object txObj = InitialContext.doLookup(
                    DeviceManagementConstants.STANDARD_USER_TRANSACTION_JNDI_NAME);
            if (txObj instanceof TransactionManager) {
                transactionManager = (TransactionManager) txObj;
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot find transaction manager at: "
                        + DeviceManagementConstants.STANDARD_USER_TRANSACTION_JNDI_NAME, e);
            }
            /* ignore, move onto next step */
        }
        if (transactionManager == null) {
            try {
                transactionManager = InitialContext.doLookup(
                        DeviceManagementConstants.STANDARD_TRANSACTION_MANAGER_JNDI_NAME);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("Cannot find transaction manager at: " +
                            DeviceManagementConstants.STANDARD_TRANSACTION_MANAGER_JNDI_NAME, e);
                }
                /* we'll do the lookup later, maybe user provided a custom JNDI name */
            }
        }
        return transactionManager;
    }

}
