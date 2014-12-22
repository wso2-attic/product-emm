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

import cdm.api.android.util.AndroidAPIUtils;
import cdm.api.android.util.AndroidConstants;
import cdm.api.android.util.Message;
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
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Device {

	private static Log log = LogFactory.getLog(Device.class);

	@GET
	public List<org.wso2.carbon.device.mgt.common.Device> getAllDevices() {
		List<org.wso2.carbon.device.mgt.common.Device> devices = null;
		String msg = "";
		DeviceManagementService dmService;

		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		try {
			if (dmService != null) {
				devices = dmService.getAllDevices(
						DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
				Response.status(HttpStatus.SC_OK);
			} else {
				Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device list.";
			log.error(msg, e);
			Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		return devices;
	}

	@GET
	@Path("{id}")
	public org.wso2.carbon.device.mgt.common.Device getDevice(@PathParam("id") String id) {
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
				if (device == null) {
					Response.status(HttpStatus.SC_NOT_FOUND);
				}

			} else {
				Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			}

		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device information.";
			log.error(msg, e);
			Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		return device;
	}

	@PUT
	@Path("{id}")
	public Message updateDevice(@PathParam("id") String id,
	                            org.wso2.carbon.device.mgt.common.Device device) {
		boolean result = false;
		String msg = "";
		DeviceManagementService dmService;
		Message responseMessage = new Message();

		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
		try {
			if (dmService != null) {
				device.setType(
						DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
				result = dmService.updateDeviceInfo(device);
				if (result) {
					Response.status(HttpStatus.SC_OK);
					responseMessage.setResponseMessage("Device information has modified successfully.");
				} else {
					Response.status(HttpStatus.SC_NOT_MODIFIED);
					responseMessage.setResponseMessage("Update device has failed.");
				}
			} else {
				Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
				msg = AndroidConstants.Messages.DEVICE_MANAGER_SERVICE_NOT_AVAILABLE;
				responseMessage.setResponseMessage(msg);
			}

		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying the device information.";
			log.error(msg, e);
			Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			responseMessage.setResponseMessage(msg);

		}
		return responseMessage;
	}

	@GET
	@Path("/operations/{id}")
	public org.wso2.carbon.device.mgt.common.Device getOperations(@PathParam("id") String id) {
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
				if (device == null) {
					Response.status(HttpStatus.SC_NOT_FOUND);
				}

			} else {
				Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			}

		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device information.";
			log.error(msg, e);
			Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		return device;
	}

}
