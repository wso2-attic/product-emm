/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

var userModule = (function () {
    var module = {};
    var log = new Log();
    var constants = require("/modules/constants.js");
    module.login = function(username, password, successCallback, failureCallback){
        var carbonModule = require("carbon");
        var carbonServer = application.get("carbonServer");
        username = username + "@" + carbonModule.server.tenantDomain();
        try {
            var authState = carbonServer.authenticate(username, password);
            log.info(username);

            delete password;
            if (authState){
                var carbonUser = carbonModule.server.tenantUser(username);
                session.put(constants.USER_SESSION_KEY, carbonUser);
                successCallback(carbonUser);
            }else{
                failureCallback();
                exit();
            }
        }catch (e) {
            if(log.isDebugEnabled()){
                log.debug(e);
            }
            throw e;
        }
    };
    module.addPermissions = function(permissionList, path, init){
        var carbonModule = require("carbon");
        var carbonServer = application.get("carbonServer");
        var options = {system: true};
        if (init == "login") {
            var carbonUser = session.get(constants.USER_SESSION_KEY);
            if (carbonUser) {
                options.tenantId = carbonUser.tenantId;
            }
        }
        var registry = new carbonModule.registry.Registry(carbonServer, options);
        for(var index in permissionList){
            var permission = permissionList[index];
            var resource = {
                collection : true,
                name : permission.name,
                properties: {
                    name : permission.name
                }
            };
            registry.put("/_system/governance/permission/" + path + "/" + permission.key, resource);
        }
    };
    module.inviteUser = function(username) {
        log.info("User invited");
    };
    module.getUsers = function(){
        var users = [{
            "username": "dulitha",
            "email": "dulitha@wso2.com",
            "name": "Dulitha Wijewantha"
        }, {"username": "dulitha", "email": "dulitha@wso2.com", "name": "Dulitha Wijewantha"}];
        return users;
    };
    module.isAuthorized = function(permission){
        var carbonModule = require("carbon");
        var carbonServer = application.get("carbonServer");
        var carbonUser = session.get(constants.USER_SESSION_KEY);
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants.ERRORS.USER_NOT_FOUND;
        }
        var userManager = new carbonModule.user.UserManager(carbonServer, carbonUser.tenantId);
        var user = new carbonModule.user.User(userManager, carbonUser.username);
        return user.isAuthorized(permission, "ui.execute");
    };
    module.logout = function(successCallback){
        session.invalidate();
        successCallback();
    };
    return module;
}());


