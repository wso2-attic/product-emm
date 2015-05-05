function onRequest(context){
    var log = new Log();
    var deviceModule = require("/modules/device.js").deviceModule;
    // temporarily providing device-type as "ios" here.
    // either we should update operations here according to all devices
    // or we should display this only for an individual device
    var deviceType = context.deviceType;
    deviceType = "android";
    if (deviceType){
        context.features = deviceModule.getFeatures(deviceType);
    }
    return context;
}