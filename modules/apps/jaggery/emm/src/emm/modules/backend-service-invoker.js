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
 * This backendServiceInvoker contains the wrappers for back end jaggery calls.
 */
var backendServiceInvoker = function () {
    var log = new Log("modules/backend-service-invoker.js");

    var publicXMLHTTPInvokers = {};
    var publicHTTPClientInvokers = {};

    var privateMethods = {};
    var publicWSInvokers = {};

    var TOKEN_EXPIRED = "Access token expired";
    var TOKEN_INVALID = "Invalid input. Access token validation failed";

    var mdmProps = require("/config/mdm-props.js").config();
    var constants = require("/modules/constants.js");
    var userModule = require("/modules/user.js")["userModule"];
    var tokenUtil = require("/modules/api-wrapper-util.js")["apiWrapperUtil"];

    /**
     * This method reads the token pair from the session and return the access token.
     * If the token pair s not set in the session this will send a redirect to the login page.
     */
    privateMethods.getAccessToken = function () {
        var tokenPair = session.get(constants["ACCESS_TOKEN_PAIR_IDENTIFIER"]);
        if (tokenPair) {
            return tokenPair.accessToken;
        } else {
            return null;
        }
    };

    /**
     * ---------------------------------------------------------------------------
     * Start of XML-HTTP-REQUEST based Interceptor implementations
     * ---------------------------------------------------------------------------
     */

    /**
     * This method add Oauth authentication header to outgoing XML-HTTP Requests if Oauth authentication is enabled.
     * @param httpMethod HTTP request type.
     * @param requestPayload payload/data if exists which is needed to be send.
     * @param endpoint Backend REST API url.
     * @param responseCallback a function to be called with response retrieved.
     * @param count a counter which hold the number of recursive execution
     */
    privateMethods.execute = function (httpMethod, requestPayload, endpoint, responseCallback, count) {
        var xmlHttpRequest = new XMLHttpRequest();
        xmlHttpRequest.open(httpMethod, endpoint);
        xmlHttpRequest.setRequestHeader(constants["CONTENT_TYPE_IDENTIFIER"], constants["APPLICATION_JSON"]);
        xmlHttpRequest.setRequestHeader(constants["ACCEPT_IDENTIFIER"], constants["APPLICATION_JSON"]);

        if (mdmProps["isOAuthEnabled"]) {
            var accessToken = privateMethods.getAccessToken();
            if (!accessToken) {
                userModule.logout(function () {
                    response.sendRedirect(mdmProps["appContext"] + "login");
                });
            } else {
                xmlHttpRequest.
                    setRequestHeader(constants["AUTHORIZATION_HEADER"], constants["BEARER_PREFIX"] + accessToken);
            }
        }

        if (requestPayload) {
            xmlHttpRequest.send(requestPayload);
        } else {
            xmlHttpRequest.send();
        }

        log.debug("Service Invoker-URL: " + endpoint);
        log.debug("Service Invoker-Method: " + httpMethod);

        log.info("Request : " + httpMethod + " " + endpoint);
        log.info("Request payload if any : " + stringify(requestPayload));
        log.info("Response status : " + xmlHttpRequest.status);
        log.info("Response payload if any : " + xmlHttpRequest.responseText);
        //log.info("Response headers : " + xmlHttpRequest.getAllResponseHeaders());

        if (xmlHttpRequest.status == 401 && (xmlHttpRequest.responseText == TOKEN_EXPIRED ||
            xmlHttpRequest.responseText == TOKEN_INVALID ) && count < 5) {
            tokenUtil.refreshToken();
            return privateMethods.execute(httpMethod, requestPayload, endpoint, responseCallback, ++count);
        } else {
            return responseCallback(xmlHttpRequest);
        }
    };

    /**
     * This method add Oauth authentication header to outgoing XML-HTTP Requests if Oauth authentication is enabled.
     * @param httpMethod HTTP request type.
     * @param requestPayload payload/data if exists which is needed to be send.
     * @param endpoint Backend REST API url.
     * @param responseCallback a function to be called with response retrieved.
     */
    privateMethods.initiateXMLHTTPRequest = function (httpMethod, requestPayload, endpoint, responseCallback) {
        return privateMethods.execute(httpMethod, requestPayload, endpoint, responseCallback, 0);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for get calls
     * @param endpoint Backend REST API url.
     * @param responseCallback a function to be called with response retrieved.
     */
    publicXMLHTTPInvokers.get = function (endpoint, responseCallback) {
        var requestPayload = null;
        return privateMethods.initiateXMLHTTPRequest(constants["HTTP_GET"], requestPayload, endpoint, responseCallback);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for post calls
     * @param endpoint Backend REST API url.
     * @param requestPayload payload/data if exists which is needed to be send.
     * @param responseCallback a function to be called with response retrieved.
     */
    publicXMLHTTPInvokers.post = function (endpoint, requestPayload, responseCallback) {
        return privateMethods.initiateXMLHTTPRequest(constants["HTTP_POST"], requestPayload, endpoint, responseCallback);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for put calls
     * @param endpoint Backend REST API url.
     * @param requestPayload payload/data if exists which is needed to be send.
     * @param responseCallback a function to be called with response retrieved.
     */
    publicXMLHTTPInvokers.put = function (endpoint, requestPayload, responseCallback) {
        return privateMethods.initiateXMLHTTPRequest(constants["HTTP_PUT"], requestPayload, endpoint, responseCallback);
    };

    /**
     * This method invokes return initiateXMLHttpRequest for delete calls
     * @param endpoint Backend REST API url.
     * @param responseCallback a function to be called with response retrieved.
     */
    publicXMLHTTPInvokers.delete = function (endpoint, responseCallback) {
        var requestPayload = null;
        return privateMethods.initiateXMLHTTPRequest(constants["HTTP_DELETE"], requestPayload, endpoint, responseCallback);
    };

    /**
     * ---------------------------------------------------------------------------
     * Start of WS-REQUEST based Interceptor implementations
     * ---------------------------------------------------------------------------
     */

    /**
     * This method add Oauth authentication header to outgoing WS Requests if Oauth authentication is enabled.
     * @param action
     * @param endpoint service end point to be triggered.
     * @param payload soap payload which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     * @param soapVersion soapVersion which need to used.
     */
    privateMethods.initiateWSRequest = function (action, endpoint, successCallback, errorCallback, soapVersion, payload) {
        var ws = require("ws");
        var wsRequest = new ws.WSRequest();
        var options = [];
        if (mdmProps["isOAuthEnabled"]) {
            var accessToken = privateMethods.getAccessToken();
            if (accessToken) {
                var authenticationHeaderName = String(constants["AUTHORIZATION_HEADER"]);
                var authenticationHeaderValue = String(constants["BEARER_PREFIX"] + accessToken);
                var headers = [];
                var oAuthAuthenticationData = {};
                oAuthAuthenticationData.name = authenticationHeaderName;
                oAuthAuthenticationData.value = authenticationHeaderValue;
                headers.push(oAuthAuthenticationData);
                options.HTTPHeaders = headers;
            } else {
                response.sendRedirect(mdmProps["httpsURL"] + "/emm/login");
            }
        }
        options.useSOAP = soapVersion;
        options.useWSA = constants["WEB_SERVICE_ADDRESSING_VERSION"];
        options.action = action;
        var wsResponse;
        try {
            wsRequest.open(options, endpoint, false);
            if (payload) {
                wsRequest.send(payload);
            } else {
                wsRequest.send();
            }
            wsResponse = wsRequest.responseE4X;
        } catch (e) {
            return errorCallback(e);
        }
        return successCallback(wsResponse);
    };

    /**
     * This method invokes return initiateWSRequest for soap calls
     * @param action describes particular soap action.
     * @param requestPayload SOAP request payload which is needed to be send.
     * @param endpoint service end point to be triggered.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     * @param soapVersion soapVersion which need to used.
     */
    publicWSInvokers.soapRequest = function (action, requestPayload, endpoint, successCallback, errorCallback, soapVersion) {
        return privateMethods.initiateWSRequest(action, endpoint, successCallback, errorCallback, soapVersion, requestPayload);
    };

    /**
     * ---------------------------------------------------------------------------
     * Start of HTTP-CLIENT-REQUEST based Interceptor implementations
     * ---------------------------------------------------------------------------
     */

    /**
     * This method add Oauth authentication header to outgoing HTTPClient Requests if Oauth authentication is enabled.
     * @param method HTTP request type.
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    privateMethods.initiateHTTPClientRequest = function (method, url, successCallback, errorCallback, payload) {
        var HttpClient = Packages.org.apache.commons.httpclient.HttpClient;
        var httpMethodObject;
        switch (method) {
            case constants["HTTP_GET"]:
                var GetMethod = Packages.org.apache.commons.httpclient.methods.GetMethod;
                httpMethodObject = new GetMethod(url);
                break;
            case constants["HTTP_POST"]:
                var PostMethod = Packages.org.apache.commons.httpclient.methods.PostMethod;
                httpMethodObject = new PostMethod(url);
                break;
            case constants["HTTP_PUT"]:
                var PutMethod = Packages.org.apache.commons.httpclient.methods.PutMethod;
                httpMethodObject = new PutMethod(url);
                break;
            case constants["HTTP_DELETE"]:
                var DeleteMethod = Packages.org.apache.commons.httpclient.methods.DeleteMethod;
                httpMethodObject = new DeleteMethod(url);
                break;
            default:
                throw new IllegalArgumentException("Invalid HTTP request method: " + method);
        }
        var Header = Packages.org.apache.commons.httpclient.Header;
        var header = new Header();
        header.setName(constants["CONTENT_TYPE_IDENTIFIER"]);
        header.setValue(constants["APPLICATION_JSON"]);
        httpMethodObject.addRequestHeader(header);
        header = new Header();
        header.setName(constants["ACCEPT_IDENTIFIER"]);
        header.setValue(constants["APPLICATION_JSON"]);
        httpMethodObject.addRequestHeader(header);

        if (mdmProps["isOAuthEnabled"]) {
            var accessToken = privateMethods.getAccessToken();
            if (accessToken) {
                header = new Header();
                header.setName(constants["AUTHORIZATION_HEADER"]);
                header.setValue(constants["BEARER_PREFIX"] + accessToken);
                httpMethodObject.addRequestHeader(header);
            } else {
                response.sendRedirect(mdmProps["httpsURL"] + "/emm/login");
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
    };

    /**
     * This method invokes return initiateHTTPClientRequest for get calls
     * @param url target url.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.get = function (url, successCallback, errorCallback) {
        var requestPayload = null;
        return privateMethods.
            initiateHTTPClientRequest(constants["HTTP_GET"], url, successCallback, errorCallback, requestPayload);
    };

    /**
     * This method invokes return initiateHTTPClientRequest for post calls
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.post = function (url, payload, successCallback, errorCallback) {
        return privateMethods.
            initiateHTTPClientRequest(constants["HTTP_POST"], url, successCallback, errorCallback, payload);
    };

    /**
     * This method invokes return initiateHTTPClientRequest for put calls
     * @param url target url.
     * @param payload payload/data which need to be send.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.put = function (url, payload, successCallback, errorCallback) {
        return privateMethods.
            initiateHTTPClientRequest(constants["HTTP_PUT"], url, successCallback, errorCallback, payload);
    };

    /**
     * This method invokes return initiateHTTPClientRequest for delete calls
     * @param url target url.
     * @param successCallback a function to be called if the respond if successful.
     * @param errorCallback a function to be called if en error is reserved.
     */
    publicHTTPClientInvokers.delete = function (url, successCallback, errorCallback) {
        var requestPayload = null;
        return privateMethods.
            initiateHTTPClientRequest(constants["HTTP_DELETE"], url, successCallback, errorCallback, requestPayload);
    };

    var publicMethods = {};
    publicMethods.XMLHttp = publicXMLHTTPInvokers;
    publicMethods.WS = publicWSInvokers;
    publicMethods.HttpClient = publicHTTPClientInvokers;

    return publicMethods;
}();
