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

var responseCodes = {
    "CREATED": "Created",
    "ACCEPTED": "202",
    "INTERNAL_SERVER_ERROR": "Internal Server Error"
};

$(document).ready(function () {
    var permissionSet = {};
    $.setPermission = function (permission) {
        permissionSet[permission] = true;
    };

    $.hasPermission = function (permission) {
        return permissionSet[permission];
    };

    loadNotifications();

    $("#ast-container").on("click", ".new-notification", function(e){
        var notificationId = $(this).data("id");
        var redirectUrl = $(this).data("url");
        var getNotificationsAPI = "/mdm-admin/notifications/"+notificationId+"/CHECKED";
        var errorMsgWrapper = "#error-msg";
        var errorMsg = "#error-msg span";
        invokerUtil.put(
            getNotificationsAPI,
            null,
            function (data) {
                data = JSON.parse(data);
                if (data.statusCode == responseCodes["ACCEPTED"]) {
                    $("#config-save-form").addClass("hidden");
                    location.href = redirectUrl;
                } else if (data == 500) {
                    $(errorMsg).text("Exception occurred at backend.");
                } else if (data == 403) {
                    $(errorMsg).text("Action was not permitted.");
                } else {
                    $(errorMsg).text("An unexpected error occurred.");
                }

                $(errorMsgWrapper).removeClass("hidden");
            }, function () {
                $(errorMsg).text("An unexpected error occurred.");
                $(errorMsgWrapper).removeClass("hidden");
            }
        );
    });

});

function loadNotifications(){
    var deviceListing = $("#notification-listing");
    var deviceListingSrc = deviceListing.attr("src");
    var currentUser = deviceListing.data("currentUser");
    $.template("notification-listing", deviceListingSrc, function (template) {
        var serviceURL = "/mdm-admin/notifications";
        var successCallback = function (data) {
            var viewModel = {};
            data = JSON.parse(data);
            viewModel.notifications = data;
            if(data.length > 0){
                var content = template(viewModel);
                $("#ast-container").html(content);
                $('#unread-notifications').datatables_extended();
                $('#all-notifications').datatables_extended();
            }

        };
        invokerUtil.get(serviceURL,
            successCallback, function(message){
                console.log(message);
        });
    });
}


// Start of HTML embedded invoke methods
var showAdvanceOperation = function (operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    var hiddenOperation = ".wr-hidden-operations-content > div";
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
};
