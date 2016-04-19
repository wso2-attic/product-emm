/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.MDMAppConstants;
import org.wso2.carbon.mdm.api.util.ResponsePayload;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Device related operations
 */
public class MobileDevice {
    private static Log log = LogFactory.getLog(MobileDevice.class);

    /**
     * Get all devices. We have to use accept all the necessary query parameters sent by datatable.
     * Hence had to put lot of query params here.
     *
     * @return Device List
     * @throws MDMAPIException
     */
    @GET
    public Object getAllDevices(@QueryParam("type") String type, @QueryParam("user") String user,
                                @QueryParam("role") String role, @QueryParam("status") EnrolmentInfo.Status status,
                                @QueryParam("start") int startIdx, @QueryParam("length") int length,
                                @QueryParam("device-name") String deviceName,
                                @QueryParam("ownership") EnrolmentInfo.OwnerShip ownership
                                ) throws MDMAPIException {
        try {
            DeviceManagementProviderService service = MDMAPIUtils.getDeviceManagementService();
            //Length > 0 means this is a pagination request.
            if (length > 0) {
                PaginationRequest paginationRequest = new PaginationRequest(startIdx, length);
                paginationRequest.setDeviceName(deviceName);
                paginationRequest.setOwner(user);
                if (ownership != null) {
                    paginationRequest.setOwnership(ownership.toString());
                }
                if (status != null) {
                    paginationRequest.setStatus(status.toString());
                }
                paginationRequest.setDeviceType(type);
                return service.getAllDevices(paginationRequest);
            }

            List<Device> allDevices;
            if ((type != null) && !type.isEmpty()) {
                allDevices = service.getAllDevices(type);
            } else if ((user != null) && !user.isEmpty()) {
                allDevices = service.getDevicesOfUser(user);
            } else if ((role != null) && !role.isEmpty()) {
                allDevices = service.getAllDevicesOfRole(role);
            } else if (status != null) {
                allDevices = service.getDevicesByStatus(status);
            } else if (deviceName != null) {
                allDevices = service.getDevicesByName(deviceName);
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
     * Fetch device details for a given device type and device Id.
     *
     * @return Device wrapped inside Response
     * @throws MDMAPIException
     */
    @GET
    @Path("view")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getDevice(@QueryParam("type") String type,
                              @QueryParam("id") String id) throws MDMAPIException {
        DeviceIdentifier deviceIdentifier = MDMAPIUtils.instantiateDeviceIdentifier(type, id);
        DeviceManagementProviderService deviceManagementProviderService = MDMAPIUtils.getDeviceManagementService();
        Device device;
        try {
            device = deviceManagementProviderService.getDevice(deviceIdentifier);
        } catch (DeviceManagementException e) {
            String error = "Error occurred while fetching the device information.";
            log.error(error, e);
            throw new MDMAPIException(error, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        if (device == null) {
            responsePayload.setStatusCode(HttpStatus.SC_NOT_FOUND);
            responsePayload.setMessageFromServer("Requested device by type: " +
                    type + " and id: " + id + " does not exist.");
            return Response.status(HttpStatus.SC_NOT_FOUND).entity(responsePayload).build();
        } else {
            responsePayload.setStatusCode(HttpStatus.SC_OK);
            responsePayload.setMessageFromServer("Sending Requested device by type: " + type + " and id: " + id + ".");
            responsePayload.setResponseContent(device);
            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
        }
    }

    /**
     * Fetch Android device details of a given user.
     *
     * @param user         User Name
     * @param tenantDomain tenant domain
     * @return Device
     * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
     */
    @GET
    @Path("user/{user}/{tenantDomain}")
    public List<Device> getDeviceByUser(@PathParam("user") String user,
                                        @PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {
        try {
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        privilegedCarbonContext.setTenantDomain(tenantDomain);
        List<Device> devices;

            devices = MDMAPIUtils.getDeviceManagementService().getDevicesOfUser(user);
            if (devices == null) {
                Response.status(Response.Status.NOT_FOUND);
            }
            return devices;
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices list of given user.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

	/**
     * Will return device list for user list or role list
     *
     * @param types Type list which for devices to be retrieved. ex: user list or role list
     * @param typeName Type name which for devices to be retrieved ex: user or role
     * @param tenantDomain Tenant domain
     * @return Device list which retrieved for user set or role set
     * @throws MDMAPIException
     */
    @GET
    @Path("{typeName}/{tenantDomain}")
    public List<Device> getDevicesOfTypes(@QueryParam("types") List<String> types, @PathParam("typeName") String typeName,
                                          @PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {
        List<Device> devicesOfType;
        Set<Device> devicesOfTypes = new HashSet<>();
        try {
            for(String type : types){
                if(MDMAppConstants.USERS.equals(typeName)) {
                    devicesOfType = MDMAPIUtils.getDeviceManagementService().getDevicesOfUser(type);
                    devicesOfTypes.addAll(devicesOfType);
                }
                else if(MDMAppConstants.ROLES.equals(typeName)) {
                    devicesOfType = MDMAPIUtils.getDeviceManagementService().getAllDevicesOfRole(type);
                    devicesOfTypes.addAll(devicesOfType);
                }
            }

            if (devicesOfTypes.isEmpty()) {
                Response.status(Response.Status.NOT_FOUND);
            }
            return new ArrayList<>(devicesOfTypes);
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while fetching the devices list for users.";
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
     * @param deviceName   Device name
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

	/**
	 * Get the list of available device types.
	 *
	 * @return list of device types.
	 * @throws MDMAPIException If some unusual behaviour is observed while fetching the device list
	 */
	@GET
	@Path("types")
	public List<DeviceType> getDeviceTypes() throws MDMAPIException {

		List<DeviceType> deviceTypes;
		try {
			deviceTypes = MDMAPIUtils.getDeviceManagementService().getAvailableDeviceTypes();
		} catch (DeviceManagementException e) {
			String msg = "Error occurred while fetching the list of device types.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
		return deviceTypes;
	}
}