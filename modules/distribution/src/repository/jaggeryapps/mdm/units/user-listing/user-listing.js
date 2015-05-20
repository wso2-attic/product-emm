function onRequest(context) {
    // var log = new Log("user-listing");
    var userModule = require("/modules/user.js").userModule;
    var allUsers = userModule.getUsers();
    var i, filteredUserList = [];
    for (i = 0; i < allUsers.length; i++) {
        if (String(allUsers[i].username) != "admin") {
            filteredUserList.push(allUsers[i]);
        }
    }
    context.users = filteredUserList;
    context.permissions = userModule.getUIPermissions();
    return context;
}