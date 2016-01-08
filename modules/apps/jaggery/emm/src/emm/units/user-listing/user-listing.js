function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    context["permissions"] = userModule.getUIPermissions();
    if(userModule.isAuthorized("/permission/admin/device-mgt/users/delete")){
        context["removePermitted"] = true;
    }

    if(userModule.isAuthorized("/permission/admin/device-mgt/users/update")){
        context["editPermitted"] = true;
    }

    return context;
}