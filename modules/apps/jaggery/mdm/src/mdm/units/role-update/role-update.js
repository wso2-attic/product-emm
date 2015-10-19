/**
 * Returns the dynamic state to be populated by add-user page.
 * 
 * @param context Object that gets updated with the dynamic state of this page to be presented
 * @returns {*} A context object that returns the dynamic state of this page to be presented
 */
function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];

    var uri = request.getRequestURI();
    var uriMatcher = new URIMatcher(String(uri));
    var isMatched = uriMatcher.match("/{context}/roles/edit-role/{rolename}");

    if (isMatched) {
        var matchedElements = uriMatcher.elements();
        var roleName = matchedElements.rolename;
        var response = userModule.getRole(roleName);
        if (response["status"] == "success") {
            context["role"] = response["content"];
        }
        var userStores = userModule.getSecondaryUserStores();
        context["userStores"] = userStores;
    }
    //TODO: error scenario
    return context;
}