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

/**
 * Following function would execute
 * when a user clicks on the list item
 * initial mode and with out select mode.
 */
function InitiateViewOption(url) {
    if ($(".select-enable-btn").text() == "Select") {
        $(location).attr('href', url);
    }
}

(function () {
    var cache = {};
    var permissionSet = {};
    var validateAndReturn = function (value) {
        return (value == undefined || value == null) ? "Unspecified" : value;
    };
    Handlebars.registerHelper("deviceMap", function (device) {
        device.owner = validateAndReturn(device.owner);
        device.ownership = validateAndReturn(device.ownership);
        var arr = device.properties;
        if (arr){
            device.properties = arr.reduce(function (total, current) {
                total[current.name] = validateAndReturn(current.value);
                return total;
            }, {});
        }
    });

    //This method is used to setup permission for device listing
    $.setPermission = function (permission) {
        permissionSet[permission] = true;
    };

    $.hasPermission = function (permission) {
        return permissionSet[permission];
    };
})();

/*
 * Setting-up global variables.
 */
var deviceCheckbox = "#ast-container .ctrl-wr-asset .itm-select input[type='checkbox']";
var assetContainer = "#ast-container";

/*
 * DOM ready functions.
 */
$(document).ready(function () {
    /* Adding selected class for selected devices */
    $(deviceCheckbox).each(function () {
        addDeviceSelectedClass(this);
    });

    var i;
    var permissionList = $("#permission").data("permission");
    for (i = 0; i < permissionList.length; i++) {
        $.setPermission(permissionList[i]);
    }

    /* for device list sorting drop down */
    $(".ctrl-filter-type-switcher").popover({
        html : true,
        content : function () {
            return $("#content-filter-types").html();
        }
    });

    $(".ast-container").on("click", ".claim-btn", function(e){
        e.stopPropagation();
        var deviceId = $(this).data("deviceid");
        var deviceListing = $("#device-listing");
        var currentUser = deviceListing.data("current-user");
        var serviceURL = "/temp-controller-agent/enrollment/claim?username=" + currentUser;
        var deviceIdentifier = {id: deviceId, type: "TemperatureController"};
        invokerUtil.put(serviceURL, deviceIdentifier, function(message){
            console.log(message);
        }, function(message){
                console.log(message.content);
            });
    });
});

/*
 * On Select All Device button click function.
 *
 * @param button: Select All Device button
 */
function selectAllDevices(button) {
    if(!$(button).data('select')){
        $(deviceCheckbox).each(function(index){
            $(this).prop('checked', true);
            addDeviceSelectedClass(this);
        });
        $(button).data('select', true);
        $(button).html('Deselect All Devices');
    }else{
        $(deviceCheckbox).each(function(index){
            $(this).prop('checked', false);
            addDeviceSelectedClass(this);
        });
        $(button).data('select', false);
        $(button).html('Select All Devices');
    }
}

/*
 * On listing layout toggle buttons click function.
 *
 * @param view: Selected view type
 * @param selection: Selection button
 */
function changeDeviceView(view, selection) {
    $(".view-toggle").each(function() {
        $(this).removeClass("selected");
    });
    $(selection).addClass("selected");
    if (view == "list") {
        $(assetContainer).addClass("list-view");
    } else {
        $(assetContainer).removeClass("list-view");
    }
}

/*
 * Add selected style class to the parent element function.
 *
 * @param checkbox: Selected checkbox
 */
function addDeviceSelectedClass(checkbox) {
    if ($(checkbox).is(":checked")) {
        $(checkbox).closest(".ctrl-wr-asset").addClass("selected device-select");
    } else {
        $(checkbox).closest(".ctrl-wr-asset").removeClass("selected device-select");
    }
}

function toTitleCase(str) {
    return str.replace(/\w\S*/g, function(txt){return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();});
}

