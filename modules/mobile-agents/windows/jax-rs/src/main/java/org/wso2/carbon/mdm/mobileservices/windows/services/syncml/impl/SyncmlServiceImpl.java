/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.services.syncml.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementService;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.DeviceUtil;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.*;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.SyncmlService;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Profile;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.util.SyncmlUtils;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.common.ProfileFeature;
import org.wso2.carbon.policy.mgt.common.monitor.ComplianceFeature;
import org.wso2.carbon.policy.mgt.common.monitor.PolicyComplianceException;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils.convertToDeviceIdentifierObject;

/**
 * Implementing class of SyncmlImpl interface.
 */
public class SyncmlServiceImpl implements SyncmlService {

    private static final int SYNCML_FIRST_MESSAGE_ID = 1;
    private static final int SYNCML_SECOND_MESSAGE_ID = 2;
    private static final int SYNCML_FIRST_SESSION_ID = 1;
    private static final int SYNCML_SECOND_SESSION_ID = 2;
    private static final int OSVERSION_POSITION = 0;
    private static final int DEVICE_ID_POSITION = 0;
    private static final int DEVICE_MODE_POSITION = 2;
    private static final int DEVICE_MAN_POSITION = 1;
    private static final int DEVICE_MOD_VER_POSITION = 3;
    private static final int DEVICE_LANG_POSITION = 4;
    private static final int IMSI_POSITION = 1;
    private static final int IMEI_POSITION = 2;
    private static final int VENDER_POSITION = 7;
    private static final int MACADDRESS_POSITION = 8;
    private static final int RESOLUTION_POSITION = 9;
    private static final int DEVICE_NAME_POSITION = 10;
    private static final String OS_VERSION = "OS_VERSION";
    private static final String IMSI = "IMSI";
    private static final String IMEI = "IMEI";
    private static final String VENDOR = "VENDER";
    private static final String MODEL = "DEVICE_MODEL";

    List<? extends Operation> pendingDataOperation;

    private static Log log = LogFactory.getLog(SyncmlServiceImpl.class);

    /**
     * This method is used to generate and return Device object from the received information at
     * the Syncml step.
     *
     * @param deviceID     - Unique device ID received from the Device
     * @param osVersion    - Device OS version
     * @param imsi         - Device IMSI
     * @param imei         - Device IMEI
     * @param manufacturer - Device Manufacturer name
     * @param model        - Device Model
     * @return - Generated device object
     */
    private Device generateDevice(String type, String deviceID, String osVersion, String imsi,
                                  String imei, String manufacturer, String model, String user) {

        Device generatedDevice = new Device();

        Device.Property OSVersionProperty = new Device.Property();
        OSVersionProperty.setName(OS_VERSION);
        OSVersionProperty.setValue(osVersion);

        Device.Property IMSEIProperty = new Device.Property();
        IMSEIProperty.setName(SyncmlServiceImpl.IMSI);
        IMSEIProperty.setValue(imsi);

        Device.Property IMEIProperty = new Device.Property();
        IMEIProperty.setName(SyncmlServiceImpl.IMEI);
        IMEIProperty.setValue(imei);

        Device.Property DevManProperty = new Device.Property();
        DevManProperty.setName(VENDOR);
        DevManProperty.setValue(manufacturer);

        Device.Property DevModProperty = new Device.Property();
        DevModProperty.setName(MODEL);
        DevModProperty.setValue(model);

        List<Device.Property> propertyList = new ArrayList<>();
        propertyList.add(OSVersionProperty);
        propertyList.add(IMSEIProperty);
        propertyList.add(IMEIProperty);
        propertyList.add(DevManProperty);
        propertyList.add(DevModProperty);

        EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
        enrolmentInfo.setOwner(user);
        enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
        enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);

        generatedDevice.setEnrolmentInfo(enrolmentInfo);
        generatedDevice.setDeviceIdentifier(deviceID);
        generatedDevice.setProperties(propertyList);
        generatedDevice.setType(type);

