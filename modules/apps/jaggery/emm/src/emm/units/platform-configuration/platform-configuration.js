function onRequest(context) {
    // var log = new Log("platform-configuration-unit backend js");
    var userModule = require("/modules/user.js")["userModule"];
    var typesListResponse = userModule.getPlatforms();
    if (typesListResponse["status"] == "success") {
        context["types"] = typesListResponse["content"];
    }
    return context;
}