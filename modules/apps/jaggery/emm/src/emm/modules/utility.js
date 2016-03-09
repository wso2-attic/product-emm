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

var utility;
utility = function () {
    // var log = new Log("modules/utility.js");
    var JavaClass = Packages.java.lang.Class;
    var PrivilegedCarbonContext = Packages.org.wso2.carbon.context.PrivilegedCarbonContext;

    var getOsgiService = function (className) {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(JavaClass.forName(className));
    };

    var publicMethods = {};

    publicMethods.startTenantFlow = function (userInfo) {
        var context, carbon = require('carbon');
        PrivilegedCarbonContext.startTenantFlow();
        context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        context.setTenantDomain(carbon.server.tenantDomain({
            tenantId: userInfo.tenantId
        }));
        context.setTenantId(userInfo.tenantId);
        context.setUsername(userInfo.username || null);
    };

    publicMethods.endTenantFlow = function () {
        PrivilegedCarbonContext.endTenantFlow();
    };

    publicMethods.getDeviceManagementService = function () {
        return getOsgiService('org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService');
    };

    publicMethods.getUserManagementService = function () {
        return getOsgiService("org.wso2.carbon.device.mgt.user.core.UserManager");
    };

    publicMethods.getPolicyManagementService = function () {
        return getOsgiService("org.wso2.carbon.policy.mgt.core.PolicyManagerService");
    };
    publicMethods.insertAppPermissions = function (userModule, type) {
        // Below are the 2 types of users:- Normal users and Admins
        userModule.addPermissions([{key: "emm-admin", name: "Device Management Admin"}], "device-mgt", type);
        userModule.addPermissions([{key: "user", name: "Device Management User"}], "device-mgt", type);

        // adding permission definitions for device-mgt/admin
        userModule.addPermissions([{key: "dashboard", name: "Dashboard"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "dashboard/view", name: "View Dashboard"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "operations", name: "Operations"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "operations/applications", name: "MAM"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "devices", name: "Devices"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "devices/list", name: "List All Devices"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "devices/view", name: "View Device"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "users", name: "Users"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/add", name: "Add New Users"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/invite", name: "Invite Users"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/list", name: "List Users"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/remove", name: "Remove Users"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/update", name: "Update Users"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/view", name: "View User"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "users/password-reset", name: "Reset Password"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "roles", name: "Roles"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "roles/add", name: "Add New Roles"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "roles/list", name: "List Roles"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "roles/remove", name: "Remove Roles"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "roles/update", name: "Update Role"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "policies", name: "Policy"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "policies/add", name: "Add Policy"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "policies/list", name: "List Policy"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "policies/update", name: "Edit Policy"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "policies/remove", name: "Remove Policy"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "policies/priority", name: "Policy Priority"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "notifications", name: "Notifications"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "notifications/add", name: "Add Notifications"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "notifications/view", name: "List Notifications"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "notifications/update", name: "Edit Notifications"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "notifications/remove", name: "Remove Notifications"}], "device-mgt/emm-admin", type);

        userModule.addPermissions([{key: "platform-configs", name: "Platform Configurations"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "platform-configs/view", name: "View Configurations"}], "device-mgt/emm-admin", type);
        userModule.addPermissions([{key: "platform-configs/modify", name: "Modify Configurations"}], "device-mgt/emm-admin", type);

        // adding permission definitions for device-mgt/user
        userModule.addPermissions([{key: "devices", name: "Devices"}], "device-mgt/user", type);
        userModule.addPermissions([{key: "enroll", name: "Enroll"}], "device-mgt/user", type);
        userModule.addPermissions([{key: "devices/list", name: "List Individual Devices"}], "device-mgt/user", type);
        userModule.addPermissions([{key: "devices/view", name: "View Devices"}], "device-mgt/user", type);

    };
    return publicMethods;

}();
