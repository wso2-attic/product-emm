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
var policy = {};
var configuredFeatures = [];

stepperRegistry["policy-platform"] = function (actionButton) {
    policy["platform"] = $(actionButton).data("platform");
    policy["platformId"] = $(actionButton).data("platform-id");

    var deviceType = policy["platform"];
    var hiddenOperationBar = $("#hidden-operations-bar-" + deviceType);
    var hiddenOperationBarSrc = hiddenOperationBar.attr("src");

    $.template("hidden-operations-bar-" + deviceType, hiddenOperationBarSrc, function (template) {
        var serviceURL = "/mdm-admin/features/" + deviceType;
        invokerUtil.get(
            serviceURL,
            // function to run when request is successful.
            function (data) {
                var viewModel = {};
                // here we take data that come as an array and traverse each element as "current"
                // and update total, i.e. {}
                viewModel["features"] = data.reduce(function (total, current) {
                    total[current["code"]] = current;
                    return total;
                }, {});
                var content = template(viewModel);
                $(".wr-advance-operations").html(content);
            },
            // function to run when request fails.
            function (message) {
                console.log(message);
            }
        );
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

function savePolicy(policy) {
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
}

function showAdvanceOperation(operation, button) {
    $(button).addClass('selected');
    $(button).siblings().removeClass('selected');
    var hiddenOperation = ".wr-hidden-operations-content > div";
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
}

$(document).ready(function () {

    // Add initial state of wizard-steps.
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
        var operation = $(this).parents(".operation-data").data("operation");
        // prevents event bubbling by figuring out what element it's being called from.
        if (event.target.tagName == "INPUT") {
            if (!$(this).hasClass("collapsed")) {
                configuredFeatures.push(operation);
            } else {
                //splicing the array if operation is present.
                var index = $.inArray(operation, configuredFeatures);
                if (index != -1) {
                    configuredFeatures.splice(index, 1);
                }
            }
        }
    });

    $(".wizard-stepper").click(function () {
        // button clicked here can be either a continue button or a back button.
        var nextStep = $(this).data("next");
        var currentStep = $(this).data("current");
        var isBackBtn = $(this).data("is-back-btn");

        // if current button is a continuation...
        if (!isBackBtn) {
            // initiate stepperRegistry functions to gather form data.
            var action = stepperRegistry[currentStep];
            if (action) {
                action(this);
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
    });
});