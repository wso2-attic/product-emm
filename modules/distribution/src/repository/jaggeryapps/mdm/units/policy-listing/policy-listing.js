function onRequest(context) {
    //var log = new Log("policy-listing.js");
    var policyModule = require("/modules/policy.js").policyModule;
    var policies = policyModule.getPolicies();
    context.saveNewPrioritiesButtonEnabled = false;
    var policyCount = policies.length;
    if (policyCount == 0) {
        context.policyCountStatusMsg = "No Policies to show currently.";
    } else if (policyCount == 1) {
        context.policyCountStatusMsg = "Add more policies to set-up a priority order.";
    } else {
        context.policyCountStatusMsg = "Drag & Move to re-order Policy Priority.";
        context.saveNewPrioritiesButtonEnabled = true;
    }
    context.policies = policies;
    return context;
}