package org.wso2.mdm.integration.mobileDevice;


import com.google.gson.JsonObject;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * This class contains integration tests for API Device management backend services.
 */


public class MobileDeviceManagement extends TestBase{
    private RestClient client;
    private JsonObject device;

    @BeforeClass(alwaysRun = true, groups = { Constants.MobileDeviceManagement.MOBILE_DEVICE_MANAGEMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test count devices with no added devices")
    public void testCountDevicesWithNoDevices() throws Exception{
        HttpResponse response = client.get(Constants.MobileDeviceManagement.GET_DEVICE_COUNT_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getResponseCode());

    }

    @Test(description = "Test view devices with no added devices")
    public void testViewDevicesWithNoDevices() throws Exception {
        HttpResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT);

        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getResponseCode());
    }

    @Test(dependsOnMethods = {"testCountDevicesWithNoDevices", "testViewDevicesWithNoDevices"},
                                                                                description = "Add an Android device.")
    public void testEnrollment() throws Exception {
        JsonObject enrollmentData = PayloadGenerator.getJsonPayload(
                Constants.AndroidEnrollment.ENROLLMENT_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        HttpResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                Constants.AndroidEnrollment.ENROLLMENT_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST).toString(), response.getData().toString(), true);
    }

    @Test(dependsOnMethods = {"testEnrollment"}, description = "Test count devices")
    public void testCountDevices() throws Exception {
        HttpResponse response = client.get(Constants.MobileDeviceManagement.GET_DEVICE_COUNT_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
        Assert.assertTrue(response.getData().toString().equals(Constants.MobileDeviceManagement.NO_OF_DEVICES));

    }

    @Test(dependsOnMethods = {"testEnrollment"}, description = "Test view devices")
    public void testViewDevices() throws Exception {
        HttpResponse response = client.get(Constants.MobileDeviceManagement.GET_ALL_DEVICES_ENDPOINT);
        //REMOVE BEFORE FREEZE
        /*File logFile = new File("testViewDeviceResponse.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
        writer.write(response.getData().toString());
        writer.close();
        */
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
    }

    @Test(dependsOnMethods = {"testEnrollment"}, description = "Test view device types")
    public void testViewDeviceTypes() throws Exception {
        HttpResponse response = client.get(Constants.MobileDeviceManagement.VIEW_DEVICE_TYPES_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
    }


}
