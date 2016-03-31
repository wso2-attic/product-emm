/*
 * Sorting function of certificates
 * listed on Certificate Management page in WSO2 MDM Console.
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

/**
 * Following click function would execute
 * when a user clicks on "Remove" link
 * on User Listing page in WSO2 MDM Console.
 */
function removeCertificate(serialNumber) {
    var removeCertificateAPI = "/mdm-admin/certificates?serial_number=" + serialNumber;
    $(modalPopupContent).html($('#remove-user-modal-content').html());
    showPopup();

    $("a#remove-user-yes-link").click(function () {
        invokerUtil.delete(
            removeCertificateAPI,
            function () {
                $("#" + userid).remove();
                var newUserListCount = $(".user-list > span").length;
                $("#certificate-listing-status-msg").text("Total number of Users found : " + newUserListCount);
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
    loadCertificates(searchQuery);
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

function loadCertificates(searchParam) {
    $("#loading-content").show();
    var userListing = $("#user-listing");
    var userListingSrc = userListing.attr("src");
    $.template("user-listing", userListingSrc, function (template) {
        var serviceURL = "/mdm-admin/certificates";

        var successCallback = function (data) {
            if (!data) {
                $('#ast-container').addClass('hidden');
                $('#certificate-listing-status-msg').text('No users are available to be displayed.');
                return;
            }
            var canRemove = $("#can-remove").val();
            var canEdit = $("#can-edit").val();
            var canResetPassword = $("#can-reset-password").val();
            data = JSON.parse(data);

            var viewModel = {};
            viewModel.certificates = data;
            for (var i = 0; i < viewModel.certificates.length; i++) {
                viewModel.certificates[i].userid = '1';
                viewModel.certificates[i].adminUser = 'test dilshan';
            }
            if (data.length > 0) {
                $('#ast-container').removeClass('hidden');
                $('#certificate-listing-status-msg').text("");
                var content = template(viewModel);
                $("#ast-container").html(content);
            } else {
                $('#ast-container').addClass('hidden');
                $('#certificate-listing-status-msg').text('No users are available to be displayed.');
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
                $('#certificate-listing-status-msg').
                    text('Invalid search query. Try again with a valid search query');
            }
        );
    });
}

$(document).ready(function () {
    loadCertificates();

    $(".viewEnabledIcon").click(function () {
        InitiateViewOption();
    });
    if (!$("#can-invite").val()) {
        $("#invite-user-button").remove();
    }
});
