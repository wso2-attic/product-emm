/* sorting function */
var sortUpdateBtn = "#sortUpdateBtn";
var sortedIDs;

var saveNewPrioritiesButton = "#save-new-priorities-button";
var saveNewPrioritiesButtonEnabled = Boolean($(saveNewPrioritiesButton).data("enabled"));
if (saveNewPrioritiesButtonEnabled) {
    $(saveNewPrioritiesButton).removeClass("hide");
}

var addSortableIndexNumbers = function () {
    $(".wr-sortable .list-group-item").not(".ui-sortable-placeholder").each(function (i) {
        $(".wr-sort-index", this).html(i+1);
    });
};

var sortElements = function () {
    addSortableIndexNumbers();
    var sortableElem = ".wr-sortable";
    $(sortableElem).sortable({
        beforeStop: function () {
            sortedIDs = $(this).sortable("toArray");
            addSortableIndexNumbers();
            $(sortUpdateBtn).prop("disabled", false);
        }
    });
    $(sortableElem).disableSelection();
};

/**
 * Modal related stuff are as follows.
 */

var modalPopup = ".wr-modalpopup";
var modalPopupContainer = modalPopup + " .modalpopup-container";
var modalPopupContent = modalPopup + " .modalpopup-content";
var body = "body";

/*
 * set popup maximum height function.
 */
function setPopupMaxHeight() {
    $(modalPopupContent).css("max-height", ($(body).height() - ($(body).height()/100 * 30)));
    $(modalPopupContainer).css("margin-top", (-($(modalPopupContainer).height()/2)));
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
 * Click functions related to
 * Policy Listing
 */

$(sortUpdateBtn).click(function () {
    $(sortUpdateBtn).prop("disabled", true);

    var newPolicyPriorityList = [];
    var policy;
    var i;
    for (i = 0; i < sortedIDs.length; i++) {
        policy = {};
        policy.id = parseInt(sortedIDs[i]);
        policy.priority = i+1;
        newPolicyPriorityList.push(policy);
    }

    var updatePolicyAPI = "/mdm/api/policies/update";

    $.ajax({
        type : "POST",
        url : updatePolicyAPI,
        contentType : "application/json",
        data : JSON.stringify(newPolicyPriorityList),
        success : function () {
            $(modalPopupContent).html($('#save-policy-priorities-success-content').html());
            showPopup();
            $("a#save-policy-priorities-success-link").click(function () {
                hidePopup();
            });
        },
        error : function () {
            $(modalPopupContent).html($('#save-policy-priorities-error-content').html());
            showPopup();
            $("a#save-policy-priorities-error-link").click(function () {
                hidePopup();
            });
        }
    });
});

$(".policy-remove-link").click(function () {
    var policyId = $(this).data("id");
    var deletePolicyAPI = "/mdm/api/policies/" + policyId + "/delete";

    $(modalPopupContent).html($('#remove-policy-modal-content').html());
    showPopup();

    $("a#remove-policy-yes-link").click(function () {
        $.ajax({
            type : "GET",
            url : deletePolicyAPI,
            success : function (data) {
                if (data == 200) {
                    $("#" + policyId).remove();
                    sortElements();
                    var newPolicyListCount = $(".policy-list > span").length;
                    if (newPolicyListCount == 1) {
                        $(saveNewPrioritiesButton).addClass("hide");
                    } else if (newPolicyListCount == 0) {
                        $("#policy-count-status-msg").text("No Policies to show currently.");
                    }
                    $(modalPopupContent).html($('#remove-policy-200-content').html());
                    $("a#remove-policy-200-link").click(function () {
                        hidePopup();
                    });
                } else if (data == 409) {
                    $(modalPopupContent).html($('#remove-policy-409-content').html());
                    $("a#remove-policy-409-link").click(function () {
                        hidePopup();
                    });
                } else if (data == 500) {
                    $(modalPopupContent).html($('#remove-policy-500-content').html());
                    $("a#remove-policy-500-link").click(function () {
                        hidePopup();
                    });
                }
            },
            error : function () {
                $(modalPopupContent).html($('#remove-policy-unexpected-error-content').html());
                $("a#remove-policy-unexpected-error-link").click(function () {
                    hidePopup();
                });
            }
        });
    });

    $("a#remove-policy-cancel-link").click(function () {
        hidePopup();
    });
});

$(document).ready(function () {
    sortElements();
});