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

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.StatusTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlDocument;
import org.wso2.carbon.mdm.mobileservices.windows.operations.WindowsOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.operations.ResultsTag;
import org.wso2.carbon.mdm.mobileservices.windows.operations.ItemTag;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Profile;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;
import org.wso2.carbon.policy.mgt.common.monitor.ComplianceFeature;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils.convertToDeviceIdentifierObject;

/**
 * This class is used to handle pending operations of the device.
 */
public class OperationHandler {
    private static Log log = LogFactory.getLog(OperationHandler.class);


    /**
     * Update the operations using device status payload.
     *
     * @param status           Client side status for the specific operations.
     * @param syncmlDocument   syncml payload for operation status which parse through  the syncml engine.
     * @param deviceIdentifier specific device identifier for each device.
     * @throws OperationManagementException
     */
    public void updateDeviceOperations(StatusTag status, SyncmlDocument syncmlDocument,
                                       DeviceIdentifier deviceIdentifier) throws OperationManagementException {
        List<? extends Operation> pendingDataOperations;
        try {
            pendingDataOperations = WindowsAPIUtils.getPendingOperations(deviceIdentifier);

            if (Constants.SyncMLResponseCodes.ACCEPTED.equals(status.getData()) ||
                    (Constants.SyncMLResponseCodes.ACCEPTED_FOR_PROCESSING.equals(status.getData()))) {
                for (Operation operation : pendingDataOperations) {
                    if (operation.getId() == status.getCommandReference()) {
                        operation.setStatus(Operation.Status.COMPLETED);
                    }
                }
                if (syncmlDocument.getHeader().getSource().getLocURI() != null) {
                    updateStatus(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                }
            } else if (Constants.SyncMLResponseCodes.PIN_NOTFOUND.equals(status.getData())) {
                for (Operation operation : pendingDataOperations) {
                    if (operation.getId() == status.getCommandReference() && (OperationCode.Command.DEVICE_LOCK.equals(
                            operation.getCode()))) {
                        operation.setStatus(Operation.Status.ERROR);
                        if (syncmlDocument.getHeader().getSource().getLocURI() != null) {
                            updateStatus(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                        }
                        NotificationManagementService nmService = WindowsAPIUtils.getNotificationManagementService();
                        Notification lockResetNotification = new Notification();
                        lockResetNotification.setOperationId(status.getCommandReference());
                        lockResetNotification.setStatus(String.valueOf(Notification.Status.NEW));
                        lockResetNotification.setDeviceIdentifier(deviceIdentifier);
                        lockResetNotification.setDescription(
                                Constants.SyncMLResponseCodes.LOCK_RESET_NOTIFICATION);
                        nmService.addNotification(lockResetNotification);
                    }
                }
            }
        } catch (DeviceManagementException e) {
            throw new OperationManagementException("Error occurred in getting pending operations.");
        } catch (NotificationManagementException e) {
            throw new OperationManagementException("Error occurred while adding notification", e);
        }
    }

    /**
     * Update operation statuses.
     *
     * @param deviceId   specific device Id.
     * @param operations operation list to be update.
     * @throws OperationManagementException
     */
    public static void updateStatus(String deviceId, List<? extends Operation> operations)
            throws OperationManagementException {
        for (Operation operation : operations) {
            WindowsAPIUtils.updateOperation(deviceId, operation);
            if (log.isDebugEnabled()) {
                log.debug("Updating operation '" + operation.toString() + "'");
            }
        }
    }

    /**
     * Update Status of the lock operation.
     *
     * @param status           Status of the operation.
     * @param syncmlDocument   parsed syncml payload.
     * @param deviceIdentifier Device Id.
     * @throws OperationManagementException
     */
    public void updateLockOperation(StatusTag status, SyncmlDocument syncmlDocument, DeviceIdentifier deviceIdentifier)
            throws OperationManagementException {
        List<? extends Operation> pendingDataOperations;
        try {
            pendingDataOperations = WindowsAPIUtils.getPendingOperations(deviceIdentifier);
            if (Constants.SyncMLResponseCodes.ACCEPTED.equals(status.getData())) {
                for (Operation operation : pendingDataOperations) {
                    if ((OperationCode.Command.DEVICE_LOCK.getCode().equals(operation.getCode()))
                            && operation.getId() == status.getCommandReference()) {
                        operation.setStatus(Operation.Status.COMPLETED);
                        updateStatus(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                    }
                }
            }
            if (Constants.SyncMLResponseCodes.PIN_NOTFOUND.equals(status.getData())) {
                for (Operation operation : pendingDataOperations) {

                    if ((OperationCode.Command.DEVICE_LOCK.getCode().equals(operation.getCode()) &&
                            operation.getId() == status.getCommandReference())) {
                        operation.setStatus(Operation.Status.ERROR);
                        updateStatus(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);

                        NotificationManagementService nmService = WindowsAPIUtils.getNotificationManagementService();
                        Notification lockResetNotification = new Notification();
                        lockResetNotification.setOperationId(status.getCommandReference());
                        lockResetNotification.setStatus(String.valueOf(Notification.Status.NEW));
                        lockResetNotification.setDeviceIdentifier(deviceIdentifier);
                        lockResetNotification.setDescription(Constants.SyncMLResponseCodes.LOCK_RESET_NOTIFICATION);

                        nmService.addNotification(lockResetNotification);
                    }
                }
            }
        } catch (DeviceManagementException e) {
            throw new OperationManagementException("Error occurred in getting pending operations.");
        } catch (NotificationManagementException e) {
            throw new OperationManagementException("Error occurred in adding notifications.");
        }
    }

    /***
     * Update status of the ring operation.
     *
     * @param status           Ring status of the device.
     * @param syncmlDocument   Parsed syncml payload from the syncml engine.
     * @param deviceIdentifier specific device id to be update.
     * @throws OperationManagementException
     */
    public void ring(StatusTag status, SyncmlDocument syncmlDocument, DeviceIdentifier deviceIdentifier)
            throws OperationManagementException {
        List<? extends Operation> pendingDataOperations;
        try {
            if ((Constants.SyncMLResponseCodes.ACCEPTED.equals(status.getData()))) {
                pendingDataOperations = WindowsAPIUtils.getPendingOperations(deviceIdentifier);
                for (Operation operation : pendingDataOperations) {
                    if ((OperationCode.Command.DEVICE_RING.equals(operation.getCode())) &&
                            (operation.getId() == status.getCommandReference())) {
                        operation.setStatus(Operation.Status.COMPLETED);
                        updateStatus(syncmlDocument.getHeader().getSource().getLocURI(),
                                pendingDataOperations);
                    }
                }
            }
        } catch (DeviceManagementException e) {
            throw new OperationManagementException("Error occurred in getting pending operation.");
        }
    }

    /***
     * Update the status of the DataWipe operation.
     *
     * @param status           Status of the data wipe.
     * @param syncmlDocument   Parsed syncml payload from the syncml engine.
     * @param deviceIdentifier specific device id to be wiped.
     * @throws OperationManagementException
     */
    public void dataWipe(StatusTag status, SyncmlDocument syncmlDocument, DeviceIdentifier deviceIdentifier)
            throws OperationManagementException {
        List<? extends Operation> pendingDataOperations;
        if ((Constants.SyncMLResponseCodes.ACCEPTED.equals(status.getData()))) {
            try {
                pendingDataOperations = WindowsAPIUtils.getPendingOperations(deviceIdentifier);
            } catch (DeviceManagementException e) {
                throw new OperationManagementException("Error occurred in getting pending operation.");
            }
            for (Operation operation : pendingDataOperations) {

                if ((OperationCode.Command.WIPE_DATA.equals(operation.getCode())) &&
                        (operation.getId() == status.getCommandReference())) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    updateStatus(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                }
            }
        }
    }

    /**
     * Get pending operations.
     *
     * @param syncmlDocument SyncmlDocument object which creates from the syncml engine using syncml payload
     * @return Return list of pending operations.
     * @throws OperationManagementException
     */
    public List<? extends Operation> getPendingOperations(SyncmlDocument syncmlDocument)
            throws OperationManagementException, WindowsOperationException {

        List<? extends Operation> pendingOperations;
        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        UpdateUriOperations(syncmlDocument);
        generateComplianceFeatureStatus(syncmlDocument);

        pendingOperations = WindowsAPIUtils.getDeviceManagementService().getPendingOperations(deviceIdentifier);
        return pendingOperations;
    }

    /**
     * Set compliance of the feature according to the device status for the specific feature.
     *
     * @param activeFeature Features to be applied on the device.
     * @param deviceFeature Actual features applied on the device.
     * @return Returns setting up compliance feature.
     */
    public ComplianceFeature setComplianceFeatures(ProfileFeature activeFeature, Profile deviceFeature) {
        ComplianceFeature complianceFeature = new ComplianceFeature();
        complianceFeature.setFeature(activeFeature);
        complianceFeature.setFeatureCode(activeFeature.getFeatureCode());
        complianceFeature.setCompliance(deviceFeature.isCompliance());
        return complianceFeature;
    }

    /**
     * Update the completed/Error status of the operation which have the URI of the operation code in the syncml payload.
     *
     * @param syncmlDocument SyncmlDocument object generated from the the syncml engine.
     * @throws OperationManagementException
     */
    public void UpdateUriOperations(SyncmlDocument syncmlDocument) throws OperationManagementException,
            WindowsOperationException {
        List<? extends Operation> pendingDataOperations;
        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());

        List<StatusTag> statuses = syncmlDocument.getBody().getStatus();
        try {
            pendingDataOperations = WindowsAPIUtils.getPendingOperations(deviceIdentifier);
        } catch (DeviceManagementException e) {
            throw new OperationManagementException("Error occurred in getting pending operation.");
        }
        for (StatusTag status : statuses) {

            if ((Constants.EXECUTE.equals(status.getCommand()))) {
                if (status.getTargetReference() == null) {
                    updateDeviceOperations(status, syncmlDocument, deviceIdentifier);
                } else {
                    if ((OperationCode.Command.DEVICE_LOCK.equals(status.getTargetReference()))) {
                        updateLockOperation(status, syncmlDocument, deviceIdentifier);
                    }
                    if ((OperationCode.Command.DEVICE_RING.equals(status.getTargetReference()))) {
                        ring(status, syncmlDocument, deviceIdentifier);
                    }
                    if (equals(OperationCode.Command.WIPE_DATA.equals(status.getTargetReference()))) {
                        dataWipe(status, syncmlDocument, deviceIdentifier);
                    }
                }
            }
            if ((Constants.SEQUENCE.equals(status.getCommand()))) {
                if ((Constants.SyncMLResponseCodes.ACCEPTED.equals(status.getData()))) {
                    for (Operation operation : pendingDataOperations) {
                        if ((PluginConstants.OperationCodes.POLICY_BUNDLE.equals(operation.getCode())) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.COMPLETED);
                        }
                        if ((PluginConstants.OperationCodes.MONITOR.equals(operation.getCode())) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.COMPLETED);
                        }
                    }
                    updateStatus(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                } else {
                    for (Operation operation : pendingDataOperations) {

                        if ((PluginConstants.OperationCodes.POLICY_BUNDLE.equals(operation.getCode())) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.ERROR);
                        }
                        if ((PluginConstants.OperationCodes.MONITOR.equals(operation.getCode())) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.ERROR);
                        }
                    }
                    updateStatus(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                }
            }
        }
    }

