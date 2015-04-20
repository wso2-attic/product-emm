function onRequest(context) {
    var userAgent = request.getHeader("User-Agent");
    var UAParser = require("/modules/ua-parser.min.js").UAParser;
    var parser = new UAParser();
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();
    if (os.name == "Windows Phone"){
        var action = request.getParameter("appru");
        if(action){
            session.put("windows_action", action);
        }
    }
    return context;
}