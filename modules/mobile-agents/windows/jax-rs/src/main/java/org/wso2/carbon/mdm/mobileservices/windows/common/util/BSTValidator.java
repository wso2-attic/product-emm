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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import java.util.regex.Pattern;

/**
 * Validator class for user authentication checking the default carbon user store.
 */
public class BSTValidator implements Validator {

    private static Log log = LogFactory.getLog(BSTValidator.class);
    private static final String BEARER_TOKEN_TYPE = "bearer";
    private static final String RESOURCE_KEY = "resource";
    private static final Pattern PATTERN = Pattern.compile("[B|b]earer\\s");

    /**
     * This method validates the binary security token in SOAP message coming from the device.
     *
     * @param credential  - binary security token credential object
     * @param requestData - Request data associated with the request
     * @return - Credential object if authentication is success, or null if not success
     * @throws WSSecurityException
     */
    @Override
    public Credential validate(Credential credential, RequestData requestData) throws WSSecurityException {
        String bearerToken;
        String requestedUri;
        Credential returnCredentials = null;

        HashMap msgContext = (HashMap) requestData.getMsgContext();
        requestedUri = msgContext.get(PluginConstants.CXF_REQUEST_URI).toString();
        BinarySecurity binarySecurityTokenObject = credential.getBinarySecurityToken();
        String binarySecurityToken = binarySecurityTokenObject.getElement().getFirstChild().getTextContent();
        Base64 base64 = new Base64();
        bearerToken = new String(base64.decode(binarySecurityToken));
        AuthenticationInfo authenticationInfo;
        try {
            authenticationInfo = validateRequest(requestedUri, bearerToken);
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext privilegedCarbonContext = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            privilegedCarbonContext.setTenantId(authenticationInfo.getTenantId());
            privilegedCarbonContext.setTenantDomain(authenticationInfo.getTenantDomain());
            privilegedCarbonContext.setUsername(authenticationInfo.getUsername());

            if (authenticate(binarySecurityToken)) {
                returnCredentials = credential;
            } else {
                String msg = "Authentication failure due to invalid binary security token.";
                log.error(msg);
                throw new WindowsDeviceEnrolmentException(msg);
            }
        } catch (AuthenticationException e) {
            String msg = "Failure occurred in the BST validator.";
            log.error(msg, e);
            throw new WSSecurityException(msg, e);
        } catch (WindowsDeviceEnrolmentException e) {
            String msg = "Authentication Failure occurred due to binary security token.";
            log.error(msg, e);
            throw new WSSecurityException(msg, e);
        } catch (OAuthTokenValidationException e) {
            String msg = "Failed to authenticate the incoming request due to oauth token validation error.";
            log.error(msg, e);
            throw new WSSecurityException(msg, e);
        }
        return returnCredentials;
    }

    /**
     * This method authenticates the user checking the binary security token in the user store.
     *
     * @param binarySecurityToken - Binary security token received in the SOAP message header
     * @return - Authentication status
     * @throws AuthenticationException
     */
    public boolean authenticate(String binarySecurityToken) throws
            AuthenticationException {

        CacheEntry cacheentry = (CacheEntry) DeviceUtil.getCacheEntry(binarySecurityToken);
        String username = cacheentry.getUsername();
        return username != null;
    }

    /**
     * Validate SOAP request token.
     *
     * @param requestedUri-              Requested endpoint URI.
     * @param binarySecurityToken-Binary security token comes from the soap request message.
     * @return returns authorized user information.
     * @throws WindowsDeviceEnrolmentException
     */
    public AuthenticationInfo validateRequest(String requestedUri, String binarySecurityToken)
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
            String msg = "Authentication failure due to invalid binary security token.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }
        return authenticationInfo;
    }
}
