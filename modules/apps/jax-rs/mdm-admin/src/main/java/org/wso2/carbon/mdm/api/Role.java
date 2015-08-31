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
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;
import org.wso2.carbon.user.api.UserStoreException;

import javax.ws.rs.GET;
import javax.ws.rs.core.Response;

public class Role {

    private static Log log = LogFactory.getLog(Role.class);

    /**
     * Get a list of roles in user-store.
     *
     * @return A list of users
     * @throws org.wso2.carbon.mdm.api.common.MDMAPIException
     */
    @GET
    public Response getRoles() throws MDMAPIException {
        ResponsePayload responsePayload = new ResponsePayload();
        try {
            if (log.isDebugEnabled()) {
                log.debug("Getting the list of roles.");
            }
            String[] roles = MDMAPIUtils.getUserStoreManager().getRoleNames();
            responsePayload.setResponseContent(roles);
            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
        } catch (UserStoreException e) {
            String msg = "Error occurred while retrieving the list of roles.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }
}
