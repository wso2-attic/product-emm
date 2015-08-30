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
    var maxHeight = "max-height";
    var marginTop = "margin-top";
    var body = "body";
    $(modalPopupContent).css(maxHeight, ($(body).height() - ($(body).height()/100 * 30)));
    $(modalPopupContainer).css(marginTop, (-($(modalPopupContainer).height()/2)));
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

$(document).ready(function () {
    sortElements();

    // Click functions related to Policy Listing
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

        var updatePolicyAPI = "/mdm-admin/policies/priorities";

        $.ajax({
            type : "PUT",
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
        var deletePolicyAPI = "/mdm-admin/policies/" + policyId;

        $(modalPopupContent).html($('#remove-policy-modal-content').html());
        showPopup();

        $("a#remove-policy-yes-link").click(function () {
            $.ajax({
                headers: {
                    Accept : "application/json"
                },
                type : "DELETE",
                url : deletePolicyAPI,
                success : function () {
                    $("#" + policyId).remove();
                    sortElements();
                    var newPolicyListCount = $(".policy-list > span").length;
                    if (newPolicyListCount == 1) {
                        $(saveNewPrioritiesButton).addClass("hidden");
                        $("#policy-listing-status-msg").text("Add more policies to set-up a priority order.");
                    } else if (newPolicyListCount == 0) {
                        $("#policy-listing-status-msg").text("No Policies to show currently.");
                    }
                    $(modalPopupContent).html($('#remove-policy-success-content').html());
                    $("a#remove-policy-success-link").click(function () {
                        hidePopup();
                    });
                },
                error : function () {
                    $(modalPopupContent).html($('#remove-policy-error-content').html());
                    $("a#remove-policy-error-link").click(function () {
                        hidePopup();
                    });
                }
            });
        });

        $("a#remove-policy-cancel-link").click(function () {
            hidePopup();
        });
    });
});