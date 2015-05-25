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

var policyModule;
policyModule = function () {
    var log = new Log("modules/user.js");

    var constants = require("/modules/constants.js");
    var utility = require("/modules/utility.js").utility;

    var Policy = Packages.org.wso2.carbon.policy.mgt.common.Policy;

    var policyManagementService = utility.getPolicyManagementService();
    var policyAdminPoint = policyManagementService.getPAP();
    var publicMethods = {};

    publicMethods.getPolicies = function () {
        log.debug(policyAdminPoint.getPolicies());

        var policies = policyAdminPoint.getPolicies();
        var policyList = [];
        var i, policy, policyObject;
        for (i = 0; i < policies.size(); i++) {
            policy = policies.get(i);
            policyObject = {};

            policyObject.id = policy.getId();
            policyObject.priorityId = policy.getPriorityId();
            policyObject.name = policy.getPolicyName();
            policyObject.platform = policy.getProfile().getDeviceType().getName();
            policyObject.ownershipType = policy.getOwnershipType();
            policyObject.roles = policy.getRoles();
            policyObject.users = policy.getUsers();
            policyObject.compliance = policy.getCompliance();

            policyList.push(policyObject);
        }
        return policyList;
    };

    publicMethods.getProfiles = function () {
        var profiles = policyAdminPoint.getProfiles();
        var profileList = [];
        var i, profile, profileObject;
        for (i = 0; i < profiles.size(); i++) {
            profile = profiles.get(i);
            profileObject = {};
            profileObject.name = profile.getProfileName();
            profileObject.id = profile.getProfileId();
            profileList.push(profileObject);
        }
        return profileList;
    };

    publicMethods.updatePolicyPriorities = function (payload) {
        var policyCount = payload.length;
        var policyList = new java.util.ArrayList();
        var i, policyObject;
        for (i = 0; i < policyCount; i++) {
            policyObject = new Policy();
            policyObject.setId(payload[i].id);
            policyObject.setPriorityId(payload[i].priority);
            policyList.add(policyObject);
        }
        policyAdminPoint.updatePolicyPriorities(policyList);
    };

    return publicMethods;
}();


