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

(function () {
    var deviceId = $(".device-id");
    var deviceIdentifier = deviceId.data("deviceid");
    var deviceType = deviceId.data("type");
    var payload = [deviceIdentifier];
    if (deviceType == "ios") {
        var serviceUrl = "/ios/operation/deviceinfo";
    } else if (deviceType == "android") {
        var serviceUrl = "/mdm-android-agent/operation/device-info";
    }
    if(serviceUrl){
        invokerUtil.post(serviceUrl, payload,
            function(message){
                console.log(message);
            }, function (message) {
                console.log(message);
            });
    }
    $(document).ready(function(){
        loadOperationBar(deviceType);
        if (document.getElementById('device-location')){
            loadMap();
        }
        loadOperationsLog();
        loadApplicationsList();
    });

    function loadMap() {
        var map;
        function initialize() {
            var mapOptions = {
                zoom: 18
            };

                var lat = $("#device-location").data("lat");
                var long = $("#device-location").data("long");

                if(lat != null && lat != undefined && lat != "" && long != null && long != undefined && long != "") {
                    $("#map-error").hide();
                    $("#device-location").show();
                    map = new google.maps.Map(document.getElementById('device-location'),
                        mapOptions);

                    var pos = new google.maps.LatLng(lat,
                        long);
                    var marker = new google.maps.Marker({
                        position: pos,
                        map: map
                    });

                    map.setCenter(pos);
                }else{
                    $("#device-location").hide();
                    $("#map-error").show();
                }

        }
        google.maps.event.addDomListener(window, 'load', initialize);
    }

    function loadOperationsLog() {
        var operationsLog = $("#operations-log");
        var deviceListingSrc = operationsLog.attr("src");
        var deviceId = operationsLog.data("device-id");
        var deviceType = operationsLog.data("device-type");

        $.template("operations-log", deviceListingSrc, function (template) {
            var serviceURL = "/mdm-admin/operations/"+deviceType+"/"+deviceId;

            var successCallback = function (data) {
                var viewModel = {};
                viewModel.operations = data;
                if(data.length > 0){
                    var content = template(viewModel);
                    $("#operations-log-container").html(content);
                    $('#operations-log-table').datatables_extended();
                }

            };
            invokerUtil.get(serviceURL,
                successCallback, function(message){
                    console.log(message);
            });
        });
    }
}());
