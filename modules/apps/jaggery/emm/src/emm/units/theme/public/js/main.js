$( document ).ready(function() {
    $('[data-toggle="tooltip"]').tooltip();
    $("[data-toggle=popover]").popover();
    $('#nav').affix({
        offset: {
            top: $('header').height()
        }
    });
}); 