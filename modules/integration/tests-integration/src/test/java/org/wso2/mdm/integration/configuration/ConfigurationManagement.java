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

package org.wso2.mdm.integration.configuration;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

/**
 * This class contains integration tests for configuration management backend services.
 */
public class ConfigurationManagement extends TestBase {

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.ConfigurationManagement.CONFIGURATION_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }
    @Test(description = "Test save configuration.")
    public void testSaveConfiguration() throws Exception {
        MDMResponse response = client.put(Constants.ConfigurationManagement.CONFIGURATION_ENDPOINT,
                PayloadGenerator.getJsonPayload(
                        Constants.ConfigurationManagement.CONFIGURATION_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_PUT).toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                        Constants.ConfigurationManagement.CONFIGURATION_RESPONSE_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_PUT).toString(), response.getBody(), true);
    }
    @Test(description = "Test get configuration.", dependsOnMethods = { "testSaveConfiguration"})
    public void testGetConfiguration() throws Exception {
        MDMResponse response = client.get(Constants.ConfigurationManagement.CONFIGURATION_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                        Constants.ConfigurationManagement.CONFIGURATION_RESPONSE_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_GET).toString(), response.getBody(), true);
    }

    @Test(description = "Test get configuration with erroneous end point.",
            dependsOnMethods = { "testSaveConfiguration"})
    public void testGetConfigurationWithErroneousEndPoint() throws Exception {
        try {
            client.get(Constants.ConfigurationManagement.CONFIGURATION_ERRONEOUS_ENDPOINT);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("HTTP response code: 400"));
        }
    }
}
