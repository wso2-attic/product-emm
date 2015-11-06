function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getRoles();
    if (response["status"] == "success") {
        context["roles"] = response["content"];
    }
    var typesListResponse = userModule.getPlatforms();
    if (typesListResponse["status"] == "success") {
        context["types"] = typesListResponse["content"];
    }
    return context;
}