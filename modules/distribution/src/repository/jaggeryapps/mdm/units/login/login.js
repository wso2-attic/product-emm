function onRequest(context){
    var mdmProps = require('/config/mdm-props.js').config();
    if (mdmProps.ssoConfiguration.enabled) {
        response.sendRedirect(mdmProps.appContext + "sso/login");
        exit();
    }else{
        context.loginPath = "api/user/login";
    }
    return context;
}
