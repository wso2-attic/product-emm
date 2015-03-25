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
import org.wso2.carbon.device.mgt.core.service.AppManager;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.device.mgt.core.service.EmailService;
import org.wso2.carbon.device.mgt.user.core.service.UserManagementService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

/**
 * MDMAPIUtils class provides utility function used by CDM REST-API classes.
 */
public class MDMAPIUtils {

    private static Log log = LogFactory.getLog(MDMAPIUtils.class);


	public static DeviceManagementService getDeviceManagementService(String tenantDomain) throws MDMAPIException {
		// until complete login this is use to load super tenant context
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		int tenantId;
		DeviceManagementService dmService;
		if (tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)){
			tenantId = MultitenantConstants.SUPER_TENANT_ID;
		}else{
			tenantId = getTenantId(tenantDomain);
		}
		ctx.setTenantDomain(tenantDomain);
		ctx.setTenantId(tenantId);
		dmService = (DeviceManagementService) ctx.getOSGiService(DeviceManagementService.class, null);
		return dmService;
	}

	public static DeviceManagementService getDeviceManagementService() throws MDMAPIException {
		return getDeviceManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
	}

	public static int getTenantId(String tenantDomain) throws MDMAPIException {
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);
		try {
			return realmService.getTenantManager().getTenantId(tenantDomain);
		} catch (UserStoreException e) {
			throw new MDMAPIException("Error obtaining tenant id from tenant domain "+tenantDomain);
		}
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

    public static EmailService getEmailService() throws MDMAPIException{
        EmailService emailService;
        PrivilegedCarbonContext.startTenantFlow();
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
        ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
        emailService = (EmailService) ctx.getOSGiService(EmailService.class, null);

        if (emailService == null){
            String msg = "email service not initialized";
            log.error(msg);
            throw new MDMAPIException(msg);
        }
        PrivilegedCarbonContext.endTenantFlow();
        return emailService;
    }

	public static DeviceIdentifier convertToDeviceIdentifierObject(String deviceId, String deviceType) {
		DeviceIdentifier identifier = new DeviceIdentifier();
		identifier.setId(deviceId);
		identifier.setType(deviceType);
		return identifier;
	}

	public static AppManager getAppManagementService(String tenantDomain) throws MDMAPIException {
		// until complete login this is use to load super tenant context
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		int tenantId;
		AppManager appService;
		if (tenantDomain.equals(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME)){
			tenantId = MultitenantConstants.SUPER_TENANT_ID;
		}else{
			tenantId = getTenantId(tenantDomain);
		}
		ctx.setTenantDomain(tenantDomain);
		ctx.setTenantId(tenantId);
		appService = (AppManager) ctx.getOSGiService(AppManager.class, null);
		return appService;
	}

	public static AppManager getAppManagementService() throws MDMAPIException {
		return getAppManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
	}

}
