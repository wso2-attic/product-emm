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

function onRequest(context) {
    // var log = new Log("device-detail.js");
    var deviceType = request.getParameter("type");
    var deviceId = request.getParameter("id");

    if (deviceType && deviceId) {
        var deviceModule = require("/modules/device.js")["deviceModule"];
        var response = deviceModule.viewDevice(deviceType, deviceId);

        if (response["status"] == "success") {
            var device = response["content"];
            var viewModel = {};
            var deviceInfo = device["properties"]["DEVICE_INFO"];
            if (deviceInfo && String(deviceInfo.toString()).length > 0) {
                deviceInfo = parse(stringify(deviceInfo));
                if (device["type"] == "ios") {
                    deviceInfo = parse(deviceInfo);
                    viewModel["imei"] = device["properties"]["IMEI"];
                    viewModel["phoneNumber"] = deviceInfo["PhoneNumber"];
                    viewModel["udid"] = deviceInfo["UDID"];
                    viewModel["BatteryLevel"] = Math.round(deviceInfo["BatteryLevel"] * 100);
                    viewModel["DeviceCapacity"] = Math.round(deviceInfo["DeviceCapacity"] * 100) / 100;
                    viewModel["AvailableDeviceCapacity"] = Math.
                        round(deviceInfo["AvailableDeviceCapacity"] * 100) / 100;
                    viewModel["DeviceCapacityUsed"] = Math.
                        round((viewModel["DeviceCapacity"] - viewModel["AvailableDeviceCapacity"]) * 100) / 100;
                    viewModel["DeviceCapacityPercentage"] = Math.
                        round(viewModel["AvailableDeviceCapacity"] / viewModel["DeviceCapacity"] * 10000) / 100;
                    viewModel["location"] = {
                        latitude: device["properties"]["LATITUDE"],
                        longitude: device["properties"]["LONGITUDE"]
                    };
                } else if (device["type"] == "android") {
                    viewModel["imei"] = device["properties"]["IMEI"];
                    viewModel["model"] = device["properties"]["DEVICE_MODEL"];
                    viewModel["vendor"] = device["properties"]["VENDOR"];
                    var osBuildDate = device["properties"]["OS_BUILD_DATE"];
                    if (osBuildDate != null && osBuildDate != "0") {
                        var formattedDate = new Date(osBuildDate * 1000);
                        viewModel["os_build_date"] = formattedDate;
                    }
                    viewModel["internal_memory"] = {};
                    viewModel["external_memory"] = {};
                    viewModel["location"] = {
                        latitude: device["properties"]["LATITUDE"],
                        longitude: device["properties"]["LONGITUDE"]
                    };
                    var info = {};
                    var infoList = parse(deviceInfo);
                    if (infoList != null && infoList != undefined) {
                        for (var j = 0; j < infoList.length; j++) {
                            info[infoList[j].name] = infoList[j].value;
                        }
                    }
                    deviceInfo = info;
                    viewModel["BatteryLevel"] = deviceInfo["BATTERY_LEVEL"];
                    viewModel["internal_memory"]["FreeCapacity"] = Math.
                        round(deviceInfo["INTERNAL_AVAILABLE_MEMORY"] * 100)/100;
                    viewModel["internal_memory"]["DeviceCapacityPercentage"] = Math.
                        round(deviceInfo["INTERNAL_AVAILABLE_MEMORY"]
                            / deviceInfo["INTERNAL_TOTAL_MEMORY"] * 10000) / 100;
                    viewModel["external_memory"]["FreeCapacity"] = Math.
                        round(deviceInfo["EXTERNAL_AVAILABLE_MEMORY"] * 100) / 100;
                    viewModel["external_memory"]["DeviceCapacityPercentage"] = Math.
                        round(deviceInfo["EXTERNAL_AVAILABLE_MEMORY"]
                            / deviceInfo["EXTERNAL_TOTAL_MEMORY"] * 10000) / 100;
                } else if (device["type"] == "windows") {
                    viewModel["imei"] = device["properties"]["IMEI"];
                    viewModel["model"] = device["properties"]["DEVICE_MODEL"];
                    viewModel["vendor"] = device["properties"]["VENDOR"];
                    viewModel["internal_memory"] = {};
                    viewModel["external_memory"] = {};
                    viewModel["location"] = {
                        latitude: device["properties"]["LATITUDE"],
                        longitude: device["properties"]["LONGITUDE"]
                    };
                }
                device["viewModel"] = viewModel;
            }
            context["device"] = device;
        }
        return context;
    }
}