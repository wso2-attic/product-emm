/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/* sorting function */
var sortUpdateBtn = "#sortUpdateBtn";
// var sortedIDs;
// var dataTableSelection = ".DTTT_selected";
$('#policy-grid').datatables_extended();
// $(".icon .text").res_text(0.2);

var saveNewPrioritiesButton = "#save-new-priorities-button";
var saveNewPrioritiesButtonEnabled = Boolean($(saveNewPrioritiesButton).data("enabled"));
if (saveNewPrioritiesButtonEnabled) {
    $(saveNewPrioritiesButton).removeClass("hide");
}

/**
 * Following function would execute
 * when a user clicks on the list item
 * initial mode and with out select mode.
 */
function InitiateViewOption() {
    $(location).attr('href', $(this).data("url"));
}

//var addSortableIndexNumbers = function () {
//    $(".wr-sortable .list-group-item").not(".ui-sortable-placeholder").each(function (i) {
//        $(".wr-sort-index", this).html(i + 1);
//    });
//};

//var sortElements = function () {
//    addSortableIndexNumbers();
//    var sortableElem = ".wr-sortable";
//    $(sortableElem).sortable({
//        beforeStop: function () {
//            sortedIDs = $(this).sortable("toArray");
//            addSortableIndexNumbers();
//            $(sortUpdateBtn).prop("disabled", false);
//        }
//    });
//    $(sortableElem).disableSelection();
//};

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
    var maxHeight = "max-height";
    var marginTop = "margin-top";
    var body = "body";
    $(modalPopupContent).css(maxHeight, ($(body).height() - ($(body).height() / 100 * 30)));
    $(modalPopupContainer).css(marginTop, (-($(modalPopupContainer).height() / 2)));
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
 * Function to get selected policies.
 */
function getSelectedPolicyStates() {
    var policyList = [];
    var thisTable = $(".DTTT_selected").closest('.dataTables_wrapper').find('.dataTable').dataTable();
    thisTable.api().rows().every(function () {
        if ($(this.node()).hasClass('DTTT_selected')) {
            policyList.push($(thisTable.api().row(this).node()).data('status'));
        }
    });

    return policyList;
}

/*
 * Function to get selected policies.
 */
function getSelectedPolicies() {
    var policyList = [];
    var thisTable = $(".DTTT_selected").closest('.dataTables_wrapper').find('.dataTable').dataTable();
    thisTable.api().rows().every(function () {
        if ($(this.node()).hasClass('DTTT_selected')) {
            policyList.push($(thisTable.api().row(this).node()).data('id'));
        }
    });

    return policyList;
}

