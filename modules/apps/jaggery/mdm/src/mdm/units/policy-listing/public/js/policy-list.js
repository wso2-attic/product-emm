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
var dataTableSelection = '.DTTT_selected';
$('#policy-grid').datatables_extended();
$(".icon .text").res_text(0.2);

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

/*
 * Function to get selected policies.
 */
function getSelectedPolicies() {
    var policyList = [];
    var thisTable = $(".DTTT_selected").closest('.dataTables_wrapper').find('.dataTable').dataTable();
    thisTable.api().rows().every(function(){
        if($(this.node()).hasClass('DTTT_selected')){
            policyList.push($(thisTable.api().row(this).node()).data('id'));
        }
    });

    return policyList;
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

    $(".policy-unpublish-link").click(function () {
        var policyList = getSelectedPolicies();
        var serviceURL = "/mdm-admin/policies/inactivate";;
        console.log(policyList);
        if (policyList == 0) {
            $(modalPopupContent).html($("#errorPolicyUnPublish").html());
        } else {
            $(modalPopupContent).html($('#unpublish-policy-modal-content').html());
        }
        showPopup();

        $("a#unpublish-policy-yes-link").click(function () {
            invokerUtil.put(
                serviceURL,
                policyList,
                // on success
                function () {
                    $(modalPopupContent).html($('#unpublish-policy-success-content').html());
                    $("a#unpublish-policy-success-link").click(function () {
                        hidePopup();
                        location.reload();
                    });
                },
                // on error
                function () {
                    $(modalPopupContent).html($('#unpublish-policy-error-content').html());
                    $("a#unpublish-policy-error-link").click(function () {
                        hidePopup();
                    });
                }
            );
        });

        $("a#unpublish-policy-cancel-link").click(function () {
            hidePopup();
        });
    });


    $(".policy-publish-link").click(function () {
        var policyList = getSelectedPolicies();
        var serviceURL = "/mdm-admin/policies/activate";;
        console.log(policyList);
        if (policyList == 0) {
            $(modalPopupContent).html($("#errorPolicyPublish").html());
        } else {
            $(modalPopupContent).html($('#publish-policy-modal-content').html());
        }
        showPopup();

        $("a#publish-policy-yes-link").click(function () {
            invokerUtil.put(
                serviceURL,
                policyList,
                // on success
                function () {
                    $(modalPopupContent).html($('#publish-policy-success-content').html());
                    $("a#publish-policy-success-link").click(function () {
                        hidePopup();
                        location.reload();
                    });
                },
                // on error
                function () {
                    $(modalPopupContent).html($('#publish-policy-error-content').html());
                    $("a#publish-policy-error-link").click(function () {
                        hidePopup();
                    });
                }
            );
        });

        $("a#publish-policy-cancel-link").click(function () {
            hidePopup();
        });
    });

    $(".policy-remove-link").click(function () {
        var policyList = getSelectedPolicies();
        var deletePolicyAPI = "/mdm-admin/policies/bulk-remove";
        if (policyList == 0) {
            $(modalPopupContent).html($("#errorPolicy").html());
        } else {
            $(modalPopupContent).html($('#remove-policy-modal-content').html());
        }
        showPopup();

        $("a#remove-policy-yes-link").click(function () {
            invokerUtil.post(
                deletePolicyAPI,
                policyList,
                // on success
                function () {
                    $(modalPopupContent).html($('#remove-policy-success-content').html());
                    $("a#remove-policy-success-link").click(function () {
                        var thisTable = $(".DTTT_selected").closest('.dataTables_wrapper').find('.dataTable').dataTable();
                        thisTable.api().rows('.DTTT_selected').remove().draw(false);
                        hidePopup();
                    });
                },
                // on error
                function () {
                    $(modalPopupContent).html($('#remove-policy-error-content').html());
                    $("a#remove-policy-error-link").click(function () {
                        hidePopup();
                    });
                }
            );
        });

        $("a#remove-policy-cancel-link").click(function () {
            hidePopup();
        });
    });
});
