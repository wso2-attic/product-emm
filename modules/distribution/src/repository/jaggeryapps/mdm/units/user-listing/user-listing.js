function onRequest(context){
    var userModule = require("/modules/user.js").userModule;
    context.users = userModule.getUsers();
    context.permissions = userModule.getUIPermissions();
    return context;
}