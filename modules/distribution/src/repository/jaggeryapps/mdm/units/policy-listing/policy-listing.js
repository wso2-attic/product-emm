function onRequest(context) {
    var policyModule = require("/modules/policy.js").policyModule;
    var policies = [{id: "1", name: "Dev team policy"}, {id: "2", name: "Sales team policy"}];
    policies = policyModule.getPolicies();
    context.policies = policies;
    return context;
}