(function(){
    var cache = {};
    var permissionSet = {};
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
    //This method is used to setup permission for device listing
    $.setPermission = function (permission){
        permissionSet[permission] = true;
    }
    $.hasPermission = function (permission){
        return permissionSet[permission];
    }
    //TODO: Call the backend and get the permission list
    var tempPermlist = ["LIST_DEVICES", "LIST_OWN_DEVICES"];
    for(temp in tempPermlist){
        $.setPermission(tempPermlist[temp]);
    }
})();

function changeDeviceView(view, selection){
    $('.view-toggle').each(function() {
        $(this).removeClass('selected');
    });
    $(selection).addClass('selected');

    if(view == 'list'){
        $('#ast-container').addClass('list-view');
    }
    else {
        $('#ast-container').removeClass('list-view');
    }
}
$(document).ready(function () {
    var deviceListing = $("#device-listing");
    var deviceListingSrc = deviceListing.attr("src");
    var imageResource = deviceListing.data("image-resource");
    $.template("device-listing", deviceListingSrc, function(template){
        var serviceURL;
        if ($.hasPermission("LIST_DEVICES")) {
            serviceURL = "https://localhost:9443/mdm/api/devices";
        }else if($.hasPermission("LIST_OWN_DEVICES")){
            //Get authenticated users devices
            serviceURL = "https://localhost:9443/wso2mdm-api/user/devices";
        }else {
            $("#ast-container").html("Permission denied");
            return;
        }
        $.get(serviceURL, function(data){
            var viewModel = {
                "devices": JSON.parse(data)
            }
            viewModel.imageLocation = imageResource;
            var content = template(viewModel);
            $("#ast-container").html(content);
        });
    });
});