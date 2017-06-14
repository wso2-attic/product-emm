/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.integration.device.enrollment;

import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

import java.io.File;
import java.net.URL;

/**
 * This contains testing of Windows device enrollment which is necessary to run prior to all other Windows related
 * tests.
 */
public class WindowsEnrollment extends TestBase {
    private MDMHttpClient client;
    private static String bsd;
    private static String UserToken = "UserToken";
    private static final String BSD_PLACEHOLDER = "{BinarySecurityToken}";
    Base64 base64Encoder;

    @BeforeClass(alwaysRun = true, groups = {Constants.WindowsEnrollment.WINDOWS_ENROLLMENT_GROUP})
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    /**
     * Test the Windows Discovery Get endpoint to see if the server is available.
     *
     * @throws Exception
     */
    @Test(groups = Constants.WindowsEnrollment.WINDOWS_ENROLLMENT_GROUP, description = "Test Windows Discovery get " +
            "request.")
    public void testServerAvailability() throws Exception {
        client.setHttpHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_SOAP_XML);
        MDMResponse response = client.get(Constants.WindowsEnrollment.DISCOVERY_GET_URL);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(groups = Constants.WindowsEnrollment.WINDOWS_ENROLLMENT_GROUP, description = "Test Windows Discovery post" +
            " request.", dependsOnMethods = {"testServerAvailability"})
    public void testDiscoveryPost() throws Exception {
        String xml = readXML(Constants.WindowsEnrollment.DISCOVERY_POST_FILE, Constants.UTF8);
        client.setHttpHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_SOAP_XML);
        MDMResponse response = client.post(Constants.WindowsEnrollment.DISCOVERY_POST_URL, xml);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(groups = Constants.WindowsEnrollment.WINDOWS_ENROLLMENT_GROUP, description = "Test Windows BST.",
            dependsOnMethods = {"testDiscoveryPost"})
    public void testBST() throws Exception {
        String token = "token";
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(Constants.WindowsEnrollment.BSD_PAYLOAD);
        JsonNode node = root.path("credentials");
        ((ObjectNode) node).put(token, OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL));
        client.setHttpHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON);
        MDMResponse response = client.post(Constants.WindowsEnrollment.BSD_URL, root.toString());
        bsd = response.getBody();
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(groups = Constants.WindowsEnrollment.WINDOWS_ENROLLMENT_GROUP, description = "Test Windows MS XCEP post" +
            " request.", dependsOnMethods = {"testBST"})
    public void testMSXCEP() throws Exception {
        base64Encoder = new Base64();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(bsd);
        JsonNode token = node.get(UserToken);
        String encodedToken = base64Encoder.encodeToString(token.getTextValue().getBytes());
        String xml = readXML(Constants.WindowsEnrollment.MS_XCEP_FILE, Constants.UTF8);
        String payload = xml.replace(BSD_PLACEHOLDER, encodedToken);
        client.setHttpHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_SOAP_XML);
        MDMResponse response = client.post(Constants.WindowsEnrollment.MS_EXCEP, payload);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    @Test(groups = Constants.WindowsEnrollment.WINDOWS_ENROLLMENT_GROUP, description = "Test Windows WSETP post " +
            "request.", dependsOnMethods = {"testMSXCEP"})
    public void testWSETP() throws Exception {
        base64Encoder = new Base64();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(bsd);
        JsonNode token = node.get(UserToken);
        String encodedToken = base64Encoder.encodeToString(token.getTextValue().getBytes());
        String xml = readXML(Constants.WindowsEnrollment.WS_STEP_FILE, Constants.UTF8);
        String payload = xml.replace(BSD_PLACEHOLDER, encodedToken);
        client.setHttpHeader(Constants.CONTENT_TYPE, Constants.APPLICATION_SOAP_XML);
        MDMResponse response = client.post(Constants.WindowsEnrollment.WSTEP_URL, payload);
        Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
    }

    private String readXML(String fileName, String characterEncoding) throws Exception {
        URL url = ClassLoader.getSystemResource(fileName);
        File file = new File(url.toURI());
        return FileUtils.readFileToString(file, characterEncoding);
    }
}
