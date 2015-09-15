function onRequest(context) {
    var log = new Log("login-box-enrollment-unit backend js");
    log.debug("calling login-box-enrollment-unit");

    var UAParser = require("/modules/ua-parser.min.js")["UAParser"];
    var parser = new UAParser();
    var userAgent = request.getHeader("User-Agent");
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();
    if (os.name == "Windows Phone") {
        var action = request.getParameter("appru");
        if (action) {
            session.put("windows_action", action);
        }
    }
    return context;
}