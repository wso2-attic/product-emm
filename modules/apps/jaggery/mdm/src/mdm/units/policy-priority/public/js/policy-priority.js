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
var applyChangesBtn = "#applyChangesBtn";
var sortedIDs;

var saveNewPrioritiesButtonEnabled = Boolean($(sortUpdateBtn).data("enabled"));
if (saveNewPrioritiesButtonEnabled) {
    $(sortUpdateBtn).removeClass("hide");
}

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
    // Click functions related to Policy Listing
    $(sortUpdateBtn).click(function () {
        $(sortUpdateBtn).prop("disabled", true);

        var newPolicyPriorityList = [];
        var policy;
        var i;
        var sortedItems = sortableListFunction.getSortedItems();
        for (i = 0; i < sortedItems.length; i++) {
            policy = {};
            policy.id = parseInt(sortedItems[i]);
            policy.priority = i + 1;
            newPolicyPriorityList.push(policy);
        }

        var updatePolicyAPI = "/mdm-admin/policies/priorities";
        invokerUtil.put(
            updatePolicyAPI,
            newPolicyPriorityList,
            function () {
                $(modalPopupContent).html($('#save-policy-priorities-success-content').html());
                showPopup();
                $("a#save-policy-priorities-success-link").click(function () {
                    hidePopup();
                });
            },
            function () {
                $("#save-policy-priorities-error-content").find(".message-from-server").html(
                    "Message From Server  :  " + data["statusText"]);
                $(modalPopupContent).html($('#save-policy-priorities-error-content').html());
                showPopup();
                $("a#save-policy-priorities-error-link").click(function () {
                    hidePopup();
                });
            }
        );
        
    });

    $(applyChangesBtn).click(function () {
        var applyPolicyChangesAPI = "/mdm-admin/policies/apply-changes";
        $(modalPopupContent).html($('#change-policy-modal-content').html());
        showPopup();

        $("a#change-policy-yes-link").click(function () {
            invokerUtil.put(
                applyPolicyChangesAPI,
                null,
                // on success
                function () {
                    $(modalPopupContent).html($('#change-policy-success-content').html());
                    showPopup();
                    $("a#change-policy-success-link").click(function () {
                        hidePopup();
                    });
                },
                // on error
                function () {
                    $(modalPopupContent).html($('#change-policy-error-content').html());
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

});
