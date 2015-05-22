var serviceEndPoint = "/mdm-admin/policies/";
var modelPopup = '.wr-modalpopup';
var modelPopupContent = modelPopup + ' .modalpopup-content';

$(document).ready(function () {
    function addSortableIndexNumbers() {
        $('.wr-sortable .list-group-item').not('.ui-sortable-placeholder').each(function(i){
            $('.wr-sort-index', this).html(i+1);
        });
    }

    /* sorting function */
    $(function () {
        addSortableIndexNumbers();
        var sortableElem = '.wr-sortable';
        $(sortableElem).sortable({
            beforeStop: function(event, ui){
                var sortedIDs = $(this).sortable('toArray');
                console.log(sortedIDs);
                addSortableIndexNumbers();
            }
        });
        $(sortableElem).disableSelection();
    });

    $(".policy-delete").click(function () {
        var policyId = $(this).data("id");
        $(modelPopupContent).html($('.policy-delete-message').html());
        $(modelPopup).show();
        invokerUtil.delete(serviceEndPoint + policyId,
            function () {
                $(modelPopupContent).html($('.policy-delete-message').html());
                $(modelPopup).show();
            }, function (jqXHR, textStatus, errorThrown) {
                $(modelPopupContent).html($('policy-delete-failed-message').html());
                $(modelPopup).show();
                console.log(textStatus);
            });
    });
});

function hidePopup() {
    $(modelPopupContent).html('');
    $(modelPopup).hide();
}