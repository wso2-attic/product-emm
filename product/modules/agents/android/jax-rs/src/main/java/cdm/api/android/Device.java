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

import com.google.gson.JsonObject;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Android Device Management REST-API implementation.
 */
public class Device {

	@GET
	public String getAllDevices() {
		return "License Agreement";
	}

	@GET
	@Path("{id}")
	public String getDevice(@PathParam("id") String id) {
		return "License Agreement";
	}

	@PUT
	@Path("{id}")
	public Response updateDevice(@PathParam("id") String id) {
		return Response.status(201).entity("Registration Successful").build();
	}
}