    /**
     * Generate status of the features that have been activated on the device.
     *
     * @param syncmlDocument syncmlDocument object pasrsed from the syncml engine.
     * @return device statuses for the activated features
     * @throws WindowsOperationException
     */
    public List<Profile> generateDeviceOperationStatusObject(SyncmlDocument syncmlDocument) throws
            WindowsOperationException {

        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        String lockUri = null;
        ResultsTag result = syncmlDocument.getBody().getResults();

        List<Profile> profiles = new ArrayList<>();
        if (result != null) {
            List<ItemTag> results = result.getItem();
            for (OperationCode.Info info : OperationCode.Info.values()) {
                if (PluginConstants.OperationCodes.PIN_CODE.equals(info
                        .name())) {
                    lockUri = info.getCode();
                }
            }
            for (ItemTag item : results) {
                for (OperationCode.Info info : OperationCode.Info.values()) {
                    if (item.getSource().getLocURI().equals(info.getCode()) &&
                            PluginConstants.OperationCodes.CAMERA_STATUS.equals(info.name())) {
                        Profile cameraProfile = new Profile();
                        cameraProfile.setFeatureCode(PluginConstants.OperationCodes.CAMERA);
                        cameraProfile.setData(item.getData());
                        if ((PluginConstants.SyncML.SYNCML_DATA_ONE.equals(item.getData()))) {
                            cameraProfile.setEnable(true);
                        } else {
                            cameraProfile.setEnable(false);
                        }
                        profiles.add(cameraProfile);
                    }
                    if (item.getSource().getLocURI().equals(info.getCode()) &&
                            PluginConstants.OperationCodes.ENCRYPT_STORAGE_STATUS.equals(info.name())) {
                        Profile encryptStorage = new Profile();
                        encryptStorage.setFeatureCode(PluginConstants.OperationCodes.ENCRYPT_STORAGE);
                        encryptStorage.setData(item.getData());
                        if ((PluginConstants.SyncML.SYNCML_DATA_ONE.equals(item.getData()))) {
                            encryptStorage.setEnable(true);
                        } else {
                            encryptStorage.setEnable(false);
                        }
                        profiles.add(encryptStorage);
                    }
                    if (item.getSource().getLocURI().equals(info.getCode()) &&
                            PluginConstants.OperationCodes.DEVICE_PASSWORD_STATUS.equals(info.name())) {
                        Profile encryptStorage = new Profile();
                        encryptStorage.setFeatureCode(PluginConstants.OperationCodes.PASSCODE_POLICY);
                        encryptStorage.setData(item.getData());
                        if ((PluginConstants.SyncML.SYNCML_DATA_ZERO.equals(item.getData()))) {
                            encryptStorage.setEnable(true);
                        } else {
                            encryptStorage.setEnable(false);
                        }
                        profiles.add(encryptStorage);
                    }
                    if (!item.getData().isEmpty() && item.getSource().getLocURI().equals(lockUri)) {
                        String pinValue = item.getData();
                        NotificationManagementService nmService = WindowsAPIUtils.getNotificationManagementService();
                        Notification notification = new Notification();
                        notification.setDescription("Auto generated DevicePin : " + pinValue);
                        notification.setOperationId(result.getCommandReference());
                        notification.setDeviceIdentifier(deviceIdentifier);
                        notification.setStatus(String.valueOf(Notification.Status.NEW));
                        try {
                            nmService.addNotification(notification);
                        } catch (NotificationManagementException e) {
                            throw new WindowsOperationException("Failure Occurred while getting notification" +
                                    " service.", e);
                        }
                        break;
                    }
                }
            }
        }
        return profiles;
    }

