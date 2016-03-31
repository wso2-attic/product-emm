function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var mdmProps = require('/config/mdm-props.js').config();
    context["permissions"] = userModule.getUIPermissions();

    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/certificate/Get")) {
        context["removePermitted"] = true;
    }
    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/certificate/Get")) {
        context["viewPermitted"] = true;
    }
    context["adminUser"] = mdmProps.adminUser;
    return context;
}