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
package org.wso2.mdm.integration.role;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * This class contains integration tests for role management backend services.
 */
public class RoleManagement extends TestBase {

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.RoleManagement.ROLE_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test add role.")
    public void testAddRole() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT);
        MDMResponse response = client.post(url,
                PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatus());
    }

    @Test(description = "Test update permission role.", dependsOnMethods = { "testAddRole"})
    public void testUpdateRolePermission() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT);
        MDMResponse response = client.put(url,
                    PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_PUT).toString());
        File logFile = new File("/home/tharinda/Working/EMM/product-mdm/testUpdateRolePermission.txt");

        BufferedWriter writer = new BufferedWriter(new FileWriter(logFile));
        writer.write(response.getBody());
        writer.write(url);
        writer.write("hello");
        writer.close();
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(description = "Test remove user.", dependsOnMethods = { "testUpdateRolePermission" })
    public void testRemoveRole() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT);
        MDMResponse response = client.delete(url);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    private String GetURL(String endPoint){
        return endPoint+"?rolename="+Constants.RoleManagement.ROLE_NAME;
    }

}