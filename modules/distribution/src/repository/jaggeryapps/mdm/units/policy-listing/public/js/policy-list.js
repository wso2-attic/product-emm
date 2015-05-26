var serviceEndPoint = "/mdm-admin/policies/";
var modelPopup = '.wr-modalpopup';
var modelPopupContent = modelPopup + ' .modalpopup-content';

$(document).ready(function () {
    /* sorting function */
    var sortUpdateBtn = '#sortUpdateBtn',
        sortedIDs;

    var saveNewPrioritiesButtonEnabled = Boolean($("#save-new-priorities-button").data("enabled"));
    if (!saveNewPrioritiesButtonEnabled) {
        $("#save-new-priorities-button").addClass("hide");
    }

    function addSortableIndexNumbers(){
        $('.wr-sortable .list-group-item').not('.ui-sortable-placeholder').each(function(i) {
            $('.wr-sort-index', this).html(i+1);
        });
    }

    $(function() {
        addSortableIndexNumbers();
        var sortableElem = '.wr-sortable';
        $(sortableElem).sortable({
            beforeStop: function(event, ui){
                sortedIDs = $(this).sortable('toArray');
                addSortableIndexNumbers();
                $(sortUpdateBtn).prop('disabled', false);
            }
        });
        $(sortableElem).disableSelection();
    });

    $(sortUpdateBtn).click(function () {
        $(sortUpdateBtn).prop('disabled', true);

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
            success : function (data) {
                alert("New Policy priorities were successfully updated.");
            },
            error : function () {
                alert("Policy update failed.");
            }
        });
    });

    // -------------------------------
    $(".policy-view-link").click(function () {
        //alert("id = " + $(this).data("id"));
    });


    $(".policy-delete-link").click(function () {
        var policyId = $(this).data("id");
        var deletePolicyAPI = "/mdm/api/policies/" + policyId + "/delete";
        var userResponse = confirm("Do you really want to delete this policy?");
        if (userResponse == true) {
            $.ajax({
                type : "GET",
                url : deletePolicyAPI,
                success : function (data) {
                    if (data == 200) {
                        alert("Policy was successfully removed.");
                        location.reload();
                    } else if (data == 409) {
                        alert("Policy does not exist.");
                    } else if (data == 500) {
                        alert("Exception at Backend.");
                    }
                },
                error : function () {
                    alert("An unexpected error occurred.");
                }
            });
        }
    });
});

function hidePopup() {
    $(modelPopupContent).html('');
    $(modelPopup).hide();
}