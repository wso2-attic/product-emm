function onRequest(context){
    var mdmProps = application.get("PINCH_CONFIG");
    var companyProps = session.get("COMPANY_DETAILS");
    if (!companyProps) {
        context.companyName = mdmProps.generalConfig.companyName;
        context.copyrightText = mdmProps.generalConfig.copyrightText;
    } else {
        context.companyName = companyProps.companyName;
        context.copyrightText = companyProps.copyrightText;
    }
    return context;
}