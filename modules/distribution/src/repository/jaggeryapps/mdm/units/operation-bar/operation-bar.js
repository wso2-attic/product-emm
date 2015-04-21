function onRequest(context){
    var log = new Log();
    var deviceModule = require("/modules/device.js").deviceModule;
    // temporarily providing device-type as "ios" here.
    // either we should update operations here according to all devices
    // or we should display this only for an individual device
    context.features = deviceModule.getFeatures("ios");
    log.info(context.features.DEVICE_LOCK);
    return context;
}