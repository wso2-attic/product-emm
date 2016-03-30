/*
 Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 WSO2 Inc. licenses this file to you under the Apache License,
 Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied. See the License for the
 specific language governing permissions and limitations
 under the License.
 */

var removeCustomParam = function () {
    $(this).parent().parent().remove();
};

var dynamicForm = '<div class="dynamic-search-param row"><div class="row"><a class="close-button-div icon fw fw-error">' +
                  '</a></div><div class="form-group wr-input-control col-md-2"><label class="wr-input-label ">State</label>' +
                  '<select class="state form-control select"><option>AND</option><option>OR</option></select></div><div ' +
                  'class="form-group wr-input-control col-md-4"><label class="wr-input-label ">Key</label><input type=' +
                  '"text" class="form-control txt-key"/></div><div class="form-group wr-input-control col-md-2">' +
                  '<label class="wr-input-label ">Operator</label><select class="form-control select operator">' +
                  '<option>=</option><option> !=</option><option> <</option>' +
                  '<option> =<</option><option> ></option><option> >=</option></select></div><div class="form-group ' +
                  'wr-input-control col-md-4"><label class="wr-input-label' +
                  ' ">Value</label><input type="text" class="form-control txt-value"/></div></div>';

$(document).ready(function () {

    $("#add-custom-param").click(function () {
        $("#customSearchParam").prepend(dynamicForm);
        $(".close-button-div").unbind("click");
        $(".close-button-div").bind("click", removeCustomParam);
    });

    $("#device-search-btn").click(function () {
        var location = $("#location").val();
        var payload_obj = {};
        var conditions = [];
        if (location) {
            var conditionObject = {};
            conditionObject.key = "LOCATION";
            conditionObject.value = location;
            conditionObject.operator = "=";
            conditionObject.state = "OR";
            conditions.push(conditionObject)
        }

        $("#customSearchParam .dynamic-search-param").each(function () {
            var value = $(this).find(".txt-value").val();
            var key = $(this).find(".txt-key").val()
            if (value && key) {
                var conditionObject = {};
                conditionObject.key = key;
                conditionObject.value = value;
                conditionObject.operator = $(this).find(".operator").val();
                conditionObject.state = $(this).find(".state").val();
                conditions.push(conditionObject)
            }
        });
        payload_obj.conditions = conditions;
        var deviceSearchAPI = "/mdm-admin/search";
        $("#advance-search-form").addClass(" hidden");
        $("#loading-content").removeClass('hidden');
        var deviceListing = $("#device-listing");
        var deviceListingSrc = deviceListing.attr("src");
        $.template("device-listing", deviceListingSrc, function (template) {

            var successCallback = function (data) {
                if (!data) {
                    $('#device-listing-status').removeClass('hidden');
                    $('#device-listing-status-msg').text('No Device are available to be displayed.');
                    return;
                }
                //console.log("data");
                data = JSON.parse(data);
                //console.log(data);
                //console.log("-------------------------");
                //console.log("/data");
                data = data.responseContent;
                //console.log(data);
                var viewModel = {};
                if (data.length > 0) {
                    $('#ast-container').removeClass('hidden');
                    $('#user-listing-status-msg').text("");
                    var content = template(viewModel);
                    $("#ast-container").html(content);
                } else {
                    $('#device-listing-status').removeClass('hidden');
                    $('#device-listing-status-msg').text('No Device are available to be displayed.');
                }
                $("#loading-content").addClass('hidden');
                if (isInit) {
                    $('#user-grid').datatables_extended();
                    isInit = false;
                }
                $(".icon .text").res_text(0.2);
            };
            invokerUtil.post(deviceSearchAPI,
                             payload_obj,
                             successCallback,
                             function (message) {
                                 $("#loading-content").addClass('hidden');
                                 $("#advance-search-result").addClass("hidden");
                                 $("#advance-search-form").removeClass(" hidden");
                                 $('#device-listing-status').removeClass('hidden');
                                 $('#certificate-listing-status-msg').text('Invalid search query. Try again with a valid search query');
                             }
            );
        });
    });
});
