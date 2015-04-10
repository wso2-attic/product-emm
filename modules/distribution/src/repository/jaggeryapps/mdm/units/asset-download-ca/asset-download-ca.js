function onRequest(context) {
    var mdmProps = require('/config/mdm-props.js').config();
    var viewModel = {};
    viewModel.rootCertificateURL = mdmProps.httpsURL + mdmProps.appContext + "controller/ios/ca";
    return viewModel;
}