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
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.ROLE_NAME);
        MDMResponse response = client.post(url,
                PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString());
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getStatus());
    }

    @Test(description = "Test view roles")
    public void testViewRoles() throws Exception {
        MDMResponse response = client.get(Constants.RoleManagement.ROLE_ENDPOINT);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        AssertUtil.jsonPayloadCompare(PayloadGenerator.getJsonPayload(Constants.RoleManagement.
                        ROLE_RESPONSE_PAYLOAD_FILE_NAME,
                Constants.HTTP_METHOD_GET).toString(), response.getBody(), true);
    }

    @Test(description = "Test add role with erroneous payload.")
    public void testAddRoleWithErroneousPayload() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.ROLE_NAME);
        MDMResponse response = client.post(url,
                PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_ERRONEOUS_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_POST).toString());
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test(description = "Test update role.", dependsOnMethods = { "testAddRole"})
    public void testUpdateUser() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.ROLE_NAME);
        MDMResponse response = client.put(url, PayloadGenerator.getJsonPayload(Constants.RoleManagement.
                ROLE_UPDATE_PAYLOAD_FILE_NAME, Constants.HTTP_METHOD_PUT).toString());
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(description = "Test update role with erroneous payload.", dependsOnMethods = { "testAddRole"})
    public void testUpdateRoleWithErroneousPayload() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.ROLE_NAME);
        MDMResponse response = client.put(url,
                PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_ERRONEOUS_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_PUT).toString());
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test(description = "Test update permission role.", dependsOnMethods = { "testUpdateUser"})
    public void testUpdateRolePermission() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.UPDATED_ROLE_NAME);
        MDMResponse response = client.put(url,
                    PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_PAYLOAD_FILE_NAME,
                            Constants.HTTP_METHOD_PUT).toString());
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(description = "Test update role with erroneous payload.", dependsOnMethods = { "testUpdateUser"})
    public void testUpdateRolePermissionWithErroneousPayload() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.UPDATED_ROLE_NAME);
        MDMResponse response = client.put(url,
                PayloadGenerator.getJsonPayload(Constants.RoleManagement.ROLE_ERRONEOUS_PAYLOAD_FILE_NAME,
                        Constants.HTTP_METHOD_PUT).toString());
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    @Test(description = "Test remove role.", dependsOnMethods = { "testUpdateRolePermission" })
    public void testRemoveRole() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.UPDATED_ROLE_NAME);
        MDMResponse response = client.delete(url);
            Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(description = "Test remove role without roles.", dependsOnMethods = { "testUpdateRolePermission" })
    public void testRemoveRoleWithoutRoles() throws Exception {
        String url=GetURL(Constants.RoleManagement.ROLE_ENDPOINT, Constants.RoleManagement.UPDATED_ROLE_NAME);
        MDMResponse response = client.delete(url);
        Assert.assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, response.getStatus());
    }

    private String GetURL(String endPoint,String param){
        return endPoint+"?rolename="+param;
    }

}