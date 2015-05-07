function onRequest(context) {
    var uri = request.getRequestURI();
    var uriMatcher = new URIMatcher(String(uri));
    var isMatched = uriMatcher.match("/{context}/device/{deviceType}/{+deviceId}");
    if (isMatched) {
        log.info("***** Call");
        var matchedElements = uriMatcher.elements();
        var deviceType = matchedElements.deviceType;
        var deviceId = matchedElements.deviceId;
        var deviceModule = require("/modules/device.js").deviceModule;
        var device = deviceModule.viewDevice(deviceType, deviceId);
        if (device){
            var viewModel = {};
            var deviceInfo = device.properties.DEVICE_INFO;
            if (deviceInfo != undefined && String(deviceInfo.toString()).length > 0){
                deviceInfo = JSON.parse(deviceInfo);
                if (device.type == "ios"){
                    viewModel.imei = device.properties.IMEI;
                    viewModel.phoneNumber = deviceInfo.PhoneNumber;
                    viewModel.udid = deviceInfo.UDID;
                    viewModel.BatteryLevel = Math.round(deviceInfo.BatteryLevel * 100);
                    viewModel.DeviceCapacity = Math.round(deviceInfo.DeviceCapacity * 100) / 100;
                    viewModel.AvailableDeviceCapacity = Math.round(deviceInfo.AvailableDeviceCapacity * 100) / 100;
                    viewModel.DeviceCapacityUsed = Math.round((viewModel.DeviceCapacity
                        - viewModel.AvailableDeviceCapacity) * 100) / 100;
                    viewModel.DeviceCapacityPercentage = Math.round(viewModel.DeviceCapacityUsed
                        / viewModel.DeviceCapacity * 10000) /100;
                }else if(device.type == "android"){
                    viewModel.imei = device.properties.imei;
                    viewModel.internal_memory = {};
                    viewModel.external_memory = {};
                    viewModel.BatteryLevel = deviceInfo.battery.level;
                    viewModel.internal_memory.FreeCapacity = Math.round((deviceInfo.internal_memory.total -
                    deviceInfo.internal_memory.available) * 100) / 100;
                    viewModel.internal_memory.DeviceCapacityPercentage = Math.round(deviceInfo.internal_memory.available
                        / deviceInfo.internal_memory.total * 10000) / 100;
                    viewModel.external_memory.FreeCapacity = Math.round((deviceInfo.external_memory.total -
                        deviceInfo.external_memory.available) * 100) / 100;
                    viewModel.external_memory.DeviceCapacityPercentage = Math.round(deviceInfo.external_memory.available
                        /deviceInfo.external_memory.total * 10000) /100;
                }
                device.viewModel = viewModel;
            }
        }
        context.device = device;
    } else {
        response.sendError(404);
    }
    return context;
}