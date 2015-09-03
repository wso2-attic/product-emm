/**
 * Sorting function of users
 * listed on User Management page in WSO2 MDM Console.
 */
$(function () {
    var sortableElem = '.wr-sortable';
    $(sortableElem).sortable({
        beforeStop : function () {
            var sortedIDs = $(this).sortable('toArray');
            console.log(sortedIDs);
        }
    });
    $(sortableElem).disableSelection();
});

var modalPopup = ".wr-modalpopup";
var modalPopupContainer = modalPopup + " .modalpopup-container";
var modalPopupContent = modalPopup + " .modalpopup-content";
var body = "body";

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    $(modalPopupContent).css('max-height', ($(body).height() - ($(body).height()/100 * 30)));
    $(modalPopupContainer).css('margin-top', (-($(modalPopupContainer).height()/2)));
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

/**
 * Following click function would execute
 * when a user clicks on "Invite" link
 * on User Management page in WSO2 MDM Console.
 */
$("a.invite-user-link").click(function () {
    var username = $(this).data("username");
    var inviteUserAPI = "/mdm-admin/users/" + username + "/email-invitation";

    $(modalPopupContent).html($('#invite-user-modal-content').html());
    showPopup();

    $("a#invite-user-yes-link").click(function () {
        invokerUtil.post(
            inviteUserAPI,
            username,
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
$("a.remove-user-link").click(function () {
    var username = $(this).data("username");
    var removeUserAPI = "/mdm-admin/users/" + username;

    $(modalPopupContent).html($('#remove-user-modal-content').html());
    showPopup();

    $("a#remove-user-yes-link").click(function () {
        invokerUtil.delete(
            removeUserAPI,
            function (data) {
                if (data["statusCode"] == 200) {
                    $("#" + username).remove();
                    // get new user-list-count
                    var newUserListCount = $(".user-list > span").length;
                    // update user-listing-status-msg with new user-count
                    $("#user-count-status-msg").text("Total number of Users found : " + newUserListCount);
                    // update modal-content with success message
                    $(modalPopupContent).html($('#remove-user-success-content').html());
                    $("a#remove-user-success-link").click(function () {
                        hidePopup();
                    });
                }
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
});