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
package org.wso2.mdm.integration.policy;

import com.nimbusds.jose.Payload;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * This class contains integration tests for policy management backend services.
 */
public class PolicyManagement extends TestBase {

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.PolicyManagement.POLICY_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test add policy.")
    public void testAddPolicy() throws Exception {
        MDMResponse response = client.post(Constants.PolicyManagement.ADD_POLICY_ENDPOINT,
        PayloadGenerator.getJsonPayload(
        Constants.PolicyManagement.POLICY_PAYLOAD_FILE_NAME,
        Constants.HTTP_METHOD_POST).toString()
        );

        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertEquals(PayloadGenerator.getJsonPayload(
        Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
        Constants.HTTP_METHOD_POST).toString(),response.getBody());
    }

    @Test(description = "Test view policy.", dependsOnMethods ={"testAddPolicy"})
    public void testViewPolicy() throws Exception {
        MDMResponse response = client.get(Constants.PolicyManagement.VIEW_POLICY_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }
    @Test(description = "Test a secondary policy adding")
    public void addSecondaryPolicy() throws Exception {
        MDMResponse response = client.post(Constants.PolicyManagement.ADD_POLICY_ENDPOINT,
                PayloadGenerator.getJsonPayload(
                        Constants.PolicyManagement.ADD_SECONDARY_POLICY_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString()
        );
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());

    }

    @Test(description = "Test prioritizing policies", dependsOnMethods ={"testAddPolicy","addSecondaryPolicy"})
    public void testPrioritizePolicy() throws Exception {
        MDMResponse response = client.post(Constants.PolicyManagement.PRIORITIZE_POLICY_ENDPOINT,
                PayloadGenerator.getJsonPayload(
                        Constants.PolicyManagement.PRIORITIZE_POLICY_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString()
        );

        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                        Constants.PolicyManagement.PRIORITIZE_POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString(),
                response.getBody().toString(), true
        );
    }


}
