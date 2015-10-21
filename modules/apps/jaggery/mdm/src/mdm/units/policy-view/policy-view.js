function onRequest(context) {
    var log = new Log("policy-view-edit-unit backend js");
    log.debug("calling policy-view-edit-unit");
    var userModule = require("/modules/user.js").userModule;
    context.roles = userModule.getRoles().content;
    return context;
}