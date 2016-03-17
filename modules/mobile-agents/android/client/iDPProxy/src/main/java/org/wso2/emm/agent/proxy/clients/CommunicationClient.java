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

import org.apache.http.client.HttpClient;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;

import java.util.Map;

/**
 * This provides a generic interface that can be use to derive http clients based on underlying
 * communication protocols.
 */
public interface CommunicationClient {

    /**
     * Get a client which uses a secured communication medium such as SSL, TLS
     * @return A secured http client.
     * @throws IDPTokenManagerException
     */
    HttpClient getHttpClient() throws IDPTokenManagerException;


    /**
     * When a request goes through a client addition, defined headers needs to be added and this
     * can be achieved here.
     * @param headers Map of headers to be added.
     */
    void addAdditionalHeader(Map<String, String> headers);
}
