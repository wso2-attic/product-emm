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

var updateStats = function (serviceURL, id) {
    invokerUtil.get(
        serviceURL,
        function (data, textStatus, jqXHR) {
            if (jqXHR.status == 200 && data) {
                var responsePayload = JSON.parse(data);
                var itemCount = responsePayload.count;
                if (itemCount) {
                    $(id).html(itemCount);
                } else {
                    $(id).html("Error...");
                }
            } else if (!data) {
                updateStats(serviceURL, id);
            }
        },

        function (jqXHR) {
            if (jqXHR.status == 404) {
                $(id).html(0);
                $(id + "-view-btn").hide();
            } else {
                $(id).html("Error...");
            }
        }
    );
};

$(document).ready(function () {
    if ($("#device-count").data("device-count")) {
        updateStats("/api/device-mgt/v1.0/devices?offset=0&limit=1", "#device-count");
    }
    if ($("#policy-count").data("policy-count")) {
        updateStats("/api/device-mgt/v1.0/policies?offset=0&limit=1", "#policy-count");
    }
    if ($("#user-count").data("user-count")) {
        updateStats("/api/device-mgt/v1.0/users?offset=0&limit=1", "#user-count");
    }
    if ($("#role-count").data("role-count")) {
        updateStats("/api/device-mgt/v1.0/roles?offset=0&limit=1", "#role-count");
    }
});

function toggleEnrollment() {
    $(".modalpopup-content").html($("#qr-code-modal").html());
    generateQRCode(".modalpopup-content .qr-code");
    showPopup();
}