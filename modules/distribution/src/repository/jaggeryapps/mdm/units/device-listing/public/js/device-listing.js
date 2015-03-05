$(document).ready(function () {
    var sample = {
        "devices": [{
            "os": "ios",
            "name": "Dulitha's iPhone",
            "owner": "Dulitha",
            "version": "6.0",
            "vendor": "Apple",
            "model": "iPhone5"
        }]
    };
    var deviceListing = $("#device-listing");
    var deviceListingSrc = deviceListing.attr("src");
    var imageResource = deviceListing.data("image-resource");
    sample.imageLocation = imageResource;
    $.get(deviceListingSrc, function( template ) {
        var compiledTemplate = Handlebars.compile(template);
        var content = compiledTemplate(sample);
        $("#device-list-box").html(content);
    });
});