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

package org.wso2.emm.agent.proxy.clients;

import android.util.Log;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.IdentityProxy;
import org.wso2.emm.agent.proxy.R;
import org.wso2.emm.agent.proxy.authenticators.AuthenticatorFactory;
import org.wso2.emm.agent.proxy.authenticators.MutualSSLAuthenticator;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.Keystore;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;

/**
 *  This client provides capability to use mutual authentication.
 */
public class MutualSSLClient implements CommunicationClient {
    private static final String TAG = MutualSSLClient.class.getName();
    private static KeyStore localTrustStore;

    static {
        if (localTrustStore == null) {
            localTrustStore = Keystore.getKeystore(R.raw.truststore,
                                                   Constants.TRUSTSTORE_PASSWORD);
        }
    }

    public RequestQueue getHttpClient() throws IDPTokenManagerException {
        RequestQueue client;
        try {
            if (Constants.SERVER_PROTOCOL.equalsIgnoreCase("https://")) {
                AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory();
                MutualSSLAuthenticator mutualSSLAuthenticator = (MutualSSLAuthenticator)
                        authenticatorFactory.getClient(Constants.Authenticator.
                                                               MUTUAL_SSL_AUTHENTICATOR, null,
                                                       Constants.ADD_HEADER_CALLBACK);
                String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                tmf.init(localTrustStore);


                KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(mutualSSLAuthenticator.
                        getCredentialCertificate(), Constants.KEYSTORE_PASSWORD.toCharArray());

                SSLContext context = SSLContext.getInstance("TLS");
                context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
                final SSLSocketFactory socketFactory = context.getSocketFactory();
                HurlStack hurlStack = new HurlStack() {
                    @Override
                    protected HttpURLConnection createConnection(URL url) throws IOException {
                        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                        httpsURLConnection.setSSLSocketFactory(socketFactory);
                        httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
                        return httpsURLConnection;
                    }
                };
                client = Volley.newRequestQueue(IdentityProxy.getInstance().getContext(), hurlStack);
            } else {
                client = Volley.newRequestQueue(IdentityProxy.getInstance().getContext());
            }
        } catch (KeyStoreException e) {
            String errorMsg = "Error occurred while accessing keystore.";
            Log.e(TAG, errorMsg);
            throw new IDPTokenManagerException(errorMsg, e);
        } catch (NoSuchAlgorithmException e) {
            String errorMsg = "Error occurred while due to mismatch of defined algorithm.";
            Log.e(TAG, errorMsg);
            throw new IDPTokenManagerException(errorMsg, e);
        } catch (UnrecoverableKeyException e) {
            String errorMsg = "Error occurred while accessing keystore.";
            Log.e(TAG, errorMsg);
            throw new IDPTokenManagerException(errorMsg, e);
        } catch (KeyManagementException e) {
            String errorMsg = "Error occurred while accessing keystore.";
            Log.e(TAG, errorMsg);
            throw new IDPTokenManagerException(errorMsg, e);
        }
        return client;
    }

    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify(hostname, session);
            }
        };
    }

    @Override
    public void addAdditionalHeader(Map<String, String> headers) {
            headers.put(Constants.Authenticator.MUTUAL_AUTH_HEADER,
                        Constants.Authenticator.MUTUAL_AUTH_HEADER_VALUE);
    }
}
