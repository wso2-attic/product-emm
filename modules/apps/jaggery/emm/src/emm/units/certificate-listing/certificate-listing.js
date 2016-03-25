function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var mdmProps = require('/config/mdm-props.js').config();
    context["permissions"] = userModule.getUIPermissions();
    if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/roles/remove")){
        context["removePermitted"] = true;
    }
    var mdmProps = require("/config/mdm-props.js").config();
    context["appContext"] = mdmProps.appContext;
    return context;
}