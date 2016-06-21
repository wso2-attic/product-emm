/**
 * Checks if provided input is valid against RegEx input.
 *
 * @param regExp Regular expression
 * @param inputString Input string to check
 * @returns {boolean} Returns true if input matches RegEx
 */
function inputIsValid(regExp, inputString) {
    regExp = new RegExp(regExp);
    return regExp.test(inputString);
}

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

/**
 * Validate if provided username is valid against RegEx configures.
 */
validateInline["user-name"] = function () {
    var usernameinput = $("input#username");
    if (inputIsValid( usernameinput.data("regex"), usernameinput.val())) {
       disableInlineError("usernameInputField", "usernameEmpty", "usernameError");
    } else {
        enableInlineError("usernameInputField", "usernameEmpty", "usernameError");
    }
};

/**
 * Validate if provided first name is valid against RegEx configures.
 */
validateInline["first-name"] = function () {
    var firstnameinput = $("input#firstname");
    if (firstnameinput.val()) {
       disableInlineError("firstNameField", "fnError");
    } else {
        enableInlineError("firstNameField", "fnError");
    }
};

/**
 * Validate if provided last name is valid against RegEx configures.
 */
validateInline["last-name"] = function () {
    var lastnameinput = $("input#lastname");
    if (lastnameinput.val()) {
       disableInlineError("lastNameField", "lnError");
    } else {
        enableInlineError("lastNameField", "lnError");
    }
};

/**
 * Checks if provided email address is valid against
 * the email format.
 */
validateInline["emailAddress"] = function () {
    var email = $("#emailAddress").val();
    if (!email) {
        enableInlineError("emailField", "email-required" , "emailError");
    } else if (emailIsValid(email)) {
        disableInlineError("emailField", "email-required" , "emailError");
        disableInlineError("emailField", "email-invalid" , "emailError");
    } else {
        enableInlineError("emailField", "email-invalid" , "emailError");
    }
};

/**
 * clear Validation messages when gain focus to the field.
 */
clearInline["user-name"] = function () {
       disableInlineError("usernameInputField", "usernameEmpty", "usernameError");
};

/**
 * clear Validation messages when gain focus to the field.
 */
clearInline["first-name"] = function () {
       disableInlineError("firstNameField", "fnError");
};

/**
 * clear Validation messages when gain focus to the field.
 */
clearInline["last-name"] = function () {
       disableInlineError("lastNameField", "lnError");
};


/**
 * clear Validation messages when gain focus to the field.
 */
clearInline["emailAddress"] = function () {
        disableInlineError("emailField", "email-required" , "emailError");
        disableInlineError("emailField", "email-invalid" , "emailError");
};

/**
 * Checks if an email address has the valid format or not.
 *
 * @param email Email address
 * @returns {boolean} true if email has the valid format, otherwise false.
 */
function emailIsValid(email) {
    var regExp = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
    return regExp.test(email);
}

$( "#userStore" )
    .change(function () {
        var str = "";
        $( "select option:selected" ).each(function() {
            str += $( this ).text() + " ";
        });
        var addUserAPI = "/mdm-admin/roles/"+ str;

        invokerUtil.get(
            addUserAPI,
            function (data) {
                data = JSON.parse(data);
                if (data.errorMessage) {
                    $(errorMsg).text("Selected user store prompted an error : " + data.errorMessage);
                    $(errorMsgWrapper).removeClass("hidden");
                } else if (data["statusCode"] == 200) {
                    $("#roles").empty();
                    for(i=0;i<data.responseContent.length;i++){
                        var newOption = $('<option value="'+data.responseContent[i]+'">'+data.responseContent[i]+'</option>');
                        $('#roles').append(newOption);
                    }
                }
            }
        );
    }).change();

