/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.api.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.beans.UserCredentialWrapper;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;

import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

/**
 * This class builds Credential modification related Responses
 */
public class CredentialManagementResponseBuilder {

    private static Log log = LogFactory.getLog(CredentialManagementResponseBuilder.class);

    private ResponsePayload responsePayload;

    /**
     * Builds the response to change the password of a user
     * @param credentials - User credentials
     * @return Response Object
     * @throws MDMAPIException
     */
    public static Response buildChangePasswordResponse(UserCredentialWrapper credentials) throws MDMAPIException {
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        ResponsePayload responsePayload = new ResponsePayload();

        try {
            byte[] decodedNewPassword = Base64.decodeBase64(credentials.getNewPassword());
            byte[] decodedOldPassword = Base64.decodeBase64(credentials.getOldPassword());
            userStoreManager.updateCredential(credentials.getUsername(), new String(
                    decodedNewPassword, "UTF-8"), new String(decodedOldPassword, "UTF-8"));
            responsePayload.setStatusCode(HttpStatus.SC_CREATED);
            responsePayload.setMessageFromServer("User password by username: " + credentials.getUsername() +
                    " was successfully changed.");
            return Response.status(HttpStatus.SC_CREATED).entity(responsePayload).build();
        } catch (UserStoreException e) {
            log.error(e.getMessage(), e);
            responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            responsePayload.setMessageFromServer("Old password does not match.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
        } catch (UnsupportedEncodingException e) {
            String errorMsg = "Could not change the password of the user: " + credentials.getUsername() +
                    ". The Character Encoding is not supported.";
            log.error(errorMsg, e);
            throw new MDMAPIException(errorMsg, e);
        }

    }

    /**
     * Builds the response to reset the password of a user
     * @param credentials - User credentials
     * @return Response Object
     * @throws MDMAPIException
     */
    public static Response buildResetPasswordResponse(UserCredentialWrapper credentials) throws MDMAPIException {
        UserStoreManager userStoreManager = MDMAPIUtils.getUserStoreManager();
        ResponsePayload responsePayload = new ResponsePayload();
        try {
            byte[] decodedNewPassword = Base64.decodeBase64(credentials.getNewPassword());
            userStoreManager.updateCredentialByAdmin(credentials.getUsername(), new String(
                    decodedNewPassword, "UTF-8"));
            responsePayload.setStatusCode(HttpStatus.SC_CREATED);
            responsePayload.setMessageFromServer("User password by username: " + credentials.getUsername() +
                    " was successfully changed.");
            return Response.status(HttpStatus.SC_CREATED).entity(responsePayload).build();
        } catch (UserStoreException e) {
            log.error(e.getMessage(), e);
            responsePayload.setStatusCode(HttpStatus.SC_BAD_REQUEST);
            responsePayload.setMessageFromServer("Could not change the password.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(responsePayload).build();
        } catch (UnsupportedEncodingException e) {
            String errorMsg = "Could not change the password of the user: " + credentials.getUsername() +
                    ". The Character Encoding is not supported.";
            log.error(errorMsg, e);
            throw new MDMAPIException(errorMsg, e);
        }
    }

}
