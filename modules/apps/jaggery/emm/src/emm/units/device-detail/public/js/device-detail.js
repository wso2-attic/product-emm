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

var InitiateViewOption = null;
(function () {
    var deviceId = $(".device-id");
    var deviceIdentifier = deviceId.data("deviceid");
    var deviceType = deviceId.data("type");
    var payload = [deviceIdentifier];
    var operationTable;
    if (deviceType == "ios") {
        var serviceUrl = "/ios/operation/deviceinfo";
    } else if (deviceType == "android") {
        var serviceUrl = "/mdm-android-agent/operation/device-info";
    }
    if(serviceUrl){
        invokerUtil.post(serviceUrl, payload,
            function(message){
                $(".panel-body").show();
            }, function (message) {
                var defaultInnerHTML =
                    "<br><p class='fw-warning'>Device data might not be updated Please refresh this page<p>";
                $(".panel-body").append(defaultInnerHTML);
            });
    }
    $('.media.tab-responsive [data-toggle=tab]').on('shown.bs.tab', function(e){
        var activeTabPane = $(e.target).attr('href'),
            activeCollpasePane = $(activeTabPane).find('[data-toggle=collapse]').data('target'),
            activeCollpasePaneSiblings = $(activeTabPane).siblings().find('[data-toggle=collapse]').data('target'),
            activeListGroupItem = $('.media .list-group-item.active');

        $(activeCollpasePaneSiblings).collapse('hide');
        $(activeCollpasePane).collapse('show');
        positionArrow(activeListGroupItem);

        $(".panel-heading .caret-updown").removeClass("fw-sort-down");
        $(".panel-heading.collapsed .caret-updown").addClass("fw-sort-up");
    });

    $('.media.tab-responsive .tab-content').on('shown.bs.collapse', function(e){
        var activeTabPane = $(e.target).parent().attr('id');
        $('.media.tab-responsive [data-toggle=tab][href=#'+activeTabPane+']').tab('show');
        $(".panel-heading .caret-updown").removeClass("fw-sort-up");
        $(".panel-heading.collapsed .caret-updown").addClass("fw-sort-down");
    });

    function positionArrow(selectedTab){
        var selectedTabHeight = $(selectedTab).outerHeight();
        var arrowPosition = 0;
        var totalHeight = 0;
        var arrow = $(".media .panel-group.tab-content .arrow-left");
        var parentHeight = $(arrow).parent().outerHeight();

        /*if($(selectedTab).prev().length){
            $(selectedTab).prevAll().each(function() {
                totalHeight += $(this).outerHeight();
            });
            arrowPosition = totalHeight + (selectedTabHeight / 2);
        }else{
            arrowPosition = selectedTabHeight / 2;
        }*/

        if(arrowPosition >= parentHeight){
            parentHeight = arrowPosition + 10;
            $(arrow).parent().height(parentHeight);
        }else{
            $(arrow).parent().removeAttr("style");
        }
        $(arrow).css("top",arrowPosition - 10);
    }

    $(document).ready(function(){
        $(".device-detail-body").removeClass("hidden");
        $("#loading-content").remove();
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
            loadOperationsLog(true);
        });

    });

    function loadOperationsLog(update) {
        var operationsLog = $("#operations-log");
        if (update) {
            operationTable = $('#operations-log-table').DataTable();
            operationTable.ajax.reload(null, false);
            return;
        }
        operationTable =  $('#operations-log-table').datatables_extended({
            serverSide: true,
            processing: false,
            searching: false,
            ordering:  false,
            pageLength : 10,
            order: [],
            ajax: {
                url : '/emm/api/operation/paginate',
                data : {deviceId : deviceIdentifier, deviceType: deviceType},
                dataSrc: function ( json ) {
                    $('#operations-spinner').addClass('hidden');
                    $("#operations-log-container").empty();
                    return json.data;
                }
            },
            columnDefs: [
                { targets: 0, data: 'code' },
                { targets: 1, data: 'status', render: function ( status, type, row, meta ) {
                    var html;
                    switch (status) {
                        case 'COMPLETED' :
                            html = '<span><i class="fw fw-ok icon-success"></i> Completed</span>';
                            break;
                        case 'PENDING' :
                            html = '<span><i class="fw fw-warning icon-warning"></i> Pending</span>';
                            break;
                        case 'ERROR' :
                            html = '<span><i class="fw fw-error icon-danger"></i> Error</span>';
                            break;
                        case 'IN_PROGRESS' :
                            html = '<span><i class="fw fw-ok icon-warning"></i> In Progress</span>';
                            break;
                    }
                    return html;
                }},
                { targets: 2, data: 'createdTimeStamp', render: function (date, type, row, meta) {;
                    var value = String(date);
                    return value.slice(0,16);
                    }
                }
            ],
            "createdRow": function( row, data, dataIndex ) {
                $(row).attr('data-type', 'selectable');
                $(row).attr('data-id', data.id);
                $.each($('td', row), function (colIndex) {
                    switch(colIndex) {
                        case 1:
                            $(this).attr('data-grid-label', 'Code');
                            $(this).attr('data-display', data.code);
                            break;
                        case 2:
                            $(this).attr('data-grid-label', 'Status');
                            $(this).attr('data-display', data.status);
                            break;
                        case 3:
                            $(this).attr('data-grid-label', "Created Timestamp");
                            $(this).attr('data-display', data.createdTimeStamp);
                            break;
                    }
                });
            }
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
                        data[i].name = decodeURIComponent(data[i].name);
                        data[i].platform = deviceType;
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
                    $("#applications-list-container").append("<br><p class='fw-warning'>Loading application was not" +
                                                             " successful please try again in a while<p>");
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
                data = JSON.parse(data);
                if (data != null && data.complianceFeatures!= null && data.complianceFeatures != undefined &&
                   data.complianceFeatures.length > 0) {
                    viewModel.compliance = "NON-COMPLIANT";
                    viewModel.complianceFeatures = data.complianceFeatures;
                    var content = template(viewModel);
                    $("#policy-list-container").html(content);
                } else {
                    viewModel.compliance = "COMPLIANT";
                    var content = template(viewModel);
                    $("#policy-list-container").html(content);
                    $("#policy-compliance-table").addClass("hidden");
                }

            };

            var successCallbackPolicy = function (data) {
                data = JSON.parse(data);
                $('#policy-spinner').addClass('hidden');
                if(data != null && data.active == true){
                    activePolicy = data;
                    invokerUtil.get(serviceURLCompliance,
                        successCallbackCompliance, function(message){
                            $("#policy-list-container").append("<br><p class='fw-warning'>Loading policy related data" +
                                                               " was not successful please try again in a while<p>");
                    });
                }
            };

            invokerUtil.get(serviceURLPolicy,
                successCallbackPolicy, function(message){
                    $("#policy-list-container").append("<br><p class='fw-warning'>Loading policy related was not" +
                    " successful please try again in a while<p>");
            });
        });

    }
}());
