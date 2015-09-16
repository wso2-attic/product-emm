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

    var UAParser = require("/modules/ua-parser.min.js")["UAParser"];
    var parser = new UAParser();
    var userAgent = request.getHeader("User-Agent");
    parser.setUA(userAgent);
    parser.getResult();
    var os = parser.getOS();
    if (os.name == "Windows Phone") {
        // login_hint passes the user email value entered in Windows workplace app
        var userEmail = request.getParameter("login_hint");
        // appru passes app ID of the Windows workplace app
        var windowsWorkplaceAppID = request.getParameter("appru");
        if (userEmail && windowsWorkplaceAppID) {
            session.put("email", userEmail);
            session.put("windowsWorkplaceAppID", windowsWorkplaceAppID);
        }
    }
    return context;
}