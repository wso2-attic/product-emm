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
    modalPopupContent = modalPopup + ' .modalpopup-content',
    navHeight = $('#nav').height(),
    headerHeight = $('header').height(),
    offset = (headerHeight + navHeight),
    deviceSelection = '.device-select',
    platformTypeConstants = {
        "ANDROID": "android",
        "IOS": "ios",
        "WINDOWS": "windows"
    };

/*
 * Function to get selected devices ID's
 */
function getSelectedDeviceIds() {
    var deviceIdentifierList = [];
    $(deviceSelection).each(function (index) {
        var device = $(this);
        var deviceId = device.data('deviceid');
        var deviceType = device.data('type');
        deviceIdentifierList.push({
            "id": deviceId,
            "type": deviceType
        });
    });
    if (deviceIdentifierList.length == 0) {
        var thisTable = $(".DTTT_selected").closest('.dataTables_wrapper').find('.dataTable').dataTable();
        thisTable.api().rows().every(function () {
            if ($(this.node()).hasClass('DTTT_selected')) {
                var deviceId = $(thisTable.api().row(this).node()).data('deviceid');
                var deviceType = $(thisTable.api().row(this).node()).data('devicetype');
                deviceIdentifierList.push({
                    "id": deviceId,
                    "type": deviceType
                });
            }
        });
    }

    return deviceIdentifierList;
}

/*
 * On operation click function.
 * @param selection: Selected operation
 */
function operationSelect(selection) {
    var deviceIdList = getSelectedDeviceIds();
    if (deviceIdList == 0) {
        $(modalPopupContent).html($("#errorOperations").html());
    } else {
        $(modalPopupContent).addClass("operation-data");
        $(modalPopupContent).html($(operations + " .operation[data-operation-code=" + selection + "]").html());
        $(modalPopupContent).data("operation-code", selection);
    }
    showPopup();
}

function getDevicesByTypes(deviceList) {
    var deviceTypes = {};
    $.each(deviceList, function (index, item) {
        if (!deviceTypes[item.type]) {
            deviceTypes[item.type] = [];
        }
        if (item.type == platformTypeConstants.ANDROID ||
            item.type == platformTypeConstants.IOS || item.type == platformTypeConstants.WINDOWS) {
            deviceTypes[item.type].push(item.id);
        }
    });
    return deviceTypes;
}

function unloadOperationBar() {
    $("#showOperationsBtn").addClass("hidden");
    $(".wr-operations").html("");
}

function loadOperationBar(deviceType) {
    var operationBar = $("#operations-bar");
    var operationBarSrc = operationBar.attr("src");
    var platformType = deviceType;
    $.template("operations-bar", operationBarSrc, function (template) {
        var serviceURL = "/mdm-admin/features/" + platformType;
        var successCallback = function (data) {
            var viewModel = {};
            data = JSON.parse(data).filter(function (current) {
                var iconName;
                switch(deviceType) {
                    case platformTypeConstants.ANDROID:
                        iconName = operationModule.getAndroidIconForFeature(current.code);
                        current.type = deviceType;
                        break;
                    case platformTypeConstants.WINDOWS:
                        iconName = operationModule.getWindowsIconForFeature(current.code);
                        break;
                    case platformTypeConstants.IOS:
                        iconName = operationModule.getIOSIconForFeature(current.code);
                        break;
                }

                if (iconName) {
                    current.icon = iconName;
                    return current;
                }
            });
            viewModel.features = data;
            var content = template(viewModel);
            $(".wr-operations").html(content);
        };
        invokerUtil.get(serviceURL, successCallback, function (message) {
            $(".wr-operations").html(message);
            });
    });
}

function runOperation(operationName) {
    var deviceIdList = getSelectedDeviceIds();
    var list = getDevicesByTypes(deviceIdList);

    var successCallback = function (data) {
        if (operationName == "NOTIFICATION") {
            $(modalPopupContent).html($("#messageSuccess").html());
        } else {
            $(modalPopupContent).html($("#operationSuccess").html());
        }
        showPopup();
    };
    var errorCallback = function (data) {
        $(modalPopupContent).html($("#errorOperationUnexpected").html());
        showPopup();
    };

    var payload, serviceEndPoint;
    if (list[platformTypeConstants.IOS]) {
        payload = operationModule.
            generatePayload(platformTypeConstants.IOS, operationName, list[platformTypeConstants.IOS]);
        serviceEndPoint = operationModule.getIOSServiceEndpoint(operationName);
    } else if (list[platformTypeConstants.ANDROID]) {
        payload = operationModule
            .generatePayload(platformTypeConstants.ANDROID, operationName, list[platformTypeConstants.ANDROID]);
        serviceEndPoint = operationModule.getAndroidServiceEndpoint(operationName);
    } else if (list[platformTypeConstants.WINDOWS]) {
        payload = operationModule.
            generatePayload(platformTypeConstants.WINDOWS, operationName, list[platformTypeConstants.WINDOWS]);
        serviceEndPoint = operationModule.getWindowsServiceEndpoint(operationName);
    }
    if (operationName == "NOTIFICATION") {
        var errorMsgWrapper = "#notification-error-msg";
        var errorMsg = "#notification-error-msg span";
        var message = $("#message").val();
        if (!message) {
            $(errorMsg).text("Enter a message. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else {
            invokerUtil.post(serviceEndPoint, payload, successCallback, errorCallback);
            $(modalPopupContent).removeData();
            hidePopup();
        }
    } else if (operationName == "UPGRADE_FIRMWARE") {
        var errorMsgWrapper = "#firmware-error-msg";
        var errorMsg = "#firmware-error-msg span";
        var schedule = $("#schedule").val();
        if (!schedule) {
            $(errorMsg).text("Enter schedule information. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else {
            invokerUtil.post(serviceEndPoint, payload, successCallback, errorCallback);
            $(modalPopupContent).removeData();
            hidePopup();
        }
    } else {
        invokerUtil.post(serviceEndPoint, payload, successCallback, errorCallback);
        $(modalPopupContent).removeData();
        hidePopup();
    }
}

/*
 * DOM ready functions.
 */
$(document).ready(function () {
    $(operations).show();
});
