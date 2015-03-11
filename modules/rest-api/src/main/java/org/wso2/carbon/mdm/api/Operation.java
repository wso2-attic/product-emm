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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.context.DeviceOperationContext;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.Message;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Operation related REST-API implementation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Operation {

	private static Log log = LogFactory.getLog(Operation.class);

	@GET
	public List<org.wso2.carbon.device.mgt.common.Operation> getAllOperations()
			throws MDMAPIException {
		List<org.wso2.carbon.device.mgt.common.Operation> operations;
		DeviceManagementService dmService;
		OperationManager operationManager;
		try {
			dmService = MDMAPIUtils.getDeviceManagementService();
			operationManager = dmService.getOperationManager(
					DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			operations = operationManager.getOperations(null);
		} catch (DeviceManagementServiceException deviceServiceMgtEx) {
			String errorMsg = "Device management service error";
			log.error(errorMsg, deviceServiceMgtEx);
			throw new MDMAPIException(errorMsg, deviceServiceMgtEx);
		} catch (DeviceManagementException deviceMgtEx) {
			String errorMsg = "Error occurred while fetching the operation manager.";
			log.error(errorMsg, deviceMgtEx);
			throw new MDMAPIException(errorMsg, deviceMgtEx);
		} catch (OperationManagementException ex) {
			String errorMsg = "Error occurred while fetching the operations for the device.";
			log.error(errorMsg, ex);
			throw new MDMAPIException(errorMsg, ex);
		}
		return operations;
	}

	@POST
	public Message addOperation(DeviceOperationContext operationContext) throws MDMAPIException {
		DeviceManagementService dmService;
		OperationManager operationManager;
		Message responseMsg = new Message();
		try {
			dmService = MDMAPIUtils.getDeviceManagementService();
			operationManager = dmService.getOperationManager(
					DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			boolean status = operationManager.addOperation(operationContext.getOperation(),
			                                               operationContext.getDevices());
			if (status) {
				Response.status(HttpStatus.SC_CREATED);
				responseMsg.setResponseMessage("Operation has added successfully.");
			} else {
				Response.status(HttpStatus.SC_OK);
				responseMsg.setResponseMessage("Failure in adding the Operation.");
			}
			return responseMsg;
		} catch (DeviceManagementServiceException deviceServiceMgtEx) {
			String errorMsg = "Device management service error";
			log.error(errorMsg, deviceServiceMgtEx);
			throw new MDMAPIException(errorMsg, deviceServiceMgtEx);
		} catch (DeviceManagementException deviceMgtEx) {
			String errorMsg = "Error occurred while adding the operation";
			log.error(errorMsg, deviceMgtEx);
			throw new MDMAPIException(errorMsg, deviceMgtEx);
		} catch (OperationManagementException ex) {
			String errorMsg = "Error occurred while saving the operation";
			log.error(errorMsg, ex);
			throw new MDMAPIException(errorMsg, ex);
		}
	}
}