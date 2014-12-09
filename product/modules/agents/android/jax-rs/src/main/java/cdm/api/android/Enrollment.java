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

import cdm.api.android.util.AndroidAPIUtil;
import com.google.gson.JsonObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Android Device Enrollment REST-API implementation.
 */
@Path("/enrollment")
public class Enrollment {

	private static Log log = LogFactory.getLog(Enrollment.class);
	@POST
	@Consumes("application/json")
	public Response enrollDevice() {
		JsonObject result = new JsonObject();
		result.addProperty("senderId","jwwfowrjwqporqwrpqworpq");
		CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
		DeviceManagementService dmService = (DeviceManagementService) context.getOSGiService(DeviceManagementService.class,null);
		Device device = AndroidAPIUtil.convertToDeviceDTO(result);
		try {
			dmService.enrollDevice(null);
		} catch (DeviceManagementException e) {
			String msg = "Error occurred while enrolling the device";
			log.error(msg, e);
		}
		return Response.status(201).entity("Registration Successful").build();
	}

	@GET
	@Path("{id}")
	public String isEnrolled(@PathParam("id") String id) {
		CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
		DeviceManagementService dmService = (DeviceManagementService) context.getOSGiService(DeviceManagementService.class,null);
		try {
			Device device = AndroidAPIUtil.convertToDeviceDTO(id);
			dmService.isRegistered(null);
		} catch (DeviceManagementException e) {
			e.printStackTrace();
		}
		return "true";
	}

	@PUT
	@Consumes("application/json")
	@Path("{id}")
	public Response modifyEnrollment(@PathParam("id") String id) {
		CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
		DeviceManagementService dmService = (DeviceManagementService) context.getOSGiService(DeviceManagementService.class,null);
		try {
			dmService.isRegistered(null);
		} catch (DeviceManagementException e) {
			e.printStackTrace();
		}
		return Response.status(201).entity("Registration Successful").build();
	}

	@DELETE
	@Path("{id}")
	public Response disenrollDevice(@PathParam("id") String id) {
		CarbonContext context = CarbonContext.getThreadLocalCarbonContext();
		DeviceManagementService dmService = (DeviceManagementService) context.getOSGiService(DeviceManagementService.class,null);
		try {
			dmService.isRegistered(null);
		} catch (DeviceManagementException e) {
			e.printStackTrace();
		}
		return Response.status(201).entity("Registration Successful").build();
	}
}
