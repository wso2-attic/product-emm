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

package org.wso2.carbon.mdm.services.android.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.license.mgt.LicenseManagementService;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.mdm.services.android.bean.OperationResponse;
import org.wso2.carbon.mdm.services.android.exception.AndroidOperationException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

/**
 * AndroidAPIUtil class provides utility functions used by Android REST-API classes.
 */
public class AndroidAPIUtils {

	private static Log log = LogFactory.getLog(AndroidAPIUtils.class);

	public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId) {
		DeviceIdentifier identifier = new DeviceIdentifier();
		identifier.setId(deviceId);
		identifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		return identifier;
	}

	public static DeviceManagementService getDeviceManagementService() {

		//TODO: complete login change super tenent context
		DeviceManagementService dmService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		dmService =
				(DeviceManagementService) ctx.getOSGiService(DeviceManagementService.class, null);
		PrivilegedCarbonContext.endTenantFlow();
		return dmService;
	}

	public static LicenseManagementService getLicenseManagerService() {

		//TODO: complete login change super tenent context
		LicenseManagementService licenseManagementService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		licenseManagementService =
				(LicenseManagementService) ctx.getOSGiService(LicenseManagementService.class, null);
		PrivilegedCarbonContext.endTenantFlow();
		return licenseManagementService;
	}

	public static MediaType getResponseMediaType(String acceptHeader) {

		MediaType responseMediaType;
		if (MediaType.WILDCARD.equals(acceptHeader)) {
			responseMediaType = MediaType.APPLICATION_JSON_TYPE;
		} else {
			responseMediaType = MediaType.valueOf(acceptHeader);
		}

		return responseMediaType;
	}

	public static Response getOperationResponse(List<String> deviceIDs, Operation operation,
	                                            Message message, MediaType responseMediaType)
			throws DeviceManagementException, OperationManagementException {

		AndroidDeviceUtils deviceUtils = new AndroidDeviceUtils();
		DeviceIDHolder deviceIDHolder = deviceUtils.validateDeviceIdentifiers(deviceIDs,
		                                                                message, responseMediaType);

		getDeviceManagementService().addOperation(operation, deviceIDHolder.getValidDeviceIDList());

		if (!deviceIDHolder.getErrorDeviceIdList().isEmpty()) {
			return javax.ws.rs.core.Response.status(AndroidConstants.StatusCodes.
					                                MULTI_STATUS_HTTP_CODE).type(
					responseMediaType).entity(deviceUtils.
	     	        convertErrorMapIntoErrorMessage(deviceIDHolder.getErrorDeviceIdList())).build();
		}

		return javax.ws.rs.core.Response.status(javax.ws.rs.core.Response.Status.CREATED).
				type(responseMediaType).build();
	}

	public static List<OperationResponse> extractOperations(String responseContent) {

		List<OperationResponse> operationResponses = null;

		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode operationsObject = mapper.readTree(responseContent);
			JsonNode operations = operationsObject.get(AndroidConstants.DeviceConstants.DEVICE_DATA);

			if (operations != null) {
				operationResponses = mapper.readValue(
						operations,
						mapper.getTypeFactory().constructCollectionType(List.class, OperationResponse.class));
			}

		} catch (JsonProcessingException e) {
			log.error("Issue in json parsing.", e);

		} catch (IOException e) {
			log.error("IOException occurred when json parsing.", e);
		}

		return operationResponses;
	}

	public static void updateOperation(Message message, MediaType responseMediaType,
								int operationID, Operation.Status status) {
		try {
			getDeviceManagementService().updateOperation(operationID, status);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			log.error("Issue in retrieving operation management service instance.", e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}
}
