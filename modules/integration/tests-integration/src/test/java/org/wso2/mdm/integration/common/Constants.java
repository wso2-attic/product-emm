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

	public static final int SUCCESS_CODE = 200;
    public static final String DEVICE_ID="1234";
    public static final String AUTOMATION_CONTEXT="MDM";
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_URL_ENCODED = "application/x-www-form-urlencoded";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String ANDROID_DEVICE_TYPE = "android";
    public static final String HTTP_METHOD_POST = "POST";
    public static final String HTTP_METHOD_PUT = "PUT";
    public static final String HTTP_METHOD_GET = "GET";
    public static final String HTTP_METHOD_DELETE = "DELETE";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_SOAP_XML = "application/soap+xml; charset=utf-8";
    public static final String UTF8 = "UTF-8";

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
		private static StringBuffer androidPayloadBuffer = new StringBuffer();

		public static final String ANDROID_REQUEST_ENROLLMENT_PAYLOAD = androidPayloadBuffer.append("{\"name\":").
				append(" \"milan\",\"type\": \"android\",\"description\": \"milan123\"," +
                       "\"deviceIdentifier\": \"" + DEVICE_ID + "\",").
				append("\"enrolmentInfo\": {\"ownership\": \"BYOD\",\"status\": \"ACTIVE\",\"owner\": \"admin\"},").
				append("\"properties\": [{\"name\": \"IMEI\",\"value\": \"123123123\"},{\"name\": \"IMSI\",").
				append("\"value\": \"123123123\"}]}\n").toString();
        public static final String ANDROID_REQUEST_MODIFY_ENROLLMENT_PAYLOAD = new StringBuffer().append("{\"name\":").
                append("\"milan\",\"type\": \"android\",\"description\": \"updatedDescription\"," +
                       "\"deviceIdentifier\": \"" + DEVICE_ID + "\",").
                append("\"enrolmentInfo\": {\"ownership\": \"BYOD\",\"status\": \"ACTIVE\",\"owner\": \"admin\"," +
                       "\"dateOfEnrolment\":\"1445438864650\"},").
                append("\"properties\": [{\"name\": \"IMEI\",\"value\": \"123123123\"},{\"name\": \"IMSI\",").
                append("\"value\": \"123123123\"}]}\n").toString();
		public static final String ANDROID_REQUEST_ENROLLMENT_EXPECTED = "{\"responseCode\": \"Created\"," +
				"\"responseMessage\": \"Device enrollment succeeded\"}";
        public static final String ANDROID_REQUEST_IS_ENROLLMENT_EXPECTED = "{\"responseCode\":\"Accepted\"," +
                                                               "\"responseMessage\":\"Device has already enrolled\"}";
        public static final String ANDROID_REQUEST_MODIFY_ENROLLMENT_EXPECTED = "{\"responseCode\":\"Accepted\"," +
                                                "\"responseMessage\":\"Device enrollment has updated successfully\"}";
        public static final String ENROLLMENT_ENDPOINT = "/mdm-android-agent/enrollment/";
        public static final String ANDROID_ENROLLMENT_GROUP = "android-enrollment";
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
        public static final String BSD_PAYLOAD = "{\"credentials\" : {\"username\" : \"admin\", " +
                                                 "\"password\" : \"admin\", " +
                                                 "\"ownership\" : \"BYOD\"}}";
    }

    public static final class AndroidOperations {
        private AndroidOperations() {
            throw new AssertionError();
        }

        public static final String OPERATIONS_GROUP = "operations";
        public static final String COMMAND_OPERATION_PAYLOAD = "[\"" + DEVICE_ID + "\"]";
        public static final String ANDROID_LOCK_ENDPOINT = "/mdm-android-agent/operation/lock";
        public static final String ANDROID_LOCATION_ENDPOINT = "/mdm-android-agent/operation/location";
        public static final String ANDROID_CLEAR_PASSWORD_ENDPOINT = "/mdm-android-agent/operation/clear-password";
        public static final String ANDROID_CAMERA_ENDPOINT = "/mdm-android-agent/operation/camera";
        public static final String ANDROID_CAMERA_PAYLOAD = "{\"operation\": {\"enabled\": false}," +
                                                            "\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";
        public static final String ANDROID_OPERATION_ENDPOINT = "/mdm-android-agent/operation/";

        public static final String ANDROID_DEVICE_INFO_ENDPOINT = "/mdm-android-agent/operation/device-info";
        public static final String ANDROID_ENTERPRISE_WIPE_ENDPOINT = "/mdm-android-agent/operation/enterprise-wipe";
        public static final String ANDROID_WIPE_DATA_ENDPOINT = "/mdm-android-agent/operation/wipe-data";
        public static final String ANDROID_WIPE_DATA_PAYLOAD = "{\"operation\": {\"pin\": \"1234\"}," +
                                                               "\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";
        public static final String ANDROID_APPLICATION_LIST_ENDPOINT =
                "/mdm-android-agent/operation/application-list";
        public static final String ANDROID_RING_ENDPOINT = "/mdm-android-agent/operation/ring-device";
        public static final String ANDROID_MUTE_ENDPOINT = "/mdm-android-agent/operation/mute";
        public static final String ANDROID_INSTALL_APPS_ENDPOINT = "/mdm-android-agent/operation/install-application";
        public static final String ANDROID_INSTALL_APPS_PAYLOAD =
                "{\"deviceIDs\": [\"" + DEVICE_ID + "\"],\"operation\": " +
                "{\"appIdentifier\": \"package_name\", \"type\":" +
                " \"enterprise/public/webapp\",\"url\": \"https://www.youtube.com\"" +
                ",\"name\": \"youtube\"}}";
        public static final String ANDROID_UNINSTALL_APPS_ENDPOINT =
                "/mdm-android-agent/operation/uninstall-application";
        public static final String ANDROID_BLACKLIST_APPS_ENDPOINT =
                "/mdm-android-agent/operation/blacklist-applications";
        public static final String ANDROID_NOTIFICATION_ENDPOINT = "/mdm-android-agent/operation/notification";
        public static final String ANDROID_NOTIFICATION_PAYLOAD = "{\"deviceIDs\": [\"" + DEVICE_ID + "\"]," +
                                                                  "\"operation\": {\"message\": \"message\"}}";
        public static final String ANDROID_WIFI_ENDPOINT = "/mdm-android-agent/operation/wifi";
        ;
        public static final String ANDROID_WIFI_PAYLOAD = "{\"operation\": {\"ssid\": \"ssid\",\"password\": " +
                                                          "\"password\"},\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";

        public static final String ANDROID_ENCRYPT_ENDPOINT = "/mdm-android-agent/operation/encrypt";
        public static final String ANDROID_ENCRYPT_PAYLOAD = "{\"operation\": {\"encrypted\": true}," +
                                                             "\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";
        public static final String ANDROID_CHANGE_LOCK_ENDPOINT = "/mdm-android-agent/operation/change-lock-code";
        public static final String ANDROID_CHANGE_LOCK_PAYLOAD = "{\"operation\": {\"lockCode\": \"lock_code\"}," +
                                                                 "\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";

        public static final String ANDROID_PASSWORD_POLICY_ENDPOINT = "/mdm-android-agent/operation/password-policy";
        public static final String ANDROID_PASSWORD_POLICY_PAYLOAD = "{\"operation\": {\"maxFailedAttempts\": 1," +
                                                                     "\"minLength\": 5,\"pinHistory\": 1," +
                                                                     "\"minComplexChars\": 4,\"maxPINAgeInDays\": 1," +
                                                                     "\"requireAlphanumeric\": true," +
                                                                     "\"allowSimple\": true}," +
                                                                     "\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";
        public static final String ANDROID_WEB_CLIP_ENDPOINT = "/mdm-android-agent/operation/webclip";
        public static final String ANDROID_WEB_CLIP_PAYLOAD = "{\"operation\": {\"identity\": \"identity\"," +
                                                              "\"title\": \"title\",\"type\": \"install\"}," +
                                                              "\"deviceIDs\": [\"" + DEVICE_ID + "\"]}";

    }

    public static final class DeviceManagement {
        private DeviceManagement() {
            throw new AssertionError();
        }

        public static final String DEVICE_MANAGEMENT_GROUP = "device-mgt";
        public static final String ANDROID_KEY_DEVICE_ID = "deviceIdentifier";
        public static final String ANDROID_KEY_DEVICE_NAME = "name";
        public static final String ANDROID_DEVICE_MGT_ENDPOINT = "/mdm-android-agent/device/";
        public static final String ANDROID_DEVICE_LICENSE_SECTION = "This";
        public static final String ANDROID_LICENSE_ENDPOINT = ANDROID_DEVICE_MGT_ENDPOINT + "license";
        public static final String ANDROID_APP_LIST_ENDPOINT = ANDROID_DEVICE_MGT_ENDPOINT + "appList/" +
                                                               Constants.DEVICE_ID;
        public static final String ANDROID_REQUEST_MODIFY_DEVICE_EXPECTED = "{\"responseMessage\":\"Device information " +
                                                                            "has modified successfully.\"}";

        public static final String ANDROID_APPLIST_PAYLOAD = "{\"id\":\"1\"," +
                                                                  "\"applicationIdentifier\": \"appid\",\"\"platform\": \"android\"," +
                                                             "\"name\": \"testapp\"}";
    }

    public static final class ConfigurationManagement {
        private ConfigurationManagement() {
            throw new AssertionError();
        }

        public static final String ANDROID_DEVICE_CONFIGURATION_GROUP = "android-config-mgt";
        public static final String ANDROID_DEVICE_CONFIG_MGT_ENDPOINT = "/mdm-android-agent/configuration/";
        public static final String ANDROID_CONFIG_PAYLOAD_FILE_NAME = "android-configuration-payloads.json";
        public static final String ANDROID_CONFIG_RESPONSE_PAYLOAD_FILE_NAME = "android-config-response-payloads.json";
    }
}
