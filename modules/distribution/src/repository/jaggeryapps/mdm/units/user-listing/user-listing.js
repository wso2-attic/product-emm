function onRequest(context) {
    // var log = new Log("user-listing");
    var userModule = require("/modules/user.js").userModule;
    var allUsers = userModule.getUsers();

    context.users = allUsers;
    context.userCount = allUsers.length;
    context.userListingStatus = "Total number of Users found : " + allUsers.length;
    context.permissions = userModule.getUIPermissions();

    return context;
}