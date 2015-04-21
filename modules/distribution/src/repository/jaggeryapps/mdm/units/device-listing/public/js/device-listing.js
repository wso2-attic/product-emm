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
    $.template = function (name, location, callback) {
        var template = cache[name];
        if (!template) {
            $.get(location, function (data) {
                var compiledTemplate = Handlebars.compile(data);
                cache[name] = compiledTemplate;
                callback(compiledTemplate);
            });
        } else {
            callback(template);
        }
    };

    Handlebars.registerHelper("deviceMap", function (device) {
        var arr = device.properties;
        device.properties = arr.reduce(function (total, current) {
            total[current.name] = current.value;
            return total;
        }, {});
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
});

/*
 * On Select All Device button click function.
 *
 * @param button: Select All Device button
 */
function selectAllDevices(button) {
    if (!$(button).data("select")) {
        $(deviceCheckbox).each(function () {
            addDeviceSelectedClass(this);
            $(this).prop("checked", true);
        });
        $(button).data("select", true);
        $(button).html("Deselect All Devices");
    } else {
        $(deviceCheckbox).each(function () {
            addDeviceSelectedClass(this);
            $(this).prop("checked", false);
        });
        $(button).data("select", false);
        $(button).html("Select All Devices");
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

$(document).ready(function () {
    var deviceListing = $("#device-listing");
    var deviceListingSrc = deviceListing.attr("src");
    var imageResource = deviceListing.data("image-resource");
    $.template("device-listing", deviceListingSrc, function (template) {
        var serviceURL;
        if ($.hasPermission("LIST_DEVICES")) {
            serviceURL = "/mdm/api/devices";
        } else if ($.hasPermission("LIST_OWN_DEVICES")) {
            //Get authenticated users devices
            serviceURL = "/mdm/api/user/devices";
        } else {
            $("#ast-container").html("Permission denied");
            return;
        }
        $.get(serviceURL, function (data) {
            var viewModel = {};
            viewModel.devices = JSON.parse(data);
            viewModel.imageLocation = imageResource;
            var content = template(viewModel);
            $("#ast-container").html(content);
            /*
             * On device checkbox select add parent selected style class
             */
            $(deviceCheckbox).click(function () {
                addDeviceSelectedClass(this);
            });
        });
    });
});