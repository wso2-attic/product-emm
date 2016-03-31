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

import android.content.Context;
import org.wso2.emm.agent.proxy.R;
import org.wso2.emm.agent.proxy.interfaces.AuthenticationCallback;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.Keystore;

import java.security.KeyStore;
import java.util.Map;

/**
 * Logic relevant to mutual authentication is written here.
 */
public class MutualSSLAuthenticator implements ClientAuthenticator {
    private static KeyStore localKeyStore = null;
    private static Context context = null;
    private static AuthenticationCallback callback = null;
    private static int requestCode = 0;

    @Override
    public void doAuthenticate() {
        callback.onAuthenticated(true, requestCode);
    }

    @Override
    public KeyStore getCredentialCertificate() {
        if (localKeyStore == null) {
            localKeyStore = Keystore.getKeystore(R.raw.keystore,
                                                 Constants.KEYSTORE_PASSWORD);
        }
        return localKeyStore;
    }

    @Override
    public Map<String, String> getCredentialKey() {
        return null;
    }

    public MutualSSLAuthenticator(Context context, AuthenticationCallback callback,
                                  int requestCode) {
        MutualSSLAuthenticator.context = context;
        MutualSSLAuthenticator.callback = callback;
        MutualSSLAuthenticator.requestCode = requestCode;
    }

}
