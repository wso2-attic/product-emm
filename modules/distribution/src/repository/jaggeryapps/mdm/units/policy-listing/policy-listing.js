function onRequest(context) {
    var policyModule = require("/modules/policy.js").policyModule;
    var policies = policyModule.getPolicies();
    context.policies = policies;
    return context;
}