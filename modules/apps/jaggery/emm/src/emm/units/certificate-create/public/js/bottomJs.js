var pemContent = "";
var validateInline = {};
var clearInline = {};

var enableInlineError = function (inputField, errorMsg, errorSign) {
    var fieldIdentifier = "#" + inputField;
    var errorMsgIdentifier = "#" + inputField + " ." + errorMsg;
    var errorSignIdentifier = "#" + inputField + " ." + errorSign;

    if (inputField) {
        $(fieldIdentifier).addClass(" has-error has-feedback");
    }

    if (errorMsg) {
        $(errorMsgIdentifier).removeClass(" hidden");
    }

    if (errorSign) {
        $(errorSignIdentifier).removeClass(" hidden");
    }
};

var disableInlineError = function (inputField, errorMsg, errorSign) {
    var fieldIdentifier = "#" + inputField;
    var errorMsgIdentifier = "#" + inputField + " ." + errorMsg;
    var errorSignIdentifier = "#" + inputField + " ." + errorSign;

    if (inputField) {
        $(fieldIdentifier).removeClass(" has-error has-feedback");
    }

    if (errorMsg) {
        $(errorMsgIdentifier).addClass(" hidden");
    }

    if (errorSign) {
        $(errorSignIdentifier).addClass(" hidden");
    }
};

function readSingleFile(evt) {
    var f = evt.target.files[0];
    if (f) {
        var r = new FileReader();
        r.onload = function(e) {
            var contents = e.target.result;
            if (f.type == "text/xml") {
                pemContent = contents;
                console.log("val ok 4 now");
            } else{
                console.log("vali faild");
                //inline error
            }
        }
        r.readAsText(f);
    } else {
        //inline error
    }
}

$(document).ready(function () {
    pemContent = "";
    document.getElementById('certificate').addEventListener('change', readSingleFile, false);

    /**
     * Following click function would execute
     * when a user clicks on "Add Certificate" button.
     */
    $("button#add-certificate-btn").click(function () {
        var serialNoInput = $("input#serialNo");
        var serialNo = serialNoInput.val();
        var errorMsgWrapper = "#certificate-create-error-msg";
        var errorMsg = "#certificate-create-error-msg span";
        if (!serialNo) {
            $(errorMsg).text("Serial Number is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!pemContent) {
            $(errorMsg).text(" .pem file must contains certificate information.");
            $(errorMsgWrapper).removeClass("hidden");
        } else {
            var addCertificateFormData = {};
            addCertificateFormData.serial = serialNo;
            addCertificateFormData.pem = pemContent;
            var addCertificateAPI = "mdm-admin/certificates/saveCertificate";

            invokerUtil.post(
                addCertificateAPI,
                addCertificateFormData,
                function (data) {
                   if (!data) {
                        // Clearing user input fields.
                        $("input#serialNo").val("");
                        $("input#certificate").val("");
                        // Refreshing with success message
                        $("#user-create-form").addClass("hidden");
                        $("#user-created-msg").removeClass("hidden");
                        generateQRCode("#user-created-msg .qr-code");
                    } else {
                        $(errorMsg).text("An unexpected error occurred at backend server. Please try again later.");
                        $(errorMsgWrapper).removeClass("hidden");
                    }
                }, function (data) {
                   if (data["status"] == 500) {
                        $(errorMsg).text("An unexpected error occurred at backend server. Please try again later.");
                    } else {
                        $(errorMsg).text(data);
                    }
                    $(errorMsgWrapper).removeClass("hidden");
                }
            );
        }
    });
});
