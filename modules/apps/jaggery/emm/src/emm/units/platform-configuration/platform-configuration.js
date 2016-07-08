function onRequest(context) {
    // var log = new Log("platform-configuration-unit backend js");
    var userModule = require("/modules/user.js")["userModule"];
    var result = userModule.getPlatforms();
    if (result["status"] == "success") {
        context["deviceTypes"] = result["content"].deviceTypes;
    }
    return context;
}