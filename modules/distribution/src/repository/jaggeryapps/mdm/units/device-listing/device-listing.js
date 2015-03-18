function onRequest(context){
    context.permissions = stringify([ "LIST_OWN_DEVICES"]);
    return context;
}