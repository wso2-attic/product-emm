function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var mdmProps = require('/config/mdm-props.js').config();
    context["permissions"] = userModule.getUIPermissions();

    if (userModule.isAuthorized("/permission/admin/device-mgt/users/delete")) {
        context["removePermitted"] = true;
    }

    if (userModule.isAuthorized("/permission/admin/device-mgt/users/update")) {
        context["editPermitted"] = true;
    }

    if (userModule.isAuthorized("/permission/admin/device-mgt/users/reset-password")) {
        context["resetPasswordPermitted"] = true;
    }

    context["adminUser"] = mdmProps.adminUser;
    return context;
}