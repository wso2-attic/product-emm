
/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.services.android;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.core.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.mdm.services.android.common.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Android Device Operation REST-API implementation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class OperationManager {

	private static Log log = LogFactory.getLog(OperationManager.class);

	@GET
	@Path("{id}")
	public List<org.wso2.carbon.device.mgt.core.operation.mgt.Operation> getPendingOperations(
			@PathParam("id") String id)
			throws AndroidAgentException {

		List<org.wso2.carbon.device.mgt.core.operation.mgt.Operation> operations;
		String msg;
		DeviceManagementService dmService;

		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();
			DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
			operations = dmService.getPendingOperations(deviceIdentifier);
			Response.status(HttpStatus.SC_OK);
			return operations;
		} catch (DeviceManagementServiceException e) {
			msg = "Device management service error";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		} catch (OperationManagementException e) {
			msg = "Error occurred while fetching the operation list for the device.";
			log.error(msg, e);
			Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			throw new AndroidAgentException(msg, e);
		}
	}

	@PUT
	public Message updateOperation() throws AndroidAgentException {
		String msg;
		DeviceManagementService dmService;
		Message responseMsg = new Message();
		try {
			dmService = AndroidAPIUtils.getDeviceManagementService();

            //TODO: need to complete updateOperation logic
			boolean result = dmService.addOperation(null, null);
			if (result) {
				Response.status(HttpStatus.SC_OK);
				responseMsg.setResponseMessage("Device has already enrolled");
			} else {
				Response.status(HttpStatus.SC_NOT_FOUND);
				responseMsg.setResponseMessage("Operation not found");
			}
			return responseMsg;
		} catch (DeviceManagementServiceException e) {
			msg = "Device management service error";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		} catch (OperationManagementException e) {
			msg = "Error occurred while updating the operation status for the device.";
			log.error(msg, e);
			Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			throw new AndroidAgentException(msg, e);
		}
	}
}