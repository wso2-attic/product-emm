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
    modalPopup = '.wr-modalpopup',
    modalPopupContainer = modalPopup + ' .modalpopup-container',
    modalPopupContent = modalPopup + ' .modalpopup-content',
    deviceCheckbox = '#ast-container .ctrl-wr-asset .itm-select input[type="checkbox"]',
    showOperationsBtn = '#showOperationsBtn',
    navHeight = $('#nav').height(),
    headerHeight = $('header').height(),
    offset = (headerHeight + navHeight),
    maxOperationsLimit = 15,
    hiddenOperation = '.wr-hidden-operations-content > div',
    deviceSelection = '.device-select',
    currentOperationList = [];

/*
 * DOM ready functions.
 */
$(document).ready(function(){
    if($(operations + "> a").length > maxOperationsLimit){
        $(showOperationsBtn).show();
    }
    else{
        $(operations).show();
    }
    toggleMoreOperationsHeight();
    //loadOperationBar("ios");
    /**
     * Android App type javascript
     */
    $(".wr-modalpopup").on("click", ".appTypesInput", function(){
        var appType = $(".appTypesInput").val();
        if (appType == "Public") {
            $('.appURLInput').prop( "disabled", true );
        }else if (appType == "Enterprise"){
            $('.appURLInput').prop( "disabled", false );
        }
    }).trigger("change");
});


/*
 * On window loaded functions.
 */
$(window).load(function(){
    setPopupMaxHeight();
});

/*
 * On window resize functions.
 */
$(window).resize(function(){
    toggleMoreOperationsHeight();
    setPopupMaxHeight();
});

/*
 * On main div.container resize functions.
 * @required  jquery.resize.js
 */
$('.container').resize(function(){
    toggleMoreOperationsHeight();
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
    var deviceIdList = getSelectedDeviceIds();
    $(modalPopupContent).addClass("operation-data");
    if (deviceIdList == 0){
        $(modalPopupContent).html($('#errorOperations').html());
    }else {
        $(modalPopupContent).html($(operations + ' .operation[data-operation='+selection+']').html());
        $(modalPopupContent).data("operation", selection);
    }
    showPopup();
}

/*
 * show popup function.
 */
function showPopup() {
    $(modalPopup).show();
}

/*
 * hide popup function.
 */
function hidePopup() {
    $(modalPopupContent).html('');
    $(modalPopupContent).removeClass("operation-data");
    $(modalPopup).hide();
}

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    $(modalPopupContent).css('max-height', ($('body').height() - ($('body').height()/100 * 30)));
    $(modalPopupContainer).css('margin-top', (-($(modalPopupContainer).height()/2)));
}


/*
 * Function to open hidden device operations list
 */
function toggleMoreOperations(){
    $('.wr-hidden-operations, .wr-page-content').toggleClass('toggled');
    $(showOperationsBtn).toggleClass('selected');
    //$('.footer').toggleClass('wr-hidden-operations-toggled');
}

/*
 * Function to fit hidden device operation window height with the screen
 */
function toggleMoreOperationsHeight(){
    $('.wr-hidden-operations').css('min-height', $('html').height() - (offset+140));
}

/*
 * Advance operations sub categories show/hide toggle function
 */
function showAdvanceOperation(operation, button){
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
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
function unloadOperationBar(){
    $("#showOperationsBtn").addClass("hidden");
    $(".wr-operations").html("");
}

function loadOperationBar(deviceType){
    var operationBar = $("#operations-bar");
    var operationBarSrc = operationBar.attr("src");
    var platformType = deviceType;
    $.template("operations-bar", operationBarSrc, function (template) {
        var serviceURL = "https://localhost:9443/mdm-admin/features/" + platformType;
        var successCallback = function (data) {
            var viewModel = {};
            var iconMap = {};
            data = data.filter(function(current){
                var iconName;
                if (deviceType == "android"){
                    var iconName = getAndroidIconForFeature(current.code);
                } else if (deviceType == "ios"){
                    var iconName = getiOSIconForFeature(current.code);
                }
                if (iconName){
                    current.icon = iconName;
                    return current;
                }
            });
            viewModel.features = data;
            var content = template(viewModel);
            $(".wr-operations").html(content);
        };
        invokerUtil.get(serviceURL,
            successCallback, function(message){
                console.log(message);
            });
    });
    var hiddenOperationBar = $("#hidden-operations-bar-" + deviceType);
    var hiddenOperationBarSrc = hiddenOperationBar.attr("src");
    $.template("hidden-operations-bar-" + deviceType, hiddenOperationBarSrc, function (template) {
        var serviceURL = "https://localhost:9443/mdm-admin/features/" + platformType;
        var successCallback = function (data) {
            var viewModel = {};
            viewModel.features = data.reduce(function (total, current) {
                total[current.code] = current;
                return total;
            }, {});
            currentOperationList = viewModel.features;
            var content = template(viewModel);
            $(".wr-hidden-operations").html(content);
        };
        invokerUtil.get(serviceURL,
            successCallback, function(message){
                console.log(message);
            });
    });
    $("#showOperationsBtn").removeClass("hidden");
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
        CELLULAR: "cellular",
        ENTERPRISE_WIPE: "enterprisewipe",
        WIFI: "wifi"
    };
    return "https://localhost:9443/ios/operation/" + featureMap[operationName];
}

