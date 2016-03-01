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

var deviceModule;
deviceModule = function () {
    var log = new Log("modules/device.js");

    var utility = require('/modules/utility.js').utility;
    var constants = require('/modules/constants.js');
    var mdmProps = require('/config/mdm-props.js').config();
    var serviceInvokers = require("/modules/backend-service-invoker.js").backendServiceInvoker;
    var publicMethods = {};
    var privateMethods = {};

    privateMethods.validateAndReturn = function (value) {
        return (value == undefined || value == null) ? constants.UNSPECIFIED : value;
    };
    /*
     @Updated
     */
    publicMethods.getLicense = function (deviceType) {
        var url;
        var license;
        if (deviceType == "windows") {
            url = mdmProps["httpURL"] + "/mdm-windows-agent/services/device/license";
        } else if (deviceType == "ios") {
            url = mdmProps["httpsURL"] + "/ios-enrollment/license/";
        }

        if (url != null && url != undefined) {
            serviceInvokers.XMLHttp.get(url, function (responsePayload) {
                license = responsePayload.text;
            });
        }
        return license;
    };

    return publicMethods;
}();
