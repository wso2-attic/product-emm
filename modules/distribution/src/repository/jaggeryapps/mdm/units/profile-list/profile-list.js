function onRequest(context){
    var policyModule = require("/modules/policy.js").policyModule;
    profiles = policyModule.getProfiles();
    context.profiles = profiles;
    return context;
}