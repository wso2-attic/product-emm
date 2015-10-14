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
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.Item;
import org.wso2.carbon.mdm.mobileservices.windows.operations.Results;
import org.wso2.carbon.mdm.mobileservices.windows.operations.Status;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlDocument;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Profile;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.util.SyncmlUtils;
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
            throws OperationManagementException, DeviceManagementException, NotificationManagementException {


        pendingDataOperations = SyncmlUtils.getDeviceManagementService()
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
            for (int x = 0; x < pendingDataOperations.size(); x++) {
                Operation operation = pendingDataOperations.get(x);
                if (operation.getId() == status.getCommandReference() && pendingDataOperations.get(x).
                        getCode().equals(String.valueOf(OperationCode.Command.DEVICE_LOCK))) {
                    operation.setStatus(Operation.Status.ERROR);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                    try {
                        NotificationManagementService service =
                                WindowsAPIUtils.getNotificationManagementService();
                        Notification lockResetNotification = new Notification();
                        lockResetNotification.setOperationId(status.getCommandReference());
                        lockResetNotification.setStatus(String.valueOf(Notification.Status.NEW));
                        lockResetNotification.setDeviceIdentifier(deviceIdentifier);
                        lockResetNotification.setDescription(
                                Constants.SyncMLResponseCodes.LOCKRESET_NOTIFICATION);
                        service.addNotification(lockResetNotification);
                    } catch (NotificationManagementException e) {
                        throw new NotificationManagementException("Failure occurred in getting notification service", e);
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

    public void lock(Status status, SyncmlDocument syncmlDocument, DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException, NotificationManagementException {

        pendingDataOperations = SyncmlUtils.getDeviceManagementService()
                .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            for (int z = 0; z < pendingDataOperations.size(); z++) {
                Operation operation = pendingDataOperations.get(z);
                if (pendingDataOperations.get(z).getCode().equals(OperationCode.Command.DEVICE_LOCK.getCode())
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
                        NotificationManagementService service = WindowsAPIUtils.getNotificationManagementService();
                        Notification lockResetNotification = new Notification();
                        lockResetNotification.setOperationId(status.getCommandReference());
                        lockResetNotification.setStatus("Error");
                        lockResetNotification.setDeviceIdentifier(deviceIdentifier);
                        lockResetNotification.setDescription(Constants.SyncMLResponseCodes.LOCKRESET_NOTIFICATION);

                        service.addNotification(lockResetNotification);
                    } catch (NotificationManagementException e) {
                        String msg = "Failure occurred in getting notification service";
                        log.error(msg);
                        throw new NotificationManagementException(msg, e);
                    }
                }
            }
        }
    }

    public void ring(Status status, SyncmlDocument syncmlDocument,
                     DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException {

        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            pendingDataOperations = SyncmlUtils.getDeviceManagementService()
                    .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
            for (int z = 0; z < pendingDataOperations.size(); z++) {
                Operation operation = pendingDataOperations.get(z);
                if (operation.getCode().equals(OperationCode.Command.DEVICE_RING) &&
                        (operation.getId() == status.getCommandReference())) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    new OperationUtils().updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperations);
                }
            }
        }
    }

    public void dataWipe(Status status, SyncmlDocument syncmlDocument,
                         DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException {

        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            pendingDataOperations = SyncmlUtils.getDeviceManagementService()
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

    public List<? extends Operation> getPendingOperations(SyncmlDocument syncmlDocument)
            throws OperationManagementException, DeviceManagementException, FeatureManagementException,
            PolicyComplianceException, NotificationManagementException {


        List<? extends Operation> pendingOperations;
        DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(
                syncmlDocument.getHeader().getSource().getLocURI());
        List<Status> statuses = syncmlDocument.getBody().getStatus();
        String lockUri = null;
        Results result = syncmlDocument.getBody().getResults();
        OperationUtils operationUtils = new OperationUtils();

        for (Status status : statuses) {

            if (status.getCommand().equals(Constants.EXECUTE)) {
                if (status.getTargetReference() == null) {
                    operationUtils.updateDeviceOperations(status, syncmlDocument, deviceIdentifier);
                } else {
                    if (status.getTargetReference().equals(OperationCode.Command.DEVICE_LOCK)) {
                        operationUtils.lock(status, syncmlDocument, deviceIdentifier);
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

                    pendingDataOperations = SyncmlUtils.getDeviceManagementService()
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
                    operationUtils.updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                } else {
                    pendingDataOperations = SyncmlUtils.getDeviceManagementService()
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
                    operationUtils.updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperations);
                }
            }
        }

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
                        if (item.getData().equals("1")) {
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
                        if (item.getData().equals("1")) {
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
                        if (item.getData().equals("0")) {
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
                        notification.setDescription(pinValue);
                        notification.setOperationId(result.getCommandReference());
                        notification.setDeviceIdentifier(deviceIdentifier);
                        try {
                            nmService.addNotification(notification);
                            if (log.isDebugEnabled()) {
                                String msg = "Lock Reset Pin code " + pinValue;
                                log.info(msg);
                            }
                        } catch (NotificationManagementException e) {
                            String msg = "Failure Occurred in getting notification service.";
                            log.error(msg);
                        }
                    }
                }
            }
        }
        boolean isCompliance = false;
        if (profiles.size() != 0) {
            try {
                List<ProfileFeature> profileFeatures = WindowsAPIUtils.getPolicyManagerService().getEffectiveFeatures(
                        deviceIdentifier);
                List<ComplianceFeature> complianceFeatures = new ArrayList<>();
                for (ProfileFeature activeFeature : profileFeatures) {
                    JSONObject policyContent = new JSONObject(activeFeature.getContent().toString());

                    for (Profile deviceFeature : profiles) {

                        if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                deviceFeature.getFeatureCode().equals(PluginConstants.OperationCodes.CAMERA)) {
                            if (policyContent.getBoolean("enabled") == (deviceFeature.isEnable())) {
                                isCompliance = true;
                                deviceFeature.setCompliance(isCompliance);
                            } else {
                                deviceFeature.setCompliance(isCompliance);
                            }
                            ComplianceFeature complianceFeature = new ComplianceFeature();
                            complianceFeature.setFeature(activeFeature);
                            complianceFeature.setFeatureCode(activeFeature.getFeatureCode());
                            complianceFeature.setCompliance(deviceFeature.isCompliance());
                            complianceFeatures.add(complianceFeature);
                        }
                        if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                deviceFeature.getFeatureCode().equals(PluginConstants.OperationCodes.ENCRYPT_STORAGE)) {
                            if (policyContent.getBoolean("encrypted") == (deviceFeature.isEnable())) {
                                isCompliance = true;
                                deviceFeature.setCompliance(isCompliance);
                            } else {
                                deviceFeature.setCompliance(isCompliance);
                            }
                            ComplianceFeature complianceFeature = new ComplianceFeature();
                            complianceFeature.setFeature(activeFeature);
                            complianceFeature.setFeatureCode(activeFeature.getFeatureCode());
                            complianceFeature.setCompliance(deviceFeature.isCompliance());
                            complianceFeatures.add(complianceFeature);
                        }
                        if (deviceFeature.getFeatureCode().equals(activeFeature.getFeatureCode()) &&
                                deviceFeature.getFeatureCode().equals(PluginConstants.OperationCodes.PASSCODE_POLICY)) {
                            if (policyContent.getBoolean("enablePassword") == (deviceFeature.isEnable())) {
                                isCompliance = true;
                                deviceFeature.setCompliance(isCompliance);
                            } else {
                                deviceFeature.setCompliance(isCompliance);
                            }
                            ComplianceFeature complianceFeature = new ComplianceFeature();
                            complianceFeature.setFeature(activeFeature);
                            complianceFeature.setFeatureCode(activeFeature.getFeatureCode());
                            complianceFeature.setCompliance(deviceFeature.isCompliance());
                            complianceFeatures.add(complianceFeature);
                        }
                    }
                }
                WindowsAPIUtils.getPolicyManagerService().checkPolicyCompliance(deviceIdentifier, complianceFeatures);
            } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                String msg = "Error occurred while getting effective policy.";
                log.error(msg);
                throw new FeatureManagementException(msg, e);
            } catch (JSONException e) {
                String msg = "Error occurred while parsing json object.";
                log.error(msg);
            } catch (PolicyComplianceException e) {
                String msg = "Error occurred while setting up policy compliance.";
                log.error(msg);
                throw new PolicyComplianceException(msg, e);
            }
        }
        pendingOperations = SyncmlUtils.getDeviceManagementService().getPendingOperations(deviceIdentifier);
        return pendingOperations;
    }
}
