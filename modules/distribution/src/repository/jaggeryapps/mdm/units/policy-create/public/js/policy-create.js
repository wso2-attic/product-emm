$('select.select2').select2({
    placeholder: 'Select..'
});

$('select.select2[multiple=multiple]').select2({
    placeholder: 'Select..',
    tags: true
});

$(document).ready(function(){
    $("#policy-create").click(function(){
        $(".policy-message").removeClass("hidden");
        $(".add-policy").addClass("hidden");
    });
});