$(document).ready(function () {
//    sortElements();

//    var policyRoles = $("#policy-roles").text();
//    var policyUsers = $("#policy-users").text();
//
//    if (!policyRoles) {
//        $("#policy-roles").hide();
//    }
//    if (!policyUsers) {
//        $("#policy-users").hide();
//    }

    /**
     * ********************************************
     * Click functions related to Policy Listing
     * ********************************************
     */

    // [1] logic for running apply-changes-for-devices use-case

    var applyChangesButtonId = "#appbar-btn-apply-changes";

    var isUpdated = $("#is-updated").val();
    if (!isUpdated) {
        // if no updated policies found, hide button from app bar
        $(applyChangesButtonId).addClass("hidden");
    } else {
        // if updated policies found, show button from app bar
        $(applyChangesButtonId).removeClass("hidden");
    }

    // click-event function for applyChangesButton
    $(applyChangesButtonId).click(function () {
        var serviceURL = "/api/device-mgt/v1.0/policies/apply-changes";
        $(modalPopupContent).html($('#change-policy-modal-content').html());
        showPopup();

        $("a#change-policy-yes-link").click(function () {
            invokerUtil.put(
                serviceURL,
                null,
                // on success
                function (data, textStatus, jqXHR) {
                    if (jqXHR.status == 200) {
                        $(modalPopupContent).html($('#change-policy-success-content').html());
                        showPopup();
                        $("a#change-policy-success-link").click(function () {
                            hidePopup();
                            location.reload();
                        });
                    }
                },
                // on error
                function (jqXHR) {
                    console.log(stringify(jqXHR.data));
                    $(modalPopupContent).html($("#change-policy-error-content").html());
                    showPopup();
                    $("a#change-policy-error-link").click(function () {
                        hidePopup();
                    });
                }
            );
        });

        $("a#change-policy-cancel-link").click(function () {
            hidePopup();
        });
    });

    // [2] logic for un-publishing a selected set of Active, Active/Updated policies

    $(".policy-unpublish-link").click(function () {
        var policyList = getSelectedPolicies();
        var statusList = getSelectedPolicyStates();
        if (($.inArray("Inactive/Updated", statusList) > -1) || ($.inArray("Inactive", statusList) > -1)) {
            // if policies found in Inactive or Inactive/Updated states with in the selection,
            // pop-up an error saying
            // "You cannot select already inactive policies. Please deselect inactive policies and try again."
            $(modalPopupContent).html($("#errorPolicyUnPublishSelection").html());
            showPopup();
        } else {
            var serviceURL = "/api/device-mgt/v1.0/policies/deactivate-policy";
            if (policyList.length == 0) {
                $(modalPopupContent).html($("#errorPolicyUnPublish").html());
            } else {
                $(modalPopupContent).html($("#unpublish-policy-modal-content").html());
            }
            showPopup();

            // on-click function for policy un-publishing "yes" button
            $("a#unpublish-policy-yes-link").click(function () {
                invokerUtil.put(
                    serviceURL,
                    policyList,
                    // on success
                    function (data, textStatus, jqXHR) {
                        if (jqXHR.status == 200 && data) {
                            $(modalPopupContent).html($("#unpublish-policy-success-content").html());
                            $("a#unpublish-policy-success-link").click(function () {
                                hidePopup();
                                location.reload();
                            });
                        }
                    },
                    // on error
                    function (jqXHR) {
                        console.log(stringify(jqXHR.data));
                        $(modalPopupContent).html($("#unpublish-policy-error-content").html());
                        $("a#unpublish-policy-error-link").click(function () {
                            hidePopup();
                        });
                    }
                );
            });

            // on-click function for policy un-publishing "cancel" button
            $("a#unpublish-policy-cancel-link").click(function () {
                hidePopup();
            });
        }
    });

    // [3] logic for publishing a selected set of Inactive, Inactive/Updated policies

    $(".policy-publish-link").click(function () {
        var policyList = getSelectedPolicies();
        var statusList = getSelectedPolicyStates();
        if (($.inArray("Active/Updated", statusList) > -1) || ($.inArray("Active", statusList) > -1)) {
            // if policies found in Active or Active/Updated states with in the selection,
            // pop-up an error saying
            // "You cannot select already active policies. Please deselect active policies and try again."
            $(modalPopupContent).html($("#active-policy-selection-error").html());
            showPopup();
        } else {
            var serviceURL = "/api/device-mgt/v1.0/policies/activate-policy";
            if (policyList.length == 0) {
                $(modalPopupContent).html($("#policy-publish-error").html());
            } else {
                $(modalPopupContent).html($("#publish-policy-modal-content").html());
            }
            showPopup();

            // on-click function for policy removing "yes" button
            $("a#publish-policy-yes-link").click(function () {
                invokerUtil.put(
                    serviceURL,
                    policyList,
                    // on success
                    function (data, textStatus, jqXHR) {
                        if (jqXHR.status == 200 && data) {
                            $(modalPopupContent).html($("#publish-policy-success-content").html());
                            $("a#publish-policy-success-link").click(function () {
                                hidePopup();
                                location.reload();
                            });
                        }
                    },
                    // on error
                    function (jqXHR) {
                        console.log(stringify(jqXHR.data));
                        $(modalPopupContent).html($("#publish-policy-error-content").html());
                        $("a#publish-policy-error-link").click(function () {
                            hidePopup();
                        });
                    }
                );
            });

            // on-click function for policy removing "cancel" button
            $("a#publish-policy-cancel-link").click(function () {
                hidePopup();
            });
        }
    });

    // [4] logic for removing a selected set of policies

    $(".policy-remove-link").click(function () {
        var policyList = getSelectedPolicies();
        var statusList = getSelectedPolicyStates();
        if (($.inArray("Active/Updated", statusList) > -1) || ($.inArray("Active", statusList) > -1)) {
            // if policies found in Active or Active/Updated states with in the selection,
            // pop-up an error saying
            // "You cannot remove already active policies. Please deselect active policies and try again."
            $(modalPopupContent).html($("#active-policy-selection-error").html());
            showPopup();
        } else {
            var serviceURL = "/api/device-mgt/v1.0/policies/remove-policy";
            if (policyList.length == 0) {
                $(modalPopupContent).html($("#policy-remove-error").html());
            } else {
                $(modalPopupContent).html($("#remove-policy-modal-content").html());
            }
            showPopup();

            // on-click function for policy removing "yes" button
            $("a#remove-policy-yes-link").click(function () {
                invokerUtil.post(
                    serviceURL,
                    policyList,
                    // on success
                    function (data, textStatus, jqXHR) {
                        if (jqXHR.status == 200 && data) {
                            $(modalPopupContent).html($("#remove-policy-success-content").html());
                            $("a#remove-policy-success-link").click(function () {
                                hidePopup();
                                location.reload();
                            });
                        }
                    },
                    // on error
                    function (jqXHR) {
                        console.log(stringify(jqXHR.data));
                        $(modalPopupContent).html($("#remove-policy-error-content").html());
                        $("a#remove-policy-error-link").click(function () {
                            hidePopup();
                        });
                    }
                );
            });

            // on-click function for policy removing "cancel" button
            $("a#remove-policy-cancel-link").click(function () {
                hidePopup();
            });
        }
    });

    $("#loading-content").remove();
    if ($("#policy-listing-status-msg").text()) {
        $("#policy-listing-status").removeClass("hidden");
    }
    $("#policy-grid").removeClass("hidden");
    // $(".icon .text").res_text(0.2);
});
