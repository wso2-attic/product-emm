/**
 * Returns the dynamic state to be populated by add-user page.
 *
 * @param context Object that gets updated with the dynamic state of this page to be presented
 * @returns {*} A context object that returns the dynamic state of this page to be presented
 */
function onRequest(context) {
    var log = new Log("units/user-create/certificate-create.js");
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getRolesByUserStore("PRIMARY");
    var mdmProps = require('/config/mdm-props.js').config();
    context["charLimit"] = mdmProps.usernameLength;
    if (response["status"] == "success") {
        context["roles"] = response["content"];
    }
    context["usernameJSRegEx"] = mdmProps.userValidationConfig.usernameJSRegEx;
    context["usernameHelpText"] = mdmProps.userValidationConfig.usernameHelpMsg;
    context["usernameRegExViolationErrorMsg"] = mdmProps.userValidationConfig.usernameRegExViolationErrorMsg;
    context["firstnameJSRegEx"] = mdmProps.userValidationConfig.firstnameJSRegEx;
    context["firstnameRegExViolationErrorMsg"] = mdmProps.userValidationConfig.firstnameRegExViolationErrorMsg;
    context["lastnameJSRegEx"] = mdmProps.userValidationConfig.lastnameJSRegEx;
    context["lastnameRegExViolationErrorMsg"] = mdmProps.userValidationConfig.lastnameRegExViolationErrorMsg;
    return context;
}