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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Setting-up global variables.
 */

var operations = '.wr-operations',
    modelPopup = '.wr-modalpopup',
    modelPopupContent = modelPopup + ' .modalpopup-content',
    deviceSelection = '.device-select',
    deviceCheckbox = '#ast-container .ctrl-wr-asset .itm-select input[type="checkbox"]',
    showOperationsBtn = '#showOperationsBtn',
    maxOperationsLimit = 15;


/*
 * DOM ready functions.
 */
$(document).ready(function(){
    /* collapse operations to a toggle menu, if exceeds max operations limit */
    if($(operations + "> a").length > maxOperationsLimit){
        $(showOperationsBtn).show();
    }
    else{
        $(operations).show();
    }
});

/*
 * On Show Operations click operation show toggling function.
 */
function showOperations(){
    $(operations).toggle('slide');
}

/*
 * On operation click function.
 * @param selection: Selected operation
 */
function operationSelect(selection){
    $(modelPopupContent).html($(operations + ' .operation[data-operation='+selection+']').html());
    showPopup();
}

/*
 * show popup function.
 */
function showPopup() {
    $(modelPopup).show();
}

/*
 * hide popup function.
 */
function hidePopup() {
    $(modelPopupContent).html('');
    $(modelPopup).hide();
}

/*
 * Function to get selected devices ID's
 */
function getSelectedDeviceIds(){
    var deviceIdentifierList = [];
    $(deviceSelection).each(function(index){
        var device = $(this);
        var deviceId = device.data('deviceid');
        var deviceType = device.data('type');
        deviceIdentifierList.push({
            "id" : deviceId,
            "type" : deviceType
        });
    });
    return deviceIdentifierList;
}
function getDevicesByTypes(deviceList){
    var deviceTypes = {};
    jQuery.each(deviceList, function(index, item) {
        if(!deviceTypes[item.type]){
            deviceTypes[item.type] = [];
        }
        if(item.type == "ios"){
            //for iOS we are sending only the IDS cause we are sending it to the JAX-RS
            deviceTypes[item.type].push(item.id);
        }else{
            deviceTypes[item.type].push(item);
        }

    });
    return deviceTypes;
}
function getiOSServiceEndpoint (operationName) {
    var featureMap = {
        DEVICE_LOCK: "lock",
        ALARM: "alarm",
        LOCATION: "location",
        AIR_PLAY: "airplay",
        INSTALL_STORE_APPLICATION: "storeapplication",
        INSTALL_ENTERPRISE_APPLICATION: "enterpriseapplication",
        REMOVE_APPLICATION: "removeapplication",
        RESTRICTION: "restriction",
        CELLULAR: "cellular"
    };
    return "https://localhost:9443/ios/operation/" + featureMap[operationName];
}
function createiOSPayload(operationName, operationData, devices) {
    // Command operations doesn't need a payload
    var payload;
    if (operationName == "AIR_PLAY") {
        payload = {
            "deviceIDs": devices,
            "operation": {
                "airPlayDestinations": [
                    operationData.location
                ],
                "airPlayCredentials": [{
                    "deviceName": operationData.deviceName,
                    "password": operationData.password
                }]
            }
        };
    }else if (operationName == "INSTALL_ENTERPRISE_APPLICATION") {
        payload = {
            "deviceIDs": devices,
            "operation": {
                "identifier": operationData.appIdentifier,
                "iTunesStoreID": operationData.ituneID,
                "removeAppUponMDMProfileRemoval": operationData.appRemoval,
                "preventBackupOfAppData": operationData.backupData,
                "bundleId": operationData.bundleId
            }
        };
    } else if (operationName == "INSTALL_STORE_APPLICATION") {
        payload = {
            "deviceIDs": devices,
            "operation": {
                "identifier": operationData.appIdentifier,
                "manifestURL": operationData.manifestURL,
                "removeAppUponMDMProfileRemoval": operationData.appRemoval,
                "preventBackupOfAppData": operationData.backupData,
                "bundleId": operationData.bundleId
            }
        };
    } else if (operationName == "REMOVE_APPLICATION"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "bundleId": operationData.bundleId
            }
        };
    } else if (operationName == "RESTRICTION"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "allowCamera": operationData.allowCamera,
                "allowCloudBackup": operationData.allowCloudBackup,
                "allowSafari": operationData.allowSafari,
                "allowScreenShot": operationData.allowScreenshot,
                "allowAirDrop": operationData.allowAirDrop
            }
        };
    }  else if (operationName == "CELLULAR"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "attachAPNName": null,
                "authenticationType": null,
                "username": null,
                "password": null,
                "apnConfigurations": [
                    {
                        "configurationName": null,
                        "authenticationType": null,
                        "username": null,
                        "password": null,
                        "proxyServer": null,
                        "proxyPort": 0
                    }
                ]
            }
        };
    }else {
        // The payload of command operations are set as device ids
        payload = devices;
    }
    return payload;
}
function createAndroidPayload(operationName, operationData, devices) {
    var payload;
    if (operationName == "CAMERA") {
        payload = {
            "deviceIDs": devices,
            "operation": {
                "enabled" : operationData.enableCamera
            }
        };
    } else if (operationName == "CHANGE_LOCK_CODE") {
        payload = {
            "deviceIDs": devices,
            "operation": {
                "lockCode" : operationData.lockCode
            }
        };
    } else if (operationName == "ENCRYPT_STORAGE") {
        payload = {
            "deviceIDs": devices,
            "operation": {
                "encrypted" : operationData.enableEncryption
            }
        };
    } else if (operationName == "NOTIFICATION"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "message" : operationData.message
            }
        };
    } else if (operationName == "WEBCLIP"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "identity": operationData.url,
                "title": operationData.title

            }
        };
    } else if (operationName == "INSTALL_APPLICATION"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "appIdentifier": operationData.packageName,
                "type": operationData.type,
                "url": operationData.url
            }
        };
    } else if (operationName == "UNINSTALL_APPLICATION"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "appIdentifier": operationData.packageName
            }
        };
    } else if (operationName == "BLACKLIST_APPLICATIONS"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "appIdentifier": operationData.packageNames
            }
        };
    } else if (operationName == "PASSCODE_POLICY"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "maxFailedAttempts": operationData.maxFailedAttempts,
                "minLength": operationData.minLength,
                "pinHistory": operationData.pinHistory,
                "minComplexChars": operationData.minComplexChars,
                "maxPINAgeInDays": operationData.maxPINAgeInDays,
                "requireAlphanumeric": operationData.requireAlphanumeric,
                "allowSimple": operationData.allowSimple

            }
        };
    } else if (operationName == "WIFI"){
        payload = {
            "deviceIDs": devices,
            "operation": {
                "ssid": operationData.ssid,
                "password": operationData.password

            }
        };
    } else {
        payload = devices;
    }
    return payload;
}
function getAndroidServiceEndpoint (operationName) {
    var featureMap = {
        DEVICE_LOCK: "lock",
        DEVICE_LOCATION: "location",
        CLEAR_PASSWORD: "clear-password",
        CAMERA: "camera",
        ENTERPRISE_WIPE: "enterprise-wipe",
        WIPE_DATA: "wipe-data",
        APPLICATION_LIST: "get-application-list",
        DEVICE_RING: "ring-device",
        DEVICE_MUTE: "mute",
        NOTIFICATION: "notification",
        WIFI: "wifi",
        ENCRYPT_STORAGE: "encrypt",
        CHANGE_LOCK_CODE: "change-lock-code",
        WEBCLIP: "webclip",
        INSTALL_APPLICATION: "install-application",
        UNINSTALL_APPLICATION: "uninstall-application",
        BLACKLIST_APPLICATIONS: "blacklist-applications",
        PASSCODE_POLICY: "password-policy"
    };
    return "https://localhost:9443/mdm-android-agent/operation/" + featureMap[operationName];
}

