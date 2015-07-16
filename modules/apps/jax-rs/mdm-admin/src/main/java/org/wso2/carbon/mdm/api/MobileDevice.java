/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.mdm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EnrolmentInfo;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Device related operations
 */
@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})
public class MobileDevice {
    private static Log log = LogFactory.getLog(MobileDevice.class);

    /**
     * Get all devices
     *
     * @return Device List
     * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
     *
     */
    @GET
    public List<Device> getAllDevices(@QueryParam("type") String type, @QueryParam("user") String user,
                                      @QueryParam("role") String role, @QueryParam("status") EnrolmentInfo.Status status) throws MDMAPIException {
        List<Device> devices;
        try {
            DeviceManagementProviderService service = MDMAPIUtils.getDeviceManagementService();
            List<Device> allDevices;
            if (type != null) {
                allDevices = service.getAllDevices(type);
            } else if (user != null) {
                allDevices = service.getDevicesOfUser(user);
            } else if (role != null){
                allDevices = service.getAllDevicesOfRole(role);
            } else if (status != null) {
				allDevices = service.getDevicesByStatus(status);
			} else {
                allDevices = service.getAllDevices();
            }
            return allDevices;
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the device list.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

    /**
     * Fetch Android device details of a given device Id.
     *
     * @param id   Device Id
     * @param type Device Type
     * @return Device
     * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
     *
     */
    @GET
    @Path("{type}/{id}")
    public org.wso2.carbon.device.mgt.common.Device getDevice(
            @PathParam("id") String id, @PathParam("type") String type) throws MDMAPIException {
        String msg;
        Device device;
        try {
            DeviceIdentifier deviceIdentifier = MDMAPIUtils.convertToDeviceIdentifierObject(id, type);
            device = MDMAPIUtils.getDeviceManagementService().getDevice(deviceIdentifier);
            if (device == null) {
                Response.status(Response.Status.NOT_FOUND);
            }
            return device;
        } catch (DeviceManagementException e) {
            msg = "Error occurred while fetching the device information.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

	/**
	 * Fetch Android device details of a given user.
	 *
	 * @param user   User Name
	 * @param tenantDomain tenant domain
	 * @return Device
	 * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
	 *
	 */
	@GET
	@Path("user/{user}/{tenantDomain}")
	public List<Device> getDeviceByUser(@PathParam("user") String user,
				@PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {
		String msg;
		List<Device> devices;
		try {
			devices = MDMAPIUtils.getDeviceManagementService().getDevicesOfUser(user);
			if (devices == null) {
				Response.status(Response.Status.NOT_FOUND);
			}
			return devices;
		} catch (DeviceManagementException e) {
			msg = "Error occurred while fetching the devices list of given user.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	/**
	 * Get current device count
	 *
	 * @return device count
	 * @throws MDMAPIException
	 */
	@GET
	@Path("count")
	public int getDeviceCount() throws MDMAPIException {
		try {
			return MDMAPIUtils.getDeviceManagementService().getDeviceCount();
		} catch (DeviceManagementException e) {
			String msg = "Error occurred while fetching the device count.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	/**
	 * Get the list of devices that matches with the given name.
	 *
	 * @param deviceName Device name
	 * @param tenantDomain Callee tenant domain
	 * @return list of devices.
	 * @throws MDMAPIException If some unusual behaviour is observed while fetching the device list
	 */
	@GET
	@Path("name/{name}/{tenantDomain}")
	public List<Device> getDevicesByName(@PathParam("name") String deviceName,
				@PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {

		List<org.wso2.carbon.device.mgt.common.Device> devices;
		try {
			devices = MDMAPIUtils.getDeviceManagementService().getDevicesByName(deviceName);
		} catch (DeviceManagementException e) {
			String msg = "Error occurred while fetching the devices list of device name.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
		return devices;
	}
}