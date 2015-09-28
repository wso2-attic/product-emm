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
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class represents license related operations.
 */
public class License {

    private static Log log = LogFactory.getLog(License.class);

    /**
     * This method returns the license text related to a given device type and language code.
     *
     * @param deviceType   Device type, ex: android, ios
     * @param languageCode Language code, ex: en_US
     * @return Returns the license text
     * @throws MDMAPIException If the device type or language code arguments are not available or invalid.
     */
    @GET
    @Path ("{deviceType}/{languageCode}")
    @Produces ({MediaType.APPLICATION_JSON})
    public Response getLicense(@PathParam ("deviceType") String deviceType,
                               @PathParam ("languageCode") String languageCode) throws MDMAPIException {

        org.wso2.carbon.device.mgt.common.license.mgt.License license;
        ResponsePayload responsePayload = new ResponsePayload();
        try {
            license = MDMAPIUtils.getDeviceManagementService().getLicense(deviceType, languageCode);
            if (license == null) {
                return Response.status(HttpStatus.SC_NOT_FOUND).build();
            }
            responsePayload = ResponsePayload.statusCode(HttpStatus.SC_OK).
                    messageFromServer("License for '" + deviceType + "' was retrieved successfully").
                    responseContent(license.getText()).
                    build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while retrieving the license configured for '" + deviceType + "' device type";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    /**
     * This method is used to add license to a specific device type.
     *
     * @param deviceType Device type, ex: android, ios
     * @param license License object
     * @return Returns the acknowledgement for the action
     * @throws MDMAPIException
     */
    @POST
    @Path ("{deviceType}")
    public Response addLicense(@PathParam ("deviceType") String deviceType,
                               org.wso2.carbon.device.mgt.common.license.mgt.License license) throws MDMAPIException {

        ResponsePayload responsePayload;
        try {
            MDMAPIUtils.getDeviceManagementService().addLicense(deviceType, license);
            responsePayload = ResponsePayload.statusCode(HttpStatus.SC_OK).
                    messageFromServer("License added successfully for '" + deviceType + "' device type").
                    build();
        } catch (DeviceManagementException e) {
            String msg = "Error occurred while adding license for '" + deviceType + "' device type";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }
}
