$(document).ready(function(){
    $(".btn-create-profile").click(function(){
        $(".create-profile").addClass("hidden");
        $(".add-policy-profile").removeClass("hidden");
    });
    $(".btn-add-profile").click(function () {
        $(".add-policy-profile").addClass("hidden");
        $(".profile-message").removeClass("hidden");
    });
});