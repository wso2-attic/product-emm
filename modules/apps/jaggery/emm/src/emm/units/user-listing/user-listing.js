function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var mdmProps = require('/config/mdm-props.js').config();
    context["permissions"] = userModule.getUIPermissions();

    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/users/remove")) {
        context["removePermitted"] = true;
    }
    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/users/update")) {
        context["editPermitted"] = true;
    }
    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/users/view")) {
        context["viewPermitted"] = true;
    }
    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/users/invite")) {
        context["invitePermitted"] = true;
    }
    if (userModule.isAuthorized("/permission/admin/device-mgt/users/reset-password")) {
        context["resetPasswordPermitted"] = true;
    }
    context["adminUser"] = mdmProps.adminUser;
    return context;
}