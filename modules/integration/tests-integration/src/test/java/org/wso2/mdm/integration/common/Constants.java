/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.integration.common;

import java.io.File;
/**
 * Constants used through out the test suite are defined here.
 */
public final class Constants {

    public static final String DEVICE_ID = "1234";
    public static final String DEVICE_ID_2 = "1235";
    public static final String DEVICE_ID_3 = "1236";
    public static final String DEVICE_ID_4 = "1237";
    public static final String DEVICE_ID_5 = "1238";
    public static final String DEVICE_ID_6 = "1239";
    public static final String DEVICE_ID_7 = "1240";
    public static final String DEVICE_ID_8 = "1241";
    public static final String DEVICE_ID_9 = "1242";
    public static final String DEVICE_ID_10 = "1243";

    public static final String NUMBER_NOT_EQUAL_TO_DEVICE_ID = "1111";
    public static final String DEVICE_IMEI ="123123123";
    public static final String AUTOMATION_CONTEXT = "MDM";
    public static final String APPLICATION_JSON = "application/json";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String ANDROID_DEVICE_TYPE = "android";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String DEVICE_IDENTIFIER_KEY = "deviceIdentifier";
    public static final String DEVICE_IDENTIFIERS_KEY = "deviceIDs";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_SOAP_XML = "application/soap+xml; charset=utf-8";
    public static final String UTF8 = "UTF-8";
    public static final String UTF16 = "UTF-16";
    public static final String ZERO = "0";
    public static final String EMPTY_ARRAY = "[]";

    public static final class DynamicClientAuthentication {
        private DynamicClientAuthentication() {
            throw new AssertionError();
        }
        private static StringBuffer dynamicClientPayloadBuffer = new StringBuffer();
        public static final String DYNAMIC_CLIENT_REGISTRATION_PAYLOAD = dynamicClientPayloadBuffer.append("{\"clientName\":").
                append(" \"device\",\"owner\": \"admin\",\"grantType\": \"password\",\"callbackUrl\": \"www.google.lk\"," +
                       "\"saasApp\": \"" + true + "\"}").toString();
        public static final String REGISTRATION_ENDPOINT = "/dynamic-client-web/register";
        public static final String TOKEN_ENDPOINT = "/oauth2/token";
        public static final String OAUTH_TOKEN_PAYLOAD = "grant_type=password&username=admin&password=admin&scope=prod";
    }

	public static final class AndroidEnrollment {
		private AndroidEnrollment() {
			throw new AssertionError();
		}
        public static final String DEVICE_TWO_ENROLLMENT_DATA = "DEVICE_TWO";
        public static final String DEVICE_THREE_ENROLLMENT_DATA = "DEVICE_THREE";
        public static final String DEVICE_FOUR_ENROLLMENT_DATA = "DEVICE_FOUR";
        public static final String DEVICE_FIVE_ENROLLMENT_DATA = "DEVICE_FIVE";
        public static final String DEVICE_SIX_ENROLLMENT_DATA= "DEVICE_SIX";
        public static final String DEVICE_SEVEN_ENROLLMENT_DATA= "DEVICE_SEVEN";
        public static final String DEVICE_EIGHT_ENROLLMENT_DATA = "DEVICE_EIGHT";
        public static final String DEVICE_NINE_ENROLLMENT_DATA = "DEVICE_NINE";
        public static final String DEVICE_TEN_ENROLLMENT_DATA = "DEVICE_TEN";

        public static final String ENROLLMENT_PAYLOAD_FILE_NAME = "android-enrollment-payloads.json";
        public static final String ENROLLMENT_ERRONEOUS_PAYLOAD_FILE_NAME = "android-enrollment-erroneous-payloads.json";
        public static final String ENROLLMENT_RESPONSE_PAYLOAD_FILE_NAME = "android-enrollment-response-payloads.json";
        public static final String ENROLLMENT_ERRONEOUS_RESPONSE_PAYLOAD_FILE_NAME =
                                                                "android-enrollment-erroneous-response-payloads.json";
        public static final String ENROLLMENT_ADDITIONAL_DEVICES_PAYLOAD_FILE_NAME = "android-enrollment-additional-devices-payloads.json";
        public static final String ENROLLMENT_ENDPOINT = "/mdm-android-agent/enrollment/";
        public static final String ENROLLMENT_GROUP = "android-enrollment";
	}