function loadDevices(searchType, searchParam){
    var deviceListing = $("#device-listing");
    var currentUser = deviceListing.data("currentUser");

    var serviceURL;
    if ($.hasPermission("LIST_DEVICES")) {
        serviceURL = "/mdm-admin/devices";
    } else if ($.hasPermission("LIST_OWN_DEVICES")) {
        //Get authenticated users devices
        serviceURL = "/mdm-admin/users/devices?username="+currentUser;
    } else {
        $("#loading-content").remove();
        $('#device-table').addClass('hidden');
        $('#device-listing-status-msg').text('Permission denied.');
        $("#device-listing-status").removeClass(' hidden');
        return;
    }

    function getPropertyValue(deviceProperties, propertyName) {
        var property;
        for (var i =0; i < deviceProperties.length; i++) {
            property = deviceProperties[i];
            if (property.name == propertyName) {
                return property.value;
            }
        }
        return {};
    }

    $('#device-grid').datatables_extended({
        serverSide: true,
        processing: false,
        searching: true,
        ordering:  false,
        filter: false,
        pageLength : 16,
        ajax: { url : '/emm/api/devices', data : {url : serviceURL},
                dataSrc: function ( json ) {
                    $('#device-grid').removeClass('hidden');
                    $("#loading-content").remove();
                    var $list = $("#device-table :input[type='search']");
                    $list.each(function(){
                        $(this).addClass("hidden");
                    });
                    return json.data;
                }
        },
        columnDefs: [
            { targets: 0, data: 'name', className: 'remove-padding icon-only content-fill viewEnabledIcon' , render: function ( data, type, row, meta ) {
                var deviceType = row.type;
                var deviceIdentifier = row.deviceIdentifier;
                var url = "#";
                if (status != 'REMOVED') {
                    url = "devices/view?type=" + deviceType + "&id=" + deviceIdentifier;
                }
                return '<div onclick="javascript:InitiateViewOption(\'' + url + '\')" class="thumbnail icon"><i class="square-element text fw fw-mobile"></i></div>';
            }},
            { targets: 1, data: 'name', className: 'fade-edge' , render: function ( name, type, row, meta ) {
                var model = getPropertyValue(row.properties, 'DEVICE_MODEL');
                var vendor = getPropertyValue(row.properties, 'VENDOR');
                var html = '<h4>Device ' + name + '</h4>';
                if (model) {
                    html += '<div>(' + vendor + '-' + model + ')</div>';
                }
                return html;
            }},
            { targets: 2, data: 'enrolmentInfo.owner', className: 'fade-edge remove-padding-top'},
            { targets: 3, data: 'enrolmentInfo.status', className: 'fade-edge remove-padding-top' ,
                render: function ( status, type, row, meta ) {
                var html;
                switch (status) {
                    case 'ACTIVE' :
                        html = '<span><i class="fw fw-ok icon-success"></i> Active</span>';
                        break;
                    case 'INACTIVE' :
                        html = '<span><i class="fw fw-warning icon-warning"></i> Inactive</span>';
                        break;
                    case 'BLOCKED' :
                        html = '<span><i class="fw fw-remove icon-danger"></i> Blocked</span>';
                        break;
                    case 'REMOVED' :
                        html = '<span><i class="fw fw-delete icon-danger"></i> Removed</span>';
                        break;
                }
                return html;
            }},
            { targets: 4, data: 'type' , className: 'fade-edge remove-padding-top' },
            { targets: 5, data: 'enrolmentInfo.ownership' , className: 'fade-edge remove-padding-top' },
            { targets: 6, data: 'enrolmentInfo.status' , className: 'text-right content-fill text-left-on-grid-view no-wrap' ,
                render: function ( status, type, row, meta ) {
                var deviceType = row.type;
                var deviceIdentifier = row.deviceIdentifier;
                var html = '<span></span>';
                return html;
            }}
        ],
        "createdRow": function( row, data, dataIndex ) {
            $(row).attr('data-type', 'selectable');
            $(row).attr('data-deviceid', data.deviceIdentifier);
            $(row).attr('data-devicetype', data.type);
            var model = getPropertyValue(data.properties, 'DEVICE_MODEL');
            var vendor = getPropertyValue(data.properties, 'VENDOR');
            var owner = data.enrolmentInfo.owner;
            var status = data.enrolmentInfo.status;
            var ownership = data.enrolmentInfo.ownership;
            var deviceType = data.type;
            $.each($('td', row), function (colIndex) {
                switch(colIndex) {
                    case 1:
                        $(this).attr('data-search', model + ',' + vendor);
                        $(this).attr('data-display', model);
                        break;
                    case 2:
                        $(this).attr('data-grid-label', "Owner");
                        $(this).attr('data-search', owner);
                        $(this).attr('data-display', owner);
                        break;
                    case 3:
                        $(this).attr('data-grid-label', "Status");
                        $(this).attr('data-search', status);
                        $(this).attr('data-display', status);
                        break;
                    case 4:
                        $(this).attr('data-grid-label', "Type");
                        $(this).attr('data-search', deviceType);
                        $(this).attr('data-display', deviceType);
                        break;
                    case 5:
                        $(this).attr('data-grid-label', "Ownership");
                        $(this).attr('data-search', ownership);
                        $(this).attr('data-display', ownership);
                        break;
                }
            });
        },
        "fnDrawCallback": function( oSettings ) {
            $(".icon .text").res_text(0.2);
        }
    });
    $(deviceCheckbox).click(function () {
        addDeviceSelectedClass(this);
    });
}

