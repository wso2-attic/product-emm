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

package org.wso2.carbon.mdm.services.android;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.*;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.mdm.services.android.bean.*;
import org.wso2.carbon.mdm.services.android.bean.Notification;
import org.wso2.carbon.mdm.services.android.bean.wrapper.*;
import org.wso2.carbon.mdm.services.android.exception.AndroidOperationException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.AndroidConstants;
import org.wso2.carbon.mdm.services.android.util.AndroidDeviceUtils;
import org.wso2.carbon.mdm.services.android.util.Message;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Android Device Operation REST-API implementation.
 */
public class OperationMgtService {

    private static Log log = LogFactory.getLog(OperationMgtService.class);
    private static final String ACCEPT = "Accept";
	private static final String OPERATION_ERROR_STATUS = "ERROR";
	private static final String DEVICE_TYPE_ANDROID = "android";

    @PUT
    @Path("{id}")
    public List<? extends Operation> getPendingOperations
            (@HeaderParam(ACCEPT) String acceptHeader, @PathParam("id") String id,
             List<? extends Operation> resultOperations) {
        Message message;
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);

        if (id == null || id.isEmpty()) {
            String errorMessage = "Device identifier is null or empty, hence returning device not found";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.BAD_REQUEST.toString()).build();
            log.error(errorMessage);
            throw new AndroidOperationException(message, responseMediaType);
        }

        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        try {
            if (!AndroidDeviceUtils.isValidDeviceIdentifier(deviceIdentifier)) {
                String errorMessage = "Device not found for identifier '" + id + "'";
                message = Message.responseMessage(errorMessage).
                        responseCode(Response.Status.BAD_REQUEST.toString()).build();
                log.error(errorMessage);
                throw new AndroidOperationException(message, responseMediaType);
            }
            if (log.isDebugEnabled()) {
                log.debug("Invoking Android pending operations:" + id);
            }
            if (resultOperations != null && !resultOperations.isEmpty()) {
                updateOperations(id, resultOperations);
            }
        } catch (OperationManagementException e) {
            log.error("Issue in retrieving operation management service instance", e);
        } catch (PolicyComplianceException e) {
            log.error("Issue in updating Monitoring operation");
        } catch (DeviceManagementException e) {
            log.error("Issue in retrieving device management service instance", e);
        } catch (ApplicationManagementException e) {
            log.error("Issue in retrieving application management service instance", e);
        } catch (NotificationManagementException e) {
	        log.error("Issue in retrieving Notification management service instance", e);
        }

        List<? extends Operation> pendingOperations;
        try {
            pendingOperations = AndroidAPIUtils.getPendingOperations(deviceIdentifier);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
        return pendingOperations;
    }

    @POST
    @Path("lock")
    public Response configureDeviceLock(@HeaderParam(ACCEPT) String acceptHeader,
                                        DeviceLockBeanWrapper deviceLockBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device lock operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();
        Response response;

        try {
            DeviceLock lock = deviceLockBeanWrapper.getOperation();

            if (lock == null) {
                throw new OperationManagementException("Lock bean is empty");
            }
            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCK);
            operation.setType(Operation.Type.PROFILE);
            operation.setEnabled(true);
            operation.setPayLoad(lock.toJSON());
            response = AndroidAPIUtils.getOperationResponse(deviceLockBeanWrapper.getDeviceIDs(), operation,
                    message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
        return response;
    }

    @POST
    @Path("unlock")
    public Response configureDeviceUnlock(@HeaderParam(ACCEPT) String acceptHeader, List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device unlock operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();
        Response response;

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_UNLOCK);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            response = AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
        return response;
    }

    @POST
    @Path("location")
    public Response getDeviceLocation(@HeaderParam(ACCEPT) String acceptHeader,
                                      List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device location operation");
        }
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCATION);
            operation.setType(Operation.Type.COMMAND);
            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation,
                    message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("clear-password")
    public Response removePassword(@HeaderParam(ACCEPT) String acceptHeader,
                                   List<String> deviceIDs) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking Android clear password operation");
        }
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CLEAR_PASSWORD);
            operation.setType(Operation.Type.COMMAND);

            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation,
                    message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("camera")
    public Response configureCamera(@HeaderParam(ACCEPT) String acceptHeader,
                                    CameraBeanWrapper cameraBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android Camera operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            Camera camera = cameraBeanWrapper.getOperation();

            if (camera == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the configure camera operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new camera instance");
            }

            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.CAMERA);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(camera.isEnabled());

            return AndroidAPIUtils.getOperationResponse(cameraBeanWrapper.getDeviceIDs(), operation, message,
                    responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("device-info")
    public Response getDeviceInformation(@HeaderParam(ACCEPT) String acceptHeader,
                                         List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking get Android device information operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_INFO);
            operation.setType(Operation.Type.COMMAND);
            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
                    responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("enterprise-wipe")
    public Response wipeDevice(@HeaderParam(ACCEPT) String acceptHeader,
                               List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking enterprise-wipe device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENTERPRISE_WIPE);
            operation.setType(Operation.Type.COMMAND);

            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
                    responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("wipe-data")
    public Response wipeData(@HeaderParam(ACCEPT) String acceptHeader,
                             WipeDataBeanWrapper wipeDataBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android wipe-data device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            WipeData wipeData = wipeDataBeanWrapper.getOperation();

            if (wipeData == null) {
                throw new OperationManagementException("WipeData bean is empty");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WIPE_DATA);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(wipeData.toJSON());

            return AndroidAPIUtils.getOperationResponse(wipeDataBeanWrapper.getDeviceIDs(), operation, message,
                    responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("application-list")
    public Response getApplications(@HeaderParam(ACCEPT) String acceptHeader,
                                    List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android getApplicationList device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.APPLICATION_LIST);
            operation.setType(Operation.Type.COMMAND);

            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
                    responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("ring-device")
    public Response ringDevice(@HeaderParam(ACCEPT) String acceptHeader,
                               List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android ring-device device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_RING);
            operation.setType(Operation.Type.COMMAND);
            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
                    responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("reboot-device")
    public Response rebootDevice(@HeaderParam(ACCEPT) String acceptHeader,
                               List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android reboot-device device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_REBOOT);
            operation.setType(Operation.Type.COMMAND);
            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
                    responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("mute")
    public Response muteDevice(@HeaderParam(ACCEPT) String acceptHeader,
                               List<String> deviceIDs) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking mute device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DEVICE_MUTE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
            return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
                    responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("install-application")
    public Response installApplication(@HeaderParam(ACCEPT) String acceptHeader,
                                       ApplicationInstallationBeanWrapper applicationInstallationBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking 'InstallApplication' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            ApplicationInstallation applicationInstallation = applicationInstallationBeanWrapper.getOperation();

            if (applicationInstallation == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the application installing operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new application installation instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.INSTALL_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationInstallation.toJSON());

            return AndroidAPIUtils.getOperationResponse(applicationInstallationBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("uninstall-application")
    public Response uninstallApplication(@HeaderParam(ACCEPT) String acceptHeader,
                                         ApplicationUninstallationBeanWrapper applicationUninstallationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'UninstallApplication' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            ApplicationUninstallation applicationUninstallation = applicationUninstallationBeanWrapper.getOperation();

            if (applicationUninstallation == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the application uninstalling operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new application uninstallation instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UNINSTALL_APPLICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(applicationUninstallation.toJSON());

            return AndroidAPIUtils.getOperationResponse(applicationUninstallationBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("blacklist-applications")
    public Response blacklistApplications(@HeaderParam(ACCEPT) String acceptHeader,
                                          BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'Blacklist-Applications' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            BlacklistApplications blacklistApplications = blacklistApplicationsBeanWrapper.getOperation();

            if (blacklistApplications == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the blacklisting apps operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new blacklist applications instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.BLACKLIST_APPLICATIONS);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(blacklistApplications.toJSON());

            return AndroidAPIUtils.getOperationResponse(blacklistApplicationsBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("upgrade-firmware")
    public Response upgradeFirmware(@HeaderParam(ACCEPT) String acceptHeader,
                                    UpgradeFirmwareBeanWrapper upgradeFirmwareBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android upgrade-firmware device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            UpgradeFirmware upgradeFirmware = upgradeFirmwareBeanWrapper.getOperation();

            if (upgradeFirmware == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the upgrade firmware operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new upgrade firmware instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.UPGRADE_FIRMWARE);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(upgradeFirmware.toJSON());
            return AndroidAPIUtils.getOperationResponse(upgradeFirmwareBeanWrapper.getDeviceIDs(),
                                                        operation, message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("vpn")
    public Response configureVPN(@HeaderParam(ACCEPT) String acceptHeader,
                                    VpnBeanWrapper vpnBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android VPN device operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            Vpn vpn = vpnBeanWrapper.getOperation();

            if (vpn == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the VPN operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new VPN instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.VPN);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(vpn.toJSON());
            return AndroidAPIUtils.getOperationResponse(vpnBeanWrapper.getDeviceIDs(),
                                                        operation, message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("notification")
    public Response sendNotification(@HeaderParam(ACCEPT) String acceptHeader,
                                     NotificationBeanWrapper notificationBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'notification' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            Notification notification = notificationBeanWrapper.getOperation();

            if (notification == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the notification operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new notification instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.NOTIFICATION);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(notification.toJSON());

            return AndroidAPIUtils.getOperationResponse(notificationBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("wifi")
    public Response configureWifi(@HeaderParam(ACCEPT) String acceptHeader,
                                  WifiBeanWrapper wifiBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'configure wifi' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            Wifi wifi = wifiBeanWrapper.getOperation();

            if (wifi == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the wifi operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new Wifi instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WIFI);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(wifi.toJSON());

            return AndroidAPIUtils.getOperationResponse(wifiBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("encrypt")
    public Response encryptStorage(@HeaderParam(ACCEPT) String acceptHeader,
                                   EncryptionBeanWrapper encryptionBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'encrypt' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            DeviceEncryption deviceEncryption = encryptionBeanWrapper.getOperation();

            if (deviceEncryption == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the device encryption operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new encryption instance");
            }

            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.ENCRYPT_STORAGE);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(deviceEncryption.isEncrypted());

            return AndroidAPIUtils.getOperationResponse(encryptionBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("change-lock-code")
    public Response changeLockCode(@HeaderParam(ACCEPT) String acceptHeader,
                                   LockCodeBeanWrapper lockCodeBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'change lock code' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            LockCode lockCode = lockCodeBeanWrapper.getOperation();

            if (lockCode == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the change lock code operation is incorrect");
                }
                throw new OperationManagementException("Issue in retrieving a new lock-code instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.CHANGE_LOCK_CODE);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(lockCode.toJSON());

            return AndroidAPIUtils.getOperationResponse(lockCodeBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("password-policy")
    public Response setPasswordPolicy(@HeaderParam(ACCEPT) String acceptHeader,
                                      PasswordPolicyBeanWrapper passwordPolicyBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'password policy' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            PasscodePolicy passcodePolicy = passwordPolicyBeanWrapper.getOperation();

            if (passcodePolicy == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the change password policy operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new Password policy instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.PASSCODE_POLICY);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(passcodePolicy.toJSON());

            return AndroidAPIUtils.getOperationResponse(passwordPolicyBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("webclip")
    public Response setWebClip(@HeaderParam(ACCEPT) String acceptHeader,
                               WebClipBeanWrapper webClipBeanWrapper) {
        if (log.isDebugEnabled()) {
            log.debug("Invoking 'webclip' operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            WebClip webClip = webClipBeanWrapper.getOperation();

            if (webClip == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the add webclip operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new web clip instance");
            }

            ProfileOperation operation = new ProfileOperation();
            operation.setCode(AndroidConstants.OperationCodes.WEBCLIP);
            operation.setType(Operation.Type.PROFILE);
            operation.setPayLoad(webClip.toJSON());

            return AndroidAPIUtils.getOperationResponse(webClipBeanWrapper.getDeviceIDs(),
                    operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    @POST
    @Path("disenroll")
    public Response setDisenrollment(@HeaderParam(ACCEPT) String acceptHeader,
                                     DisenrollmentBeanWrapper disenrollmentBeanWrapper) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device disenrollment operation");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            Disenrollment disenrollment = disenrollmentBeanWrapper.getOperation();

            if (disenrollment == null) {
                if (log.isDebugEnabled()) {
                    log.debug("The payload of the device disenrollment operation is incorrect");
                }
                throw new OperationManagementException("Issue in creating a new disenrollment instance");
            }

            CommandOperation operation = new CommandOperation();
            operation.setCode(AndroidConstants.OperationCodes.DISENROLL);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(disenrollment.isEnabled());

            return AndroidAPIUtils.getOperationResponse(disenrollmentBeanWrapper.getDeviceIDs(), operation,
                    message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message = Message.responseMessage(errorMessage).
                    responseCode(Response.Status.INTERNAL_SERVER_ERROR.toString()).build();
            log.error(errorMessage, e);
            throw new AndroidOperationException(message, responseMediaType);
        }
    }

    private void updateOperations(String deviceId, List<? extends Operation> operations)
            throws OperationManagementException, PolicyComplianceException,
            ApplicationManagementException, NotificationManagementException, DeviceManagementException {
        for (org.wso2.carbon.device.mgt.common.operation.mgt.Operation operation : operations) {
            AndroidAPIUtils.updateOperation(deviceId, operation);
	        if(operation.getStatus().equals(OPERATION_ERROR_STATUS)){
		        org.wso2.carbon.device.mgt.common.notification.mgt.Notification notification = new
				        org.wso2.carbon.device.mgt.common.notification.mgt.Notification();
		        DeviceIdentifier id = new DeviceIdentifier();
		        id.setId(deviceId);
		        id.setType(DEVICE_TYPE_ANDROID);
		        String deviceName = AndroidAPIUtils.getDeviceManagementService().getDevice(id).getName();
		        notification.setOperationId(operation.getId());
		        notification.setStatus(org.wso2.carbon.device.mgt.common.notification.mgt.Notification.
				        Status.NEW.toString());
		        notification.setDeviceIdentifier(id);
		        notification.setDescription("Operation " + operation.getCode() + " failed to execute on device "+
		                                    deviceName+". Device ID : " + deviceId);
		        AndroidAPIUtils.getNotificationManagementService().addNotification(notification);
	        }
            if (log.isDebugEnabled()) {
                log.debug("Updating operation '" + operation.toString() + "'");
            }
        }
    }
}
