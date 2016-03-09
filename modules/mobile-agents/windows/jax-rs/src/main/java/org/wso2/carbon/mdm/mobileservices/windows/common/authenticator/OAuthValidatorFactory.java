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

package org.wso2.carbon.mdm.mobileservices.windows.common.authenticator;

import org.wso2.carbon.mdm.mobileservices.windows.common.authenticator.impl.LocalOAuthValidator;
import org.wso2.carbon.mdm.mobileservices.windows.common.authenticator.impl.RemoteOAuthValidator;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.webapp.authenticator.framework.config.AuthenticatorConfig;

import java.util.Properties;

/**
 * The class validate the configurations and provide the most suitable implementation according to the configuration.
 * Factory class for OAuthValidator.
 */
public class OAuthValidatorFactory {

    private static Properties authenticatorProperties;

    public static OAuth2TokenValidator getValidator() {
        Properties authenticatorProperties = getAuthenticatorProperties();
        boolean isRemote = Boolean.parseBoolean(authenticatorProperties.getProperty("IsRemote"));
        if (isRemote) {
            String url = authenticatorProperties.getProperty("TokenValidationEndpointUrl");
            if ((url == null) || (url.isEmpty())) {
                throw new IllegalStateException("OAuth token validation endpoint url is not provided");
            }
            String adminUsername = authenticatorProperties.getProperty("Username");
            if (adminUsername == null) {
                throw new IllegalStateException("Username to connect to the OAuth token validation endpoint " +
                        "is not provided");
            }

            String adminPassword = authenticatorProperties.getProperty("Password");
            if (adminPassword == null) {
                throw new IllegalStateException("Password to connect to the OAuth token validation endpoint " +
                        "is not provided");
            }

            Properties validatorProperties = new Properties();
            validatorProperties.setProperty("MaxTotalConnections", authenticatorProperties.getProperty("MaxTotalConnections"));
            validatorProperties.setProperty("MaxConnectionsPerHost", authenticatorProperties.getProperty("MaxConnectionsPerHost"));
            if ((url != null) && (!url.trim().isEmpty())) {
                url = url + "/services/OAuth2TokenValidationService.OAuth2TokenValidationServiceHttpsSoap12Endpoint/";
                return new RemoteOAuthValidator(url, adminUsername, adminPassword, validatorProperties);
            }
            throw new IllegalStateException("Remote server host can't be empty in OAuthAuthenticator configuration.");
        }
        return new LocalOAuthValidator();
    }

    private static Properties getAuthenticatorProperties() {
        if (authenticatorProperties == null) {
            AuthenticatorConfig config = WindowsAPIUtils.getBSTAuthenticatorConfig();
            if ((config.getParams() != null) && (!config.getParams().isEmpty())) {
                Properties properties = new Properties();
                for (AuthenticatorConfig.Parameter param : config.getParams()) {
                    properties.setProperty(param.getName(), param.getValue());
                }
                authenticatorProperties = properties;
            }
        }
        return authenticatorProperties;
    }
}
