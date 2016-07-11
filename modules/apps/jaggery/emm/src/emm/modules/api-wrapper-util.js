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

var apiWrapperUtil = function () {
    var module = {};
    var tokenUtil = require("/modules/util.js").util;
    var constants = require("/modules/constants.js");
    var devicemgtProps = require('/config/mdm-props.js').config();
    var log = new Log("/modules/api-wrapper-util.js");

    module.refreshToken = function () {
        var tokenPair = session.get(constants.ACCESS_TOKEN_PAIR_IDENTIFIER);
        var clientData = session.get(constants.ENCODED_CLIENT_KEYS_IDENTIFIER);
        tokenPair = tokenUtil.refreshToken(tokenPair, clientData);
        session.put(constants.ACCESS_TOKEN_PAIR_IDENTIFIER, tokenPair);
    };
    module.setupAccessTokenPair = function (type, properties) {
        var tokenInfo;
        var tokenPair = {};
        var clientData = tokenUtil.getDyanmicCredentials(properties);
        var encodedClientKeys = tokenUtil.encode(clientData.clientId + ":" + clientData.clientSecret);
        session.put(constants.ENCODED_CLIENT_KEYS_IDENTIFIER, encodedClientKeys);

        // getting all scopes from config
        var scopes = devicemgtProps.scopes;
        var scope = "";
        scopes.forEach(function(entry) {
            scope += entry + " ";
        });

        if (type == "password") {
            tokenInfo = tokenUtil.
                getTokenWithPasswordGrantType(properties.username, encodeURIComponent(properties.password), encodedClientKeys, scope);
        } else if (type == "saml") {
            tokenInfo = tokenUtil.
                getTokenWithSAMLGrantType(properties.samlToken, encodedClientKeys, scope);
        }
        tokenPair.refreshToken = tokenInfo.refresh_token;
        tokenPair.accessToken = tokenInfo.access_token;
        session.put(constants.ACCESS_TOKEN_PAIR_IDENTIFIER, tokenPair);
        scopes = tokenInfo.scopes.split(" ");
        session.put(constants.ALLOWED_SCOPES, scopes);

    };
    return module;
}();