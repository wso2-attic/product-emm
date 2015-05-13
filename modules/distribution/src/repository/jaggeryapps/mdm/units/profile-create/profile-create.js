function onRequest(context) {
    var policyModule = require("/modules/policy.js").policyModule;
    var policies = policyModule.getPolicies();
    context.policies = policies;
    context.deviceTypes = [{id: "1", name: "Android"},{id: "2", name: "iOS"},];
    return context;
}