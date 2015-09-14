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

package org.wso2.carbon.mdm.services.android;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Android Platform Configuration REST-API implementation.
 * All end points supports JSON, XMl with content negotiation.
 */
@WebService
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class ConfigurationMgtService {

	private static Log log = LogFactory.getLog(ConfigurationMgtService.class);

	@POST
	public Message configureSettings(TenantConfiguration configuration)
			throws AndroidAgentException {

		Message responseMsg = new Message();
		String msg;
		try {
			configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			AndroidAPIUtils.getDeviceManagementService().saveConfiguration(configuration);
			Response.status(Response.Status.CREATED);
			responseMsg.setResponseMessage("Android platform configuration saved successfully");
			responseMsg.setResponseCode(Response.Status.CREATED.toString());
		} catch (DeviceManagementException e) {
			msg = "Error occurred while configuring the android platform";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		} finally {
			AndroidAPIUtils.endTenantFlow();
		}
		return responseMsg;
	}

	@GET
	public TenantConfiguration getConfiguration() throws AndroidAgentException {
		String msg;
		TenantConfiguration tenantConfiguration;
		try {
			tenantConfiguration = AndroidAPIUtils.getDeviceManagementService().
					getConfiguration(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
		} catch (DeviceManagementException e) {
			msg = "Error occurred while retrieving the Android tenant configuration";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		} finally {
			AndroidAPIUtils.endTenantFlow();
		}
		return tenantConfiguration;
	}

	@PUT
	public Message updateConfiguration(TenantConfiguration configuration)
			throws AndroidAgentException {
		String msg;
		Message responseMsg = new Message();
		try {
			configuration.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID);
			AndroidAPIUtils.getDeviceManagementService().saveConfiguration(configuration);
			Response.status(Response.Status.CREATED);
			responseMsg.setResponseMessage("Android platform configuration succeeded");
			responseMsg.setResponseCode(Response.Status.CREATED.toString());
		} catch (DeviceManagementException e) {
			msg = "Error occurred while modifying configuration settings of Android platform";
			log.error(msg, e);
			throw new AndroidAgentException(msg, e);
		} finally {
			AndroidAPIUtils.endTenantFlow();
		}
		return responseMsg;
	}
}