function createiOSPayload(operationName, operationData, devices) {
    // Command operations doesn't need a payload
    var payload;
    var operationType = "profile";
    if (operationName == "AIR_PLAY") {
        payload = {
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
    }else if (operationName == "INSTALL_STORE_APPLICATION") {
        payload = {
            "operation": {
                "identifier": operationData.appIdentifier,
                "iTunesStoreID": operationData.ituneID,
                "removeAppUponMDMProfileRemoval": operationData.appRemoval,
                "preventBackupOfAppData": operationData.backupData,
                "bundleId": operationData.bundleId
            }
        };
    } else if (operationName == "INSTALL_ENTERPRISE_APPLICATION") {
        payload = {
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
            "operation": {
                "bundleId": operationData.bundleId
            }
        };
    } else if (operationName == "RESTRICTION"){
        payload = {
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
    } else if (operationName == "WIFI"){
        payload = {
            "operation": {
                "hiddenNetwork": operationData.hiddenNetwork,
                "autoJoin": operationData.autoJoin,
                "encryptionType": operationData.encryptionType,
                "hotspot": false,
                "domainName": null,
                "serviceProviderRoamingEnabled": false,
                "displayedOperatorName": null,
                "proxyType": null,
                "roamingConsortiumOIs": null,
                "password": operationData.password,
                "clientConfiguration": {
                    "username": null,
                    "acceptEAPTypes": null,
                    "userPassword": null,
                    "oneTimePassword": false,
                    "payloadCertificateAnchorUUID": null,
                    "outerIdentity": null,
                    "tlstrustedServerNames": null,
                    "tlsallowTrustExceptions": false,
                    "tlscertificateIsRequired": false,
                    "ttlsinnerAuthentication": null,
                    "eapfastusePAC": false,
                    "eapfastprovisionPAC": false,
                    "eapfastprovisionPACAnonymously": false,
                    "eapsimnumberOfRANDs": 0
                },
                "payloadCertificateUUID": null,
                "proxyServer": null,
                "proxyPort": 0,
                "proxyUsername": null,
                "proxyPassword": null,
                "proxyPACURL": null,
                "proxyPACFallbackAllowed": false,
                "ssid": operationData.ssid,
                "nairealmNames": null,
                "mccandMNCs": null
            }
        };
    } else if (operationName == "MAIL"){
        payload = {
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
    } else {
        // The payload of command operations are set as device ids
        payload = devices;
        operationType = "command";
    }
    if (operationType == "profile" && devices) {
        payload.deviceIDs = devices;
    }
    return payload;
}
function getiOSIconForFeature(featureName){
    var featureMap = {
        DEVICE_LOCK: "fw-lock",
        LOCATION: "fw-map-location",
        ENTERPRISE_WIPE: "fw-clean",
        ALARM: "fw-dial-up"
    };
    return featureMap[featureName];
}
function createAndroidPayload(operationName, operationData, devices) {
    var payload;
    var operationType = "profile";
    if (operationName == "CAMERA") {
        payload = {
            "operation": {
                "enabled" : operationData.enableCamera
            }
        };
    } else if (operationName == "CHANGE_LOCK_CODE") {
        payload = {
            "operation": {
                "lockCode" : operationData.lockCode
            }
        };
    } else if (operationName == "ENCRYPT_STORAGE") {
        payload = {
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
            "operation": {
                "identity": operationData.url,
                "title": operationData.title

            }
        };
    } else if (operationName == "INSTALL_APPLICATION"){
        payload = {
            "operation": {
                "appIdentifier": operationData.packageName,
                "type": operationData.type,
                "url": operationData.url
            }
        };
    } else if (operationName == "UNINSTALL_APPLICATION"){
        payload = {
            "operation": {
                "appIdentifier": operationData.packageName
            }
        };
    } else if (operationName == "BLACKLIST_APPLICATIONS"){
        payload = {
            "operation": {
                "appIdentifier": operationData.packageNames
            }
        };
    } else if (operationName == "PASSCODE_POLICY"){
        payload = {
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
            "operation": {
                "ssid": operationData.ssid,
                "password": operationData.password

            }
        };
    } else {
        operationType = "command";
        payload = devices;
    }
    if (operationType == "profile" && devices) {
        payload.deviceIDs = devices;
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
function getAndroidIconForFeature(featureName){
    var featureMap = {
        DEVICE_LOCK: "fw-lock",
        DEVICE_LOCATION: "fw-map-location",
        CLEAR_PASSWORD: "fw-key",
        ENTERPRISE_WIPE: "fw-clean",
        WIPE_DATA: "fw-database",
        DEVICE_RING: "fw-dial-up",
        DEVICE_MUTE: "fw-incoming-call",
        NOTIFICATION: "fw-message",
        CHANGE_LOCK_CODE: "fw-padlock"
    };
    return featureMap[featureName];
}
$.fn.filterByData = function(prop, val) {
    return this.filter(
        function() { return $(this).data(prop)==val; }
    );
}
/*
 @DeviceType = Device Type of the profile
 @operationCode = Feature Codes to generate the profile from
 @DeviceList = Optional device list to include in payload body for operations
 */
function generatePayload(deviceType, operationCode, deviceList){
    var payload;
    var operationData = {};
    $(".operation-data").filterByData("operation", operationCode).find(".operationDataKeys").each(
        function(index){
            var operationDataObj = $(this);
            var key = operationDataObj.data("key");
            var value = operationDataObj.val();
            if (operationDataObj.is(':checkbox')){
                value = operationDataObj.is(":checked");
            }else if (operationDataObj.is('select')){
                var value = operationDataObj.find("option:selected").data("id");
                if (!value){
                    value = operationDataObj.find("option:selected").text();
                }
            }
            operationData[key] = value;
        });
    if(deviceType == "ios"){
        payload = createiOSPayload(operationCode, operationData, deviceList);
    }
    if(deviceType == "android"){
        payload = createAndroidPayload(operationCode, operationData, deviceList);
    }
    return payload;
}

/*
    @DeviceType = Device Type of the profile
    @FeatureCodes = Feature Codes to generate the profile from
 */
function generateProfile(deviceType, featureCodes){
    var generatedProfile = {};
    for (var i = 0; i < featureCodes.length; ++i) {
        var featureCode = featureCodes[i];
        var payload = generatePayload(deviceType, featureCode);
        generatedProfile[featureCode] = payload;
    }
    return generatedProfile;
}
function runOperation(operationName) {
    var deviceIdList = getSelectedDeviceIds();
    var list = getDevicesByTypes(deviceIdList);
    var successCallback = function(message){
        console.log(message);
        $(".wr-notification-bar").append('<div class="wr-notification-desc new"><div ' +
        'class="wr-notification-operation">' + currentOperationList[operationName].name +
        '- Operation Successful!</div><hr /></div>');
        var notificationCount = parseInt($(".wr-notification-bubble").html());
        notificationCount++;
        $(".wr-notification-bubble").html(notificationCount);
    };
    if(list["ios"]){
        var payload = generatePayload("ios", operationName, list["ios"]);
        var serviceEndPoint = getiOSServiceEndpoint(operationName);

    }
    if(list["android"]){
        var payload = generatePayload("android", operationName, list["android"]);
        var serviceEndPoint = getAndroidServiceEndpoint(operationName);
    }
    invokerUtil.post(serviceEndPoint, payload,
        successCallback, function(jqXHR, textStatus, errorThrown){
            console.log(textStatus);
        });
    $(modalPopupContent).removeData();
    hidePopup();
}
