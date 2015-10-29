/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * This backendServiceInvoker contains the wrappers for back end jaggary calls.
 */
var backendServiceInvoker = function () {
    var publicXMLHTTPInvokers = {};
    var publicWSInvokers = {};
    var publicHTTPClientInvokers = {};
    var IS_OAUTH_ENABLED = true;

    var constants = require("/modules/constants.js");
    var tokenUtil = require("/modules/api-wrapper-util.js").apiWrapperUtil;
    var log = new Log("modules/backend-service-invoker.js");

    /**
     * This method add Oauth authentication header to outgoing XMLHTTP Requests if Oauth authentication is enabled.
     * @param method HTTP request type.
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    function initiateXMLHTTPRequest(method, url, payload, successCallback, errorCallback) {
        var execute = function () {
            var xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.open(method, url);
            xmlHttpRequest.setRequestHeader(constants.CONTENT_TYPE_IDENTIFIER, constants.APPLICATION_JSON);
            xmlHttpRequest.setRequestHeader(constants.ACCEPT_IDENTIFIER, constants.APPLICATION_JSON);
            if (IS_OAUTH_ENABLED) {
                var accessToken = session.get(constants.ACCESS_TOKEN_PAIR_IDENTIFIER).accessToken;
                    xmlHttpRequest.setRequestHeader(
                        constants.AUTHORIZATION_HEADER, constants.BEARER_PREFIX + accessToken);

            }
            xmlHttpRequest.send((payload));
            if ((xmlHttpRequest.status >= 200 && xmlHttpRequest.status < 300) || xmlHttpRequest.status == 302) {
                if (xmlHttpRequest.responseText != null) {
                    return successCallback(parse(xmlHttpRequest.responseText));
                } else {
                    return successCallback(null);
                }
            } else if (xmlHttpRequest.status == 401) {
                tokenUtil.refreshToken();
                return execute();
            }
            else {
                return errorCallback(parse(xmlHttpRequest.responseText));
            }
        };
        var accessToken = session.get(constants.ACCESS_TOKEN_PAIR_IDENTIFIER).accessToken.trim();
        if (accessToken){
            return execute();
        }else {
            tokenUtil.refreshToken();
            return execute();
        }
    }

    /**
     * This method add Oauth authentication header to outgoing HTTPClient Requests if Oauth authentication is enabled.
     * @param method HTTP request type.
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    function initiateHTTPClientRequest(method, url, payload, successCallback, errorCallback) {
        var HttpClient = Packages.org.apache.commons.httpclient.HttpClient;
        var httpMethodObject;
        switch (method) {
            case constants.HTTP_POST:
                var PostMethod = Packages.org.apache.commons.httpclient.methods.PostMethod;
                httpMethodObject = new PostMethod(url);
                break;
            case constants.HTTP_PUT:
                var PutMethod = Packages.org.apache.commons.httpclient.methods.PutMethod;
                httpMethodObject = new PutMethod(url);
                break;
            case constants.HTTP_GET:
                var GetMethod = Packages.org.apache.commons.httpclient.methods.GetMethod;
                httpMethodObject = new GetMethod(url);
                break;
            case constants.HTTP_DELETE:
                var DeleteMethod = Packages.org.apache.commons.httpclient.methods.DeleteMethod;
                httpMethodObject = new DeleteMethod(url);
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP request type: " + method);
        }
        var Header = Packages.org.apache.commons.httpclient.Header;
        var header = new Header();
        header.setName(constants.CONTENT_TYPE_IDENTIFIER);
        header.setValue(constants.APPLICATION_JSON);
        httpMethodObject.addRequestHeader(header);
        header = new Header();
        header.setName(constants.ACCEPT_IDENTIFIER);
        header.setValue(constants.APPLICATION_JSON);
        httpMethodObject.addRequestHeader(header);
        if (IS_OAUTH_ENABLED) {
            var accessToken = session.get(constants.ACCESS_TOKEN_PAIR_IDENTIFIER).accessToken;
            if (!(!accessToken.trim())) {
                header = new Header();
                header.setName(constants.AUTHORIZATION_HEADER);
                header.setValue(constants.BEARER_PREFIX + accessToken);
                httpMethodObject.addRequestHeader(header);
            }

        }
        var stringRequestEntity = new StringRequestEntity(stringify(payload));
        httpMethodObject.setRequestEntity(stringRequestEntity);
        var client = new HttpClient();
        try {
            client.executeMethod(httpMethodObject);
            var status = httpMethodObject.getStatusCode();
            if (status == 200) {
                return successCallback(httpMethodObject.getResponseBody());
            } else {
                return errorCallback(httpMethodObject.getResponseBody());
            }
        } catch (e) {
            return errorCallback(response);
        } finally {
            method.releaseConnection();
        }
    }

    /**
     * This method add Oauth authentication header to outgoing WS Requests if Oauth authentication is enabled.
     * @param action
     * @param endpoint service end point to be triggered.
     * @param payload soap payload which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     * @param soapVersion soapVersion which need to used.
     */
    function initiateWSRequest(action, endpoint, payload, successCallback, errorCallback, soapVersion) {
        var ws = require('ws');
        var wsRequest = new ws.WSRequest();
        var options = new Array();
        if (IS_OAUTH_ENABLED) {
            var accessToken = session.get(constants.ACCESS_TOKEN_PAIR_IDENTIFIER).accessToken;
            if (!(!accessToken)) {
                var authenticationHeaderName = String(constants.AUTHORIZATION_HEADER);
                var authenticationHeaderValue =String(constants.BEARER_PREFIX + accessToken);
                var headers = [];
                var oAuthAuthenticationData = {};
                oAuthAuthenticationData.name =  authenticationHeaderName;
                oAuthAuthenticationData.value = authenticationHeaderValue;
                headers.push(oAuthAuthenticationData);
                options.HTTPHeaders = headers;
            }
        }
        options.useSOAP = soapVersion;
        options.useWSA = constants.WEB_SERVICE_ADDRESSING_VERSION;
        options.action = action;
        var wsResponse;
        try {
            wsRequest.open(options, endpoint, false);
            wsRequest.send(payload);
            wsResponse = wsRequest.responseE4X;
        } catch (e) {
            return errorCallback(e);
        }
        return successCallback(wsResponse);
    }

    /**
     * This method invokes return initiateXMLHttpRequest for get calls
     * @param url target url.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicXMLHTTPInvokers.get = function (url, successCallback, errorCallback) {
        var payload = null;
        return initiateXMLHTTPRequest(constants.HTTP_GET, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for post calls
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicXMLHTTPInvokers.post = function (url, payload, successCallback, errorCallback) {
        return initiateXMLHTTPRequest(constants.HTTP_POST, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for put calls
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicXMLHTTPInvokers.put = function (url, payload, successCallback, errorCallback) {
        return initiateXMLHTTPRequest(constants.HTTP_PUT, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for delete calls
     * @param url target url.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicXMLHTTPInvokers.delete = function (url, successCallback, errorCallback) {
        var payload = null;
        return initiateXMLHTTPRequest(constants.HTTP_DELETE, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateWSRequest for soap calls
     * @param endpoint service end point to be triggered.
     * @param payload soap payload which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     * @param soapVersion soapVersion which need to used.
     */
    publicWSInvokers.soapRequest = function (action, endpoint, payload, successCallback, errorCallback, soapVersion) {
        return initiateWSRequest(action, endpoint, payload, successCallback, errorCallback, soapVersion);
    };


    /**
     * This method invokes return initiateHTTPClientRequest for get calls
     * @param url target url.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.get = function (url, successCallback, errorCallback) {
        var payload = null;
        return initiateHTTPClientRequest(constants.HTTP_GET, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateHTTPClientRequest for post calls
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.post = function (url, payload, successCallback, errorCallback) {
        return initiateHTTPClientRequest(constants.HTTP_POST, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateHTTPClientRequest for put calls
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.put = function (url, payload, successCallback, errorCallback) {
        return initiateHTTPClientRequest(constants.HTTP_PUT, url, payload, successCallback, errorCallback);
    };

    /**
     * This method invokes return initiateHTTPClientRequest for delete calls
     * @param url target url.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.delete = function (url, successCallback, errorCallback) {
        var payload = null;
        return initiateHTTPClientRequest(constants.HTTP_DELETE, url, payload, successCallback, errorCallback);
    };

    var publicInvokers = {};
    publicInvokers.XMLHttp = publicXMLHTTPInvokers;
    publicInvokers.WS = publicWSInvokers;
    publicInvokers.HttpClient = publicHTTPClientInvokers;
    return publicInvokers;
}();