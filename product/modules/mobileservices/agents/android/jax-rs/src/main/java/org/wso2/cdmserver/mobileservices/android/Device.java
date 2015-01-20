/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.cdmserver.mobileservices.android;

import org.wso2.cdmserver.mobileservices.android.common.AndroidAgentException;
import org.wso2.cdmserver.mobileservices.android.util.AndroidAPIUtils;
import org.wso2.cdmserver.mobileservices.android.util.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Android Device Management REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Device {

    private static Log log = LogFactory.getLog(Device.class);

    /**
     * Get all devices.Returns list of devices registered in the CDM.
     * @return Device List
     * @throws AndroidAgentException
     */
    @GET
    public List<org.wso2.carbon.device.mgt.common.Device> getAllDevices() throws AndroidAgentException {

        List<org.wso2.carbon.device.mgt.common.Device> devices;

        try {
            devices = AndroidAPIUtils.getDeviceManagementService().getAllDevices(DeviceManagementConstants
                    .MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            return devices;
        } catch (DeviceManagementServiceException deviceMgtServiceEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceMgtServiceEx);
            throw new AndroidAgentException(errorMsg, deviceMgtServiceEx);
        } catch (DeviceManagementException e) {
            String errorMsg = "Error occurred while fetching the device list.";
            log.error(errorMsg, e);
            throw new AndroidAgentException(errorMsg, e);
        }
    }

    /**
     * Fetch device details of given device Id.
     * @param id Device Id
     * @return Device
     * @throws AndroidAgentException
     */
    @GET
    @Path("{id}")
    public org.wso2.carbon.device.mgt.common.Device getDevice(@PathParam("id") String id) throws AndroidAgentException {

        String msg;
        org.wso2.carbon.device.mgt.common.Device device;

        try {
            DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
            device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
            if (device == null) {
                Response.status(Response.Status.NOT_FOUND);
            }
            return device;
        } catch (DeviceManagementServiceException deviceMgtServiceEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceMgtServiceEx);
            throw new AndroidAgentException(errorMsg, deviceMgtServiceEx);
        } catch (DeviceManagementException deviceMgtEx) {
            msg = "Error occurred while fetching the device information.";
            log.error(msg, deviceMgtEx);
            throw new AndroidAgentException(msg, deviceMgtEx);
        }
    }

    /**
     * Update device details of given device id.
     * @param id Device Id
     * @param device  Device Details
     * @return Message
     * @throws AndroidAgentException
     */
    @PUT
    @Path("{id}")
    public Message updateDevice(@PathParam("id") String id, org.wso2.carbon.device.mgt.common.Device device) throws
            AndroidAgentException {

        Message responseMessage = new Message();
        boolean result;
        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = AndroidAPIUtils.getDeviceManagementService().updateDeviceInfo(device);
            if (result) {
                Response.status(Response.Status.ACCEPTED);
                responseMessage.setResponseMessage("Device information has modified successfully.");
            } else {
                Response.status(Response.Status.NOT_MODIFIED);
                responseMessage.setResponseMessage("Device not found for the update.");
            }
            return responseMessage;
        } catch (DeviceManagementServiceException deviceManagementServiceException) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceManagementServiceException);
            throw new AndroidAgentException(errorMsg, deviceManagementServiceException);
        } catch (DeviceManagementException deviceMgtEx) {
            String msg = "Error occurred while modifying the device information.";
            log.error(msg, deviceMgtEx);
            throw new AndroidAgentException(msg, deviceMgtEx);
        }
    }

    @POST
    @Path("/device/license")
    @Produces("text/plain")
    public String getLicense() {
        //TODO: need to implement fetch license from core
        return "License Agreement";
    }
}
