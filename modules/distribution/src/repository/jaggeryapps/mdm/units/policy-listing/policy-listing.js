function onRequest(context) {
    //var log = new Log("policy-listing.js");
    var policyModule = require("/modules/policy.js").policyModule;
    //var policies = policyModule.getPolicies();
    context.policies = policyModule.getPolicies();
    context.policyCount = context.policies.length;
    return context;
}