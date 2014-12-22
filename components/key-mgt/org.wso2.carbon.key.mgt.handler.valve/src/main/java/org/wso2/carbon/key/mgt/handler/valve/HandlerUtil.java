/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.key.mgt.handler.valve;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.catalina.connector.Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.core.APIManagerErrorConstants;
import org.wso2.carbon.apimgt.core.authenticate.APITokenValidator;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dto.APIKeyValidationInfoDTO;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.base.IdentityException;
import org.wso2.carbon.identity.core.util.IdentityUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HandlerUtil {

    private static APIKeyValidationInfoDTO apiKeyValidationDTO;
    private static final Log log = LogFactory.getLog(HandlerUtil.class);

    /**
     * Retrieve bearer token form the HTTP header
     * @param bearerToken   Bearer Token extracted out of the corresponding HTTP header
     */
    public static String getAccessToken(String bearerToken) {
        String accessToken = null;
        String[] token = bearerToken.split(HandlerConstants.TOKEN_NAME_BEARER);
        if (token.length > 1 && token[1] != null) {
            accessToken = token[1].trim();
        }
        return accessToken;
    }

    public static String getAPIVersion(HttpServletRequest request) {
        int contextStartsIndex = (request.getRequestURI()).indexOf(request.getContextPath()) + 1;
        int length = request.getContextPath().length();
        String afterContext = (request.getRequestURI()).substring(contextStartsIndex + length);
        int SlashIndex = afterContext.indexOf(("/"));

        if (SlashIndex != -1) {
            return afterContext.substring(0, SlashIndex);
        } else {
            return afterContext;
        }
    }

    public static void handleNoMatchAuthSchemeCallForRestService(Response response,String httpVerb, String reqUri,
                                                                 String version, String context ) {
        String errMsg = "Resource is not matched for HTTP Verb " + httpVerb + ". API context " + context +
                ",version " + version + ", request " + reqUri;
        APIFaultException e = new APIFaultException( APIManagerErrorConstants.API_AUTH_INCORRECT_API_RESOURCE, errMsg);
        String faultPayload = getFaultPayload(e, APIManagerErrorConstants.API_SECURITY_NS,
                APIManagerErrorConstants.API_SECURITY_NS_PREFIX).toString();
        handleRestFailure(response, faultPayload);
    }

    public static boolean doAuthenticate(String context, String version, String accessToken,
                                         String requiredAuthenticationLevel, String clientDomain)
            throws APIManagementException,
            APIFaultException {

        if (APIConstants.AUTH_NO_AUTHENTICATION.equals(requiredAuthenticationLevel)) {
            return true;
        }
        APITokenValidator tokenValidator = new APITokenValidator();
        apiKeyValidationDTO = tokenValidator.validateKey(context, version, accessToken,
                requiredAuthenticationLevel, clientDomain);
        if (apiKeyValidationDTO.isAuthorized()) {
            String userName = apiKeyValidationDTO.getEndUserName();
            PrivilegedCarbonContext.getThreadLocalCarbonContext()
                    .setUsername(apiKeyValidationDTO.getEndUserName());
            try {
                PrivilegedCarbonContext.getThreadLocalCarbonContext()
                        .setTenantId(IdentityUtil.getTenantIdOFUser(userName));
            } catch (IdentityException e) {
                log.error("Error while retrieving Tenant Id", e);
                return false;
            }
            return true;
        } else {
            throw new APIFaultException(apiKeyValidationDTO.getValidationStatus(),
                    "Access failure for API: " + context + ", version: " +
                            version + " with key: " + accessToken);
        }
    }

    public static void handleRestFailure(Response response, String payload) {
        response.setStatus(403);
        response.setContentType("application/xml");
        response.setCharacterEncoding("UTF-8");
        try {
            response.getWriter().write(payload);
        } catch (IOException e) {
            log.error("Error in sending fault response", e);
        }
    }

    public static OMElement getFaultPayload(APIFaultException exception, String FaultNS,
                                            String FaultNSPrefix) {
        OMFactory fac = OMAbstractFactory.getOMFactory();
        OMNamespace ns = fac.createOMNamespace(FaultNS, FaultNSPrefix);
        OMElement payload = fac.createOMElement("fault", ns);

        OMElement errorCode = fac.createOMElement("code", ns);
        errorCode.setText(String.valueOf(exception.getErrorCode()));
        OMElement errorMessage = fac.createOMElement("message", ns);
        errorMessage.setText(APIManagerErrorConstants.getFailureMessage(exception.getErrorCode()));
        OMElement errorDetail = fac.createOMElement("description", ns);
        errorDetail.setText(exception.getMessage());

        payload.addChild(errorCode);
        payload.addChild(errorMessage);
        payload.addChild(errorDetail);
        return payload;
    }

}
