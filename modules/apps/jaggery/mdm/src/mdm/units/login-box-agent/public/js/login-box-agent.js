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
 * This method will return query parameter value given its name
 * @param name Query parameter name
 * @returns {string} Query parameter value
 */
var getParameterByName = function (name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
};

var errorMsgWrapper = "#enrollment-error-msg";
var errorMsg = errorMsgWrapper + " span";

/**
 * This method will execute on login form submission and validate input.
 * @returns {boolean}
 */
var validate = function () {
    var username = $("input#username").val();
    var password = $("input#password").val();

    if (!username && !password) {
        $(errorMsg).text("Both username and password are empty. You cannot proceed.");
        if ($(errorMsgWrapper).hasClass("hidden")) {
            $(errorMsgWrapper).removeClass("hidden");
        }
        return false;
    } else if (!username && password) {
        $(errorMsg).text("Username should not be empty.");
        if ($(errorMsgWrapper).hasClass("hidden")) {
            $(errorMsgWrapper).removeClass("hidden");
        }
        return false;
    } else if (username && !password) {
        $(errorMsg).text("Password should not be empty.");
        if ($(errorMsgWrapper).hasClass("hidden")) {
            $(errorMsgWrapper).removeClass("hidden");
        }
        return false;
    } else {
        return true;
    }
};

$(document).ready(function () {
    var error = getParameterByName("error");
    if (error == "auth-failed") {
        var defaultMessage = "Please provide a correct username and password to continue.";
        var customMessage = getParameterByName("message");
        if (customMessage) {
            $(errorMsg).text("Authentication failed. " + customMessage);
        } else {
            $(errorMsg).text("Authentication failed. " + defaultMessage);
        }
        $(errorMsgWrapper).removeClass("hidden");
    } else if (error == "unexpected") {
        $(errorMsg).text("An unexpected error occured. Please try again.");
        $(errorMsgWrapper).removeClass("hidden");
    }
});

$(".btn-download-agent").click(function () {
    $(".form-login-box").submit();
});


