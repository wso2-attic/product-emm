function onRequest(context) {
    var viewModel = {};
    viewModel.rootCertificateURL = "/mdm/controller/ios/ca";
    viewModel.licenseURL = "/mdm/login-agent";
    return viewModel;
}