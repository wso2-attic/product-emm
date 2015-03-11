function onRequest(context){
    var log = new Log();
    var url  = "http://localhost:9763/wso2mdm-api/features";
    var features = get(url, "json");
    features = JSON.parse(features.data);
    features = features.reduce(function ( total, current ) {
        total[ current.name ] = current;
        return total;
    }, {});
    context.features = features;
    return context;
}