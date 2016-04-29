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

import com.google.gson.JsonPrimitive;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.Constants;
import org.wso2.mdm.integration.common.OAuthUtil;
import org.wso2.mdm.integration.common.RestClient;
import org.wso2.mdm.integration.common.TestBase;

public class AddDeviceLocationInfo extends TestBase {

    private static Log log = LogFactory.getLog(AddDeviceLocationInfo.class);

    private RestClient client;
    private String endpoint = "/mdm-android-agent/operation";
    private String APP_LIST_RESPONSE = "[{\"name\":\"WSO2%20Agent\",\"package\":\"org.wso2.emm.agent\",\"version\":2,\"USS\":26464}," +
            "{\"name\":\"PT\",\"package\":\"com.zd.pt\",\"version\":1}," +
            "{\"name\":\"TestStorage\",\"package\":\"com.storage.test.milan.teststorage\",\"version\":1}," +
            "{\"name\":\"API%20Demos\",\"package\":\"com.example.android.apis\",\"version\":21}," +
            "{\"name\":\"com.android.gesture.builder\",\"package\":\"com.android.gesture.builder\",\"version\":21}]";

    @BeforeTest(alwaysRun = true, groups = {Constants.AndroidEnrollment.ENROLLMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(alwaysRun = true, groups = {Constants.AndroidEnrollment.ENROLLMENT_GROUP})
    public void sendDeviceApplicationListResponse() throws Exception {

        JsonPrimitive deviceID = new JsonPrimitive(Constants.DEVICE_ID);

//        HttpResponse response = client.post(endpoint+"/"+Constants.DEVICE_ID, "");
//        log.info("----------- " +response.getResponseCode());
//        log.info("----------- " +response.getResponseMessage());

    }

}

