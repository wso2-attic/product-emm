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
    var log = new Log("enrollment-access-control-unit backend js");
    log.debug("calling enrollment-access-control-unit");

    var mdmProps = require('/config/mdm-props.js').config();
    var UAParser = require("/modules/ua-parser.min.js")["UAParser"];

    var parser = new UAParser();
    var userAgent = request.getHeader("User-Agent");
    parser.setUA(userAgent);
    parser.getResult();
    var userAgentPlatform = parser.getOS()["name"];

    if (userAgentPlatform != context["allowedPlatform"]) {
        // if userAgentPlatform is not allowed
        response.sendRedirect(mdmProps["appContext"] + "enrollments/error/unintentional-request");
    } else {
        // if userAgentPlatform is allowed,
        // restricting unordered intermediate page access
        if (context["lastPage"] && context["currentPage"] && context["nextPage"]) {
            // meaning it's not first page, but a middle page
            if (!session.get("lastAccessedPage")) {
                // meaning a middle page is accessed at first
                response.sendRedirect(mdmProps["appContext"] + "enrollments/error/unintentional-request");
            } else if (!(session.get("lastAccessedPage") == context["currentPage"]) &&
                !(session.get("lastAccessedPage") == context["lastPage"]) &&
                !(session.get("lastAccessedPage") == context["nextPage"])) {
                response.sendRedirect(mdmProps["appContext"] + "enrollments/error/unintentional-request");
            } else if (context["currentPage"]) {
                // if currentPage is set, update lastAccessedPage as currentPage
                session.put("lastAccessedPage", context["currentPage"]);
            }
        } else if (context["lastPage"] && context["currentPage"] && !context["nextPage"]) {
            // meaning it's not first page, not a middle page, but the last page in wizard
            if (!session.get("lastAccessedPage")) {
                // this means the last page is accessed at first
                response.sendRedirect(mdmProps["appContext"] + "enrollments/error/unintentional-request");
            } else if (!(session.get("lastAccessedPage") == context["currentPage"]) &&
                !(session.get("lastAccessedPage") == context["lastPage"])) {
                response.sendRedirect(mdmProps["appContext"] + "enrollments/error/unintentional-request");
            } else if (context["currentPage"]) {
                // if currentPage is set, update lastAccessedPage as currentPage
                session.put("lastAccessedPage", context["currentPage"]);
            }
        } else if (context["currentPage"]) {
            // meaning it's the first page
            // if currentPage is set, update lastAccessedPage as currentPage
            session.put("lastAccessedPage", context["currentPage"]);
        }
    }

    if (log.isDebugEnabled()) {
        log.debug("last-accessed-page = " + session.get("lastAccessedPage") +
            " : " + "session-id = " + session.getId());
    }
    return context;
}