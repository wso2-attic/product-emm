/**
 * Returns the dynamic state to be populated by add-user page.
 * 
 * @param context Object that gets updated with the dynamic state of this page to be presented
 * @returns {*} A context object that returns the dynamic state of this page to be presented
 */
function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var mdmProps = require('/config/mdm-props.js').config();
    var response = userModule.getRoles();
    if (response["status"] == "success") {
        context["roles"] = response["content"];
    }
    var userStores = userModule.getSecondaryUserStores();
    context["userStores"] = userStores;
    context["roleNameJSRegEx"] = mdmProps.roleValidationConfig.rolenameJSRegEx;
    context["roleNameHelpText"] = mdmProps.roleValidationConfig.rolenameHelpMsg;
    context["roleNameRegExViolationErrorMsg"] = mdmProps.roleValidationConfig.rolenameRegExViolationErrorMsg;
    return context;
}