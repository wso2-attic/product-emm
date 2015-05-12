function onRequest(context){
    var userModule = require("/modules/user.js").userModule;
    var policyModule = require("/modules/policy.js").policyModule;
    var profiles = policyModule.getProfiles();
    var roles = userModule.getRoles(true);
    var users = userModule.getUsers();
    var actions = ["Enforce"];
    context.roles = roles;
    context.users = users;
    context.profiles = profiles;
    context.actions = actions;
    return context;
}