    public static final class AndroidPolicy {
        private AndroidPolicy() {
            throw new AssertionError();
        }
        public static final String POLICY_RESPONSE_PAYLOAD_FILE_NAME = "android-policy-response-payloads.json";
        public static final String POLICY_ERRONEOUS_RESPONSE_PAYLOAD_FILE_NAME = "android-policy-erroneous-response-payloads.json";
        public static final String POLICY_ENDPOINT = "/mdm-android-agent/policy/";
        public static final String POLICY_GROUP = "android-policy";
        public static final String GET_EFFECTIVE_POLICY = "getEffectivePolicy";
    }

    public static final class WindowsEnrollment {
        private WindowsEnrollment() {
            throw new AssertionError();
        }

        public static final String DISCOVERY_GET_URL = "/mdm-windows-agent/services/discovery/get";
        public static final String DISCOVERY_POST_URL = "/mdm-windows-agent/services/discovery/post";
        public static final String BSD_URL = "/mdm-windows-agent/services/federated/bst/authentication";
        public static final String MS_EXCEP = "/mdm-windows-agent/services/certificatepolicy/xcep";
        public static final String WINDOWS_ENROLLMENT_GROUP = "windows-enrollment";
        public static final String WSTEP_URL = "/mdm-windows-agent/services/deviceenrolment/wstep";
        public static final String SYNC_ML_URL = "/mdm-windows-agent/services/syncml/devicemanagement/request";
        public static final String DISCOVERY_POST_FILE = "windows" + File.separator + "enrollment" + File
                .separator + "discovery-post.xml";
        public static final String MS_XCEP_FILE =
                "windows" + File.separator + "enrollment" + File.separator + "ms_xcep.xml";
        public static final String WS_STEP_FILE =
                "windows" + File.separator + "enrollment" + File.separator + "wstep.xml";
        public static final String SYNCML_FILE =
                "windows" + File.separator + "enrollment" + File.separator + "inital_device_info.xml";
        public static final String BSD_PAYLOAD = "{\"credentials\" : {\"username\" : \"admin\", \"email\" : \"admin@wso2.com\", " +
                                                 "\"password\" : \"admin\", \"ownership\" : \"BYOD\", " +
                                                 "\"token\" : \"cbe53efd46ec612c456540f8dfef5428\"}}";
    }

    public static final class AndroidOperations {
        private AndroidOperations() {
            throw new AssertionError();
        }

        public static final String OPERATION_PAYLOAD_FILE_NAME = "android-operation-payloads.json";
        public static final String OPERATIONS_GROUP = "operations";
        public static final String COMMAND_OPERATION_PAYLOAD = "[\"" + DEVICE_ID + "\"]";
        public static final String COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID = "[\"" + NUMBER_NOT_EQUAL_TO_DEVICE_ID + "\"]";
        public static final String COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID =
                "["+ DEVICE_ID + ", " + NUMBER_NOT_EQUAL_TO_DEVICE_ID + "]";
        public static final String OPERATION_RESPONSE_FOR_INVALID_DEVICE_ID = "Device Id not found for device found at 1";
        public static final String OPERATION_RESPONSE_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID =
                                                                            "Device Id not found for device found at 2";
        public static final String CAMERA_OPERATION = "camera";
        public static final String LOCK_OPERATION = "lock";
        public static final String WIPE_DATA_OPERATION = "wipe_data";
        public static final String INSTALL_APPS_OPERATION = "install_apps";
        public static final String NOTIFICATION_OPERATION = "notification";
        public static final String UPGRADE_FIRMWARE_OPERATION = "upgrade-firmware";
        public static final String WIFI_OPERATION = "wifi";
        public static final String VPN_OPERATION = "vpn";
        public static final String ENCRYPT_OPERATION = "encrypt";
        public static final String CHANGE_LOCK_OPERATION = "change_lock";
        public static final String PASSWORD_POLICY_OPERATION = "password_policy";
        public static final String WEB_CLIP_OPERATION = "web_clip";
        public static final String DEVICE_LIST_START_INDEX = "1";
        public static final String DEVICE_LIST_LENGTH = "10";
        public static final String OPERATION_ENDPOINT = "/mdm-android-agent/operation/";
        public static final String LOCK_ENDPOINT = "/mdm-android-agent/operation/lock";
        public static final String UNLOCK_ENDPOINT = "/mdm-android-agent/operation/unlock";
        public static final String LOCATION_ENDPOINT = "/mdm-android-agent/operation/location";
        public static final String CLEAR_PASSWORD_ENDPOINT = "/mdm-android-agent/operation/clear-password";
        public static final String CAMERA_ENDPOINT = "/mdm-android-agent/operation/camera";
        public static final String DEVICE_INFO_ENDPOINT = "/mdm-android-agent/operation/device-info";
        public static final String ENTERPRISE_WIPE_ENDPOINT = "/mdm-android-agent/operation/enterprise-wipe";
        public static final String WIPE_DATA_ENDPOINT = "/mdm-android-agent/operation/wipe-data";
        public static final String APPLICATION_LIST_ENDPOINT = "/mdm-android-agent/operation/application-list";
        public static final String RING_ENDPOINT = "/mdm-android-agent/operation/ring-device";
        public static final String REBOOT_ENDPOINT = "/mdm-android-agent/operation/reboot-device";
        public static final String UPGRADE_FIRMWARE_ENDPOINT = "/mdm-android-agent/operation/upgrade-firmware";
        public static final String VPN_ENDPOINT = "/mdm-android-agent/operation/vpn";
        public static final String MUTE_ENDPOINT = "/mdm-android-agent/operation/mute";
        public static final String INSTALL_APPS_ENDPOINT = "/mdm-android-agent/operation/install-application";
        public static final String UNINSTALL_APPS_ENDPOINT = "/mdm-android-agent/operation/uninstall-application";
        public static final String BLACKLIST_APPS_ENDPOINT = "/mdm-android-agent/operation/blacklist-applications";
        public static final String NOTIFICATION_ENDPOINT = "/mdm-android-agent/operation/notification";
        public static final String WIFI_ENDPOINT = "/mdm-android-agent/operation/wifi";
        public static final String ENCRYPT_ENDPOINT = "/mdm-android-agent/operation/encrypt";
        public static final String CHANGE_LOCK_ENDPOINT = "/mdm-android-agent/operation/change-lock-code";
        public static final String PASSWORD_POLICY_ENDPOINT = "/mdm-android-agent/operation/password-policy";
        public static final String WEB_CLIP_ENDPOINT = "/mdm-android-agent/operation/webclip";
    }

