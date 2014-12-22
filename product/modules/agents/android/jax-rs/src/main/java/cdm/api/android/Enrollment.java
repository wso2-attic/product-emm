/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
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

/**
 * Android Device Enrollment REST-API implementation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Enrollment {

    private static Log log = LogFactory.getLog(Enrollment.class);

    /*
    * Request Format : {"deviceIdentifier":"macid","description":"description","ownership":"BYOD",
    * "properties":[{"name":"username","value":"harshan"},{"name":"device","value":"Harshan S5"},
    * {"name":"imei","value":"356938035643809"},{"name":"imsi","value":"404685505601234"},{"name":"model","value":"Galaxy S5"},
    * {"name":"regId","value":"02fab24b2242"},{"name":"vendor","value":"Samsung"},
    * {"name":"osVersion","value":"5.0.0"}]}
    *
    **/
    @POST
    public Message enrollDevice(org.wso2.carbon.device.mgt.common.Device device) throws AndroidAgentException {

        DeviceManagementService dmService;
        Message responseMsg = new Message();

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        }

        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            dmService.enrollDevice(device);
            Response.status(HttpStatus.SC_CREATED);
            responseMsg.setResponseMessage("Device enrollment succeeded");
            return responseMsg;

        } catch (DeviceManagementException deviceMgtEx) {
            String errorMsg = "Error occurred while enrolling the device";
            log.error(errorMsg, deviceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceMgtEx);
        }

    }

    @GET
    @Path("{id}")
    public Message isEnrolled(@PathParam("id") String id) throws AndroidAgentException {

        boolean result;
        DeviceManagementService dmService;
        Message responseMsg = new Message();

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        }

        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);

        try {
            result = dmService.isEnrolled(deviceIdentifier);
            if (result) {
                Response.status(HttpStatus.SC_OK);
                responseMsg.setResponseMessage("Device has already enrolled");
            } else {
                Response.status(HttpStatus.SC_NOT_FOUND);
                responseMsg.setResponseMessage("Device not found");
            }

            return responseMsg;

        } catch (DeviceManagementException deviceMgtEx) {
            String errormsg = "Error occurred while enrollment of the device.";
            log.error(errormsg, deviceMgtEx);
            throw new AndroidAgentException(errormsg, deviceMgtEx);
        }

    }

    /*
    * Request Format : {"deviceIdentifier":"macid","description":"description","ownership":"BYOD",
    * "properties":[{"name":"username","value":"harshan"},{"name":"device","value":"Harshan S5"},
    * {"name":"imei","value":"356938035643809"},{"name":"imsi","value":"404685505601234"},{"name":"model","value":"Galaxy S5"},
    * {"name":"regId","value":"02fab24b2242"},{"name":"vendor","value":"Samsung"},
    * {"name":"osVersion","value":"5.0.0"}]}
    *
    **/
    @PUT
    @Path("{id}")
    public Message modifyEnrollment(@PathParam("id") String id, org.wso2.carbon.device.mgt.common.Device device)
            throws AndroidAgentException {

        boolean result;
        DeviceManagementService dmService;
        Message responseMsg = new Message();

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        }

        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = dmService.modifyEnrollment(device);

            if (result) {
                responseMsg.setResponseMessage("Device enrollment has updated successfully");
                Response.status(HttpStatus.SC_OK);
            } else {
                responseMsg.setResponseMessage("device not found for enrollment");
                Response.status(HttpStatus.SC_NOT_MODIFIED);
            }

            return responseMsg;

        } catch (DeviceManagementException e) {
            String errorMsg = "Error occurred while modifying enrollment of the device";
            log.error(errorMsg, e);
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            responseMsg.setResponseMessage(errorMsg);
            return responseMsg;
        }

    }

    @DELETE
    @Path("{id}")
    public Message disenrollDevice(@PathParam("id") String id) throws AndroidAgentException {

        DeviceManagementService dmService;
        Message responseMsg = new Message();

        boolean result;

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();

        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        }
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);

        try {
            result = dmService.disenrollDevice(deviceIdentifier);
            if (result) {
                responseMsg.setResponseMessage("Device has disenrolled successfully");
                Response.status(HttpStatus.SC_OK);
            } else {
                responseMsg.setResponseMessage("Device not found");
                Response.status(HttpStatus.SC_NOT_FOUND);
            }

            return responseMsg;
        } catch (DeviceManagementException deviceMgtEx) {
            String errorMsg = "Error occurred while dis enrolling the device";
            log.error(errorMsg, deviceMgtEx);
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            responseMsg.setResponseMessage(errorMsg);
            return responseMsg;
        }
    }
}
