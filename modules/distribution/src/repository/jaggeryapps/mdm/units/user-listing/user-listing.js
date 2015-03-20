function onRequest(context){
    var log = new Log();
    var userModule = require("/modules/user.js").userModule;
    //var features = deviceModule.getFeatures();
    context.users = userModule.getUsers();
    return context;
}