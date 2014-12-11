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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

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
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
			ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			dmService = (DeviceManagementService) ctx
					.getOSGiService(DeviceManagementService.class, null);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		Device device = AndroidAPIUtils.convertToDeviceObject(jsonPayload);
		try {
			if(dmService!=null){
				result = dmService.enrollDevice(device);
				status = 1;
			}else{
				status = -1;
				msg = "Device Manager service not available";
			}

		} catch (DeviceManagementException e) {
			msg = "Error occurred while enrolling the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(201).entity("Registration Successful").build();
				}
				break;
			case -1:
				return Response.status(500).entity(msg).build();
		}
		return Response.status(400).entity("Registration Failed").build();
	}

	@GET
	@Path("{id}")
	public Response isEnrolled(@PathParam("id") String id) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
			ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			dmService = (DeviceManagementService) ctx
					.getOSGiService(DeviceManagementService.class, null);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		try {
			if(dmService!=null){
				result = dmService.isEnrolled(deviceIdentifier);
				status = 1;
			}else{
				status = -1;
				msg = "Device Manager service not available";
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while checking enrollment of the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(200).entity(result).build();
				}
				break;
			case -1:
				return Response.status(500).entity(msg).build();
		}
		return Response.status(404).entity(result).build();
	}

	@PUT
	@Path("{id}")
	public Response modifyEnrollment(@PathParam("id") String id,String jsonPayload) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
			ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			dmService = (DeviceManagementService) ctx
					.getOSGiService(DeviceManagementService.class, null);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		Device device = AndroidAPIUtils.convertToDeviceObject(jsonPayload);
		try {
			if(dmService!=null){
				result = dmService.modifyEnrollment(device);
				status = 1;
			}else{
				status = -1;
				msg = "Device Manager service not available";
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying enrollment of the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(200).entity("Device information modified").build();
				}
				break;
			case -1:
				return Response.status(500).entity(msg).build();
		}
		return Response.status(400).entity("Update enrollment failed").build();
	}

	@DELETE
	@Path("{id}")
	public Response disenrollDevice(@PathParam("id") String id) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
			ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			dmService = (DeviceManagementService) ctx
					.getOSGiService(DeviceManagementService.class, null);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		try {
			if(dmService!=null){
				result = dmService.disenrollDevice(deviceIdentifier);
				status = 1;
			}else{
				status = -1;
				msg = "Device Manager service not available";
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while disenrolling the device";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(200).entity(result).build();
				}
				break;
			case -1:
				return Response.status(500).entity(msg).build();
		}
		return Response.status(404).entity("Device not found").build();
	}
}
