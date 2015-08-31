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
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;
import org.wso2.carbon.mdm.beans.UserWrapper;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private static Log log = LogFactory.getLog(MobileDevice.class);

    @POST
    @Produces({MediaType.APPLICATION_JSON})
    public Response addUser(UserWrapper userWrapper) throws MDMAPIException {
        UserStoreManager usm = MDMAPIUtils.getUserStoreManager();
        ResponsePayload responsePayload = new ResponsePayload();
        try {
            if (usm.isExistingUser(userWrapper.getUsername())) {
                // if user already exists
                responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
                responsePayload.setMessageFromServer("User already exists.");
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
            } else {
                String initialUserPassword = generateInitialUserPassword();
                Map<String, String> defaultUserClaims = buildDefaultUserClaims(userWrapper.getFirstname(),
                        userWrapper.getLastname(), userWrapper.getEmailAddress());
                // calling addUser method of carbon user api
                usm.addUser(userWrapper.getUsername(), initialUserPassword, userWrapper.getRoles(),
                        defaultUserClaims, null);
                // if addUser method successfully executed without no exception
                responsePayload.setStatusCode(HttpStatus.SC_CREATED);
                responsePayload.setMessageFromServer("User was successfully added.");
                return Response.status(HttpStatus.SC_CREATED).entity(responsePayload).build();
            }
        } catch (UserStoreException e) {
            String errorMsg = "Exception in trying to add user.";
            log.error(errorMsg, e);
            throw new MDMAPIException(errorMsg, e);
        }
    }

    private String generateInitialUserPassword() {
        int passwordLength = 6;
        //defining the pool of characters to be used for initial password generation
        String lowerCaseCharset = "abcdefghijklmnopqrstuvwxyz";
        String upperCaseCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numericCharset = "0123456789";

        String totalCharset = lowerCaseCharset + upperCaseCharset + numericCharset;
        int totalCharsetLength = totalCharset.length();

        String initialUserPassword = "";
        for (int i = 0; i < passwordLength; i++) {
            initialUserPassword += totalCharset.charAt((int) (Math.random() * totalCharsetLength));
        }
        if (log.isDebugEnabled()) {
            log.debug("Initial user password is created for new user: " + initialUserPassword);
        }
        return initialUserPassword;
    }

    private Map<String, String> buildDefaultUserClaims (String firstname, String lastname, String emailAddress) {
        Map<String, String> defaultUserClaims = new HashMap<String, String>();
        defaultUserClaims.put("http://wso2.org/claims/givenname", firstname);
        defaultUserClaims.put("http://wso2.org/claims/lastname", lastname);
        defaultUserClaims.put("http://wso2.org/claims/emailaddress", emailAddress);
        if (log.isDebugEnabled()) {
            log.debug("Default claim map is created for new user: " + defaultUserClaims.toString());
        }
        return defaultUserClaims;
    }

    /**
     * Get a list of devices based on the username.
     *
     * @param username Username of the device owner.
     * @return A list of devices.
     * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
     *
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/{tenantDomain}/{username}/devices")
    public List<Device> getAllDeviceOfUser(@PathParam("username") String username,
                                           @PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {
        DeviceManagementProviderService dmService;
        try {
            dmService = MDMAPIUtils.getDeviceManagementService(tenantDomain);
            return dmService.getDevicesOfUser(username);
        } catch (DeviceManagementException e) {
            String errorMsg = "Device management error";
            log.error(errorMsg, e);
            throw new MDMAPIException(errorMsg, e);
        }
    }

    @GET
    public List<String> getAllUsers() throws MDMAPIException {
        try {
            String[] users = MDMAPIUtils.getUserStoreManager().listUsers("", -1);
            return Arrays.asList(users);
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving the list of users";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

    @GET
    @Path("{type}/{id}")
    public List<String> getUserRoles() throws MDMAPIException {
        try {
            String[] roles = MDMAPIUtils.getUserStoreManager().getRoleNames();
            return Arrays.asList(roles);
        } catch (UserStoreException e) {
            throw new MDMAPIException("Error occurred while retrieving list of roles created within the current " +
                    "tenant", e);
        }
    }

    //TODO : Refactor the users/count API to remove tenant-domain parameter
    @GET
    @Path("count/{tenantDomain}")
    public int getUserCount(@PathParam("tenantDomain") String tenantDomain) throws MDMAPIException {
        try {
            String[] users = MDMAPIUtils.getUserStoreManager().listUsers("", -1);
            if (users == null) {
                return 0;
            }
            return users.length;
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving the list of users that exist within the current tenant";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }

}
