function onRequest(context) {
    var userModule = require("/modules/user.js").userModule;
    var constants = require("/modules/constants.js");
    var permissions = userModule.getUIPermissions();
    var mdmProps = require('/config/mdm-props.js').config();
    context.permissions = permissions;
    context["enrollmentURL"] = mdmProps.enrollmentURL;
    return context;
}