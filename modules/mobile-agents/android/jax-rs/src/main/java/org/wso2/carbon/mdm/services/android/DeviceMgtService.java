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

import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.license.mgt.License;
import org.wso2.carbon.mdm.services.android.bean.wrapper.DeviceInfo;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class DeviceMgtService {

	private static Log log = LogFactory.getLog(DeviceMgtService.class);

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
			return devices;
		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the device list.";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}
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
			return device;
		} catch (DeviceManagementException deviceMgtEx) {
			msg = "Error occurred while fetching the device information.";
			log.error(msg, deviceMgtEx);
			throw new AndroidAgentException(msg, deviceMgtEx);
		}
	}

    /**
     * Update Android device details of given device id.
     *
     * @param id     Device Id
     * @param deviceInfo Device Details
     * @return Message
     * @throws AndroidAgentException
     */
    @PUT
    @Path("{id}")
    public Message updateDevice(@PathParam("id") String id, DeviceInfo deviceInfo) throws AndroidAgentException {
        String msg;
        Message responseMessage = new Message();
        boolean result;
        Device device = deviceInfo.getDevice();
        try {
            device.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
            result = AndroidAPIUtils.getDeviceManagementService().updateDeviceInfo(device, deviceInfo.getApplicationList());
            if (result) {
                Response.status(Response.Status.ACCEPTED);
                responseMessage.setResponseMessage("Device information has modified successfully.");
            } else {
                Response.status(Response.Status.NOT_MODIFIED);
                responseMessage.setResponseMessage("Device not found for the update.");
            }
            return responseMessage;
        } catch (DeviceManagementException e) {
            msg = "Error occurred while modifying the device information.";
            log.error(msg, e);
            throw new AndroidAgentException(msg, e);
        }
    }

	@GET
	@Path("license")
	@Produces("text/plain")
	public String getLicense() throws AndroidAgentException {
		String msg;
		License license;
		/*try {
			license =
					AndroidAPIUtils.getLicenseManagerService().getLicense(
									DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID,
									DeviceManagementConstants.LanguageCodes.LANGUAGE_CODE_ENGLISH_US);

		} catch (LicenseManagementException e) {
			msg = "Error occurred while retrieving the license configured for Android device enrolment";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		}*/
		return "License";
	}

}
