$( document ).ready(function() {

    $('select.select2').select2({
        placeholder: 'Select..'
    });

    $('select.select2[multiple=multiple]').select2({
        placeholder: 'Select..',
        tags: true
    });

});

/**
 * Checks if an email address has the valid format or not.
 *
 * @param email Email address
 * @returns {boolean} true if email has the valid format, otherwise false.
 */
var emailIsValid = function(email) {
    var atPosition = email.indexOf("@");
    var dotPosition = email.lastIndexOf(".");
    return !(atPosition < 1 || ( dotPosition - atPosition < 2 ));
};

/**
 * Following will be called when a user clicks
 * on the "Add User" button.
 */
$("button#add-user-btn").click(function(){
    //e.preventDefault();
    var username = $("input#username").val();
    var firstname = $("input#firstname").val();
    var lastname = $("input#lastname").val();
    var emailAddress = $("input#email").val();
    var userRoles = $("select#roles").val();

    if (!username) {
        $('.wr-validation-summary p').text("Username is a required field. It cannot be empty.");
        $('.wr-validation-summary').removeClass("hidden");
    } else if (!firstname) {
        $('.wr-validation-summary p').text("Firstname is a required field. It cannot be empty.");
        $('.wr-validation-summary').removeClass("hidden");
    } else if (!lastname) {
        $('.wr-validation-summary p').text("Lastname is a required field. It cannot be empty.");
        $('.wr-validation-summary').removeClass("hidden");
    } else if (!emailAddress) {
        $('.wr-validation-summary p').text("Email is a required field. It cannot be empty.");
        $('.wr-validation-summary').removeClass("hidden");
    } else if (!emailIsValid(emailAddress)) {
        $('.wr-validation-summary p').text("Email is not valid. Please enter a correct email address.");
        $('.wr-validation-summary').removeClass("hidden");
    } else {
        var addUserFormData = {};
        addUserFormData.username = username;
        addUserFormData.firstname = firstname;
        addUserFormData.lastname = lastname;
        addUserFormData.emailAddress = emailAddress;
        addUserFormData.userRoles = userRoles;

        var addUserAPI = "/mdm/api/user/add";

        $.ajax({
            type:'POST',
            url:addUserAPI,
            contentType:'application/json',
            data:JSON.stringify(addUserFormData),
            success:function(data){
                if (data == 201) {
                    $('.wr-validation-summary p').text("User created");
                } else if (data == 400) {
                    $('.wr-validation-summary p').text("Exception at backend");
                } else if (data == 403) {
                    $('.wr-validation-summary p').text("Action not permitted");
                } else if (data == 409) {
                    $('.wr-validation-summary p').text("User exists");
                }
                $('.wr-validation-summary').removeClass("hidden");
            },
            error:function(){
                $('.wr-validation-summary p').text("An unexpected error occurred");
                $('.wr-validation-summary').removeClass("hidden");
            }
        });
    }
});