    /**
     * Generate Compliance Features.
     *
     * @param syncmlDocument syncmlDocument object parsed from the syncml engine.
     * @throws WindowsOperationException
     */
    public void generateComplianceFeatureStatus(SyncmlDocument syncmlDocument) throws WindowsOperationException {
        List<Profile> profiles = generateDeviceOperationStatusObject(syncmlDocument);
        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        boolean isCompliance = false;
        if (profiles.size() != Constants.EMPTY) {
            try {
                if (WindowsAPIUtils.getPolicyManagerService().getAppliedPolicyToDevice(deviceIdentifier).getProfile().
                        getProfileFeaturesList() != null) {
                    List<ProfileFeature> profileFeatures = WindowsAPIUtils.getPolicyManagerService().
                            getAppliedPolicyToDevice(deviceIdentifier).getProfile().getProfileFeaturesList();
                    List<ComplianceFeature> complianceFeatures = new ArrayList<>();
                    for (ProfileFeature activeFeature : profileFeatures) {
                        JSONObject policyContent = new JSONObject(activeFeature.getContent().toString());

                        for (Profile deviceFeature : profiles) {
                            if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                    (PluginConstants.OperationCodes.CAMERA.equals(deviceFeature.getFeatureCode()))) {
                                if (policyContent.getBoolean(PluginConstants.PolicyConfigProperties.
                                        POLICY_ENABLE) == (deviceFeature.isEnable())) {
                                    isCompliance = true;
                                    deviceFeature.setCompliance(isCompliance);
                                } else {
                                    deviceFeature.setCompliance(isCompliance);
                                }
                                ComplianceFeature complianceFeature = setComplianceFeatures(activeFeature,
                                        deviceFeature);
                                complianceFeatures.add(complianceFeature);
                            }
                            if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                    (PluginConstants.OperationCodes.
                                            ENCRYPT_STORAGE.equals(deviceFeature.getFeatureCode()))) {
                                if (policyContent.getBoolean(PluginConstants.PolicyConfigProperties.
                                        ENCRYPTED_ENABLE) == (deviceFeature.isEnable())) {
                                    isCompliance = true;
                                    deviceFeature.setCompliance(isCompliance);
                                } else {
                                    deviceFeature.setCompliance(isCompliance);
                                }
                                ComplianceFeature complianceFeature = setComplianceFeatures(activeFeature,
                                        deviceFeature);
                                complianceFeatures.add(complianceFeature);
                            }
                            if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                    (PluginConstants.OperationCodes.
                                            PASSCODE_POLICY.equals(deviceFeature.getFeatureCode()))) {
                                if (policyContent.getBoolean(PluginConstants.PolicyConfigProperties.
                                        ENABLE_PASSWORD) == (deviceFeature.isEnable())) {
                                    isCompliance = true;
                                    deviceFeature.setCompliance(isCompliance);
                                } else {
                                    deviceFeature.setCompliance(isCompliance);
                                }
                                ComplianceFeature complianceFeature = setComplianceFeatures(activeFeature,
                                        deviceFeature);
                                complianceFeatures.add(complianceFeature);
                            }
                        }
                    }
                    WindowsAPIUtils.getPolicyManagerService().checkPolicyCompliance(deviceIdentifier,
                            complianceFeatures);
                }
            } catch (JSONException e) {
                throw new WindowsOperationException("Error occurred while parsing json object.", e);
            } catch (PolicyComplianceException e) {
                throw new WindowsOperationException("Error occurred while setting up policy compliance.", e);
            } catch (PolicyManagementException e) {
                throw new WindowsOperationException("Error occurred while getting effective policy.", e);
            }
        }

    }
}
