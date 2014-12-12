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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.HttpStatus;
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
public class Enrollment {

	private static Log log = LogFactory.getLog(Enrollment.class);

	@POST
	public Response enrollDevice(String jsonPayload) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		Device device = AndroidAPIUtils.convertToDeviceObject(jsonPayload);
		try {
			if (dmService != null) {
				result = dmService.enrollDevice(device);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while enrolling the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(HttpStatus.SC_CREATED).entity("Device enrollment has succeeded").build();
				}
				break;
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_BAD_REQUEST).entity("Device enrollment has Failed").build();
	}

	@GET
	@Path("{id}")
	public Response isEnrolled(@PathParam("id") String id) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		try {
			if (dmService != null) {
				result = dmService.isEnrolled(deviceIdentifier);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while checking enrollment of the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(HttpStatus.SC_OK).entity(result).build();
				}
				break;
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_NOT_FOUND).entity(result).build();
	}

	@PUT
	@Path("{id}")
	public Response modifyEnrollment(@PathParam("id") String id, String jsonPayload) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		Device device = AndroidAPIUtils.convertToDeviceObject(jsonPayload);
		try {
			if (dmService != null) {
				result = dmService.modifyEnrollment(device);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying enrollment of the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(HttpStatus.SC_OK).entity("Enrollment information has modified").build();
				}
				break;
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_NOT_MODIFIED).entity("Update enrollment has failed").build();
	}

	@DELETE
	@Path("{id}")
	public Response disenrollDevice(@PathParam("id") String id) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		try {
			if (dmService != null) {
				result = dmService.disenrollDevice(deviceIdentifier);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while disenrolling the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(HttpStatus.SC_OK).entity(result).build();
				}
				break;
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_NOT_FOUND).entity("Device not found").build();
	}
}
