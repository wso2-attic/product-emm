/*
* Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.wso2.carbon.mdm.services.android.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.mdm.services.android.bean.OperationResponse;
import org.wso2.carbon.mdm.services.android.exception.AndroidOperationException;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * AndroidOperationUtils class provides utility functions used by Android REST-API classes.
 */
public class AndroidOperationUtils {

	private static Log log = LogFactory.getLog(AndroidOperationUtils.class);

	public static void updateOperations(List<OperationResponse> operations, Message message,
										MediaType responseMediaType) {

		int id;
		for (OperationResponse operationResponse : operations) {
			id = Integer.valueOf(operationResponse.getId());
			AndroidAPIUtils.updateOperation(message, responseMediaType, id, Operation.Status.COMPLETED);
			if (log.isDebugEnabled()) {
				log.debug("Updating operation '" + operationResponse.getCode() + "'");
			}
		}
	}

	public static List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation>
		getPendingOperations(DeviceIdentifier deviceIdentifier, Message message, MediaType responseMediaType) {

		List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations;

		try {
			operations = AndroidAPIUtils.getDeviceManagementService().getPendingOperations(deviceIdentifier);
		} catch (OperationManagementException ex) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			log.error("Issue in retrieving operation management service instance.", ex);
			throw new AndroidOperationException(message, responseMediaType);
		}
		return operations;
	}
}
