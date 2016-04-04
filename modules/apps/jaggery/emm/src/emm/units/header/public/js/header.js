/**
 * Checks if provided input is valid against RegEx input.
 *
 * @param regExp Regular expression
 * @param inputString Input string to check
 * @returns {boolean} Returns true if input matches RegEx
 */
function inputIsValid(regExp, inputString) {
    return regExp.test(inputString);
}

$(document).ready(function () {
    var modalPopup = ".wr-modalpopup";
    var modalPopupContainer = modalPopup + " .modalpopup-container";
    var modalPopupContent = modalPopup + " .modalpopup-content";

    $("#change-password").click(function () {

        $(modalPopupContent).html($('#change-password-window').html());
        showPopup();

        $("a#change-password-yes-link").click(function () {
            var oldPassword = $("#old-password").val();
            var newPassword = $("#new-password").val();
            var confirmedPassword = $("#confirmed-password").val();
            var user = $("#user").val();

            var errorMsgWrapper = "#notification-error-msg";
            var errorMsg = "#notification-error-msg span";
            if (!oldPassword) {
                $(errorMsg).text("Old password is a required field. It cannot be empty.");
                $(errorMsgWrapper).removeClass("hidden");
            } else if (!newPassword) {
                $(errorMsg).text("New password is a required field. It cannot be empty.");
                $(errorMsgWrapper).removeClass("hidden");
            } else if (!confirmedPassword) {
                $(errorMsg).text("Retyping the new password is required.");
                $(errorMsgWrapper).removeClass("hidden");
            } else if (confirmedPassword != newPassword) {
                $(errorMsg).text("New password doesn't match the confirmation.");
                $(errorMsgWrapper).removeClass("hidden");
            } else if (!inputIsValid(/^[\S]{5,30}$/, confirmedPassword)) {
                $(errorMsg).text("Password should be minimum 5 characters long, should not include any whitespaces.");
                $(errorMsgWrapper).removeClass("hidden");
            } else {
                var changePasswordFormData = {};
                changePasswordFormData.username = user;
                changePasswordFormData.oldPassword = window.btoa(unescape(encodeURIComponent(oldPassword)));
                changePasswordFormData.newPassword = window.btoa(unescape(encodeURIComponent(confirmedPassword)));

                var changePasswordAPI = "/mdm-admin/users/change-password";

                invokerUtil.post(
                    changePasswordAPI,
                    changePasswordFormData,
                    function (data) {
                        data = JSON.parse(data);
                        if (data.statusCode == 201) {
                            $(modalPopupContent).html($('#change-password-success-content').html());
                            $("a#change-password-success-link").click(function () {
                                hidePopup();
                            });
                        }
                    }, function (data) {
                        if (data.status == 400) {
                            $(errorMsg).text("Old password does not match with the provided value.");
                            $(errorMsgWrapper).removeClass("hidden");
                        } else {
                            $(errorMsg).text("An unexpected error occurred. Please try again later.");
                            $(errorMsgWrapper).removeClass("hidden");
                        }
                    }
                );
            }

        });

        $("a#change-password-cancel-link").click(function () {
            hidePopup();
        });
    });
});