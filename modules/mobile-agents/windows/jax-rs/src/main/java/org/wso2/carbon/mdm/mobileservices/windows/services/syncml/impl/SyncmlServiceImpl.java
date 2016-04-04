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

package org.wso2.carbon.mdm.mobileservices.windows.services.syncml.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlMessageFormatException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsConfigurationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.AuthenticationInfo;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.DeviceUtil;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.*;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.*;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.SyncmlService;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.WindowsDevice;
import org.wso2.carbon.policy.mgt.common.PolicyManagementException;
import org.wso2.carbon.policy.mgt.core.PolicyManagerService;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils.convertToDeviceIdentifierObject;

/**
 * Implementing class of SyncmlImpl interface.
 */
public class SyncmlServiceImpl implements SyncmlService {

    private static Log log = LogFactory.getLog(SyncmlServiceImpl.class);

    /**
     * This method is used to generate and return Device object from the received information at
     * the Syncml step.
     *
     * @param windowsDevice Windows specific property object.
     * @return - Generated device object.
     */
    private Device generateDevice(WindowsDevice windowsDevice) {

        Device generatedDevice = new Device();

        Device.Property OSVersionProperty = new Device.Property();
        OSVersionProperty.setName(PluginConstants.SyncML.OS_VERSION);
        OSVersionProperty.setValue(windowsDevice.getOsVersion());

        Device.Property IMSEIProperty = new Device.Property();
        IMSEIProperty.setName(PluginConstants.SyncML.IMSI);
        IMSEIProperty.setValue(windowsDevice.getImsi());

        Device.Property IMEIProperty = new Device.Property();
        IMEIProperty.setName(PluginConstants.SyncML.IMEI);
        IMEIProperty.setValue(windowsDevice.getImei());

        Device.Property DevManProperty = new Device.Property();
        DevManProperty.setName(PluginConstants.SyncML.VENDOR);
        DevManProperty.setValue(windowsDevice.getManufacturer());

        Device.Property DevModProperty = new Device.Property();
        DevModProperty.setName(PluginConstants.SyncML.MODEL);
        DevModProperty.setValue(windowsDevice.getModel());

        List<Device.Property> propertyList = new ArrayList<>();
        propertyList.add(OSVersionProperty);
        propertyList.add(IMSEIProperty);
        propertyList.add(IMEIProperty);
        propertyList.add(DevManProperty);
        propertyList.add(DevModProperty);

        EnrolmentInfo enrolmentInfo = new EnrolmentInfo();
        enrolmentInfo.setOwner(windowsDevice.getUser());
        enrolmentInfo.setOwnership(EnrolmentInfo.OwnerShip.BYOD);
        enrolmentInfo.setStatus(EnrolmentInfo.Status.ACTIVE);

        generatedDevice.setEnrolmentInfo(enrolmentInfo);
        generatedDevice.setDeviceIdentifier(windowsDevice.getDeviceId());
        generatedDevice.setProperties(propertyList);
        generatedDevice.setType(windowsDevice.getDeviceType());

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
            throws WindowsDeviceEnrolmentException, WindowsOperationException, NotificationManagementException,
            WindowsConfigurationException {
        int msgId;
        int sessionId;
        String user;
        String token;
        String response;
        SyncmlDocument syncmlDocument;
        List<Operation> deviceInfoOperations;
        List<? extends Operation> pendingOperations;
        OperationHandler operationHandler = new OperationHandler();
        DeviceInfo deviceInfo = new DeviceInfo();

        try {
            if (SyncmlParser.parseSyncmlPayload(request) != null) {
                syncmlDocument = SyncmlParser.parseSyncmlPayload(request);
                SyncmlHeader syncmlHeader = syncmlDocument.getHeader();
                sessionId = syncmlHeader.getSessionId();
                user = syncmlHeader.getSource().getLocName();
                DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(syncmlHeader.getSource().
                        getLocURI());
                msgId = syncmlHeader.getMsgID();
                if ((PluginConstants.SyncML.SYNCML_FIRST_MESSAGE_ID == msgId) &&
                        (PluginConstants.SyncML.SYNCML_FIRST_SESSION_ID == sessionId)) {
                    token = syncmlHeader.getCredential().getData();
                    CacheEntry cacheToken = (CacheEntry) DeviceUtil.getCacheEntry(token);

                    if ((cacheToken.getUsername() != null) && (cacheToken.getUsername().equals(user))) {

                        if (enrollDevice(request)) {
                            deviceInfoOperations = deviceInfo.getDeviceInfo();
                            response = generateReply(syncmlDocument, deviceInfoOperations);
                            return Response.status(Response.Status.OK).entity(response).build();
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
                } else if (PluginConstants.SyncML.SYNCML_SECOND_MESSAGE_ID == msgId &&
                        PluginConstants.SyncML.SYNCML_FIRST_SESSION_ID == sessionId) {
                    if (enrollDevice(request)) {
                        return Response.ok().entity(generateReply(syncmlDocument, null)).build();
                    } else {
                        String msg = "Error occurred in modify enrollment.";
                        log.error(msg);
                        return Response.status(Response.Status.NOT_MODIFIED).entity(msg).build();
                    }
                } else if (sessionId >= PluginConstants.SyncML.SYNCML_SECOND_SESSION_ID) {
                    if ((syncmlDocument.getBody().getAlert() != null)) {
                        if (!syncmlDocument.getBody().getAlert().getData().equals(Constants.DISENROLL_ALERT_DATA)) {
                            pendingOperations = operationHandler.getPendingOperations(syncmlDocument);
                            return Response.ok().entity(generateReply(syncmlDocument, pendingOperations)).build();
                        } else {
                            if (WindowsAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier) != null) {
                                WindowsAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
                                return Response.ok().entity(generateReply(syncmlDocument, null)).build();
                            } else {
                                String msg = "Enrolled device can not be found in the server.";
                                log.error(msg);
                                return Response.status(Response.Status.NOT_FOUND).entity(msg).build();
                            }
                        }
                    } else {
                        pendingOperations = operationHandler.getPendingOperations(syncmlDocument);
                        return Response.ok().entity(generateReply(syncmlDocument, pendingOperations)).build();
                    }
                } else {
                    String msg = "Failure occurred in Device request message.";
                    log.error(msg);
                    return Response.status(Response.Status.BAD_REQUEST).entity(msg).build();
                }
            }
        } catch (SyncmlMessageFormatException e) {
            String msg = "Error occurred while parsing syncml request.";
            log.error(msg, e);
            throw new WindowsOperationException(msg, e);
        } catch (OperationManagementException e) {
            String msg = "Cannot access operation management service.";
            log.error(msg, e);
            throw new WindowsOperationException(msg, e);
        } catch (SyncmlOperationException e) {
            String msg = "Error occurred while getting effective feature.";
            log.error(msg, e);
            throw new WindowsConfigurationException(msg, e);
        } catch (DeviceManagementException e) {
            String msg = "Failure occurred in dis-enrollment flow.";
            log.error(msg, e);
            throw new WindowsOperationException(msg, e);
        }
        return null;
    }

    /**
     * Enroll phone device
     *
     * @param request Device syncml request for the server side.
     * @return enroll state
     * @throws WindowsDeviceEnrolmentException
     * @throws WindowsOperationException
     */
    private boolean enrollDevice(Document request) throws WindowsDeviceEnrolmentException,
            WindowsOperationException {

        String osVersion;
        String imsi = null;
        String imei = null;
        String devID;
        String devMan;
        String devMod;
        String devLang;
        String vendor;
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
            if (msgID == PluginConstants.SyncML.SYNCML_FIRST_MESSAGE_ID) {
                ReplaceTag replace = syncmlDocument.getBody().getReplace();
                List<ItemTag> itemList = replace.getItems();
                devID = itemList.get(PluginConstants.SyncML.DEVICE_ID_POSITION).getData();
                devMan = itemList.get(PluginConstants.SyncML.DEVICE_MAN_POSITION).getData();
                devMod = itemList.get(PluginConstants.SyncML.DEVICE_MODEL_POSITION).getData();
                modVersion = itemList.get(PluginConstants.SyncML.DEVICE_MOD_VER_POSITION).getData();
                devLang = itemList.get(PluginConstants.SyncML.DEVICE_LANG_POSITION).getData();
                user = syncmlDocument.getHeader().getSource().getLocName();
                AuthenticationInfo authenticationInfo = new AuthenticationInfo();
                authenticationInfo.setUsername(user);
                WindowsAPIUtils.startTenantFlow(authenticationInfo);

                if (log.isDebugEnabled()) {
                    log.debug(
                            "OS Version:" + modVersion + ", DevID: " + devID + ", DevMan: " + devMan +
                                    ", DevMod: " + devMod + ", DevLang: " + devLang);
                }
                WindowsDevice windowsDevice = new WindowsDevice();
                windowsDevice.setDeviceType(DeviceManagementConstants.MobileDeviceTypes.
                        MOBILE_DEVICE_TYPE_WINDOWS);
                windowsDevice.setDeviceId(devID);
                windowsDevice.setImei(imei);
                windowsDevice.setImsi(imsi);
                windowsDevice.setManufacturer(devMan);
                windowsDevice.setOsVersion(modVersion);
                windowsDevice.setModel(devMod);
                windowsDevice.setUser(user);
                Device device = generateDevice(windowsDevice);
                status = WindowsAPIUtils.getDeviceManagementService().enrollDevice(device);
                return status;

            } else if (msgID == PluginConstants.SyncML.SYNCML_SECOND_MESSAGE_ID) {

                List<ItemTag> itemList = syncmlDocument.getBody().getResults().getItem();
                osVersion = itemList.get(PluginConstants.SyncML.OSVERSION_POSITION).getData();
                imsi = itemList.get(PluginConstants.SyncML.IMSI_POSITION).getData();
                imei = itemList.get(PluginConstants.SyncML.IMEI_POSITION).getData();
                vendor = itemList.get(PluginConstants.SyncML.VENDOR_POSITION).getData();
                devMod = itemList.get(PluginConstants.SyncML.MODEL_POSITION).getData();
                macAddress = itemList.get(PluginConstants.SyncML.MAC_ADDRESS_POSITION).getData();
                resolution = itemList.get(PluginConstants.SyncML.RESOLUTION_POSITION).getData();
                deviceName = itemList.get(PluginConstants.SyncML.DEVICE_NAME_POSITION).getData();
                DeviceIdentifier deviceIdentifier = convertToDeviceIdentifierObject(syncmlDocument.
                        getHeader().getSource().getLocURI());
                Device existingDevice = WindowsAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
                if (!existingDevice.getProperties().isEmpty()) {
                    List<Device.Property> existingProperties = new ArrayList<>();

                    Device.Property imeiProperty = new Device.Property();
                    imeiProperty.setName(PluginConstants.SyncML.IMEI);
                    imeiProperty.setValue(imei);
                    existingProperties.add(imeiProperty);

                    Device.Property osVersionProperty = new Device.Property();
                    osVersionProperty.setName(PluginConstants.SyncML.OS_VERSION);
                    osVersionProperty.setValue(osVersion);
                    existingProperties.add(osVersionProperty);

                    Device.Property imsiProperty = new Device.Property();
                    imsiProperty.setName(PluginConstants.SyncML.IMSI);
                    imsiProperty.setValue(imsi);
                    existingProperties.add(imsiProperty);

                    Device.Property vendorProperty = new Device.Property();
                    vendorProperty.setName(PluginConstants.SyncML.VENDOR);
                    vendorProperty.setValue(vendor);
                    existingProperties.add(vendorProperty);

                    Device.Property macAddressProperty = new Device.Property();
                    macAddressProperty.setName(PluginConstants.SyncML.MAC_ADDRESS);
                    macAddressProperty.setValue(macAddress);
                    existingProperties.add(macAddressProperty);

                    Device.Property resolutionProperty = new Device.Property();
                    resolutionProperty.setName(PluginConstants.SyncML.DEVICE_INFO);
                    resolutionProperty.setValue(resolution);
                    existingProperties.add(resolutionProperty);

                    Device.Property deviceNameProperty = new Device.Property();
                    deviceNameProperty.setName(PluginConstants.SyncML.DEVICE_NAME);
                    deviceNameProperty.setValue(deviceName);
                    existingProperties.add(deviceNameProperty);

                    Device.Property deviceModelProperty = new Device.Property();
                    deviceNameProperty.setName(PluginConstants.SyncML.MODEL);
                    deviceNameProperty.setValue(devMod);
                    existingProperties.add(deviceModelProperty);

                    existingDevice.setProperties(existingProperties);
                    existingDevice.setDeviceIdentifier(syncmlDocument.getHeader().getSource().getLocURI());
                    existingDevice.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
                    status = WindowsAPIUtils.getDeviceManagementService().modifyEnrollment(existingDevice);
                    // call effective policy for the enrolling device.
                    PolicyManagerService policyManagerService = WindowsAPIUtils.getPolicyManagerService();
                    policyManagerService.getEffectivePolicy(deviceIdentifier);
                    return status;
                }
            }
        } catch (DeviceManagementException e) {
            throw new WindowsDeviceEnrolmentException("Failure occurred while enrolling device.", e);
        } catch (PolicyManagementException e) {
            throw new WindowsOperationException("Error occurred while getting effective policy.", e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
        return status;
    }

    /**
     * Generate Device payloads.
     *
     * @param syncmlDocument Parsed syncml payload from the syncml engine.
     * @param operations     Operations for generate payload.
     * @return String type syncml payload.
     * @throws WindowsOperationException
     * @throws PolicyManagementException
     * @throws org.wso2.carbon.policy.mgt.common.FeatureManagementException
     */
    public String generateReply(SyncmlDocument syncmlDocument, List<? extends Operation> operations)
            throws SyncmlMessageFormatException, SyncmlOperationException {

        OperationReply operationReply;
        SyncmlGenerator generator;
        SyncmlDocument syncmlResponse;
        if (operations == null) {
            operationReply = new OperationReply(syncmlDocument);
        } else {
            operationReply = new OperationReply(syncmlDocument, operations);
        }
        syncmlResponse = operationReply.generateReply();
        generator = new SyncmlGenerator();
        return generator.generatePayload(syncmlResponse);
    }
}
