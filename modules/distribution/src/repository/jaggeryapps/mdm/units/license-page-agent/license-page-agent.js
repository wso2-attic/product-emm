function onRequest(context){
    var log = new Log();
    var userAgent = request.getHeader("User-Agent");
    var UAParser = require("/modules/ua-parser.min.js").UAParser;
    var parser = new UAParser();
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();
    var viewModel = context;
    viewModel.link = os.name; if(os.name == "iOS"){
        viewModel.header = "Step 3. Login to Mobile Device Manager";
    }else if(os.name == "Windows Phone"){
        viewModel.header = "Step 1. Login to Mobile Device Manager";
    }
    return viewModel;
}