        return generatedDevice;
    }

    /**
     * Method for calling SyncML engine for producing the Syncml response. For the first SyncML message comes from
     * the device, this method produces a response to retrieve device information for enrolling the device.
     *
     * @param request - SyncML request
     * @return - SyncML response
     * @throws WindowsOperationException
     * @throws WindowsDeviceEnrolmentException
     */
    @Override
    public Response getResponse(Document request)
            throws WindowsDeviceEnrolmentException, WindowsOperationException, OperationManagementException,
            DeviceManagementException, FeatureManagementException, PolicyComplianceException, JSONException,
            PolicyManagementException, NotificationManagementException {

        String val = SyncmlServiceImpl.getStringFromDoc(request);
        int msgID;
        int sessionId;
        String user;
        String token;
        String response;
        SyncmlDocument syncmlDocument;
        List<Operation> deviceInfoOperations;
        List<? extends Operation> pendingOperations;

        if (SyncmlParser.parseSyncmlPayload(request) != null) {
            syncmlDocument = SyncmlParser.parseSyncmlPayload(request);

            SyncmlHeader syncmlHeader = syncmlDocument.getHeader();

            sessionId = syncmlHeader.getSessionId();
            user = syncmlHeader.getSource().getLocName();
            DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(syncmlHeader.getSource()
                    .getLocURI());
            msgID = syncmlHeader.getMsgID();
            if (SYNCML_FIRST_MESSAGE_ID == msgID && SYNCML_FIRST_SESSION_ID == sessionId) {
                token = syncmlHeader.getCredential().getData();
                CacheEntry cacheToken = (CacheEntry) DeviceUtil.getCacheEntry(token);

                if (cacheToken.getUsername().equals(user)) {

                    if (enrollDevice(request)) {
                        deviceInfoOperations = getDeviceInfo();
                        try {
                            response = generateReply(syncmlDocument, deviceInfoOperations);
                            return Response.status(Response.Status.OK).entity(response).build();
                        } catch (JSONException e) {
                            throw new JSONException("Error occurred in while parsing json object.");
                        } catch (PolicyManagementException e) {
                            throw new PolicyManagementException("Error occurred in while getting effective policy.", e);
                        } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                            throw new FeatureManagementException("Error occurred in while getting effective feature", e);
                        }

                    } else {
                        String msg = "Error occurred in device enrollment.";
                        log.error(msg);
                        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
                    }
                } else {
                    String msg = "Authentication failure due to incorrect credentials.";
                    log.error(msg);
                    return Response.status(Response.Status.UNAUTHORIZED).entity(msg).build();
                }
            } else if (SYNCML_SECOND_MESSAGE_ID == msgID && SYNCML_FIRST_SESSION_ID == sessionId) {

                if (enrollDevice(request)) {
                    try {
                        return Response.ok().entity(generateReply(syncmlDocument, null)).build();
                    } catch (JSONException e) {
                        throw new JSONException("Error occurred in while parsing json object.");
                    } catch (PolicyManagementException e) {
                        throw new PolicyManagementException("Error occurred in while getting effective policy.", e);
                    } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                        throw new FeatureManagementException("Error occurred in while getting effective feature", e);
                    }

                } else {
                    String msg = "Error occurred in modify enrollment.";
                    log.error(msg);
                    return Response.status(Response.Status.NOT_MODIFIED).entity(msg).build();
                }
            } else if (sessionId >= SYNCML_SECOND_SESSION_ID) {
                if ((syncmlDocument.getBody().getAlert() != null)) {
                    if (!syncmlDocument.getBody().getAlert().getData().equals(Constants.DISENROLL_ALERT_DATA)) {
                        try {
                            pendingOperations = getPendingOperations(syncmlDocument);
                            String gen = generateReply(syncmlDocument, pendingOperations);
                            //return Response.ok().entity(generateReply(syncmlDocument, (List<Operation>)
                            //	pendingOperations)).build();
                            return Response.ok().entity(gen).build();

                        } catch (OperationManagementException e) {
                            String msg = "Cannot access operation management service.";
                            log.error(msg);
                            throw new OperationManagementException(msg, e);
                        } catch (DeviceManagementException e) {
                            String msg = "Cannot access Device management service.";
                            log.error(msg);
                            throw new DeviceManagementException(msg, e);
                        } catch (FeatureManagementException e) {
                            String msg = "Error occurred in getting effective features. ";
                            log.error(msg);
                            throw new FeatureManagementException(msg, e);
                        } catch (PolicyComplianceException e) {
                            String msg = "Error occurred in setting policy compliance.";
                            log.error(msg);
                            throw new PolicyComplianceException(msg, e);
                        } catch (JSONException e) {
                            throw new JSONException("Error occurred in while parsing json object.");
                        } catch (PolicyManagementException e) {
                            throw new PolicyManagementException("Error occurred in while getting effective policy.", e);
                        } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                            throw new FeatureManagementException("Error occurred in while getting effective feature", e);
                        } catch (NotificationManagementException e) {
                            throw new NotificationManagementException("Error occurred in while getting notification service ", e);
                        }
                    } else {
                        try {
                            if (WindowsAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier) != null) {
                                WindowsAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
                                return Response.ok().entity(generateReply(syncmlDocument, null)).build();
                            } else {
                                String msg = "Enrolled device can not be found in the server.";
                                log.error(msg);
                                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
                            }
                        } catch (DeviceManagementException e) {
                            String msg = "Failure occurred in dis-enrollment flow.";
                            log.error(msg);
                            throw new WindowsOperationException(msg, e);
                        } catch (JSONException e) {
                            throw new JSONException("Error occurred in while parsing json object.");
                        } catch (PolicyManagementException e) {
                            throw new PolicyManagementException("Error occurred in while getting effective policy.", e);
                        } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                            throw new FeatureManagementException("Error occurred in while getting effective feature", e);
                        }
                    }
                } else {
                    try {
                        pendingOperations = getPendingOperations(syncmlDocument);
                        String replygen = generateReply(syncmlDocument, pendingOperations);
                        //return Response.ok().entity(generateReply(syncmlDocument, (List<Operation>)pendingOperations))
                        //.build();
                        return Response.ok().entity(replygen).build();

                    } catch (OperationManagementException e) {
                        String msg = "Cannot access operation management service.";
                        log.error(msg);
                        throw new WindowsOperationException(msg, e);
                    } catch (DeviceManagementException e) {
                        String msg = "Cannot access Device management service.";
                        log.error(msg);
                        throw new WindowsOperationException(msg, e);
                    } catch (FeatureManagementException e) {
                        String msg = "Error occurred in getting effective features. ";
                        log.error(msg);
                        throw new FeatureManagementException(msg, e);
                    } catch (PolicyComplianceException e) {
                        String msg = "Error occurred in setting policy compliance.";
                        log.error(msg);
                        throw new PolicyComplianceException(msg, e);
                    } catch (JSONException e) {
                        throw new JSONException("Error occurred in while parsing json object.");
                    } catch (PolicyManagementException e) {
                        throw new PolicyManagementException("Error occurred in while getting effective policy.", e);
                    } catch (org.wso2.carbon.policy.mgt.common.FeatureManagementException e) {
                        throw new FeatureManagementException("Error occurred in while getting effective feature", e);
                    } catch (NotificationManagementException e) {
                        throw new NotificationManagementException("Error occurred in while getting notification " + "service ", e);
                    }
                }
            } else {
                String msg = "Failure occurred in Device request message.";
                log.error(msg);
                return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
            }
        }
        return null;
    }
    private boolean enrollDevice(Document request) throws WindowsDeviceEnrolmentException, WindowsOperationException {

        String osVersion;
        String imsi = null;
        String imei = null;
        String devID;
        String devMan;
        String devMod;
        String devLang;
        String vender;
        String macAddress;
        String resolution;
        String modVersion;
        boolean status = false;
        String user;
        String deviceName;
        int msgID;
        SyncmlDocument syncmlDocument;

        try {
            syncmlDocument = SyncmlParser.parseSyncmlPayload(request);
            msgID = syncmlDocument.getHeader().getMsgID();
            if (msgID == SYNCML_FIRST_MESSAGE_ID) {
                Replace replace = syncmlDocument.getBody().getReplace();
                List<Item> itemList = replace.getItems();
                devID = itemList.get(DEVICE_ID_POSITION).getData();
                devMan = itemList.get(DEVICE_MAN_POSITION).getData();
                devMod = itemList.get(DEVICE_MODE_POSITION).getData();
                modVersion = itemList.get(DEVICE_MOD_VER_POSITION).getData();
                devLang = itemList.get(DEVICE_LANG_POSITION).getData();
                user = syncmlDocument.getHeader().getSource().getLocName();

                if (log.isDebugEnabled()) {
                    log.debug(
                            "OS Version:" + modVersion + ", DevID: " + devID + ", DevMan: " + devMan +
                                    ", DevMod: " + devMod + ", DevLang: " + devLang);
                }
                Device generateDevice = generateDevice(DeviceManagementConstants.MobileDeviceTypes.
                        MOBILE_DEVICE_TYPE_WINDOWS, devID, modVersion, imsi, imei, devMan, devMod, user);
                status = WindowsAPIUtils.getDeviceManagementService().enrollDevice(generateDevice);
                return status;

            } else if (msgID == SYNCML_SECOND_MESSAGE_ID) {
                List<Item> itemList = syncmlDocument.getBody().getResults().getItem();
                osVersion = itemList.get(OSVERSION_POSITION).getData();
                imsi = itemList.get(IMSI_POSITION).getData();
                imei = itemList.get(IMEI_POSITION).getData();
                vender = itemList.get(VENDER_POSITION).getData();
                macAddress = itemList.get(MACADDRESS_POSITION).getData();
                resolution = itemList.get(RESOLUTION_POSITION).getData();
                deviceName = itemList.get(DEVICE_NAME_POSITION).getData();
                DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(syncmlDocument.getHeader().getSource()
                        .getLocURI());
                Device existingDevice = WindowsAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);

                if (!existingDevice.getProperties().isEmpty()) {
                    List<Device.Property> existingProperties = new ArrayList<>();

                    Device.Property imeiProperty = new Device.Property();
                    imeiProperty.setName("IMEI");
                    imeiProperty.setValue(imei);
                    existingProperties.add(imeiProperty);

                    Device.Property osVersionProperty = new Device.Property();
                    osVersionProperty.setName("OS_VERSION");
                    osVersionProperty.setValue(osVersion);
                    existingProperties.add(osVersionProperty);

                    Device.Property imsiProperty = new Device.Property();
                    imsiProperty.setName("IMSI");
                    imsiProperty.setValue(imsi);
                    existingProperties.add(imsiProperty);

                    Device.Property venderProperty = new Device.Property();
                    venderProperty.setName("VENDOR");
                    venderProperty.setValue(vender);
                    existingProperties.add(venderProperty);

                    Device.Property macAddressProperty = new Device.Property();
                    macAddressProperty.setName("MAC_ADDRESS");
                    macAddressProperty.setValue(macAddress);
                    existingProperties.add(macAddressProperty);

                    Device.Property resolutionProperty = new Device.Property();
                    resolutionProperty.setName("DEVICE_INFO");
                    resolutionProperty.setValue(resolution);
                    existingProperties.add(resolutionProperty);

                    Device.Property deviceNameProperty = new Device.Property();
                    deviceNameProperty.setName("DEVICE_NAME");
                    deviceNameProperty.setValue(deviceName);
                    existingProperties.add(deviceNameProperty);

                    existingDevice.setProperties(existingProperties);
                    existingDevice.setDeviceIdentifier(syncmlDocument.getHeader().getSource().getLocURI());
                    existingDevice.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
                    status = WindowsAPIUtils.getDeviceManagementService().modifyEnrollment(existingDevice);
                    return status;
                }
            }
        } catch (DeviceManagementException e) {
            String msg = "Failure occurred in enrolling device.";
            log.debug(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        } catch (WindowsOperationException e) {
            String msg = "Failure occurred in parsing Syncml document.";
            log.error(msg, e);
            throw new WindowsOperationException(msg, e);
        }
        return status;
    }

    public static String getStringFromDoc(org.w3c.dom.Document doc) {
        DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
        LSSerializer lsSerializer = domImplementation.createLSSerializer();
        return lsSerializer.writeToString(doc);
    }

    public String generateReply(SyncmlDocument syncmlDocument, List<? extends Operation> lsDeviceInfo)
            throws WindowsOperationException, JSONException, PolicyManagementException,
            org.wso2.carbon.policy.mgt.common.FeatureManagementException {
        OperationReply operationReply;
        SyncmlGenerator generator;
        SyncmlDocument syncmlResponse;
        if (lsDeviceInfo == null) {
            operationReply = new OperationReply(syncmlDocument);
        } else {
            operationReply = new OperationReply(syncmlDocument, lsDeviceInfo);
        }
        syncmlResponse = operationReply.generateReply();
        generator = new SyncmlGenerator();
        return generator.generatePayload(syncmlResponse);
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

        for (Status status : statuses) {

            if (status.getCommand().equals(Constants.EXECUTE)) {
                if (status.getTargetReference() == null) {
                    updateDeviceOperations(status, syncmlDocument, deviceIdentifier);
                } else {
                    if (status.getTargetReference().equals(OperationCode.Command.DEVICE_LOCK)) {
                        lock(status, syncmlDocument, deviceIdentifier);
                    }
                    if (status.getTargetReference().equals(OperationCode.Command.DEVICE_RING)) {
                        ring(status, syncmlDocument, deviceIdentifier);
                    }
                    if (status.getTargetReference().equals(OperationCode.Command.WIPE_DATA)) {
                        dataWipe(status, syncmlDocument, deviceIdentifier);
                    }
                }
            }
            if (status.getCommand().equals(Constants.SEQUENCE)) {
                if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {

                    pendingDataOperation = SyncmlUtils.getDeviceManagementService()
                            .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
                    for (Operation operation : pendingDataOperation) {
                        if (operation.getCode().equals(PluginConstants.OperationCodes.POLICY_BUNDLE) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.COMPLETED);
                        }
                        if (operation.getCode().equals(PluginConstants.OperationCodes.MONITOR) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.COMPLETED);
                        }
                    }
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
                } else {
                    pendingDataOperation = SyncmlUtils.getDeviceManagementService()
                            .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
                    for (Operation operation : pendingDataOperation) {

                        if (operation.getCode().equals(PluginConstants.OperationCodes.POLICY_BUNDLE) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.ERROR);
                        }
                        if (operation.getCode().equals(PluginConstants.OperationCodes.MONITOR) &&
                                operation.getId() == status.getCommandReference()) {
                            operation.setStatus(Operation.Status.ERROR);
                        }
                    }
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
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
                    if (item.getSource().getLocURI().equals
                            ("./Vendor/MSFT/PolicyManager/Device/Security/RequireDeviceEncryption")) {
                        Profile encryptStorage = new Profile();
                        encryptStorage.setFeatureCode("ENCRYPT_STORAGE");
                        encryptStorage.setData(item.getData());
                        if (item.getData().equals("1")) {
                            encryptStorage.setEnable(true);
                        } else {
                            encryptStorage.setEnable(false);
                        }
                        profiles.add(encryptStorage);
                    }
                    if (item.getSource().getLocURI().equals
                            ("./Vendor/MSFT/PolicyManager/Device/DeviceLock/DevicePasswordEnabled")) {

                        Profile encryptStorage = new Profile();
                        encryptStorage.setFeatureCode("PASSCODE_POLICY");
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

        pendingDataOperation = SyncmlUtils.getDeviceManagementService()
                .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            for (int z = 0; z < pendingDataOperation.size(); z++) {
                Operation operation = pendingDataOperation.get(z);
                if (pendingDataOperation.get(z).getCode().equals(OperationCode.Command.DEVICE_LOCK.getCode())
                        && operation.getId() == status.getCommandReference()) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
                }
            }
        }
        if (status.getData().equals(Constants.SyncMLResponseCodes.PIN_NOTFOUND)) {
            for (Operation operation : pendingDataOperation) {

                if (operation.getCode().equals(OperationCode.Command.DEVICE_LOCK.getCode()) &&
                        operation.getId() == status.getCommandReference()) {
                    operation.setStatus(Operation.Status.ERROR);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
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
            pendingDataOperation = SyncmlUtils.getDeviceManagementService()
                    .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
            for (int z = 0; z < pendingDataOperation.size(); z++) {
                Operation operation = pendingDataOperation.get(z);
                if (operation.getCode().equals(OperationCode.Command.DEVICE_RING) &&
                        (operation.getId() == status.getCommandReference())) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
                }
            }
        }
    }

    public void dataWipe(Status status, SyncmlDocument syncmlDocument,
                         DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException {

        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED)) {
            pendingDataOperation = SyncmlUtils.getDeviceManagementService()
                    .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
            for (Operation operation : pendingDataOperation) {

                if (operation.getCode().equals(OperationCode.Command.WIPE_DATA) &&
                        (operation.getId() == status.getCommandReference())) {
                    operation.setStatus(Operation.Status.COMPLETED);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(),
                            pendingDataOperation);
                }
            }
        }
    }

    public void updateDeviceOperations(Status status, SyncmlDocument syncmlDocument,
                                       DeviceIdentifier deviceIdentifier)
            throws OperationManagementException, DeviceManagementException {

        pendingDataOperation = SyncmlUtils.getDeviceManagementService()
                .getOperationsByDeviceAndStatus(deviceIdentifier, Operation.Status.PENDING);
        if (status.getData().equals(Constants.SyncMLResponseCodes.ACCEPTED) || status.getData().equals
                (Constants.SyncMLResponseCodes.ACCEPTED_FOR_PROCESSING)) {
            for (Operation operation : pendingDataOperation) {
                if (operation.getId() == status.getCommandReference()) {
                    operation.setStatus(Operation.Status.COMPLETED);
                }
            }
            updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
        } else if (status.getData().equals(Constants.SyncMLResponseCodes.PIN_NOTFOUND)) {
            for (int x = 0; x < pendingDataOperation.size(); x++) {
                Operation operation = pendingDataOperation.get(x);
                if (operation.getId() == status.getCommandReference() && pendingDataOperation.get(x).
                        getCode().equals(String.valueOf(OperationCode.Command.DEVICE_LOCK))) {
                    operation.setStatus(Operation.Status.ERROR);
                    updateOperations(syncmlDocument.getHeader().getSource().getLocURI(), pendingDataOperation);
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
                        String msg = "Failure occurred in getting notification service";
                        log.error(msg);
                    }
                }
            }
        }
    }

    public List<Operation> getDeviceInfo() {

        List<Operation> deviceInfoOperations = new ArrayList<>();

        Operation osVersion = new Operation();
        osVersion.setCode("SOFTWARE_VERSION");
        osVersion.setType(Operation.Type.INFO);
        deviceInfoOperations.add(osVersion);

        Operation imsi = new Operation();
        imsi.setCode("IMSI");
        imsi.setType(Operation.Type.INFO);
        deviceInfoOperations.add(imsi);

        Operation imei = new Operation();
        imei.setCode("IMEI");
        imei.setType(Operation.Type.INFO);
        deviceInfoOperations.add(imei);

        Operation deviceID = new Operation();
        deviceID.setCode("DEV_ID");
        deviceID.setType(Operation.Type.INFO);
        deviceInfoOperations.add(deviceID);

        Operation manufacturer = new Operation();
        manufacturer.setCode("MANUFACTURER");
        manufacturer.setType(Operation.Type.INFO);
        deviceInfoOperations.add(manufacturer);

        Operation model = new Operation();
        model.setCode("MODEL");
        model.setType(Operation.Type.INFO);
        deviceInfoOperations.add(model);

        Operation language = new Operation();
        language.setCode("LANGUAGE");
        language.setType(Operation.Type.INFO);
        deviceInfoOperations.add(language);

        Operation vender = new Operation();
        vender.setCode("VENDER");
        vender.setType(Operation.Type.INFO);
        deviceInfoOperations.add(vender);

        Operation macaddress = new Operation();
        macaddress.setCode("MAC_ADDRESS");
        macaddress.setType(Operation.Type.INFO);
        deviceInfoOperations.add(macaddress);

        Operation resolution = new Operation();
        resolution.setCode("RESOLUTION");
        resolution.setType(Operation.Type.INFO);
        deviceInfoOperations.add(resolution);

        Operation deviceName = new Operation();
        deviceName.setCode("DEVICE_NAME");
        deviceName.setType(Operation.Type.INFO);
        deviceInfoOperations.add(deviceName);

        return deviceInfoOperations;
    }

}
