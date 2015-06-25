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

var stepperRegistry = {};
var stepsCompleted = 0;
var stepperRegistryValidation = {};
var policy = {};
var configuredFeatures = [];

stepperRegistry["policy-platform"] = function (actionButton) {
    policy["platform"] = $(actionButton).data("platform");
    policy["platformId"] = $(actionButton).data("platform-id");

    var deviceType = policy["platform"];
    var hiddenOperationsByDeviceType = $("#hidden-operations-" + deviceType);
    var hiddenOperationsByDeviceTypeCacheKey = deviceType + "HiddenOperations";
    var hiddenOperationsByDeviceTypeSrc = hiddenOperationsByDeviceType.attr("src");

    $.template(hiddenOperationsByDeviceTypeCacheKey, hiddenOperationsByDeviceTypeSrc, function (template) {
        var content = template();
        $(".wr-advance-operations").html(content);
    });
};

stepperRegistry["policy-profile"] = function () {
    policy["profile"] = operationModule.generateProfile(policy["platform"], configuredFeatures);
};

stepperRegistry["policy-criteria"] = function () {
    $("input[type='radio'].select-users-radio").each(function () {
        if ( $(this).is(':radio')) {
            if ($(this).is(":checked")) {
                if($(this).attr("id") == "users-radio-btn") {
                    policy["selectedUsers"] = $("#users-input").val();
                } else if ($(this).attr("id") == "user-roles-radio-btn") {
                    policy["selectedUserRoles"] = $("#user-roles-input").val();
                }
            }
        }
    });
    policy["selectedNonCompliantAction"] = $("#action-input").find(":selected").data("action");
    policy["selectedOwnership"] = $("#ownership-input").val();
};

stepperRegistry["policy-naming"] = function () {
    policy["policyName"] = $("#policy-name-input").val();
    policy["policyDescription"] = $("#policy-description-input").val();
    //All data is collected. Policy can now be created.
    savePolicy(policy);
};

stepperRegistryValidation["policy-naming"] = function () {
    var validationStatus = {};

    // taking values of inputs to be validated
    var policyName = $("input#policy-name-input").val();
    // starting validation process and updating validationStatus
    if (!policyName) {
        validationStatus["error"] = true;
        validationStatus["mainErrorMsg"] = "Policy name is empty. You cannot proceed.";
    } else if (!inputIsValid(/^[^*^`]{1,30}$/, policyName)) {
        validationStatus["error"] = true;
        validationStatus["mainErrorMsg"] = "Policy name either contains invalid characters or exceeds maximum allowed length. Please check again";
    } else {
        validationStatus["error"] = false;
    }
    // ending validation process

    // start taking specific actions upon validation
    var stepperIsToBeContinued;
    if (validationStatus["error"]) {
        stepperIsToBeContinued = false;
        var mainErrorMsgWrapper = "#policy-naming-main-error-msg";
        var mainErrorMsg = mainErrorMsgWrapper + " span";
        $(mainErrorMsg).text(validationStatus["mainErrorMsg"]);
        $(mainErrorMsgWrapper).removeClass("hidden");
    } else {
        stepperIsToBeContinued = true;
    }

    return stepperIsToBeContinued;
};

/**
 * Checks if provided input is valid against RegEx input.
 *
 * @param regEx Regular expression
 * @param inputString Input string to check
 * @returns {boolean} Returns true if input matches RegEx
 */
var inputIsValid = function (regEx, inputString) {
    return regEx.test(inputString);
};

stepperRegistryValidation["policy-profile"] = function () {
    var validationStatusArray = [];
    var validationStatus;
    // starting validation process and updating validationStatus
    if (policy.platform == "android") {
        if (configuredFeatures.length == 0) {
            validationStatus = {
                "error": true,
                "mainErrorMsg": "You cannot continue. Zero configured features."
            };
            validationStatusArray.push(validationStatus);
        } else {
            if ($.inArray("PASSCODE_POLICY", configuredFeatures) != -1) {
                // if PASSCODE_POLICY is configured
                validationStatus = {
                    "error": false,
                    "okFeature": "passcode-policy"
                };
                validationStatusArray.push(validationStatus);
            }
            if ($.inArray("CAMERA", configuredFeatures) != -1) {
                // if CAMERA is configured
                validationStatus = {
                    "error": false,
                    "okFeature": "restrictions"
                };
                validationStatusArray.push(validationStatus);
            }
            if ($.inArray("ENCRYPT_STORAGE", configuredFeatures) != -1) {
                // if ENCRYPT_STORAGE is configured
                validationStatus = {
                    "error": false,
                    "okFeature": "encrypt-storage"
                };
                validationStatusArray.push(validationStatus);
            }
            if ($.inArray("WIFI", configuredFeatures) != -1) {
                // if WIFI is configured
                var ssid = $("input#ssid").val();
                if (!ssid) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "WIFI SSID is not given. You cannot proceed.",
                        "erroneousFeature": "wifi"
                    };
                    validationStatusArray.push(validationStatus);
                } else if (!inputIsValid(/^[^*^`]{1,30}$/, ssid)) {
                    validationStatus = {
                        "error": true,
                        "subErrorMsg": "WIFI SSID either contains invalid characters or exceeds maximum allowed length. Please check again.",
                        "erroneousFeature": "wifi"
                    };
                    validationStatusArray.push(validationStatus);
                } else {
                    validationStatus = {
                        "error": false,
                        "okFeature": "wifi"
                    };
                    validationStatusArray.push(validationStatus);
                }
            }
        }
    } else if (policy.platform == "ios") {
        validationStatus = {
            "error": false
        };
        validationStatusArray.push(validationStatus);
    }
    // ending validation process

    // start taking specific actions upon validation
    var stepperIsToBeContinued;
    var errorCount = 0;
    var mainErrorMsgWrapper, mainErrorMsg, subErrorMsgWrapper, subErrorMsg, subErrorIcon, subOkIcon;
    var i;
    for (i = 0; i < validationStatusArray.length; i++) {
        validationStatus = validationStatusArray[i];
        if (validationStatus["error"]) {
            errorCount++;
            if (validationStatus["mainErrorMsg"]) {
                mainErrorMsgWrapper = "#policy-profile-main-error-msg";
                mainErrorMsg = mainErrorMsgWrapper + " span";
                $(mainErrorMsg).text(validationStatus["mainErrorMsg"]);
                $(mainErrorMsgWrapper).removeClass("hidden");
            } else if (validationStatus["subErrorMsg"]) {
                subErrorMsgWrapper = "#" + validationStatus["erroneousFeature"] + "-feature-error-msg";
                subErrorMsg = subErrorMsgWrapper + " span";
                subErrorIcon = "#" + validationStatus["erroneousFeature"] + "-error";
                subOkIcon = "#" + validationStatus["erroneousFeature"] + "-ok";
                $(subErrorMsg).text(validationStatus["subErrorMsg"]);
                $(subErrorMsgWrapper).removeClass("hidden");
                if (!$(subOkIcon).hasClass("hidden")) {
                    $(subOkIcon).addClass("hidden");
                }
                $(subErrorIcon).removeClass("hidden");
            }
        } else {
            if (validationStatus["okFeature"]) {
                subErrorMsgWrapper = "#" + validationStatus["okFeature"] + "-feature-error-msg";
                subOkIcon = "#" + validationStatus["okFeature"] + "-ok";
                subErrorIcon = "#" + validationStatus["okFeature"] + "-error";
                if (!$(subErrorMsgWrapper).hasClass("hidden")) {
                    $(subErrorMsgWrapper).addClass("hidden");
                }
                if (!$(subErrorIcon).hasClass("hidden")) {
                    $(subErrorIcon).addClass("hidden");
                }
                $(subOkIcon).removeClass("hidden");
            }
        }
    }

    stepperIsToBeContinued = (errorCount == 0);
    return stepperIsToBeContinued;
};

