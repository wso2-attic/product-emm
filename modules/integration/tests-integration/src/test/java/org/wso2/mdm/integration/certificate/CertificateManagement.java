/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.mdm.integration.certificate;

import org.apache.commons.httpclient.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

/**
 * Contains integration test for certificate management related tasks such as adding certificates
 * and retrieving certificates
 */
public class CertificateManagement extends TestBase {

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.CertificateManagement.CERTIFICATE_MANAGEMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPSURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Test certificate adding.")
    public void testAddCertificate() throws Exception {
        MDMResponse response = client.post(Constants.CertificateManagement.CERTIFICATE_ADD_ENDPOINT,
                                           Constants.CertificateManagement.CERTIFICATE_ADD_PAYLOAD);
        Assert.assertEquals(HttpStatus.SC_CREATED ,response.getStatus());
    }

    @Test(description = "Test get certificate.")
    public void testGetCertificate() throws Exception {
        MDMResponse response = client.get(Constants.CertificateManagement.CERTIFICATE_GET_ENDPOINT);
        org.testng.Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertTrue(response.getBody().contains(Constants.CertificateManagement.CERTIFICATE_PAYLOAD));
    }

    @Test(description = "Test get all certificate.")
    public void testGetAllCertificate() throws Exception {
        MDMResponse response = client.get(Constants.CertificateManagement.CERTIFICATE_GET_ALL_ENDPOINT);
        org.testng.Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        Assert.assertTrue(response.getBody().contains(Constants.CertificateManagement.CERTIFICATE_PAYLOAD));
    }

}
