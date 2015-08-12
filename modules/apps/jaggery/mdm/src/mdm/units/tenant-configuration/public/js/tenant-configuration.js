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
 * Checks if provided input is valid against RegEx input.
 *
 * @param regExp Regular expression
 * @param inputString Input string to check
 * @returns {boolean} Returns true if input matches RegEx
 */
function inputIsValid(regExp, inputString) {
    return regExp.test(inputString);
}

/**
 * Checks if an email address has the valid format or not.
 *
 * @param email Email address
 * @returns {boolean} true if email has the valid format, otherwise false.
 */
function emailIsValid(email) {
    var regExp = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    return regExp.test(email);
}

var notifierTypeConstants = {
    "LOCAL": "1",
    "GCM": "2"
};
// Constants to define platform types available
var platformTypeConstants = {
    "ANDROID": "android",
    "IOS": "ios"
};

var responseCodes = {
    "CREATED": "Created",
    "INTERNAL_SERVER_ERROR": "Internal Server Error"
};

var configParams = {
    "NOTIFIER_TYPE": "notifierType",
    "NOTIFIER_FREQUENCY": "notifierFrequency",
    "GCM_API_KEY": "gcmAPIKey",
    "GCM_SENDER_ID": "gcmSenderId",
    "CONFIG_EMAIL": "configEmail",
    "CONFIG_COUNTRY": "configCountry",
    "CONFIG_STATE": "configState",
    "CONFIG_LOCALITY": "configLocality",
    "CONFIG_ORGANIZATION": "configOrganization",
    "CONFIG_ORGANIZATION_UNIT": "configOrganizationUnit",
    "MDM_CERT_PASSWORD": "MDMCertPassword",
    "MDM_CERT_TOPIC_ID": "MDMCertTopicID",
    "APNS_CERT_PASSWORD": "APNSCertPassword"
};

