package org.wso2.carbon.mdm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.user.common.Role;
import org.wso2.carbon.device.mgt.user.common.UserManagementException;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
public class User {
	private static Log log = LogFactory.getLog(MobileDevice.class);

	/**
	 * Get a list of devices based on the username.
	 *
	 * @param username Username of the device owner.
	 * @return A list of devices.
	 * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("/{tenantDomain}/{username}/devices")
	public List<Device> getAllDeviceOfUser(@PathParam("username") String username,
	                                       @PathParam("tenantDomain") String tenantDomain)
			throws MDMAPIException {
		DeviceManagementService dmService;
		try {
			dmService = MDMAPIUtils.getDeviceManagementService(tenantDomain);
			return dmService.getDeviceListOfUser(username);
		} catch (DeviceManagementException e) {
			String errorMsg = "Device management error";
			log.error(errorMsg, e);
			throw new MDMAPIException(errorMsg, e);
		}
	}

	@GET
	public List<org.wso2.carbon.device.mgt.user.common.User> getAllUsers() throws MDMAPIException {
		String msg;
		List<org.wso2.carbon.device.mgt.user.common.User> users;

		try {
			users = MDMAPIUtils.getUserManagementService().getUsersForTenant(-1234);
			return users;
		} catch (UserManagementException e) {
			msg = "User management service error.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@GET
	@Path("{type}/{id}")
	public List<Role> getUserRoles() throws MDMAPIException {
		String msg;
		List<Role> roles;
		try {
			roles = MDMAPIUtils.getUserManagementService().getRolesForTenant(-1234);
			return roles;
		} catch (UserManagementException e) {
			msg = "User management service error.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@GET
	@Path("count/{tenantDomain}")
	public int getUserCount(@PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {
		int userCount = 0;
		String msg;
		List<org.wso2.carbon.device.mgt.user.common.User> users;
		int tenantId = MDMAPIUtils.getTenantId(tenantDomain);
		try {
			users = MDMAPIUtils.getUserManagementService().getUsersForTenant(tenantId);
			if (users != null) {
				userCount = users.size();
			}
			return userCount;
		} catch (UserManagementException e) {
			msg = "Error occurred while retrieving users count.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

}
