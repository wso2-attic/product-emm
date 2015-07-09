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

var operationModule = function () {
    var publicMethods = {};
    var privateMethods = {};

    // Constants to define platform types available
    var platformTypeConstants = {
        "ANDROID": "android",
        "IOS": "ios"
    };

    // Constants to define operation types available
    var operationTypeConstants = {
        "PROFILE": "profile",
        "CONFIG": "config",
        "COMMAND": "command"
    };

    if (Object.freeze) {
        Object.freeze(operationTypeConstants);
    }

    publicMethods.getIOSServiceEndpoint = function (operationCode) {
        var featureMap = {
            "DEVICE_LOCK": "lock",
            "ALARM": "alarm",
            "LOCATION": "location",
            "AIR_PLAY": "airplay",
            "RESTRICTION": "restriction",
            "CELLULAR": "cellular",
            "WIFI": "wifi",
            "INSTALL_STORE_APPLICATION": "storeapplication",
            "INSTALL_ENTERPRISE_APPLICATION": "enterpriseapplication",
            "REMOVE_APPLICATION": "removeapplication",
            "ENTERPRISE_WIPE": "enterprisewipe"
        };
        return "/ios/operation/" + featureMap[operationCode];
    };

    privateMethods.generateIOSOperationPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        switch (operationCode) {
            case "PASSCODE_POLICY":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "forcePIN": operationData["forcePIN"],
                        "allowSimple": operationData["allowSimple"],
                        "requireAlphanumeric": operationData["requireAlphanumeric"],
                        "minLength": operationData["minLength"],
                        "minComplexChars": operationData["minComplexChars"],
                        "maxPINAgeInDays": operationData["maxPINAgeInDays"],
                        "pinHistory": operationData["pinHistory"],
                        "maxInactivity": operationData["maxAutoLock"],
                        "maxGracePeriod": operationData["gracePeriod"],
                        "maxFailedAttempts": operationData["maxFailedAttempts"]
                    }
                };
                break;
            case "WIFI_SETTINGS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "ssid": operationData["wfSsid"],
                        "hiddenNetwork": operationData["wfHiddenNetwork"],
                        "autoJoin": operationData["wfAutoJoin"],
                        "proxyType": operationData["wfProxyType"],
                        "encryptionType": operationData["wfEncryptionType"],
                        "domainName": operationData["wfDomainName"],
                        "serviceProviderRoamingEnabled": operationData["wfSpRomainEnabled"],
                        "displayedOperatorName": operationData["wfDisplayedOperatorName"],
                        "roamingConsortiumOIs": operationData[""],
                        "password": operationData["wfEncPassword"],
                        "clientConfiguration": {
                            "username": operationData["wfEncEapUsername"],
                            "acceptEAPTypes": operationData[""],
                            "userPassword": operationData["wfEncEapUserPassword"],
                            "oneTimePassword": operationData["wfEncEapOneTimePassword"],
                            "payloadCertificateAnchorUUID": operationData[""],
                            "outerIdentity": operationData["wfEncEapOuterIdentity"],
                            "tlstrustedServerNames": operationData[""],
                            "tlsallowTrustExceptions": operationData["wfEncEapTlsAllowTrustExceptions"],
                            "tlscertificateIsRequired": operationData["wfEncEapTlsCertIsRequired"],
                            "ttlsinnerAuthentication": operationData["wfEapTlsInnerAuthType"],
                            "eapfastusePAC": operationData["wfEncEapFastUsePac"],
                            "eapfastprovisionPAC": operationData["wfEncEapFastProvisionPac"],
                            "eapfastprovisionPACAnonymously": operationData["wfEncEapFastProvisionPacAnon"],
                            "eapsimnumberOfRANDs": operationData[""]
                        },
                        "payloadCertificateUUID": operationData["wfEncPayloadCertUuid"],
                        "proxyServer": operationData["wfProxyServer"],
                        "proxyPort": operationData["wfProxyPort"],
                        "proxyUsername": operationData["wfProxyUsername"],
                        "proxyPassword": operationData["wfProxyPassword"],
                        "proxyPACURL": operationData["wfProxyPacUrl"],
                        "proxyPACFallbackAllowed": operationData["wfProxyAllowPacFallback"],
                        "nairealmNames": operationData[""],
                        "mccandMNCs": operationData[""]
                    }
                };
                break;
            case "CONTACTS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "accountDescription": operationData["accountDescription"],
                        "accountHostname": operationData["accountHostname"],
                        "accountPort": operationData["accountPort"],
                        "principalURL": operationData["principalURL"],
                        "accountUsername": operationData["accountUsername"],
                        "accountPassword": operationData["accountPassword"],
                        "useSSL": operationData["useSSL"]
                    }
                };
                break;
            case "CALENDAR":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "accountDescription": operationData["calAccountDescription"],
                        "hostName": operationData["calAccountHostname"],
                        "port": operationData["calAccountPort"],
                        "principalURL": operationData["calPrincipalURL"],
                        "username": operationData["calAccountUsername"],
                        "password": operationData["calAccountPassword"],
                        "useSSL": operationData["calUseSSL"]
                    }
                };
                break;
            case "SUBSCRIBED_CALENDARS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "accountDescription": operationData["csDescription"],
                        "hostName": operationData["csURL"],
                        "username": operationData["csUsername"],
                        "password": operationData["csPassword"],
                        "useSSL": operationData["csUseSSL"]
                    }
                };
                break;
            case "SCEP_SETTINGS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "scepURL": operationData["scepURL"],
                        "scepName": operationData["scepName"],
                        "scepSubject": operationData["scepSubject"],
                        "scepSubjectAlternativeNameType": operationData["scepSubjectAlternativeNameType"],
                        "scepSubjectAlternativeNameValue": operationData["scepSubjectAlternativeNameValue"],
                        "scepNTprincipalName": operationData["scepNTprincipalName"],
                        "scepRetries": operationData["scepRetries"],
                        "scepRetryDelay": operationData["scepRetryDelay"],
                        "scepChallenge": operationData["scepChallenge"],
                        "scepKeySize": operationData["scepKeySize"],
                        "scepUsedAsDS": operationData["scepUsedAsDS"],
                        "scepUseForKE": operationData["scepUseForKE"],
                        "scepFingerprint": operationData["scepFingerprint"]
                    }
                };
                break;
            case "APN_SETTINGS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "apn": operationData["apnAccessPointName"],
                        "username": operationData["apnAccessPointUsername"],
                        "password": operationData["apnAccessPointPassword"],
                        "proxyServer": operationData["apnProxyServer"],
                        "proxyPort": operationData["apnProxyPort"]
                    }
                };
                break;
            case "WEB_CLIPS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "label": operationData["wcLabel"],
                        "url": operationData["wcURL"],
                        "icon": operationData[""],
                        "isRemovable": operationData["wcRemovable"],
                        "isPrecomposed": operationData["wcPrecomposedIcon"],
                        "isFullScreen": operationData["wcFullScreen"]
                    }
                };
                break;
            case "EMAIL_SETTINGS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "emAccountDescription": operationData["emAccountDescription"],
                        "emAccountType": operationData["emAccountType"],
                        "emUserDisplayName": operationData["emAddress"],
                        "wcPrecomposedIcon": operationData["emAllowMovingMessages"],
                        "emAllowAddressSyncing": operationData["emAllowAddressSyncing"],
                        "emUseInMail": operationData["emUseInMail"],
                        "emEnableMime": operationData["emEnableMime"]
                    }
                };
                break;
            default:
                // If the operation is neither of above, it is a command operation
                operationType = operationTypeConstants["COMMAND"];
                // Operation payload of a command operation is simply an array of device IDs
                payload = deviceList;
        }

        if (operationType == operationTypeConstants["PROFILE"] && deviceList) {
            payload["deviceIDs"] = deviceList;
        }

        return payload;
    };

    privateMethods.generateAndroidOperationPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        switch (operationCode) {
            case "CAMERA":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "enabled" : operationData["enableCamera"]
                    }
                };
                break;
            case "CHANGE_LOCK_CODE":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "lockCode" : operationData["lockCode"]
                    }
                };
                break;
            case "ENCRYPT_STORAGE":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "encrypted" : operationData["enableEncryption"]
                    }
                };
                break;
            case "NOTIFICATION":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "message" : operationData["message"]
                    }
                };
                break;
            case "WEBCLIP":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "identity": operationData["url"],
                        "title": operationData["title"]
                    }
                };
                break;
            case "INSTALL_APPLICATION":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "appIdentifier": operationData["packageName"],
                        "type": operationData["type"],
                        "url": operationData["url"]
                    }
                };
                break;
            case "UNINSTALL_APPLICATION":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "appIdentifier": operationData["packageName"]
                    }
                };
                break;
            case "BLACKLIST_APPLICATIONS":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "appIdentifier": operationData["packageNames"]
                    }
                };
                break;
            case "PASSCODE_POLICY":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "allowSimple": operationData["allowSimple"],
                        "requireAlphanumeric": operationData["requireAlphanumeric"],
                        "minLength": operationData["minLength"],
                        "minComplexChars": operationData["minComplexChars"],
                        "maxPINAgeInDays": operationData["maxPINAgeInDays"],
                        "pinHistory": operationData["pinHistory"],
                        "maxFailedAttempts": operationData["maxFailedAttempts"]
                    }
                };
                break;
            case "WIFI":
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "ssid": operationData["ssid"],
                        "password": operationData["password"]
                    }
                };
                break;
            default:
                // If the operation is neither of above, it is a command operation
                operationType = operationTypeConstants["COMMAND"];
                // Operation payload of a command operation is simply an array of device IDs
                payload = deviceList;
        }

        if (operationType == operationTypeConstants["PROFILE"] && deviceList) {
            payload["deviceIDs"] = deviceList;
        }

        return payload;
    };

    publicMethods.getAndroidServiceEndpoint = function (operationCode) {
        var featureMap = {
            "WIFI": "wifi",
            "CAMERA": "camera",
            "DEVICE_LOCK": "lock",
            "DEVICE_LOCATION": "location",
            "CLEAR_PASSWORD": "clear-password",
            "APPLICATION_LIST": "get-application-list",
            "DEVICE_RING": "ring-device",
            "DEVICE_MUTE": "mute",
            "NOTIFICATION": "notification",
            "ENCRYPT_STORAGE": "encrypt",
            "CHANGE_LOCK_CODE": "change-lock-code",
            "WEBCLIP": "webclip",
            "INSTALL_APPLICATION": "install-application",
            "UNINSTALL_APPLICATION": "uninstall-application",
            "BLACKLIST_APPLICATIONS": "blacklist-applications",
            "PASSCODE_POLICY": "password-policy",
            "ENTERPRISE_WIPE": "enterprise-wipe",
            "WIPE_DATA": "wipe-data"
        };
        return "/mdm-android-agent/operation/" + featureMap[operationCode];
    };

    /**
     * Get the icon for the featureCode
     * @param operationCode
     * @returns icon class
     */
    publicMethods.getAndroidIconForFeature = function (operationCode) {
        var featureMap = {
            "DEVICE_LOCK": "fw-lock",
            "DEVICE_LOCATION": "fw-map-location",
            "CLEAR_PASSWORD": "fw-key",
            "ENTERPRISE_WIPE": "fw-clean",
            "WIPE_DATA": "fw-database",
            "DEVICE_RING": "fw-dial-up",
            "DEVICE_MUTE": "fw-incoming-call",
            "NOTIFICATION": "fw-message",
            "CHANGE_LOCK_CODE": "fw-padlock"
        };
        return featureMap[operationCode];
    };

    /**
     * Get the icon for the featureCode
     * @param operationCode
     * @returns icon class
     */
    publicMethods.getIOSIconForFeature = function (operationCode) {
        var featureMap = {
            "DEVICE_LOCK": "fw-lock",
            "LOCATION": "fw-map-location",
            "ENTERPRISE_WIPE": "fw-clean",
            "ALARM": "fw-dial-up"
        };
        return featureMap[operationCode];
    };

    privateMethods.createTemperatureControllerPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        if (operationCode == "BUZZER") {
            operationType = operationTypeConstants["PROFILE"];
            payload = {
                "operation": {
                    "enabled" : operationData["enableBuzzer"]
                }
            };
        } else {
            operationType = operationTypeConstants["COMMAND"];
            payload = deviceList;
        }
        if (operationType == operationTypeConstants["PROFILE"] && deviceList) {
            payload["deviceIDs"] = deviceList;
        }
        return payload;
    };

    publicMethods.getTemperatureControllerServiceEndpoint = function (operationCode) {
        var featureMap = {
            "BUZZER": "buzzer"
        };
        return "/temp-controller-agent/operations/" + featureMap[operationCode];
    };

    publicMethods.getTemperatureControllerIconForFeature = function (operationCode) {
        var featureMap = {
            "BUZZER": "fw-dial-up"
        };
        return featureMap[operationCode];
    };

    /**
     * Filter a list by a data attribute
     * @param prop
     * @param val
     * @returns {Array}
     */
    $.fn.filterByData = function (prop, val) {
        return this.filter(
            function () { return $(this).data(prop) == val; }
        );
    };

    /**
     *
     * @param platformType Platform Type of the profile
     * @param operationCode Operation Codes to generate the profile from
     * @param deviceList Optional device list to include in payload body for operations
     * @returns {*}
     */
    publicMethods.generatePayload = function (platformType, operationCode, deviceList) {
        var payload;
        var operationData = {};
        $(".operation-data").filterByData("operation-code", operationCode).find(".operationDataKeys").each(
            function () {
                var operationDataObj = $(this);
                var key = operationDataObj.data("key");
                var value = operationDataObj.val();
                if (operationDataObj.is(":checkbox")) {
                    value = operationDataObj.is(":checked");
                } else if (operationDataObj.is("select")) {
                    value = operationDataObj.find("option:selected").attr("value");
                } else if (operationDataObj.hasClass("grouped-array-input")) {
                    value = [];
                    if (operationDataObj.hasClass("valued-check-box-array")) {
                        $(".child-input", this).each(function () {
                            if ($(this).is(":checked")) {
                                value.push($(this).data("value"));
                            }
                        });
                    } else if (operationDataObj.hasClass("one-column-text-field-array")) {
                        $(".child-input", this).each(function () {
                            value.push($(this).val());
                        });
                    }
                }
                operationData[key] = value;
            }
        );
        switch (platformType) {
            case platformTypeConstants["ANDROID"]:
                payload = privateMethods.generateAndroidOperationPayload(operationCode, operationData, deviceList);
                break;
            case platformTypeConstants["IOS"]:
                payload = privateMethods.generateIOSOperationPayload(operationCode, operationData, deviceList);
                break;
        }
        return payload;
    };

    /**
     * generateProfile method is only used for policy-creation UIs
     *
     * @param platformType Platform Type of the profile
     * @param operationCodes Operation codes to generate the profile from
     * @returns {{}}
     */
    publicMethods.generateProfile = function (platformType, operationCodes) {
        var generatedProfile = {};
        for (var i = 0; i < operationCodes.length; ++i) {
            var operationCode = operationCodes[i];
            var payload = publicMethods.generatePayload(platformType, operationCode, null);
            generatedProfile[operationCode] = payload["operation"];
        }
        return generatedProfile;
    };

    return publicMethods;

}();