    public static final class WindowsOperation {
        private WindowsOperation () { throw new AssertionError(); }

        public static final String WINDOWS_OPERATION_GROUP = "windows-operations";
        public static final String DEVICE_ID = "[urn:uuid:FAEFB2D5-1771-5446-A635-797AFC474895]";

        public static final String LOCK_ENDPOINT = "/windows/operations/lock";
        public static final String WIPE_ENDPOINT = "/windows/operations/data-wipe";
        public static final String RING_ENDPOINT = "/windows/operations/ring";
        public static final String DISENROLL_ENDPOINT = "/windows/operations/disenroll";
        public static final String RESET_ENDPOINT = "/windows/operations/lock-reset";
    }

    public static final class AndroidDeviceManagement {
        private AndroidDeviceManagement() {
            throw new AssertionError();
        }

        public static final String DEVICE_MANAGEMENT_GROUP = "device-mgt";
        public static final String KEY_DEVICE_ID = "deviceIdentifier";
        public static final String KEY_DEVICE_NAME = "name";
        public static final String DEVICE_MGT_ENDPOINT = "/mdm-android-agent/device/";
        public static final String LICENSE_SECTION = "This";
        public static final String LICENSE_ENDPOINT = DEVICE_MGT_ENDPOINT + "license";
        public static final String APP_LIST_ENDPOINT = DEVICE_MGT_ENDPOINT + "appList/" +
                                                               Constants.DEVICE_ID;
        public static final String REQUEST_MODIFY_DEVICE_EXPECTED = "{\"responseMessage\":\"Device information " +
                                                                            "has modified successfully.\"}";

        public static final String APPLIST_PAYLOAD = "{\"id\":\"1\"," +
                                                                  "\"applicationIdentifier\": \"appid\",\"\"platform\": \"android\"," +
                                                             "\"name\": \"testapp\"}";
        public static final String RESPONSE_PAYLOAD_FILE_NAME = "android-device-mgt-response-payloads.json";
    }

    public static final class AndroidConfigurationManagement {
        private AndroidConfigurationManagement() {
            throw new AssertionError();
        }

        public static final String DEVICE_CONFIGURATION_GROUP = "android-config-mgt";
        public static final String CONFIG_MGT_ENDPOINT = "/mdm-android-agent/configuration/";
        public static final String PAYLOAD_FILE_NAME = "android-configuration-payloads.json";
        public static final String RESPONSE_PAYLOAD_FILE_NAME = "android-config-response-payloads.json";
    }

