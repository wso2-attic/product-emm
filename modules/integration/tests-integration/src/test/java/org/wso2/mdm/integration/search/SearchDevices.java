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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

public class SearchDevices extends TestBase {
    private MDMHttpClient client;

    @BeforeTest(alwaysRun = true, groups = { Constants.AndroidEnrollment.ENROLLMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Testing the location search")
    public void testAddPolicyWithErroneousPayload() throws Exception {
        JsonObject jsonObject = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject item = new JsonObject();
        item.addProperty("key", "LOCATION");
        item.addProperty("operator", "=");
        item.addProperty("value", "Colombo");
        item.addProperty("state", "AND");
        array.add(item);
        jsonObject.add("conditions", array);
        MDMResponse response = client
                .post(Constants.MobileDeviceManagement.SEARCH_DEVICE_ENDPOINT, jsonObject.toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }
}

