function onRequest(context){
    var activeClass = "itm-wiz-current";
    var log = new Log();
    var userAgent = request.getHeader("User-Agent");
    var UAParser = require("/modules/ua-parser.min.js").UAParser;
    var parser = new UAParser();
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();
    var viewModel = {
        "isPage1": "",
        "isPage2": "",
        "isPage3": "",
        "isPage4": ""
    };
    viewModel["isPage"+context.page] = activeClass;
    viewModel.os = os.name;
    return viewModel;
}