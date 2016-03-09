function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var mdmProps = require('/config/mdm-props.js').config();
    context["permissions"] = userModule.getUIPermissions();
    if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/roles/remove")){
        context["removePermitted"] = true;
    }
    if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/roles/update")){
        context["editPermitted"] = true;
    }
    var mdmProps = require("/config/mdm-props.js").config();
    context["appContext"] = mdmProps.appContext;
    context["adminRole"] = mdmProps.adminRole;
    return context;
}