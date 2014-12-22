/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cdm.api.android;

import cdm.api.android.common.AndroidAgentException;
import cdm.api.android.util.AndroidAPIUtils;
import cdm.api.android.util.Message;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Android Device Management REST-API implementation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Device {

    private static Log log = LogFactory.getLog(Device.class);

    @GET
    public List<org.wso2.carbon.device.mgt.common.Device> getAllDevices() throws AndroidAgentException {

        List<org.wso2.carbon.device.mgt.common.Device> devices;
        String msg;
        DeviceManagementService dmService;

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceMgtServiceEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceMgtServiceEx);
            throw new AndroidAgentException();
        }

        try {

            devices = dmService.getAllDevices(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            Response.status(HttpStatus.SC_OK);

        } catch (DeviceManagementException e) {
            msg = "Error occurred while fetching the device list.";
            log.error(msg, e);
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            throw new AndroidAgentException(msg, e);

        }
        return devices;
    }

    @GET
    @Path("{id}")
    public org.wso2.carbon.device.mgt.common.Device getDevice(@PathParam("id") String id) throws AndroidAgentException {
        String msg;
        DeviceManagementService dmService;
        org.wso2.carbon.device.mgt.common.Device device;

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceMgtServiceEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceMgtServiceEx);
            throw new AndroidAgentException(errorMsg, deviceMgtServiceEx);
        }
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        try {
            device = dmService.getDevice(deviceIdentifier);
            if (device == null) {
                Response.status(HttpStatus.SC_NOT_FOUND);
            }
        } catch (DeviceManagementException deviceMgtEx) {
            msg = "Error occurred while fetching the device information.";
            log.error(msg, deviceMgtEx);
            throw new AndroidAgentException(msg, deviceMgtEx);
        }
        return device;
    }

    @PUT
    @Path("{id}")
    public Message updateDevice(@PathParam("id") String id, org.wso2.carbon.device.mgt.common.Device device) throws
            AndroidAgentException {

        DeviceManagementService dmService = null;
        Message responseMessage = new Message();

        boolean result;

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceManagementServiceException) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceManagementServiceException);
        }

        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = dmService.updateDeviceInfo(device);

            if (result) {
                Response.status(HttpStatus.SC_OK);
                responseMessage.setResponseMessage("Device information has modified successfully.");
            } else {
                Response.status(HttpStatus.SC_NOT_MODIFIED);
                responseMessage.setResponseMessage("Device not found for the update.");
            }
            return responseMessage;

        } catch (DeviceManagementException deviceMgtEx) {
            String msg = "Error occurred while modifying the device information.";
            log.error(msg, deviceMgtEx);
            throw new AndroidAgentException(msg, deviceMgtEx);
        }

    }
}
