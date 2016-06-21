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

    privateMethods.execute = function (method, url, payload, successCallback, errorCallback) {
        var requestPayload = {};
        requestPayload.actionMethod = method;
        requestPayload.actionUrl = url;
        requestPayload.actionPayload = JSON.stringify(payload);

        var request = {
            url: "/emm/api/invoker/execute/",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(requestPayload),
            accept: "application/json",
            success: successCallback,
            error: function (jqXHR) {
                if (jqXHR.status == "401") {
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

    publicMethods.get = function (url, successCallback, errorCallback) {
        var payload = null;
        privateMethods.execute("GET", url, payload, successCallback, errorCallback);
    };

    publicMethods.post = function (url, payload, successCallback, errorCallback) {
        privateMethods.execute("POST", url, payload, successCallback, errorCallback);
    };

    publicMethods.put = function (url, payload, successCallback, errorCallback) {
        privateMethods.execute("PUT", url, payload, successCallback, errorCallback);
    };

    publicMethods.delete = function (url, successCallback, errorCallback) {
        var payload = null;
        privateMethods.execute("DELETE", url, payload, successCallback, errorCallback);
    };

    return publicMethods;
}();
