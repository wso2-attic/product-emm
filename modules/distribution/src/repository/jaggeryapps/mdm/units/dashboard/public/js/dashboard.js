var updateStats = function(serviceURL, id){
    invokerUtil.get(serviceURL,
        function(data){
            $(id).html(data);
        }, function(message){
            console.log(message);
        });
}

$(document).ready(function(){
    updateStats("https://localhost:9443/mdm-admin/devices/count", "#deviceCount");
    updateStats("https://localhost:9443/mdm-admin/policies/count", "#policyCount");
    updateStats("https://localhost:9443/mdm-admin/users/count/" + "carbon.super", "#userCount");
});