    public static final class OperationManagement {
        private OperationManagement(){ throw new AssertionError();}
        public static final String PATH_APPS = "/apps";

        public static final String OPERATION_MANAGEMENT_GROUP = "api-policy-mgt";
        public static final String GET_DEVICE_APPS_ENDPOINT = "/mdm-admin/operations/android/";
        public static final String GET_DEVICE_OPERATIONS_ENDPOINT = "/mdm-admin/operations/android/";
        public static final String GET_DEVICE_LIST_OPERATIONS_END_POINT = "/mdm-admin/operations/paginate/android/";
    }

    public static final class MobileDeviceManagement {
        private MobileDeviceManagement(){ throw new AssertionError();}
        public static final String MOBILE_DEVICE_MANAGEMENT_GROUP = "mobile-device-mgt";
        public static final String GET_DEVICE_COUNT_ENDPOINT = "/mdm-admin/devices/count";
        public static final String NO_OF_DEVICES = "10";
        public static final String GET_ALL_DEVICES_ENDPOINT ="/mdm-admin/devices";
        public static final String VIEW_DEVICE_TYPES_ENDPOINT = "/mdm-admin/devices/types";
        public static final String VIEW_DEVICE_RESPONSE_PAYLOAD_FILE_NAME =
                                                        "mobile-device-mgt-view-device-types-response-payloads.json";
    }


    public static final class UserManagement {
        private UserManagement() { throw new AssertionError(); }

        public static final String USER_MANAGEMENT_GROUP = "user-mgt";
        public static final String USER_NAME = "username123";
        public static final String USER_ENDPOINT = "/mdm-admin/users";
        public static final String USER_PAYLOAD_FILE_NAME = "user-payloads.json";
        public static final String USER_ERRONEOUS_PAYLOAD_FILE_NAME = "user-erroneous-payloads.json";
        public static final String USER_RESPONSE_PAYLOAD_FILE_NAME = "user-response-payloads.json";
        public static final String USER_ERRONEOUS_RESPONSE_PAYLOAD_FILE_NAME = "user-erroneous-response-payloads.json";
        public static final String VIEW_USER_ENDPOINT = "/mdm-admin/users/view";

    }

    public static final class RoleManagement {
        private RoleManagement() { throw new AssertionError();}

        public static final String ROLE_MANAGEMENT_GROUP = "role-mgt";
        public static final String ROLE_NAME = "administration";
        public static final String UPDATED_ROLE_NAME = "manager";
        public static final String ROLE_ENDPOINT = "/mdm-admin/roles";
        public static final String ROLE_PAYLOAD_FILE_NAME = "role-payloads.json";
        public static final String ROLE_RESPONSE_PAYLOAD_FILE_NAME = "role-response-payloads.json";
        public static final String ROLE_ERRONEOUS_PAYLOAD_FILE_NAME = "role-erroneous-payloads.json";
        public static final String ROLE_UPDATE_PAYLOAD_FILE_NAME = "role-update-payloads.json";

    }

    public static final class PolicyManagement {
        private PolicyManagement() { throw new AssertionError();}

        public static final String POLICY_MANAGEMENT_GROUP = "policy-mgt";
        public static final String ADD_POLICY_ENDPOINT= "/mdm-admin/policies/active-policy";
        public static final String GET_ALL_POLICIES_ENDPOINT = "/mdm-admin/policies";
        public static final String POLICY_PRIORITIES_ENDPOINT = "/mdm-admin/policies/priorities";

        public static final String ANDROID_POLICY_PAYLOAD_FILE_NAME = "android-policy-payloads.json";
        public static final String ANDROID_POLICY_WORK_PROFILE_PAYLOAD_FILE_NAME = "android-policy-work-profile-payload.json";
        public static final String ANDROID_POLICY_ERRONEOUS_PAYLOAD_FILE_NAME = "policy-erroneous-payloads.json";
        public static final String WINDOWS_POLICY_PAYLOAD_FILE_NAME = "windows-policy-payloads.json";
        public static String WINDOWS_POLICY_DEVICE_ID = "";
        public static String WINDOWS_POLICY_SECOND_DEVICE_ID = "";
        public static final String WINDOWS_ADD_SECOND_POLICY_PAYLOAD_FILE_NAME =
                "windows-add-second-policy-payload.json";
        public static String POLICY_PRIORITIES_PAYLOAD_FILE_NAME = "";
        public static final String POLICY_ERRONEOUS_PAYLOAD_FILE_NAME = "[{\"id\":1,\"priority\":1}," +
                "{\"id\":1,\"priority\":2}]";;
        public static final String POLICY_RESPONSE_PAYLOAD_FILE_NAME = "policy-response-payloads.json";
        public static final String POLICY_PRIORITIES_RESPONSE_PAYLOAD_FILE_NAME = "policy-priories-response-payloads.json";

