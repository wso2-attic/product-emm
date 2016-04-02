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
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationManagementException;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Android Device Management REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@WebService
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class DeviceManagementService {

	private static Log log = LogFactory.getLog(DeviceManagementService.class);

	/**
	 * Get all devices.Returns list of Android devices registered in MDM.
	 *
	 * @return Device List
	 * @throws org.wso2.carbon.mdm.services.android.exception.AndroidAgentException
	 */
	@GET
	public List<org.wso2.carbon.device.mgt.common.Device> getAllDevices()
			throws AndroidAgentException {
		String msg;
		List<org.wso2.carbon.device.mgt.common.Device> devices;

		try {
			devices = AndroidAPIUtils.getDeviceManagementService().
					getAllDevices(DeviceManagementConstants.MobileDeviceTypes.
							              MOBILE_DEVICE_TYPE_ANDROID);
		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device list.";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return devices;
	}

	/**
	 * Fetch Android device details of a given device Id.
	 *
	 * @param id Device Id
	 * @return Device
	 * @throws org.wso2.carbon.mdm.services.android.exception.AndroidAgentException
	 */
	@GET
	@Path("{id}")
	public org.wso2.carbon.device.mgt.common.Device getDevice(@PathParam("id") String id)
			throws AndroidAgentException {

		String msg;
		org.wso2.carbon.device.mgt.common.Device device;

		try {
			DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
			device = AndroidAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
			if (device == null) {
				Response.status(Response.Status.NOT_FOUND);
			}
		} catch (DeviceManagementException deviceMgtEx) {
			msg = "Error occurred while fetching the device information.";
			log.error(msg, deviceMgtEx);
			throw new AndroidAgentException(msg, deviceMgtEx);
		}
		return device;
	}

	/**
	 * Update Android device details of given device id.
	 *
	 * @param id     Device Id
	 * @param device Device Details
	 * @return Message
	 * @throws AndroidAgentException
	 */
	@PUT
	@Path("{id}")
	public Message updateDevice(@PathParam("id") String id, Device device)
			throws AndroidAgentException {
		String msg;
		Message responseMessage = new Message();
		DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
		deviceIdentifier.setId(id);
		deviceIdentifier
				.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		boolean result;
		try {
			device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			result = AndroidAPIUtils.getDeviceManagementService()
			                        .updateDeviceInfo(deviceIdentifier, device);
			if (result) {
				Response.status(Response.Status.ACCEPTED);
				responseMessage.setResponseMessage("Device information has modified successfully.");
			} else {
				Response.status(Response.Status.NOT_MODIFIED);
				responseMessage.setResponseMessage("Device not found for the update.");
			}
		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying the device information.";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return responseMessage;
	}

	@GET
	@Path("license")
	@Produces("text/html")
	public String getLicense() throws AndroidAgentException {
		License license;

		try {
			license =
					AndroidAPIUtils.getDeviceManagementService().getLicense(
							DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID,
							DeviceManagementConstants.LanguageCodes.LANGUAGE_CODE_ENGLISH_US);
		} catch (DeviceManagementException e) {
			String msg = "Error occurred while retrieving the license configured for Android device enrolment";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
		return (license == null) ? null : license.getText();
	}

}
