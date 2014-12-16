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

import cdm.api.android.util.AndroidAPIUtils;
import cdm.api.android.util.AndroidConstants;
import cdm.api.android.util.Message;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
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

    @POST
    public Message enrollDevice(Device device) {

        boolean result = false;
        int status = 0;
        String msg = "";
        DeviceManagementService dmService;
        Message responseMsg = new Message();

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

        try {
            if (dmService != null) {
                result = dmService.enrollDevice(device);
                Response.status(HttpStatus.SC_CREATED);
                responseMsg.setResponseMessage("Device enrollment has succeeded");
                return responseMsg;

            } else {
                responseMsg.setResponseMessage(AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE);
                Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                return responseMsg;
            }
        } catch (DeviceManagementException e) {
            log.error(msg, e);
            responseMsg.setResponseMessage("Error occurred while enrolling the device");
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return responseMsg;
        }

    }

    @GET
    @Path("{id}")
    public Message isEnrolled(@PathParam("id") String id) {

        boolean result = false;
        String msg = "";
        DeviceManagementService dmService;
        Message responseMsg = new Message();

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        try {
            if (dmService != null) {
                result = dmService.isEnrolled(deviceIdentifier);
                if (result) {
                    Response.status(HttpStatus.SC_OK);
                    responseMsg.setResponseMessage("Device already enroll");
                } else {

                    Response.status(HttpStatus.SC_NOT_FOUND);
                    responseMsg.setResponseMessage("Device not enroll");
                }
                return responseMsg;
            } else {
                Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                responseMsg.setResponseMessage(AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE);
                return responseMsg;
            }
        } catch (DeviceManagementException e) {
            msg = "Error occurred while checking enrollment of the device";
            log.error(msg, e);
            responseMsg.setResponseMessage(msg);
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return responseMsg;
        }

    }

    @PUT
    @Path("{id}")
    public Message modifyEnrollment(@PathParam("id") String id, Device device) {
        boolean result = false;
        String msg = "";
        DeviceManagementService dmService;
        Message responseMsg = new Message();

        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }

        try {
            if (dmService != null) {
                result = dmService.modifyEnrollment(device);

                if (result) {
                    responseMsg.setResponseMessage("update device");
                    Response.status(HttpStatus.SC_OK);
                }else{
                    responseMsg.setResponseMessage("Update enrollment has failed");
                    Response.status(HttpStatus.SC_NOT_MODIFIED);
                }
            } else {
                msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
                responseMsg.setResponseMessage(msg);
                Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            }
            return responseMsg;
        } catch (DeviceManagementException e) {
            msg = "Error occurred while modifying enrollment of the device";
            log.error(msg, e);
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            responseMsg.setResponseMessage(msg);
            return responseMsg;
        }

    }

    @DELETE
    @Path("{id}")
    public Message disenrollDevice(@PathParam("id") String id) {

        boolean result = false;
        String msg = "";
        DeviceManagementService dmService;
        Message responseMsg = new Message();


        try {
            dmService = AndroidAPIUtils.getDeviceManagementService();
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
        DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
        try {
            if (dmService != null) {
                result = dmService.disenrollDevice(deviceIdentifier);
                if (result) {
                    responseMsg.setResponseMessage("Dis enrolled device");
                    Response.status(HttpStatus.SC_OK);
                }else{
                    responseMsg.setResponseMessage("Device not found");
                    Response.status(HttpStatus.SC_NOT_FOUND);
                }
            } else {
                Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
                responseMsg.setResponseMessage(msg);
            }

            return responseMsg;
        } catch (DeviceManagementException e) {
            msg = "Error occurred while disenrolling the device";
            log.error(msg, e);
            Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            responseMsg.setResponseMessage(msg);
            return  responseMsg;
        }
    }
}
