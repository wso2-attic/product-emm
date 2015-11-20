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
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2TokenValidationResponseDTO;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.AuthenticationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.util.HashMap;

/**
 * Validator class for user authentication checking the default carbon user store.
 */
public class BSTValidator implements Validator {

    private static Log log = LogFactory.getLog(BSTValidator.class);
    private static final String BEARER_TOKEN_TYPE = "bearer";
    private static final String RESOURCE_KEY = "resource";

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
        String encodedBinarySecurityToken;
        String requestedUri;
        Credential returnCredentials = null;

        HashMap msgContext = (HashMap) requestData.getMsgContext();
        requestedUri = msgContext.get(PluginConstants.CXF_REQUEST_URI).toString();
        BinarySecurity binarySecurityTokenObject = credential.getBinarySecurityToken();
        String binarySecurityToken = binarySecurityTokenObject.getElement().getFirstChild().getTextContent();
        Base64 base64 = new Base64();
        encodedBinarySecurityToken = new String(base64.decode(binarySecurityToken));
        AuthenticationInfo authenticationInfo;
        try {
            authenticationInfo = validateRequest(requestedUri, encodedBinarySecurityToken);
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
     * @param requestedUri-                     Requested endpoint URI.
     * @param encodedBinarySecurityToken-Binary security token comes from the soap request message.
     * @return returns authorized user information.
     * @throws WindowsDeviceEnrolmentException
     */
    public AuthenticationInfo validateRequest(String requestedUri, String encodedBinarySecurityToken)
            throws WindowsDeviceEnrolmentException {

        AuthenticationInfo authenticationInfo = new AuthenticationInfo();
        // Create a OAuth2TokenValidationRequestDTO object for validating access token
        OAuth2TokenValidationRequestDTO dto = new OAuth2TokenValidationRequestDTO();
        //Set the access token info
        OAuth2TokenValidationRequestDTO.OAuth2AccessToken oAuth2AccessToken = dto.new OAuth2AccessToken();
        oAuth2AccessToken.setTokenType(BSTValidator.BEARER_TOKEN_TYPE);
        oAuth2AccessToken.setIdentifier(encodedBinarySecurityToken);
        dto.setAccessToken(oAuth2AccessToken);

        //Set the resource context param. This will be used in scope validation.
        OAuth2TokenValidationRequestDTO.TokenValidationContextParam
                resourceContextParam = dto.new TokenValidationContextParam();
        resourceContextParam.setKey(BSTValidator.RESOURCE_KEY);
        resourceContextParam.setValue(requestedUri + ":POST");

        OAuth2TokenValidationRequestDTO.TokenValidationContextParam[]
                tokenValidationContextParams =
                new OAuth2TokenValidationRequestDTO.TokenValidationContextParam[1];
        tokenValidationContextParams[0] = resourceContextParam;
        dto.setContext(tokenValidationContextParams);
        try {
            OAuth2TokenValidationResponseDTO oAuth2TokenValidationResponseDTO =
                    WindowsAPIUtils.getOAuth2TokenValidationService().validate(dto);
            if (oAuth2TokenValidationResponseDTO.isValid()) {
                String username = oAuth2TokenValidationResponseDTO.getAuthorizedUser();
                authenticationInfo.setUsername(username);
                authenticationInfo.setTenantDomain(MultitenantUtils.getTenantDomain(username));
                authenticationInfo.setTenantId(WindowsAPIUtils.getTenantIdOFUser(username));
            } else {
                authenticationInfo.setMessage(oAuth2TokenValidationResponseDTO.getErrorMsg());
            }
        } catch (DeviceManagementException e) {
            String msg = "Authentication failure due to invalid binary security token.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }
        return authenticationInfo;
    }
}
