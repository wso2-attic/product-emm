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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

import java.io.IOException;

/**
 * This class contains integration tests for windows policy management backend services.
 */
public class WindowsPolicyManagement extends TestBase {

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.PolicyManagement.POLICY_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test add policy.")
    public void testAddPolicy() throws Exception  {
        MDMResponse response = client.post(Constants.PolicyManagement.ADD_POLICY_ENDPOINT,
                PayloadGenerator.getJsonPayload(Constants.PolicyManagement.WINDOWS_POLICY_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        Assert.assertEquals(PayloadGenerator.getJsonPayload(Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST).toString(),response.getBody());

    }

    @Test(description = "Test add second policy." , dependsOnMethods = { "testAddPolicy"})
    public void testAddSecondPolicy() throws Exception  {
        MDMResponse response = client.post(Constants.PolicyManagement.ADD_POLICY_ENDPOINT,
                PayloadGenerator.getJsonPayload(Constants.PolicyManagement.WINDOWS_ADD_SECOND_POLICY_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        Assert.assertEquals(PayloadGenerator.getJsonPayload(Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_POST).toString(),response.getBody());

    }

    @Test(description = "Test get all policies." , dependsOnMethods = { "testAddSecondPolicy"})
    public void testGetAllPolicies() throws Exception  {
        MDMResponse response = client.get(Constants.PolicyManagement.GET_ALL_POLICIES_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        setPolicyIDs(response.getBody());
    }

    @Test(description = "Test policy priorities." , dependsOnMethods = { "testGetAllPolicies"})
    public void testPolicyPriorities() throws Exception  {
        MDMResponse response = client.put(Constants.PolicyManagement.POLICY_PRIORITIES_ENDPOINT,
                Constants.PolicyManagement.POLICY_PRIORITIES_PAYLOAD_FILE_NAME);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertEquals(PayloadGenerator.getJsonPayload(Constants.PolicyManagement.
                        POLICY_PRIORITIES_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_PUT).toString(),response.getBody());
    }

    @Test(description = "Test policy priorities with erroneous payload." , dependsOnMethods = { "testGetAllPolicies"})
    public void testPolicyPrioritiesWithErroneousPayload() throws Exception  {
        MDMResponse response = client.put(Constants.PolicyManagement.POLICY_PRIORITIES_ENDPOINT,
                Constants.PolicyManagement.POLICY_ERRONEOUS_PAYLOAD_FILE_NAME);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test(description = "Test update policy.", dependsOnMethods = { "testPolicyPriorities"})
    public void testUpdatePolicy() throws Exception {

        MDMResponse response = client.put(Constants.PolicyManagement.UPDATE_WINDOWS_POLICY_ENDPOINT,
                PayloadGenerator.getJsonPayload(
                        Constants.PolicyManagement.WINDOWS_POLICY_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_PUT).toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatus());
        Assert.assertEquals(PayloadGenerator.getJsonPayload(Constants.PolicyManagement.
                        POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_PUT).toString(),response.getBody());
    }

    @Test(description = "Test remove policy.", dependsOnMethods = { "testUpdatePolicy" })
    public void testRemovePolicy() throws Exception {

        MDMResponse response = client.post(Constants.PolicyManagement.REMOVE_POLICY_ENDPOINT,
                Constants.PolicyManagement.REMOVE_WINDOWS_POLICY_PAYLOAD_FILE_NAME);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertEquals(PayloadGenerator.getJsonPayload(Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_DELETE).toString(), response.getBody());
    }

    private void setPolicyIDs(String JsonString) throws IOException {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(JsonString).getAsJsonObject();
        JsonArray policies= (JsonArray) jsonObject.get("responseContent");
        setPolicyIDs(String.valueOf(policies.get(0).getAsJsonObject().get("id")),
                String.valueOf(policies.get(1).getAsJsonObject().get("id")));
    }

    private void setPolicyIDs(String ID, String SecondID) {
        Constants.PolicyManagement.WINDOWS_POLICY_DEVICE_ID= ID;
        Constants.PolicyManagement.WINDOWS_POLICY_SECOND_DEVICE_ID= SecondID;
        Constants.PolicyManagement.POLICY_PRIORITIES_PAYLOAD_FILE_NAME=
                "[{\"id\":"+ID+",\"priority\":1}," +
                        "{\"id\":"+SecondID+",\"priority\":2}]";
        Constants.PolicyManagement.UPDATE_WINDOWS_POLICY_ENDPOINT="/mdm-admin/policies/"+ID;
        Constants.PolicyManagement.REMOVE_WINDOWS_POLICY_PAYLOAD_FILE_NAME="["+ID+"," +""+SecondID+"]";
    }
}