function runOperation(operationName) {
    var operationData = {};
    $(".modalpopup-content > .operationData[data-operation='"+operationName+"']").find(".operationDataKeys").each(
        function(index){
            var operationDataObj = $(this);
            var key = operationDataObj.data("key");
            var value = operationDataObj.val();
            if (operationDataObj.is(':checkbox')){
                if (value=="on"){
                    value = true;
                }else if(value=="off"){
                    value = false;
                }
            }else if (operationDataObj.is('select')){
                value = operationDataObj.find("option:selected").text();
            }
            operationData[key] = value;
        });
    var deviceIdList = getSelectedDeviceIds();
    var list = getDevicesByTypes(deviceIdList);
    var successCallback = function(message){
        console.log(message);
        $(".wr-notification-bar").append('<div class="wr-notification-desc new"><div ' +
        'class="wr-notification-operation">Device ' + operationName.toLowerCase() +
        'Operation Successful!</div><hr /> </div>');
        var notificationCount = parseInt($(".wr-notification-bubble").html());
        notificationCount++;
        $(".wr-notification-bubble").html(notificationCount);
    };
    if(list["ios"]){
        var payload = getiOSServiceEndpoint(operationName, operationData, list["ios"]);
        var serviceEndPoint = getiOSServiceEndpoint(operationName);

    }
    if(list["android"]){
        var payload = getAndroidServiceEndpoint(operationName, operationData, list["android"]);
        var serviceEndPoint = getAndroidServiceEndpoint(operationName);
    }
    invokerUtil.post(serviceEndPoint, payload,
        successCallback, function(jqXHR, textStatus, errorThrown){
            console.log(textStatus);
        });
    hidePopup();
}
