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

$(document).ready(function () {
    var validateAndReturn = function (value) {
        return (value == undefined || value == null) ? "Unspecified" : value;
    };
    Handlebars.registerHelper("deviceMap", function (device) {
        device.owner = validateAndReturn(device.owner);
        device.ownership = validateAndReturn(device.ownership);
        var arr = device.properties;
        if (arr){
            device.properties = arr.reduce(function (total, current) {
                total[current.name] = validateAndReturn(current.value);
                return total;
            }, {});
        }
    });
    loadDevicesList();
});

function loadDevicesList() {
    var devicesList = $("#user-devices-view");
    var deviceListingSrc = devicesList.attr("src");
    var username = devicesList.data("username");
    var domain = devicesList.data("domain");

    $.template("user-devices-view", deviceListingSrc, function (template) {
        var serviceURL = "/mdm-admin/users/"+domain+"/"+username+"/devices";

        var successCallback = function (data) {
            var viewModel = {};
            data = JSON.parse(data);
            viewModel.devices = data;
            if(data.length > 0){
                var content = template(viewModel);
                $("#enrolled_devices-container").html(content);
            }

        };
        invokerUtil.get(serviceURL,
            successCallback, function(message){
                console.log(message);
        });
    });
}

