function onRequest(context) {
    var userModule = require("/modules/user.js").userModule;
    context.permissions = userModule.getUIPermissions();
    return context;
}