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
package org.wso2.mdm.integration.mobileDevice;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;
import com.google.gson.JsonParser;

import java.util.HashSet;
import java.util.Set;

/**
 * This class contains integration tests for API Device management backend services.
 */
public class MobileDeviceManagement extends TestBase {
    private MDMHttpClient client;
    private static JsonParser parser = new JsonParser();

    @BeforeClass(alwaysRun = true, groups = {Constants.MobileDeviceManagement.MOBILE_DEVICE_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Add an Android device.")
    public void addEnrollment() throws Exception {
        //enroll first device
        JsonObject enrollmentData = PayloadGenerator.getJsonPayload(
                Constants.AndroidEnrollment.ENROLLMENT_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        MDMResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        //enroll additional 9 devices
        enrollDevice(Constants.DEVICE_ID_2, Constants.AndroidEnrollment.DEVICE_TWO_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_3, Constants.AndroidEnrollment.DEVICE_THREE_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_4, Constants.AndroidEnrollment.DEVICE_FOUR_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_5, Constants.AndroidEnrollment.DEVICE_FIVE_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_6, Constants.AndroidEnrollment.DEVICE_SIX_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_7, Constants.AndroidEnrollment.DEVICE_SEVEN_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_8, Constants.AndroidEnrollment.DEVICE_EIGHT_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_9, Constants.AndroidEnrollment.DEVICE_NINE_ENROLLMENT_DATA);
        enrollDevice(Constants.DEVICE_ID_10, Constants.AndroidEnrollment.DEVICE_TEN_ENROLLMENT_DATA);
    }

    private void enrollDevice(String deviceId, String deviceEnrollmentData) throws Exception{
        JsonObject enrollmentData = PayloadGenerator.getJsonPayload(
                Constants.AndroidEnrollment.ENROLLMENT_ADDITIONAL_DEVICES_PAYLOAD_FILE_NAME,
                deviceEnrollmentData);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, deviceId);
        MDMResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test(dependsOnMethods = {"addEnrollment"}, description = "Test count devices")
    public void testCountDevices() throws Exception {
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        JsonObject jsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertTrue(jsonObject.get("count").toString().equals(Constants.MobileDeviceManagement.NO_OF_DEVICES));

    }
    @Test(dependsOnMethods = {"addEnrollment"}, description = "Test view devices")
    public void testViewDevices() throws Exception {
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
   }

    @Test(dependsOnMethods = {"addEnrollment"}, description = "Test view device types")
    public void testViewDeviceTypes() throws Exception {
        MDMResponse response = client.get(Constants.MobileDeviceManagement.VIEW_DEVICE_TYPES_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        JsonObject deviceTypesJsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertEquals(deviceTypesJsonObject.get("count").getAsInt(),
                Constants.MobileDeviceManagement.DEVICE_TYPES_COUNT);
        JsonArray responseDeviceTypes = deviceTypesJsonObject.getAsJsonArray("deviceTypes");
        Set<String> deviceTypesSet = new HashSet<>();
        for (int i = 0; i < responseDeviceTypes.size(); i++) {
            deviceTypesSet.add(responseDeviceTypes.get(i).getAsString());
        }
        JsonArray deviceTypes = PayloadGenerator.getJsonPayload(
                Constants.MobileDeviceManagement.VIEW_DEVICE_RESPONSE_PAYLOAD_FILE_NAME);
        for (int i = 0; i < deviceTypes.size(); i++) {
            String type = deviceTypes.get(i).getAsJsonObject().get("name").getAsString();
            Assert.assertTrue(deviceTypesSet.contains(type));

        }
        //Response has two device types, because in windows enrollment a windows device is previously enrolled.
    }

    //Pagination testings for GetAllDevice Function
    @Test(dependsOnMethods = "addEnrollment", description = "Get 5 records of devices")
    public void testGetDevicesForSetOfDevices() throws Exception{
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT+"?offset=0&limit=5");
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        JsonObject jsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertTrue("missing 'devices' attribute in response", jsonObject.has("devices"));
        JsonArray jsonArray = jsonObject.getAsJsonArray("devices");
        Assert.assertTrue("response array length not equal to requested length", String.valueOf(jsonArray.size()).equals("5"));
    }

    @Test(dependsOnMethods = "addEnrollment", description = "Get all android devices")
    public void testGetAndroidDevices() throws Exception{
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT+"?type=android");
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatus());
        JsonObject jsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertTrue("number of android devices in response not equal to the actual enrolled number.",
                                                                        String.valueOf(jsonObject.get("count")).equals("10"));
    }

    @Test(dependsOnMethods = "addEnrollment", description = "Get all windows devices")
    public void testGetWindowsDevices() throws Exception{
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT+"?type=windows");
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatus());
        JsonObject jsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertEquals(Constants.EMPTY_ARRAY, jsonObject.get("devices").toString());
    }

    @Test(dependsOnMethods = "addEnrollment", description = "Get all devices belongs to role admin")
    public void testGetDevicesBelongToAdmin() throws Exception{
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT+"?role=admin");
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatus());
        JsonObject jsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertTrue("number of devices in response not equal to the actual owned number.",
                                                                        String.valueOf(jsonObject.get("count")).equals("10"));
    }

    @Test(dependsOnMethods = "addEnrollment", description = "Test response for invalid start record number")
    public void testGetDevicesWithInvalidStartNumber() throws Exception{
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT+"?offset=");
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND,response.getStatus());
    }

    @Test(dependsOnMethods = "addEnrollment", description = "Test response for minus length")
    public void testGetDeviceWithMinusLength() throws Exception{
        MDMResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT+"?start=0&length=-2");
        Assert.assertEquals(HttpStatus.SC_OK,response.getStatus());
        JsonObject jsonObject = parser.parse(response.getBody()).getAsJsonObject();
        Assert.assertTrue("number of android devices in response not equal to the actual enrolled number.",
                                                                        String.valueOf(jsonObject.get("count")).equals("10"));
    }
    // End of pagination testing for GetAllDevices

}
