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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import org.wso2.cdmserver.mobileservices.android.common.AndroidAgentException;
import org.wso2.cdmserver.mobileservices.android.util.AndroidAPIUtils;
import org.wso2.cdmserver.mobileservices.android.util.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Android Device Enrollment REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Enrollment {

    private static Log log = LogFactory.getLog(Enrollment.class);

    @POST
    public Message enrollDevice(org.wso2.carbon.device.mgt.common.Device device) throws AndroidAgentException {

        Message responseMsg = new Message();

        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            AndroidAPIUtils.getDeviceManagementService().enrollDevice(device);
            Response.status(Response.Status.CREATED);
            responseMsg.setResponseMessage("Device enrollment succeeded");
            return responseMsg;
        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
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
        Message responseMsg = new Message();
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);

        try {
            result = AndroidAPIUtils.getDeviceManagementService().isEnrolled(deviceIdentifier);
            if (result) {
                responseMsg.setResponseMessage("Device has already enrolled");
            } else {
                Response.status(Response.Status.NOT_FOUND);
                responseMsg.setResponseMessage("Device not found");
            }
            return responseMsg;
        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        } catch (DeviceManagementException deviceMgtEx) {
            String errorMsg = "Error occurred while enrollment of the device.";
            log.error(errorMsg, deviceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceMgtEx);
        }
    }

    @PUT
    @Path("{id}")
    public Message modifyEnrollment(@PathParam("id") String id, org.wso2.carbon.device.mgt.common.Device device)
            throws AndroidAgentException {

        boolean result;
        Message responseMsg = new Message();
        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = AndroidAPIUtils.getDeviceManagementService().modifyEnrollment(device);
            if (result) {
                responseMsg.setResponseMessage("Device enrollment has updated successfully");
                Response.status(Response.Status.ACCEPTED);
            } else {
                responseMsg.setResponseMessage("Device not found for enrollment");
                Response.status(Response.Status.NOT_MODIFIED);
            }
            return responseMsg;
        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        } catch (DeviceManagementException deviceMgtEx) {
            String errorMsg = "Error occurred while modifying enrollment of the device";
            log.error(errorMsg, deviceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceMgtEx);
        }
    }

    @DELETE
    @Path("{id}")
    public Message disEnrollDevice(@PathParam("id") String id) throws AndroidAgentException {

        Message responseMsg = new Message();
        boolean result;
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);

        try {
            result = AndroidAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
            if (result) {
                responseMsg.setResponseMessage("Device has removed successfully");
            } else {
                responseMsg.setResponseMessage("Device not found");
                Response.status(Response.Status.NOT_FOUND);
            }
            return responseMsg;
        } catch (DeviceManagementServiceException deviceServiceMgtEx) {
            String errorMsg = "Device management service error";
            log.error(errorMsg, deviceServiceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceServiceMgtEx);
        } catch (DeviceManagementException deviceMgtEx) {
            String errorMsg = "Error occurred while dis enrolling the device";
            log.error(errorMsg, deviceMgtEx);
            throw new AndroidAgentException(errorMsg, deviceMgtEx);
        }
    }
}
