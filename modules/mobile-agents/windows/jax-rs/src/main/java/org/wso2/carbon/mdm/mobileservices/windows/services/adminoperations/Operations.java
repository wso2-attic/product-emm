/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations;

import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Interface for Admin operations persisting. This interface accepts operations added via UI.
 */
@Path("/operation")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)

public interface Operations {

    @POST
    @Path("/devicelock")
    Response lock(@HeaderParam("Accept") String headerParam, List<String> deviceids) throws WindowsDeviceEnrolmentException;

    @POST
    @Path("/devicedisenroll")
    Response disenroll(@HeaderParam("Accept") String headerParam, List<String> deviceids) throws WindowsDeviceEnrolmentException;

    @POST
    @Path("/devicewipe")
    Response wipe(@HeaderParam("Accept") String headerParam, List<String> deviceids) throws WindowsDeviceEnrolmentException;

    @POST
    @Path("/devicering")
    Response ring(@HeaderParam("Accept") String headerParam, List<String> deviceids) throws WindowsDeviceEnrolmentException;

    @POST
    @Path("/lockreset")
    Response lockReset(@HeaderParam("Accept") String acceptHeader, List<String> deviceids)
            throws WindowsDeviceEnrolmentException;
}
