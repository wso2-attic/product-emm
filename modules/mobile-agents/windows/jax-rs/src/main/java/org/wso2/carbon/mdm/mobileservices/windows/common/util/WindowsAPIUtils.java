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

import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.MDMAPIException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * class for Windows API utilities.
 */
public class WindowsAPIUtils {

    public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
        DeviceIdentifier identifier = new DeviceIdentifier();
        identifier.setId(deviceId);
        identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
        return identifier;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
    }

    public static UserStoreManager getUserStoreManager() throws MDMAPIException {
        RealmService realmService;
        UserStoreManager userStoreManager;
        try {
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
            if (realmService == null) {
                String msg = "Realm service not initialized";
                throw new MDMAPIException(msg);
            }
            int tenantId = CarbonContext.getThreadLocalCarbonContext().getTenantId();
            userStoreManager = realmService.getTenantUserRealm(tenantId).getUserStoreManager();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving current user store manager";
            throw new MDMAPIException(msg, e);
        }
        return userStoreManager;
    }

    public static NotificationManagementService getNotificationManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return (NotificationManagementService) ctx.getOSGiService(NotificationManagementService.class, null);
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
        if (!deviceIDHolder.getErrorDeviceIdList().isEmpty()) {
            return javax.ws.rs.core.Response.status(Constants.StatusCodes.
                    MULTI_STATUS_HTTP_CODE).type(
                    responseMediaType).entity(deviceUtils.
                    convertErrorMapIntoErrorMessage(deviceIDHolder.getErrorDeviceIdList())).build();
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).
                type(responseMediaType).build();
    }

    public static PolicyManagerService getPolicyManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        return (PolicyManagerService) ctx.getOSGiService(PolicyManagerService.class, null);
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
}
