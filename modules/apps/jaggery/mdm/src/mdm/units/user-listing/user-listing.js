function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getUsers();
    if(response["content"] != null) {
        for(var i=0; i<response["content"].length; i++){
            response["content"][i].userid = response["content"][i].username.replace(/[^\w\s]/gi, '');
        }
    }
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