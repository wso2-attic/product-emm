$( document ).ready(function() {
    setTimeout(function(){
        var deviceId = getParameterByName("deviceId");
        window.location.href = "wso2agent://" + deviceId;
    }, 1000);
    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
});