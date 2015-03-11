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
    modelPopupContent = modelPopup + ' .modalpopup-content';

/*
 * On operation click function.
 * @param selection: Selected operation
 */
function operationSelect(selection){
    $(modelPopupContent).html($(operations + ' .operation[data-operation='+selection+']').html());
    showPopup();
}

function showPopup() {
    $(modelPopup).show();
}

function hidePopup() {
    $(modelPopupContent).html('');
    $(modelPopup).hide();
}

function getSelectedDeviceIds(){
    var deviceIdentifierList = [];
    $(".device-checkbox").each(function(index){
        var device = $(this);
        var deviceId = device.data("deviceid");
        var deviceType = device.data("type");
        deviceIdentifierList.push({
            "id" : deviceId,
            "type" : deviceType
        });
    });
    return deviceIdentifierList;
}
/*{
    "devices": [
    {
        "type": "android",
        "id": "sdkfjlsdkfjslkf"
    }
],
    "operation": {
    "code": "DEVICE_LOCK",
        "properties": {
        "key": "value"
    },
    "type": "COMMAND"
}
}
*/

function runOperation(operation) {
    var operationObject = {"code": operation, "type": "COMMAND"};
    var deviceIdList = getSelectedDeviceIds();
    var payload = {
        "devices" : deviceIdList,
        "operation" : operationObject
    };
    $.ajax({
        url:"https://localhost:9443/wso2mdm-api/operations",
        type:"POST",
        data: JSON.stringify(payload),
        contentType:"application/json",
        dataType:"json",
        success: function(message){
            console.log(message);
        }
    }).error(function(message, sdf,sdf){
        console.log(message);
    });
    hidePopup();
}
