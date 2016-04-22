/*
 * Sorting function of users
 * listed on User Management page in WSO2 MDM Console.
 */
$(function () {
    var sortableElem = '.wr-sortable';
    $(sortableElem).sortable({
        beforeStop: function () {
            var sortedIDs = $(this).sortable('toArray');
        }
    });
    $(sortableElem).disableSelection();
});

var modalPopup = ".wr-modalpopup";
var modalPopupContainer = modalPopup + " .modalpopup-container";
var modalPopupContent = modalPopup + " .modalpopup-content";
var body = "body";
var isInit = true;
$(".icon .text").res_text(0.2);

var resetPasswordServiceURL = "/mdm-admin/users/reset-password";

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    $(modalPopupContent).css('max-height', ($(body).height() - ($(body).height() / 100 * 30)));
    $(modalPopupContainer).css('margin-top', (-($(modalPopupContainer).height() / 2)));
}

/*
 * show popup function.
 */
function showPopup() {
    $(modalPopup).show();
    setPopupMaxHeight();
}

/*
 * hide popup function.
 */
function hidePopup() {
    $(modalPopupContent).html('');
    $(modalPopup).hide();
}

/*
 * Function to get selected usernames.
 */
function getSelectedUsernames() {
    var usernameList = [];
    var userList = $("#user-grid").dataTable().fnGetNodes();
    userList.each(function () {
        if ($(this).hasClass('DTTT_selected')) {
            usernameList.push($(this).attr('data-username'));
        }
    });
    return usernameList;
}

/**
 * Following click function would execute
 * when a user clicks on "Invite" link
 * on User Management page in WSO2 MDM Console.
 */
$("a.invite-user-link").click(function () {
    var usernameList = getSelectedUsernames();
    var inviteUserAPI = "/mdm-admin/users/email-invitation";

    if (usernameList.length == 0) {
        $(modalPopupContent).html($("#errorUsers").html());
    } else {
        $(modalPopupContent).html($('#invite-user-modal-content').html());
    }

    showPopup();

    $("a#invite-user-yes-link").click(function () {
        invokerUtil.post(
            inviteUserAPI,
            usernameList,
            function () {
                $(modalPopupContent).html($('#invite-user-success-content').html());
                $("a#invite-user-success-link").click(function () {
                    hidePopup();
                });
            },
            function () {
                $(modalPopupContent).html($('#invite-user-error-content').html());
                $("a#invite-user-error-link").click(function () {
                    hidePopup();
                });
            }
        );
    });

    $("a#invite-user-cancel-link").click(function () {
        hidePopup();
    });
});

/**
 * Following click function would execute
 * when a user clicks on "Remove" link
 * on User Listing page in WSO2 MDM Console.
 */
function removeUser(uname, uid) {
    var username = uname;
    var userid = uid;
    var removeUserAPI = "/mdm-admin/users?username=" + username;
    $(modalPopupContent).html($('#remove-user-modal-content').html());
    showPopup();

    $("a#remove-user-yes-link").click(function () {
        invokerUtil.delete(
            removeUserAPI,
            function () {
                $("#" + userid).remove();
                // get new user-list-count
                var newUserListCount = $(".user-list > span").length;
                // update user-listing-status-msg with new user-count
                $("#user-listing-status-msg").text("Total number of Users found : " + newUserListCount);
                // update modal-content with success message
                $(modalPopupContent).html($('#remove-user-success-content').html());
                $("a#remove-user-success-link").click(function () {
                    hidePopup();
                });
            },
            function () {
                $(modalPopupContent).html($('#remove-user-error-content').html());
                $("a#remove-user-error-link").click(function () {
                    hidePopup();
                });
            }
        );
    });

    $("a#remove-user-cancel-link").click(function () {
        hidePopup();
    });
}

/**
 * Following click function would execute
 * when a user clicks on "Reset Password" link
 * on User Listing page in WSO2 MDM Console.
 */
