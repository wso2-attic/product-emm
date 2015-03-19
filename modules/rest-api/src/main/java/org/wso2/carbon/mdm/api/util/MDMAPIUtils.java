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
package org.wso2.carbon.mdm.api.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.device.mgt.user.core.service.UserManagementService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * MDMAPIUtils class provides utility function used by CDM REST-API classes.
 */
public class MDMAPIUtils {

    private static Log log = LogFactory.getLog(MDMAPIUtils.class);

	public static DeviceManagementService getDeviceManagementService() {
        // until complete login this is use to load super tenant context
		DeviceManagementService dmService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		dmService = (DeviceManagementService) ctx.getOSGiService(DeviceManagementService.class, null);
        PrivilegedCarbonContext.endTenantFlow();
		return dmService;
	}
    public static UserManagementService getUserManagementService() throws MDMAPIException{

        UserManagementService umService;
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        umService = (UserManagementService) ctx.getOSGiService(UserManagementService.class, null);

        if (umService == null){
            String msg = "user management service not initialized";
            log.error(msg);
            throw new MDMAPIException(msg);
        }
        PrivilegedCarbonContext.endTenantFlow();
        return umService;
    }
	public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId, String deviceType) {
		DeviceIdentifier identifier = new DeviceIdentifier();
		identifier.setId(deviceId);
		identifier.setType(deviceType);
		return identifier;
	}

}
