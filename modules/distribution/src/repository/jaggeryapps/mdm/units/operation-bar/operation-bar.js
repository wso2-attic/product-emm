function onRequest(context){
    var log = new Log();
    var deviceModule = require("/modules/device.js").deviceModule;
   // var features = deviceModule.getFeatures();
    context.features = deviceModule.getFeatures();
    log.info(context.features.DEVICE_LOCK);
    return context;
}