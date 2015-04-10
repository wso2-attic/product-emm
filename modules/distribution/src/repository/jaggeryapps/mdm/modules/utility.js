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

var utility = (function () {
    var module = {};
    var PrivilegedCarbonContext = Packages.org.wso2.carbon.context.PrivilegedCarbonContext;
    var Class = java.lang.Class;

    var osgiService = function (clazz) {
        return PrivilegedCarbonContext.getThreadLocalCarbonContext().getOSGiService(Class.forName(clazz));
    };
    module.getDeviceManagementService = function () {
        return osgiService('org.wso2.carbon.device.mgt.core.service.DeviceManagementService');
    };
    module.getUserManagementService = function(){
        return osgiService('org.wso2.carbon.device.mgt.user.core.service.UserManagementService');
    };
    module.insertAppPermissions = function (userModule, type) {
        userModule.addPermissions([{key: "device-mgt/", name: "Device Management"}], "", type);
        userModule.addPermissions([{key: "device-mgt/admin", name: "Device Management Admin"}], "", type);
        userModule.addPermissions([{key: "device-mgt/user", name: "Device Management User"}], "", type);

        userModule.addPermissions([{key: "devices", name: "Device"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "users", name: "User"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "devices", name: "Device"}], "device-mgt/user", type);

        userModule.addPermissions([{key: "devices/list", name: "List all Devices"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "devices/operation", name: "Perform operations"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "users/list", name: "List Users"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "users/invite", name: "Invite Users"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "users/add", name: "Add New Users"}], "device-mgt/admin", type);
        userModule.addPermissions([{key: "users/remove", name: "Remove Users"}], "device-mgt/admin", type);

        userModule.addPermissions([{key: "devices/list", name: "List own Devices"}], "device-mgt/user", type);

        userModule.addPermissions([{
            key: "devices/operation",
            name: "Perform operations on own devices"
        }], "device-mgt/user", "init");
    }
    return module;
}());


