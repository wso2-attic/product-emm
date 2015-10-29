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

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

/**
 * This contains testing of Android device enrollment which is necessary to run prior to all other Android related
 * tests.
 */
public class AndroidEnrollment extends TestBase {
    private RestClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.AndroidEnrollment.ANDROID_ENROLLMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test an Android device enrollment.")
    public void testEnrollment() throws Exception {
        HttpResponse response = client.post(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT,
                                            Constants.AndroidEnrollment.ANDROID_REQUEST_ENROLLMENT_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_OK);
        AssertUtil.jsonPayloadCompare(Constants.AndroidEnrollment.ANDROID_REQUEST_ENROLLMENT_EXPECTED,
                                      response.getData().toString(), true);
    }

    @Test(description = "Test an Android device is enrolled.", dependsOnMethods = { "testEnrollment" })
    public void testIsEnrolled() throws Exception {
        HttpResponse response = client.get(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
        AssertUtil.jsonPayloadCompare(Constants.AndroidEnrollment.ANDROID_REQUEST_IS_ENROLLMENT_EXPECTED,
                                      response.getData().toString(), true);
    }

    @Test(description = "Test modify enrollment.", dependsOnMethods = { "testEnrollment" })
    public void testModifyEnrollment() throws Exception {
        HttpResponse response = client.put(Constants.AndroidEnrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID,
                                           Constants.AndroidEnrollment.ANDROID_REQUEST_MODIFY_ENROLLMENT_PAYLOAD);
        AssertUtil.jsonPayloadCompare(Constants.AndroidEnrollment.ANDROID_REQUEST_MODIFY_ENROLLMENT_EXPECTED,
                                      response.getData().toString(), true);
    }

    /*@Test(description = "Test disenrollment.", dependsOnGroups = { Constants.Operations.OPERATIONS_GROUP })
    public void testDisEnrollDevice() throws Exception {
        int response = client.delete(Constants.Enrollment.ENROLLMENT_ENDPOINT + Constants.DEVICE_ID);
        Assert.assertEquals(response, HttpStatus.SC_ACCEPTED);
    }*/
}
