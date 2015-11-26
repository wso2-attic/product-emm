function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getUsers();
    context["permissions"] = userModule.getUIPermissions();
    if (response["status"] == "success") {
        context["users"] = response["content"];
        context["userListingStatusMsg"] = "Total number of Users found : " + context["users"].length;
    } else {
        context["users"] = [];
        context["userListingStatusMsg"] = "Error in retrieving user list.";
    }
    return context;
}