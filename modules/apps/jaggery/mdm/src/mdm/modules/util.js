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
    var mdmProps = require('/config/mdm-props.js').config();
    var carbon = require('carbon');
    var realmService = carbon.server.osgiService('org.wso2.carbon.user.core.service.RealmService');
    var adminUserName = realmService.getBootstrapRealmConfiguration().getAdminUserName();

    module.getDyanmicCredentials = function (owner) {
        var payload = {
            "callbackUrl": mdmProps.callBackUrl,
            "clientName": "mdm",
            "tokenScope": "admin",
            "owner": adminUserName,
            "grantType": "password refresh_token urn:ietf:params:oauth:grant-type:saml2-bearer",
            "saasApp" :true
        };
        var xhr = new XMLHttpRequest();
        var tokenEndpoint = mdmProps.dynamicClientRegistrationEndPoint;
        xhr.open("POST", tokenEndpoint, false);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.send(payload);
        var clientData = {};
        if (xhr.status == 201) {
            var data = parse(xhr.responseText);
            clientData.clientId = data.client_id;
            clientData.clientSecret = data.client_secret;
        } else if (xhr.status == 400) {
            throw "Invalid client meta data";
        } else {
            throw "Error in obtaining client id and secret";
        }
        return clientData;
    };

    /**
     * Encode the payload in Base64
     * @param payload
     * @returns {Packages.java.lang.String}
     */
    module.encode = function (payload) {
        return new String(Base64.encodeBase64(new String(payload).getBytes()));
    }

    module.decode = function (payload) {
        return new String(Base64.decodeBase64(new String(payload).getBytes()));
    }

    /**
     * Get an AccessToken pair based on username and password
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @param scope
     * @returns {{accessToken: "", refreshToken: ""}}
     */
    module.getTokenWithPasswordGrantType = function (username, password, encodedClientKeys, scope) {
        var xhr = new XMLHttpRequest();
        var tokenEndpoint = mdmProps.idPServer + "/oauth2/token";
        xhr.open("POST", tokenEndpoint, false);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.setRequestHeader("Authorization", "Basic " + encodedClientKeys);
        xhr.send("grant_type=password&username=" + username + "&password=" + password + "&scope=" + scope);
        delete password, delete clientSecret, delete encodedClientKeys;
        var tokenPair = {};
        if (xhr.status == 200) {
            var data = parse(xhr.responseText);
            tokenPair.refreshToken = data.refresh_token;
            tokenPair.accessToken = data.access_token;
        } else if (xhr.status == 403) {
            throw "Error in obtaining token with Password grant type";
        } else {
            throw "Error in obtaining token with Password grant type";
        }
        return tokenPair;
    };
    module.getTokenWithSAMLGrantType = function (assertion, clientKeys, scope) {

        var assertionXML = module.decode(assertion) ;
        var encodedExtractedAssertion;
        var extractedAssertion;
        //TODO: make assertion extraction with proper parsing. Since Jaggery XML parser seem to add formatting
        //which causes signature verification to fail.
        var assertionStartMarker = "<saml2:Assertion";
        var assertionEndMarker = "<\/saml2:Assertion>";
        var assertionStartIndex = assertionXML.indexOf(assertionStartMarker);
        var assertionEndIndex = assertionXML.indexOf(assertionEndMarker);
        if (assertionStartIndex != -1 && assertionEndIndex != -1) {
            extractedAssertion = assertionXML.substring(assertionStartIndex, assertionEndIndex) + assertionEndMarker;
        } else {
            throw "Invalid SAML response. SAML response has no valid assertion string";
        }

        encodedExtractedAssertion = this.encode(extractedAssertion);

        var xhr = new XMLHttpRequest();
        var tokenEndpoint = mdmProps.idPServer + "/oauth2/token";
        xhr.open("POST", tokenEndpoint, false);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.setRequestHeader("Authorization", "Basic " + clientKeys);
        xhr.send("grant_type=urn:ietf:params:oauth:grant-type:saml2-bearer&assertion=" +
                 encodeURIComponent(encodedExtractedAssertion) + "&scope=" + "PRODUCTION");
        var tokenPair = {};
        if (xhr.status == 200) {
            var data = parse(xhr.responseText);
            tokenPair.refreshToken = data.refresh_token;
            tokenPair.accessToken = data.access_token;
        } else if (xhr.status == 403) {
            throw "Error in obtaining token with SAML extension grant type";
        } else {
            throw "Error in obtaining token with SAML extension grant type";
        }
        return tokenPair;
    };
    module.refreshToken = function (tokenPair, clientData, scope) {
        var xhr = new XMLHttpRequest();
        var tokenEndpoint = mdmProps.idPServer + "/oauth2/token";
        xhr.open("POST", tokenEndpoint, false);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.setRequestHeader("Authorization", "Basic " + clientData);
        var url = "grant_type=refresh_token&refresh_token=" + tokenPair.refreshToken;
        if (scope) {
            url = url + "&scope=" + scope
        }
        xhr.send(url);
        delete clientData;
        var tokenPair = {};
        if (xhr.status == 200) {
            var data = parse(xhr.responseText);
            tokenPair.refreshToken = data.refresh_token;
            tokenPair.accessToken = data.access_token;
        } else if (xhr.status == 403) {
            throw "Error in obtaining token with Refresh Token  Grant Type";
        } else {
            throw "Error in obtaining token with  Refresh Token Type";
        }
        return tokenPair;
    };
    return module;
}();