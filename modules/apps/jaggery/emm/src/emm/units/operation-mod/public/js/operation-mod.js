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
        "IOS": "ios",
        "WINDOWS": "windows"
    };

    // Constants to define operation types available
    var operationTypeConstants = {
        "PROFILE": "profile",
        "CONFIG": "config",
        "COMMAND": "command"
    };

    // Constants to define Android Operation Constants
    var androidOperationConstants = {
        "PASSCODE_POLICY_OPERATION_CODE": "PASSCODE_POLICY",
        "VPN_OPERATION_CODE": "VPN",
        "CAMERA_OPERATION_CODE": "CAMERA",
        "ENCRYPT_STORAGE_OPERATION_CODE": "ENCRYPT_STORAGE",
        "WIFI_OPERATION_CODE": "WIFI",
        "WIPE_OPERATION_CODE": "WIPE_DATA",
        "NOTIFICATION_OPERATION_CODE": "NOTIFICATION",
        "WORK_PROFILE_CODE": "WORK_PROFILE",
        "CHANGE_LOCK_CODE_OPERATION_CODE": "CHANGE_LOCK_CODE",
        "LOCK_OPERATION_CODE": "DEVICE_LOCK",
        "UPGRADE_FIRMWARE": "UPGRADE_FIRMWARE",
        "DISALLOW_ADJUST_VOLUME": "DISALLOW_ADJUST_VOLUME",
        "DISALLOW_CONFIG_BLUETOOTH" : "DISALLOW_CONFIG_BLUETOOTH",
        "DISALLOW_CONFIG_CELL_BROADCASTS" : "DISALLOW_CONFIG_CELL_BROADCASTS",
        "DISALLOW_CONFIG_CREDENTIALS" : "DISALLOW_CONFIG_CREDENTIALS",
        "DISALLOW_CONFIG_MOBILE_NETWORKS" : "DISALLOW_CONFIG_MOBILE_NETWORKS",
        "DISALLOW_CONFIG_TETHERING" : "DISALLOW_CONFIG_TETHERING",
        "DISALLOW_CONFIG_VPN" : "DISALLOW_CONFIG_VPN",
        "DISALLOW_CONFIG_WIFI" : "DISALLOW_CONFIG_WIFI",
        "DISALLOW_APPS_CONTROL" : "DISALLOW_APPS_CONTROL",
        "DISALLOW_CREATE_WINDOWS" : "DISALLOW_CREATE_WINDOWS",
        "DISALLOW_CROSS_PROFILE_COPY_PASTE" : "DISALLOW_CROSS_PROFILE_COPY_PASTE",
        "DISALLOW_DEBUGGING_FEATURES" : "DISALLOW_DEBUGGING_FEATURES",
        "DISALLOW_FACTORY_RESET" : "DISALLOW_FACTORY_RESET",
        "DISALLOW_ADD_USER" : "DISALLOW_ADD_USER",
        "DISALLOW_INSTALL_APPS" : "DISALLOW_INSTALL_APPS",
        "DISALLOW_INSTALL_UNKNOWN_SOURCES" : "DISALLOW_INSTALL_UNKNOWN_SOURCES",
        "DISALLOW_MODIFY_ACCOUNTS" : "DISALLOW_MODIFY_ACCOUNTS",
        "DISALLOW_MOUNT_PHYSICAL_MEDIA" : "DISALLOW_MOUNT_PHYSICAL_MEDIA",
        "DISALLOW_NETWORK_RESET" : "DISALLOW_NETWORK_RESET",
        "DISALLOW_OUTGOING_BEAM" : "DISALLOW_OUTGOING_BEAM",
        "DISALLOW_OUTGOING_CALLS" : "DISALLOW_OUTGOING_CALLS",
        "DISALLOW_REMOVE_USER" : "DISALLOW_REMOVE_USER",
        "DISALLOW_SAFE_BOOT" : "DISALLOW_SAFE_BOOT",
        "DISALLOW_SHARE_LOCATION" : "DISALLOW_SHARE_LOCATION",
        "DISALLOW_SMS" : "DISALLOW_SMS",
        "DISALLOW_UNINSTALL_APPS" : "DISALLOW_UNINSTALL_APPS",
        "DISALLOW_UNMUTE_MICROPHONE" : "DISALLOW_UNMUTE_MICROPHONE",
        "DISALLOW_USB_FILE_TRANSFER" : "DISALLOW_USB_FILE_TRANSFER",
        "ALLOW_PARENT_PROFILE_APP_LINKING" : "ALLOW_PARENT_PROFILE_APP_LINKING",
        "ENSURE_VERIFY_APPS" : "ENSURE_VERIFY_APPS",
        "AUTO_TIME" : "AUTO_TIME",
        "SET_SCREEN_CAPTURE_DISABLED" : "SET_SCREEN_CAPTURE_DISABLED",
        "SET_STATUS_BAR_DISABLED" : "SET_STATUS_BAR_DISABLED"
    };

    // Constants to define Windows Operation Constants
    var windowsOperationConstants = {
        "PASSCODE_POLICY_OPERATION_CODE": "PASSCODE_POLICY",
        "CAMERA_OPERATION_CODE": "CAMERA",
        "ENCRYPT_STORAGE_OPERATION_CODE": "ENCRYPT_STORAGE",
        "NOTIFICATION_OPERATION_CODE": "NOTIFICATION",
        "CHANGE_LOCK_CODE_OPERATION_CODE": "CHANGE_LOCK_CODE"
    };

    // Constants to define iOS Operation Constants
    var iosOperationConstants = {
        "PASSCODE_POLICY_OPERATION_CODE": "PASSCODE_POLICY",
        "RESTRICTIONS_OPERATION_CODE": "RESTRICTION",
        "VPN_OPERATION_CODE": "VPN",
        "WIFI_OPERATION_CODE": "WIFI",
        "EMAIL_OPERATION_CODE": "EMAIL",
        "AIRPLAY_OPERATION_CODE": "AIR_PLAY",
        "LDAP_OPERATION_CODE": "LDAP",
        "CALENDAR_OPERATION_CODE": "CALDAV",
        "NOTIFICATION_OPERATION_CODE": "NOTIFICATION",
        "CALENDAR_SUBSCRIPTION_OPERATION_CODE": "CALENDAR_SUBSCRIPTION",
        "APN_OPERATION_CODE": "APN",
        "DOMAIN_CODE": "DOMAIN",
        "CELLULAR_OPERATION_CODE": "CELLULAR"
    };

    publicMethods.getIOSServiceEndpoint = function (operationCode) {
        var featureMap = {
            "DEVICE_LOCK": "lock",
            "VPN": "vpn",
            "RING": "ring",
            "LOCATION": "location",
            "NOTIFICATION": "notification",
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

    /**
     * Convert the ios platform specific code to the generic payload.
     * TODO: think of the possibility to follow a pattern to the key name (namespace?)
     * @param operationCode
     * @param operationPayload
     * @returns {{}}
     */
    privateMethods.generateGenericPayloadFromIOSPayload = function (operationCode, operationPayload) {
        var payload = {};
        operationPayload = JSON.parse(operationPayload);
        switch (operationCode) {
            case iosOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                payload = {
                    "passcodePolicyForcePIN": operationPayload["forcePIN"],
                    "passcodePolicyAllowSimple": operationPayload["allowSimple"],
                    "passcodePolicyRequireAlphanumeric": operationPayload["requireAlphanumeric"],
                    "passcodePolicyMinLength": operationPayload["minLength"],
                    "passcodePolicyMinComplexChars": operationPayload["minComplexChars"],
                    "passcodePolicyMaxPasscodeAgeInDays": operationPayload["maxPINAgeInDays"],
                    "passcodePolicyPasscodeHistory": operationPayload["pinHistory"],
                    "passcodePolicyMaxAutoLock": operationPayload["maxInactivity"],
                    "passcodePolicyGracePeriod": operationPayload["maxGracePeriod"],
                    "passcodePolicyMaxFailedAttempts": operationPayload["maxFailedAttempts"]
                };
                break;
            case iosOperationConstants["RESTRICTIONS_OPERATION_CODE"]:
                payload = {
                    "restrictionsAllowAccountModification": operationPayload["allowAccountModification"],
                    "restrictionsAllowAddingGameCenterFriends": operationPayload["allowAddingGameCenterFriends"],
                    "restrictionsAllowAirDrop": operationPayload["allowAirDrop"],
                    "restrictionsAllowAppCellularDataModification": operationPayload["allowAppCellularDataModification"],
                    "restrictionsAllowAppInstallation": operationPayload["allowAppInstallation"],
                    "restrictionsAllowAppRemoval": operationPayload["allowAppRemoval"],
                    "restrictionsAllowAssistant": operationPayload["allowAssistant"],
                    "restrictionsAllowAssistantUserGeneratedContent": operationPayload["allowAssistantUserGeneratedContent"],
                    "restrictionsAllowAssistantWhileLocked": operationPayload["allowAssistantWhileLocked"],
                    "restrictionsAllowBookstore": operationPayload["allowBookstore"],
                    "restrictionsAllowBookstoreErotica": operationPayload["allowBookstoreErotica"],
                    "restrictionsAllowCamera": operationPayload["allowCamera"],
                    "restrictionsAllowChat": operationPayload["allowChat"],
                    "restrictionsAllowCloudBackup": operationPayload["allowCloudBackup"],
                    "restrictionsAllowCloudDocumentSync": operationPayload["allowCloudDocumentSync"],
                    "restrictionsAllowCloudKeychainSync": operationPayload["allowCloudKeychainSync"],
                    "restrictionsAllowDiagnosticSubmission": operationPayload["allowDiagnosticSubmission"],
                    "restrictionsAllowExplicitContent": operationPayload["allowExplicitContent"],
                    "restrictionsAllowFindMyFriendsModification": operationPayload["allowFindMyFriendsModification"],
                    "restrictionsAllowFingerprintForUnlock": operationPayload["allowFingerprintForUnlock"],
                    "restrictionsAllowGameCenter": operationPayload["allowGameCenter"],
                    "restrictionsAllowGlobalBackgroundFetchWhenRoaming": operationPayload["allowGlobalBackgroundFetchWhenRoaming"],
                    "restrictionsAllowInAppPurchases": operationPayload["allowInAppPurchases"],
                    "restrictionsAllowLockScreenControlCenter": operationPayload["allowLockScreenControlCenter"],
                    "restrictionsAllowHostPairing": operationPayload["allowHostPairing"],
                    "restrictionsAllowLockScreenNotificationsView": operationPayload["allowLockScreenNotificationsView"],
                    "restrictionsAllowLockScreenTodayView": operationPayload["allowLockScreenTodayView"],
                    "restrictionsAllowMultiplayerGaming": operationPayload["allowMultiplayerGaming"],
                    "restrictionsAllowOpenFromManagedToUnmanaged": operationPayload["allowOpenFromManagedToUnmanaged"],
                    "restrictionsAllowOpenFromUnmanagedToManaged": operationPayload["allowOpenFromUnmanagedToManaged"],
                    "restrictionsAllowOTAPKIUpdates": operationPayload["allowOTAPKIUpdates"],
                    "restrictionsAllowPassbookWhileLocked": operationPayload["allowPassbookWhileLocked"],
                    "restrictionsAllowPhotoStream": operationPayload["allowPhotoStream"],
                    "restrictionsAllowSafari": operationPayload["allowSafari"],
                    "restrictionsSafariAllowAutoFill": operationPayload["safariAllowAutoFill"],
                    "restrictionsSafariForceFraudWarning": operationPayload["safariForceFraudWarning"],
                    "restrictionsSafariAllowJavaScript": operationPayload["safariAllowJavaScript"],
                    "restrictionsSafariAllowPopups": operationPayload["safariAllowPopups"],
                    "restrictionsAllowScreenShot": operationPayload["allowScreenShot"],
                    "restrictionsAllowSharedStream": operationPayload["allowSharedStream"],
                    "restrictionsAllowUIConfigurationProfileInstallation": operationPayload["allowUIConfigurationProfileInstallation"],
                    "restrictionsAllowUntrustedTLSPrompt": operationPayload["allowUntrustedTLSPrompt"],
                    "restrictionsAllowVideoConferencing": operationPayload["allowVideoConferencing"],
                    "restrictionsAllowVoiceDialing": operationPayload["allowVoiceDialing"],
                    "restrictionsAllowYouTube": operationPayload["allowYouTube"],
                    "restrictionsAllowITunes": operationPayload["allowiTunes"],
                    "restrictionsForceAssistantProfanityFilter": operationPayload["forceAssistantProfanityFilter"],
                    "restrictionsForceEncryptedBackup": operationPayload["forceEncryptedBackup"],
                    "restrictionsForceITunesStorePasswordEntry": operationPayload["forceITunesStorePasswordEntry"],
                    "restrictionsForceLimitAdTracking": operationPayload["forceLimitAdTracking"],
                    "restrictionsForceAirPlayOutgoingRequestsPairingPassword": operationPayload["forceAirPlayOutgoingRequestsPairingPassword"],
                    "restrictionsForceAirPlayIncomingRequestsPairingPassword": operationPayload["forceAirPlayIncomingRequestsPairingPassword"],
                    "restrictionsAllowManagedAppsCloudSync": operationPayload["allowManagedAppsCloudSync"],
                    "restrictionsAllowEraseContentAndSettings": operationPayload["allowEraseContentAndSettings"],
                    "restrictionsAllowSpotlightInternetResults": operationPayload["allowSpotlightInternetResults"],
                    "restrictionsAllowEnablingRestrictions": operationPayload["allowEnablingRestrictions"],
                    "restrictionsAllowActivityContinuation": operationPayload["allowActivityContinuation"],
                    "restrictionsAllowEnterpriseBookBackup": operationPayload["allowEnterpriseBookBackup"],
                    "restrictionsAllowEnterpriseBookMetadataSync": operationPayload["allowEnterpriseBookMetadataSync"],
                    "restrictionsAllowPodcasts": operationPayload["allowPodcasts"],
                    "restrictionsAllowDefinitionLookup": operationPayload["allowDefinitionLookup"],
                    "restrictionsAllowPredictiveKeyboard": operationPayload["allowPredictiveKeyboard"],
                    "restrictionsAllowAutoCorrection": operationPayload["allowAutoCorrection"],
                    "restrictionsAllowSpellCheck": operationPayload["allowSpellCheck"],
                    "restrictionsSafariAcceptCookies": operationPayload["safariAcceptCookies"],
                    "restrictionsAutonomousSingleAppModePermittedAppIDs": operationPayload["autonomousSingleAppModePermittedAppIDs"]
                };
                break;
            case iosOperationConstants["VPN_OPERATION_CODE"]:
                var pptp = false;
                var l2tp = false;
                if (operationPayload["vpnType"] == "PPTP") {
                    pptp = true;
                } else if (operationPayload["vpnType"] == "L2TP") {
                    l2tp = true;
                }

                payload = {
                    "userDefinedName": operationPayload["userDefinedName"],
                    "overridePrimary": operationPayload["overridePrimary"],
                    "onDemandEnabled": operationPayload["onDemandEnabled"],
                    "onDemandMatchDomainsAlways": operationPayload["onDemandMatchDomainsAlways"],
                    "onDemandMatchDomainsNever": operationPayload["onDemandMatchDomainsNever"],
                    "onDemandMatchDomainsOnRetry": operationPayload["onDemandMatchDomainsOnRetry"],
                    "onDemandRules": operationPayload["onDemandRules"],
                    "vendorConfigs": operationPayload["vendorConfigs"],
                    "vpnType": operationPayload["vpnType"],
                    "pptpAuthName": pptp ? operationPayload.ppp["authName"] : "",
                    "pptpTokenCard": pptp ? operationPayload.ppp["tokenCard"] : "",
                    "pptpAuthPassword": pptp ? operationPayload.ppp["authPassword"] : "",
                    "pptpCommRemoteAddress": pptp ? operationPayload.ppp["commRemoteAddress"] : "",
                    "pptpRSASecureID": pptp ? operationPayload.ppp["RSASecureID"] : "",
                    "pptpCCPEnabled": pptp ? operationPayload.ppp["CCPEnabled"] : "",
                    "pptpCCPMPPE40Enabled": pptp ? operationPayload.ppp["CCPMPPE40Enabled"] : "",
                    "pptpCCPMPPE128Enabled": pptp ? operationPayload.ppp["CCPMPPE128Enabled"] : "",
                    "l2tpAuthName": l2tp ? operationPayload.ppp["authName"] : "",
                    "l2tpTokenCard": l2tp ? operationPayload.ppp["tokenCard"] : "",
                    "l2tpAuthPassword": l2tp ? operationPayload.ppp["authPassword"] : "",
                    "l2tpCommRemoteAddress": l2tp ? operationPayload.ppp["commRemoteAddress"] : "",
                    "l2tpRSASecureID": l2tp ? operationPayload.ppp["RSASecureID"] : "",
                    "ipsecRemoteAddress": operationPayload.ipSec["remoteAddress"],
                    "ipsecAuthenticationMethod": operationPayload.ipSec["authenticationMethod"],
                    "ipsecLocalIdentifier": operationPayload.ipSec["localIdentifier"],
                    "ipsecSharedSecret": operationPayload.ipSec["sharedSecret"],
                    "ipsecPayloadCertificateUUID": operationPayload.ipSec["payloadCertificateUUID"],
                    "ipsecXAuthEnabled": operationPayload.ipSec["XAuthEnabled"],
                    "ipsecXAuthName": operationPayload.ipSec["XAuthName"],
                    "ipsecPromptForVPNPIN": operationPayload.ipSec["promptForVPNPIN"],
                    "ikev2RemoteAddress": operationPayload.ikEv2["remoteAddress"],
                    "ikev2LocalIdentifier": operationPayload.ikEv2["localIdentifier"],
                    "ikev2RemoteIdentifier": operationPayload.ikEv2["remoteIdentifier"],
                    "ikev2AuthenticationMethod": operationPayload.ikEv2["authenticationMethod"],
                    "ikev2SharedSecret": operationPayload.ikEv2["sharedSecret"],
                    "ikev2PayloadCertificateUUID": operationPayload.ikEv2["payloadCertificateUUID"],
                    "ikev2ExtendedAuthEnabled": operationPayload.ikEv2["extendedAuthEnabled"],
                    "ikev2AuthName": operationPayload.ikEv2["authName"],
                    "ikev2AuthPassword": operationPayload.ikEv2["authPassword"],
                    "ikev2DeadPeerDetectionInterval": operationPayload.ikEv2["deadPeerDetectionInterval"],
                    "ikev2ServerCertificateIssuerCommonName": operationPayload.ikEv2["serverCertificateIssuerCommonName"],
                    "ikev2ServerCertificateCommonName": operationPayload.ikEv2["serverCertificateCommonName"]
                };
                break;
            case iosOperationConstants["WIFI_OPERATION_CODE"]:
                payload = {
                    "wifiHiddenNetwork": operationPayload["hiddenNetwork"],
                    "wifiSSID": operationPayload["ssid"],
                    "wifiAutoJoin": operationPayload["autoJoin"],
                    "wifiProxyType": operationPayload["proxyType"],
                    "wifiEncryptionType": operationPayload["encryptionType"],
                    "wifiIsHotSpot": operationPayload["hotspot"],
                    "wifiDomainName": operationPayload["domainName"],
                    "wifiServiceProviderRoamingEnabled": operationPayload["serviceProviderRoamingEnabled"],
                    "wifiDisplayedOperatorName": operationPayload["displayedOperatorName"],
                    "wifiRoamingConsortiumOIs": operationPayload["roamingConsortiumOIs"],
                    "wifiPassword": operationPayload["password"],
                    "wifiPayloadCertUUID": operationPayload["payloadCertificateUUID"],
                    "wifiProxyServer": operationPayload["proxyServer"],
                    "wifiProxyPort": operationPayload["proxyPort"],
                    "wifiProxyUsername": operationPayload["proxyUsername"],
                    "wifiProxyPassword": operationPayload["proxyPassword"],
                    "wifiProxyPACURL": operationPayload["proxyPACURL"],
                    "wifiProxyPACFallbackAllowed": operationPayload["proxyPACFallbackAllowed"],
                    "wifiNAIRealmNames": operationPayload["nairealmNames"],
                    "wifiMCCAndMNCs": operationPayload["mccandMNCs"],
                    "wifiEAPUsername": operationPayload.clientConfiguration["username"],
                    "wifiAcceptedEAPTypes": operationPayload.clientConfiguration["acceptEAPTypes"],
                    "wifiEAPPassword": operationPayload.clientConfiguration["userPassword"],
                    "wifiEAPOneTimePassword": operationPayload.clientConfiguration["oneTimePassword"],
                    "wifiPayloadCertificateAnchorUUIDs": operationPayload.clientConfiguration["payloadCertificateAnchorUUID"],
                    "wifiEAPOuterIdentity": operationPayload.clientConfiguration["outerIdentity"],
                    "wifiTLSTrustedServerNames": operationPayload.clientConfiguration["tlstrustedServerNames"],
                    "wifiEAPTLSAllowTrustExceptions": operationPayload.clientConfiguration["tlsallowTrustExceptions"],
                    "wifiEAPTLSCertIsRequired": operationPayload.clientConfiguration["tlscertificateIsRequired"],
                    "wifiEAPTLSInnerAuthType": operationPayload.clientConfiguration["ttlsinnerAuthentication"],
                    "wifiEAPFastUsePAC": operationPayload.clientConfiguration["eapfastusePAC"],
                    "wifiEAPFastProvisionPAC": operationPayload.clientConfiguration["eapfastprovisionPAC"],
                    "wifiEAPFastProvisionPACAnonymously": operationPayload.clientConfiguration["eapfastprovisionPACAnonymously"],
                    "wifiEAPSIMNoOfRands": operationPayload.clientConfiguration["eapsimnumberOfRANDs"]
                };
                break;
            case iosOperationConstants["EMAIL_OPERATION_CODE"]:
                payload = {
                    "emailAccountDescription":  operationPayload["emailAccountDescription"],
                    "emailAccountName":  operationPayload["emailAccountName"],
                    "emailAccountType":  operationPayload["emailAccountType"],
                    "emailAddress":  operationPayload["emailAddress"],
                    "emailIncomingMailServerAuthentication":  operationPayload["incomingMailServerAuthentication"],
                    "emailIncomingMailServerHostname":  operationPayload["incomingMailServerHostName"],
                    "emailIncomingMailServerPort":  operationPayload["incomingMailServerPortNumber"],
                    "emailIncomingUseSSL":  operationPayload["incomingMailServerUseSSL"],
                    "emailIncomingMailServerUsername":  operationPayload["incomingMailServerUsername"],
                    "emailIncomingMailServerPassword":  operationPayload["incomingPassword"],
                    "emailOutgoingMailServerPassword":  operationPayload["outgoingPassword"],
                    "emailOutgoingPasswordSameAsIncomingPassword":  operationPayload["outgoingPasswordSameAsIncomingPassword"],
                    "emailOutgoingMailServerAuthentication":  operationPayload["outgoingMailServerAuthentication"],
                    "emailOutgoingMailServerHostname":  operationPayload["outgoingMailServerHostName"],
                    "emailOutgoingMailServerPort":  operationPayload["outgoingMailServerPortNumber"],
                    "emailOutgoingUseSSL":  operationPayload["outgoingMailServerUseSSL"],
                    "emailOutgoingMailServerUsername":  operationPayload["outgoingMailServerUsername"],
                    "emailPreventMove":  operationPayload["preventMove"],
                    "emailPreventAppSheet":  operationPayload["preventAppSheet"],
                    "emailDisableMailRecentSyncing":  operationPayload["disableMailRecentSyncing"],
                    "emailIncomingMailServerIMAPPathPrefix":  operationPayload["incomingMailServerIMAPPathPrefix"],
                    "emailSMIMEEnabled":  operationPayload["smimeenabled"],
                    "emailSMIMESigningCertificateUUID":  operationPayload["smimesigningCertificateUUID"],
                    "emailSMIMEEncryptionCertificateUUID":  operationPayload["smimeencryptionCertificateUUID"],
                    "emailSMIMEEnablePerMessageSwitch":  operationPayload["smimeenablePerMessageSwitch"]
                };
                break;
            case iosOperationConstants["AIRPLAY_OPERATION_CODE"]:
                payload = {
                    "airplayDestinations": operationPayload["airPlayDestinations"],
                    "airplayCredentials": operationPayload["airPlayCredentials"]
                };
                break;
            case iosOperationConstants["LDAP_OPERATION_CODE"]:
                payload = {
                    "ldapAccountDescription": operationPayload["accountDescription"],
                    "ldapAccountHostname": operationPayload["accountHostName"],
                    "ldapUseSSL": operationPayload["accountUseSSL"],
                    "ldapAccountUsername": operationPayload["accountUsername"],
                    "ldapAccountPassword": operationPayload["accountPassword"],
                    "ldapSearchSettings": operationPayload["ldapSearchSettings"]
                };
                break;
            case iosOperationConstants["CALENDAR_OPERATION_CODE"]:
                payload = {
                    "calendarAccountDescription": operationPayload["accountDescription"],
                    "calendarAccountHostname": operationPayload["hostName"],
                    "calendarAccountUsername": operationPayload["username"],
                    "calendarAccountPassword": operationPayload["password"],
                    "calendarUseSSL": operationPayload["useSSL"],
                    "calendarAccountPort": operationPayload["port"],
                    "calendarPrincipalURL": operationPayload["principalURL"]
                };
                break;
            case iosOperationConstants["CALENDAR_SUBSCRIPTION_OPERATION_CODE"]:
                payload = {
                    "calendarSubscriptionDescription": operationPayload["accountDescription"],
                    "calendarSubscriptionHostname": operationPayload["hostName"],
                    "calendarSubscriptionUsername": operationPayload["username"],
                    "calendarSubscriptionPassword": operationPayload["password"],
                    "calendarSubscriptionUseSSL": operationPayload["useSSL"]
                };
                break;
            case iosOperationConstants["APN_OPERATION_CODE"]:
                payload = {
                    "apnConfigurations": operationPayload["apnConfigurations"]
                };
                break;
            case iosOperationConstants["CELLULAR_OPERATION_CODE"]:
                payload = {
                    "cellularAttachAPNName": operationPayload["attachAPNName"],
                    "cellularAuthenticationType": operationPayload["authenticationType"],
                    "cellularUsername": operationPayload["username"],
                    "cellularPassword": operationPayload["password"],
                    "cellularAPNConfigurations": operationPayload["apnConfigurations"]
                };
                break;
        }
        return payload;
    };

    privateMethods.generateIOSOperationPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        switch (operationCode) {
            case iosOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "forcePIN": operationData["passcodePolicyForcePIN"],
                        "allowSimple": operationData["passcodePolicyAllowSimple"],
                        "requireAlphanumeric": operationData["passcodePolicyRequireAlphanumeric"],
                        "minLength": operationData["passcodePolicyMinLength"],
                        "minComplexChars": operationData["passcodePolicyMinComplexChars"],
                        "maxPINAgeInDays": operationData["passcodePolicyMaxPasscodeAgeInDays"],
                        "pinHistory": operationData["passcodePolicyPasscodeHistory"],
                        "maxInactivity": operationData["passcodePolicyMaxAutoLock"],
                        "maxGracePeriod": operationData["passcodePolicyGracePeriod"],
                        "maxFailedAttempts": operationData["passcodePolicyMaxFailedAttempts"]
                    }
                };
                break;
            case iosOperationConstants["WIFI_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "ssid": operationData["wifiSSID"],
                        "hiddenNetwork": operationData["wifiHiddenNetwork"],
                        "autoJoin": operationData["wifiAutoJoin"],
                        "proxyType": operationData["wifiProxyType"],
                        "encryptionType": operationData["wifiEncryptionType"],
                        "hotspot": operationData["wifiIsHotSpot"],
                        "domainName": operationData["wifiDomainName"],
                        "serviceProviderRoamingEnabled": operationData["wifiServiceProviderRoamingEnabled"],
                        "displayedOperatorName": operationData["wifiDisplayedOperatorName"],
                        "roamingConsortiumOIs": operationData["wifiRoamingConsortiumOIs"],
                        "password": operationData["wifiPassword"],
                        "clientConfiguration": {
                            "username": operationData["wifiEAPUsername"],
                            "acceptEAPTypes": operationData["wifiAcceptedEAPTypes"],
                            "userPassword": operationData["wifiEAPPassword"],
                            "oneTimePassword": operationData["wifiEAPOneTimePassword"],
                            "payloadCertificateAnchorUUID": operationData["wifiPayloadCertificateAnchorUUIDs"],
                            "outerIdentity": operationData["wifiEAPOuterIdentity"],
                            "tlstrustedServerNames": operationData["wifiTLSTrustedServerNames"],
                            "tlsallowTrustExceptions": operationData["wifiEAPTLSAllowTrustExceptions"],
                            "tlscertificateIsRequired": operationData["wifiEAPTLSCertIsRequired"],
                            "ttlsinnerAuthentication": operationData["wifiEAPTLSInnerAuthType"],
                            "eapfastusePAC": operationData["wifiEAPFastUsePAC"],
                            "eapfastprovisionPAC": operationData["wifiEAPFastProvisionPAC"],
                            "eapfastprovisionPACAnonymously": operationData["wifiEAPFastProvisionPACAnonymously"],
                            "eapsimnumberOfRANDs": operationData["wifiEAPSIMNoOfRands"]
                        },
                        "payloadCertificateUUID": operationData["wifiPayloadCertUUID"],
                        "proxyServer": operationData["wifiProxyServer"],
                        "proxyPort": operationData["wifiProxyPort"],
                        "proxyUsername": operationData["wifiProxyUsername"],
                        "proxyPassword": operationData["wifiProxyPassword"],
                        "proxyPACURL": operationData["wifiProxyPACURL"],
                        "proxyPACFallbackAllowed": operationData["wifiProxyPACFallbackAllowed"],
                        "nairealmNames": operationData["wifiNAIRealmNames"],
                        "mccandMNCs": operationData["wifiMCCAndMNCs"]
                    }
                };
                break;
            case iosOperationConstants["VPN_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                var ppp = {};
                var ipSec = {};
                var ikev2 = {};
                if (operationData["vpnType"] == "PPTP") {
                    ppp = {
                        "authName": operationData["pptpAuthName"],
                        "tokenCard": operationData["pptpTokenCard"],
                        "authPassword": operationData["pptpAuthPassword"],
                        "commRemoteAddress": operationData["pptpCommRemoteAddress"],
                        "RSASecureID": operationData["pptpRSASecureID"],
                        "CCPEnabled": operationData["pptpCCPEnabled"],
                        "CCPMPPE40Enabled": operationData["pptpCCPMPPE40Enabled"],
                        "CCPMPPE128Enabled": operationData["pptpCCPMPPE128Enabled"]
                    };
                } else if (operationData["vpnType"] == "L2TP") {
                    ppp = {
                        "authName": operationData["l2tpAuthName"],
                        "tokenCard": operationData["l2tpTokenCard"],
                        "authPassword": operationData["l2tpAuthPassword"],
                        "commRemoteAddress": operationData["l2tpCommRemoteAddress"],
                        "RSASecureID": operationData["l2tpRSASecureID"]
                    };
                } else if (operationData["vpnType"] == "IPSec") {
                    ipSec = {
                        "remoteAddress" : operationData["ipsecRemoteAddress"],
                        "authenticationMethod" : operationData["ipsecAuthenticationMethod"],
                        "localIdentifier" : operationData["ipsecLocalIdentifier"],
                        "sharedSecret" : operationData["ipsecSharedSecret"],
                        "payloadCertificateUUID" : operationData["ipsecPayloadCertificateUUID"],
                        "XAuthEnabled" : operationData["ipsecXAuthEnabled"],
                        "XAuthName" : operationData["ipsecXAuthName"],
                        "promptForVPNPIN" : operationData["ipsecPromptForVPNPIN"]
                    };
                } else if (operationData["vpnType"] == "IKEv2") {
                    ikev2 = {
                        "remoteAddress" : operationData["ikev2RemoteAddress"],
                        "localIdentifier" : operationData["ikev2LocalIdentifier"],
                        "remoteIdentifier" : operationData["ikev2RemoteIdentifier"],
                        "authenticationMethod" : operationData["ikev2AuthenticationMethod"],
                        "sharedSecret" : operationData["ikev2SharedSecret"],
                        "payloadCertificateUUID" : operationData["ikev2PayloadCertificateUUID"],
                        "extendedAuthEnabled" : operationData["ikev2ExtendedAuthEnabled"],
                        "authName" : operationData["ikev2AuthName"],
                        "authPassword" : operationData["ikev2AuthPassword"],
                        "deadPeerDetectionInterval" : operationData["ikev2DeadPeerDetectionInterval"],
                        "serverCertificateIssuerCommonName" : operationData["ikev2ServerCertificateIssuerCommonName"],
                        "serverCertificateCommonName" : operationData["ikev2ServerCertificateCommonName"]
                    };
                }

                payload = {
                    "operation": {
                        "userDefinedName": operationData["userDefinedName"],
                        "overridePrimary": operationData["overridePrimary"],
                        "onDemandEnabled": operationData["onDemandEnabled"],
                        "onDemandMatchDomainsAlways": operationData["onDemandMatchDomainsAlways"],
                        "onDemandMatchDomainsNever": operationData["onDemandMatchDomainsNever"],
                        "onDemandMatchDomainsOnRetry": operationData["onDemandMatchDomainsOnRetry"],
                        "onDemandRules" : operationData["onDemandRules"],
                        "vendorConfigs" : operationData["vendorConfigs"],
                        "vpnType" : operationData["vpnType"],
                        "ppp": ppp,
                        "ipSec": ipSec,
                        "ikEv2": ikev2
                    }
                };
                break;
            case iosOperationConstants["RESTRICTIONS_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "allowAccountModification": operationData["restrictionsAllowAccountModification"],
                        "allowAddingGameCenterFriends": operationData["restrictionsAllowAddingGameCenterFriends"],
                        "allowAirDrop": operationData["restrictionsAllowAirDrop"],
                        "allowAppCellularDataModification": operationData["restrictionsAllowAppCellularDataModification"],
                        "allowAppInstallation": operationData["restrictionsAllowAppInstallation"],
                        "allowAppRemoval": operationData["restrictionsAllowAppRemoval"],
                        "allowAssistant": operationData["restrictionsAllowAssistant"],
                        "allowAssistantUserGeneratedContent": operationData["restrictionsAllowAssistantUserGeneratedContent"],
                        "allowAssistantWhileLocked": operationData["restrictionsAllowAssistantWhileLocked"],
                        "allowBookstore": operationData["restrictionsAllowBookstore"],
                        "allowBookstoreErotica": operationData["restrictionsAllowBookstoreErotica"],
                        "allowCamera": operationData["restrictionsAllowCamera"],
                        "allowChat": operationData["restrictionsAllowChat"],
                        "allowCloudBackup": operationData["restrictionsAllowCloudBackup"],
                        "allowCloudDocumentSync": operationData["restrictionsAllowCloudDocumentSync"],
                        "allowCloudKeychainSync": operationData["restrictionsAllowCloudKeychainSync"],
                        "allowDiagnosticSubmission": operationData["restrictionsAllowDiagnosticSubmission"],
                        "allowExplicitContent": operationData["restrictionsAllowExplicitContent"],
                        "allowFindMyFriendsModification": operationData["restrictionsAllowFindMyFriendsModification"],
                        "allowFingerprintForUnlock": operationData["restrictionsAllowFingerprintForUnlock"],
                        "allowGameCenter": operationData["restrictionsAllowGameCenter"],
                        "allowGlobalBackgroundFetchWhenRoaming": operationData["restrictionsAllowGlobalBackgroundFetchWhenRoaming"],
                        "allowInAppPurchases": operationData["restrictionsAllowInAppPurchases"],
                        "allowLockScreenControlCenter": operationData["restrictionsAllowLockScreenControlCenter"],
                        "allowHostPairing": operationData["restrictionsAllowHostPairing"],
                        "allowLockScreenNotificationsView": operationData["restrictionsAllowLockScreenNotificationsView"],
                        "allowLockScreenTodayView": operationData["restrictionsAllowLockScreenTodayView"],
                        "allowMultiplayerGaming": operationData["restrictionsAllowMultiplayerGaming"],
                        "allowOpenFromManagedToUnmanaged": operationData["restrictionsAllowOpenFromManagedToUnmanaged"],
                        "allowOpenFromUnmanagedToManaged": operationData["restrictionsAllowOpenFromUnmanagedToManaged"],
                        "allowOTAPKIUpdates": operationData["restrictionsAllowOTAPKIUpdates"],
                        "allowPassbookWhileLocked": operationData["restrictionsAllowPassbookWhileLocked"],
                        "allowPhotoStream": operationData["restrictionsAllowPhotoStream"],
                        "allowSafari": operationData["restrictionsAllowSafari"],
                        "safariAllowAutoFill": operationData["restrictionsSafariAllowAutoFill"],
                        "safariForceFraudWarning": operationData["restrictionsSafariForceFraudWarning"],
                        "safariAllowJavaScript": operationData["restrictionsSafariAllowJavaScript"],
                        "safariAllowPopups": operationData["restrictionsSafariAllowPopups"],
                        "allowScreenShot": operationData["restrictionsAllowScreenShot"],
                        "allowSharedStream": operationData["restrictionsAllowSharedStream"],
                        "allowUIConfigurationProfileInstallation": operationData["restrictionsAllowUIConfigurationProfileInstallation"],
                        "allowUntrustedTLSPrompt": operationData["restrictionsAllowUntrustedTLSPrompt"],
                        "allowVideoConferencing": operationData["restrictionsAllowVideoConferencing"],
                        "allowVoiceDialing": operationData["restrictionsAllowVoiceDialing"],
                        "allowYouTube": operationData["restrictionsAllowYouTube"],
                        "allowiTunes": operationData["restrictionsAllowITunes"],
                        "forceAssistantProfanityFilter": operationData["restrictionsForceAssistantProfanityFilter"],
                        "forceEncryptedBackup": operationData["restrictionsForceEncryptedBackup"],
                        "forceITunesStorePasswordEntry": operationData["restrictionsForceITunesStorePasswordEntry"],
                        "forceLimitAdTracking": operationData["restrictionsForceLimitAdTracking"],
                        "forceAirPlayOutgoingRequestsPairingPassword": operationData["restrictionsForceAirPlayOutgoingRequestsPairingPassword"],
                        "forceAirPlayIncomingRequestsPairingPassword": operationData["restrictionsForceAirPlayIncomingRequestsPairingPassword"],
                        "allowManagedAppsCloudSync": operationData["restrictionsAllowManagedAppsCloudSync"],
                        "allowEraseContentAndSettings": operationData["restrictionsAllowEraseContentAndSettings"],
                        "allowSpotlightInternetResults": operationData["restrictionsAllowSpotlightInternetResults"],
                        "allowEnablingRestrictions": operationData["restrictionsAllowEnablingRestrictions"],
                        "allowActivityContinuation": operationData["restrictionsAllowActivityContinuation"],
                        "allowEnterpriseBookBackup": operationData["restrictionsAllowEnterpriseBookBackup"],
                        "allowEnterpriseBookMetadataSync": operationData["restrictionsAllowEnterpriseBookMetadataSync"],
                        "allowPodcasts": operationData["restrictionsAllowPodcasts"],
                        "allowDefinitionLookup": operationData["restrictionsAllowDefinitionLookup"],
                        "allowPredictiveKeyboard": operationData["restrictionsAllowPredictiveKeyboard"],
                        "allowAutoCorrection": operationData["restrictionsAllowAutoCorrection"],
                        "allowSpellCheck": operationData["restrictionsAllowSpellCheck"],
                        "safariAcceptCookies": operationData["restrictionsSafariAcceptCookies"],
                        "autonomousSingleAppModePermittedAppIDs": operationData["restrictionsAutonomousSingleAppModePermittedAppIDs"]
                    }
                };
                break;
            case iosOperationConstants["EMAIL_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "emailAccountDescription": operationData["emailAccountDescription"],
                        "emailAccountName": operationData["emailAccountName"],
                        "emailAccountType": operationData["emailAccountType"],
                        "emailAddress": operationData["emailAddress"],
                        "incomingMailServerAuthentication": operationData["emailIncomingMailServerAuthentication"],
                        "incomingMailServerHostName": operationData["emailIncomingMailServerHostname"],
                        "incomingMailServerPortNumber": operationData["emailIncomingMailServerPort"],
                        "incomingMailServerUseSSL": operationData["emailIncomingUseSSL"],
                        "incomingMailServerUsername": operationData["emailIncomingMailServerUsername"],
                        "incomingPassword": operationData["emailIncomingMailServerPassword"],
                        "outgoingPassword": operationData["emailOutgoingMailServerPassword"],
                        "outgoingPasswordSameAsIncomingPassword": operationData["emailOutgoingPasswordSameAsIncomingPassword"],
                        "outgoingMailServerAuthentication": operationData["emailOutgoingMailServerAuthentication"],
                        "outgoingMailServerHostName": operationData["emailOutgoingMailServerHostname"],
                        "outgoingMailServerPortNumber": operationData["emailOutgoingMailServerPort"],
                        "outgoingMailServerUseSSL": operationData["emailOutgoingUseSSL"],
                        "outgoingMailServerUsername": operationData["emailOutgoingMailServerUsername"],
                        "preventMove": operationData["emailPreventMove"],
                        "preventAppSheet": operationData["emailPreventAppSheet"],
                        "disableMailRecentSyncing": operationData["emailDisableMailRecentSyncing"],
                        "incomingMailServerIMAPPathPrefix": operationData["emailIncomingMailServerIMAPPathPrefix"],
                        "smimeenabled": operationData["emailSMIMEEnabled"],
                        "smimesigningCertificateUUID": operationData["emailSMIMESigningCertificateUUID"],
                        "smimeencryptionCertificateUUID": operationData["emailSMIMEEncryptionCertificateUUID"],
                        "smimeenablePerMessageSwitch": operationData["emailSMIMEEnablePerMessageSwitch"]
                    }
                };
                break;
            case iosOperationConstants["AIRPLAY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "airPlayDestinations": operationData["airplayDestinations"],
                        "airPlayCredentials": operationData["airplayCredentials"]
                    }
                };
                break;
            case iosOperationConstants["LDAP_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "accountDescription": operationData["ldapAccountDescription"],
                        "accountHostName": operationData["ldapAccountHostname"],
                        "accountUseSSL": operationData["ldapUseSSL"],
                        "accountUsername": operationData["ldapAccountUsername"],
                        "accountPassword": operationData["ldapAccountPassword"],
                        "ldapSearchSettings": operationData["ldapSearchSettings"]
                    }
                };
                break;
            case iosOperationConstants["CALENDAR_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "accountDescription": operationData["calendarAccountDescription"],
                        "hostName": operationData["calendarAccountHostname"],
                        "username": operationData["calendarAccountUsername"],
                        "password": operationData["calendarAccountPassword"],
                        "useSSL": operationData["calendarUseSSL"],
                        "port": operationData["calendarAccountPort"],
                        "principalURL": operationData["calendarPrincipalURL"]
                    }
                };
                break;
            case iosOperationConstants["CALENDAR_SUBSCRIPTION_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "accountDescription": operationData["calendarSubscriptionDescription"],
                        "hostName": operationData["calendarSubscriptionHostname"],
                        "username": operationData["calendarSubscriptionUsername"],
                        "password": operationData["calendarSubscriptionPassword"],
                        "useSSL": operationData["calendarSubscriptionUseSSL"]
                    }
                };
                break;
            case iosOperationConstants["APN_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "apnConfigurations": operationData["apnConfigurations"]
                    }
                };
                break;
            case iosOperationConstants["DOMAIN_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "emailDomains": operationData["emailDomains"],
                        "webDomains": operationData["webDomains"]
                    }
                };
                break;
            case iosOperationConstants["CELLULAR_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "attachAPNName": operationData["cellularAttachAPNName"],
                        "authenticationType": operationData["cellularAuthenticationType"],
                        "username": operationData["cellularUsername"],
                        "password": operationData["cellularPassword"],
                        "apnConfigurations": operationData["cellularAPNConfigurations"]
                    }
                };
                break;
            case iosOperationConstants["NOTIFICATION_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "message" : operationData["message"]
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

    /**
     * Convert the android platform specific code to the generic payload.
     * TODO: think of the possibility to follow a pattern to the key name (namespace?)
     * @param operationCode
     * @param operationPayload
     * @returns {{}}
     */
    privateMethods.generateGenericPayloadFromAndroidPayload = function (operationCode, operationPayload) {
        var payload = {};
        operationPayload = JSON.parse(operationPayload);
        switch (operationCode) {
            case androidOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                payload = {
                    "passcodePolicyAllowSimple": operationPayload["allowSimple"],
                    "passcodePolicyRequireAlphanumeric": operationPayload["requireAlphanumeric"],
                    "passcodePolicyMinLength": operationPayload["minLength"],
                    "passcodePolicyMinComplexChars": operationPayload["minComplexChars"],
                    "passcodePolicyMaxPasscodeAgeInDays": operationPayload["maxPINAgeInDays"],
                    "passcodePolicyPasscodeHistory": operationPayload["pinHistory"],
                    "passcodePolicyMaxFailedAttempts": operationPayload["maxFailedAttempts"]
                };
                break;
            case androidOperationConstants["CAMERA_OPERATION_CODE"]:
                payload = operationPayload;
                break;
            case androidOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"]:
                payload = {
                    "encryptStorageEnabled": operationPayload["encrypted"]
                };
                break;
            case androidOperationConstants["WIFI_OPERATION_CODE"]:
                payload = {
                    "wifiSSID": operationPayload["ssid"],
                    "wifiPassword": operationPayload["password"]
                };
                break;
            case androidOperationConstants["VPN_OPERATION_CODE"]:
                payload = {
                    "serverAddress": operationPayload["serverAddress"],
                    "serverPort": operationPayload["serverPort"],
                    "sharedSecret": operationPayload["sharedSecret"],
                    "dnsServer": operationPayload["dnsServer"]
                };
                break;
        }
        return payload;
    };

    privateMethods.generateAndroidOperationPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        switch (operationCode) {
            case androidOperationConstants["CAMERA_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "CAMERA" : operationData["cameraEnabled"],
                        "DISALLOW_ADJUST_VOLUME" : operationData["disallowAdjustVolumeEnabled"],
                        "DISALLOW_CONFIG_BLUETOOTH" : operationData["disallowConfigBluetooth"],
                        "DISALLOW_CONFIG_CELL_BROADCASTS" : operationData["disallowConfigCellBroadcasts"],
                        "DISALLOW_CONFIG_CREDENTIALS" : operationData["disallowConfigCredentials"],
                        "DISALLOW_CONFIG_MOBILE_NETWORKS" : operationData["disallowConfigMobileNetworks"],
                        "DISALLOW_CONFIG_TETHERING" : operationData["disallowConfigTethering"],
                        "DISALLOW_CONFIG_VPN" : operationData["disallowConfigVpn"],
                        "DISALLOW_CONFIG_WIFI" : operationData["disallowConfigWifi"],
                        "DISALLOW_APPS_CONTROL" : operationData["disallowAppControl"],
                        "DISALLOW_CREATE_WINDOWS" : operationData["disallowCreateWindows"],
                        "DISALLOW_CROSS_PROFILE_COPY_PASTE" : operationData["disallowCrossProfileCopyPaste"],
                        "DISALLOW_DEBUGGING_FEATURES" : operationData["disallowDebugging"],
                        "DISALLOW_FACTORY_RESET" : operationData["disallowFactoryReset"],
                        "DISALLOW_ADD_USER" : operationData["disallowAddUser"],
                        "DISALLOW_INSTALL_APPS" : operationData["disallowInstallApps"],
                        "DISALLOW_INSTALL_UNKNOWN_SOURCES" : operationData["disallowInstallUnknownSources"],
                        "DISALLOW_MODIFY_ACCOUNTS" : operationData["disallowModifyAccounts"],
                        "DISALLOW_MOUNT_PHYSICAL_MEDIA" : operationData["disallowMountPhysicalMedia"],
                        "DISALLOW_NETWORK_RESET" : operationData["disallowNetworkReset"],
                        "DISALLOW_OUTGOING_BEAM" : operationData["disallowOutgoingBeam"],
                        "DISALLOW_OUTGOING_CALLS" : operationData["disallowOutgoingCalls"],
                        "DISALLOW_REMOVE_USER" : operationData["disallowRemoveUser"],
                        "DISALLOW_SAFE_BOOT" : operationData["disallowSafeBoot"],
                        "DISALLOW_SHARE_LOCATION" : operationData["disallowLocationSharing"],
                        "DISALLOW_SMS" : operationData["disallowSMS"],
                        "DISALLOW_UNINSTALL_APPS" : operationData["disallowUninstallApps"],
                        "DISALLOW_UNMUTE_MICROPHONE" : operationData["disallowUnmuteMicrophone"],
                        "DISALLOW_USB_FILE_TRANSFER" : operationData["disallowUSBFileTransfer"],
                        "ALLOW_PARENT_PROFILE_APP_LINKING" : operationData["disallowParentProfileAppLinking"],
                        "ENSURE_VERIFY_APPS" : operationData["ensureVerifyApps"],
                        "AUTO_TIME" : operationData["enableAutoTime"],
                        "SET_SCREEN_CAPTURE_DISABLED" : operationData["diableScreenCapture"],
                        "SET_STATUS_BAR_DISABLED" : operationData["disableStatusBar"]
                    }
                };
                break;
            case androidOperationConstants["CHANGE_LOCK_CODE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "lockCode" : operationData["lockCode"]
                    }
                };
                break;
            case androidOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "encrypted" : operationData["encryptStorageEnabled"]
                    }
                };
                break;
            case androidOperationConstants["NOTIFICATION_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "message" : operationData["message"]
                    }
                };
                break;
            case androidOperationConstants["UPGRADE_FIRMWARE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "schedule" : operationData["schedule"]
                    }
                };
                break;
            case androidOperationConstants["WIPE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "pin" : operationData["pin"]
                    }
                };
                break;
            case androidOperationConstants["WIFI_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "ssid": operationData["wifiSSID"],
                        "password": operationData["wifiPassword"]
                    }
                };
                break;
            case androidOperationConstants["VPN_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "serverAddress": operationData["serverAddress"],
                        "serverPort": operationData["serverPort"],
                        "sharedSecret": operationData["sharedSecret"],
                        "dnsServer": operationData["dnsServer"]
                    }
                };
                break;
            case androidOperationConstants["LOCK_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "message" : operationData["lock-message"],
                        "isHardLockEnabled" : operationData["hard-lock"]
                    }
                };
                break;
            case androidOperationConstants["WORK_PROFILE_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "profileName": operationData["workProfilePolicyProfileName"],
                        "enableSystemApps": operationData["workProfilePolicyEnableSystemApps"],
                        "hideSystemApps": operationData["workProfilePolicyHideSystemApps"],
                        "unhideSystemApps": operationData["workProfilePolicyUnhideSystemApps"],
                        "enablePlaystoreApps": operationData["workProfilePolicyEnablePlaystoreApps"]
                    }
                };
                break;
            case androidOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "allowSimple": operationData["passcodePolicyAllowSimple"],
                        "requireAlphanumeric": operationData["passcodePolicyRequireAlphanumeric"],
                        "minLength": operationData["passcodePolicyMinLength"],
                        "minComplexChars": operationData["passcodePolicyMinComplexChars"],
                        "maxPINAgeInDays": operationData["passcodePolicyMaxPasscodeAgeInDays"],
                        "pinHistory": operationData["passcodePolicyPasscodeHistory"],
                        "maxFailedAttempts": operationData["passcodePolicyMaxFailedAttempts"]
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
            "VPN": "vpn",
            "DEVICE_LOCK": "lock",
            "DEVICE_UNLOCK": "unlock",
            "DEVICE_LOCATION": "location",
            "CLEAR_PASSWORD": "clear-password",
            "APPLICATION_LIST": "get-application-list",
            "DEVICE_RING": "ring-device",
            "DEVICE_REBOOT": "reboot-device",
            "UPGRADE_FIRMWARE": "upgrade-firmware",
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
     * Convert the windows platform specific code to the generic payload.
     * TODO: think of the possibility to follow a pattern to the key name (namespace?)
     * @param operationCode
     * @param operationPayload
     * @returns {{}}
     */
    privateMethods.generateGenericPayloadFromWindowsPayload = function (operationCode, operationPayload) {
        var payload = {};
        operationPayload = JSON.parse(operationPayload);
        switch (operationCode) {
            case windowsOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                payload = {
                    "passcodePolicyAllowSimple": operationPayload["allowSimple"],
                    "passcodePolicyRequireAlphanumeric": operationPayload["requireAlphanumeric"],
                    "passcodePolicyMinLength": operationPayload["minLength"],
                    "passcodePolicyMinComplexChars": operationPayload["minComplexChars"],
                    "passcodePolicyMaxPasscodeAgeInDays": operationPayload["maxPINAgeInDays"],
                    "passcodePolicyPasscodeHistory": operationPayload["pinHistory"],
                    "passcodePolicyMaxFailedAttempts": operationPayload["maxFailedAttempts"]
                };
                break;
            case windowsOperationConstants["CAMERA_OPERATION_CODE"]:
                payload = {
                    "cameraEnabled": operationPayload["enabled"]
                };
                break;
            case windowsOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"]:
                payload = {
                    "encryptStorageEnabled": operationPayload["encrypted"]
                };
                break;
        }
        return payload;
    };

    privateMethods.generateWindowsOperationPayload = function (operationCode, operationData, deviceList) {
        var payload;
        var operationType;
        switch (operationCode) {
            case windowsOperationConstants["CAMERA_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "enabled" : operationData["cameraEnabled"]
                    }
                };
                break;
            case windowsOperationConstants["CHANGE_LOCK_CODE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "lockCode" : operationData["lockCode"]
                    }
                };
                break;
            case windowsOperationConstants["ENCRYPT_STORAGE_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "encrypted" : operationData["encryptStorageEnabled"]
                    }
                };
                break;
            case windowsOperationConstants["NOTIFICATION_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "message" : operationData["message"]
                    }
                };
                break;
            case windowsOperationConstants["PASSCODE_POLICY_OPERATION_CODE"]:
                operationType = operationTypeConstants["PROFILE"];
                payload = {
                    "operation": {
                        "allowSimple": operationData["passcodePolicyAllowSimple"],
                        "requireAlphanumeric": operationData["passcodePolicyRequireAlphanumeric"],
                        "minLength": operationData["passcodePolicyMinLength"],
                        "minComplexChars": operationData["passcodePolicyMinComplexChars"],
                        "maxPINAgeInDays": operationData["passcodePolicyMaxPasscodeAgeInDays"],
                        "pinHistory": operationData["passcodePolicyPasscodeHistory"],
                        "maxFailedAttempts": operationData["passcodePolicyMaxFailedAttempts"]
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


    publicMethods.getWindowsServiceEndpoint = function (operationCode) {
        var featureMap = {
            "CAMERA": "camera",
            "DEVICE_LOCK": "lock",
            "DEVICE_LOCATION": "location",
            "CLEAR_PASSWORD": "clear-password",
            "APPLICATION_LIST": "get-application-list",
            "DEVICE_RING": "ring-device",
            "DEVICE_REBOOT": "reboot-device",
            "UPGRADE_FIRMWARE": "upgrade-firmware",
            "DEVICE_MUTE": "mute",
            "LOCK_RESET": "lock-reset",
            "NOTIFICATION": "notification",
            "ENCRYPT_STORAGE": "encrypt",
            "CHANGE_LOCK_CODE": "change-lock-code",
            "WEBCLIP": "webclip",
            "INSTALL_APPLICATION": "install-application",
            "UNINSTALL_APPLICATION": "uninstall-application",
            "BLACKLIST_APPLICATIONS": "blacklist-applications",
            "PASSCODE_POLICY": "password-policy",
            "ENTERPRISE_WIPE": "enterprise-wipe",
            "WIPE_DATA": "wipe-data",
            "DISENROLL": "disenroll"
        };
        return "/mdm-windows-agent/services/windows/operation/" + featureMap[operationCode];
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
            "ENTERPRISE_WIPE": "fw-clear",
            "WIPE_DATA": "fw-database",
            "DEVICE_RING": "fw-dial-up",
            "DEVICE_REBOOT": "fw-refresh",
            "UPGRADE_FIRMWARE": "fw-up-arrow",
            "DEVICE_MUTE": "fw-mute",
            "NOTIFICATION": "fw-message",
            "CHANGE_LOCK_CODE": "fw-security",
            "DEVICE_UNLOCK": "fw-lock"
        };
        return featureMap[operationCode];
    };

    /**
     * Get the icon for the featureCode
     * @param operationCode
     * @returns icon class
     */
    publicMethods.getWindowsIconForFeature = function (operationCode) {
        var featureMap = {
            "DEVICE_LOCK": "fw-lock",
            "DEVICE_LOCATION": "fw-map-location",
            "DISENROLL": "fw-delete",
            "WIPE_DATA": "fw-clear",
            "DEVICE_RING": "fw-dial-up",
            "DEVICE_REBOOT": "fw-refresh",
            "UPGRADE_FIRMWARE": "fw-up-arrow",
            "DEVICE_MUTE": "fw-incoming-call",
            "NOTIFICATION": "fw-message",
            "LOCK_RESET": "fw-key"
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
            "ENTERPRISE_WIPE": "fw-clear",
            "NOTIFICATION": "fw-message",
            "RING": "fw-dial-up"
        };
        return featureMap[operationCode];
    };

    /**
     * Filter a list by a data attribute.
     * @param prop
     * @param val
     * @returns {Array}
     */
    $.fn.filterByData = function (prop, val) {
        return this.filter(
            function () {return $(this).data(prop) == val;}
        );
    };

    /**
     * Method to generate Platform specific operation payload.
     *
     * @param platformType Platform Type of the profile
     * @param operationCode Operation Codes to generate the profile from
     * @param deviceList Optional device list to include in payload body for operations
     * @returns {*}
     */
    publicMethods.generatePayload = function (platformType, operationCode, deviceList) {
        var payload;
        var operationData = {};
        // capturing form input data designated by .operationDataKeys
        $(".operation-data").filterByData("operation-code", operationCode).find(".operationDataKeys").each(
            function () {
                var operationDataObj = $(this);
                var key = operationDataObj.data("key");
                var value;
                if (operationDataObj.is(":text") || operationDataObj.is("textarea") ||
                    operationDataObj.is(":password")) {
                    value = operationDataObj.val();
                } else if (operationDataObj.is(":checkbox")) {
                    value = operationDataObj.is(":checked");
                } else if (operationDataObj.is("select")) {
                    value = operationDataObj.find("option:selected").attr("value");
                } else if (operationDataObj.hasClass("grouped-array-input")) {
                    value = [];
                    var childInput;
                    var childInputValue;
                    if (operationDataObj.hasClass("one-column-input-array")) {
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            if (childInput.is(":text") || childInput.is("textarea") || childInput.is(":password")) {
                                childInputValue = childInput.val();
                            } else if (childInput.is(":checkbox")) {
                                childInputValue = childInput.is(":checked");
                            } else if (childInput.is("select")) {
                                childInputValue = childInput.find("option:selected").attr("value");
                            }
                            // push to value
                            value.push(childInputValue);
                        });
                    } else if (operationDataObj.hasClass("valued-check-box-array")) {
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            if (childInput.is(":checked")) {
                                // get associated value with check-box
                                childInputValue = childInput.data("value");
                                // push to value
                                value.push(childInputValue);
                            }
                        });
                    } else if (operationDataObj.hasClass("multi-column-joined-input-array")) {
                        var columnCount = operationDataObj.data("column-count");
                        var inputCount = 0;
                        var joinedInput;
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            if (childInput.is(":text") || childInput.is("textarea") || childInput.is(":password")) {
                                childInputValue = childInput.val();
                            } else if (childInput.is(":checkbox")) {
                                childInputValue = childInput.is(":checked");
                            } else if (childInput.is("select")) {
                                childInputValue = childInput.find("option:selected").attr("value");
                            }
                            inputCount++;
                            if (inputCount % columnCount == 1) {
                                // initialize joinedInput value
                                joinedInput = "";
                                // append childInputValue to joinedInput
                                joinedInput += childInputValue;
                            } else if ((inputCount % columnCount) >= 2) {
                                // append childInputValue to joinedInput
                                joinedInput += childInputValue;
                            } else {
                                // append childInputValue to joinedInput
                                joinedInput += childInputValue;
                                // push to value
                                value.push(joinedInput);
                            }
                        });
                    } else if (operationDataObj.hasClass("multi-column-key-value-pair-array")) {
                        columnCount = operationDataObj.data("column-count");
                        inputCount = 0;
                        var childInputKey;
                        var keyValuePairJson;
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            childInputKey = childInput.data("child-key");
                            if (childInput.is(":text") || childInput.is("textarea") || childInput.is(":password")) {
                                childInputValue = childInput.val();
                            } else if (childInput.is(":checkbox")) {
                                childInputValue = childInput.is(":checked");
                            } else if (childInput.is("select")) {
                                childInputValue = childInput.find("option:selected").attr("value");
                            }
                            inputCount++;
                            if ((inputCount % columnCount) == 1) {
                                // initialize keyValuePairJson value
                                keyValuePairJson = {};
                                // set key-value-pair
                                keyValuePairJson[childInputKey] = childInputValue;
                            } else if ((inputCount % columnCount) >= 2) {
                                // set key-value-pair
                                keyValuePairJson[childInputKey] = childInputValue;
                            } else {
                                keyValuePairJson = {};
                                // set key-value-pair
                                keyValuePairJson[childInputKey] = childInputValue;
                                // push to value
                                value.push(keyValuePairJson);
                            }
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
            case platformTypeConstants["WINDOWS"]:
                payload = privateMethods.generateWindowsOperationPayload(operationCode, operationData, deviceList);
                break;
        }
        return payload;
    };

    /**
     * Method to populate the Platform specific operation payload.
     *
     * @param platformType Platform Type of the profile
     * @param operationCode Operation Codes to generate the profile from
     * @param operationPayload payload
     * @returns {*}
     */
    publicMethods.populateUI = function (platformType, operationCode, operationPayload) {
        var uiPayload;
        switch (platformType) {
            case platformTypeConstants["ANDROID"]:
                uiPayload = privateMethods.generateGenericPayloadFromAndroidPayload(operationCode, operationPayload);
                break;
            case platformTypeConstants["IOS"]:
                uiPayload = privateMethods.generateGenericPayloadFromIOSPayload(operationCode, operationPayload);
                break;
            case platformTypeConstants["WINDOWS"]:
                uiPayload = privateMethods.generateGenericPayloadFromWindowsPayload(operationCode, operationPayload);
                break;
        }
        // capturing form input data designated by .operationDataKeys
        $(".operation-data").filterByData("operation-code", operationCode).find(".operationDataKeys").each(
            function () {
                var operationDataObj = $(this);
                //TODO :remove
                //operationDataObj.prop('disabled', true)
                var key = operationDataObj.data("key");
                // retrieve corresponding input value associated with the key
                var value = uiPayload[key];
                // populating input value according to the type of input
                if (operationDataObj.is(":text") ||
                    operationDataObj.is("textarea") ||
                    operationDataObj.is(":password")) {
                    operationDataObj.val(value);
                } else if (operationDataObj.is(":checkbox")) {
                    operationDataObj.prop("checked", value);
                } else if (operationDataObj.is("select")) {
                    operationDataObj.val(value);
                    /* trigger a change of value, so that if slidable panes exist,
                     make them slide-down or slide-up accordingly */
                    operationDataObj.trigger("change");
                } else if (operationDataObj.hasClass("grouped-array-input")) {
                    // then value is complex
                    var i, childInput;
                    var childInputIndex = 0;
                    // var childInputValue;
                    if (operationDataObj.hasClass("one-column-input-array")) {
                        // generating input fields to populate complex value
                        for (i = 0; i < value.length; ++i) {
                            operationDataObj.parent().find("a").filterByData("click-event", "add-form").click();
                        }
                        // traversing through each child input
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            var childInputValue = value[childInputIndex];
                            // populating extracted value in the UI according to the input type
                            if (childInput.is(":text") ||
                                childInput.is("textarea") ||
                                childInput.is(":password") ||
                                childInput.is("select")) {
                                childInput.val(childInputValue);
                            } else if (childInput.is(":checkbox")) {
                                operationDataObj.prop("checked", childInputValue);
                            }
                            // incrementing childInputIndex
                            childInputIndex++;
                        });
                    } else if (operationDataObj.hasClass("valued-check-box-array")) {
                        // traversing through each child input
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            // check if corresponding value of current checkbox exists in the array of values
                            if (value.indexOf(childInput.data("value")) != -1) {
                                // if YES, set checkbox as checked
                                childInput.prop("checked", true);
                            }
                        });
                    } else if (operationDataObj.hasClass("multi-column-joined-input-array")) {
                        // generating input fields to populate complex value
                        for (i = 0; i < value.length; ++i) {
                            operationDataObj.parent().find("a").filterByData("click-event", "add-form").click();
                        }
                        var columnCount = operationDataObj.data("column-count");
                        var multiColumnJoinedInputArrayIndex = 0;
                        // handling scenarios specifically
                        if (operationDataObj.attr("id") == "wifi-mcc-and-mncs") {
                            // traversing through each child input
                            $(".child-input", this).each(function () {
                                childInput = $(this);
                                var multiColumnJoinedInput = value[multiColumnJoinedInputArrayIndex];
                                var childInputValue;
                                if ((childInputIndex % columnCount) == 0) {
                                    childInputValue = multiColumnJoinedInput.substring(3, 0)
                                } else {
                                    childInputValue = multiColumnJoinedInput.substring(3);
                                    // incrementing childInputIndex
                                    multiColumnJoinedInputArrayIndex++;
                                }
                                // populating extracted value in the UI according to the input type
                                if (childInput.is(":text") ||
                                    childInput.is("textarea") ||
                                    childInput.is(":password") ||
                                    childInput.is("select")) {
                                    childInput.val(childInputValue);
                                } else if (childInput.is(":checkbox")) {
                                    operationDataObj.prop("checked", childInputValue);
                                }
                                // incrementing childInputIndex
                                childInputIndex++;
                            });
                        }
                    } else if (operationDataObj.hasClass("multi-column-key-value-pair-array")) {
                        // generating input fields to populate complex value
                        for (i = 0; i < value.length; ++i) {
                            operationDataObj.parent().find("a").filterByData("click-event", "add-form").click();
                        }
                        columnCount = operationDataObj.data("column-count");
                        var multiColumnKeyValuePairArrayIndex = 0;
                        // traversing through each child input
                        $(".child-input", this).each(function () {
                            childInput = $(this);
                            var multiColumnKeyValuePair = value[multiColumnKeyValuePairArrayIndex];
                            var childInputKey = childInput.data("child-key");
                            var childInputValue = multiColumnKeyValuePair[childInputKey];
                            // populating extracted value in the UI according to the input type
                            if (childInput.is(":text") ||
                                childInput.is("textarea") ||
                                childInput.is(":password") ||
                                childInput.is("select")) {
                                childInput.val(childInputValue);
                            } else if (childInput.is(":checkbox")) {
                                operationDataObj.prop("checked", childInputValue);
                            }
                            // incrementing multiColumnKeyValuePairArrayIndex for the next row of inputs
                            if ((childInputIndex % columnCount) == (columnCount - 1)) {
                                multiColumnKeyValuePairArrayIndex++;
                            }
                            // incrementing childInputIndex
                            childInputIndex++;
                        });
                    }
                }
            }
        );
    };

    /**
     * generateProfile method is only used for policy-creation UIs.
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

            if(platformType == platformTypeConstants["ANDROID"] &&
                operationCodes[i] == androidOperationConstants["CAMERA_OPERATION_CODE"]){
                var operations = payload["operation"];
                for (var key in operations){
                    operationCode = key;
                    var restriction = false;
                    if(operations[key]){
                        restriction = true;
                    }
                    var payloadResult = {
                        "operation": {
                            "enabled" : restriction
                        }
                    };
                    generatedProfile[operationCode] = payloadResult["operation"];
                }

            } else {
                generatedProfile[operationCode] = payload["operation"];
            }
        }
        console.log(generatedProfile);
        return generatedProfile;
    };

    /**
     * populateProfile method is used to populate the html ui with saved payload.
     *
     * @param platformType Platform Type of the profile
     * @param payload List of profileFeatures
     * @returns [] configuredOperations array
     */
    publicMethods.populateProfile = function (platformType, payload) {
        var i, configuredOperations = [];
        var restrictions = {};
        for (i = 0; i < payload.length; ++i) {
            var configuredFeature = payload[i];
            var featureCode = configuredFeature["featureCode"];
            var operationPayload = configuredFeature["content"];
            if(platformType == platformTypeConstants["ANDROID"]){
                var restriction  = JSON.parse(operationPayload);
                if(featureCode == androidOperationConstants["CAMERA_OPERATION_CODE"]){
                    restrictions["cameraEnabled"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_ADJUST_VOLUME"]){
                    restrictions["disallowAdjustVolumeEnabled"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_BLUETOOTH"]){
                    restrictions["disallowConfigBluetooth"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_CELL_BROADCASTS"]){
                    restrictions["disallowConfigCellBroadcasts"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_CREDENTIALS"]){
                    restrictions["disallowConfigCredentials"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_MOBILE_NETWORKS"]){
                    restrictions["disallowConfigMobileNetworks"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_TETHERING"]){
                    restrictions["disallowConfigTethering"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_VPN"]){
                    restrictions["disallowConfigVpn"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CONFIG_WIFI"]){
                    restrictions["disallowConfigWifi"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_APPS_CONTROL"]){
                    restrictions["disallowAppControl"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CREATE_WINDOWS"]){
                    restrictions["disallowCreateWindows"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_CROSS_PROFILE_COPY_PASTE"]){
                    restrictions["disallowCrossProfileCopyPaste"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_DEBUGGING_FEATURES"]){
                    restrictions["disallowDebugging"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_FACTORY_RESET"]){
                    restrictions["disallowFactoryReset"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_ADD_USER"]){
                    restrictions["disallowAddUser"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_INSTALL_APPS"]){
                    restrictions["disallowInstallApps"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_INSTALL_UNKNOWN_SOURCES"]){
                    restrictions["disallowInstallUnknownSources"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_MODIFY_ACCOUNTS"]){
                    restrictions["disallowModifyAccounts"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_MOUNT_PHYSICAL_MEDIA"]){
                    restrictions["disallowMountPhysicalMedia"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_NETWORK_RESET"]){
                    restrictions["disallowNetworkReset"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_OUTGOING_BEAM"]){
                    restrictions["disallowOutgoingBeam"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_OUTGOING_CALLS"]){
                    restrictions["disallowOutgoingCalls"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_REMOVE_USER"]){
                    restrictions["disallowRemoveUser"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_SAFE_BOOT"]){
                    restrictions["disallowSafeBoot"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_SHARE_LOCATION"]){
                    restrictions["disallowLocationSharing"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_SMS"]){
                    restrictions["disallowSMS"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_UNINSTALL_APPS"]){
                    restrictions["disallowUninstallApps"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_UNMUTE_MICROPHONE"]){
                    restrictions["disallowUnmuteMicrophone"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["DISALLOW_USB_FILE_TRANSFER"]){
                    restrictions["disallowUSBFileTransfer"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["ALLOW_PARENT_PROFILE_APP_LINKING"]){
                    restrictions["disallowParentProfileAppLinking"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["ENSURE_VERIFY_APPS"]){
                    restrictions["ensureVerifyApps"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["AUTO_TIME"]){
                    restrictions["enableAutoTime"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["SET_SCREEN_CAPTURE_DISABLED"]){
                    restrictions["diableScreenCapture"] = restriction["enabled"];
                    continue;
                } else if (featureCode == androidOperationConstants["SET_STATUS_BAR_DISABLED"]){
                    restrictions["disableStatusBar"] = restriction["enabled"];
                    continue;
                }
            }
            //push the feature-code to the configuration array
            configuredOperations.push(featureCode);
            publicMethods.populateUI(platformType, featureCode, operationPayload);
        }
        if(restrictions){
            configuredOperations.push(androidOperationConstants["CAMERA_OPERATION_CODE"]);
            publicMethods.populateUI(platformType, androidOperationConstants["CAMERA_OPERATION_CODE"], JSON.stringify(restrictions));
        }
        return configuredOperations;
    };

    return publicMethods;
}();
