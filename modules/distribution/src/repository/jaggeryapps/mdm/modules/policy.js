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

var policyModule = function () {
    var log = new Log("modules/user.js");

    var constants = require("/modules/constants.js");
    var dataConfig = require("/config/mdm-props.js").config();
    var utility = require("/modules/utility.js").utility;

    var policyManagementService = utility.getPolicyManagementService();
    var policyAdminPoint = policyManagementService.getPAP();
    var publicMethods = {};
    var privateMethods = {};

    publicMethods.getPolicies = function (){
        log.debug(policyAdminPoint.getPolicies());
        var policies = policyAdminPoint.getPolicies();
        var policyList = [];
        for (var i = 0; i < policies.size(); i++) {
            var policy = policies.get(i);
            var policyObject = {};
            policyObject.name = policy.getPolicyName();
            policyObject.id = policy.getId();
            policyList.push(policyObject);
        }
        return policyList;
    };

    publicMethods.getProfiles = function (){
        var profiles = policyAdminPoint.getProfiles();
        var profileList = [];
        for (var i = 0; i < profiles.size(); i++) {
            var profile = profiles.get(i);
            var profileObject = {};
            profileObject.name = profile.getProfileName();
            profileObject.id = profile.getProfileId();
            profileList.push(profileObject);
        }
        return profileList;
    };
    return publicMethods;
}();


