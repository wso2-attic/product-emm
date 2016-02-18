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

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.wso2.carbon.identity.oauth2.stub.OAuth2TokenValidationServiceStub;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.OAuthTokenValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OAuthTokenValidationStubFactory implements PoolableObjectFactory {
    private String url;
    private String basicAuthHeader;
    private HttpClient httpClient;

    private static final Log log = LogFactory.getLog(OAuthTokenValidationStubFactory.class);

    public OAuthTokenValidationStubFactory(String url, String adminUsername, String adminPassword,
                                           Properties properties) {
        this.validateUrl(url);
        this.url = url;

        this.validateCredentials(adminUsername, adminPassword);
        this.basicAuthHeader = new String(Base64.encodeBase64((adminUsername + ":" + adminPassword).getBytes()));

        HttpConnectionManager connectionManager = this.createConnectionManager(properties);
        this.httpClient = new HttpClient(connectionManager);
    }

    /**
     * Creates an instance of MultiThreadedHttpConnectionManager using HttpClient 3.x APIs
     *
     * @param properties Properties to configure MultiThreadedHttpConnectionManager
     * @return An instance of properly configured MultiThreadedHttpConnectionManager
     */
    private HttpConnectionManager createConnectionManager(Properties properties) {
        HttpConnectionManagerParams params = new HttpConnectionManagerParams();
        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("Parameters required to initialize HttpClient instances " +
                    "associated with OAuth token validation service stub are not provided");
        }
        String maxConnectionsPerHostParam = properties.getProperty(PluginConstants.
                AuthenticatorProperties.MAX_CONNECTION_PER_HOST);
        if (maxConnectionsPerHostParam == null || maxConnectionsPerHostParam.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("MaxConnectionsPerHost parameter is not explicitly defined. Therefore, the default, " +
                        "which is 2, will be used");
            }
        } else {
            params.setDefaultMaxConnectionsPerHost(Integer.parseInt(maxConnectionsPerHostParam));
        }

        String maxTotalConnectionsParam = properties.getProperty(PluginConstants.
                AuthenticatorProperties.MAX_TOTAL_CONNECTIONS);
        if (maxTotalConnectionsParam == null || maxTotalConnectionsParam.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug("MaxTotalConnections parameter is not explicitly defined. Therefore, the default, " +
                        "which is 10, will be used");
            }
        } else {
            params.setMaxTotalConnections(Integer.parseInt(maxTotalConnectionsParam));
        }
        HttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.setParams(params);
        return connectionManager;
    }

    /**
     * Creates an instance of PoolingHttpClientConnectionManager using HttpClient 4.x APIs
     *
     * @param properties Properties to configure PoolingHttpClientConnectionManager
     * @return An instance of properly configured PoolingHttpClientConnectionManager
     */
    private HttpClientConnectionManager createClientConnectionManager(Properties properties) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        if (properties != null) {
            String maxConnectionsPerHostParam = properties.getProperty(PluginConstants.
                    AuthenticatorProperties.MAX_CONNECTION_PER_HOST);
            if (maxConnectionsPerHostParam == null || maxConnectionsPerHostParam.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("MaxConnectionsPerHost parameter is not explicitly defined. Therefore, the default, " +
                            "which is 2, will be used");
                }
            } else {
                connectionManager.setDefaultMaxPerRoute(Integer.parseInt(maxConnectionsPerHostParam));
            }

            String maxTotalConnectionsParam = properties.getProperty(PluginConstants.
                    AuthenticatorProperties.MAX_TOTAL_CONNECTIONS);
            if (maxTotalConnectionsParam == null || maxTotalConnectionsParam.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("MaxTotalConnections parameter is not explicitly defined. Therefore, the default, " +
                            "which is 10, will be used");
                }
            } else {
                connectionManager.setMaxTotal(Integer.parseInt(maxTotalConnectionsParam));
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Properties, i.e. MaxTotalConnections/MaxConnectionsPerHost, required to tune the " +
                        "HttpClient used in OAuth token validation service stub instances are not provided. " +
                        "Therefore, the defaults, 2/10 respectively, will be used");
            }
        }
        return connectionManager;
    }

    @Override
    public Object makeObject() throws Exception {
        return this.createStub();
    }

    @Override
    public void destroyObject(Object o) throws Exception {

    }

    @Override
    public boolean validateObject(Object o) {
        return true;
    }

    @Override
    public void activateObject(Object o) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("OAuth token validate stub instance is activated");
        }
    }

    @Override
    public void passivateObject(Object o) throws Exception {
        if (o instanceof OAuth2TokenValidationServiceStub) {
            OAuth2TokenValidationServiceStub stub = (OAuth2TokenValidationServiceStub) o;
            stub._getServiceClient().cleanupTransport();
        }
    }

    private OAuth2TokenValidationServiceStub createStub() throws OAuthTokenValidationException {
        OAuth2TokenValidationServiceStub stub;
        try {
            stub = new OAuth2TokenValidationServiceStub(url);
            ServiceClient client = stub._getServiceClient();
            client.getServiceContext().getConfigurationContext().setProperty(
                    HTTPConstants.CACHED_HTTP_CLIENT, httpClient);

            List<Header> headerList = new ArrayList<>();
            Header header = new Header();
            header.setName(HTTPConstants.HEADER_AUTHORIZATION);
            header.setValue(OAuthConstants.AUTHORIZATION_HEADER_PREFIX_BASIC + " " + basicAuthHeader);
            headerList.add(header);

            Options options = client.getOptions();
            options.setProperty(HTTPConstants.HTTP_HEADERS, headerList);
            options.setProperty(HTTPConstants.REUSE_HTTP_CLIENT, "true");
            client.setOptions(options);
        } catch (AxisFault axisFault) {
            throw new OAuthTokenValidationException("Error occurred while creating the " +
                    "OAuth2TokenValidationServiceStub.", axisFault);
        }
        return stub;
    }

    private void validateUrl(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Url provided as the endpoint of the OAuth token validation service " +
                    "is null");
        }
    }

    private void validateCredentials(String adminUsername, String adminPassword) {
        if (adminUsername == null || adminUsername.isEmpty()) {
            throw new IllegalArgumentException("An appropriate username required to initialize OAuth token " +
                    "validation service stub factory hasn't been provided");
        }
        if (adminPassword == null || adminPassword.isEmpty()) {
            throw new IllegalArgumentException("An appropriate password required to initialize OAuth token " +
                    "validation service stub factory hasn't been provided");
        }
    }

}