function resetPassword(uname) {

    $(modalPopupContent).html($('#reset-password-window').html());
    showPopup();

    $("a#reset-password-yes-link").click(function () {
        var newPassword = $("#new-password").val();
        var confirmedPassword = $("#confirmed-password").val();
        var user = uname;

        var errorMsgWrapper = "#notification-error-msg";
        var errorMsg = "#notification-error-msg span";
        if (!newPassword) {
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
            var resetPasswordFormData = {};
            resetPasswordFormData.username = user;
            resetPasswordFormData.newPassword = window.btoa(unescape(encodeURIComponent(confirmedPassword)));

            invokerUtil.post(
                resetPasswordServiceURL,
                resetPasswordFormData,
                function (data) {   // The success callback
                    data = JSON.parse(data);
                    if (data.statusCode == 201) {
                        $(modalPopupContent).html($('#reset-password-success-content').html());
                        $("a#reset-password-success-link").click(function () {
                            hidePopup();
                        });
                    }
                }, function (data) {    // The error callback
                    if (data.statusCode == 400) {
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

    $("a#reset-password-cancel-link").click(function () {
        hidePopup();
    });
}

/**
 * Following on click function would execute
 * when a user type on the search field on User Listing page in
 * WSO2 MDM Console then click on the search button.
 */
$("#search-btn").click(function () {
    var searchQuery = $("#search-by-username").val();
    $("#ast-container").empty();
    loadUsers(searchQuery);
});

/**
 * Following function would execute
 * when a user clicks on the list item
 * initial mode and with out select mode.
 */
function InitiateViewOption() {
    if ($("#can-view").val()) {
        $(location).attr('href', $(this).data("url"));
    } else {
        $(modalPopupContent).html($('#errorUserView').html());
        showPopup();
    }
}

function loadUsers(searchParam) {
    $("#loading-content").show();
    var userListing = $("#user-listing");
    var userListingSrc = userListing.attr("src");
    $.template("user-listing", userListingSrc, function (template) {
        var serviceURL = "/mdm-admin/users";
        if (searchParam) {
            serviceURL = serviceURL + "/view-users?username=" + searchParam;
        }
        var successCallback = function (data) {
            if (!data) {
                $('#ast-container').addClass('hidden');
                $('#user-listing-status-msg').text('No users are available to be displayed.');
                return;
            }
            var canRemove = $("#can-remove").val();
            var canEdit = $("#can-edit").val();
            var canResetPassword = $("#can-reset-password").val();
            data = JSON.parse(data);
            data = data.responseContent;
            var viewModel = {};
            viewModel.users = data;
            for (var i = 0; i < viewModel.users.length; i++) {
                viewModel.users[i].userid = viewModel.users[i].username.replace(/[^\w\s]/gi, '');
                if (canRemove) {
                    viewModel.users[i].canRemove = true;
                }
                if (canEdit) {
                    viewModel.users[i].canEdit = true;
                }
                if (canResetPassword) {
                    viewModel.users[i].canResetPassword = true;
                }
                viewModel.users[i].adminUser = $("#user-table").data("user");
            }
            if (data.length > 0) {
                $('#ast-container').removeClass('hidden');
                $('#user-listing-status-msg').text("");
                var content = template(viewModel);
                $("#ast-container").html(content);
            } else {
                $('#ast-container').addClass('hidden');
                $('#user-listing-status-msg').text('No users are available to be displayed.');
            }
            $("#loading-content").hide();
            if (isInit) {
                $('#user-grid').datatables_extended();
                isInit = false;
            }
            $(".icon .text").res_text(0.2);
        };
        invokerUtil.get(serviceURL,
                        successCallback,
                        function (message) {
                            $('#ast-container').addClass('hidden');
                            $('#user-listing-status-msg').
                                text('Invalid search query. Try again with a valid search query');
                        }
        );
    });
}

$(document).ready(function () {
    loadUsers();

    $(".viewEnabledIcon").click(function () {
        InitiateViewOption();
    });
    if (!$("#can-invite").val()) {
        $("#invite-user-button").remove();
    }
});
