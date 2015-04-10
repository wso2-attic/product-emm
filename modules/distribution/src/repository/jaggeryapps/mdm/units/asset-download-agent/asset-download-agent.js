function onRequest(context){
    var mdmProps = require('/config/mdm-props.js').config();

    var log = new Log("asset-download-agent-unit");
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
        viewModel.header = "Download and install Agent";
        viewModel.link = mdmProps.httpURL + app.publicURL + "/asset/" + mdmProps.androidAgentApp;
    }else if(os.name == "iOS"){
        viewModel.header = "Step 1. Download and install Agent";
        viewModel.link = "itms-services://?action=download-manifest&url=itms-services://?" +
            "action=download-manifest&url=" + mdmProps.httpURL + app.publicURL + "/asset/" + mdmProps.iOSAgentApp;
    }
    return viewModel;
}