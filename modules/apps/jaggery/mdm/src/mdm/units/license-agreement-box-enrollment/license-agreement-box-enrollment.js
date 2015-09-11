function onRequest() {

     var deviceModule = require("/modules/device.js")["deviceModule"];
     var license = deviceModule.getLicense();

     var context = {};
     if (license) {
          context.license = license.text;
     }

     return context;
}