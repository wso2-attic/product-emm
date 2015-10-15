/**
 * Sorting function of roles
 * listed on Role Management page in WSO2 MDM Console.
 */

var loadPaginatedObjects = function(objectGridId, objectGridContainer, objectGridTemplateSrc, serviceURL, callback){
    var templateSrc = $(objectGridTemplateSrc).attr("src");
    $.template(objectGridId, templateSrc, function (template) {
        invokerUtil.get(serviceURL,
            function(data){
                data = callback(data);
                if(data.length > 0){
                    var content = template(data.viewModel);
                    $(objectGridContainer).html(content);
                }
                //$(objectGridId).datatables_extended();
            }, function(message){
                console.log(message);
            });
    });
}

$(function () {
    var serviceURL = "/mdm-admin/roles";
    var callback = function(data){
        data = {
            "viewModel": {
                "roles": data.responseContent
            },
            "length": data.responseContent.length
        }
        return data;
    }
    loadPaginatedObjects("#role-grid", "#ast-container", "#role-listing", serviceURL, callback);

    var sortableElem = '.wr-sortable';
    $(sortableElem).sortable({
        beforeStop : function () {
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
var dataTableSelection = '.DTTT_selected';
$('#role-grid').datatables_extended();
$(".icon .text").res_text(0.2);

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

/*
 * Function to get selected usernames.
 */
function getSelectedUsernames() {
    var usernameList = [];
    var thisTable = $(".DTTT_selected").closest('.dataTables_wrapper').find('.dataTable').dataTable();
    thisTable.api().rows().every(function(){
        if($(this.node()).hasClass('DTTT_selected')){
            usernameList.push($(thisTable.api().row(this).node()).data('id'));
        }
    });
    return usernameList;
}
/**
 * Following click function would execute
 * when a user clicks on "Remove" link
 * on User Listing page in WSO2 MDM Console.
 */
$("a.remove-user-link").click(function () {
    var username = $(this).data("username");
    var removeUserAPI = "/mdm-admin/users/" + username;

    $(modalPopupContent).html($('#remove-user-modal-content').html());
    showPopup();

    $("a#remove-user-yes-link").click(function () {
        invokerUtil.delete(
            removeUserAPI,
            function (data) {
                if (data["statusCode"] == 200) {
                    $("#" + username).remove();
                    // get new user-list-count
                    var newUserListCount = $(".user-list > span").length;
                    // update user-listing-status-msg with new user-count
                    $("#user-listing-status-msg").text("Total number of Users found : " + newUserListCount);
                    // update modal-content with success message
                    $(modalPopupContent).html($('#remove-user-success-content').html());
                    $("a#remove-user-success-link").click(function () {
                        hidePopup();
                    });
                }
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
});