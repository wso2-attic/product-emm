function onRequest(context) {
    var policyModule = require("/modules/policy.js")["policyModule"];
    var userModule = require("/modules/user.js")["userModule"];
    var response = policyModule.getAllPolicies();
    if (response["status"] == "success") {
        var policyListToView = response["content"];
        context["policyListToView"] = policyListToView;
        var policyCount = policyListToView.length;
        if (policyCount == 0) {
            context["policyListingStatusMsg"] = "No policy is available to be displayed.";
            context["saveNewPrioritiesButtonEnabled"] = false;
            context["noPolicy"] = true;
        } else if (policyCount == 1) {
            context["saveNewPrioritiesButtonEnabled"] = false;
            context["noPolicy"] = false;
            context["isUpdated"] = response["updated"] ;
        } else {
            context["saveNewPrioritiesButtonEnabled"] = true;
            context["noPolicy"] = false;
            context["isUpdated"] = response["updated"] ;
        }
    } else {
        // here, response["status"] == "error"
        context["policyListToView"] = [];
        context["policyListingStatusMsg"] = "An unexpected error occurred @ backend. Please try again later.";
        context["saveNewPrioritiesButtonEnabled"] = false;
        context["noPolicy"] = true;
    }

    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/policies/remove")) {
        context["removePermitted"] = true;
    }
    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/policies/update")) {
        context["editPermitted"] = true;
    }

    return context;
}
