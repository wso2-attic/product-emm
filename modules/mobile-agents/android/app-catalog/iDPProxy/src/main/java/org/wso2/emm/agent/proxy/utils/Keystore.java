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

package org.wso2.emm.agent.proxy.utils;

import android.util.Log;
import org.wso2.emm.agent.proxy.IdentityProxy;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class Keystore {
    private static final String TAG = Keystore.class.getName();

    public static KeyStore getKeystore(int resourceID, String password) {
        KeyStore keystore = null;
        try {
            keystore = KeyStore.getInstance(Constants.BKS);
        } catch (KeyStoreException e) {
            Log.e(TAG, "Failed to get an instance of BKS: ", e);
        }
        InputStream inStreamClient = IdentityProxy.getInstance().getContext().getResources().
                openRawResource(resourceID);
        try {
            keystore.load(inStreamClient, password.toCharArray());
        } catch (IOException | NoSuchAlgorithmException | CertificateException e) {
            Log.e(TAG, "Failed to get certificates: ", e);
        } finally {
            StreamHandlerUtil.closeInputStream(inStreamClient, TAG);
        }
        return keystore;
    }
}
