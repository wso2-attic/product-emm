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

var operationModule = function () {
    var publicMethods = {};
    var privateMethods = {};

    publicMethods.getIOSServiceEndpoint = function (operationName) {
        var featureMap = {
            "DEVICE_LOCK": "lock",
            "ALARM": "alarm",
            "LOCATION": "location",
            "AIR_PLAY": "airplay",
            "RESTRICTION": "restriction",
            "CELLULAR": "cellular",
            "WIFI": "wifi",
            "INSTALL_STORE_APPLICATION": "storeapplication",
            "INSTALL_ENTERPRISE_APPLICATION": "enterpriseapplication",
            "REMOVE_APPLICATION": "removeapplication",
            "ENTERPRISE_WIPE": "enterprisewipe"
        };
        return "/ios/operation/" + featureMap[operationName];
    };

    privateMethods.generateIOSOperationPayload = function (operationName, operationData, deviceList) {
        var payload;
        var operationType;
        if (operationName == "PASSCODE_POLICY") {
            operationType = "profile";
            payload = {
                "operation": {
                    "forcePIN": operationData["forcePIN"],
                    "allowSimple": operationData["allowSimple"],
                    "requireAlphanumeric": operationData["requireAlphanumeric"],
                    "minLength": operationData["minLength"],
                    "minComplexChars": operationData["minComplexChars"],
                    "maxPINAgeInDays": operationData["maxPINAgeInDays"],
                    "pinHistory": operationData["pinHistory"],
                    "maxInactivity": operationData["maxAutoLock"],
                    "maxGracePeriod": operationData["gracePeriod"],
                    "maxFailedAttempts": operationData["maxFailedAttempts"]
                }
            };
        } else {
            // If the operation is neither of above, it is a command operation
            operationType = "command";
            // Operation payload of a command operation is simply an array of device IDs
            payload = deviceList;
        }
        if (operationType == "profile" && deviceList) {
            payload["deviceIDs"] = deviceList;
        }
        return payload;
    };

    privateMethods.generateAndroidOperationPayload = function (operationName, operationData, deviceList) {
        var payload;
        var operationType;
        if (operationName == "CAMERA") {
            operationType = "profile";
            payload = {
                "operation": {
                    "enabled" : operationData["enableCamera"]
                }
            };
        } else if (operationName == "CHANGE_LOCK_CODE") {
            operationType = "profile";
            payload = {
                "operation": {
                    "lockCode" : operationData["lockCode"]
                }
            };
        } else if (operationName == "ENCRYPT_STORAGE") {
            operationType = "profile";
            payload = {
                "operation": {
                    "encrypted" : operationData["enableEncryption"]
                }
            };
        } else if (operationName == "NOTIFICATION") {
            operationType = "profile";
            payload = {
                "operation": {
                    "message" : operationData["message"]
                }
            };
        } else if (operationName == "WEBCLIP") {
            operationType = "profile";
            payload = {
                "operation": {
                    "identity": operationData["url"],
                    "title": operationData["title"]
                }
            };
        } else if (operationName == "INSTALL_APPLICATION") {
            operationType = "profile";
            payload = {
                "operation": {
                    "appIdentifier": operationData["packageName"],
                    "type": operationData["type"],
                    "url": operationData["url"]
                }
            };
        } else if (operationName == "UNINSTALL_APPLICATION") {
            operationType = "profile";
            payload = {
                "operation": {
                    "appIdentifier": operationData["packageName"]
                }
            };
        } else if (operationName == "BLACKLIST_APPLICATIONS") {
            operationType = "profile";
            payload = {
                "operation": {
                    "appIdentifier": operationData["packageNames"]
                }
            };
        } else if (operationName == "PASSCODE_POLICY") {
            operationType = "profile";
            payload = {
                "operation": {
                    "allowSimple": operationData["allowSimple"],
                    "requireAlphanumeric": operationData["requireAlphanumeric"],
                    "minLength": operationData["minLength"],
                    "minComplexChars": operationData["minComplexChars"],
                    "maxPINAgeInDays": operationData["maxPINAgeInDays"],
                    "pinHistory": operationData["pinHistory"],
                    "maxFailedAttempts": operationData["maxFailedAttempts"]
                }
            };
        } else if (operationName == "WIFI") {
            operationType = "profile";
            payload = {
                "operation": {
                    "ssid": operationData["ssid"],
                    "password": operationData["password"]
                }
            };
        } else {
            // If the operation is neither of above, it is a command operation
            operationType = "command";
            // Operation payload of a command operation is simply an array of device IDs
            payload = deviceList;
        }
        if (operationType == "profile" && deviceList) {
            payload["deviceIDs"] = deviceList;
        }
        return payload;
    };

    publicMethods.getAndroidServiceEndpoint = function (operationName) {
        var featureMap = {
            "WIFI": "wifi",
            "CAMERA": "camera",
            "DEVICE_LOCK": "lock",
            "DEVICE_LOCATION": "location",
            "CLEAR_PASSWORD": "clear-password",
            "APPLICATION_LIST": "get-application-list",
            "DEVICE_RING": "ring-device",
            "DEVICE_MUTE": "mute",
            "NOTIFICATION": "notification",
            "ENCRYPT_STORAGE": "encrypt",
            "CHANGE_LOCK_CODE": "change-lock-code",
            "WEBCLIP": "webclip",
            "INSTALL_APPLICATION": "install-application",
            "UNINSTALL_APPLICATION": "uninstall-application",
            "BLACKLIST_APPLICATIONS": "blacklist-applications",
            "PASSCODE_POLICY": "password-policy",
            "ENTERPRISE_WIPE": "enterprise-wipe",
            "WIPE_DATA": "wipe-data"
        };
        return "/mdm-android-agent/operation/" + featureMap[operationName];
    };

    /**
     * Get the icon for the featureCode
     * @param featureCode
     * @returns icon class
     */
    publicMethods.getAndroidIconForFeature = function (featureCode) {
        var featureMap = {
            "DEVICE_LOCK": "fw-lock",
            "DEVICE_LOCATION": "fw-map-location",
            "CLEAR_PASSWORD": "fw-key",
            "ENTERPRISE_WIPE": "fw-clean",
            "WIPE_DATA": "fw-database",
            "DEVICE_RING": "fw-dial-up",
            "DEVICE_MUTE": "fw-incoming-call",
            "NOTIFICATION": "fw-message",
            "CHANGE_LOCK_CODE": "fw-padlock"
        };
        return featureMap[featureCode];
    };

    /**
     * Get the icon for the featureCode
     * @param featureCode
     * @returns icon class
     */
    publicMethods.getIOSIconForFeature = function (featureCode) {
        var featureMap = {
            "DEVICE_LOCK": "fw-lock",
            "LOCATION": "fw-map-location",
            "ENTERPRISE_WIPE": "fw-clean",
            "ALARM": "fw-dial-up"
        };
        return featureMap[featureCode];
    };

    privateMethods.createTemperatureControllerPayload = function (operationName, operationData, devices) {
        var payload;
        var operationType;
        if (operationName == "BUZZER") {
            operationType = "profile";
            payload = {
                "operation": {
                    "enabled" : operationData["enableBuzzer"]
                }
            };
        } else {
            operationType = "command";
            payload = devices;
        }
        if (operationType == "profile" && devices) {
            payload["deviceIDs"] = devices;
        }
        return payload;
    };

    publicMethods.getTemperatureControllerServiceEndpoint = function (operationName) {
        var featureMap = {
            "BUZZER": "buzzer"
        };
        return "/temp-controller-agent/operations/" + featureMap[operationName];
    };

    publicMethods.getTemperatureControllerIconForFeature = function (featureCode) {
        var featureMap = {
            "BUZZER": "fw-dial-up"
        };
        return featureMap[featureCode];
    };

    /**
     * Filter a list by a data attribute
     * @param prop
     * @param val
     * @returns {Array}
     */
    $.fn.filterByData = function (prop, val) {
        return this.filter(
            function () { return $(this).data(prop) == val; }
        );
    };

    /*
     @DeviceType = Device Type of the profile
     @operationCode = Feature Codes to generate the profile from
     @DeviceList = Optional device list to include in payload body for operations
     */
    publicMethods.generatePayload = function (deviceType, operationCode, deviceList) {
        var payload;
        var operationData = {};
        $(".operation-data").filterByData("operation-code", operationCode).find(".operationDataKeys").each(
            function () {
                var operationDataObj = $(this);
                var key = operationDataObj.data("key");
                var value = operationDataObj.val();
                if (operationDataObj.is(':checkbox')) {
                    value = operationDataObj.is(":checked");
                } else if (operationDataObj.is('select')) {
                    value = operationDataObj.find("option:selected").attr("value");
                }
                operationData[key] = value;
            }
        );
        if (deviceType == "ios") {
            payload = privateMethods.generateIOSOperationPayload(operationCode, operationData, deviceList);
        }
        if (deviceType == "android") {
            payload = privateMethods.generateAndroidOperationPayload(operationCode, operationData, deviceList);
        }
        if (deviceType == "TemperatureController") {
            payload = privateMethods.createTemperatureControllerPayload(operationCode, operationData, deviceList);
        }
        return payload;
    };

    /*
     @DeviceType = Device Type of the profile
     @FeatureCodes = Feature Codes to generate the profile from
     */
    publicMethods.generateProfile = function (deviceType, featureCodes) {
        var generatedProfile = {};
        for (var i = 0; i < featureCodes.length; ++i) {
            var featureCode = featureCodes[i];
            var payload = publicMethods.generatePayload(deviceType, featureCode);
            generatedProfile[featureCode] = payload["operation"];
        }
        return generatedProfile;
    };

    return publicMethods;

}();