$(document).ready(function(){
    $('.btn-select-devices').click(function(){
        if(!$(this).data("select")){
            $(".device-checkbox").each(function(index){
                $(this).prop('checked', true);
            });
            $(this).data("select", true);
        }else{
            $(".device-checkbox").each(function(index){
                $(this).prop('checked', false);
            });
            $(this).data("select", false);
        }
    });
});