function onRequest(context) {
    var uri = request.getRequestURI();
    var uriMatcher = new URIMatcher(String(uri));
    var isMatched = uriMatcher.match("/{context}/device/{deviceType}/{+deviceId}");
    if (isMatched) {
        var matchedElements = uriMatcher.elements();
        var deviceType = matchedElements.deviceType;
        var deviceId = matchedElements.deviceId;
        var deviceModule = require("/modules/device.js").deviceModule;
        var device = deviceModule.viewDevice(deviceType, deviceId);
        if (device.properties.DEVICE_INFO != undefined && String(device.properties.DEVICE_INFO.toString()).length > 0){
            if (device.type == "ios"){
                device.properties.DEVICE_INFO = JSON.parse(device.properties.DEVICE_INFO);
                device.properties.DEVICE_INFO.BatteryLevel = Math.round(device.properties.DEVICE_INFO.BatteryLevel
                * 100);
                device.properties.DEVICE_INFO.DeviceCapacity = Math.round(device.properties.DEVICE_INFO.DeviceCapacity
                * 100) / 100;
                device.properties.DEVICE_INFO.AvailableDeviceCapacity =
                    Math.round(device.properties.DEVICE_INFO.AvailableDeviceCapacity * 100) / 100;
                device.properties.DEVICE_INFO.DeviceCapacityUsed = Math.round((device.properties.DEVICE_INFO.DeviceCapacity
                - device.properties.DEVICE_INFO.AvailableDeviceCapacity) * 100) / 100;
                device.properties.DEVICE_INFO.DeviceCapacityPercentage = Math.round(
                    device.properties.DEVICE_INFO.DeviceCapacityUsed/ device.properties.DEVICE_INFO.DeviceCapacity * 10000)
                /100;
            }else if(device.type == "android"){

                device.properties.DEVICE_INFO = JSON.parse(device.properties.DEVICE_INFO);
                device.properties.DEVICE_INFO.internal_memory.FreeCapacity =
                    Math.round((device.properties.DEVICE_INFO.internal_memory.total -
                    device.properties.DEVICE_INFO.internal_memory.available) * 100) / 100;
                device.properties.DEVICE_INFO.internal_memory.DeviceCapacityPercentage = Math.round(
                    device.properties.DEVICE_INFO.internal_memory.available/
                    device.properties.DEVICE_INFO.internal_memory.total * 10000) / 100;
                device.properties.DEVICE_INFO.external_memory.FreeCapacity =
                    Math.round((device.properties.DEVICE_INFO.external_memory.total -
                    device.properties.DEVICE_INFO.external_memory.available) * 100) / 100;
                device.properties.DEVICE_INFO.external_memory.DeviceCapacityPercentage = Math.round(
                    device.properties.DEVICE_INFO.external_memory.available/ device.properties.DEVICE_INFO.external_memory.total * 10000)
                /100;
                log.info(device.properties.DEVICE_INFO);
            }
        }
        context.device = device;
    } else {
        response.sendError(404);
    }
    return context;
}