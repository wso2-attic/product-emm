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
package org.wso2.mdm.integration.device.operation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import junit.framework.Assert;

import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This contain tests to check operations supported by Android. Test are executed against a previously enrolled device
 */
public class AndroidOperation extends TestBase {
    private static JsonParser parser = new JsonParser();
    private RestClient client;
    private MDMHttpClient mdmHttpClient;

    @BeforeTest(alwaysRun = true,
            groups = { Constants.AndroidEnrollment.ENROLLMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPURL, Constants.APPLICATION_JSON, accessTokenString);
        this.mdmHttpClient = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
        //Enroll a device
        JsonObject enrollmentData = PayloadGenerator
                .getJsonPayload(Constants.AndroidEnrollment.ENROLLMENT_PAYLOAD_FILE_NAME, Constants.HTTP_METHOD_POST);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device lock operation.")
    public void testLock() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.LOCK_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.LOCK_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());

    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device lock operation for invalid device id.")
    public void testLockWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.LOCK_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);

        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.LOCK_ENDPOINT,
                    operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device unlock operation.")
    public void testUnlock() throws Exception {
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.UNLOCK_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device unlock operation for invalid device id")
    public void testUnlockWithInvalidDeviceId() throws Exception {
        try {
            client.post(
                    Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.UNLOCK_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device location operation.")
    public void testLocation() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.LOCATION_ENDPOINT, Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device location operation for invalid device id")
    public void testLocationWithInvalidDeviceId() throws Exception {
        try {
            client.post(
                    Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.LOCATION_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device location "
                    + "operation for two device ids including an invalid device id as the second one")
    public void testLocationForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.LOCATION_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device clear password operation.")
    public void testClearPassword() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.CLEAR_PASSWORD_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device clear password operation for invalid device id.")
    public void testClearPasswordWithInvalidDeviceId() throws Exception {
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                            + Constants.AndroidOperations.CLEAR_PASSWORD_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device clear password "
                    + "operation for two device ids including an invalid device id as the second one")
    public void testClearPasswordForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.CLEAR_PASSWORD_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device camera operation.")
    public void testCamera() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.CAMERA_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.CAMERA_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device camera operation for invalid device id.")
    public void testCameraWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.CAMERA_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(
                    Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.CAMERA_ENDPOINT,
                    operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device camera operation "
                    + "for two device ids including an invalid device id as the second one")
    public void testCameraForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.CAMERA_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.CAMERA_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device information operation.")
    public void testDeviceInfo() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.DEVICE_INFO_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device information operation.")
    public void testDeviceInfoWithInvalidDeviceId() throws Exception {
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                            + Constants.AndroidOperations.DEVICE_INFO_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device device information"
                    + "operation for two device ids including an invalid device id as the second one")
    public void testDeviceInfoForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.DEVICE_INFO_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android enterprise-wipe operation.")
    public void testEnterpriseWipe() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.ENTERPRISE_WIPE_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android enterprise-wipe operation for invalid device id.")
    public void testEnterpriseWipeWithInvalidDeviceId() throws Exception {
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                            + Constants.AndroidOperations.ENTERPRISE_WIPE_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device location "
                    + "operation for two device ids including an invalid device id as the second one")
    public void testEnterpriseWipeForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.ENTERPRISE_WIPE_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android wipe data operation.")
    public void testWipeData() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WIPE_DATA_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.WIPE_DATA_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android wipe data operation for invalid device id.")
    public void testWipeDataWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WIPE_DATA_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(
                    Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.WIPE_DATA_ENDPOINT,
                    operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android wipe data operation for "
                    + "two device ids including an invalid device id as the second one.")
    public void testWipeDataForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WIPE_DATA_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.WIPE_DATA_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android application list operation.")
    public void testApplicationList() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.APPLICATION_LIST_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android application list operation for invalid device id.")
    public void testApplicationListWithInvalidDeviceId() throws Exception {
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                            + Constants.AndroidOperations.APPLICATION_LIST_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device application list "
                    + "operation for two device ids including an invalid device id as the second one")
    public void testApplicationListForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                        + Constants.AndroidOperations.APPLICATION_LIST_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android ring operation.")
    public void testRing() throws Exception {
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.RING_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android ring operation for invalid device id.")
    public void testRingWithInvalidDeviceId() throws Exception {
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.RING_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device ring operation for "
                    + "two device ids including an invalid device id as the second one")
    public void testRingForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.RING_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device reboot operation.")
    public void testDeviceReboot() throws Exception {
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.REBOOT_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android mute operation.")
    public void testMute() throws Exception {
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.MUTE_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android mute operation for invalid device id.")
    public void testMuteWithInvalidDeviceId() throws Exception {
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.MUTE_ENDPOINT,
                    Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_INVALID_DEVICE_ID);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android device mute  operation "
                    + "for two device ids including an invalid device id as the second one")
    public void testMuteForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.MUTE_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD_FOR_TWO_DEVICES_WITH_ONE_INVALID_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    //    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android install apps operation.")
    //    public void testInstallApps() throws Exception {
    //        JsonObject operationData = PayloadGenerator
    //                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
    //                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
    //        JsonArray deviceIds = new JsonArray();
    //        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
    //        deviceIds.add(deviceID);
    //        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
    //        HttpResponse response = client.post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.INSTALL_APPS_ENDPOINT,
    //                operationData.toString());
    //        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    //    }
    //
    //    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android install apps operation for"
    //            + "invalid device id") public void testInstallAppsWithInvalidDeviceId() throws Exception {
    //        JsonObject operationData = PayloadGenerator
    //                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
    //                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
    //        JsonArray deviceIds = new JsonArray();
    //        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
    //        deviceIds.add(deviceID);
    //        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
    //        try {
    //            client.post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.INSTALL_APPS_ENDPOINT,
    //                    operationData.toString());
    //        } catch (Exception e) {
    //            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
    //        }
    //    }
    //
    //    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android install apps operation for"
    //            + "two device ids including an invalid device id as the second one")
    //    public void testInstallAppsForTwoDevicesWithOneInvalidDeviceId() throws Exception {
    //        JsonObject operationData = PayloadGenerator
    //                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
    //                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
    //        JsonArray deviceIds = new JsonArray();
    //        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
    //        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
    //        deviceIds.add(deviceID1);
    //        deviceIds.add(deviceID2);
    //        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
    //        HttpResponse response = client.post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.INSTALL_APPS_ENDPOINT,
    //                operationData.toString());
    //        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    //    }
    //
    //    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android uninstall apps operation.")
    //    public void testUninstallApps() throws Exception {
    //        JsonObject operationData = PayloadGenerator
    //                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
    //                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
    //        JsonArray deviceIds = new JsonArray();
    //        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
    //        deviceIds.add(deviceID);
    //        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
    //        HttpResponse response = client
    //                .post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.UNINSTALL_APPS_ENDPOINT,
    //                        operationData.toString());
    //        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    //    }
    //
    //    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android uninstall apps operation "
    //            + "for invalid device id")
    //    public void testUninstallAppsWithInvalidDeviceId() throws Exception {
    //        JsonObject operationData = PayloadGenerator
    //                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
    //                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
    //        JsonArray deviceIds = new JsonArray();
    //        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
    //        deviceIds.add(deviceID);
    //        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
    //        try {
    //            client.post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.UNINSTALL_APPS_ENDPOINT,
    //                    operationData.toString());
    //        } catch (Exception e) {
    //            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
    //        }
    //    }
    //
    //    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android uninstall apps operation "
    //            + "for two device ids including an invalid device id as the second one")
    //    public void testUninstallAppsForTwoDevicesWithOneInvalidDeviceId() throws Exception {
    //        JsonObject operationData = PayloadGenerator
    //                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
    //                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
    //        JsonArray deviceIds = new JsonArray();
    //        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
    //        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
    //        deviceIds.add(deviceID1);
    //        deviceIds.add(deviceID2);
    //        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
    //        HttpResponse response = client
    //                .post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.UNINSTALL_APPS_ENDPOINT,
    //                        operationData.toString());
    //        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    //    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android blacklist apps operation.")
    public void testBlacklistApps() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.BLACKLIST_APPS_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android blacklist apps operation for invalid device id")
    public void testBlacklistAppsWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                    + Constants.AndroidOperations.BLACKLIST_APPS_ENDPOINT, operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android blacklist apps operation"
                    + "for two device ids including an invalid device id as the second one")
    public void testBlacklistAppsForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.INSTALL_APPS_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.BLACKLIST_APPS_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android notification operation.")
    public void testNotification() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.NOTIFICATION_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.NOTIFICATION_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android firmware upgrade operation.")
    public void testUpgradeFirmware() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.UPGRADE_FIRMWARE_OPERATION);
        String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        operationData.addProperty("schedule", date);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.UPGRADE_FIRMWARE_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android VPN configuration operation.")
    public void testVPN() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.VPN_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.VPN_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android notification operation for invalid device id")
    public void testNotificationWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.NOTIFICATION_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(Constants.AndroidOperations.NOTIFICATION_ENDPOINT, operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android notification operation"
                    + "for two device ids including an invalid device id as the second one")
    public void testNotificationForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.NOTIFICATION_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.NOTIFICATION_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android WiFi operation.")
    public void testWiFi() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WIFI_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.WIFI_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android WiFi operation for invalid device id")
    public void testWiFiWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WIFI_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.WIFI_ENDPOINT,
                    operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android WiFi operation for"
                    + "two device ids including an invalid device id as the second one")
    public void testWiFiForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WIFI_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.WIFI_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android encrypt operation.")
    public void testEncrypt() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.ENCRYPT_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.ENCRYPT_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android encrypt operation for invalid device id")
    public void testEncryptWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.ENCRYPT_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(
                    Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.ENCRYPT_ENDPOINT,
                    operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android encrypt operation for"
                    + "two device ids including an invalid device id as the second one")
    public void testEncryptForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.ENCRYPT_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client
                .post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.ENCRYPT_ENDPOINT,
                        operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android change lock operation.")
    public void testChangeLock() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.CHANGE_LOCK_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.CHANGE_LOCK_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android change lock operation for invalid device id")
    public void testChangeLockWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.CHANGE_LOCK_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                    + Constants.AndroidOperations.CHANGE_LOCK_ENDPOINT, operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android change lock operation for"
                    + "two device ids including an invalid device id as the second one")
    public void testChangeLockForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.CHANGE_LOCK_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.CHANGE_LOCK_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android password policy operation.")
    public void testPasswordPolicy() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.PASSWORD_POLICY_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.PASSWORD_POLICY_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android password policy operation for invalid device id")
    public void testPasswordPolicyWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.PASSWORD_POLICY_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                    + Constants.AndroidOperations.PASSWORD_POLICY_ENDPOINT, operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android password policy operation"
                    + "for two device ids including an invalid device id as the second one")
    public void testPasswordPolicyForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.PASSWORD_POLICY_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.PASSWORD_POLICY_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android web clip operation.")
    public void testWebClip() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WEB_CLIP_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.WEB_CLIP_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android web clip operation for invalid device id")
    public void testWebClipWithInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WEB_CLIP_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        try {
            client.post(
                    Constants.AndroidOperations.ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.WEB_CLIP_ENDPOINT,
                    operationData.toString());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP,
            description = "Test Android web clip operation for invalid device id")
    public void testWebClipForTwoDevicesWithOneInvalidDeviceId() throws Exception {
        JsonObject operationData = PayloadGenerator
                .getJsonPayload(Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                        Constants.AndroidOperations.WEB_CLIP_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID1 = new JsonPrimitive(Constants.DEVICE_ID);
        JsonPrimitive deviceID2 = new JsonPrimitive(Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        deviceIds.add(deviceID1);
        deviceIds.add(deviceID2);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.ANDROID_DEVICE_MGT_API
                + Constants.AndroidOperations.WEB_CLIP_ENDPOINT, operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }
}