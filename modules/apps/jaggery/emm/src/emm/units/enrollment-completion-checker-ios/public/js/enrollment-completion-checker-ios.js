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
    var iOSCheckUrl = "/emm/enrollment/ios/check";
    setInterval(function () {
        $.get(iOSCheckUrl, function(data, status){
            var parsedData = JSON.parse(data);
            var extractedData = parsedData["deviceID"];
            if(extractedData && extractedData.indexOf("_accessToken_") != -1){
                var result = extractedData.split("_accessToken_");
                var deviceId =  result[0];
                var result = result[1].split("_refreshToken_");
                var accessToken =  result[0];
                var result = result[1].split("_clientCredentials_");
                var refreshToken =  result[0];
                var clientCredentials =  result[1];

                window.location = "/emm/enrollments/ios/thank-you-agent?device-id=" + encodeURI(deviceId) + "&accessToken=" +
                    encodeURI(accessToken) +"&refreshToken=" + encodeURI(refreshToken) + "&clientCredentials=" + encodeURI(clientCredentials);
            }
        });
    }, 1000);
});