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

/**
 * Following click function would execute
 * when a user clicks on "Invite" link
 * on User Management page in WSO2 MDM Console.
 */
$("a.invite-user-link").click(function () {
    var username = $(this).data("username");
    var inviteUserAPI = "/mdm/api/users/" + username + "/invite";
    var userResponse = confirm("An invitation mail will be sent to User (" + username + ") " +
                               "to initiate Enrollment Process");
    if (userResponse == true) {
        invokerUtil.get(inviteUserAPI,
            function () {
                alert("User invitation for enrollment sent.");
            }, function () {
                alert("An unexpected error occurred.");
            });
    }
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
 * when a user clicks on "Remove" link
 * on User Management page in WSO2 MDM Console.
 */
$("a.remove-user-link").click(function () {
    var username = $(this).data("username");
    var removeUserAPI = "/mdm/api/users/" + username + "/remove";

    $(modalPopupContent).html($('#remove-user-modal-content').html());
    showPopup();

    $("a#remove-user-modal-link").click(function () {
        invokerUtil.get(
            removeUserAPI,
            function (data) {
                if (data == 200) {
                    $("#" + username).addClass("hide");
                    $(modalPopupContent).html($('#remove-user-200-content').html());
                    $("a#remove-user-200-link").click(function () {
                        hidePopup();
                    });
                } else if (data == 400) {
                    $(modalPopupContent).html($('#remove-user-400-content').html());
                    $("a#remove-user-400-link").click(function () {
                        hidePopup();
                    });
                } else if (data == 403) {
                    $(modalPopupContent).html($('#remove-user-403-content').html());
                    $("a#remove-user-403-link").click(function () {
                        hidePopup();
                    });
                } else if (data == 409) {
                    $(modalPopupContent).html($('#remove-user-409-content').html());
                    $("a#remove-user-409-link").click(function () {
                        hidePopup();
                    });
                }
            }, function () {
                $(modalPopupContent).html($('#remove-user-unexpected-error-content').html());
                $("a#remove-user-unexpected-error-link").click(function () {
                    hidePopup();
                });
            }
        );
    });
});



