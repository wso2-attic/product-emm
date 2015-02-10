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

var utility = require('/modules/utility.js');
var constants = require('/modules/constants.js');
var DeviceIdentifier = Packages.org.wso2.carbon.device.mgt.common.DeviceIdentifier;
var DeviceManagerUtil = Packages.org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
var Operation = Packages.org.wso2.carbon.device.mgt.common.Operation;
var Type =  Packages.org.wso2.carbon.device.mgt.common.Operation.Type;
var Properties = Packages.java.util.Properties;
var ArrayList = Packages.java.util.ArrayList;
var log = new Log();
var deviceManagementService = utility.getDeviceManagementService();

var unspecifiedFilter = function(prop){
    if(prop==null){
        return constants.UNSPECIFIED;
    }else{
        return prop;
    }
}

var listDevices = function () {
    var devices = deviceManagementService.getAllDevices(constants.PLATFORM_ANDROID);
    var deviceList = [];
    for (i = 0; i < devices.size(); i++) {
        var device = devices.get(i);
        var propertiesList = DeviceManagerUtil.convertPropertiesToMap(device.getProperties());
        var deviceObject = {};
        deviceObject[constants.DEVICE_IDENTIFIER] = unspecifiedFilter(device.getDeviceIdentifier());
        deviceObject[constants.DEVICE_NAME] = unspecifiedFilter(device.getName());
        deviceObject[constants.DEVICE_OWNERSHIP] =  unspecifiedFilter(device.getOwnership());
        deviceObject[constants.DEVICE_OWNER] = unspecifiedFilter(device.getOwner());
        deviceObject[constants.DEVICE_TYPE] = unspecifiedFilter(device.getType());
        deviceObject[constants.DEVICE_VENDOR] = unspecifiedFilter(propertiesList.get(constants.DEVICE_VENDOR));
        deviceObject[constants.DEVICE_MODEL] = unspecifiedFilter(propertiesList.get(constants.DEVICE_MODEL));
        deviceObject[constants.DEVICE_OS_VERSION] = unspecifiedFilter(propertiesList.get(constants.DEVICE_OS_VERSION));
        deviceList.push(deviceObject);
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

var getOperations = function(type){
    var features = deviceManagementService.getOperationManager(constants.PLATFORM_ANDROID).getFeaturesForDeviceType(type);
    var featuresConverted = [];
    for (i = 0; i < features.size(); i++) {
        var feature = features.get(i);
        var featureObject = {};
        featureObject[constants.FEATURE_NAME] = feature.getName();
        featureObject[constants.FEATURE_DESCRIPTION] = feature.getDescription()
        featuresConverted.push(featureObject);
    }
    return featuresConverted;
}
var performOperation = function(deviceId, featureName, properties, type){
    var operation = new Operation();
    operation.setCode(featureName);
    operation.setType(Type.COMMAND);
    var props = new Properties();
    for (i = 0; i < properties.length; i++) {
        var object = properties[i];
        props.setProperty(object.key,object.value);
    }
    operation.setProperties(props);
    var deviceIdentifier = new DeviceIdentifier();
    deviceIdentifier.setId(deviceId)
    deviceIdentifier.setType(type);
    var deviceList = new ArrayList();
    deviceList.add(deviceIdentifier);
    deviceManagementService.getOperationManager(constants.PLATFORM_ANDROID).addOperation(operation, deviceList);
}

var viewDevice = function(type, deviceId){
    var device = this.getDevice(type, deviceId);
    var propertiesList = DeviceManagerUtil.convertPropertiesToMap(device.getProperties());
    var entries = propertiesList.entrySet();
    var iterator = entries.iterator();
    var properties = {};
    while(iterator.hasNext()){
        var entry = iterator.next();
        var key = entry.getKey();
        var value = entry.getValue();
        properties[key]= unspecifiedFilter(value);
    }
    var deviceObject = {};
    deviceObject[constants.DEVICE_IDENTIFIER] = unspecifiedFilter(device.getDeviceIdentifier());
    deviceObject[constants.DEVICE_NAME] = unspecifiedFilter(device.getName());
    deviceObject[constants.DEVICE_OWNERSHIP] =  unspecifiedFilter(device.getOwnership());
    deviceObject[constants.DEVICE_OWNER] = unspecifiedFilter(device.getOwner());
    deviceObject[constants.DEVICE_TYPE] = unspecifiedFilter(device.getType());
    deviceObject[constants.DEVICE_VENDOR] = unspecifiedFilter(propertiesList.get(constants.DEVICE_VENDOR));
    deviceObject[constants.DEVICE_MODEL] = unspecifiedFilter(propertiesList.get(constants.DEVICE_MODEL));
    deviceObject[constants.DEVICE_OS_VERSION] = unspecifiedFilter(propertiesList.get(constants.DEVICE_OS_VERSION));
    deviceObject[constants.DEVICE_PROPERTIES] = properties
    return deviceObject;
}