/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.mdm.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManager;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * MDMAPIUtils class provides utility function used by CDM REST-API classes.
 */
public class MDMAPIUtils {

    private static Log log = LogFactory.getLog(MDMAPIUtils.class);

    public static DeviceManagementProviderService getDeviceManagementService(
            String tenantDomain) throws MDMAPIException {
        // until complete login this is use to load super tenant context
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId;
        DeviceManagementProviderService dmService;
        if (tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
            tenantId = MultitenantConstants.SUPER_TENANT_ID;
        } else {
            tenantId = getTenantId(tenantDomain);
        }
        ctx.setTenantDomain(tenantDomain);
        ctx.setTenantId(tenantId);
        dmService = (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        return dmService;
    }

    public static DeviceManagementProviderService getDeviceManagementService() throws MDMAPIException {
        return getDeviceManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }

    public static int getTenantId(String tenantDomain) throws MDMAPIException {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
        try {
            return realmService.getTenantManager().getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            throw new MDMAPIException("Error obtaining tenant id from tenant domain " + tenantDomain);
        }
    }

    public static UserStoreManager getUserStoreManager() throws MDMAPIException {

        RealmService realmService;
        UserStoreManager userStoreManager;
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);

            if (realmService == null) {
                String msg = "Realm service not initialized";
                log.error(msg);
                throw new MDMAPIException(msg);
            }
            int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
            userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
        return userStoreManager;
    }

    public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId, String deviceType) {
        DeviceIdentifier identifier = new DeviceIdentifier();
        identifier.setId(deviceId);
        identifier.setType(deviceType);
        return identifier;
    }

    public static ApplicationManager getAppManagementService(String tenantDomain) throws MDMAPIException {
        // until complete login this is use to load super tenant context
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId;
        ApplicationManager appService;
        if (tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
            tenantId = MultitenantConstants.SUPER_TENANT_ID;
        } else {
            tenantId = getTenantId(tenantDomain);
        }
        ctx.setTenantDomain(tenantDomain);
        ctx.setTenantId(tenantId);
        appService = (ApplicationManager) ctx.getOSGiService(ApplicationManager.class, null);
        PrivilegedCarbonContext.endTenantFlow();
        return appService;
    }

    public static ApplicationManager getAppManagementService() throws MDMAPIException {
        return getAppManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }

    public static PolicyManagerService getPolicyManagementService(String tenantDomain) throws MDMAPIException {
        PolicyManagerService policyManagementService;
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        int tenantId;
        if (tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)) {
            tenantId = MultitenantConstants.SUPER_TENANT_ID;
        } else {
            tenantId = getTenantId(tenantDomain);
        }
        ctx.setTenantDomain(tenantDomain);
        ctx.setTenantId(tenantId);
        policyManagementService = (PolicyManagerService) ctx.getOSGiService(PolicyManagerService.class, null);

        if (policyManagementService == null) {
            String msg = "Policy Management service not initialized";
            log.error(msg);
            throw new MDMAPIException(msg);
        }
        PrivilegedCarbonContext.endTenantFlow();
        return policyManagementService;
    }

    public static PolicyManagerService getPolicyManagementService() throws MDMAPIException {
        return getPolicyManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
    }
}
