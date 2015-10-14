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
import org.wso2.carbon.CarbonConstants;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;
import org.wso2.carbon.mdm.beans.RoleWrapper;
import org.wso2.carbon.mdm.util.SetReferenceTransformer;
import org.wso2.carbon.user.api.AuthorizationManager;
import org.wso2.carbon.user.api.Permission;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Role {

    private static Log log = LogFactory.getLog(Role.class);
    /**
     * Get user roles (except all internal roles) from system.
     *
     * @return A list of users
     * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    public Response getRoles() throws MDMAPIException {
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        String[] roles;
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting the list of user roles");
            }
            roles = userStoreManager.getRoleNames();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving the list of user roles.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        // removing all internal roles
        List<String> rolesWithoutInternalRoles = new ArrayList<String>();
        for (String role : roles) {
            if (!role.startsWith("Internal/")) {
                rolesWithoutInternalRoles.add(role);
            }
        }
        ResponsePayload responsePayload = new ResponsePayload();
        responsePayload.setStatusCode(HttpStatus.SC_OK);
        responsePayload.setMessageFromServer("All user roles were successfully retrieved.");
        responsePayload.setResponseContent(rolesWithoutInternalRoles);
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    /**
     * API is used to persist a new Role
     * @param roleWrapper
     * @return
     * @throws MDMAPIException
     */
    @POST
    @Produces({ MediaType.APPLICATION_JSON })
    public Response addRole(RoleWrapper roleWrapper) throws MDMAPIException {
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Persisting the role to user store");
            }
            Permission[] permissions = null;
            if (roleWrapper.getPermissions() != null && roleWrapper.getPermissions().length > 0){
                permissions = new Permission[roleWrapper.getPermissions().length];

                for (int i = 0; i < permissions.length; i++) {
                    String permission = roleWrapper.getPermissions()[i];
                    permissions[i] = new Permission(permission, CarbonConstants.UI_PERMISSION_ACTION);
                }
            }
            userStoreManager.addRole(roleWrapper.getRoleName(), roleWrapper.getUsers(), permissions);
        } catch (UserStoreException e) {
            String msg = "Error occurred while saving the role: " + roleWrapper.getRoleName();
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).build();
    }

    /**
     * API is used to update a role Role
     * @param roleWrapper
     * @return
     * @throws MDMAPIException
     */
    @PUT
    @Path("{roleName}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response updateRole(@PathParam("roleName") String roleName, RoleWrapper roleWrapper) throws MDMAPIException {
        final UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        final AuthorizationManager authorizationManager = MDMAPIUtils.getAuthorizationManager();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Updating the role to user store");
            }
            userStoreManager.updateRoleName(roleName, roleWrapper.getRoleName());
            SetReferenceTransformer transformer = new SetReferenceTransformer();
            transformer.transform(Arrays.asList(userStoreManager.getUserListOfRole(roleName)),
                                  Arrays.asList(roleWrapper.getUsers()));
            final String[] usersToAdd = (String [])
                    transformer.getObjectsToAdd().toArray(new String[transformer.getObjectsToAdd().size()]);
            final String[] usersToDelete = (String [])
                    transformer.getObjectsToRemove().toArray(new String[transformer.getObjectsToRemove().size()]);

            userStoreManager.updateUserListOfRole(roleName, usersToDelete, usersToAdd);

            // Delete all authorizations for the current role before authorizing the permission tree
            authorizationManager.clearRoleAuthorization(roleName);
            if (roleWrapper.getPermissions() != null && roleWrapper.getPermissions().length > 0){
                for (int i = 0; i < roleWrapper.getPermissions().length; i++) {
                    String permission = roleWrapper.getPermissions()[i];
                    authorizationManager.authorizeRole(roleName, permission, CarbonConstants.UI_PERMISSION_ACTION);
                }
            }
        } catch (UserStoreException e) {
            String msg = "Error occurred while saving the role: " + roleWrapper.getRoleName();
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).build();
    }

    /**
     * API is used to delete a role and authorizations
     * @param roleName
     * @return
     * @throws MDMAPIException
     */
    @DELETE
    @Path("{roleName}")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response deleteRole(@PathParam("roleName") String roleName) throws MDMAPIException {
        final UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        final AuthorizationManager authorizationManager = MDMAPIUtils.getAuthorizationManager();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Deleting the role in user store");
            }
            userStoreManager.deleteRole(roleName);
            // Delete all authorizations for the current role before deleting
            authorizationManager.clearRoleAuthorization(roleName);
        } catch (UserStoreException e) {
            String msg = "Error occurred while deleting the role: " + roleName;
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).build();
    }

    /**
     * API is used to update users of a role
     * @param roleName
     * @param userList
     * @return
     * @throws MDMAPIException
     */
    @PUT
    @Path("{roleName}/users")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response updateUsers(@PathParam("roleName") String roleName, List<String> userList)
            throws MDMAPIException {
        final UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Updating the users of a role");
            }
            SetReferenceTransformer transformer = new SetReferenceTransformer();
            transformer.transform(Arrays.asList(userStoreManager.getUserListOfRole(roleName)),
                                  userList);
            final String[] usersToAdd = (String [])
                    transformer.getObjectsToAdd().toArray(new String[transformer.getObjectsToAdd().size()]);
            final String[] usersToDelete = (String [])
                    transformer.getObjectsToRemove().toArray(new String[transformer.getObjectsToRemove().size()]);

            userStoreManager.updateUserListOfRole(roleName, usersToDelete, usersToAdd);
        } catch (UserStoreException e) {
            String msg = "Error occurred while saving the users of the role: " + roleName;
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).build();
    }
}
