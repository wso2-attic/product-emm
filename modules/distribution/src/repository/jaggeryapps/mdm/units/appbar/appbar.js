function onRequest(context) {
    var userModule = require("/modules/user.js").userModule;
    var permissions = userModule.getUIPermissions();
    context.permissions = permissions;
    var links = {
        "users": [],
        "policies": [],
        "profiles": [],
        "device-mgt": []
    };
    var dashboardLink = {
        title: "Back to Dashboard",
        icon: "fw-left-arrow",
        url: "/mdm"
    };
    if (permissions.DASHBOARD_VIEW){
        links.users.push(dashboardLink);
        links.policies.push(dashboardLink);
        links.profiles.push(dashboardLink);
        links['device-mgt'].push(dashboardLink);
    }
    if (permissions.ADD_USER) {
        links.users.push({
            title: "Add User",
            icon: "fw-add",
            url: "/mdm/users/add-user"
        });
    }
    if (permissions.ADD_POLICY) {
        links.policies.push({
            title: "Add Policy",
            icon: "fw-add",
            url: "/mdm/policies/add-policy"
        });
    }
    context.currentActions = links[context.link];
    return context;
}