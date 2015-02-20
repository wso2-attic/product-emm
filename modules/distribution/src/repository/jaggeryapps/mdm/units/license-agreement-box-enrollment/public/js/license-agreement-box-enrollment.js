$( document ).ready(function() {
    var iOSCheckUrl = "/mdm/controller/check";
    setInterval(
        (function x() {
            $.get(iOSCheckUrl).done(function(data, textStatus){
                if(textStatus==200){
                    window.location = "/mdm/thank-you-agent"
                }
            }).fail(function(jqXHR, textStatus){
                    window.location = "/mdm/login-agent#error"
            });
        })(), 5000);
});
