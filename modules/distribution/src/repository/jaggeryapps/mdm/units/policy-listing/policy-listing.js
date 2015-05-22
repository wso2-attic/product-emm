function onRequest(context) {
    // var log = new Log("policy-listing.js");
    var policyModule = require("/modules/policy.js").policyModule;
    context.policies = policyModule.getPolicies();

    return context;
}