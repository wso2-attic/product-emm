/*
 Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.

 WSO2 Inc. licenses this file to you under the Apache License,
 Version 2.0 (the "License"); you may not use this file except
 in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 either express or implied. See the License for the
 specific language governing permissions and limitations
 under the License.
 */


/**
 * Returns the dynamic state to be populated by add-user page.
 *
 * @param context Object that gets updated with the dynamic state of this page to be presented
 * @returns {*} A context object that returns the dynamic state of this page to be presented
 */
function onRequest(context) {
    var log = new Log("units/user-create/certificate-create.js");
    var userModule = require("/modules/user.js")["userModule"];
    var response = userModule.getRolesByUserStore("PRIMARY");
    var mdmProps = require('/config/mdm-props.js').config();
    context["charLimit"] = mdmProps.usernameLength;
    if (response["status"] == "success") {
        context["roles"] = response["content"];
    }
    context["usernameJSRegEx"] = mdmProps.userValidationConfig.usernameJSRegEx;
    context["usernameHelpText"] = mdmProps.userValidationConfig.usernameHelpMsg;
    context["usernameRegExViolationErrorMsg"] = mdmProps.userValidationConfig.usernameRegExViolationErrorMsg;
    context["firstnameJSRegEx"] = mdmProps.userValidationConfig.firstnameJSRegEx;
    context["firstnameRegExViolationErrorMsg"] = mdmProps.userValidationConfig.firstnameRegExViolationErrorMsg;
    context["lastnameJSRegEx"] = mdmProps.userValidationConfig.lastnameJSRegEx;
    context["lastnameRegExViolationErrorMsg"] = mdmProps.userValidationConfig.lastnameRegExViolationErrorMsg;
    return context;
}