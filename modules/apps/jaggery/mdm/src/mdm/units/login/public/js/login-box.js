
function utf8_to_b64( str ) {
    return window.btoa(unescape(encodeURIComponent( str )));
}

function b64_to_utf8( str ) {
    return decodeURIComponent(escape(window.atob( str )));
}

function post(path, params, method) {
    method = method || "post"; // Set method to post by default if not specified.

    // The rest of this code assumes you are not using a library.
    // It can be made less wordy if you use one.
    var form = document.createElement("form");
    form.setAttribute("method", method);
    form.setAttribute("action", path);

    for(var key in params) {
        if(params.hasOwnProperty(key)) {
            var hiddenField = document.createElement("input");
            hiddenField.setAttribute("type", "hidden");
            hiddenField.setAttribute("name", key);
            hiddenField.setAttribute("value", params[key]);

            form.appendChild(hiddenField);
        }
    }

    document.body.appendChild(form);
    form.submit();
}


$( document ).ready(function() {
    var currentHash = window.location.hash;
    if(currentHash=="#auth-failed") {
        $('.wr-validation-summary p').text("Sorry!, Please make sure to enter correct username and password");
        $('.wr-validation-summary').removeClass("hidden");
    }else if(currentHash=="#error"){
        $('.wr-validation-summary p').text("Sorry!, Error occured");
        $('.wr-validation-summary').removeClass("hidden");
    }
    $('.btn-download-agent').click(function(){
        var username = $("input#username").val();
        var password = $("input#password").val();

        if (!username) {
            $('.wr-validation-summary p').text("Sorry!, Username cannot be empty.");
            $('.wr-validation-summary').removeClass("hidden");
        } else if (!password){
            $('.wr-validation-summary p').text("Sorry!, Password cannot be empty.");
            $('.wr-validation-summary').removeClass("hidden");
        } else {
            var username = utf8_to_b64($("input#username").val());
            var password = utf8_to_b64($("input#password").val());
            post($("#login").attr("action"),{"username":username,password:password},"POST");
        }
    });
});

function submitLoginForm() {
    $('form').each(function() {
        $(this).find('input').keypress(function(e) {
            if(e.which == 10 || e.which == 13) {
                var username = $("input#username").val();
                var password = $("input#password").val();

                if (!username) {
                    $('.wr-validation-summary p').text("Sorry!, Username cannot be empty.");
                    $('.wr-validation-summary').removeClass("hidden");
                } else if (!password){
                    $('.wr-validation-summary p').text("Sorry!, Password cannot be empty.");
                    $('.wr-validation-summary').removeClass("hidden");
                } else {
                    var username = utf8_to_b64($("input#username").val());
                    var password = utf8_to_b64($("input#password").val());
                    post($("#login").attr("action"),{"username":username,password:password},"POST");
                }
            }
        });
    });
}