$(document).ready(function () {
    $("#gcm-inputs").hide();
    var getConfigAPI = "/mdm-android-agent/configuration";

    /**
     * Following request would execute
     * on page load event of tenant configuration page in WSO2 EMM Console.
     * Upon receiving the response, the parameters will be set to the fields,
     * in case those configurations are already set.
     */
    invokerUtil.get(
        getConfigAPI,

        function (data) {

            if (data != null && data.configuration != null) {
                for (var i = 0; i < data.configuration.length; i++) {
                    var config = data.configuration[i];
                    if(config.name == configParams["NOTIFIER_TYPE"]){
                        $("#android-config-notifier").val(config.value);
                        if(config.value != notifierTypeConstants["GCM"] ) {
                            $("#gcm-inputs").hide();
                        }else{
                            $("#gcm-inputs").show();
                        }
                    } else if(config.name == configParams["NOTIFIER_FREQUENCY"]){
                        $("input#android-config-notifier-frequency").val(config.value);
                    } else if(config.name == configParams["GCM_API_KEY"]){
                        $("input#android-config-gcm-api-key").val(config.value);
                    } else if(config.name == configParams["GCM_SENDER_ID"]){
                        $("input#android-config-gcm-sender-id").val(config.value);
                    }
                }
            }

        }, function () {

        }
    );

    $("select.select2[multiple=multiple]").select2({
        tags : true
    });

    $("#android-config-notifier").change(function() {
        var notifierType = $("#android-config-notifier").find("option:selected").attr("value");
        if(notifierType  != notifierTypeConstants["GCM"] ) {
            $("#gcm-inputs").hide();
        }else{
            $("#gcm-inputs").show();
        }
    });
    /**
     * Following click function would execute
     * when a user clicks on "Save" button
     * on Android tenant configuration page in WSO2 EMM Console.
     */
    $("button#save-android-btn").click(function() {
        var notifierType = $("#android-config-notifier").find("option:selected").attr("value");
        var notifierFrequency = $("input#android-config-notifier-frequency").val();
        var gcmAPIKey = $("input#android-config-gcm-api-key").val();
        var gcmSenderId = $("input#android-config-gcm-sender-id").val();

        var errorMsgWrapper = "#android-config-error-msg";
        var errorMsg = "#android-config-error-msg span";
        if (!notifierFrequency) {
            $(errorMsg).text("Notifier frequency is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!$.isNumeric(notifierFrequency)) {
            $(errorMsg).text("Provided notifier frequency is invalid. Please check.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (notifierType == notifierTypeConstants["GCM"] && !gcmAPIKey) {
            $(errorMsg).text("GCM API Key is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (notifierType == notifierTypeConstants["GCM"] && !gcmSenderId) {
            $(errorMsg).text("GCM Sender ID is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else {

            var addConfigFormData = {};
            var configList = new Array();

            var type = {
                "name": configParams["NOTIFIER_TYPE"],
                "value": notifierType,
                "contentType": "text"
            };

            var frequency = {
                "name": configParams["NOTIFIER_FREQUENCY"],
                "value": notifierFrequency,
                "contentType": "text"
            };

            var gcmKey = {
                "name": configParams["GCM_API_KEY"],
                "value": gcmAPIKey,
                "contentType": "text"
            };
            var gcmId = {
                "name": configParams["GCM_SENDER_ID"],
                "value": gcmSenderId,
                "contentType": "text"
            };

            configList.push(type);
            configList.push(frequency);
            if (notifierType == notifierTypeConstants["GCM"]) {
                configList.push(gcmKey);
                configList.push(gcmId);
            }

            addConfigFormData.type = platformTypeConstants["ANDROID"];
            addConfigFormData.configuration = configList;

            var addConfigAPI = "/mdm-android-agent/configuration";

            invokerUtil.post(
                addConfigAPI,
                addConfigFormData,
                function (data) {
                    if (data.responseCode == responseCodes["CREATED"]) {
                        $("#config-save-form").addClass("hidden");
                        $("#record-created-msg").removeClass("hidden");
                    } else if (data == 500) {
                        $(errorMsg).text("Exception occurred at backend.");
                    } else if (data == 403) {
                        $(errorMsg).text("Action was not permitted.");
                    }

                    $(errorMsgWrapper).removeClass("hidden");
                }, function () {
                    $(errorMsg).text("An unexpected error occurred.");
                    $(errorMsgWrapper).removeClass("hidden");
                }
            );
        }
    });

    $("button#save-ios-btn").click(function() {

        var errorMsgWrapper = "#ios-config-error-msg";
        var errorMsg = "#ios-config-error-msg span";

        var configEmail = $("#ios-config-email").val();
        var configCountry = $("#ios-config-country").val();
        var configState = $("#ios-config-state").val();
        var configLocality = $("#ios-config-locality").val();
        var configOrganization = $("#ios-config-organization").val();
        var configOrganizationUnit = $("#ios-config-organization-unit").val();
        var MDMCertPassword = $("#ios-config-mdm-certificate-password").val();
        var MDMCertTopicID = $("#ios-config-mdm-certificate-topic-id").val();
        var APNSCertPassword = $("#ios-config-apns-certificate-password").val();

        if (!configEmail) {
            $(errorMsg).text("SCEP email is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!configCountry) {
            $(errorMsg).text("SCEP country is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!configState) {
            $(errorMsg).text("SCEP state is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!configLocality) {
            $(errorMsg).text("SCEP locality is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!configOrganization) {
            $(errorMsg).text("SCEP organization is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!configOrganizationUnit) {
            $(errorMsg).text("SCEP organization unit is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!MDMCertPassword) {
            $(errorMsg).text("MDM certificate password is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!MDMCertTopicID) {
            $(errorMsg).text("MDM certificate topic ID is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!APNSCertPassword) {
            $(errorMsg).text("APNS certificate password is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        }

        var addConfigFormData = {};
        var configList = new Array();

        var configEmail = {
            "name": configParams["CONFIG_EMAIL"],
            "value": configEmail,
            "contentType": "text"
        };

        var configCountry = {
            "name": configParams["CONFIG_COUNTRY"],
            "value": configCountry,
            "contentType": "text"
        };

        var configState = {
            "name": configParams["CONFIG_STATE"],
            "value": configState,
            "contentType": "text"
        };

        var configLocality = {
            "name": configParams["CONFIG_LOCALITY"],
            "value": configLocality,
            "contentType": "text"
        };

        var configOrganization = {
            "name": configParams["CONFIG_ORGANIZATION"],
            "value": configOrganization,
            "contentType": "text"
        };

        var configOrganizationUnit = {
            "name": configParams["CONFIG_ORGANIZATION_UNIT"],
            "value": configOrganizationUnit,
            "contentType": "text"
        };

        var MDMCertPassword = {
            "name": configParams["MDM_CERT_PASSWORD"],
            "value": MDMCertPassword,
            "contentType": "text"
        };

        var MDMCertTopicID = {
            "name": configParams["MDM_CERT_TOPIC_ID"],
            "value": MDMCertTopicID,
            "contentType": "text"
        };

        var APNSCertPassword = {
            "name": configParams["APNS_CERT_PASSWORD"],
            "value": APNSCertPassword,
            "contentType": "text"
        };

        configList.push(configEmail);
        configList.push(configCountry);
        configList.push(configState);
        configList.push(configLocality);
        configList.push(configOrganization);
        configList.push(configOrganizationUnit);
        configList.push(MDMCertPassword);
        configList.push(MDMCertTopicID);
        configList.push(APNSCertPassword);

        addConfigFormData.type = platformTypeConstants["IOS"];
        addConfigFormData.configuration = configList;

        var addConfigAPI = "/ios/configuration";

        invokerUtil.post(
            addConfigAPI,
            addConfigFormData,
            function (data) {
                if (data.responseCode == responseCodes["CREATED"]) {
                    $("#config-save-form").addClass("hidden");
                    $("#record-created-msg").removeClass("hidden");
                } else if (data == 500) {
                    $(errorMsg).text("Exception occurred at backend.");
                } else if (data == 400) {
                    $(errorMsg).text("Configurations cannot be empty.");
                }

                $(errorMsgWrapper).removeClass("hidden");
            }, function () {
                $(errorMsg).text("An unexpected error occurred.");
                $(errorMsgWrapper).removeClass("hidden");
            }
        );

    });
});

// Start of HTML embedded invoke methods
var showAdvanceOperation = function (operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    var hiddenOperation = ".wr-hidden-operations-content > div";
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
};
