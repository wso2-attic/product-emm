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

package org.wso2.carbon.mdm.mobileservices.windows.common.authenticator.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.wso2.carbon.identity.oauth2.stub.OAuth2TokenValidationServiceStub;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationRequestDTO;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationRequestDTO_OAuth2AccessToken;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationRequestDTO_TokenValidationContextParam;
import org.wso2.carbon.identity.oauth2.stub.dto.OAuth2TokenValidationResponseDTO;
import org.wso2.carbon.mdm.mobileservices.windows.common.authenticator.OAuth2TokenValidator;
import org.wso2.carbon.mdm.mobileservices.windows.common.authenticator.OAuthTokenValidationStubFactory;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.OAuthTokenValidationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.OAuthValidationResponse;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import java.rmi.RemoteException;
import java.util.Properties;

/**
 * Handles the OAuth2 token validation from remote IS servers using remote OAuthValidation service-stub.
 */
public class RemoteOAuthValidator implements OAuth2TokenValidator {

    private GenericObjectPool stubs;
    private static final Log log = LogFactory.getLog(RemoteOAuthValidator.class);

    public RemoteOAuthValidator(String hostURL, String adminUsername, String adminPassword, Properties properties) {
        this.stubs =
                new GenericObjectPool(new OAuthTokenValidationStubFactory(
                        hostURL, adminUsername, adminPassword, properties));
    }

    public OAuthValidationResponse validateToken(String accessToken,
                                                 String resource) throws OAuthTokenValidationException {
        OAuth2TokenValidationServiceStub stub = null;
        OAuth2TokenValidationResponseDTO validationResponse;
        try {
            OAuth2TokenValidationRequestDTO validationRequest = createValidationRequest(accessToken, resource);
            stub = (OAuth2TokenValidationServiceStub) this.stubs.borrowObject();
            validationResponse =
                    stub.findOAuthConsumerIfTokenIsValid(validationRequest).getAccessTokenValidationResponse();
        } catch (RemoteException e) {
            throw new OAuthTokenValidationException("Remote Exception occurred while invoking the Remote " +
                    "IS server for OAuth2 token validation.", e);
        } catch (Exception e) {
            throw new OAuthTokenValidationException("Error occurred while borrowing an oauth token validation " +
                    "service stub from the pool", e);
        } finally {
            try {
                this.stubs.returnObject(stub);
            } catch (Exception e) {
                log.warn("Error occurred while returning the object back to the oauth token validation service " +
                        "stub pool", e);
            }
        }

        if (validationResponse == null) {
            if (log.isDebugEnabled()) {
                log.debug("Response returned by the OAuth token validation service is null");
            }
            return null;
        }

        boolean isValid = validationResponse.getValid();
        String tenantDomain;
        String username;
        if (isValid) {
            username = MultitenantUtils.getTenantAwareUsername(validationResponse.getAuthorizedUser());
            tenantDomain = MultitenantUtils.getTenantDomain(validationResponse.getAuthorizedUser());
        } else {
            OAuthValidationResponse oAuthValidationResponse = new OAuthValidationResponse();
            oAuthValidationResponse.setErrorMsg(validationResponse.getErrorMsg());
            return oAuthValidationResponse;
        }
        return new OAuthValidationResponse(username, tenantDomain, isValid);
    }

    private OAuth2TokenValidationRequestDTO createValidationRequest(String accessToken, String resource) {
        OAuth2TokenValidationRequestDTO validationRequest = new OAuth2TokenValidationRequestDTO();
        OAuth2TokenValidationRequestDTO_OAuth2AccessToken oauthToken =
                new OAuth2TokenValidationRequestDTO_OAuth2AccessToken();

        oauthToken.setTokenType("bearer");
        oauthToken.setIdentifier(accessToken);
        validationRequest.setAccessToken(oauthToken);

        OAuth2TokenValidationRequestDTO_TokenValidationContextParam resourceContextParam =
                new OAuth2TokenValidationRequestDTO_TokenValidationContextParam();

        resourceContextParam.setKey("resource");
        resourceContextParam.setValue(resource);

        OAuth2TokenValidationRequestDTO_TokenValidationContextParam[] tokenValidationContextParams =
                new OAuth2TokenValidationRequestDTO_TokenValidationContextParam[1];

        tokenValidationContextParams[0] = resourceContextParam;
        validationRequest.setContext(tokenValidationContextParams);

        return validationRequest;
    }

}
