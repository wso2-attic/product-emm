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
 * on Certificate Listing page in WSO2 MDM Console.
 */
function removeCertificate(serialNumber) {
    var removeCertificateAPI = "/mdm-admin/certificates/" + serialNumber;
    $(modalPopupContent).html($('#remove-certificate-modal-content').html());
    showPopup();

    $("a#remove-certificate-yes-link").click(function () {
        invokerUtil.delete(
            removeCertificateAPI,
            function () {
                $("#" + serialNumber).remove();
                var newCertificateListCount = $(".user-list > span").length;
                $("#certificate-listing-status-msg").text("Total number of Certificates found : " + newCertificateListCount);
                $(modalPopupContent).html($('#remove-certificate-success-content').html());
                $("a#remove-certificate-success-link").click(function () {
                    hidePopup();
                });
            },
            function () {
                $(modalPopupContent).html($('#remove-certificate-error-content').html());
                $("a#remove-certificate-error-link").click(function () {
                    hidePopup();
                });
            }
        );
    });

    $("a#remove-certificate-cancel-link").click(function () {
        hidePopup();
    });
}

/**
 * Following on click function would execute
 * when a user type on the search field on certificate Listing page in
 * WSO2 MDM Console then click on the search button.
 */
$("#search-btn").click(function () {
    var searchQuery = $("#search-by-certificate").val();
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
        $(modalPopupContent).html($('#errorCertificateView').html());
        showPopup();
    }
}

function loadCertificates(searchParam) {
    $("#loading-content").show();
    var certificateListing = $("#certificate-listing");
    var certificateListingSrc = certificateListing.attr("src");
    $.template("certificate-listing", certificateListingSrc, function (template) {
        var serviceURL = "/mdm-admin/certificates";

        if(searchParam != null && searchParam != undefined && searchParam.trim() != '') {
            serviceURL = "/mdm-admin/certificates/" + searchParam;
        }

        var successCallback = function (data) {
            if (!data) {
                $('#ast-container').addClass('hidden');
                $('#certificate-listing-status-msg').text('No certificates are available to be displayed.');
                return;
            }
            var canRemove = $("#can-remove").val();
            var canEdit = $("#can-edit").val();
            var canResetPassword = $("#can-reset-password").val();
            data = JSON.parse(data);

            var viewModel = {};
            viewModel.certificates = data;

            for (var i = 0; i < viewModel.certificates.length; i++) {
                viewModel.certificates[i].removePermitted = true;
                viewModel.certificates[i].viewPermitted = true;
            }

            if (viewModel.certificates.length > 0) {
                $('#ast-container').removeClass('hidden');
                $('#certificate-listing-status-msg').text("");
                var content = template(viewModel);
                $("#ast-container").html(content);
            } else {
                $('#ast-container').addClass('hidden');
                $('#certificate-listing-status-msg').text('No certificates are available to be displayed.');
            }

            $("#loading-content").hide();

            if (isInit) {
                $('#certificate-grid').datatables_extended();
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
});
