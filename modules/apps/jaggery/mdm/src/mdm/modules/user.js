/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * This module contains user and roles related functionality
 */
var userModule = function () {
    var log = new Log("modules/user.js");

    var constants = require("/modules/constants.js");
    var utility = require("/modules/utility.js")["utility"];
    var mdmProps = require('/config/mdm-props.js').config();
    var serviceInvokers = require("/modules/backend-service-invoker.js").backendServiceInvoker;

    /* Initializing user manager */
    var carbon = require('carbon');
    var tenantId = carbon.server.tenantId();
    var url = carbon.server.address('https') + "/admin/services";
    var server = new carbon.server.Server(url);
    var userManager = new carbon.user.UserManager(server, tenantId);

    var deviceManagementService = utility.getDeviceManagementService();
    var EmailMessageProperties = Packages.org.wso2.carbon.device.mgt.common.EmailMessageProperties;

    var publicMethods = {};
    var privateMethods = {};

    /**
     *  Get the carbon user object from the session. If not found - it will throw a user not found error.
     * @returns {carbon user object}
     */
    privateMethods.getCarbonUser = function () {
        var carbon = require('carbon');
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        var utility = require('/modules/utility.js').utility;
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants.ERRORS.USER_NOT_FOUND;
        }
        return carbonUser;
    }

    /**
     * Only GET method is implemented for now since there are no other type of methods used this method.
     * @param url - URL to call the backend without the host
     * @param method - HTTP Method (GET, POST)
     * @returns {
     *  'status': 'success'|'error',
     *  'content': {}
     * }
     */
    privateMethods.callBackend = function (url, method) {
        switch (method) {
            case constants.HTTP_GET:
                var response = serviceInvokers.XMLHttp.get(url, function (responsePayload) {
                        var response = {};
                        response.content = responsePayload["responseContent"];
                        if(responsePayload["responseContent"] == null && responsePayload != null){
                            response.content = responsePayload;
                        }
                        response.status = "success";
                        return response;
                    },
                    function (responsePayload) {
                        var response = {};
                        response.content = responsePayload;
                        response.status = "error";
                        return response;
                    });
                return response;
                break;
            case constants.HTTP_POST:
                //todo
                log.error("programing error");
                break;
            case constants.HTTP_PUT:
                //todo
                log.error("programing error");
                break;
            case constants.HTTP_DELETE:
                //todo
                log.error("programing error");
                break;
        }
    }

    /*
     @Deprecated
     */
    /**
     * Add user to mdm-user-store.
     *
     * @param username Username of the user
     * @param firstname First name of the user
     * @param lastname Last name of the user
     * @param emailAddress Email address of the user
     * @param userRoles Roles assigned to the user
     *
     * @returns {number} HTTP Status code 201 if succeeded, 409 if user already exists
     */
    publicMethods.addUser = function (username, firstname, lastname, emailAddress, userRoles) {
        var statusCode, carbon = require('carbon');
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        var utility = require('/modules/utility.js').utility;
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants.ERRORS.USER_NOT_FOUND;
        }
        try {
            utility.startTenantFlow(carbonUser);
            var tenantId = carbon.server.tenantId();
            var userManager = new carbon.user.UserManager(server, tenantId);
            if (userManager.userExists(username)) {
                if (log.isDebugEnabled()) {
                    log.debug("A user with name '" + username + "' already exists.");
                }
                // http status code 409 refers to - conflict.
                statusCode = 409;
            } else {
                var initialUserPassword = privateMethods.generateInitialUserPassword();
                var defaultUserClaims = privateMethods.buildDefaultUserClaims(firstname, lastname, emailAddress);

                userManager.addUser(username, initialUserPassword, userRoles, defaultUserClaims, "default");
                privateMethods.inviteUserToEnroll(username, initialUserPassword);
                if (log.isDebugEnabled()) {
                    log.debug("A new user with name '" + username + "' was created.");
                }
                // http status code 201 refers to - created.
                statusCode = 201;
            }
            return statusCode;
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /*
     @Deprecated
     */
    /**
     * Remove an existing user from mdm-user-store.
     *
     * @param username Username of the user
     * @returns {number} HTTP Status code 200 if succeeded, 409 if the user does not exist
     */
    publicMethods.removeUser = function (username) {
        var statusCode, carbon = require('carbon');
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        var utility = require('/modules/utility.js').utility;
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants.ERRORS.USER_NOT_FOUND;
        }
        try {
            utility.startTenantFlow(carbonUser);
            var tenantId = carbon.server.tenantId();
            var userManager = new carbon.user.UserManager(server, tenantId);
            if (userManager.userExists(username)) {
                userManager.removeUser(username);
                if (log.isDebugEnabled()) {
                    log.debug("An existing user with name '" + username + "' was removed.");
                }
                // http status code 200 refers to - success.
                statusCode = 200;
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("A user with name '" + username + "' does not exist to remove.");
                }
                // http status code 409 refers to - conflict.
                statusCode = 409;
            }
            return statusCode;
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /*
     @Deprecated
     */
    /**
     * Private method to be used by addUser() to
     * generate an initial user password for a user.
     * This will be the password used by a user for his initial login to the system.
     *
     * @returns {string} Initial User Password
     */
    privateMethods.generateInitialUserPassword = function () {
        var passwordLength = 6;
        //defining the pool of characters to be used for initial password generation
        var lowerCaseCharset = "abcdefghijklmnopqrstuvwxyz";
        var upperCaseCharset = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var numericCharset = "0123456789";

        var totalCharset = lowerCaseCharset + upperCaseCharset + numericCharset;
        var totalCharsetLength = totalCharset.length;

        var initialUserPassword = "";
        for (var i = 0; i < passwordLength; ++i) {
            initialUserPassword += totalCharset.charAt(Math.floor(Math.random() * totalCharsetLength));
        }
        if (log.isDebugEnabled()) {
            log.debug("Initial password created for new user : " + initialUserPassword);
        }
        return String(initialUserPassword);
    };

    /*
     @Deprecated
     */
    /**
     * Build default user claims.
     *
     * @param firstname First name of the user
     * @param lastname Last name of the user
     * @param emailAddress Email address of the user
     *
     * @returns {Object} Default user claims to be provided
     */
    privateMethods.buildDefaultUserClaims = function (firstname, lastname, emailAddress) {
        var defaultUserClaims = {
            "http://wso2.org/claims/givenname": firstname,
            "http://wso2.org/claims/lastname": lastname,
            "http://wso2.org/claims/emailaddress": emailAddress
        };
        if (log.isDebugEnabled()) {
            log.debug("ClaimMap created for new user : " + stringify(defaultUserClaims));
        }
        return defaultUserClaims;
    };

    /*
     @Deprecated
     */
    /**
     * Send an initial invitation email to a user with username/password attached
     * for the very-first enrollment with WSO2 MDM.
     *
     * @param username Username of the user
     * @param password Password of the user
     */
    privateMethods.inviteUserToEnroll = function (username, password) {
        var carbon = require('carbon');
        var enrollmentURL = mdmProps.httpsURL + mdmProps.appContext + "download-agent";
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        var utility = require('/modules/utility.js').utility;
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants.ERRORS.USER_NOT_FOUND;
        }
        //var user = userManagementService.getUser(username, carbonUser.tenantId);
        try {
            utility.startTenantFlow(carbonUser);
            var tenantId = carbon.server.tenantId();
            var userManager = new carbon.user.UserManager(server, tenantId);
            var emailTo = [];
            var user = userManager.getUser(username);
            emailTo[0] = privateMethods.getEmail(username, userManager);
            var emailMessageProperties = new EmailMessageProperties();
            emailMessageProperties.setMailTo(emailTo);
            emailMessageProperties.setFirstName(privateMethods.getFirstName(username, userManager));
            emailMessageProperties.setUserName(username);
            emailMessageProperties.setPassword(password);
            emailMessageProperties.setEnrolmentUrl(enrollmentURL);
            deviceManagementService.sendRegistrationEmail(emailMessageProperties);
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /*
     @Deprecated
     */
    privateMethods.getEmail = function (username, userManager) {
        return userManager.getClaim(username, "http://wso2.org/claims/emailaddress", null)
    };

    /*
     @Deprecated
     */
    privateMethods.getFirstName = function (username, userManager) {
        return userManager.getClaim(username, "http://wso2.org/claims/givenname", null)
    };

    /*
     @Deprecated
     */
    privateMethods.getLastName = function (username, userManager) {
        return userManager.getClaim(username, "http://wso2.org/claims/lastname", null)
    };

    /*
     @Deprecated
     */
    publicMethods.inviteUser = function (username) {
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        var utility = require('/modules/utility.js').utility;
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants.ERRORS.USER_NOT_FOUND;
        }
        var enrollmentURL = mdmProps.httpsURL + mdmProps.appContext + "download-agent";

        try {
            utility.startTenantFlow(carbonUser);
            var tenantId = carbon.server.tenantId();
            var userManager = new carbon.user.UserManager(server, tenantId);
            var user = userManager.getUser(username);
            var emailProperties = new EmailMessageProperties();
            var emailTo = [];
            emailTo[0] = privateMethods.getEmail(username, userManager);
            emailProperties.setMailTo(emailTo);
            //emailProperties.setFirstName(user.getFirstName());
            emailProperties.setFirstName(privateMethods.getFirstName(username, userManager));
            emailProperties.setEnrolmentUrl(enrollmentURL);
            deviceManagementService.sendEnrolmentInvitation(emailProperties);
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /*
     @Updated
     */
    publicMethods.getUsers = function () {
        var carbonUser = session.get(constants["USER_SESSION_KEY"]);
        var utility = require("/modules/utility.js")["utility"];
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants["ERRORS"]["USER_NOT_FOUND"];
        }
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/users";
            return privateMethods.callBackend(url, constants.HTTP_GET);

        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /**
     * Return a User object from the backend by calling the JAX-RS
     * @param username
     * @returns {
     *  'status': 'success'|'error',
     *  'content': {
            "username": "abc",
            "firstname": "abc",
            "lastname": "efj",
            "emailAddress": "abc@abc.com"
        }
     * }
     */
    publicMethods.getUser = function (username) {
        var carbonUser = privateMethods.getCarbonUser();
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/users/view?username=" + username;
            var response = privateMethods.callBackend(url, constants.HTTP_GET);
            response["userDomain"] = carbonUser.domain;
            return response;
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };
    /**
     * TODO: comment
     * @param username
     * @returns {*}
     */
    publicMethods.getRolesByUsername = function (username) {
        var carbonUser = privateMethods.getCarbonUser();
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/users/roles?username=" + username;
            var response = privateMethods.callBackend(url, constants.HTTP_GET);
            return response;
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    }

    /*
     @NewlyAdded
     */
    publicMethods.getUsersByUsername = function () {
        var carbonUser = session.get(constants["USER_SESSION_KEY"]);
        var utility = require("/modules/utility.js")["utility"];
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants["ERRORS"]["USER_NOT_FOUND"];
        }
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/users/users-by-username";
            return privateMethods.callBackend(url, constants.HTTP_GET)
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /*
     @Updated
     */
    /**
     * Get User Roles from user store (Internal roles not included).
     */
    publicMethods.getRoles = function () {
        var carbonUser = session.get(constants["USER_SESSION_KEY"]);
        var utility = require('/modules/utility.js')["utility"];
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants["ERRORS"]["USER_NOT_FOUND"];
        }
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/roles";
            return privateMethods.callBackend(url, constants.HTTP_GET);
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /**
     * Get Platforms.
     */
    publicMethods.getPlatforms = function () {
        var carbonUser = session.get(constants["USER_SESSION_KEY"]);
        var utility = require('/modules/utility.js')["utility"];
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants["ERRORS"]["USER_NOT_FOUND"];
        }
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/devices/types";
            return privateMethods.callBackend(url, constants.HTTP_GET);
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };
    /*
     @Updated
     */
    /**
     * Get role
     */
    publicMethods.getRole = function (roleName) {
        var carbonUser = session.get(constants["USER_SESSION_KEY"]);
        var utility = require('/modules/utility.js')["utility"];
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants["ERRORS"]["USER_NOT_FOUND"];
        }
        try {
            utility.startTenantFlow(carbonUser);
            var url = mdmProps["httpsURL"] + "/mdm-admin/roles/" + roleName;
            var response = privateMethods.callBackend(url, constants.HTTP_GET);
            return response;
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    /**
     * Authenticate a user when he or she attempts to login to MDM.
     *
     * @param username Username of the user
     * @param password Password of the user
     * @param successCallback Function to be called at the event of successful authentication
     * @param failureCallback Function to be called at the event of failed authentication
     */
    publicMethods.login = function (username, password, successCallback, failureCallback) {
        var carbonModule = require("carbon");
        var carbonServer = application.get("carbonServer");
        try {
            // check if the user is an authenticated user.
            var isAuthenticated = carbonServer.authenticate(username, password);
            if (isAuthenticated) {
                var tenantUser = carbonModule.server.tenantUser(username);
                session.put(constants.USER_SESSION_KEY, tenantUser);
                successCallback(tenantUser);
            } else {
                failureCallback();
            }
        } catch (e) {
            throw e;
        }
    };

    publicMethods.logout = function (successCallback) {
        session.invalidate();
        successCallback();
    };

    publicMethods.isAuthorized = function (permission) {
        var carbon = require("carbon");
        var carbonServer = application.get("carbonServer");
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        var utility = require('/modules/utility.js').utility;
        if (!carbonUser) {
            log.error("User object was not found in the session");
            response.sendError(401, constants.ERRORS.USER_NOT_FOUND);
            exit();
        }

        try {
            utility.startTenantFlow(carbonUser);
            var tenantId = carbon.server.tenantId();
            var userManager = new carbon.user.UserManager(server, tenantId);
            var user = new carbon.user.User(userManager, carbonUser.username);
            return user.isAuthorized(permission, "ui.execute");
        } catch (e) {
            throw e;
        } finally {
            utility.endTenantFlow();
        }
    };

    publicMethods.getUIPermissions = function () {
        var permissions = {};
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/devices/list") ||
            publicMethods.isAuthorized("/permission/admin/device-mgt/user/devices/list")) {
            permissions["LIST_DEVICES"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/users/list")) {
            permissions["LIST_USERS"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/roles/list")) {
            permissions["LIST_ROLES"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/policies/list")) {
            permissions["LIST_POLICIES"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/users/add")) {
            permissions["ADD_USER"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/roles/add")) {
            permissions["ADD_ROLE"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/policies/add")) {
            permissions["ADD_POLICY"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/policies/priority")) {
            permissions["CHANGE_POLICY_PRIORITY"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/dashboard/view")) {
            permissions["VIEW_DASHBOARD"] = true;
        }
        if (publicMethods.isAuthorized("/permission/admin/device-mgt/emm-admin/platform-configs/view")) {
            permissions["TENANT_CONFIGURATION"] = true;
        }

        return permissions;
    };

    publicMethods.addPermissions = function (permissionList, path, init) {
        var registry, carbon = require("carbon");
        var carbonServer = application.get("carbonServer");
        var utility = require('/modules/utility.js').utility;
        var options = {system: true};
        if (init == "login") {
            try {
                var carbonUser = session.get(constants.USER_SESSION_KEY);
                if (!carbonUser) {
                    log.error("User object was not found in the session");
                    throw constants.ERRORS.USER_NOT_FOUND;
                }
                utility.startTenantFlow(carbonUser);
                var tenantId = carbon.server.tenantId();
                if (carbonUser) {
                    options.tenantId = tenantId;
                }
                registry = new carbon.registry.Registry(carbonServer, options);
                var i, permission, resource;
                for (i = 0; i < permissionList.length; i++) {
                    permission = permissionList[i];
                    resource = {
                        collection: true,
                        name: permission.name,
                        properties: {
                            name: permission.name
                        }
                    };
                    if (path != "") {
                        registry.put("/_system/governance/permission/admin/" + path + "/" + permission.key, resource);
                    } else {
                        registry.put("/_system/governance/permission/admin/" + permission.key, resource);
                    }
                }
            } catch (e) {
                throw e;
            } finally {
                utility.endTenantFlow();
            }
        } else {
            registry = new carbon.registry.Registry(carbonServer, options);
            var i, permission, resource;
            for (i = 0; i < permissionList.length; i++) {
                permission = permissionList[i];
                resource = {
                    collection: true,
                    name: permission.name,
                    properties: {
                        name: permission.name
                    }
                };
                if (path != "") {
                    registry.put("/_system/governance/permission/admin/" + path + "/" + permission.key, resource);
                } else {
                    registry.put("/_system/governance/permission/admin/" + permission.key, resource);
                }
            }
        }
    };

    /**
     * Private method to be used by addUser() to
     * retrieve secondary user stores.
     * This needs Authentication since the method access admin services.
     *
     * @returns {string array} Array of secondary user stores.
     */
    publicMethods.getSecondaryUserStores = function () {
        var returnVal = [];
        var endpoint = mdmProps.adminService + constants.USER_STORE_CONFIG_ADMIN_SERVICE_END_POINT;
        var wsPayload = "<xsd:getSecondaryRealmConfigurations  xmlns:xsd='http://org.apache.axis2/xsd'/>";
        serviceInvokers.WS.soapRequest(
            "urn:getSecondaryRealmConfigurations", endpoint, wsPayload, function (wsResponse) {
                var domainIDs = stringify(wsResponse.*::['return']. *::domainId.text());
                if (domainIDs != "\"\"") {
                    var regExpForSearch = new RegExp(constants.USER_STORES_NOISY_CHAR, "g");
                    domainIDs = domainIDs.replace(regExpForSearch, "");
                    returnVal = domainIDs.split(constants.USER_STORES_SPLITTING_CHAR);
                }
            }, function (e) {
                log.error("Error retrieving secondary user stores", e);
            }, constants.SOAP_VERSION);
        return returnVal;
    };
    return publicMethods;
}();
