function onRequest(context){
    var log = new Log();
    var userAgent = request.getHeader("User-Agent");
    var UAParser = require("/modules/ua-parser.min.js").UAParser;
    var parser = new UAParser();
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();
    var viewModel = context;
    viewModel.link = os.name;
    if(os.name == "Android"){
        log.info("Android detected");
        viewModel.header = "Complete MDM Registration";
    }else if(os.name == "iOS"){
        viewModel.header = "Step 4. Complete MDM Registration";
    }else if(os.name == "Windows Phone"){
        viewModel.header = "Step 2. Complete MDM Registration";
    }
    return viewModel;
}