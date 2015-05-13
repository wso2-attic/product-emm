function onRequest(context){
    var profiles = [{name : "Android devices"}, {name: "iOS devices"}];
    context.profiles = profiles;
    return context;
}