var savePolicy = function (policy) {
    var profilePayloads = [];
    // traverses key by key in policy["profile"]
    var key;
    for (key in policy["profile"]) {
        if (policy["profile"].hasOwnProperty(key)) {
            profilePayloads.push({
                "featureCode": key,
                "deviceTypeId": policy["platformId"],
                "content": policy["profile"][key]
            });
        }
    }
    var payload = {
        "policyName": policy["policyName"],
        "compliance": policy["selectedNonCompliantAction"],
        "ownershipType": policy["selectedOwnership"],
        "profile": {
            "profileName": policy["policyName"],
            "deviceType": {
                "id": policy["platformId"]
            },
            "profileFeaturesList": profilePayloads
        }
    };

    if (policy["selectedUsers"]) {
        payload["users"] = policy["selectedUsers"];
    } else if (policy["selectedUserRoles"]) {
        payload["roles"] = policy["selectedUserRoles"];
    } else {
        payload["users"] = [];
        payload["roles"] = [];
    }

    console.log(JSON.stringify(payload));

    invokerUtil.post(
        "/mdm-admin/policies",
        payload,
        function () {
            $(".policy-message").removeClass("hidden");
            $(".add-policy").addClass("hidden");
        },
        function () {

        }
    );
};

var showAdvanceOperation = function (operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    var hiddenOperation = ".wr-hidden-operations-content > div";
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
};

