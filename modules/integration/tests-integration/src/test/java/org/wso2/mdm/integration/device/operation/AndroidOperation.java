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
package org.wso2.mdm.integration.device.operation;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.Constants;
import org.wso2.mdm.integration.common.RestClient;
import org.wso2.mdm.integration.common.TestBase;

/**
 * This contain tests to check operations supported by Android. Test are executed against a previously enrolled device
 */
public class AndroidOperation extends TestBase {
    private RestClient client;

    @BeforeTest(alwaysRun = true, groups = { Constants.Enrollment.ANDROID_ENROLLMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        client = new RestClient(backendURL, Constants.APPLICATION_JSON);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android device lock operation.")
    public void testLock() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_LOCK_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android device location operation.")
    public void testLocation() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_LOCATION_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android device clear password " +
                                                                        "operation.")
    public void testClearPassword() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_CLEAR_PASSWORD_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android device camera operation.")
    public void testCamera() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_CAMERA_ENDPOINT,
                                            Constants.Operations.ANDROID_CAMERA_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android device information operation.")
    public void testDeviceInfo() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_DEVICE_INFO_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android enterprise-wipe operation.")
    public void testEnterpriseWipe() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_ENTERPRISE_WIPE_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android wipe data operation.")
    public void testWipeData() throws Exception {
        System.out.println(Constants.Operations.ANDROID_INSTALL_APPS_PAYLOAD);
        HttpResponse response = client.post(Constants.Operations.ANDROID_WIPE_DATA_ENDPOINT,
                                            Constants.Operations.ANDROID_WIPE_DATA_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android application list operation.")
    public void testApplicationList() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_APPLICATION_LIST_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android ring operation.")
    public void testRing() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_RING_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android mute operation.")
    public void testMute() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_MUTE_ENDPOINT,
                                            Constants.Operations.COMMAND_OPERATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android install apps operation.")
    public void testInstallApps() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_INSTALL_APPS_ENDPOINT,
                                            Constants.Operations.ANDROID_INSTALL_APPS_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android uninstall apps operation.")
    public void testUninstallApps() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_UNINSTALL_APPS_ENDPOINT,
                                            Constants.Operations.ANDROID_INSTALL_APPS_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android blacklist apps operation.")
    public void testBlacklistApps() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_BLACKLIST_APPS_ENDPOINT,
                                            Constants.Operations.ANDROID_INSTALL_APPS_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android notification operation.")
    public void testNotification() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_NOTIFICATION_ENDPOINT,
                                            Constants.Operations.ANDROID_NOTIFICATION_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android WiFi operation.")
    public void testWiFi() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_WIFI_ENDPOINT,
                                            Constants.Operations.ANDROID_WIFI_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android encrypt operation.")
    public void testEncrypt() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_ENCRYPT_ENDPOINT,
                                            Constants.Operations.ANDROID_ENCRYPT_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android change lock operation.")
    public void testChangeLock() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_CHANGE_LOCK_ENDPOINT,
                                            Constants.Operations.ANDROID_CHANGE_LOCK_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android password policy operation.")
    public void testPasswordPolicy() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_PASSWORD_POLICY_ENDPOINT,
                                            Constants.Operations.ANDROID_PASSWORD_POLICY_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

    @Test(groups = Constants.Operations.OPERATIONS_GROUP, description = "Test Android web clip operation.")
    public void testWebClip() throws Exception {
        HttpResponse response = client.post(Constants.Operations.ANDROID_WEB_CLIP_ENDPOINT,
                                            Constants.Operations.ANDROID_WEB_CLIP_PAYLOAD);
        Assert.assertEquals(response.getResponseCode(), HttpStatus.SC_CREATED);
    }

}
