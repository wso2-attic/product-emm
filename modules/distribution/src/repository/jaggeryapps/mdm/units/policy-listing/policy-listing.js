function onRequest(context) {
    var policies = [{id: "1", name: "Dev team policy"}, {id: "2", name: "Sales team policy"}];
    context.policies = policies;
    return context;
}