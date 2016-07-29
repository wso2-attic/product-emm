/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mdm.qsg;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.wso2.mdm.qsg.dto.EMMConfig;
import org.wso2.mdm.qsg.dto.HTTPResponse;
import org.wso2.mdm.qsg.utils.Constants;
import org.wso2.mdm.qsg.utils.HTTPInvoker;

import java.util.HashMap;

/**
 * This class holds the user-mgt related operations like user-create, role-create and change-password.
 */
public class UserOperations {

    public static boolean createUser(String username, String email, boolean isAdmin) {
        HashMap<String, String> headers = new HashMap<String, String>();
        String userEndpoint = EMMConfig.getInstance().getEmmHost() + "/api/device-mgt/v1.0/users";
        //Set the user payload
        JSONObject userData = new JSONObject();
        userData.put("username", username);
        userData.put("emailAddress", email);
        JSONArray roles = new JSONArray();
        if (isAdmin) {
            roles.add("admin");
            userData.put("firstname", "Tom");
            userData.put("lastname", "Admin");
        } else {
            userData.put("password", "kimemmtrial");
            userData.put("firstname", "Kim");
            userData.put("lastname", "User");
        }
        userData.put("roles", roles);
        //Set the headers
        headers.put(Constants.Header.CONTENT_TYPE, Constants.ContentType.APPLICATION_JSON);
        HTTPResponse httpResponse = HTTPInvoker
                .sendHTTPPostWithOAuthSecurity(userEndpoint, userData.toJSONString(), headers);
        if (httpResponse.getResponseCode() == Constants.HTTPStatus.CREATED) {
            return true;
        }
        return false;
    }

    public static boolean changePassword(String username, String pwd) {
        HashMap<String, String> headers = new HashMap<String, String>();
        String pwdEndpoint =
                EMMConfig.getInstance().getEmmHost() + "/api/device-mgt/v1.0/admin/users/" + username + "/credentials";
        //Set the password payload
        JSONObject pwdData = new JSONObject();
        pwdData.put("newPassword", pwd);

        //Set the headers
        headers.put(Constants.Header.CONTENT_TYPE, Constants.ContentType.APPLICATION_JSON);
        HTTPResponse httpResponse =
                HTTPInvoker.sendHTTPPostWithOAuthSecurity(pwdEndpoint, pwdData.toJSONString(), headers);
        if (httpResponse.getResponseCode() == Constants.HTTPStatus.OK) {
            return true;
        }
        return false;
    }

    private static String[] getUserPermissions() {
        String permissions = "\"/permission/admin/device-mgt/admin/certificate\"," +
                             "\"/permission/admin/device-mgt/admin/certificate/Add\",\"/permission/admin/device-mgt/admin/certificate/GetAll\"," +
                             "\"/permission/admin/device-mgt/admin/certificate/GetSignCSR\",\"/permission/admin/device-mgt/admin/certificate/Remove\"," +
                             "\"/permission/admin/device-mgt/admin/certificate/View\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/get-info\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/get-installed-applications\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/install-application\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/location\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/lock-device\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/mute\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/reboot\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/ring\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/send-notification\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/uninstall-application\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/unlock-device\"," +
                             "\"/permission/admin/device-mgt/admin/device/android/operation/update-application\"," +
                             "/permission/admin/device-mgt/admin/devices/List\"," +
                             "\"/permission/admin/device-mgt/admin/devices/Search\",\"/permission/admin/device-mgt/admin/devices/View\"," +
                             "\"/permission/admin/device-mgt/admin/devices/View-Applications\"," +
                             "\"/permission/admin/device-mgt/admin/devices/View-Compliance-Data\"," +
                             "\"/permission/admin/device-mgt/admin/devices/View-Features\"," +
                             "\"/permission/admin/device-mgt/admin/devices/View-Operations\"," +
                             "\"/permission/admin/device-mgt/admin/notifications\",\"/permission/admin/device-mgt/admin/notifications/View\"," +
                             "\"/permission/admin/device-mgt/user\",\"/permission/admin/device-mgt/user/device\"," +
                             "\"/permission/admin/device-mgt/user/device/android\",\"/permission/admin/device-mgt/user/device/android/enroll\"," +
                             "\"/permission/admin/login\"";
        return permissions.split(",");
    }

    public static boolean createRole(String roleName, String[] users) {
        HashMap<String, String> headers = new HashMap<String, String>();
        String roleEndpoint = EMMConfig.getInstance().getEmmHost() + "/api/device-mgt/v1.0/roles";
        //Set the role payload
        JSONObject roleData = new JSONObject();
        roleData.put("roleName", roleName);
        JSONArray perms = new JSONArray();
        String[] permissions = getUserPermissions();
        for (String perm : permissions) {
            perms.add(perm);
        }
        JSONArray usrs = new JSONArray();
        for (String usr : users) {
            usrs.add(usr);
        }
        roleData.put("permissions", perms);
        roleData.put("users", usrs);
        //Set the headers
        headers.put(Constants.Header.CONTENT_TYPE, Constants.ContentType.APPLICATION_JSON);
        HTTPResponse httpResponse =
                HTTPInvoker.sendHTTPPostWithOAuthSecurity(roleEndpoint, roleData.toJSONString(), headers);
        if (httpResponse.getResponseCode() == Constants.HTTPStatus.CREATED) {
            return true;
        }
        return false;
    }
}