$(document).ready(function () {
    $("#emailValidationText").hide();
    $("select.select2[multiple=multiple]").select2({
        tags: false
    });

    /**
     * Following click function would execute
     * when a user clicks on "Add User" button
     * on Add User page in WSO2 MDM Console.
     */
    $("button#add-user-btn").click(function () {

        var usernameInput = $("input#username");
        var firstnameInput = $("input#firstname");
        var lastnameInput = $("input#lastname");
        var charLimit = parseInt($("input#username").attr("limit"));
        var domain = $("#userStore").val();
        var username = usernameInput.val().trim();
        var firstname = firstnameInput.val();
        var lastname = lastnameInput.val();
        var emailAddress = $("input#emailAddress").val();
        var roles = $("select#roles").val();
        var errorMsgWrapper = "#user-create-error-msg";
        var errorMsg = "#user-create-error-msg span";
        if (!username) {
            $(errorMsg).text("Username is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!inputIsValid(usernameInput.data("regex"), username)) {
            $(errorMsg).text(usernameInput.data("errormsg"));
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!firstname) {
            $(errorMsg).text("Firstname is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!inputIsValid(firstnameInput.data("regex"), firstname)) {
            $(errorMsg).text(firstnameInput.data("errormsg"));
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!lastname) {
            $(errorMsg).text("Lastname is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!inputIsValid(lastnameInput.data("regex"), lastname)) {
            $(errorMsg).text(lastnameInput.data("errormsg"));
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!emailAddress) {
            $(errorMsg).text("Email is a required field. It cannot be empty.");
            $(errorMsgWrapper).removeClass("hidden");
        } else if (!emailIsValid(emailAddress)) {
            $(errorMsg).text("Provided email is invalid.");
            $(errorMsgWrapper).removeClass("hidden");
        } else {
            var addUserFormData = {};

            addUserFormData.username = domain + "/" + username;
            addUserFormData.firstname = firstname;
            addUserFormData.lastname = lastname;
            addUserFormData.emailAddress = emailAddress;
            addUserFormData.roles = roles;

            var addUserAPI = "/mdm-admin/users";

            invokerUtil.post(
                addUserAPI,
                addUserFormData,
                function (data) {
                    data = JSON.parse(data);
                    if (data.errorMessage) {
                        $(errorMsg).text("Selected user store prompted an error : " + data.errorMessage);
                        $(errorMsgWrapper).removeClass("hidden");
                    } else if (data["statusCode"] == 201) {
                        // Clearing user input fields.
                        $("input#username").val("");
                        $("input#firstname").val("");
                        $("input#lastname").val("");
                        $("input#email").val("");
                        $("select#roles").select2("val", "");
                        // Refreshing with success message
                        $("#user-create-form").addClass("hidden");
                        $("#user-created-msg").removeClass("hidden");
                        generateQRCode("#user-created-msg .qr-code");
                    } else if (data["status"] == 409) {
                        $(errorMsg).text(data["messageFromServer"]);
                        $(errorMsgWrapper).removeClass("hidden");
                    } else if (data["status"] == 500) {
                        $(errorMsg).text("An unexpected error occurred at backend server. Please try again later.");
                        $(errorMsgWrapper).removeClass("hidden");
                    }
                }, function (data) {
                    if (data["status"] == 409) {
                        $(errorMsg).text("User : " + username + " already exists. Pick another username.");
                    } else if (data["status"] == 500) {
                        $(errorMsg).text("An unexpected error occurred at backend server. Please try again later.");
                    } else {
                        $(errorMsg).text(data.errorMessage);
                    }
                    $(errorMsgWrapper).removeClass("hidden");
                }
            );
        }
    });

    $("#username").focus(function() {
        clearInline["user-name"]();
    });

    $("#username").blur(function() {
        validateInline["user-name"]();
    });

    $("#emailAddress").focus(function() {
        clearInline["emailAddress"]();
    });

    $("#emailAddress").blur(function() {
        validateInline["emailAddress"]();
    });

    $("#lastname").focus(function() {
        clearInline["last-name"]();
    });

    $("#lastname").blur(function() {
        validateInline["last-name"]();
    });

    $("#firstname").focus(function() {
        clearInline["first-name"]();
    });

    $("#firstname").blur(function() {
        validateInline["first-name"]();
    });
});
