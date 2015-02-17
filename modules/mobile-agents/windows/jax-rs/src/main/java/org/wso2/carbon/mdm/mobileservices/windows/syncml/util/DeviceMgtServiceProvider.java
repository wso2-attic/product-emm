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

package org.wso2.carbon.mdm.mobileservices.windows.syncml.util;

import org.apache.log4j.Logger;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
* Class for retrieving DeviceManagementService object.
*/
public class DeviceMgtServiceProvider {

	private static Logger logger = Logger.getLogger(DeviceMgtServiceProvider.class);

	/**
	 * This method returns Device Management Object for certain tasks such as Device enrollment etc.
	 * @return DeviceManagementServiceObject
	 * @throws DeviceManagementServiceException
	 */
	public static DeviceManagementService getDeviceManagementService() throws DeviceManagementServiceException {

		DeviceManagementService dmService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		dmService = (DeviceManagementService) ctx.getOSGiService(DeviceManagementService.class, null);

		if (dmService == null) {
			String msg = "Device management service not initialized";
			logger.error(msg);
			throw new DeviceManagementServiceException(msg);
		}
		PrivilegedCarbonContext.endTenantFlow();
		return dmService;
	}

}
