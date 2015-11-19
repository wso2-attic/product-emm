package org.wso2.mdm.integration.operation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * This class contains integration tests for API Operation management backend services.
 */
public class OperationManagement extends TestBase {

    private RestClient client;
    private JsonObject device;

    @BeforeClass(alwaysRun = true, groups = { Constants.OperationManagement.OPERATION_MANAGEMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Add an Android device.")
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

    @Test(dependsOnMethods = {"testEnrollment"}, description = "Install an app to Android device.")
    public void testInstallApps() throws Exception {
        JsonObject operationData = PayloadGenerator.getJsonPayload(
                Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                Constants.AndroidOperations.INSTALL_APPS_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = client.post(Constants.AndroidOperations.INSTALL_APPS_ENDPOINT,
                operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }


    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get device apps.")
    public void testGetDeviceApps() throws Exception {
        HttpResponse response = client.get(Constants.OperationManagement.GET_DEVICE_APPS_ENDPOINT+Constants.DEVICE_IMEI+Constants.OperationManagement.PATH_APPS);
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
        Assert.assertTrue(response.getData().toString().equals(PayloadGenerator.getJsonPayload(Constants.OperationManagement.OPERATION_GET_DEVICE_APPS_RESPONSE_PAYLOADS_FILE_NAME,Constants.HTTP_METHOD_GET).toString()));

    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get operations for device.")
    public void testGetDeviceOperations() throws Exception {
        HttpResponse response = client.get(Constants.OperationManagement.GET_DEVICE_OPERATIONS_ENDPOINT+Constants.DEVICE_IMEI);
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());

    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get device apps with wrong Device ID")
    public void testGetDeviceAppsWithWrongDeviceID() throws Exception{
        HttpResponse response = client.get(Constants.OperationManagement.GET_DEVICE_APPS_ENDPOINT+Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID+Constants.OperationManagement.PATH_APPS);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getResponseCode());

    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "test get operations for device with wrong Device ID")
    public void testGetDeviceOperationsWithWrongDeviceID() throws Exception{
        HttpResponse response = client.get(Constants.OperationManagement.GET_DEVICE_OPERATIONS_ENDPOINT+Constants.DEVICE_IMEI);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getResponseCode());

    }

}