        public static final String UPDATE_ANDROID_POLICY_ENDPOINT = "/mdm-admin/policies/1";
        public static String UPDATE_WINDOWS_POLICY_ENDPOINT = "";

        public static final String REMOVE_POLICY_ENDPOINT = "/mdm-admin/policies/bulk-remove";
        public static final String REMOVE_ANDROID_POLICY_PAYLOAD_FILE_NAME = "[1]";
        public static String REMOVE_WINDOWS_POLICY_PAYLOAD_FILE_NAME = "";

        public static final String VIEW_POLICY_LIST_ENDPOINT = "/mdm-admin/policies";
    }

    public static final class FeatureManagement {
        private FeatureManagement() { throw new AssertionError(); }

        public static final String FEATURE_MANAGEMENT_GROUP = "feature-mgt";
        public static final String VIEW_FEATURES_ENDPOINT = "/mdm-admin/features/android";
        public static final String VIEW_FEATURES_ERRONEOUS_ENDPOINT = "/mdm-admin/features";
    }

    public static final class LicenseManagement {
        private LicenseManagement() { throw new AssertionError(); }

        public static final String LICENSE_MANAGEMENT_GROUP = "license-mgt";
        public static final String GET_LICENSE_ENDPOINT = "/mdm-admin/license/android/en_US";
        public static final String GET_LICENSE_ERRONEOUS_ENDPOINT = "/mdm-admin/license";
        public static final String LICENSE_RESPONSE_PAYLOAD_FILE_NAME = "license-response-payloads.json";

    }

    public static final class ConfigurationManagement {
        private ConfigurationManagement() { throw new AssertionError(); }

        public static final String CONFIGURATION_MANAGEMENT_GROUP = "configuration-mgt";
        public static final String CONFIGURATION_ENDPOINT = "/mdm-admin/configuration";
        public static final String CONFIGURATION_ERRONEOUS_ENDPOINT = "/mdm-admin/configuration/android";
        public static final String CONFIGURATION_PAYLOAD_FILE_NAME = "configuration-payloads.json";
        public static final String CONFIGURATION_RESPONSE_PAYLOAD_FILE_NAME = "configuration-response-payloads.json";
        public static final String CONFIGURATION_ERRONEOUS_RESPONSE = "Scope validation failed";
    }

    public static final class NotificationManagement {
        private NotificationManagement() { throw new AssertionError(); }

        public static final String NOTIFICATION_MANAGEMENT_GROUP = "notification-mgt";
        public static final String NOTIFICATION_ENDPOINT = "/mdm-admin/notifications";
        public static final String NOTIFICATION_PAYLOAD_FILE_NAME = "notification-payloads.json";
        public static final String NOTIFICATION_ERRONEOUS_PAYLOAD_FILE_NAME = "notification-erroneous-payloads.json";
        public static final String NOTIFICATION_RESPONSE_PAYLOAD_FILE_NAME = "notification-response-payloads.json";
        public static final String NOTIFICATION_UPDATE_ENDPOINT = "/mdm-admin/notifications/1234567/NEW";

    }

    public static final class CertificateManagement {
        private CertificateManagement() { throw new AssertionError(); }

