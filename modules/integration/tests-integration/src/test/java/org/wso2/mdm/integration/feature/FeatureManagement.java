/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.integration.feature;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

/**
 * This class contains integration tests for feature management backend services.
 */
public class FeatureManagement extends TestBase{

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.FeatureManagement.FEATURE_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test view features.")
    public void testViewFeatures() throws Exception {
        MDMResponse response = client.get(Constants.FeatureManagement.VIEW_FEATURES_ENDPOINT +
                    "/" + Constants.ANDROID_DEVICE_TYPE + "/" + Constants.DEVICE_ID + "/features");
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(description = "Test view features with erroneous end point.")
    public void testViewFeaturesWithErroneousEndPoint() throws Exception {
        try {
            client.get(Constants.FeatureManagement.VIEW_FEATURES_ERRONEOUS_ENDPOINT);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }
}
