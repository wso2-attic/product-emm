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

function onRequest(context) {
    //var log = new Log("policy-listing.js");
    var policyModule = require("/modules/policy.js")["policyModule"];
    var response = policyModule.getAllPolicies();

    if (response["status"] == "success") {
        var policyListToView = response["content"];
        context["policyListToView"] = policyListToView;
        var policyCount = policyListToView.length;
        if (policyCount == 0) {
            context["policyListingStatusMsg"] = "No policy is available to be displayed.";
            context["saveNewPrioritiesButtonEnabled"] = false;
            context["noPolicy"] = true;
        } else if (policyCount == 1) {
            context["policyListingStatusMsg"] = "Two or more policies should be available to set up a priority order.";
            context["saveNewPrioritiesButtonEnabled"] = false;
            context["noPolicy"] = false;
        } else {
            context["policyListingStatusMsg"] = "Drag and move to re-order policy priority.";
            context["saveNewPrioritiesButtonEnabled"] = true;
            context["noPolicy"] = false;
        }
    } else {
        // here, response["status"] == "error"
        context["policyListToView"] = [];
        context["policyListingStatusMsg"] = "Error in retrieving policies. Please try reloading the page.";
        context["saveNewPrioritiesButtonEnabled"] = false;
    }

    return context;
}
