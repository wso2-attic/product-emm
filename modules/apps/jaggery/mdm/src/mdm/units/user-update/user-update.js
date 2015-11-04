/**
 * Returns a context with the user object to be populated by the edit-user page.
 *
 * @param context Object that gets updated with the dynamic state of this page to be presented
 * @returns {*} A context object that returns the dynamic state of this page to be presented
 */
function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];

    var uri = request.getRequestURI();
    var userName = request.getParameter("username");

    if (userName) {
        var response = userModule.getUser(userName);

        if (response["status"] == "success") {
            context["editUser"] = response["content"];
        }

        response = userModule.getRolesByUsername(userName);
        if (response["status"] == "success") {
            context["userRoles"] = response["content"];
        }
    }
    return context;
}