function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getRoles();
    if (response["status"] == "success") {
        context["roles"] = response["content"];
    }
    response = userModule.getUsersByUsername();
    if (response["status"] == "success") {
        context["users"] = response["content"];
    }
    return context;
}