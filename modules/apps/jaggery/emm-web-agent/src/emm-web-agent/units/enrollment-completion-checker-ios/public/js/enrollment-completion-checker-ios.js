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

$(document).ready(function () {
    var iOSCheckUrl = "/emm-web-agent/enrollment/ios/check";
    setInterval(function () {
        $.post(iOSCheckUrl, function(data, status){
            var parsedData = JSON.parse(data);
            var deviceId = parsedData["deviceID"];
            var refreshToken = parsedData["refreshToken"];
            var accessToken = parsedData["accessToken"];
            var clientCredentials = parsedData["clientCredentials"];
            if(deviceId){
                window.location = "/emm-web-agent/enrollments/ios/thank-you-agent?device-id=" + encodeURI(deviceId) + "&accessToken=" +
                    encodeURI(accessToken) +"&refreshToken=" + encodeURI(refreshToken) + "&clientCredentials=" + encodeURI(clientCredentials);
            }
        });
    }, 1000);
});