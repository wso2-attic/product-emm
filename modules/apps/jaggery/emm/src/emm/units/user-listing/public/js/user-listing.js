/*
 * Sorting function of users
 * listed on User Management page in WSO2 MDM Console.
 */
$(function () {
    var sortableElem = '.wr-sortable';
    $(sortableElem).sortable({
        beforeStop: function () {
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
var isInit = true;
$(".icon .text").res_text(0.2);


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
    var userList = $("#user-grid").find('> tbody > tr');
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
 * Following on click function would execute
 * when a user type on the search field on User Listing page in
 * WSO2 MDM Console then click on the search button.
 */

$("#search-btn").click(function () {
    var searchQuery = $("#search-by-username").val();
    $("#ast-container").empty();
    loadUsers(searchQuery);

});


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
            data = JSON.parse(data);
            data = data.responseContent;
            var viewModel = {};
            viewModel.users = data;
            for (var i = 0; i < viewModel.users.length; i++) {
                viewModel.users[i].userid = viewModel.users[i].username.replace(/[^\w\s]/gi, '');
                if (!canRemove) {
                    viewModel.users[i].canRemove = true;
                }

                if (!canEdit) {
                    viewModel.users[i].canEdit = true;
                }
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
});