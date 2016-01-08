function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    context["permissions"] = userModule.getUIPermissions();
    if(userModule.isAuthorized("/permission/admin/device-mgt/roles/delete")){
        context["removePermitted"] = true;
    }

    if(userModule.isAuthorized("/permission/admin/device-mgt/roles/update")){
        context["editPermitted"] = true;
    }
    return context;
}