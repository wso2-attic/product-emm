function UserInfo (username, firstName, lastName, email) {
    this.username = username;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    var log = new Log();
    if (firstName != null && lastName != null) {
        this.name = firstName + " " + lastName;
    } else if (firstName == null && lastName != null) {
        this.name = lastName;
    } else if (lastName == null && firstName != null) {
        this.name = firstName;
    } else {
        this.name = username;
    }
}

UserInfo.prototype.getUsername = function() {
    return this.username;
};

UserInfo.prototype.getFirstName = function() {
    return this.firstName;
};

UserInfo.prototype.getLastName = function() {
    return this.lastName;
};

UserInfo.prototype.getEmail = function() {
    return this.email;
};

UserInfo.prototype.getName = function() {
    return this.name;
};




