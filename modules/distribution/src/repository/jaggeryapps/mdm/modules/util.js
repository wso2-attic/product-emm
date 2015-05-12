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

var util = function () {
    var module = {};
    var Base64 = Packages.org.apache.commons.codec.binary.Base64;
    var String = Packages.java.lang.String;
    var log = new Log();
    function encode(payload){
        log.info(payload);
        log.info(Base64.encodeBase64(new String(payload).getBytes()));
        return new String(Base64.encodeBase64(new String(payload).getBytes()));
    }

    module.getTokenWithPasswordGrantType = function (username, password, clientId, clientSecret, scope) {
        var xhr = new XMLHttpRequest();
        var tokenEndpoint = "https://localhost:9443/oauth2/token";
        var encodedClientKeys = encode(clientId + ":" + clientSecret);
        log.info("*****");
        log.info(encodedClientKeys);
        xhr.open("POST", tokenEndpoint, false);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.setRequestHeader("Authorization", "Basic " + encodedClientKeys);
        xhr.send("grant_type=password&username=" + username + "&password=" + password + "&scope=" + scope);
        delete password, delete clientSecret, delete encodedClientKeys;
        log.info(xhr.status);
        if (xhr.status == 200) {
            log.info("+++");
            log.info(parse(xhr.responseText));
        } else if (xhr.status == 403) {
            throw "Error in obtaining token with Password Grant Type";
        } else {
            throw "Error in obtaining token with Password Grant Type";
        }
    };
    module.getTokenWithSAMLGrantType = function () {

    };
    return module;
}();