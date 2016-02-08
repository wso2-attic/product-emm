/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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

package org.wso2.carbon.mdm.mobileservices.windows.services.authbst.impl;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.Token;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.AuthenticationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.DeviceUtil;
import org.wso2.carbon.mdm.mobileservices.windows.services.authbst.BSTProvider;
import org.wso2.carbon.mdm.mobileservices.windows.services.authbst.beans.Credentials;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.core.Response;

/**
 * Implementation class of BSTProvider interface which authenticates the credentials comes via MDM login page.
 */
public class BSTProviderImpl implements BSTProvider {

    /**
     * This method validates the device user, checking passed credentials and returns the corresponding
     * binary security token which is used in XCEP and WSTEP stages for authentication.
     *
     * @param credentials - Credential object passes from the wab page.
     * @return - Response with binary security token.
     */
    @Override
    public Response getBST(Credentials credentials) throws WindowsDeviceEnrolmentException {

        String domainUser = credentials.getUsername();
        String userToken = credentials.getUsertoken();
        String encodedToken;
        try {
            Token tokenBean = new Token();
            tokenBean.setChallengeToken(userToken);
            Base64 base64 = new Base64();
            encodedToken = base64.encodeToString(userToken.getBytes());
            DeviceUtil.persistChallengeToken(encodedToken, null, domainUser);
            JSONObject tokenContent = new JSONObject();
            tokenContent.put("UserToken", userToken);
            return Response.ok().entity(tokenContent.toString()).build();
        } catch (JSONException e) {
            throw new WindowsDeviceEnrolmentException(
                    "Failure occurred in generating Json payload for challenge token.", e);
        }
    }

    /**
     * This method authenticate the user checking the carbon default user store.
     *
     * @param username     - Username in username token
     * @param password     - Password in username token
     * @param tenantDomain - Tenant domain is extracted from the username
     * @return - Returns boolean representing authentication result
     * @throws AuthenticationException
     */
    private boolean authenticate(String username, String password, String tenantDomain) throws
            AuthenticationException {

        try {
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            RealmService realmService = (RealmService) ctx.getOSGiService(RealmService.class, null);

            if (realmService == null) {
                throw new AuthenticationException("RealmService not initialized.");
            }

            int tenantId;
            if (tenantDomain == null || tenantDomain.trim().isEmpty()) {
                tenantId = MultitenantConstants.SUPER_TENANT_ID;
            } else {
                tenantId = realmService.getTenantManager().getTenantId(tenantDomain);
            }

            if (tenantId == MultitenantConstants.INVALID_TENANT_ID) {
                throw new AuthenticationException("Invalid tenant domain " + tenantDomain);
            }
            UserRealm userRealm = realmService.getTenantUserRealm(tenantId);

            return userRealm.getUserStoreManager().authenticate(username, password);
        } catch (UserStoreException e) {
            throw new AuthenticationException("User store is not initialized.", e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

}
