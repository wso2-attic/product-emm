function onRequest(context) {
    // var log = new Log("policy-listing.js");
    var policyModule = require("/modules/policy.js")["policyModule"];
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
        } else {
            context["saveNewPrioritiesButtonEnabled"] = true;
            context["noPolicy"] = false;
        }
    } else {
        // here, response["status"] == "error"
        context["policyListToView"] = [];
        context["policyListingStatusMsg"] = "An unexpected error occured @ backend. Please try again later.";
        context["saveNewPrioritiesButtonEnabled"] = false;
        context["noPolicy"] = true;
    }
    return context;
}
