function onRequest(context){
    var userModule = require("/modules/user.js").userModule;
    var constants = require("/modules/constants.js");
    var permissions = [];
    if(userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/notifications/list")){
        permissions.push("LIST_NOTIFICATIONS");
    }
    var currentUser = session.get(constants.USER_SESSION_KEY);
    context.permissions = stringify(permissions);
    context.currentUser = currentUser;
    return context;
}
