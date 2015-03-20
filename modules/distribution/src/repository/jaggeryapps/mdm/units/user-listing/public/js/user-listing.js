
$(document).ready(function(){
    $(".inviteAction").click(function(){
        var username = $(this).data("username");
        $.get( "https://localhost:9443/mdm/api/users/"+username+"/invite", function( data ) {
            console.log(message);
        }).fail(function(message){
            console.log(message);
        });
    });
});