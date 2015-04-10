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
var deviceModule = (function () {
    var log = new Log();
    var module = {};
    var utility = require('/modules/utility.js').utility;
    var constants = require('/modules/constants.js');

    var DeviceIdentifier = Packages.org.wso2.carbon.device.mgt.common.DeviceIdentifier;
    var DeviceManagerUtil = Packages.org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;

    var CommandOperation = Packages.org.wso2.carbon.device.mgt.common.operation.mgt.CommandOperation;
    var ConfigOperation = Packages.org.wso2.carbon.device.mgt.common.operation.mgt.ConfigOperation;
    var SimpleOperation = Packages.org.wso2.carbon.device.mgt.common.operation.mgt.SimpleOperation;
    var Type =  Packages.org.wso2.carbon.device.mgt.common.operation.mgt.Operation.Type;
    var Properties = Packages.java.util.Properties;
    var ArrayList = Packages.java.util.ArrayList;
    var deviceManagementService = utility.getDeviceManagementService();

    var defaultVal = function(prop){
        return (prop == undefined || prop == null) ? constants.UNSPECIFIED : prop;
    };

    module.listDevices = function () {
        var devices = deviceManagementService.getAllDevices();
        var deviceList = [];
        for (var i = 0; i < devices.size(); i++) {
            var device = devices.get(i);
            var propertiesList = DeviceManagerUtil.convertDevicePropertiesToMap(device.getProperties());
            var deviceObject = {};
            deviceObject[constants.DEVICE_IDENTIFIER] = defaultVal(device.getDeviceIdentifier());
            deviceObject[constants.DEVICE_NAME] = defaultVal(device.getName());
            deviceObject[constants.DEVICE_OWNERSHIP] =  defaultVal(device.getOwnership());
            deviceObject[constants.DEVICE_OWNER] = defaultVal(device.getOwner());
            deviceObject[constants.DEVICE_TYPE] = defaultVal(device.getType());
            deviceObject[constants.DEVICE_PROPERTIES] = {};
            deviceObject[constants.DEVICE_PROPERTIES][constants.DEVICE_VENDOR] = defaultVal(propertiesList.get(constants.DEVICE_VENDOR));
            deviceObject[constants.DEVICE_PROPERTIES][constants.DEVICE_MODEL] = defaultVal(propertiesList.get(constants.DEVICE_MODEL));
            deviceObject[constants.DEVICE_PROPERTIES][constants.DEVICE_OS_VERSION] = defaultVal(propertiesList.get(constants.DEVICE_OS_VERSION));
            deviceList.push(deviceObject);
        }
        return deviceList;
    };
    module.listDevicesForUser = function(username){
        var devices = deviceManagementService.getDeviceListOfUser(username);
        var deviceList = [];
        for (var i = 0; i < devices.size(); i++) {
            var device = devices.get(i);
            var propertiesList = DeviceManagerUtil.convertDevicePropertiesToMap(device.getProperties());
            var deviceObject = {};
            log.info(device.getName());
            deviceObject[constants.DEVICE_IDENTIFIER] = defaultVal(device.getDeviceIdentifier());
            deviceObject[constants.DEVICE_NAME] = defaultVal(device.getName());
            deviceObject[constants.DEVICE_OWNERSHIP] =  defaultVal(device.getOwnership());
            deviceObject[constants.DEVICE_OWNER] = defaultVal(device.getOwner());
            deviceObject[constants.DEVICE_TYPE] = defaultVal(device.getType());
            deviceObject[constants.DEVICE_PROPERTIES] = {};
            deviceObject[constants.DEVICE_PROPERTIES][constants.DEVICE_VENDOR] = defaultVal(propertiesList.get(constants.DEVICE_VENDOR));
            deviceObject[constants.DEVICE_PROPERTIES][constants.DEVICE_MODEL] = defaultVal(propertiesList.get(constants.DEVICE_MODEL));
            deviceObject[constants.DEVICE_PROPERTIES][constants.DEVICE_OS_VERSION] = defaultVal(propertiesList.get(constants.DEVICE_OS_VERSION));
            deviceList.push(deviceObject);
        }
        return deviceList;
    }

    var getDevice = function(type, deviceId){
        var deviceIdentifier =  new DeviceIdentifier();
        deviceIdentifier.setType(type);
        deviceIdentifier.setId(deviceId);
        return deviceManagementService.getDevice(deviceIdentifier);
    };

    /*
     Get the supported features by the device type
     */
    module.getFeatures = function(){
        var features = deviceManagementService.getFeatureManager(constants.PLATFORM_ANDROID).getFeatures();
        var featuresConverted = {};
        if(features){
            for (var i = 0; i < features.size(); i++) {
                var feature = features.get(i);
                var featureObject = {};
                featureObject[constants.FEATURE_NAME] = feature.getName();
                featureObject[constants.FEATURE_DESCRIPTION] = feature.getDescription();
                featuresConverted[feature.getName()] = featureObject;
            }
        }

        return featuresConverted;
    };

    module.performOperation = function(devices, operation){
        var operationInstance;
        if (operation.type=="COMMAND") {
            operationInstance = new CommandOperation();
        }else if (operation.type=="CONFIG") {
            operationInstance = new ConfigOperation();
        }else {
            operationInstance = new SimpleOperation();
        }
        operationInstance.setCode(operation.featureName);
        var props = new Properties();
        for (var i = 0; i < operation.properties.length; i++) {
            var object = properties[i];
            props.setProperty(object.key,object.value);
        }
        operationInstance.setProperties(props);
        var deviceList = new ArrayList();
        for(var i in devices){
            var device = devices[i];
            var deviceIdentifier = new DeviceIdentifier();
            deviceIdentifier.setId(device.id)
            deviceIdentifier.setType(device.type);
            deviceList.add(deviceIdentifier);
        }
        deviceManagementService.addOperation(operationInstance, deviceList);
    };
    module.viewDevice = function(type, deviceId){
        var device = getDevice(type, deviceId);
        if (device){
            var propertiesList = DeviceManagerUtil.convertDevicePropertiesToMap(device.getProperties());
            var entries = propertiesList.entrySet();
            var iterator = entries.iterator();
            var properties = {};
            while(iterator.hasNext()){
                var entry = iterator.next();
                var key = entry.getKey();
                var value = entry.getValue();
                properties[key]= defaultVal(value);
            }
            var deviceObject = {};
            deviceObject[constants.DEVICE_IDENTIFIER] = device.getDeviceIdentifier();
            deviceObject[constants.DEVICE_NAME] = defaultVal(device.getName());
            deviceObject[constants.DEVICE_OWNERSHIP] =  defaultVal(device.getOwnership());
            deviceObject[constants.DEVICE_OWNER] = device.getOwner();
            deviceObject[constants.DEVICE_TYPE] = device.getType();
            deviceObject[constants.DEVICE_PROPERTIES] = properties;
            return deviceObject;
        }
    };
    return module;
}());