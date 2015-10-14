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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.Token;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.AuthenticationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.MDMAPIException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.DeviceUtil;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.services.authbst.BSTProvider;
import org.wso2.carbon.mdm.mobileservices.windows.services.authbst.beans.Credentials;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.user.api.UserStoreManager;
import org.wso2.carbon.user.core.service.RealmService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import javax.ws.rs.core.Response;

/**
 * Implementation class of BSTProvider interface which authenticates the credentials comes via MDM login page.
 */
public class BSTProviderImpl implements BSTProvider {

    private static Log log = LogFactory.getLog(BSTProviderImpl.class);
    private static final String DELIMITER = "@";
    private static final int USER_SEGMENT = 0;
    private static final int DOMAIN_SEGMENT = 1;

    /**
     * This method validates the device user, checking passed credentials and returns the corresponding
     * binary security token which is used in XCEP and WSTEP stages for authentication.
     *
     * @param credentials - Credential object passes from the wab page
     * @return - Response with binary security token
     */
    @Override
    public Response getBST(Credentials credentials) throws WindowsDeviceEnrolmentException {

        String domainUser = credentials.getUsername();
        String userEmail = credentials.getEmail();
//        String[] domainUserArray = domainUser.split(DELIMITER);
//        String user = domainUserArray[USER_SEGMENT];
//        String domain = domainUserArray[DOMAIN_SEGMENT];
        String domain = "";
        String password = credentials.getPassword();

        try {
            if (authenticate(domainUser, password, domain)) {
                String challengetoken = DeviceUtil.generateRandomToken();
                Token tokenbean = new Token();
                tokenbean.setChallengeToken(challengetoken);
                DeviceUtil.persistChallengeToken(tokenbean.getChallengeToken(), "", domainUser);

                return Response.ok().entity(tokenbean.getChallengeToken()).build();
            } else {
                String msg = "Authentication failure due to incorrect credentials.";
                log.error(msg);
                return Response.status(403).entity("Authentication failure").build();
            }
        } catch (AuthenticationException e) {
            String msg = "Failure occurred in user authentication process.";
            log.error(msg);
            throw new WindowsDeviceEnrolmentException(msg);
        } catch (DeviceManagementException e) {
            String msg = "Failure occurred in generating challenge token.";
            log.error(msg);
            throw new WindowsDeviceEnrolmentException(msg);
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
                String msg = "RealmService not initialized.";
                log.error(msg);
                throw new AuthenticationException(msg);
            }

            int tenantId;
            if (tenantDomain == null || tenantDomain.trim().isEmpty()) {
                tenantId = MultitenantConstants.SUPER_TENANT_ID;
            } else {
                tenantId = realmService.getTenantManager().getTenantId(tenantDomain);
            }

            if (tenantId == MultitenantConstants.INVALID_TENANT_ID) {
                String msg = "Invalid tenant domain " + tenantDomain;
                log.error(msg);
                throw new AuthenticationException(msg);
            }
            UserRealm userRealm = realmService.getTenantUserRealm(tenantId);

            return userRealm.getUserStoreManager().authenticate(username, password);
        } catch (UserStoreException e) {
            String msg = "User store is not initialized.";
            log.error(msg, e);
            throw new AuthenticationException(msg, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Gets a claim-value from user-store.
     *
     * @param username Username of the user
     * @param claimUri required ClaimUri
     * @return A list of usernames
     * @throws MDMAPIException, UserStoreException
     */
    private String getClaimValue(String username, String claimUri) throws MDMAPIException, UserStoreException {
        UserStoreManager userStoreManager = WindowsAPIUtils.getUserStoreManager();
        return userStoreManager.getUserClaimValue(username, claimUri, null);
    }
}
