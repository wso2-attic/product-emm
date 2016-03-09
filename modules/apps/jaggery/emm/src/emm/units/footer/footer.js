function onRequest(context){
    var mdmProps = application.get("PINCH_CONFIG");
    var companyProps = session.get("COMPANY_DETAILS");
    if (!companyProps) {
        context.companyName = mdmProps.generalConfig.companyName;
        context.copyrightPrefix = mdmProps.generalConfig.copyrightPrefix;
        context.copyrightOwner = mdmProps.generalConfig.copyrightOwner;
        context.copyrightOwnersSite = mdmProps.generalConfig.copyrightOwnersSite;
        context.copyrightSuffix = mdmProps.generalConfig.copyrightSuffix;
    } else {
        context.companyName = companyProps.companyName;
        context.copyrightPrefix = companyProps.copyrightPrefix;
        context.copyrightOwner = companyProps.copyrightOwner;
        context.copyrightOwnersSite = companyProps.copyrightOwnersSite;
        context.copyrightSuffix = companyProps.copyrightSuffix;
    }
    return context;
}