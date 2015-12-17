function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    context["permissions"] = userModule.getUIPermissions();
    if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/users/remove")){
        context["removePermitted"] = true;
    }
    return context;
}