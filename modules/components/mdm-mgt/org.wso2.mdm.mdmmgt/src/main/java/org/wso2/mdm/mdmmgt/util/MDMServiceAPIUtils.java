/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mdm.mdmmgt.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfigurationManagementService;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import org.wso2.mdm.mdmmgt.common.MDMException;

import java.util.List;

/**
 * MDMServiceAPIUtils class provides utility function.
 */
public class MDMServiceAPIUtils {

    private static Log log = LogFactory.getLog(MDMServiceAPIUtils.class);

    public static DeviceManagementProviderService getDeviceManagementService(int tenantId) {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantId(tenantId, true);
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Device Management provider service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }

    public static int getTenantId(String tenantDomain) throws MDMException {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
        try {
            return realmService.getTenantManager().getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            throw new MDMException(
                    "Error obtaining tenant id from tenant domain " + tenantDomain);
        }
    }

    public static UserStoreManager getUserStoreManager(int tenantId) throws MDMException {
        RealmService realmService;
        UserStoreManager userStoreManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ctx.setTenantId(tenantId, true);
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            log.error(msg, e);
            throw new MDMException(msg, e);
        }
        return userStoreManager;
    }

    /**
     * Getting the current tenant's user realm
     *
     * @return
     * @throws org.wso2.mdm.mdmmgt.common.MDMException
     */
    public static UserRealm getUserRealm(int tenantId) throws MDMException {
        RealmService realmService;
        UserRealm realm;
        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ctx.setTenantId(tenantId, true);
            ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);

            if (realmService == null) {
                String msg = "Realm service not initialized";
                log.error(msg);
                throw new MDMException(msg);
            }
            realm = realmService.getTenantUserRealm(tenantId);
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user realm";
            log.error(msg, e);
            throw new MDMException(msg, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
        return realm;
    }

    public static AuthorizationManager getAuthorizationManager(int tenantId) throws MDMException {
        RealmService realmService;
        AuthorizationManager authorizationManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ctx.setTenantId(tenantId, true);
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            authorizationManager = realmService.getTenantUserRealm(tenantId).getAuthorizationManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current Authorization manager.";
            log.error(msg, e);
            throw new MDMException(msg, e);
        }
        return authorizationManager;
    }

    /**
     * This method is used to get the current tenant id.
     *
     * @return returns the tenant id.
     */
    public static int getTenantId() {
        return CarbonContext.getThreadLocalCarbonContext().getTenantId();
    }

    public static DeviceIdentifier instantiateDeviceIdentifier(String deviceType, String deviceId) {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setType(deviceType);
        deviceIdentifier.setId(deviceId);
        return deviceIdentifier;
    }

    public static ApplicationManagementProviderService getAppManagementService(int tenantId) {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantId(tenantId, true);
        ApplicationManagementProviderService applicationManagementProviderService =
                (ApplicationManagementProviderService) ctx.getOSGiService(ApplicationManagementProviderService.class, null);
        if (applicationManagementProviderService == null) {
            String msg = "Application management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return applicationManagementProviderService;
    }

    public static PolicyManagerService getPolicyManagementService(int tenantId) {
        PolicyManagerService policyManagementService;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantId(tenantId, true);
        policyManagementService =
                (PolicyManagerService) ctx.getOSGiService(PolicyManagerService.class, null);
        if (policyManagementService == null) {
            String msg = "Policy Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return policyManagementService;
    }

    public static TenantConfigurationManagementService getTenantConfigurationManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        TenantConfigurationManagementService tenantConfigurationManagementService =
                (TenantConfigurationManagementService) ctx.getOSGiService(TenantConfigurationManagementService.class, null);
        if (tenantConfigurationManagementService == null) {
            String msg = "Tenant configuration Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return tenantConfigurationManagementService;
    }

    public static NotificationManagementService getNotificationManagementService() {
        NotificationManagementService notificationManagementService;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        notificationManagementService = (NotificationManagementService) ctx.getOSGiService(
                NotificationManagementService.class, null);
        if (notificationManagementService == null) {
            String msg = "Notification Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return notificationManagementService;
    }

    public static PaginationResult getPagingResponse(int recordsTotal, int recordsFiltered, int draw, List<?> data) {
        PaginationResult pagingResponse = new PaginationResult();
        pagingResponse.setRecordsTotal(recordsTotal);
        pagingResponse.setRecordsFiltered(recordsFiltered);
        pagingResponse.setDraw(draw);
        pagingResponse.setData(data);
        return pagingResponse;
    }
}
