/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.device.details.DeviceInfo;
import org.wso2.carbon.device.mgt.common.device.details.DeviceLocation;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceDetailsMgtException;
import org.wso2.carbon.device.mgt.core.device.details.mgt.DeviceInformationManager;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class DeviceInformation {

    private static Log log = LogFactory.getLog(DeviceInformation.class);

    @GET
    @Path("{type}/{id}")
    public Response getDeviceInfo(@PathParam("type") String type, @PathParam("id") String id) throws MDMAPIException {
        DeviceInformationManager informationManager;
        DeviceInfo deviceInfo;
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(id);
            deviceIdentifier.setType(type);
            informationManager = MDMAPIUtils.getDeviceInformationManagerService();
            deviceInfo = informationManager.getDeviceInfo(deviceIdentifier);

        } catch (DeviceDetailsMgtException e) {
            String msg = "Error occurred while getting the device information.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).entity(deviceInfo).build();
    }


    @GET
    @Path("location/{type}/{id}")
    public Response getDeviceLocation(@PathParam("type") String type, @PathParam("id") String id) throws MDMAPIException {
        DeviceInformationManager informationManager;
        DeviceLocation deviceLocation;
        try {
            DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(id);
            deviceIdentifier.setType(type);
            informationManager = MDMAPIUtils.getDeviceInformationManagerService();
            deviceLocation = informationManager.getDeviceLocation(deviceIdentifier);

        } catch (DeviceDetailsMgtException e) {
            String msg = "Error occurred while getting the device location.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).entity(deviceLocation).build();
    }
}

