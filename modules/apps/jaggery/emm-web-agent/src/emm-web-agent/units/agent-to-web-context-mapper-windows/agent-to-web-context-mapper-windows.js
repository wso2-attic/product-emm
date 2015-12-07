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
    var log = new Log("agent-to-web-context-mapper-windows-unit backend js");
    log.debug("calling agent-to-web-context-mapper-windows-unit");

    if (!(session.get("email") && session.get("windowsWorkplaceAppID"))) {
        // if both email and windowsWorkplaceAppID session values are not set
        // this means either shifting to the page from agent or directly accessing the page out-of-order

        // checking if user is actually shifting to the page from agent
        // login_hint passes the user email value entered in Windows workplace app
        var userEmail = request.getParameter("login_hint");
        // appru passes app ID of the Windows workplace app
        var windowsWorkplaceAppID = request.getParameter("appru");
        if (!userEmail || !windowsWorkplaceAppID) {
            response.sendRedirect(mdmProps["appContext"] + "enrollments/error/unintentional-request");
        } else {
            /* allowing to skip first step of windows enrollment by
            setting session.put("lastAccessedPage", "invoke-agent")...
            This update was proposed to overcome following problem:
            First step of enrollment and second step of enrollment being linked with two sessions as
            first step is initiated by Internet explorer and the second by an internal web-view */
            session.put("lastAccessedPage", "invoke-agent");
            session.put("email", userEmail);
            session.put("windowsWorkplaceAppID", windowsWorkplaceAppID);
        }
    }

    return context;
}