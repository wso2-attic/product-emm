var updateStats = function (serviceURL, id) {
    invokerUtil.get(
        serviceURL,
        function (data) {
            if (!data) {
                updateStats(serviceURL, id);
            } else {
                var data = JSON.parse(data);
                var count =data.count;
                $(id).html(count);
                if (Number(count) <= 0) {
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
        updateStats("/api/device-mgt/v1.0/devices", "#device-count");
    }
    if ($("#policy-count").data("policy-count")) {
        updateStats("/api/device-mgt/v1.0/policies", "#policy-count");
    }
    if ($("#user-count").data("user-count")) {
        updateStats("/api/device-mgt/v1.0/users", "#user-count");
    }
    if ($("#role-count").data("role-count")) {
        updateStats("/api/device-mgt/v1.0/roles", "#role-count");
    }
});

function toggleEnrollment() {
    $(".modalpopup-content").html($("#qr-code-modal").html());
    generateQRCode(".modalpopup-content .qr-code");
    showPopup();
}