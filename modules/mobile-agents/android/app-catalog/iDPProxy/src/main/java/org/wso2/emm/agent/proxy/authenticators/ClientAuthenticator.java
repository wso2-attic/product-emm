/*
 *   Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *   WSO2 Inc. licenses this file to you under the Apache License,
 *   Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.wso2.emm.agent.proxy.authenticators;

import java.security.KeyStore;
import java.util.Map;

/**
 * This is an interface for authenticator where different type of authentication
 * methods can be plugged in by implementing the authenticator.
 */
public interface ClientAuthenticator {
    /**
     * Perform the authentication and using a callback, the result is given back to the caller.
     */
    void doAuthenticate();

    /**
     * Get a certificate that can be used in the authentication for providing authenticity to
     * a request.
     * @return A Keystore with the certificates inside.
     */
    KeyStore getCredentialCertificate();

    /**
     * Get any data that can be used as a authentication credentials.
     * @return A map of credentials and relevant data.
     */
    Map<String, String> getCredentialKey();
}
