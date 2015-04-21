function onRequest(context) {
    var uri = request.getRequestURI();
    var uriMatcher = new URIMatcher(String(uri));
    var isMatched = uriMatcher.match("/{context}/device/{deviceType}/{+deviceId}");
    if (isMatched) {
        var matchedElements = uriMatcher.elements();
        var deviceType = matchedElements.deviceType;
        var deviceId = matchedElements.deviceId;
        var deviceModule = require("/modules/device.js").deviceModule;
        context.device = deviceModule.viewDevice(deviceType, deviceId);
    } else {
        response.sendError(404);
    }
    return context;
}