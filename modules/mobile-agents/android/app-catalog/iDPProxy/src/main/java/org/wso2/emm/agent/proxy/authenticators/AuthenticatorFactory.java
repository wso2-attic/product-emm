/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.emm.agent.proxy.authenticators;

import org.wso2.emm.agent.proxy.IdentityProxy;
import org.wso2.emm.agent.proxy.interfaces.AuthenticationCallback;

/**
 * Creates new instances of authenticators based on the pre-configured authenticators.
 */
public class AuthenticatorFactory {
    public ClientAuthenticator getClient(String authenticatorType, AuthenticationCallback callback,
                                         int requestCode) {
        if (authenticatorType == null) {
            return null;
        }
        if (authenticatorType.equalsIgnoreCase("MUTUAL_SSL_AUTHENTICATOR")) {
            return new MutualSSLAuthenticator(IdentityProxy.getInstance().getContext(), callback,
                                              requestCode);
        } else if (authenticatorType.equalsIgnoreCase("OAUTH_AUTHENTICATOR")) {
            return new OAuthAuthenticator();
        }

        return null;
    }
}
