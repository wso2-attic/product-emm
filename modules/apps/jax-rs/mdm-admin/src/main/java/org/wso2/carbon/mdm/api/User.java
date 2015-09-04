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
import org.wso2.carbon.device.mgt.common.EmailMessageProperties;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;
import org.wso2.carbon.mdm.beans.UserWrapper;
import org.wso2.carbon.mdm.util.Constants;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

/**
 * This class represents the JAX-RS services of User related functionality.
 */
public class User {

	private static Log log = LogFactory.getLog(User.class);

	/**
	 * Method to add user to emm-user-store.
	 *
	 * @param userWrapper Wrapper object representing input json payload
	 * @return {Response} Status of the request wrapped inside Response object
	 * @throws MDMAPIException
	 */
	@POST
	@Consumes({ MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response addUser(UserWrapper userWrapper) throws MDMAPIException {
		UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
		ResponsePayload responsePayload = new ResponsePayload();
		try {
			if (userStoreManager.isExistingUser(userWrapper.getUsername())) {
				// if user already exists
				if (log.isDebugEnabled()) {
					log.debug("User by username: " + userWrapper.getUsername() +
					          " already exists. Therefore, request made to add user was refused.");
				}
				// returning response with bad request state
				responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				responsePayload.setMessageFromServer("User by username: " + userWrapper.getUsername() +
						                      " already exists. Therefore, request made to add user was refused.");
				return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
			} else {
				String initialUserPassword = generateInitialUserPassword();
				Map<String, String> defaultUserClaims = buildDefaultUserClaims(userWrapper.getFirstname(),
						                       userWrapper.getLastname(), userWrapper.getEmailAddress());
				// calling addUser method of carbon user api
				userStoreManager.addUser(userWrapper.getUsername(), initialUserPassword,
                        userWrapper.getRoles(), defaultUserClaims, null);
                // invite newly added user to enroll device
				inviteNewlyAddedUserToEnrollDevice(userWrapper.getUsername());
				// Outputting debug message upon successful addition of user
				if (log.isDebugEnabled()) {
					log.debug("User by username: " + userWrapper.getUsername() + " was successfully added.");
				}
				// returning response with success state
				responsePayload.setStatusCode(HttpStatus.SC_CREATED);
				responsePayload.setMessageFromServer("User by username: " + userWrapper.getUsername() +
						                      " was successfully added.");
				return Response.status(HttpStatus.SC_CREATED).entity(responsePayload).build();
			}
		} catch (UserStoreException e) {
			String errorMsg = "Exception in trying to add user by username: " + userWrapper.getUsername();
			log.error(errorMsg, e);
			throw new MDMAPIException(errorMsg, e);
		} catch (DeviceManagementException e) {
			String errorMsg = "Exception in trying to add user by username: " + userWrapper.getUsername();
			log.error(errorMsg, e);
			throw new MDMAPIException(errorMsg, e);
		}
	}

	/**
	 * Private method to be used by addUser() to
	 * generate an initial user password for a user.
	 * This will be the password used by a user for his initial login to the system.
	 *
	 * @return {string} Initial User Password
	 */
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

	/**
	 * Method to build default user claims.
	 *
	 * @param firstname    First name of the user
	 * @param lastname     Last name of the user
	 * @param emailAddress Email address of the user
	 * @return {Object} Default user claims to be provided
	 */
	private Map<String, String> buildDefaultUserClaims(String firstname, String lastname, String emailAddress) {
		Map<String, String> defaultUserClaims = new HashMap<String, String>();
		defaultUserClaims.put(Constants.USER_CLAIM_FIRST_NAME, firstname);
		defaultUserClaims.put(Constants.USER_CLAIM_LAST_NAME, lastname);
		defaultUserClaims.put(Constants.USER_CLAIM_EMAIL_ADDRESS, emailAddress);
		if (log.isDebugEnabled()) {
			log.debug("Default claim map is created for new user: " + defaultUserClaims.toString());
		}
		return defaultUserClaims;
	}

	/**
	 * Method to remove user from emm-user-store.
	 *
	 * @param username Username of the user
	 * @return {Response} Status of the request wrapped inside Response object
	 * @throws MDMAPIException
	 */
	@DELETE
	@Path("{username}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response removeUser(@PathParam("username") String username) throws MDMAPIException {
		UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
		ResponsePayload responsePayload = new ResponsePayload();
		try {
			if (userStoreManager.isExistingUser(username)) {
				// if user already exists, trying to remove user
				userStoreManager.deleteUser(username);
				// Outputting debug message upon successful removal of user
				if (log.isDebugEnabled()) {
					log.debug("User by username: " + username + " was successfully removed.");
				}
				// returning response with success state
				responsePayload.setStatusCode(HttpStatus.SC_OK);
				responsePayload.setMessageFromServer(
						"User by username: " + username + " was successfully removed.");
				return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
			} else {
				// Outputting debug message upon trying to remove non-existing user
				if (log.isDebugEnabled()) {
					log.debug("User by username: " + username + " does not exist for removal.");
				}
				// returning response with bad request state
				responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
				responsePayload.setMessageFromServer(
						"User by username: " + username + " does not exist for removal.");
				return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
			}
		} catch (UserStoreException e) {
			String errorMsg = "Exception in trying to remove user by username: " + username;
			log.error(errorMsg, e);
			throw new MDMAPIException(errorMsg, e);
		}
	}

	/**
	 * Get the list of all users with all user-related info.
	 *
	 * @return A list of users
	 * @throws MDMAPIException
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllUsers() throws MDMAPIException {
        if (log.isDebugEnabled()) {
            log.debug("Getting the list of users with all user-related information");
        }
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
		ArrayList<UserWrapper> userList= new ArrayList<UserWrapper>();
		try {
			String[] users = userStoreManager.listUsers("", -1);
            UserWrapper user;
			for (String username : users) {
				user = new UserWrapper();
				user.setUsername(username);
				user.setEmailAddress(getClaimValue(username, Constants.USER_CLAIM_EMAIL_ADDRESS));
				user.setFirstname(getClaimValue(username, Constants.USER_CLAIM_FIRST_NAME));
				user.setLastname(getClaimValue(username, Constants.USER_CLAIM_LAST_NAME));
				userList.add(user);
			}
		} catch (UserStoreException e) {
			String msg = "Error occurred while retrieving the list of users";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("All users were successfully retrieved. " +
                "Obtained user count: " + userList.size());
        responsePayload.setResponseContent(userList);
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
	}

    /**
     * Get the list of usernames in the system.
     *
     * @return A list of usernames
     * @throws MDMAPIException
     */
    @GET
    @Path("users-by-username")
    public Response getAllUsersByUsername() throws MDMAPIException {
        if (log.isDebugEnabled()) {
            log.debug("Getting the list of users by name");
        }
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        String[] users;
        try {
            users = userStoreManager.listUsers("", -1);
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving the list of users";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("All users by username were successfully retrieved. " +
                "Obtained user count: " + users.length);
        responsePayload.setResponseContent(Arrays.asList(users));
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    /**
     * Gets a claim-value from user-store.
     *
     * @param username Username of the user
     * @param claimUri required ClaimUri
     * @return A list of usernames
     * @throws MDMAPIException, UserStoreException
     */
    private String getClaimValue(String username, String claimUri) throws MDMAPIException, UserStoreException {
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        return userStoreManager.getUserClaimValue(username, claimUri, null);
    }

    /**
     * Method used to send an invitation email to a new user to enroll a device.
     *
     * @param username Username of the user
     * @throws MDMAPIException, UserStoreException, DeviceManagementException
     */
    private void inviteNewlyAddedUserToEnrollDevice(String username) throws
            MDMAPIException, UserStoreException, DeviceManagementException {
        if (log.isDebugEnabled()) {
            log.debug("Sending invitation mail to user by username: " + username);
        }
        DeviceManagementProviderService deviceManagementProviderService = MDMAPIUtils.getDeviceManagementService();
        EmailMessageProperties emailMessageProperties = new EmailMessageProperties();
        emailMessageProperties.setUserName(username);
        emailMessageProperties.setEnrolmentUrl("https://download-agent");
        emailMessageProperties.setFirstName(getClaimValue(username, Constants.USER_CLAIM_FIRST_NAME));
        emailMessageProperties.setPassword(generateInitialUserPassword());
        String[] mailAddress = new String[1];
        mailAddress[0] = getClaimValue(username, Constants.USER_CLAIM_EMAIL_ADDRESS);
        emailMessageProperties.setMailTo(mailAddress);
        deviceManagementProviderService.sendRegistrationEmail(emailMessageProperties);
    }

    /**
     * Method used to send an invitation email to a existing user to enroll a device.
     *
     * @param username Username of the user
     * @throws MDMAPIException
     */
    @POST
    @Path("{username}/email-invitation")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response inviteExistingUserToEnrollDevice(@PathParam("username") String username) throws MDMAPIException {
        if (log.isDebugEnabled()) {
            log.debug("Sending enrollment invitation mail to existing user by username: " + username);
        }
        DeviceManagementProviderService deviceManagementProviderService = MDMAPIUtils.getDeviceManagementService();
        try {
            EmailMessageProperties emailMessageProperties = new EmailMessageProperties();
            emailMessageProperties.setEnrolmentUrl("https://download-agent");
            emailMessageProperties.setFirstName(getClaimValue(username, Constants.USER_CLAIM_FIRST_NAME));
            emailMessageProperties.setUserName(username);
            String[] mailAddress = new String[1];
            mailAddress[0] = getClaimValue(username, Constants.USER_CLAIM_EMAIL_ADDRESS);
            emailMessageProperties.setMailTo(mailAddress);
            deviceManagementProviderService.sendEnrolmentInvitation(emailMessageProperties);
        } catch (UserStoreException e) {
            String errorMsg = "Exception in trying to invite user by username: " + username;
            log.error(errorMsg, e);
            throw new MDMAPIException(errorMsg, e);
        } catch (DeviceManagementException e) {
            String errorMsg = "Exception in trying to invite user by username: " + username;
            log.error(errorMsg, e);
            throw new MDMAPIException(errorMsg, e);
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("Email invitation was successfully sent to user by username:" + username);
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    /**
     * Get a list of devices based on the username.
     *
     * @param username Username of the device owner
     * @return A list of devices
     * @throws MDMAPIException
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/{tenantDomain}/{username}/devices")
    public List<Device> getAllDeviceOfUser(@PathParam("username") String username,
                                           @PathParam("tenantDomain") String tenantDomain)
            throws MDMAPIException {
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
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("{type}/{id}")
	public List<String> getUserRoles() throws MDMAPIException {
		try {
			String[] roles = MDMAPIUtils.getUserStoreManager().getRoleNames();
			return Arrays.asList(roles);
		} catch (UserStoreException e) {
			throw new MDMAPIException(
					"Error occurred while retrieving list of roles created within the current " +
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
			String msg =
					"Error occurred while retrieving the list of users that exist within the current tenant";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}
}
