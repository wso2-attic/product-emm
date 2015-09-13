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
        context["header"] = "Complete EMM Registration";
    } else if (os.name == "iOS") {
        context["header"] = "Step 2: Login to Enterprise Mobility Manager";
    } else if (os.name == "Windows Phone") {
        context["header"] = "Step 1: Login to Enterprise Mobility Manager";
    }
    return context;
}