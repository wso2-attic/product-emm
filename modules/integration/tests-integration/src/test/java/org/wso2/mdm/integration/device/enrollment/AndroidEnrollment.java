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
package org.wso2.mdm.integration.device.enrollment;

import com.google.gson.JsonObject;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

/**
 * This contains testing of Android device enrollment which is necessary to run prior to all other Android related
 * tests.
 */
public class AndroidEnrollment extends TestBase {
    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.AndroidEnrollment.ENROLLMENT_GROUP })
    public void initTest()
            throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test an Android device enrollment.")
    public void testEnrollment() throws Exception {
        JsonObject enrollmentData = PayloadGenerator
                .getJsonPayload(Constants.AndroidEnrollment.ENROLLMENT_PAYLOAD_FILE_NAME, Constants.HTTP_METHOD_POST);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        MDMResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertTrue(
                response.getBody().contains(Constants.AndroidEnrollment.ENROLLMENT_RESPONSE_PAYLOAD_FOR_POST));
    }

    @Test(description = "Test an Android device enrollment with no device identifier")
    public void testEnrollmentWithErroneousInfo()
            throws Exception {
        JsonObject enrollmentData = PayloadGenerator
                .getJsonPayload(Constants.AndroidEnrollment.ENROLLMENT_ERRONEOUS_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        MDMResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT, enrollmentData.toString());
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test(description = "Test an Android device is enrolled.", dependsOnMethods = {
            "testEnrollment" })
    public void testIsEnrolled() throws Exception {
        MDMResponse response = client
                .get(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID + "/status");
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());

        AssertUtil.jsonPayloadCompare(PayloadGenerator
                .getJsonPayload(Constants.AndroidEnrollment.ENROLLMENT_RESPONSE_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_GET).toString(), response.getBody(), true);
    }

    @Test(description = "Test modify enrollment.", dependsOnMethods = {
            "testIsEnrolled" })
    public void testModifyEnrollment() throws Exception {
        JsonObject enrollmentData = PayloadGenerator
                .getJsonPayload(Constants.AndroidEnrollment.ENROLLMENT_PAYLOAD_FILE_NAME, Constants.HTTP_METHOD_PUT);
        enrollmentData.addProperty(Constants.DEVICE_IDENTIFIER_KEY, Constants.DEVICE_ID);
        MDMResponse response = client
                .put(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID, enrollmentData.toString());
        //        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
        //                                              Constants.AndroidEnrollment.ENROLLMENT_RESPONSE_PAYLOAD_FILE_NAME,
        //                                              Constants.HTTP_METHOD_PUT).toString(), response.getBody(), true);
    }

    @Test(description = "Test disenrollment.", dependsOnMethods = {
            "testIsEnrolled" })
    public void testDisEnrollDevice() throws Exception {
        MDMResponse response = client.delete(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertTrue(
                response.getBody().contains(Constants.AndroidEnrollment.ENROLLMENT_RESPONSE_PAYLOAD_FOR_DELETE));
    }

    @Test(description = "Test disenrollment with wrong device ID.", dependsOnMethods = {
            "testIsEnrolled" })
    public void testDisEnrollDeviceWithWrongDeviceId() throws Exception {
        MDMResponse response = client
                .delete(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.NUMBER_NOT_EQUAL_TO_DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

    @Test(description = "Test disenrollment for already disenrolled device.", dependsOnMethods = {
            "testDisEnrollDevice" })
    public void testDisEnrollDeviceSameDeviceTwice() throws Exception {
        MDMResponse response = client.delete(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_NOT_FOUND, response.getStatus());
    }

}
