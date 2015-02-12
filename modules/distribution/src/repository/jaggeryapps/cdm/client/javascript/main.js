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
function identifierFormatter(value, row, index) {
    return [
        '<a class="like" href="/cdm/devices/' + row["deviceType"] + '/' + escape(value) + '" title="Like">',
        value,
        '</a>'
    ].join('');
}

var currentDeviceOperation;
var currentDevice;
var currentDeviceType;
function performOperation(){
    currentDevice = escape($("#deviceMain").data("deviceid"));
    currentDeviceType = $("#deviceMain").data("devicetype");
    $.post("/cdm/api/operation/"+currentDeviceType+"/"+currentDevice+"/"+currentDeviceOperation,function(){
        $('#confirmModel').modal('hide');
    });
}

$(document).ready(function(){
    $(".device-operation").click(function(){
        currentDeviceOperation = $(this).data("operation");
        $('#confirmModel').modal('show');
    });
});