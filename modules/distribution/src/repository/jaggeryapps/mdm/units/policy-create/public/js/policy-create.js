var stepperRegistry = {};
var hiddenOperation = ".wr-hidden-operations-content > div";
var advanceOperation = ".wr-advance-operations";

var policy = {};
var configuredFeatures = [];

stepperRegistry["policy-platform"] = function (actionButton) {
    policy.platform = $(actionButton).data("platform");
    policy.platformId = $(actionButton).data("platform-id");

    var deviceType = policy.platform;
    var hiddenOperationBar = $("#hidden-operations-bar-" + deviceType);
    var hiddenOperationBarSrc = hiddenOperationBar.attr("src");

    $.template("hidden-operations-bar-" + deviceType, hiddenOperationBarSrc, function (template) {
        var serviceURL = "/mdm-admin/features/" + deviceType;
        var successCallback = function (data) {
            var viewModel = {};
            viewModel.features = data.reduce(function (total, current) {
                total[current.code] = current;
                return total;
            }, {});
            var content = template(viewModel);
            $(".wr-advance-operations").html(content);
        };
        invokerUtil.get(
            serviceURL,
            successCallback,
            function (message) {
                console.log(message);
            }
        );
    });
};

stepperRegistry["policy-profile"] = function () {
    policy.profile = operationModule.generateProfile(policy.platform, configuredFeatures);
};

stepperRegistry["policy-criteria"] = function () {
    $("input[type='radio'].select-users-radio").each(function () {
        if ( $(this).is(':radio')) {
            if ($(this).is(":checked")) {
                if($(this).attr("id") == "users-radio-btn") {
                    policy.selectedUsers = $("#users-input").val();
                } else if ($(this).attr("id") == "user-roles-radio-btn") {
                    policy.selectedUserRoles = $("#user-roles-input").val();
                }
            }
        }
    });
    policy.selectedNonCompliancyAction = $("#action-input").find(":selected").data("action");
    policy.selectedOwnership = $("#ownership-input").val();
};

stepperRegistry["policy-naming"] = function () {
    policy.policyName = $("#policy-name-input").val();
    policy.policyDescription = $("#policy-description-input").val();
    //All data is collected. Policy can now be created.
    savePolicy(policy);
};

function savePolicy(policy) {
    var profilePayloads = [];
    for (var key in policy.profile) {
        if (policy.profile.hasOwnProperty(key)) {
            profilePayloads.push({
                featureCode : key,
                deviceTypeId : policy.platformId,
                content : policy.profile[key]
            });
        }
    }
    var payload = {
        policyName : policy.policyName,
        compliance : policy.selectedNonCompliancyAction,
        ownershipType : policy.selectedOwnership,
        profile : {
            profileName : policy.policyName,
            deviceType : {
                id : policy.platformId
            },
            profileFeaturesList : profilePayloads
        }
    };
    payload.users = [];
    payload.roles = [];
    if (policy.selectedUsers) {
        payload.users = policy.selectedUsers;
    } else if (policy.selectedUserRoles) {
        payload.roles = policy.selectedUserRoles;
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
    $(hiddenOperation + '[data-operation="' + operation + '"]').show();
    $(hiddenOperation + '[data-operation="' + operation + '"]').siblings().hide();
}

$(document).ready(function () {

    $("select.select2[multiple=multiple]").select2({
        tags : true
    });

    $("#users-input, #user-roles-input").select2({
        tags : true
    }).on("select2:select", function (e) {
        if (e.params.data.id == "ANY") {
            $(this).val("ANY").trigger("change");
        } else {
            $("option[value=ANY]", this).prop("selected", false).parent().trigger("change");
        }
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

    //Adds an event listener to switch
    $(advanceOperation).on("click", ".wr-input-control.switch", function (event) {
        var operation = $(this).parents(".operation-data").data("operation");
        //prevents event bubbling by figuring out what element it's being called from
        if (event.target.tagName == "INPUT") {
            if (!$(this).hasClass("collapsed")) {
                configuredFeatures.push(operation);
            } else {
                //splicing the array if operation is present
                var index = $.inArray(operation, configuredFeatures);
                if (index != -1) {
                    configuredFeatures.splice(index, 1);
                }
            }
        }
    });

    $(".wizard-stepper").click(function () {
        var nextStep = $(this).data("next");
        var currentStep = $(this).data("current");
        var isBackButton = $(this).data("back");
        if (!isBackButton) {
            var action = stepperRegistry[currentStep];
            if (action) {
                action(this);
            }
        }
        if (!nextStep) {
            window.location.href = $(this).data("direct");
        }
        $(".itm-wiz").each(function () {
            var step = $(this).data("step");
            if (step == nextStep){
                $(this).addClass("itm-wiz-current");
            }else{
                $(this).removeClass("itm-wiz-current");
            }
        });
        $(".wr-wizard").html($(".wr-steps").html());
        $("." + nextStep).removeClass("hidden");
        $("." + currentStep).addClass("hidden");
    });
});