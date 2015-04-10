$( document ).ready(function() {
//    var iOSCheckUrl = "/mdm/controller/check";
//    setInterval(
//        (function x() {
//            $.get(iOSCheckUrl).done(function(data, textStatus){
//                if(textStatus==200){
//                    window.location = "/mdm/thank-you-agent"
//                }
//            }).fail(function(jqXHR, textStatus){
//                if(jqXHR.status==403){
//                    window.location = "/mdm/login-agent#error"
//                }
//            });
//        })(), 5000);

    var iOSCheckUrl = "/mdm/controller/check";
    setInterval(function() {
        $.ajax({
            type: 'GET',
            url: iOSCheckUrl,
            success: function(data) {

            },
            error: function() {

            }
        });
    }, 5000);

});

/**
 * Following will be called when a user clicks
 * on the "Add User" button.
 */
$("a.btn-download-agent").click(function(e){
    e.preventDefault();

    var enrollmentURL = "/mdm/controller/enroll";

    $.ajax({
        type: 'GET',
        url: enrollmentURL,
        success: function(data) {
            if (data) {
                //alert("Response data is not empty : " + data);
                // then device is an iOS device
                var iOSResponse = JSON.parse(data);
                setInterval(function(){
                    //alert("Response data is not empty : " + data);
                    var deviceCheckURL = iOSResponse.deviceCheckURL;
                    var inputs = iOSResponse.inputs;

                    $.ajax({
                        type:'POST',
                        url:deviceCheckURL,
                        contentType:'application/json',
                        data:JSON.stringify(inputs),
                        success:function(data){
                            var response = JSON.parse(data);
                            var deviceID = response.deviceID;
                            if(deviceID != null) {
                                window.location.href = "wso2agent://" + deviceID;
                            }
                        },
                        error:function(){
                            alert("iOS enrollment error");
                        }
                    });
                }, 5000);
            } else {
                alert("Response data is empty");
            }
        },
        error: function() {
            alert("Error in request to /mdm/controller/enroll");
        }
    });
});

