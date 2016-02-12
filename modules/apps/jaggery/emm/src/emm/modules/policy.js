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
 @Refactored
 */
var policyModule;
policyModule = function () {
    var log = new Log("modules/policy.js");

    var constants = require("/modules/constants.js");
    var utility = require("/modules/utility.js")["utility"];
    var mdmProps = require('/config/mdm-props.js').config();
    var serviceInvokers = require("/modules/backend-service-invoker.js").backendServiceInvoker;

    var publicMethods = {};
    var privateMethods = {};

    privateMethods.handleGetAllPoliciesError = function (responsePayload) {
        var response = {};
        response.status = "error";
        /* responsePayload == "Scope validation failed"
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
        if (responsePayload == "Scope validation failed") {
            response.content = "Permission Denied";
        } else {
            response.content = responsePayload;
        }
        return response;
    };

    privateMethods.handleGetAllPoliciesSuccess = function (responsePayload) {
        var isUpdated = false;
        var policyListFromRestEndpoint = responsePayload["responseContent"];
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
            policyObjectToView["platform"] = policyObjectFromRestEndpoint["profile"]["deviceType"]["name"];
            policyObjectToView["ownershipType"] = policyObjectFromRestEndpoint["ownershipType"];
            policyObjectToView["roles"] = privateMethods.
                getElementsInAString(policyObjectFromRestEndpoint["roles"]);
            policyObjectToView["users"] = privateMethods.
                getElementsInAString(policyObjectFromRestEndpoint["users"]);
            policyObjectToView["compliance"] = policyObjectFromRestEndpoint["compliance"];

            if (policyObjectFromRestEndpoint["active"] == true && policyObjectFromRestEndpoint["updated"] == true) {
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
        var response = {};
        response.updated = isUpdated;
        response.status = "success";
        response.content = policyListToView;
        return response;
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
        var utility = require('/modules/utility.js')["utility"];
        try {
            var url = mdmProps["httpsURL"] + "/mdm-admin/policies";
            var response = serviceInvokers.XMLHttp.
                get(url, privateMethods.handleGetAllPoliciesSuccess,privateMethods.handleGetAllPoliciesError);
            return response;
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