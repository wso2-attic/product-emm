function onRequest(context){
    var activeClass = "itm-wiz-current";
    var log = new Log();
    var viewModel = {
        "isPage1": "",
        "isPage2": "",
        "isPage3": "",
        "isPage4": ""
    };
    log.info(context);
    viewModel["isPage"+context.page] = activeClass;
    log.info("******"+stringify(viewModel));
    return viewModel;
}