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
        loadOperationsLog();
        loadApplicationsList();
        loadPolicyCompliance();

        $("#refresh-policy").click(function () {
            $('#policy-spinner').removeClass('hidden');
            loadPolicyCompliance();
        });

        $("#refresh-apps").click(function () {
            $('#apps-spinner').removeClass('hidden');
            loadApplicationsList();
        });

        $("#refresh-operations").click(function () {
            $('#operations-spinner').removeClass('hidden');
            loadOperationsLog();
        });

    });

    function loadOperationsLog() {
        var operationsLog = $("#operations-log");
        var deviceListingSrc = operationsLog.attr("src");
        var deviceId = operationsLog.data("device-id");
        var deviceType = operationsLog.data("device-type");

        $.template("operations-log", deviceListingSrc, function (template) {
            var serviceURL = "/mdm-admin/operations/"+deviceType+"/"+deviceId;

            var successCallback = function (data) {
                data = JSON.parse(data);
                $('#operations-spinner').addClass('hidden');
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

    function loadApplicationsList() {
        var applicationsList = $("#applications-list");
        var deviceListingSrc = applicationsList.attr("src");
        var deviceId = applicationsList.data("device-id");
        var deviceType = applicationsList.data("device-type");

        $.template("application-list", deviceListingSrc, function (template) {
            var serviceURL = "/mdm-admin/operations/"+deviceType+"/"+deviceId+"/apps";

            var successCallback = function (data) {
                data = JSON.parse(data);
                $('#apps-spinner').addClass('hidden');
                var viewModel = {};
                if(data != null && data.length > 0) {
                    for (var i = 0; i < data.length; i++) {
                        data[i].name = data[i].name.replace(/[^\w\s]/gi, ' ');
                        data[i].name = data[i].name.replace(/[0-9]/g, ' ');
                    }
                }
                viewModel.applications = data;
                viewModel.deviceType = deviceType;
                if(data.length > 0){
                    var content = template(viewModel);
                    $("#applications-list-container").html(content);
                }

            };
            invokerUtil.get(serviceURL,
                successCallback, function(message){
                    console.log(message);
            });
        });
    }

    function loadPolicyCompliance() {
        var policyCompliance = $("#policy-view");
        var policySrc = policyCompliance.attr("src");
        var deviceId = policyCompliance.data("device-id");
        var deviceType = policyCompliance.data("device-type");
        var activePolicy = null;

        $.template("policy-view", policySrc, function (template) {
            var serviceURLPolicy ="/mdm-admin/policies/"+deviceType+"/"+deviceId+"/active-policy"
            var serviceURLCompliance = "/mdm-admin/policies/"+deviceType+"/"+deviceId;

            var successCallbackCompliance = function (data) {
                var viewModel = {};
                viewModel.policy = activePolicy;
                viewModel.deviceType = deviceType;
                if(data != null && data.complianceFeatures!= null && data.complianceFeatures != undefined && data.complianceFeatures.length > 0) {
                    viewModel.compliance = "NON-COMPLIANT";
                    viewModel.complianceFeatures = data.complianceFeatures;
                    var content = template(viewModel);
                    $("#policy-list-container").html(content);
                } else {
                    viewModel.compliance = "COMPLIANT";
                    var content = template(viewModel);
                    $("#policy-list-container").html(content);
                    $("#operations-log-table").addClass("hidden");
                }

            };

            var successCallbackPolicy = function (data) {
                data = JSON.parse(data);
                $('#policy-spinner').addClass('hidden');
                if(data != null && data.active == true){
                    activePolicy = data;
                    invokerUtil.get(serviceURLCompliance,
                        successCallbackCompliance, function(message){
                            console.log(message);
                    });
                }
            };

            invokerUtil.get(serviceURLPolicy,
                successCallbackPolicy, function(message){
                    console.log(message);
            });
        });

    }

}());
