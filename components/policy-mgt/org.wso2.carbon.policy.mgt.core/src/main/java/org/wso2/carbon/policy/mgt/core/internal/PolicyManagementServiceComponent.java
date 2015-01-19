/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.policy.mgt.core.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.policy.mgt.core.config.PolicyConfigurationManager;
import org.wso2.carbon.policy.mgt.core.config.PolicyManagementConfig;
import org.wso2.carbon.policy.mgt.core.config.datasource.DataSourceConfig;
import org.wso2.carbon.policy.mgt.core.dao.PolicyManagementDAOFactory;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.carbon.policy.manager" immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setRealmService"
 * unbind="unsetRealmService"
 */

public class PolicyManagementServiceComponent {

    private static Log log = LogFactory.getLog(PolicyManagementServiceComponent.class);

    protected void activate(ComponentContext componentContext) {

        try {
            PolicyConfigurationManager.getInstance().initConfig();
            PolicyManagementConfig config = PolicyConfigurationManager.getInstance().getPolicyManagementConfig();
            DataSourceConfig dsConfig = config.getPolicyManagementRepository().getDataSourceConfig();
            PolicyManagementDAOFactory.init(dsConfig);

        } catch (Throwable t) {
            String msg = "Error occurred while initializing the Policy management core.";
            log.error(msg, t);
        }
    }


    /**
     * Sets Realm Service
     *
     * @param realmService An instance of RealmService
     */
    protected void setRealmService(RealmService realmService) {

        if (log.isDebugEnabled()) {
            log.debug("Setting Realm Service");
        }
        PolicyManagementDataHolder.getInstance().setRealmService(realmService);
    }

    /**
     * Unsets Realm Service
     *
     * @param realmService An instance of RealmService
     */
    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting Realm Service");
        }
        PolicyManagementDataHolder.getInstance().setRealmService(null);
    }

}
