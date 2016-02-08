/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.identity.core.util.IdentityTenantUtil;
import org.wso2.carbon.identity.oauth2.OAuth2TokenValidationService;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.user.api.TenantManager;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import org.wso2.carbon.webapp.authenticator.framework.config.AuthenticatorConfig;
import org.wso2.carbon.webapp.authenticator.framework.config.AuthenticatorConfigService;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Class for get Windows API utilities.
 */
public class WindowsAPIUtils {

    public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
        DeviceIdentifier identifier = new DeviceIdentifier();
        identifier.setId(deviceId);
        identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
        return identifier;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        DeviceManagementProviderService deviceManagementProviderService = null;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        if (ctx.getOSGiService(DeviceManagementProviderService.class, null) != null) {
            deviceManagementProviderService =
                    (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        }
        if (deviceManagementProviderService == null) {
            throw new IllegalStateException("Device Management service has not initialized.");
        }
        return deviceManagementProviderService;
    }

    public static NotificationManagementService getNotificationManagementService() {
        NotificationManagementService notificationManagementService = null;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        if (ctx.getOSGiService(DeviceManagementProviderService.class, null) != null) {
            notificationManagementService = (NotificationManagementService) ctx.getOSGiService(
                    NotificationManagementService.class, null);
        }
        if (notificationManagementService == null) {
            throw new IllegalStateException("Notification Management service not initialized.");
        }
        return notificationManagementService;
    }

    public static MediaType getResponseMediaType(String acceptHeader) {
        MediaType responseMediaType;
        if (MediaType.WILDCARD.equals(acceptHeader)) {
            responseMediaType = MediaType.APPLICATION_JSON_TYPE;
        } else {
            responseMediaType = MediaType.valueOf(acceptHeader);
        }
        return responseMediaType;
    }

    public static Response getOperationResponse(List<String> deviceIDs, Operation operation,
                                                Message message, MediaType responseMediaType)
            throws DeviceManagementException, OperationManagementException {
        WindowsDeviceUtils deviceUtils = new WindowsDeviceUtils();
        DeviceIDHolder deviceIDHolder = deviceUtils.validateDeviceIdentifiers(deviceIDs,
                message, responseMediaType);
        getDeviceManagementService().addOperation(operation, deviceIDHolder.getValidDeviceIDList());
        if (!deviceIDHolder.getInvalidDeviceIdList().isEmpty()) {
            return javax.ws.rs.core.Response.status(PluginConstants.StatusCodes.
                    MULTI_STATUS_HTTP_CODE).type(
                    responseMediaType).entity(deviceUtils.
                    convertErrorMapIntoErrorMessage(deviceIDHolder.getInvalidDeviceIdList())).build();
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).
                type(responseMediaType).build();
    }

    public static PolicyManagerService getPolicyManagerService() {
        PolicyManagerService policyManagerService = null;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        if (ctx.getOSGiService(DeviceManagementProviderService.class, null) != null) {
            policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                    PolicyManagerService.class, null);
        }
        if (policyManagerService == null) {
            throw new IllegalStateException("Policy Manager service has not initialized");
        }
        return policyManagerService;
    }

    public static void updateOperation(String deviceId, Operation operation)
            throws OperationManagementException {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
        getDeviceManagementService().updateOperation(deviceIdentifier, operation);
    }

    public static TenantConfiguration getTenantConfiguration() throws DeviceManagementException {
        return getDeviceManagementService().getConfiguration(
                DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
    }

    public static int getTenantIdOFUser(String username) throws DeviceManagementException {
        int tenantId = 0;
        String domainName = MultitenantUtils.getTenantDomain(username);
        if (domainName != null) {
            try {
                if (IdentityTenantUtil.getRealmService() != null) {
                    TenantManager tenantManager = IdentityTenantUtil.getRealmService().getTenantManager();
                    tenantId = tenantManager.getTenantId(domainName);
                }
            } catch (UserStoreException e) {
                String errorMsg = "Error when getting the tenant id from the tenant domain : " + domainName;
                throw new DeviceManagementException(errorMsg, e);
            }
        }
        return tenantId;
    }

    public static OAuth2TokenValidationService getOAuth2TokenValidationService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        OAuth2TokenValidationService oAuth2TokenValidationService = null;
        if (ctx.getOSGiService(OAuth2TokenValidationService.class, null) != null) {
            oAuth2TokenValidationService =
                    (OAuth2TokenValidationService) ctx.getOSGiService(OAuth2TokenValidationService.class, null);
        }
        if (oAuth2TokenValidationService == null) {
            throw new IllegalStateException("OAuth2TokenValidation service has not initialized.");
        }
        return oAuth2TokenValidationService;
    }

    public static AuthenticatorConfig getBSTAuthenticatorConfig() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        AuthenticatorConfig authenticatorConfig = null;
        if (ctx.getOSGiService(AuthenticatorConfigService.class, null) != null) {
            AuthenticatorConfigService authenticatorConfigService =
                    (AuthenticatorConfigService) ctx.getOSGiService(AuthenticatorConfigService.class, null);
            authenticatorConfig = authenticatorConfigService.getAuthenticatorConfig("BST");
        }
        if (authenticatorConfig == null) {
            throw new IllegalStateException("BST authenticatorConfig has not initialized.");
        }
        return authenticatorConfig;
    }

    public static void startTenantFlow(String userName) {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.
                getThreadLocalCarbonContext();
        privilegedCarbonContext.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        privilegedCarbonContext.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        privilegedCarbonContext.setUsername(userName);
    }

    /**
     * This method is used to get tenant configurations.
     *
     * @return List of Configurations entries.
     * @throws DeviceManagementException
     */
    public static List<ConfigurationEntry> getTenantConfigurationData() throws DeviceManagementException {
        if (WindowsAPIUtils.getTenantConfiguration() != null) {
            TenantConfiguration configuration = WindowsAPIUtils.getTenantConfiguration();
            return configuration.getConfiguration();
        } else {
            return null;
        }
    }
}
