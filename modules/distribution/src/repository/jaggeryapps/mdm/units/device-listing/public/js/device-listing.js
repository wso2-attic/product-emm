(function(){
    var cache = {};
    $.template = function(name, location, callback){
        var template = cache[name];
        if (!template){
            $.get(location, function( data ) {
                var compiledTemplate = Handlebars.compile(data);
                cache[name] = compiledTemplate;
                callback(compiledTemplate);
            });
        }else{
            callback(template);
        }
    };
    Handlebars.registerHelper('deviceMap', function(device) {
        var arr = device.properties;
        var obj = arr.reduce(function ( total, current ) {
            total[ current.name ] = current.value;
            return total;
        }, {});
        device.properties = obj;
    });
})();
$(document).ready(function () {
    var deviceListing = $("#device-listing");
    var deviceListingSrc = deviceListing.attr("src");
    var imageResource = deviceListing.data("image-resource");
    var performOperation = function(devices, operation){

    };
    $.template("device-listing", deviceListingSrc, function(template){
        $.get("https://localhost:9443/wso2mdm-api/devices", function(data){
            var viewModel = {
                "devices": data
            }
            viewModel.imageLocation = imageResource;
            var content = template(viewModel);
            $("#device-list-box").html(content);
        });

    });
});