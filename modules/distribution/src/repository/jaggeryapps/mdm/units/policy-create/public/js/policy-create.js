$('select.select2').select2({
    placeholder: 'Select..'
});

$('select.select2[multiple=multiple]').select2({
    placeholder: 'Select..',
    tags: true
});

$(document).ready(function(){
    $("#policy-create").click(function(){
        var policyName = $("#policy-name-input").val();
        var selectedProfiles = $("#profile-input").find(":selected");
        var selectedProfileId = selectedProfiles.data("id");
        var selectedUserRoles = $("#user-roles-input").val();
        var selectedUsers = $("#users-input").val();
        var selectedAction = $("#action-input").val();
        var payload = {
            policyName: policyName,
            users: selectedUsers,
            roleList: selectedUserRoles,
            profileId: selectedProfileId
        };
        invokerUtil.post("https://localhost:9443/mdm-admin/policies", payload, function(){
            $(".policy-message").removeClass("hidden");
            $(".add-policy").addClass("hidden");
        }, function(){

        });

    });
});