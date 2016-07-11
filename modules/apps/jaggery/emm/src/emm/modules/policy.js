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
    var log = new Log("modules/policy.js");

    var constants = require("/modules/constants.js");
    var utility = require("/modules/utility.js")["utility"];
    var mdmProps = require('/config/mdm-props.js').config();
    var serviceInvokers = require("/modules/backend-service-invoker.js")["backendServiceInvoker"];

    var publicMethods = {};
    var privateMethods = {};

    privateMethods.handleGetAllPoliciesResponse = function (backendResponse) {
        var response = {};
        if (backendResponse.status == 200 && backendResponse.responseText) {
            var isUpdated = false;
            var policyListFromRestEndpoint = parse(backendResponse.responseText)["policies"];

            var policyListToView = [];
            var i, policyObjectFromRestEndpoint, policyObjectToView;
            for (i = 0; i < policyListFromRestEndpoint.length; i++) {
                // get list object
                policyObjectFromRestEndpoint = policyListFromRestEndpoint[i];
                // populate list object values to view-object
                policyObjectToView = {};
                policyObjectToView["id"] = policyObjectFromRestEndpoint["id"];
                policyObjectToView["priorityId"] = policyObjectFromRestEndpoint["priorityId"];
                policyObjectToView["name"] = policyObjectFromRestEndpoint["policyName"];
                policyObjectToView["platform"] = policyObjectFromRestEndpoint["profile"]["deviceType"];
                policyObjectToView["ownershipType"] = policyObjectFromRestEndpoint["ownershipType"];

                var assignedRoleCount = policyObjectFromRestEndpoint["roles"].length;
                var assignedUserCount = policyObjectFromRestEndpoint["users"].length;

                if (assignedRoleCount == 0) {
                    policyObjectToView["roles"] = "None";
                } else if (assignedRoleCount == 1) {
                    policyObjectToView["roles"] = policyObjectFromRestEndpoint["roles"][0];
                } else if (assignedRoleCount > 1) {
                    policyObjectToView["roles"] = policyObjectFromRestEndpoint["roles"][0] + ", ...";
                }

                if (assignedUserCount == 0) {
                    policyObjectToView["users"] = "None";
                } else if (assignedUserCount == 1) {
                    policyObjectToView["users"] = policyObjectFromRestEndpoint["users"][0];
                } else if (assignedUserCount > 1) {
                    policyObjectToView["users"] = policyObjectFromRestEndpoint["users"][0] + ", ...";
                }

                policyObjectToView["compliance"] = policyObjectFromRestEndpoint["compliance"];

                if (policyObjectFromRestEndpoint["active"] == true &&
                    policyObjectFromRestEndpoint["updated"] == true) {
                    policyObjectToView["status"] = "Active/Updated";
                    isUpdated = true;
                } else if (policyObjectFromRestEndpoint["active"] == true &&
                    policyObjectFromRestEndpoint["updated"] == false) {
                    policyObjectToView["status"] = "Active";
                } else if (policyObjectFromRestEndpoint["active"] == false &&
                    policyObjectFromRestEndpoint["updated"] == true) {
                    policyObjectToView["status"] = "Inactive/Updated";
                    isUpdated = true;
                } else if (policyObjectFromRestEndpoint["active"] == false &&
                    policyObjectFromRestEndpoint["updated"] == false) {
                    policyObjectToView["status"] = "Inactive";
                }
                // push view-objects to list
                policyListToView.push(policyObjectToView);
            }
            // generate response
            response.updated = isUpdated;
            response.status = "success";
            response.content = policyListToView;

            return response;
        } else {
            response.status = "error";
            /* backendResponse.responseText == "Scope validation failed"
            Here the response.context("Scope validation failed") is used other then response.status(401).
            Reason for this is IDP return 401 as the status in 4 different situations such as,
            1. UnAuthorized.
            2. Scope Validation Failed.
            3. Permission Denied.
            4. Access Token Expired.
            5. Access Token Invalid.
            In these cases in order to identify the correct situation we have to compare the unique value from status and
            context which is context.
            */
            if (backendResponse.responseText == "Scope validation failed") {
                response.content = "Permission Denied";
            } else {
                response.content = backendResponse.responseText;
            }
            return response;
        }
    };

    /*
     @Updated
     */
    publicMethods.getAllPolicies = function () {
        var carbonUser = session.get(constants["USER_SESSION_KEY"]);
        if (!carbonUser) {
            log.error("User object was not found in the session");
            throw constants["ERRORS"]["USER_NOT_FOUND"];
        }
        try {
            var url = mdmProps["httpsURL"] + mdmProps["backendRestEndpoints"]["deviceMgt"] +
                "/policies?offset=0&limit=100";
            return serviceInvokers.XMLHttp.get(url, privateMethods.handleGetAllPoliciesResponse);
        } catch (e) {
            throw e;
        }
    };

    /*
     @Updated - used by getAllPolicies
     */
    privateMethods.getElementsInAString = function (elementList) {
        var i, elementsInAString = "";
        for (i = 0; i < elementList.length; i++) {
            if (i == elementList.length - 1) {
                elementsInAString += elementList[i];
            } else {
                elementsInAString += elementList[i] + ", ";
            }
        }
        return elementsInAString;
    };

    return publicMethods;
}();