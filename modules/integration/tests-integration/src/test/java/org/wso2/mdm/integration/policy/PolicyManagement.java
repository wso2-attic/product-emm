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

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;


/**
 * This class contains integration tests for policy management backend services.
 */
public class PolicyManagement extends TestBase {

    private RestClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.PolicyManagement.POLICY_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test add policy.")
    public void testAddPolicy()   {
        HttpResponse response = null;
        try {
            response = client.post(Constants.PolicyManagement.ADD_POLICY_ENDPOINT,
                        PayloadGenerator.getJsonPayload(
                                Constants.PolicyManagement.POLICY_PAYLOAD_FILE_NAME,
                                Constants.HTTP_METHOD_POST).toString()
                );
            Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
            AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                            Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_POST).toString(),
                    response.getData().toString(), true
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test(description = "Test view policy list.", dependsOnMethods = { "testAddPolicy"})
    public void testViewPolicyList() throws Exception {
        HttpResponse response = client.get(Constants.PolicyManagement.VIEW_POLICY_LIST_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
//        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
//                        Constants.PolicyManagement.VIEW_POLICY_LIST_RESPONSE_PAYLOAD_FILE_NAME,
//                        Constants.HTTP_METHOD_GET).toString(),
//                response.getData().toString(), true
//        );
    }

    @Test(description = "Test update policy.", dependsOnMethods = { "testViewPolicyList"})
    public void testUpdatePolicy()  {
        HttpResponse response = null;
        try {
            response = client.put(Constants.PolicyManagement.UPDATE_POLICY_ENDPOINT,
                    PayloadGenerator.getJsonPayload(
                            Constants.PolicyManagement.POLICY_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_PUT).toString()
            );
            Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
            AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                            Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_PUT).toString(),
                    response.getData().toString(), true
            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test(description = "Test remove policy.", dependsOnMethods = { "testAddPolicy" })
    public void testRemovePolicy()  {
        HttpResponse response = null;
        try {
            response = client.post(Constants.PolicyManagement.REMOVE_POLICY_ENDPOINT,
                    PayloadGenerator.getJsonPayload(
                            Constants.PolicyManagement.REMOVE_POLICY_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_POST).toString()
            );
            Assert.assertEquals(HttpStatus.SC_OK, response.getResponseCode());
            AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(
                            Constants.PolicyManagement.POLICY_RESPONSE_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_POST).toString(),
                    response.getData().toString(), true
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
