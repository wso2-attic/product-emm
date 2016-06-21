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
 * This invokerRequestWrapper contains the wrappers for invoker util requests.
 */
var invokerRequestWrapper = function () {

    var constants = require("/modules/constants.js");
    var serviceInvokers = require("/modules/backend-service-invoker.js").backendServiceInvoker;

    var publicWrappers = [];

    publicWrappers.initiate = function (method, url, payload) {
        switch (method) {
            case constants.HTTP_GET:
                var response = serviceInvokers.XMLHttp.get(url, function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload["responseContent"];
                                                               response.status = "success";
                                                               return response;
                                                           },
                                                           function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload;
                                                               response.status = "error";
                                                               return response;
                                                           });
                return response;
                break;
            case constants.HTTP_POST:
                var response = serviceInvokers.XMLHttp.post(url, payload, function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload["responseContent"];
                                                               response.status = "success";
                                                               return response;
                                                           },
                                                           function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload;
                                                               response.status = "error";
                                                               return response;
                                                           });
                return response;
                break;
            case constants.HTTP_PUT:
                var response = serviceInvokers.XMLHttp.put(url, payload, function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload["responseContent"];
                                                               response.status = "success";
                                                               return response;
                                                           },
                                                           function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload;
                                                               response.status = "error";
                                                               return response;
                                                           });
                return response;
                break;
            case constants.HTTP_DELETE:
                var response = serviceInvokers.XMLHttp.delete(url, function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload["responseContent"];
                                                               response.status = "success";
                                                               return response;
                                                           },
                                                           function (responsePayload) {
                                                               var response = {};
                                                               response.content = responsePayload;
                                                               response.status = "error";
                                                               return response;
                                                           });
                return response;
                break;
        }
    }

}();