        public static final String CERTIFICATE_MANAGEMENT_GROUP = "certificate-mgt";
        public static final String CERTIFICATE_ADD_PAYLOAD= "[{\"serial\" : \"12438035315552875930\",  \"pem\" :\"MIIFlTCCA32gAwIBAgIJAKycxzhPSvWjMA0GCSqGSIb3DQEBBQUAMFkxCzAJBgNVBAYTAnNsMQwwCgYDVQQIDANhc2QxDDAKBgNVBAcMA2FzZDELMAkGA1UECgwCYXMxCzAJBgNVBAsMAnNhMRQwEgYDVQQDDAsxMC4xMC4xMC4yNDAeFw0xNjAyMTUwNTU5MDFaFw0xNzAyMTQwNTU5MDFaMFkxCzAJBgNVBAYTAnNsMQwwCgYDVQQIDANhc2QxDDAKBgNVBAcMA2FzZDELMAkGA1UECgwCYXMxCzAJBgNVBAsMAnNhMRQwEgYDVQQDDAsxMC4xMC4xMC4yNDCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAOAg4JBfnZ1x/c/ktkuq7Wj8HQIm5CrwwYj+h2GDSDyqUKpyd3NJReNqUnhsL7MYR85tmP0SpBlD4X8KSBOuohVw4/cupmk0AlctINaTLRab0aN7ux43fTglD2O5ATdtlH+xLHuLMKcREV+ZedrwjiqzbUX/J/5EYNxW3fAh9pk/PB31Jbv0UTv7dTghsiecYQb6ENlP0sID6gAi9Br9oKKz/mPQFGafIUXZYEiuc2ugYeNQsnTnteAwIR/0LeedsRTUk4sM/z9EZ/NJ/RALwrZ8SPcA90grWkl3m6+GWYvjbyg4T47m9Vy6YD7p56QYitaGgooo2Tyj/tc2UrtPfJhmmkjs3dHDrvZLRskU2YXU+89qiuhfGulQG6Jz76Tf26gAX3tvhRACG8rbduSjvkyGJde5ig//RC+JeUrdm+m3XwEbjFyGzVHaKjxIlQ/JXx1ffvZ+g2NLQUI/g3RbPQESTys0qWP25FYoJSv3i1w6C7akX2DwWUM+KzzCGRVRpNBaDai/mkVI0IVcb5X2pfqMygS+SA0pXtl1x/5YFhdQe2uoVvukb1cNcuOpBL6BMsFAd9CVDs21e7wDo74Sf879ve8bZF2M5WAQoKG9cbYrA8KdX0vInEcYs1VwSuMaTABQSOw+LI+ubmeO9zZ0HrtU9okzQ8JaVeX/NRDP4JYDAgMBAAGjYDBeMB0GA1UdDgQWBBT6TvCK/V/RfWuOP5boEG500eLe4TAfBgNVHSMEGDAWgBT6TvCK/V/RfWuOP5boEG500eLe4TAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBhjANBgkqhkiG9w0BAQUFAAOCAgEAdheZlPw+QSN6HbVaXJTE/N272iz02HWz+5wREIHi31fAFCQs3KP/ozOSC6mmlRkJ1ry7SRslCXVI7CqFJsuc0xR/cLb8Ti3CuzNRHd3N81tLtW8GdEU8wItQTJTkXBPiG2ZM6d7Un1daL1T5VTHONE/n2rQqpCREQvqJnLuCxdyrGGRHrtM/wOSQ+s2yIFdYOdG6GiuBIz4ML8runEb2cpSxJrILvqOV3GakBwz9OARhrtowztH8WaC93WeMFAJHyzFBcnmjKozpJTKqZ4oF+5o8o2ENly6+a/PExu7uhU9eDKMzc/rGVKOGF+NqxIiDJbGlCcAsQ9+uo3Xkh2T1rBsM0/COfRHz1jpRJy5YpHCKDyv1rrE7plNXEtejjHxj2iwu8mzfCwznH0B06ThjEPUHWS0GrTjWWCjaP0R3hIU/s/H8b8KabryRwezFINOWAN8CZNoMtR8b5YzlktFxKbe6E5H3v39s2xg1fvwwZKwZU3DbSWpwybGaBBUsNgTtT3ZhCm3eXkdESvAmp4+jm+M+nCuiwnJ0Sdv1azjPv4Jvie7ObHv7soN18bsiooYUyksw0YRcVDFckHK0tm2vZT+XC57P/c3IeVso1K7S0+Q9GHW/2OMQXHldXVywQB3RZ1dRO3qXLDh26DiJi0d/mJgI+8LooHOmreXTZLfwWsc=\", \"tenantId\":\"-1234\"}]";
        public static final String CERTIFICATE_ADD_ENDPOINT = "/mdm-admin/certificates/";
        public static final String CERTIFICATE_GET_ENDPOINT = "/mdm-admin/certificates/12438035315552875930";
        public static final String CERTIFICATE_GET_ALL_ENDPOINT = "/mdm-admin/certificates/paginate?start=0&length=1";
        public static final String CERTIFICATE_PAYLOAD= "\"serialNumber\":\"12438035315552875930\"";

    }
}
