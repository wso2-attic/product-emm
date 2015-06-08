function onRequest(context) {
    var userModule = require("/modules/user.js").userModule;

    context.roles = userModule.getRoles(true);
    context.users = userModule.getUsers();
    context.actions = ["Enforce"];

    return context;
}