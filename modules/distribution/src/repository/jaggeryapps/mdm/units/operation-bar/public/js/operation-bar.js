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

function runOperation(operation) {
    var operationObject = {"code": operation, "type": "COMMAND", properties: []};
    var deviceIdList = getSelectedDeviceIds();
    var list = getDevicesByTypes(deviceIdList);
    var successCallback = function(message){
        console.log(message);
        $(".wr-notification-bar").append('<div class="wr-notification-desc new"><div class="wr-notification-operation">' +
        'Device '+operation.toLowerCase()+' Operation Successful!</div><hr /> </div>');
        var notificationCount = parseInt($(".wr-notification-bubble").html());
        notificationCount++;
        $(".wr-notification-bubble").html(notificationCount);
    };
    if(list["ios"]){
        var payload = list["ios"];
        $.ajax({
            url: "https://localhost:9443/ios/operation/lock",
            type: "POST",
            data: JSON.stringify(payload),
            contentType: "application/json",
            accept: "application/json",
            dataType: "json",
            success: successCallback
        }).error(function(message, sdf,sdf){
            console.log(message);
        });
    }
    if(list["android"]){
        var payload = {
            "devices" : list["android"],
            "operation" : operationObject
        };
        $.ajax({
            url: "https://localhost:9443/mdm/api/operation",
            type: "POST",
            data: JSON.stringify(payload),
            contentType: "application/json",
            dataType: "json",
            success: successCallback
        }).error(function(message, sdf,sdf){
            console.log(message);
        });
    }
    hidePopup();
}
