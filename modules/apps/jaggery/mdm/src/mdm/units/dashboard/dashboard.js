function onRequest(context) {
    var userModule = require("/modules/user.js").userModule;
    var constants = require("/modules/constants.js");
    var permissions = userModule.getUIPermissions();
    context.permissions = permissions;
    return context;
}