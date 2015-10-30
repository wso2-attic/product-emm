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
                console.log(message);
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
    var deviceListingSrc = deviceListing.attr("src");
    var imageResource = deviceListing.data("image-resource");
    var currentUser = deviceListing.data("currentUser");
    $.template("device-listing", deviceListingSrc, function (template) {
        var serviceURL;
        if ($.hasPermission("LIST_DEVICES")) {
            serviceURL = "/mdm-admin/devices";
        } else if ($.hasPermission("LIST_OWN_DEVICES")) {
            //Get authenticated users devices
            serviceURL = "/mdm-admin/user/"+currentUser+"/carbon.super";
        } else {
            $("#ast-container").html("Permission denied");
            return;
        }
        if (searchParam){
            if(searchType == "users"){
                serviceURL = serviceURL + "?user=" + searchParam;
            }else if(searchType == "user-roles"){
                serviceURL = serviceURL + "?role=" + searchParam;
            }else{
                serviceURL = serviceURL + "?type=" + searchParam;
            }
        }
        var successCallback = function (data) {
            data = JSON.parse(data);
            var viewModel = {};
            viewModel.devices = data;
            viewModel.imageLocation = imageResource;
            if(data.length > 0){
                var content = template(viewModel);
                $("#ast-container").html(content);
                /*
                 * On device checkbox select add parent selected style class
                 */
                $(deviceCheckbox).click(function () {
                    addDeviceSelectedClass(this);
                });
            } else {
                $('#device-grid').addClass('hidden');
                $('#device-listing-status-msg').text('No device is available to be displayed.');

            }
            $('#device-grid').datatables_extended();
            $(".icon .text").res_text(0.2);


        };
        invokerUtil.get(serviceURL,
            successCallback, function(message){
                console.log(message);
            });
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



/*
 * DOM ready functions.
 */
$(document).ready(function () {
    loadDevices();
    //$('#device-grid').datatables_extended();

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
                console.log(message);
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