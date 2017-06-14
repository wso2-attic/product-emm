/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.wso2.mdm.integration.search;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.Constants;
import org.wso2.mdm.integration.common.OAuthUtil;
import org.wso2.mdm.integration.common.RestClient;
import org.wso2.mdm.integration.common.TestBase;

public class AddDeviceInfoOperation extends TestBase {
    private RestClient client;
    private static final String ANDROID_DEVICE_MGT_API = "/api/device-mgt/android/v1.0";

    @BeforeTest(alwaysRun = true, groups = { Constants.AndroidEnrollment.ENROLLMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPURL, Constants.APPLICATION_JSON, accessTokenString);
    }


    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android device location "
            + "operation.")
    public void testLocation() throws Exception {
        HttpResponse response = client.post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.LOCATION_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android device information " +
            "operation.")
    public void testDeviceInfo() throws Exception {
        HttpResponse response = client.post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.DEVICE_INFO_ENDPOINT,
                Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android application list "
            + "operation.")
    public void testApplicationList() throws Exception {
        HttpResponse response = client
                .post(ANDROID_DEVICE_MGT_API + Constants.AndroidOperations.APPLICATION_LIST_ENDPOINT,
                        Constants.AndroidOperations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }
}

