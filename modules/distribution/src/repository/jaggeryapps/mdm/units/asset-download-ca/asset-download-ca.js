function onRequest(context) {
    var viewModel = {};
    viewModel.rootCertificateURL = "/ios/enrollment/CA";
    viewModel.licenseURL = "/mdm/login-agent";
    return viewModel;
}