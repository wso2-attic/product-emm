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

var invokerUtil = function () {

    var publicMethods = {};
    var privateMethods = {};

    privateMethods.execute = function (requestMethod, requestURL, requestPayload, successCallback, errorCallback) {
        var restAPIRequestDetails = {};
        restAPIRequestDetails["requestMethod"] = requestMethod;
        restAPIRequestDetails["requestURL"] = requestURL;
        restAPIRequestDetails["requestPayload"] = JSON.stringify(requestPayload);

        var request = {
            url: "/emm/api/invoker/execute/",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(restAPIRequestDetails),
            accept: "application/json",
            success: successCallback,
            error: function (jqXHR) {
                if (jqXHR.status == 401) {
                    console.log("Unauthorized access attempt!");
                    $(modalPopupContent).html($("#error-msg").html());
                    showPopup();
                } else {
                    errorCallback(jqXHR);
                }
            }
        };

        $.ajax(request);
    };

    publicMethods.get = function (requestURL, successCallback, errorCallback) {
        var requestPayload = null;
        privateMethods.execute("GET", requestURL, requestPayload, successCallback, errorCallback);
    };

    publicMethods.post = function (requestURL, requestPayload, successCallback, errorCallback) {
        privateMethods.execute("POST", requestURL, requestPayload, successCallback, errorCallback);
    };

    publicMethods.put = function (requestURL, requestPayload, successCallback, errorCallback) {
        privateMethods.execute("PUT", requestURL, requestPayload, successCallback, errorCallback);
    };

    publicMethods.delete = function (requestURL, successCallback, errorCallback) {
        var requestPayload = null;
        privateMethods.execute("DELETE", requestURL, requestPayload, successCallback, errorCallback);
    };

    return publicMethods;
}();
