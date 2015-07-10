function onRequest(context){
    var username = request.getParameter("login_hint");
    if (username){
        context.username = username;
    }
    return context;
}
