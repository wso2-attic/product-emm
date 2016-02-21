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

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.handler.RequestData;
import org.apache.ws.security.message.token.BinarySecurity;
import org.apache.ws.security.validate.Credential;
import org.apache.ws.security.validate.Validator;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.authenticator.OAuthValidatorFactory;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.AuthenticationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.OAuthTokenValidationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;

import java.util.HashMap;

/**
 * Validator class for user authentication checking the default carbon user store.
 */
public class BSTValidator implements Validator {

    /**
     * This method validates the binary security token in SOAP message coming from the device.
     *
     * @param credential  - binary security token credential object.
     * @param requestData - Request data associated with the request.
     * @return - Credential object if authentication is success, or null if not success.
     * @throws WSSecurityException
     */
    @Override
    public Credential validate(Credential credential, RequestData requestData) throws WSSecurityException {
        String bearerToken;
        String requestedUri;
        Credential userCredentials;

        HashMap msgContext = (HashMap) requestData.getMsgContext();
        requestedUri = msgContext.get(PluginConstants.CXF_REQUEST_URI).toString();
        BinarySecurity binarySecurityTokenObject = credential.getBinarySecurityToken();
        String binarySecurityToken = binarySecurityTokenObject.getElement().getFirstChild().getTextContent();
        Base64 base64 = new Base64();
        bearerToken = new String(base64.decode(binarySecurityToken));
        AuthenticationInfo authenticationInfo;
        try {
            authenticationInfo = validateRequest(requestedUri, bearerToken);
            WindowsAPIUtils.startTenantFlow(authenticationInfo);

            if (authenticate(binarySecurityToken, authenticationInfo)) {
                userCredentials = credential;
            } else {
                throw new WindowsDeviceEnrolmentException(
                        "Authentication failure due to invalid binary security token.");
            }
        } catch (AuthenticationException e) {
            throw new WSSecurityException("Failure occurred in the BST validator.", e);
        } catch (WindowsDeviceEnrolmentException e) {
            throw new WSSecurityException("Authentication failure occurred due to binary security token.", e);
        } catch (OAuthTokenValidationException e) {
            throw new WSSecurityException(
                    "Failed to authenticate the incoming request due to oauth token validation error.", e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
        return userCredentials;
    }

    /**
     * This method authenticates the client who comes with binary security token.
     *
     * @param binarySecurityToken - Binary security token received in the SOAP message header.
     * @return - Authentication status.
     * @throws AuthenticationException
     */
    private boolean authenticate(String binarySecurityToken, AuthenticationInfo authenticationInfo) throws
            AuthenticationException {
        WindowsAPIUtils.startTenantFlow(authenticationInfo);
        if (DeviceUtil.getCacheEntry(binarySecurityToken) != null) {
            CacheEntry cacheentry = (CacheEntry) DeviceUtil.getCacheEntry(binarySecurityToken);
            String username = cacheentry.getUsername();
            return username != null;
        } else {
            return false;
        }
    }

    /**
     * Validate SOAP request token.
     *
     * @param requestedUri        -Requested endpoint URI.
     * @param binarySecurityToken -Binary security token comes from the soap request message.
     * @return returns authorized user information.
     * @throws WindowsDeviceEnrolmentException
     */
    private AuthenticationInfo validateRequest(String requestedUri, String binarySecurityToken)
            throws WindowsDeviceEnrolmentException, OAuthTokenValidationException {

        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        String resource = requestedUri + ":POST";
        OAuthValidationResponse oAuthValidationResponse = OAuthValidatorFactory.getValidator().
                validateToken(binarySecurityToken, resource);
        try {
            if (oAuthValidationResponse.isValid()) {
                String username = oAuthValidationResponse.getUserName();
                String tenantDomain = oAuthValidationResponse.getTenantDomain();

                authenticationInfo.setUsername(username);
                authenticationInfo.setTenantDomain(tenantDomain);
                authenticationInfo.setTenantId(WindowsAPIUtils.getTenantIdOFUser(username + "@" + tenantDomain));
            } else {
                authenticationInfo.setMessage(oAuthValidationResponse.getErrorMsg());
            }
        } catch (DeviceManagementException e) {
            throw new WindowsDeviceEnrolmentException(
                    "Authentication failure due to invalid binary security token.", e);
        }
        return authenticationInfo;
    }
}
