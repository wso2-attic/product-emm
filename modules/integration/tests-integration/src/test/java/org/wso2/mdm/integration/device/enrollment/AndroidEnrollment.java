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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.AssertUtil;
import org.wso2.mdm.integration.common.Constants;
import org.wso2.mdm.integration.common.RestClient;
import org.wso2.mdm.integration.common.TestBase;

/**
 * This contains testing of Android device enrollment which is necessery to run prior to all other Android related
 * tests.
 */
public class AndroidEnrollment extends TestBase {
    private RestClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.Enrollment.ANDROID_ENROLLMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        client = new RestClient(backendURL);
    }

    @Test(description = "Test an Android device enrollment.")
    public void testAndroidEnrollment() throws Exception {
        HttpResponse response = client.post(Constants.Enrollment.ENROLLMENT_ENDPOINT,
                                            Constants.Enrollment.ANDROID_REQUEST_ENROLLMENT_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), Constants.SUCCESS_CODE);
        JsonElement jsonElement =
                new JsonParser().parse((String) Constants.Enrollment.ANDROID_REQUEST_ENROLLMENT_EXPECTED);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        AssertUtil.jsonPayloadCompare(Constants.Enrollment.ANDROID_REQUEST_ENROLLMENT_EXPECTED,
                                      response.getData().toString(), true);
    }
}
