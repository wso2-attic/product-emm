$(document).ready(function(){
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
        invokerUtil.post("https://localhost:9443/mdm-admin/profiles", payload, function(){
            $(".create-profile").addClass("hidden");
            $(".add-policy-profile").removeClass("hidden");
        }, function(){

        });
    });
    $(".btn-add-profile").click(function () {
        $(".add-policy-profile").addClass("hidden");
        $(".profile-message").removeClass("hidden");
    });
});