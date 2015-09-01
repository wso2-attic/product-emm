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

package org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.impl;

import com.ibm.wsdl.OperationImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsOperationsException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.Message;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.Operations;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Implementation class of operations interface. Each method in this class receives the operations comes via UI
 * and persists those in the correct format.
 */
public class OperationsImpl implements Operations {

    private static Log log = LogFactory.getLog(OperationImpl.class);

    @POST
    @Path("/devicelock")
    public Response lock (@HeaderParam("Accept") String acceptHeader, List<String> deviceIDs)
            throws WindowsDeviceEnrolmentException {
        if (log.isDebugEnabled()) {
            log.debug("Invoking windows device lock operation");
        }

        MediaType responseMediaType = WindowsAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(Constants.OperationCodes.DEVICE_LOCK);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);

            return WindowsAPIUtils.getOperationResponse(deviceIDs, operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        }
    }

    @POST
    @Path("/devicedisenroll")
    public Response disenroll (@HeaderParam("Accept") String acceptHeader, List<String> deviceIDs)
            throws WindowsDeviceEnrolmentException {

        MediaType responseMediaType = WindowsAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();
            CommandOperation operation = new CommandOperation();
            operation.setCode(Constants.OperationCodes.DISENROLL);
            operation.setType(Operation.Type.COMMAND);
            operation.setEnabled(true);
        try {

            return WindowsAPIUtils.getOperationResponse(deviceIDs, operation, message, responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        }
    }

    @POST
    @Path("/devicewipe")
    public Response wipe (@HeaderParam("Accept") String acceptHeader, List<String> deviceids)
            throws WindowsDeviceEnrolmentException {
        if (log.isDebugEnabled()) {
            log.debug("Invoking windows wipe-data device operation");
        }


        MediaType responseMediaType = WindowsAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

            CommandOperation operation = new CommandOperation();
            operation.setCode(Constants.OperationCodes.WIPE_DATA);
            operation.setType(Operation.Type.COMMAND);
            try{
        return WindowsAPIUtils.getOperationResponse(deviceids, operation, message,
                    responseMediaType);

        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        }
    }

    @POST
    @Path("/devicering")
    public Response ring (@HeaderParam("Accept") String acceptHeader, List<String> deviceIDs)
            throws WindowsDeviceEnrolmentException {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Windows ring-device device operation");
        }

        MediaType responseMediaType = WindowsAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();

        try {
            CommandOperation operation = new CommandOperation();
            operation.setCode(Constants.OperationCodes.DEVICE_RING);
            operation.setType(Operation.Type.COMMAND);

            return WindowsAPIUtils.getOperationResponse(deviceIDs, operation, message, responseMediaType);
        } catch (OperationManagementException e) {
            String errorMessage = "Issue in retrieving operation management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        } catch (DeviceManagementException e) {
            String errorMessage = "Issue in retrieving device management service instance";
            message.setResponseMessage(errorMessage);
            message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
            log.error(errorMessage, e);
            throw new WindowsOperationsException(message, responseMediaType);
        }
    }


}
