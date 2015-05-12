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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.Credential;
import org.wso2.carbon.device.mgt.common.app.mgt.AppManagerConnector;
import org.wso2.carbon.device.mgt.common.app.mgt.AppManagerConnectorException;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Authentication related REST-API implementation.
 */
public class Authentication {
	private static Log log = LogFactory.getLog(Authentication.class);

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@Path("credentials")
	public Credential auth() throws MDMAPIException {
		AppManagerConnector appManager;
		try {
			appManager = MDMAPIUtils.getAppManagementService();
			return appManager.getClientCredentials();
		} catch (AppManagerConnectorException e) {
			String errorMsg = "Device management error";
			log.error(errorMsg, e);
			throw new MDMAPIException(errorMsg, e);
		}
	}
}
