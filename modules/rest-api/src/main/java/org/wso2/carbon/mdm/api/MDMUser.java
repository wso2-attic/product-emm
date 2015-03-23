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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.api;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.EmailMessageProperties;
import org.wso2.carbon.device.mgt.user.common.Role;
import org.wso2.carbon.device.mgt.user.common.UserManagementException;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.device.mgt.user.common.User;

import javax.ws.rs.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * User related operations
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class MDMUser {

    private static Log log = LogFactory.getLog(MobileDevice.class);

    @GET
    public List<User> getAllUsers() throws MDMAPIException {
        String msg;
        List<User> users;

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

}