/*
 * Setting-up global variables.
 */
var deviceCheckbox = "#ast-container .ctrl-wr-asset .itm-select input[type='checkbox']";
var assetContainer = "#ast-container";

function openCollapsedNav(){
    $('.wr-hidden-nav-toggle-btn').addClass('active');
    $('#hiddenNav').slideToggle('slideDown', function(){
        if($(this).css('display') == 'none'){
            $('.wr-hidden-nav-toggle-btn').removeClass('active');
        }
    });
}

function initPage() {
    var currentUser = $("#device-listing").data("currentUser");
    var serviceURL;
    if ($.hasPermission("LIST_DEVICES")) {
        serviceURL = "/mdm-admin/devices";
    } else if ($.hasPermission("LIST_OWN_DEVICES")) {
        //Get authenticated users devices
        serviceURL = "/mdm-admin/users/devices?username=" + currentUser;
    }
    invokerUtil.get(
        serviceURL,
        function (data) {
            if (data) {
                data = JSON.parse(data);
                if (data.length > 0) {
                    loadDevices();
                } else {
                    $("#loading-content").remove();
                    $("#device-listing-status-msg").text("No enrolled devices found.");
                    $("#device-listing-status").removeClass(' hidden');
                }
            }
        }, function (message) {
            initPage();
        }
    );
}

/*
 * DOM ready functions.
 */
$(document).ready(function () {
    initPage();

    /* Adding selected class for selected devices */
    $(deviceCheckbox).each(function () {
        addDeviceSelectedClass(this);
    });

    var i;
    var permissionList = $("#permission").data("permission");
    for (i = 0; i < permissionList.length; i++) {
        $.setPermission(permissionList[i]);
    }

    /* for device list sorting drop down */
    $(".ctrl-filter-type-switcher").popover({
        html : true,
        content : function () {
            return $("#content-filter-types").html();
        }
    });

    $(".ast-container").on("click", ".claim-btn", function(e){
        e.stopPropagation();
        var deviceId = $(this).data("deviceid");
        var deviceListing = $("#device-listing");
        var currentUser = deviceListing.data("current-user");
        var serviceURL = "/temp-controller-agent/enrollment/claim?username=" + currentUser;
        var deviceIdentifier = {id: deviceId, type: "TemperatureController"};
        invokerUtil.put(serviceURL, deviceIdentifier, function(message){
            console.log(message);
        }, function(message){
                console.log(message.content);
            });
    });

    /* for data tables*/
    $('[data-toggle="tooltip"]').tooltip();

    $("[data-toggle=popover]").popover();

    $(".ctrl-filter-type-switcher").popover({
        html : true,
        content: function() {
            return $('#content-filter-types').html();
        }
    });

    $('#nav').affix({
        offset: {
            top: $('header').height()
        }
    });

});