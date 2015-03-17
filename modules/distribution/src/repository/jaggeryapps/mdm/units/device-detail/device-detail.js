function onRequest(context){
    var log = new Log();
    var uri = request.getRequestURI();
    var uriMatcher = new URIMatcher(uri);
    var elements = uriMatcher.match("/{context}/device/{type}/{deviceId}");
    var deviceModule = require("/modules/device.js").deviceModule;
    //TODO:- null scenarios
    var type = elements['type'];
    var deviceId = elements['deviceId'];
    if(!type && !deviceId){
        response.sendError("404");
        exit();
    }
    context.device = deviceModule.viewDevice(type, deviceId);
    log.info(context);
    return context;
}