/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.mdm.integration.operation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.annotations.ExecutionEnvironment;
import org.wso2.carbon.automation.engine.annotations.SetEnvironment;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

/**
 * This class contains integration tests for API Operation management backend services on Oracle Platform
 */
@SetEnvironment(executionEnvironments = {ExecutionEnvironment.PLATFORM})
public class OracleOperationManagement extends TestBase {
    private JsonObject device;
    private MDMHttpClient client;
    private RestClient rclient;

    @BeforeClass(alwaysRun = true, groups = { Constants.OperationManagement.OPERATION_MANAGEMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
        this.rclient = new RestClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Add an Android device.")
    public void enrollAndroidDevice() throws Exception {
        JsonObject enrollmentData = PayloadGenerator.getJsonPayload(
                Constants.AndroidEnrollment.ENROLLMENT_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        MDMResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                Constants.AndroidEnrollment.ENROLLMENT_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST).toString(), response.getBody(), true);
    }

    @Test(dependsOnMethods = {"enrollAndroidDevice"}, description = "Install an app to enrolled android device")
    public void testInstallApps() throws Exception {
        JsonObject operationData = PayloadGenerator.getJsonPayload(
                Constants.AndroidOperations.OPERATION_PAYLOAD_FILE_NAME,
                Constants.AndroidOperations.INSTALL_APPS_OPERATION);
        JsonArray deviceIds = new JsonArray();
        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);
        deviceIds.add(deviceID);
        operationData.add(Constants.DEVICE_IDENTIFIERS_KEY, deviceIds);
        HttpResponse response = rclient.post(Constants.AndroidOperations.INSTALL_APPS_ENDPOINT,
                operationData.toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get device apps with wrong Device ID")
    public void testGetDeviceAppsWithWrongDeviceID() throws Exception{
        MDMResponse response = client.get(Constants.OperationManagement.GET_DEVICE_APPS_ENDPOINT+
                Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID+Constants.OperationManagement.PATH_APPS);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());

    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get operations for device with wrong Device ID")
    public void testGetDeviceOperationsWithWrongDeviceID() throws Exception{
        MDMResponse response = client.get(Constants.OperationManagement.GET_DEVICE_OPERATIONS_ENDPOINT+
                Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get operations for android device")
    public void testGetDeviceOperations() throws Exception{
        MDMResponse response = client.get(Constants.OperationManagement.GET_DEVICE_OPERATIONS_ENDPOINT +
                Constants.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(dependsOnMethods = {"testInstallApps"}, description = "Test get device apps for android deviceD")
    public void testGetDeviceApps() throws Exception{
        MDMResponse response = client.get(Constants.OperationManagement.GET_DEVICE_APPS_ENDPOINT+
                Constants.DEVICE_ID+Constants.OperationManagement.PATH_APPS);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());

    }
}
