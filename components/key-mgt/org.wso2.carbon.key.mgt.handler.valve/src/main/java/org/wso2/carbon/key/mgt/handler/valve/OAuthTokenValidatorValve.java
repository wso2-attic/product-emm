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

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.api.APIManagementException;
import org.wso2.carbon.apimgt.core.authenticate.APITokenValidator;
import org.wso2.carbon.apimgt.core.gateway.APITokenAuthenticator;
import org.wso2.carbon.apimgt.impl.APIConstants;
import org.wso2.carbon.apimgt.impl.dao.ApiMgtDAO;
import org.wso2.carbon.apimgt.impl.utils.APIUtil;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Enumeration;

public class OAuthTokenValidatorValve extends ValveBase {

    private static final Log log = LogFactory.getLog(OAuthTokenValidatorValve.class);

    APITokenAuthenticator authenticator;

    public OAuthTokenValidatorValve() {
        authenticator = new APITokenAuthenticator();
    }

    @Override
    public void invoke(Request request, Response response) throws java.io.IOException, javax.servlet.ServletException {
        String context = request.getContextPath();
        if (context == null || context.equals("")) {
            //Invoke the next valve in handler chain.
            getNext().invoke(request, response);
            return;
        }

        boolean contextExist;
        Boolean contextValueInCache = null;
        if (APIUtil.getAPIContextCache().get(context) != null) {
            contextValueInCache = Boolean.parseBoolean(APIUtil.getAPIContextCache().get(context).toString());
        }

        if (contextValueInCache != null) {
            contextExist = contextValueInCache;
        } else {
            contextExist = ApiMgtDAO.isContextExist(context);
            APIUtil.getAPIContextCache().put(context, contextExist);
        }

        if (!contextExist) {
            getNext().invoke(request, response);
            return;
        }

        try {
            handleWSDLGetRequest(request, response, context);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        String authHeader = request.getHeader(APIConstants.OperationParameter.AUTH_PARAM_NAME);
        String accessToken = null;

            /* Authenticate*/
        try {
            if (authHeader != null) {
                accessToken = HandlerUtil.getAccessToken(authHeader);
            } else {
                // There can be some API published with None Auth Type
                    /*
                     * throw new
					 * APIFaultException(APIConstants.KeyValidationStatus
					 * .API_AUTH_INVALID_CREDENTIALS,
					 * "Invalid format for Authorization header. Expected 'Bearer <token>'"
					 * );
					 */
            }

            String apiVersion = HandlerUtil.getAPIVersion(request);
            String domain = request.getHeader(APITokenValidator.getAPIManagerClientDomainHeader());
            String authLevel = authenticator.getResourceAuthenticationScheme(context,
                    apiVersion,
                    request.getRequestURI(),
                    request.getMethod());
            if (HandlerConstants.NO_MATCHING_AUTH_SCHEME.equals(authLevel)) {
                HandlerUtil.handleNoMatchAuthSchemeCallForRestService(response,
                        request.getMethod(), request.getRequestURI(),
                        apiVersion, context);
                return;
            } else {
                HandlerUtil.doAuthenticate(context, apiVersion, accessToken, authLevel, domain);
            }
        } catch (APIManagementException e) {
            //ignore
        } catch (APIFaultException e) {
            log.error("Error occurred while key validation", e);
            return;
        }

        getNext().invoke(request, response);
    }

    private void handleWSDLGetRequest(Request request, Response response,
                                      String context) throws IOException, ServletException {
        if (request.getMethod().equals("GET")) {
            // TODO:Need to get these paths from a config file.
            if (request.getRequestURI().matches(context + "/[^/]*/services")) {
                getNext().invoke(request, response);
                return;
            }
            Enumeration<String> params = request.getParameterNames();
            String paramName;
            while (params.hasMoreElements()) {
                paramName = params.nextElement();
                if (paramName.endsWith("wsdl") || paramName.endsWith("wadl")) {
                    getNext().invoke(request, response);
                    return;
                }
            }
        }
    }

}
