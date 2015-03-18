function onRequest(context){
    var log = new Log();
    var uri = request.getRequestURI();
    var uriMatcher = new URIMatcher(uri);
    var elements = uriMatcher.match("/{context}/device/{type}/{deviceId}");
    //TODO:- null scenarios
    var type = elements['type'];
    var deviceId = elements['deviceId'];
    if(!type && !deviceId){
        response.sendError("404");
        exit();
    }
    var url  = "http://localhost:9763/wso2mdm-api/devices/"+type+"/"+deviceId;
    var response = get(url, "json");
    var status = response.xhr.status;
    if (status == "200"){
        var device = JSON.parse(response.data);
        var arr = device.properties;
        var obj = arr.reduce(function ( total, current ) {
            total[ current.name ] = current.value;
            return total;
        }, {});
        device.properties = obj;
        context.device = device;
    }else if(status == "404"){
        // Handle 404/204 status
    }
    return context;
}