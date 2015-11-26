function onRequest(context){
    var mdmProps = application.get("PINCH_CONFIG");
    var companyProps = session.get("COMPANY_DETAILS");
    if (!companyProps) {
        context.browserTitle = mdmProps.generalConfig.browserTitle;
    } else {
        context.browserTitle = companyProps.browserTitle;
    }
    return context;
}