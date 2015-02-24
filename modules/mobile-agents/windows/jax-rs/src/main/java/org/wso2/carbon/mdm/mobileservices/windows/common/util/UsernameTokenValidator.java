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

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.apache.log4j.Logger;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.AuthenticationException;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

public class UsernameTokenValidator implements Validator {

	private static final int USER_PART = 0;
	private static final int DOMAIN_PART = 1;
	private static final String DELIMITER = "@";
	private static final String EMPTY_STRING = "";

	private Logger logger = Logger.getLogger(UsernameTokenValidator.class);

	@Override public Credential validate(Credential credential, RequestData requestData)
			throws WSSecurityException {

		String domainUser = credential.getUsernametoken().getName();
		String[] domainUserArray = domainUser.split(DELIMITER);
		Credential returnCredentials=null;

		String user = domainUserArray[USER_PART];
		String domain = domainUserArray[DOMAIN_PART];
		String password = credential.getUsernametoken().getPassword();

		//Generic exception is caught here as there is no need of taking different actions for different exceptions.
		try {
			domain=""; //remove later...
			if(authenticate(user, password, domain)){
				returnCredentials=credential;
			}
			else{
				throw new WSSecurityException("Authentication failure due to incorrect credentials.");
			}
		} catch (Exception e) {
			throw new WSSecurityException("Failure occurred in the credential validator.");
		}
		return returnCredentials;
	}

	public boolean authenticate(String username, String password, String tenantDomain)
			throws AuthenticationException {

		try {
			PrivilegedCarbonContext.startTenantFlow();
			PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
			ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
			ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
			RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);

			if (realmService == null) {
				String msg = "RealmService not initialized.";
				throw new AuthenticationException(msg);
			}

			int tenantId;

			if (tenantDomain == null || EMPTY_STRING.equals(tenantDomain.trim())) {
				tenantId = MultitenantConstants.SUPER_TENANT_ID;
			} else {
				tenantId = realmService.getTenantManager().getTenantId(tenantDomain);
			}

			if (tenantId == MultitenantConstants.INVALID_TENANT_ID) {
				logger.error("Invalid tenant domain " + tenantDomain);
				return false;
			}

			UserRealm userRealm = realmService.getTenantUserRealm(tenantId);

			return userRealm.getUserStoreManager().authenticate(
					username, password);
		} catch (UserStoreException e) {
			String msg = "User store not initialized.";
			logger.error(msg);
			throw new AuthenticationException(msg);
		} finally {
			PrivilegedCarbonContext.endTenantFlow();
		}
	}
}
