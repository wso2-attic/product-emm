/**
 * Returns a context with the user object to be populated by the edit-user page.
 *
 * @param context Object that gets updated with the dynamic state of this page to be presented
 * @returns {*} A context object that returns the dynamic state of this page to be presented
 */
function onRequest(context) {
    var userModule = require("/modules/user.js")["userModule"];
    var userName = request.getParameter("username");

    if (userName) {
        var response = userModule.getUser(userName);

        if (response["status"] == "success") {
            context["user"] = response["content"];
            context["user"].domain = response["userDomain"];
        }

        response = userModule.getRolesByUsername(userName);
        if (response["status"] == "success") {
            context["userRoles"] = response["content"];
        }
    }

    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/users/update")){
        context["editPermitted"] = true;
    }

    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/devices/list")){
        context["listDevicePermitted"] = true;
    }

    if (userModule.isAuthorized("/permission/admin/device-mgt/emm-admin/devices/view")){
        context["viewDevicePermitted"] = true;
    }

    return context;
}