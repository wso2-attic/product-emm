function onRequest(context) {
    var log = new Log("login-page-agent-unit backend js");
    log.debug("calling login-page-agent-unit");

    var UAParser = require("/modules/ua-parser.min.js")["UAParser"];
    var parser = new UAParser();
    var userAgent = request.getHeader("User-Agent");
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();

    if (os.name == "Android") {
        context["header"] = "Complete MDM Registration";
    } else if (os.name == "iOS") {
        context["header"] = "Step 2. Login to EMM";
    } else if (os.name == "Windows Phone") {
        context["header"] = "Step 1. Login to EMM";
    }
    return context;
}