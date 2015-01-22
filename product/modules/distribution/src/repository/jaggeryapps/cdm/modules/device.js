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

var utility = require("/modules/utility.js");
var DeviceIdentifier = Packages.org.wso2.carbon.device.mgt.common.DeviceIdentifier;
var log = new Log();

var deviceManagementService = utility.getDeviceManagementService();

var listDevices = function () {
    var devices = deviceManagementService.getAllDevices("android");
    var deviceList = [];

    for (i = 0; i < devices.size(); i++) {
        var device = devices.get(i);
        deviceList.push({
            "identifier": device.getDeviceIdentifier(),
            "name": device.getName(),
            "ownership": device.getOwnership(),
            "owner": device.getOwner(),
            "deviceType": device.getType(),
            "vendor": device.getProperties().get("vendor"),
            "model": device.getProperties().get("model"),
            "osVersion": device.getProperties().get("osVersion")
        });
    }
    return deviceList;
}
var getDevice = function(type, deviceId){
    var deviceIdentifier =  new DeviceIdentifier();
    deviceIdentifier.setType(type);
    deviceIdentifier.setId(deviceId);
    var device = deviceManagementService.getDevice(deviceIdentifier);
    return device;
}

var viewDevice = function(type, deviceId){
    var device = this.getDevice(type, deviceId);

    var entries = device.getProperties().entrySet();
    var iterator = entries.iterator();
    var properties = {};
    while(iterator.hasNext()){
        var entry = iterator.next();
        var key = entry.getKey();
        var value = entry.getValue();
        properties[key]= value;
    }
    return {
        "identifier": device.getDeviceIdentifier(),
        "name": device.getName(),
        "ownership": device.getOwnership(),
        "owner": device.getOwner(),
        "deviceType": device.getType(),
        "vendor": device.getProperties().get("vendor"),
        "model": device.getProperties().get("model"),
        "osVersion": device.getProperties().get("osVersion"),
        "properties": properties
    };
}