function onRequest(context){
    var constants = require("/modules/constants.js");
    var mdmProps = require('/config/mdm-props.js').config();
    var localLogoutURL = mdmProps.appContext + "api/user/logout";
    var ssoLogoutURL = mdmProps.appContext + "sso/logout";
    context.logoutURL = mdmProps.ssoConfiguration.enabled? ssoLogoutURL : localLogoutURL;
    context.user = session.get(constants.USER_SESSION_KEY);
    context.homeLink = "/mdm";
    return context;
}
