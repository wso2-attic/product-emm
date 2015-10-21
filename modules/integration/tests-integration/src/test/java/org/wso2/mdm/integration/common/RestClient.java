/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.integration.common;

import org.wso2.carbon.automation.test.utils.http.client.HttpRequestUtil;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the rest client that is used to calls to APIs.
 */
public class RestClient {

    private String backEndUrl;
    private String authrizationString;
    private Map<String, String> requestHeaders = new HashMap<String, String>();
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String AUTHORIZATION = "Authorization";

    public RestClient(String backEndUrl, String contentType) {
        this.backEndUrl = backEndUrl;
        this.requestHeaders.put(CONTENT_TYPE, contentType);
    }

    public RestClient(String backEndUrl, String contentType, String authorization) {
        this.backEndUrl = backEndUrl;
        this.requestHeaders.put(CONTENT_TYPE, contentType);
        if(authorization != null || !authorization.isEmpty()){
            this.authrizationString = authorization;
            this.requestHeaders.put(AUTHORIZATION, authorization);
        }
    }

    public String getAuthrizationString() {
        return authrizationString;
    }

    public void setAuthrizationString(String authrizationString) {
        this.authrizationString = authrizationString;
    }

    public void setHttpHeader(String headerName, String value) {
        this.requestHeaders.put(headerName, value);
    }

    public String getHttpHeader(String headerName) {
        return this.requestHeaders.get(headerName);
    }

    public void removeHttpHeader(String headerName) {
        this.requestHeaders.remove(headerName);
    }

    public HttpResponse post(String endpoint, String body) throws Exception {
        return HttpRequestUtil.doPost(new URL(backEndUrl + endpoint), body, requestHeaders);
    }

    public HttpResponse get(String endpoint) throws Exception {
        return HttpRequestUtil.doGet(backEndUrl + endpoint, requestHeaders);
    }
}