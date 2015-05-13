var serviceEndPoint = "https://localhost:9443/mdm-admin/policies/",
    modelPopup = '.wr-modalpopup',
    modelPopupContent = modelPopup + ' .modalpopup-content';
$(document).ready(function(){
    $(".policy-delete").click(function(){
        var policyId = $(this).data("id");
        $(modelPopupContent).html($('.policy-delete-message').html());
        $(modelPopup).show();
        invokerUtil.delete(serviceEndPoint + policyId,
            function(){
                $(modelPopupContent).html($('.policy-delete-message').html());
                $(modelPopup).show();
            }, function(jqXHR, textStatus, errorThrown){
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