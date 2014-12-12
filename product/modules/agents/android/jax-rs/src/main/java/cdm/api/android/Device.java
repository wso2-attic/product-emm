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
import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Android Device Management REST-API implementation.
 */
public class Device {

	private static Log log = LogFactory.getLog(Device.class);

	@GET
	public Response getAllDevices() {
		List<org.wso2.carbon.device.mgt.common.Device> result = null;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		try {
			if (dmService != null) {
				result = dmService.getAllDevices(
						DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}

		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device list";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if(result!=null){
					String response = new Gson().toJson(result);
					return Response.status(HttpStatus.SC_OK).entity(response).build();
				}
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_NOT_FOUND).entity("Unable to fetch device list").build();
	}

	@GET
	@Path("{id}")
	public Response getDevice(@PathParam("id") String id) {
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		org.wso2.carbon.device.mgt.common.Device device =
				new org.wso2.carbon.device.mgt.common.Device();
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		try {
			if (dmService != null) {
				device = dmService.getDevice(deviceIdentifier);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}

		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device information";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if(device!=null) {
					String response = new Gson().toJson(device);
					return Response.status(HttpStatus.SC_OK).entity(response).build();
				}
				break;
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_NOT_FOUND).entity("Unable to fetch device information").build();
	}

	@PUT
	@Path("{id}")
	public Response updateDevice(@PathParam("id") String id, String jsonPayload) {
		boolean result = false;
		int status = 0;
		String msg = "";
		DeviceManagementService dmService;
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		org.wso2.carbon.device.mgt.common.Device device =
				AndroidAPIUtils.convertToDeviceObject(jsonPayload);
		try {
			if (dmService != null) {
				result = dmService.updateDeviceInfo(device);
				status = 1;
			} else {
				status = -1;
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying the device information";
			log.error(msg, e);
			status = -1;
		}
		switch (status) {
			case 1:
				if (result) {
					return Response.status(HttpStatus.SC_OK).entity("Device has modified").build();
				}
				break;
			case -1:
				return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(msg).build();
		}
		return Response.status(HttpStatus.SC_NOT_MODIFIED).entity("Update device has failed").build();
	}
}
