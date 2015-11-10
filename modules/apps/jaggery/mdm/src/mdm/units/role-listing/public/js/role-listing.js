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
                    $('#role-grid').datatables_extended();
                    $("#dt-select-all").addClass("hidden");
                    $(".icon .text").res_text(0.2);
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
        data = JSON.parse(data);
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
            var sortedIDs = $(this).sortable('toArray');;
        }
    });
    $(sortableElem).disableSelection();
});

var modalPopup = ".wr-modalpopup";
var modalPopupContainer = modalPopup + " .modalpopup-container";
var modalPopupContent = modalPopup + " .modalpopup-content";
var body = "body";
var dataTableSelection = '.DTTT_selected';


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

/**
 * Following click function would execute
 * when a user clicks on "Remove" link
 * on Role Listing page in WSO2 MDM Console.
 */
$("#role-grid").on("click", ".remove-role-link", function () {
    var role = $(this).data("role");
    var removeRoleAPI = "/mdm-admin/roles/" + role;

    $(modalPopupContent).html($('#remove-role-modal-content').html());
    showPopup();

    $("a#remove-role-yes-link").click(function () {
        invokerUtil.delete(
            removeRoleAPI,
            function () {
                $("#role-" + role).remove();
                $(modalPopupContent).html($('#remove-role-success-content').html());
                $("a#remove-role-success-link").click(function () {
                    hidePopup();
                });
            },
            function () {
                $(modalPopupContent).html($('#remove-role-error-content').html());
                $("a#remove-role-error-link").click(function () {
                    hidePopup();
                });
            }
        );
    });

    $("a#remove-role-cancel-link").click(function () {
        hidePopup();
    });
});