$(document).ready(function () {

    // Adding initial state of wizard-steps.
    $("#policy-platform-wizard-steps").html($(".wr-steps").html());

    $("select.select2[multiple=multiple]").select2({
        "tags": true
    });

    $("#users-select-field").hide();
    $("#user-roles-select-field").show();

    $("input[type='radio'].select-users-radio").change(function () {
        if ($("#users-radio-btn").is(":checked")) {
            $("#user-roles-select-field").hide();
            $("#users-select-field").show();
        }
        if ($("#user-roles-radio-btn").is(":checked")) {
            $("#users-select-field").hide();
            $("#user-roles-select-field").show();
        }
    });

    // Support for special input type "ANY" on user(s) & user-role(s) selection
    $("#users-input, #user-roles-input").select2({
        "tags": true
    }).on("select2:select", function (e) {
        if (e.params.data.id == "ANY") {
            $(this).val("ANY").trigger("change");
        } else {
            $("option[value=ANY]", this).prop("selected", false).parent().trigger("change");
        }
    });

    // Maintains an array of configured features of the profile
    $(".wr-advance-operations").on("click", ".wr-input-control.switch", function (event) {
        var operationCode = $(this).parents(".operation-data").data("operation-code");
        var operation = $(this).parents(".operation-data").data("operation");
        var operationDataWrapper = $(this).data("target");
        // prevents event bubbling by figuring out what element it's being called from.
        if (event.target.tagName == "INPUT") {
            var zeroConfiguredFeaturesErrorMsg = "#policy-profile-main-error-msg";
            if (!$(this).hasClass("collapsed")) {
                configuredFeatures.push(operationCode);
                // when a feature is enabled, if "zero-configured-features" msg is available, hide that.
                if (!$(zeroConfiguredFeaturesErrorMsg).hasClass("hidden")) {
                    $(zeroConfiguredFeaturesErrorMsg).addClass("hidden");
                }
            } else {
                //splicing the array if operation is present.
                var index = $.inArray(operationCode, configuredFeatures);
                if (index != -1) {
                    configuredFeatures.splice(index, 1);
                }
                // when a feature is disabled, clearing all its current error or success states
                var subErrorMsgWrapper = "#" + operation + "-feature-error-msg";
                var subErrorIcon = "#" + operation + "-error";
                var subOkIcon = "#" + operation + "-ok";
                if (!$(subErrorMsgWrapper).hasClass("hidden")) {
                    $(subErrorMsgWrapper).addClass("hidden");
                }
                if (!$(subErrorIcon).hasClass("hidden")) {
                    $(subErrorIcon).addClass("hidden");
                }
                if (!$(subOkIcon).hasClass("hidden")) {
                    $(subOkIcon).addClass("hidden");
                }
                // clearing input fields
                $(operationDataWrapper + " input").each(
                    function () {
                        if ($(this).is("input:text") || $(this).is("input:password")) {
                            $(this).val("");
                        } else if ($(this).is("input:checkbox")) {
                            $(this).prop("checked", "checked");
                        }
                    }
                );
                // clearing select fields
                $(operationDataWrapper + " select").each(
                    function () {
                        $("option:first", this).prop("selected", "selected");
                    }
                );
            }
        }
    });

    $(".wizard-stepper").click(function () {
        // button clicked here can be either a continue button or a back button.
        var currentStep = $(this).data("current");
        var validationIsRequired = $(this).data("validate");
        var stepperIsToBeContinued;

        if (validationIsRequired) {
            stepperIsToBeContinued = stepperRegistryValidation[currentStep]();
        } else {
            stepperIsToBeContinued = true;
        }

        if (stepperIsToBeContinued) {
            // When moving back and forth, following code segment will
            // remove if there are any visible error-messages and error-icons.
            var errorMsgWrappers = ".alert.alert-danger";
            $(errorMsgWrappers).each(
                function () {
                    if (!$(this).hasClass("hidden")) {
                        $(this).addClass("hidden");
                    }
                }
            );

            var nextStep = $(this).data("next");
            var isBackBtn = $(this).data("is-back-btn");

            // if current button is a continuation...
            if (!isBackBtn) {
                // initiate stepperRegistry functions to gather form data.
                var action = stepperRegistry[currentStep];
                if (action) {
                    action(this);
                }
                stepsCompleted++;
                if (stepsCompleted > 0 && stepsCompleted < 4) {
                    $("#" + nextStep + "-page-wizard-title").text("ADD " + policy.platform + " POLICY");
                }
            } else {
                stepsCompleted--;
                // if user traverses back to a platform select
                if (stepsCompleted == 0) {
                    // reinitialize configuredFeatures
                    configuredFeatures = [];
                    // clearing already-loaded platform specific hidden-operations html content from the relevant div
                    // so that, the wrong content would not be shown at the first glance in case
                    // the user selects a different platform
                    $(".wr-advance-operations").html(
                        "<div class='wr-advance-operations-init'>" +
                            "<br>" +
                            "&nbsp;&nbsp;&nbsp;&nbsp;" +
                            "<i class='fw fw-settings fw-spin fw-2x'></i>" +
                            "&nbsp;&nbsp;" +
                            "Loading Platform Features..." +
                            "<br>" +
                            "<br>" +
                        "</div>"
                    );
                }
            }

            // following step occurs only at the last stage of the wizard.
            if (!nextStep) {
                window.location.href = $(this).data("direct");
            }

            // updating next wizard step as current.
            $(".itm-wiz").each(function () {
                var step = $(this).data("step");
                if (step == nextStep) {
                    $(this).addClass("itm-wiz-current");
                } else {
                    $(this).removeClass("itm-wiz-current");
                }
            });

            // adding next update of wizard-steps.
            $("#" + nextStep + "-wizard-steps").html($(".wr-steps").html());

            // hiding current section of the wizard and showing next section.
            $("." + currentStep).addClass("hidden");
            $("." + nextStep).removeClass("hidden");
        }
    });
});