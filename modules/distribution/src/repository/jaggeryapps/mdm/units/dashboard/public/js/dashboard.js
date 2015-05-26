var updateStats = function(serviceURL, id){
    invokerUtil.get(serviceURL,
        function(data){
            $(id).html(data);
        }, function(message){
            console.log(message);
        });
}

$(document).ready(function(){
    updateStats("/mdm-admin/devices/count", "#deviceCount");
    updateStats("/mdm-admin/policies/count", "#policyCount");
    updateStats("/mdm-admin/users/count/" + "carbon.super", "#userCount");
});