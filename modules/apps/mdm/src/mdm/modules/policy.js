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

    // Class imports from java layer.
    var Policy = Packages.org.wso2.carbon.policy.mgt.common.Policy;

    var policyManagementService = utility.getPolicyManagementService();
    var policyAdminPoint = policyManagementService.getPAP();
    var publicMethods = {};
    var privateMethods = {};

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
            policyObject.roles = privateMethods.getElementsInAString(policy.getRoles());
            policyObject.users = privateMethods.getElementsInAString(policy.getUsers());
            policyObject.compliance = policy.getCompliance();

            policyList.push(policyObject);
        }
        return policyList;
    };

    privateMethods.getElementsInAString = function (elementList) {
        var i, elementsInAString = "";
        for (i = 0; i < elementList.size(); i++) {
            if (i == elementList.size() - 1) {
                elementsInAString += elementList.get(i);
            } else {
                elementsInAString += elementList.get(i) + ", ";
            }
        }
        return elementsInAString;
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

    publicMethods.deletePolicy = function (policyId) {
        var isDeleted;
        try {
            isDeleted = policyAdminPoint.deletePolicy(policyId);
            if (isDeleted) {
                // http status code 200 refers to - success.
                return 200;
            } else {
                // http status code 409 refers to - conflict.
                return 409;
            }
        } catch (e) {
            throw e;
        }
    };

    return publicMethods;
}();