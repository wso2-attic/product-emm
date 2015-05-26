$(document).ready(function(){
    var profileId;
    $(".btn-create-profile").click(function(){
        var profileName = $("#profile-name-input").val();
        var selectedDeviceType = $("#device-type-input").find(":selected");
        var selectedDeviceId = selectedDeviceType.data("id");
        var payload = {
            profileName: profileName,
            deviceType: {
                id: selectedDeviceId
            },
            profileFeaturesList: [{
                featureCode: "DEVICE_LOCK",
                deviceTypeId: selectedDeviceId,
                content: ""
            }]
        };
        invokerUtil.post("/mdm-admin/profiles", payload, function(data){
            profileId = data.profileId;
            $(".create-profile").addClass("hidden");
            $(".add-policy-profile").removeClass("hidden");
        }, function(){
        });
    });
    $(".btn-add-profile").click(function () {
        var policyId = $("#policy-name-input").find(":selected").data("id");
        var payload = {
            id: policyId,
            profileId: profileId
        };
        invokerUtil.post("/mdm-admin/policies/" + policyId, payload, function(){
            $(".add-policy-profile").addClass("hidden");
            $(".profile-message").removeClass("hidden");
        }, function(){

        });
    });
});