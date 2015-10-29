function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getRoles();
    var typesListResponse = userModule.getPlatforms();

    if (response["status"] == "success") {
        context["roles"] = response["content"];
    }
    response = userModule.getUsersByUsername();
    if (response["status"] == "success") {
        context["users"] = response["content"];
    }

    if (typesListResponse["status"] == "success") {
        context["types"] = typesListResponse["content"];
    }
    return context;
}