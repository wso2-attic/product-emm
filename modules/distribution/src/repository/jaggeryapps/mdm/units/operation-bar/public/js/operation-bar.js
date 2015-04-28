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
    deviceSelection = '.device-select';

/*
 * On operation click function.
 * @param selection: Selected operation
 */
function operationSelect(selection){
    $(modelPopupContent).html($(operations + ' .operation[data-operation='+selection+']').html());
    showPopup();
}

/*
 * On operation click function.
 * @param operation: Selected operation
 */
function runOperation(operation){
    console.log(operation);
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
            }
            operationData[key] = value;
        });
    var operationObject = {"code": operationName, "type": "COMMAND", properties: []};
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
        // Command operations doesn't need a payload
        var iOSFeatureMap = {
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
        var payload = list["ios"];
        var operation = iOSFeatureMap[operationName];
        if (operationName == "AIR_PLAY") {
            payload = {
                "deviceIDs": list["ios"],
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
                "deviceIDs": list["ios"],
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
                "deviceIDs": list["ios"],
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
                "deviceIDs": list["ios"],
                "operation": {
                    "bundleId": operationData.bundleId
                }
            };
        } else if (operationName == "RESTRICTION"){
            payload = {
                "deviceIDs": list["ios"],
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
                "deviceIDs": list["ios"],
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
        }
        invokerUtil.post("https://localhost:9443/ios/operation/" + operation, payload,
            successCallback, function(jqXHR, textStatus, errorThrown){
                console.log(textStatus);
            });
    }
    if(list["android"]){
        var payload =  list["android"];
        invokerUtil.post("https://localhost:9443/android/operations/lock", payload,
            successCallback, function(jqXHR, textStatus, errorThrown){
                console.log(errorThrown);
            });
    }
    hidePopup();
}
