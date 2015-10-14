/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.MDMAppConstants;
import org.wso2.carbon.mdm.api.util.ResponsePayload;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * General Tenant Configuration REST-API implementation.
 * All end points support JSON, XMl with content negotiation.
 */
@WebService
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Configuration {

	private static Log log = LogFactory.getLog(Configuration.class);

	@POST
	public ResponsePayload saveTenantConfiguration(TenantConfiguration configuration)
			throws MDMAPIException {
		ResponsePayload responseMsg = new ResponsePayload();
		try {
			MDMAPIUtils.getTenantConfigurationManagementService().saveConfiguration(configuration,
                                    MDMAppConstants.RegistryConstants.GENERAL_CONFIG_RESOURCE_PATH);
			Response.status(HttpStatus.SC_CREATED);
			responseMsg.setMessageFromServer("Tenant configuration saved successfully.");
			responseMsg.setStatusCode(HttpStatus.SC_CREATED);
			return responseMsg;
		} catch (ConfigurationManagementException e) {
            String msg = "Error occurred while saving the tenant configuration.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@GET
	public TenantConfiguration getConfiguration() throws MDMAPIException {
		String msg;
		try {
			return MDMAPIUtils.getTenantConfigurationManagementService().getConfiguration(MDMAppConstants.
                                        RegistryConstants.GENERAL_CONFIG_RESOURCE_PATH);
		} catch (ConfigurationManagementException e) {
			msg = "Error occurred while retrieving the tenant configuration.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@PUT
	public ResponsePayload updateConfiguration(TenantConfiguration configuration) throws MDMAPIException {
		ResponsePayload responseMsg = new ResponsePayload();
		try {
			MDMAPIUtils.getTenantConfigurationManagementService().saveConfiguration(configuration,
                                    MDMAppConstants.RegistryConstants.GENERAL_CONFIG_RESOURCE_PATH);
			Response.status(HttpStatus.SC_CREATED);
			responseMsg.setMessageFromServer("Tenant configuration updated successfully.");
			responseMsg.setStatusCode(HttpStatus.SC_CREATED);
			return responseMsg;
		} catch (ConfigurationManagementException e) {
            String msg = "Error occurred while updating the tenant configuration.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

}
