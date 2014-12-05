/*
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.device.mgt.core.internal;

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.ndatasource.core.DataSourceService;
import org.wso2.carbon.securevault.SecretCallbackHandlerService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.user.core.tenant.TenantManager;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import javax.transaction.TransactionManager;

public class DeviceManagerDataHolder {

    private DataSourceService dataSourceService;

    private RealmService realmService;

    private TransactionManager transactionManager;

    private SecretCallbackHandlerService secretCallbackHandlerService;

    private TenantManager tenantManager;

    private static DeviceManagerDataHolder thisInstance = new DeviceManagerDataHolder();

    private DeviceManagerDataHolder() {
    }

    public static DeviceManagerDataHolder getInstance() {
        return thisInstance;
    }

    public DataSourceService getDataSourceService() {
        return dataSourceService;
    }

    public void setDataSourceService(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    public RealmService getRealmService() {
        return realmService;
    }

    public void setRealmService(RealmService realmService) {
        this.realmService = realmService;
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public SecretCallbackHandlerService getSecretCallbackHandlerService() {
        return secretCallbackHandlerService;
    }

    public void setSecretCallbackHandlerService(
            SecretCallbackHandlerService secretCallbackHandlerService) {
        this.secretCallbackHandlerService = secretCallbackHandlerService;
    }

    public static void setThisInstance(DeviceManagerDataHolder thisInstance) {
        DeviceManagerDataHolder.thisInstance = thisInstance;
    }

    public TenantManager getTenantManager() throws DeviceManagementException {
        RealmService realmService = getRealmService();
        if (realmService == null) {
            throw new DeviceManagementException("Realm service is not initialized properly");
        }
        return realmService.getTenantManager();
    }

    /**
     * Get tenant id of the current tenant
     *
     * @return tenant id
     * @throws DeviceManagementException if error occurred when getting tenant id
     */
    public int getTenantId() throws DeviceManagementException {
        CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
        int tenantId = context.getTenantId();
        if (tenantId != MultitenantConstants.INVALID_TENANT_ID) {
            return tenantId;
        }
        String tenantDomain = context.getTenantDomain();
        if (tenantDomain == null) {
            String msg = "Tenant domain is not properly set and thus, is null";
            throw new DeviceManagementException(msg);
        }
        TenantManager tenantManager = getTenantManager();
        try {
            tenantId = tenantManager.getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving id from the domain of tenant " +
                    tenantDomain;
            throw new DeviceManagementException(msg);
        }
        return tenantId;
    }

}
