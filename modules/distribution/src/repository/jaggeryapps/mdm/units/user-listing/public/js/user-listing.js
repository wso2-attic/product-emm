/* sorting function */
$(function() {
    var sortableElem = '.wr-sortable';
    $(sortableElem).sortable({
        beforeStop: function(event, ui){
            var sortedIDs = $(this).sortable('toArray');
            console.log(sortedIDs);
        }
    });
    $(sortableElem).disableSelection();
});

$("a#invite-user-link").click(function() {
    var username = $(this).data("username");
    $.get("/mdm/api/users/" + username + "/invite", function( data ) {
        alert("User invitation for enrollment sent.");
    }).fail(function(message){

    });
});

$("a#remove-user-link").click(function() {
    var username = $(this).data("username");
    var removeUserAPI = "/mdm/api/users/" + username + "/remove";
    $.ajax({
        type:'GET',
        url:removeUserAPI,
        success:function(data){
            if (data == 200) {
                alert("user successfully removed.");
                location.reload();
            } else if (data == 400) {
                alert("Exception at backend.");
            } else if (data == 403) {
                alert("Action not permitted.");
            } else if (data == 409) {
                alert("User does not exist.");
            }
        },
        error:function(){
            alert("An unexpected error occurred.");
        }
    });
});

