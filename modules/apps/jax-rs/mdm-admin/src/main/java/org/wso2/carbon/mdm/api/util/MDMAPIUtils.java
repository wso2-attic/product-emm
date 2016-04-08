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

package org.wso2.carbon.mdm.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.certificate.mgt.core.service.CertificateManagementService;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.analytics.dashboard.GadgetDataService;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfigurationManagementService;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchManagerService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.policy.mgt.common.PolicyMonitoringTaskException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.policy.mgt.core.task.TaskScheduleService;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * MDMAPIUtils class provides utility function used by CDM REST-API classes.
 */
public class MDMAPIUtils {

    private static final String NOTIFIER_FREQUENCY = "notifierFrequency";
    public static final MediaType DEFAULT_CONTENT_TYPE = MediaType.APPLICATION_JSON_TYPE;

    private static Log log = LogFactory.getLog(MDMAPIUtils.class);

    public static int getNotifierFrequency(TenantConfiguration tenantConfiguration) {
        List<ConfigurationEntry> configEntryList = tenantConfiguration.getConfiguration();
        if (configEntryList != null && !configEntryList.isEmpty()) {
            for(ConfigurationEntry entry : configEntryList) {
                if (NOTIFIER_FREQUENCY.equals(entry.getName())) {
                    return Integer.parseInt((String) entry.getValue());
                }
            }
        }
        return 0;
    }

    public static void scheduleTaskService(int notifierFrequency) {
        TaskScheduleService taskScheduleService;
        try {
            taskScheduleService = getPolicyManagementService().getTaskScheduleService();
            if (taskScheduleService.isTaskScheduled()) {
                taskScheduleService.updateTask(notifierFrequency);
            } else {
                taskScheduleService.startTask(notifierFrequency);
            }
        } catch (PolicyMonitoringTaskException e) {
            log.error("Exception occurred while starting the Task service.", e);
        }
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Device Management provider service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }

    public static int getTenantId(String tenantDomain) throws MDMAPIException {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
        try {
            return realmService.getTenantManager().getTenantId(tenantDomain);
        } catch (UserStoreException e) {
            throw new MDMAPIException(
                    "Error obtaining tenant id from tenant domain " + tenantDomain);
        }
    }

    public static UserStoreManager getUserStoreManager() throws MDMAPIException {
        RealmService realmService;
        UserStoreManager userStoreManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            int tenantId = ctx.getTenantId();
            userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return userStoreManager;
    }

    /**
     * Getting the current tenant's user realm
     *
     * @return
     * @throws MDMAPIException
     */
    public static UserRealm getUserRealm() throws MDMAPIException {
        RealmService realmService;
        UserRealm realm;
        try {
            //PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            //ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            //ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);

            if (realmService == null) {
                String msg = "Realm service not initialized";
                log.error(msg);
                throw new MDMAPIException(msg);
            }
            int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
            realm = realmService.getTenantUserRealm(tenantId);
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user realm";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        } finally {
            //PrivilegedCarbonContext.endTenantFlow();
        }
        return realm;
    }

    public static AuthorizationManager getAuthorizationManager() throws MDMAPIException {
        RealmService realmService;
        AuthorizationManager authorizationManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service has not initialized.";
                log.error(msg);
                throw new IllegalStateException(msg);
            }
            int tenantId = ctx.getTenantId();
            authorizationManager = realmService.getTenantUserRealm(tenantId).getAuthorizationManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current Authorization manager.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
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

    public static ApplicationManagementProviderService getAppManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ApplicationManagementProviderService applicationManagementProviderService =
                (ApplicationManagementProviderService) ctx.getOSGiService(ApplicationManagementProviderService.class, null);
        if (applicationManagementProviderService == null) {
            String msg = "Application management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return applicationManagementProviderService;
    }

    public static PolicyManagerService getPolicyManagementService() {
        PolicyManagerService policyManagementService;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
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

    public static CertificateManagementService getCertificateManagementService() {

        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        CertificateManagementService certificateManagementService = (CertificateManagementService)
                ctx.getOSGiService(CertificateManagementService.class, null);

        if (certificateManagementService == null) {
            String msg = "Certificate Management service not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        return certificateManagementService;
    }


    public static MediaType getResponseMediaType(String acceptHeader) {
        MediaType responseMediaType;
        if (acceptHeader == null || MediaType.WILDCARD.equals(acceptHeader)) {
            responseMediaType = DEFAULT_CONTENT_TYPE;
        } else {
            responseMediaType = MediaType.valueOf(acceptHeader);
        }

        return responseMediaType;
    }

    public static DeviceInformationManager getDeviceInformationManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager deviceInformationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);
        if (deviceInformationManager == null) {
            String msg = "Device information Manager service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceInformationManager;
    }

    public static SearchManagerService getSearchManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        SearchManagerService searchManagerService =
                (SearchManagerService) ctx.getOSGiService(SearchManagerService.class, null);
        if (searchManagerService == null) {
            String msg = "Device search manager service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return searchManagerService;
    }

    public static GadgetDataService getGadgetDataService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        GadgetDataService gadgetDataService = (GadgetDataService) ctx.getOSGiService(GadgetDataService.class, null);
        if (gadgetDataService == null) {
            throw new IllegalStateException("Gadget Data Service has not been initialized.");
        }
        return gadgetDataService;
    }
}
