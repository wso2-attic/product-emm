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
import org.wso2.carbon.device.mgt.common.FeatureManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Profile;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;
import org.wso2.carbon.policy.mgt.common.monitor.ComplianceFeature;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;

import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils.convertToDeviceIdentifierObject;

/**
 * Class contains Operation related utilities.
 */
public class OperationUtils {
    private static Log log = LogFactory.getLog(OperationUtils.class);
    List<? extends Operation> pendingDataOperations;


    /**
     * Update the operations using device status payload.
     *
     * @param status           Client side status for the specific operations
     * @param syncmlDocument   syncml payload for operation status which parse through  the syncml engine
     * @param deviceIdentifier specific device identifier for each device
     * @throws OperationManagementException
     * @throws DeviceManagementException
     */
    public void updateDeviceOperations(Status status, SyncmlDocument syncmlDocument,
                                       DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException, NotificationManagementException,
            WindowsOperationException {

        pendingDataOperations = WindowsAPIUtils.getDeviceManagementService()
                .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED) || status.getData().equals
                (Constants.SyncMLResponseCodes.ACCEPTED_FOR_PROCESSING)) {
            for (Operation operation : pendingDataOperations) {
                if (operation.getId() == status.getCommandReference()) {
                    operation.setStatus(Operation.Status.COMPLETED);
                }
            }
            updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
        } else if (status.getData().equals(Constants.SyncMLResponseCodes.PIN_NOTFOUND)) {
            for (Operation operation : pendingDataOperations) {
                if (operation.getId() == status.getCommandReference() && operation.
                        getCode().equals(String.valueOf(OperationCode.Command.DEVICE_LOCK))) {
                    operation.setStatus(Operation.Status.ERROR);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                    try {
                        NotificationManagementService nmService = WindowsAPIUtils.getNotificationManagementService();
                        Notification lockResetNotification = new Notification();
                        lockResetNotification.setOperationId(status.getCommandReference());
                        lockResetNotification.setStatus(String.valueOf(Notification.Status.NEW));
                        lockResetNotification.setDeviceIdentifier(deviceIdentifier);
                        lockResetNotification.setDescription(
                                Constants.SyncMLResponseCodes.LOCKRESET_NOTIFICATION);
                        nmService.addNotification(lockResetNotification);
                    } catch (NotificationManagementException e) {
                        throw new WindowsOperationException("Failure occurred in getting notification service", e);
                    }
                }
            }
        }
    }

    /**
     * Update operation statuses
     *
     * @param deviceId   specific device Id
     * @param operations operation list to be update
     * @throws OperationManagementException
     */
    public void updateOperations(String deviceId,
                                 List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations)
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
     * @throws DeviceManagementException
     * @throws NotificationManagementException
     */
    public void lockOperationUpdate(Status status, SyncmlDocument syncmlDocument, DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException, NotificationManagementException {

        pendingDataOperations = WindowsAPIUtils.getDeviceManagementService()
                .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            for (Operation operation : pendingDataOperations) {
                if (operation.getCode().equals(OperationCode.Command.DEVICE_LOCK.getCode())
                        && operation.getId() == status.getCommandReference()) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    new OperationUtils().updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                }
            }
        }
        if (status.getData().equals(Constants.SyncMLResponseCodes.PIN_NOTFOUND)) {
            for (Operation operation : pendingDataOperations) {

                if (operation.getCode().equals(OperationCode.Command.DEVICE_LOCK.getCode()) &&
                        operation.getId() == status.getCommandReference()) {
                    operation.setStatus(Operation.Status.ERROR);
                    new OperationUtils().updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                    try {
                        NotificationManagementService nmService = WindowsAPIUtils.getNotificationManagementService();
                        Notification lockResetNotification = new Notification();
                        lockResetNotification.setOperationId(status.getCommandReference());
                        lockResetNotification.setStatus(String.valueOf(Notification.Status.NEW));
                        lockResetNotification.setDeviceIdentifier(deviceIdentifier);
                        lockResetNotification.setDescription(Constants.SyncMLResponseCodes.LOCKRESET_NOTIFICATION);

                        nmService.addNotification(lockResetNotification);
                    } catch (NotificationManagementException e) {
                        String msg = "Failure occurred in getting notification service";
                        log.error(msg, e);
                        throw new NotificationManagementException(msg, e);
                    }
                }
            }
        }
    }

    /***
     * Update status of the ring operation.
     *
     * @param status           Ring status of the device.
     * @param syncmlDocument   Parsed syncml payload from the syncml engine.
     * @param deviceIdentifier specific device id to be update.
     * @throws OperationManagementException
     * @throws DeviceManagementException
     */
    public void ring(Status status, SyncmlDocument syncmlDocument,
                     DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException {

        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            pendingDataOperations = WindowsAPIUtils.getDeviceManagementService()
                    .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
            for (Operation operation : pendingDataOperations) {
                if (operation.getCode().equals(OperationCode.Command.DEVICE_RING) &&
                        (operation.getId() == status.getCommandReference())) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    new OperationUtils().updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                }
            }
        }
    }

    /***
     * Update the status of the DataWipe operation.
     *
     * @param status           Status of the datawipe.
     * @param syncmlDocument   Parsed syncml payload from the syncml engine.
     * @param deviceIdentifier specific device id to be wiped.
     * @throws OperationManagementException
     * @throws DeviceManagementException
     */
    public void dataWipe(Status status, SyncmlDocument syncmlDocument,
                         DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException {

        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            pendingDataOperations = WindowsAPIUtils.getDeviceManagementService()
                    .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
            for (Operation operation : pendingDataOperations) {

                if (operation.getCode().equals(OperationCode.Command.WIPE_DATA) &&
                        (operation.getId() == status.getCommandReference())) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
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
     * @throws DeviceManagementException
     * @throws FeatureManagementException
     * @throws PolicyComplianceException
     * @throws NotificationManagementException
     */
    public List<? extends Operation> getPendingOperations(SyncmlDocument syncmlDocument)
            throws OperationManagementException, DeviceManagementException, FeatureManagementException,
            PolicyComplianceException, NotificationManagementException, WindowsDeviceEnrolmentException,
            WindowsOperationException {

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
     * @param activeFeature
     * @param deviceFeature
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
     * @throws DeviceManagementException
     * @throws NotificationManagementException
     * @throws OperationManagementException
     */
    public void UpdateUriOperations(SyncmlDocument syncmlDocument) throws DeviceManagementException,
            NotificationManagementException, OperationManagementException, WindowsOperationException {
        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        List<Status> statuses = syncmlDocument.getBody().getStatus();
        OperationUtils operationUtils = new OperationUtils();

        for (Status status : statuses) {

            if (status.getCommand().equals(Constants.EXECUTE)) {
                if (status.getTargetReference() == null) {
                    operationUtils.updateDeviceOperations(status, syncmlDocument, deviceIdentifier);
                } else {
                    if (status.getTargetReference().equals(OperationCode.Command.DEVICE_LOCK)) {
                        operationUtils.lockOperationUpdate(status, syncmlDocument, deviceIdentifier);
                    }
                    if (status.getTargetReference().equals(OperationCode.Command.DEVICE_RING)) {
                        operationUtils.ring(status, syncmlDocument, deviceIdentifier);
                    }
                    if (status.getTargetReference().equals(OperationCode.Command.WIPE_DATA)) {
                        operationUtils.dataWipe(status, syncmlDocument, deviceIdentifier);
                    }
                }
            }
            if (status.getCommand().equals(Constants.SEQUENCE)) {
                if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {

                    pendingDataOperations = WindowsAPIUtils.getDeviceManagementService()
                            .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
                    for (Operation operation : pendingDataOperations) {
                        if (operation.getCode().equals(PluginConstants.OperationCodes.POLICY_BUNDLE) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.COMPLETED);
                        }
                        if (operation.getCode().equals(PluginConstants.OperationCodes.MONITOR) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.COMPLETED);
                        }
                    }
                    operationUtils.updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                } else {
                    pendingDataOperations = WindowsAPIUtils.getDeviceManagementService()
                            .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
                    for (Operation operation : pendingDataOperations) {

                        if (operation.getCode().equals(PluginConstants.OperationCodes.POLICY_BUNDLE) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.ERROR);
                        }
                        if (operation.getCode().equals(PluginConstants.OperationCodes.MONITOR) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.ERROR);
                        }
                    }
                    operationUtils.updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
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
     * @throws NotificationManagementException
     */
    public List<Profile> generateDeviceOperationStatusObject(SyncmlDocument syncmlDocument) throws
            NotificationManagementException, WindowsOperationException {

        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        String lockUri = null;
        Results result = syncmlDocument.getBody().getResults();

        List<Profile> profiles = new ArrayList<>();
        if (result != null) {
            List<Item> results = result.getItem();
            for (OperationCode.Info info : OperationCode.Info.values()) {
                if (PluginConstants.OperationCodes.PIN_CODE.equals(info
                        .name())) {
                    lockUri = info.getCode();
                }
            }
            for (Item item : results) {
                for (OperationCode.Info info : OperationCode.Info.values()) {
                    if (item.getSource().getLocURI().equals(info.getCode()) && info.name().equals(
                            PluginConstants.OperationCodes.CAMERA_STATUS)) {
                        Profile cameraProfile = new Profile();
                        cameraProfile.setFeatureCode(PluginConstants.OperationCodes.CAMERA);
                        cameraProfile.setData(item.getData());
                        if (item.getData().equals(PluginConstants.SyncML.SYNCML_DATA_ONE)) {
                            cameraProfile.setEnable(true);
                        } else {
                            cameraProfile.setEnable(false);
                        }
                        profiles.add(cameraProfile);
                    }
                    if (item.getSource().getLocURI().equals(info.getCode()) && info.name().equals(
                            PluginConstants.OperationCodes.ENCRYPT_STORAGE_STATUS)) {
                        Profile encryptStorage = new Profile();
                        encryptStorage.setFeatureCode(PluginConstants.OperationCodes.ENCRYPT_STORAGE);
                        encryptStorage.setData(item.getData());
                        if (item.getData().equals(PluginConstants.SyncML.SYNCML_DATA_ONE)) {
                            encryptStorage.setEnable(true);
                        } else {
                            encryptStorage.setEnable(false);
                        }
                        profiles.add(encryptStorage);
                    }
                    if (item.getSource().getLocURI().equals(info.getCode()) && info.name().equals(
                            PluginConstants.OperationCodes.DEVICE_PASSWORD_STATUS)) {
                        Profile encryptStorage = new Profile();
                        encryptStorage.setFeatureCode(PluginConstants.OperationCodes.PASSCODE_POLICY);
                        encryptStorage.setData(item.getData());
                        if (item.getData().equals(PluginConstants.SyncML.SYNCML_DATA_ZERO)) {
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
                            String msg = "Failure Occurred in getting notification service.";
                            log.error(msg, e);
                            throw new WindowsOperationException(msg, e);
                        }
                        break;
                    }
                }
            }
        }
        return profiles;
    }

    /**
     * Generate Compliance Features
     *
     * @param syncmlDocument syncmlDocument object parsed from the syncml engine.
     * @throws NotificationManagementException
     * @throws FeatureManagementException
     * @throws PolicyComplianceException
     */
    public void generateComplianceFeatureStatus(SyncmlDocument syncmlDocument) throws NotificationManagementException,
            FeatureManagementException, PolicyComplianceException, WindowsDeviceEnrolmentException,
            WindowsOperationException {
        List<Profile> profiles = generateDeviceOperationStatusObject(syncmlDocument);
        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        boolean isCompliance = false;
        if (profiles.size() != Constants.EMPTY) {
            try {
                List<ProfileFeature> profileFeatures = WindowsAPIUtils.getPolicyManagerService().getEffectiveFeatures(
                        deviceIdentifier);
                List<ComplianceFeature> complianceFeatures = new ArrayList<>();
                for (ProfileFeature activeFeature : profileFeatures) {
                    JSONObject policyContent = new JSONObject(activeFeature.getContent().toString());

                    for (Profile deviceFeature : profiles) {
                        if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                deviceFeature.getFeatureCode().equals(PluginConstants.OperationCodes.CAMERA)) {
                            if (policyContent.getBoolean(PluginConstants.PolicyConfigProperties.
                                    POLICY_ENABLE) == (deviceFeature.isEnable())) {
                                isCompliance = true;
                                deviceFeature.setCompliance(isCompliance);
                            } else {
                                deviceFeature.setCompliance(isCompliance);
                            }
                            ComplianceFeature complianceFeature = setComplianceFeatures(activeFeature, deviceFeature);
                            complianceFeatures.add(complianceFeature);
                        }
                        if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                deviceFeature.getFeatureCode().equals(PluginConstants.OperationCodes.
                                        ENCRYPT_STORAGE)) {
                            if (policyContent.getBoolean(PluginConstants.PolicyConfigProperties.
                                    ENCRYPTED_ENABLE) == (deviceFeature.isEnable())) {
                                isCompliance = true;
                                deviceFeature.setCompliance(isCompliance);
                            } else {
                                deviceFeature.setCompliance(isCompliance);
                            }
                            ComplianceFeature complianceFeature = setComplianceFeatures(activeFeature, deviceFeature);
                            complianceFeatures.add(complianceFeature);
                        }
                        if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                deviceFeature.getFeatureCode().equals(PluginConstants.OperationCodes.
                                        PASSCODE_POLICY)) {
                            if (policyContent.getBoolean(PluginConstants.PolicyConfigProperties.
                                    ENABLE_PASSWORD) == (deviceFeature.isEnable())) {
                                isCompliance = true;
                                deviceFeature.setCompliance(isCompliance);
                            } else {
                                deviceFeature.setCompliance(isCompliance);
                            }
                            ComplianceFeature complianceFeature = setComplianceFeatures(activeFeature, deviceFeature);
                            complianceFeatures.add(complianceFeature);
                        }
                    }
                }
                WindowsAPIUtils.getPolicyManagerService().checkPolicyCompliance(deviceIdentifier, complianceFeatures);
            } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                String msg = "Error occurred while getting effective policy.";
                log.error(msg, e);
                throw new FeatureManagementException(msg, e);
            } catch (JSONException e) {
                String msg = "Error occurred while parsing json object.";
                log.error(msg);
                throw new WindowsDeviceEnrolmentException(msg, e);
            } catch (PolicyComplianceException e) {
                String msg = "Error occurred while setting up policy compliance.";
                log.error(msg, e);
                throw new PolicyComplianceException(msg, e);
            }
        }

    }
}
