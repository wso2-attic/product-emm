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

    var module = {};

    var END_POINT = window.location.origin + "/emm/api/invoker/execute/";

    module.get = function (url, successCallback, errorCallback) {
        var payload = null;
        execute("GET", url, payload, successCallback, errorCallback);
    };

    module.post = function (url, payload, successCallback, errorCallback) {
        execute("POST", url, payload, successCallback, errorCallback);
    };

    module.put = function (url, payload, successCallback, errorCallback) {
        execute("PUT", url, payload, successCallback, errorCallback);
    };

    module.delete = function (url, successCallback, errorCallback) {
        var payload = null;
        execute("DELETE", url, payload, successCallback, errorCallback);
    };

    var execute = function (method, url, payload, successCallback, errorCallback) {
        var request = {
            url: END_POINT,
            type: "POST",
            contentType: "application/json",
            accept: "application/json",
            success: successCallback
        };

        var data = {};
        data.actionMethod = method;
        data.actionUrl = url;
        data.actionPayload = JSON.stringify(payload);

        request.data = JSON.stringify(data);

        $.ajax(request).fail(function (jqXHR) {
            if (jqXHR.status == "401") {
                console.log("Unauthorized access attempt!");
                $(modalPopupContent).html($('#error-msg').html());
                showPopup();
            } else {
                errorCallback(jqXHR);
            }
        });
    };

    return module;

}();
