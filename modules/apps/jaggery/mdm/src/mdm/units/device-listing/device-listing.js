function onRequest(context){
    var userModule = require("/modules/user.js").userModule;
    var constants = require("/modules/constants.js");
    var permissions = [];
    if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/devices/list")){
        permissions.push("LIST_DEVICES");
    }else if(userModule.isAuthorized("/permission/admin/device-mgt/user/devices/list")){
        permissions.push("LIST_OWN_DEVICES");
    }else if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/policies/list")){
        permissions.push("LIST_POLICIES");
    }
    var currentUser = session.get(constants.USER_SESSION_KEY);
    context.permissions = stringify(permissions);
    context.currentUser = currentUser;
    return context;
}