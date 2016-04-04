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
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.IdentityProxy;
import org.wso2.emm.agent.proxy.R;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.StreamHandlerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

public class OAuthSSLClient implements CommunicationClient {
    private static final String TAG = OAuthSSLClient.class.getName();

    @Override
    public HttpClient getHttpClient() throws IDPTokenManagerException {
        HttpClient client = null;
        InputStream inStream = null;
        try {
            if (Constants.SERVER_PROTOCOL.equalsIgnoreCase("https://")) {
                KeyStore localTrustStore = KeyStore.getInstance("BKS");
                inStream = IdentityProxy.getInstance().getContext().getResources().
                        openRawResource(R.raw.truststore);
                localTrustStore.load(inStream, Constants.TRUSTSTORE_PASSWORD.toCharArray());

                SchemeRegistry schemeRegistry = new SchemeRegistry();
                schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(),
                                                   Constants.HTTP));
                SSLSocketFactory sslSocketFactory = new SSLSocketFactory(localTrustStore);
                sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                schemeRegistry.register(new Scheme("https", sslSocketFactory, Constants.HTTPS));
                HttpParams params = new BasicHttpParams();
                ClientConnectionManager connectionManager =
                        new ThreadSafeClientConnManager(params, schemeRegistry);

                client = new DefaultHttpClient(connectionManager, params);

            } else {
                client = new DefaultHttpClient();
            }

        } catch (KeyStoreException e) {
            String errorMsg = "Error occurred while accessing keystore.";
            Log.e(TAG, errorMsg);
            throw new IDPTokenManagerException(errorMsg, e);
        } catch (CertificateException e) {
            String errorMsg = "Error occurred while loading certificate.";
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
        } catch (IOException e) {
            String errorMsg = "Error occurred while loading trust store. ";
            Log.e(TAG, errorMsg);
            throw new IDPTokenManagerException(errorMsg, e);
        } finally {
            StreamHandlerUtil.closeInputStream(inStream, TAG);
        }
        return client;
    }

    //TODO: Move oauth specific bits in Agent source to proxy.
    @Override
    public void addAdditionalHeader(Map<String, String> headers) {

    }
}
