/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.services.android.util;

import com.google.gson.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.app.mgt.ApplicationManagementProviderService;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.device.mgt.core.search.mgt.impl.Utils;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.device.mgt.mobile.impl.android.gcm.GCMService;
import org.wso2.carbon.mdm.services.android.bean.property.DeviceInfo;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * AndroidAPIUtil class provides utility functions used by Android REST-API classes.
 */
public class AndroidAPIUtils {

    private static Log log = LogFactory.getLog(AndroidAPIUtils.class);

    public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
        DeviceIdentifier identifier = new DeviceIdentifier();
        identifier.setId(deviceId);
        identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
        return identifier;
    }

    public static String getAuthenticatedUser() {
        PrivilegedCarbonContext threadLocalCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        String username = threadLocalCarbonContext.getUsername();
        String tenantDomain = threadLocalCarbonContext.getTenantDomain();
        if (username != null && username.endsWith(tenantDomain)) {
            return username.substring(0, username.lastIndexOf("@"));
        }
        return username;
    }

    public static DeviceManagementProviderService getDeviceManagementService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceManagementProviderService deviceManagementProviderService =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);
        if (deviceManagementProviderService == null) {
            String msg = "Device Management service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return deviceManagementProviderService;
    }

    public static GCMService getGCMService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        GCMService gcmService = (GCMService) ctx.getOSGiService(GCMService.class, null);
        if (gcmService == null) {
            String msg = "GCM service has not initialized.";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return gcmService;
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

        AndroidDeviceUtils deviceUtils = new AndroidDeviceUtils();
        DeviceIDHolder deviceIDHolder = deviceUtils.validateDeviceIdentifiers(deviceIDs,
                message, responseMediaType);

        List<DeviceIdentifier> validDeviceIds = deviceIDHolder.getValidDeviceIDList();
        int status = getDeviceManagementService().addOperation(operation, validDeviceIds);
        if (status > 0) {
            GCMService gcmService = getGCMService();
            if (gcmService.isGCMEnabled()) {
                List<DeviceIdentifier> deviceIDList = deviceIDHolder.getValidDeviceIDList();
                List<Device> devices = new ArrayList<Device>(deviceIDList.size());
                for (DeviceIdentifier deviceIdentifier : deviceIDList) {
                    devices.add(getDeviceManagementService().getDevice(deviceIdentifier));
                }
                getGCMService().sendNotification(operation.getCode(), devices);
            }
        }
        if (!deviceIDHolder.getErrorDeviceIdList().isEmpty()) {
            return javax.ws.rs.core.Response.status(AndroidConstants.StatusCodes.
                    MULTI_STATUS_HTTP_CODE).type(
                    responseMediaType).entity(deviceUtils.
                    convertErrorMapIntoErrorMessage(deviceIDHolder.getErrorDeviceIdList())).build();
        }
        return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).
                type(responseMediaType).build();
    }


    public static PolicyManagerService getPolicyManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        PolicyManagerService policyManagerService = (PolicyManagerService) ctx.getOSGiService(
                PolicyManagerService.class, null);
        if (policyManagerService == null) {
            String msg = "Policy Manager service has not initialized";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return policyManagerService;
    }

    public static ApplicationManagementProviderService getApplicationManagerService() {
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ApplicationManagementProviderService applicationManagementProviderService =
                (ApplicationManagementProviderService) ctx.getOSGiService(ApplicationManagementProviderService.class, null);
        if (applicationManagementProviderService == null) {
            String msg = "Application Management provider service has not initialized";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        return applicationManagementProviderService;
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

    public static void updateOperation(String deviceId, Operation operation)
            throws OperationManagementException, PolicyComplianceException, ApplicationManagementException {
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId(deviceId);
        deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);

        if (AndroidConstants.OperationCodes.MONITOR.equals(operation.getCode())) {
            if (log.isDebugEnabled()) {
                log.info("Received compliance status from MONITOR operation ID: " + operation.getId());
            }
            getPolicyManagerService().checkPolicyCompliance(deviceIdentifier, operation.getPayLoad());
        } else if (AndroidConstants.OperationCodes.APPLICATION_LIST.equals(operation.getCode())) {
            if (log.isDebugEnabled()) {
                log.info("Received applications list from device '" + deviceId + "'");
            }
            updateApplicationList(operation, deviceIdentifier);

        } else if (AndroidConstants.OperationCodes.DEVICE_INFO.equals(operation.getCode())) {

            try {
                Device device = new Gson().fromJson(operation.getOperationResponse(), Device.class);
                org.wso2.carbon.device.mgt.common.device.details.DeviceInfo deviceInfo = convertDeviceToInfo(device);
                deviceInfo.setDeviceIdentifier(deviceIdentifier);
                updateDeviceInfo(deviceInfo);
            } catch (DeviceDetailsMgtException e) {
                throw new OperationManagementException("Error occurred while updating the device infomation.", e);
            }


        } else if (AndroidConstants.OperationCodes.DEVICE_LOCATION.equals(operation.getCode())) {
            try {
                DeviceLocation location = new Gson().fromJson(operation.getOperationResponse(), DeviceLocation.class);
                location.setDeviceIdentifier(deviceIdentifier);
                updateDeviceLocation(location);
            } catch (DeviceDetailsMgtException e) {
                throw new OperationManagementException("Error occurred while updating the device location.", e);
            }
        }

        getDeviceManagementService().updateOperation(deviceIdentifier, operation);
    }

    public static List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> getPendingOperations
            (DeviceIdentifier deviceIdentifier) throws OperationManagementException {

        List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations;
        operations = getDeviceManagementService().getPendingOperations(deviceIdentifier);
        return operations;
    }

    private static void updateApplicationList(Operation operation, DeviceIdentifier deviceIdentifier)
            throws ApplicationManagementException {
        // Parsing json string to get applications list.
        JsonElement jsonElement = new JsonParser().parse(operation.getOperationResponse());
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        Application app;
        List<Application> applications = new ArrayList<Application>(jsonArray.size());
        for (JsonElement element : jsonArray) {
            app = new Application();
            app.setName(element.getAsJsonObject().
                    get(AndroidConstants.ApplicationProperties.NAME).getAsString());
            app.setApplicationIdentifier(element.getAsJsonObject().
                    get(AndroidConstants.ApplicationProperties.IDENTIFIER).getAsString());
            app.setPlatform(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            if (element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.USS) != null) {
                app.setMemoryUsage(element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.USS).getAsInt());
            }
            if (element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.VERSION) != null) {
                app.setVersion(element.getAsJsonObject().get(AndroidConstants.ApplicationProperties.VERSION).getAsString());
            }
            applications.add(app);
        }
        getApplicationManagerService().updateApplicationListInstalledInDevice(deviceIdentifier, applications);
    }


    private static void updateDeviceLocation(DeviceLocation deviceLocation) throws DeviceDetailsMgtException {

        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager informationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);

        informationManager.addDeviceLocation(deviceLocation);
    }


    private static void updateDeviceInfo(org.wso2.carbon.device.mgt.common.device.details.DeviceInfo deviceInfo)
            throws DeviceDetailsMgtException {

        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        DeviceInformationManager informationManager =
                (DeviceInformationManager) ctx.getOSGiService(DeviceInformationManager.class, null);

        informationManager.addDeviceInfo(deviceInfo);
    }


    private static org.wso2.carbon.device.mgt.common.device.details.DeviceInfo convertDeviceToInfo(Device device) {

        org.wso2.carbon.device.mgt.common.device.details.DeviceInfo deviceInfo =
                new org.wso2.carbon.device.mgt.common.device.details.DeviceInfo();

        List<Device.Property> props = device.getProperties();

        for (Device.Property prop : props) {
            if (Utils.getDeviceDetailsColumnNames().containsValue(prop.getName())) {
                if (prop.getName().equalsIgnoreCase("DEVICE_MODEL")) {
                    deviceInfo.setDeviceModel(prop.getValue());
                } else if (prop.getName().equalsIgnoreCase("VENDOR")) {
                    deviceInfo.setVendor(prop.getValue());
                } else if (prop.getName().equalsIgnoreCase("OS_VERSION")) {
                    deviceInfo.setOsVersion(prop.getValue());
                } else if (prop.getName().equalsIgnoreCase("BATTERY_LEVEL")) {
                    deviceInfo.setBatteryLevel(Double.parseDouble(prop.getValue()));
                } else if (prop.getName().equalsIgnoreCase("INTERNAL_TOTAL_MEMORY")) {
                    deviceInfo.setInternalTotalMemory(Double.parseDouble(prop.getValue()));
                } else if (prop.getName().equalsIgnoreCase("INTERNAL_AVAILABLE_MEMORY")) {
                    deviceInfo.setInternalAvailableMemory(Double.parseDouble(prop.getValue()));
                } else if (prop.getName().equalsIgnoreCase("EXTERNAL_TOTAL_MEMORY")) {
                    deviceInfo.setExternalTotalMemory(Double.parseDouble(prop.getValue()));
                } else if (prop.getName().equalsIgnoreCase("EXTERNAL_AVAILABLE_MEMORY")) {
                    deviceInfo.setExternalAvailableMemory(Double.parseDouble(prop.getValue()));
                }

            } else {
                if (prop.getName().equalsIgnoreCase("CPU_INFO")) {
                    deviceInfo.setTotalRAMMemory(Double.parseDouble(getProperty(prop.getValue(), "User")));
                } else if (prop.getName().equalsIgnoreCase("RAM_INFO")) {
                    deviceInfo.setTotalRAMMemory(Double.parseDouble(getProperty(prop.getValue(), "TOTAL_MEMORY")));
                    deviceInfo.setAvailableRAMMemory(Double.parseDouble(getProperty(prop.getValue(), "AVAILABLE_MEMORY")));
                } else if (prop.getName().equalsIgnoreCase("BATTERY_INFO")) {
                    deviceInfo.setPluggedIn(Boolean.parseBoolean(getProperty(prop.getValue(), "PLUGGED")));
                } else if (prop.getName().equalsIgnoreCase("NETWORK_INFO")) {
                    deviceInfo.setSsid(getProperty(prop.getValue(), "WIFI_SSID"));
                    deviceInfo.setConnectionType(getProperty(prop.getValue(), "CONNECTION_TYPE"));
                }
            }
        }
        return deviceInfo;
    }

    private static String getProperty(String a, String needed) {

        JsonElement jsonElement = new JsonParser().parse(a);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        boolean exist = false;
        for (JsonElement element : jsonArray) {
          //  if (((JsonObject) element).entrySet().iterator().next().getValue().getAsString().equalsIgnoreCase(needed));
            for (Map.Entry<String, JsonElement> ob : ((JsonObject) element).entrySet()){
                if(exist){
                    return ob.getValue().getAsString().replace("%", "");
                }
                if(ob.getValue().getAsString().equalsIgnoreCase(needed)){
                    exist = true;
                }
            }
        }
        return "";
    }
}
