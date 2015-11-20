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

package org.wso2.carbon.mdm.services.android;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Android Device Enrollment REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@WebService
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class EnrollmentService {

	private static Log log = LogFactory.getLog(EnrollmentService.class);

	@POST
	public Message enrollDevice(org.wso2.carbon.device.mgt.common.Device device)
			throws AndroidAgentException {

		Message responseMsg = new Message();
		String msg;
		try {
			device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			device.getEnrolmentInfo().setOwner(AndroidAPIUtils.getAuthenticatedUser());
			boolean status = AndroidAPIUtils.getDeviceManagementService().enrollDevice(device);
			if (status) {
				Response.status(Response.Status.CREATED);
				responseMsg.setResponseMessage("Device enrollment succeeded.");
				responseMsg.setResponseCode(Response.Status.CREATED.toString());
			} else {
				Response.status(Response.Status.INTERNAL_SERVER_ERROR);
				responseMsg.setResponseMessage("Device enrollment failed.");
				responseMsg.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while enrolling the device";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return responseMsg;
	}

	@GET
	@Path("{deviceId}")
	public Message isEnrolled(@PathParam("deviceId") String id) throws AndroidAgentException {
		String msg;
		boolean result;
		Message responseMsg = new Message();
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);

		try {
			result = AndroidAPIUtils.getDeviceManagementService().isEnrolled(deviceIdentifier);
			if (result) {
				responseMsg.setResponseMessage("Device has already enrolled");
				responseMsg.setResponseCode(Response.Status.ACCEPTED.toString());
				Response.status(Response.Status.ACCEPTED);
			} else {
				responseMsg.setResponseMessage("Device not found");
				responseMsg.setResponseCode(Response.Status.NOT_FOUND.toString());
				Response.status(Response.Status.NOT_FOUND);
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while enrollment of the device.";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return responseMsg;
	}

	@PUT
	@Path("{deviceId}")
	public Message modifyEnrollment(@PathParam("deviceId") String id,
									org.wso2.carbon.device.mgt.common.Device device)
			throws AndroidAgentException {
		String msg;
		boolean result;
		Message responseMsg = new Message();
		try {
			device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			result = AndroidAPIUtils.getDeviceManagementService().modifyEnrollment(device);
			if (result) {
				responseMsg.setResponseMessage("Device enrollment has updated successfully");
				responseMsg.setResponseCode(Response.Status.ACCEPTED.toString());
				Response.status(Response.Status.ACCEPTED);
			} else {
				responseMsg.setResponseMessage("Device not found for enrollment");
				responseMsg.setResponseCode(Response.Status.NOT_MODIFIED.toString());
				Response.status(Response.Status.NOT_MODIFIED);
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying enrollment of the device";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return responseMsg;
	}

	@DELETE
	@Path("{deviceId}")
	public Message disEnrollDevice(@PathParam("deviceId") String id) throws AndroidAgentException {
		Message responseMsg = new Message();
		boolean result;
		String msg;
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);

		try {
			result = AndroidAPIUtils.getDeviceManagementService().disenrollDevice(deviceIdentifier);
			if (result) {
				responseMsg.setResponseMessage("Device has removed successfully");
				responseMsg.setResponseCode(Response.Status.ACCEPTED.toString());
				Response.status(Response.Status.ACCEPTED);
			} else {
				responseMsg.setResponseMessage("Device not found");
				responseMsg.setResponseCode(Response.Status.NOT_FOUND.toString());
				Response.status(Response.Status.NOT_FOUND);
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while dis enrolling the device";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return responseMsg;
	}
}
