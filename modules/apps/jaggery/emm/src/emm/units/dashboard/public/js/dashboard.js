var updateStats = function (serviceURL, id) {
    invokerUtil.get(
        serviceURL,
        function (data) {
            if (!data) {
                updateStats(serviceURL, id);
            } else {
                data = JSON.parse(data);
                $(id).html(data);
                if (Number(data) <= 0) {
                    $(id + "-view-btn").hide();
                }
            }
        }, function (message) {
            console.log(message.content);
        }
    );
};

$(document).ready(function () {
    if ($("#device-count").data("device-count")) {
        updateStats("/mdm-admin/devices/count", "#device-count");
    }
    if ($("#policy-count").data("policy-count")) {
        updateStats("/mdm-admin/policies/count", "#policy-count");
    }
    if ($("#user-count").data("user-count")) {
        updateStats("/mdm-admin/users/count", "#user-count");
    }
    if ($("#role-count").data("role-count")) {
        updateStats("/mdm-admin/roles/count", "#role-count");
    }
});

function toggleEnrollment() {
    $(".modalpopup-content").html($("#qr-code-modal").html());
    generateQRCode(".modalpopup-content .qr-code");
    showPopup();
}