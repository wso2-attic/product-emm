package org.wso2.mdm.integration.device.operation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.*;

/**
 * This contain tests to check operations supported by Windows. Test are executed against a previously enrolled device
 */
public class WindowsOperation extends TestBase {
    private RestClient client;

    @BeforeTest(alwaysRun = true, groups = { Constants.WindowsOperation.WINDOWS_OPERATION_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPURL, Constants.APPLICATION_JSON, accessTokenString);
        //Enroll a device
    }

    @Test(groups = Constants.WindowsOperation.WINDOWS_OPERATION_GROUP, description = "Test Windows device lock operation.")
    public void testLock() throws Exception {
        HttpResponse response = client.post(Constants.WindowsOperation.LOCK_ENDPOINT,
                Constants.WindowsOperation.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.WindowsOperation.WINDOWS_OPERATION_GROUP, description = "Test Windows device wipe operation.")
    public void testWipe() throws Exception {
        HttpResponse response = client.post(Constants.WindowsOperation.WIPE_ENDPOINT,
                Constants.WindowsOperation.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.WindowsOperation.WINDOWS_OPERATION_GROUP, description = "Test Windows device ring operation.")
    public void testRing() throws Exception {
        HttpResponse response = client.post(Constants.WindowsOperation.RING_ENDPOINT,
                Constants.WindowsOperation.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.WindowsOperation.WINDOWS_OPERATION_GROUP, description = "Test Windows device disEnroll operation.")
    public void testDisEnroll() throws Exception {
        HttpResponse response = client.post(Constants.WindowsOperation.DISENROLL_ENDPOINT,
                Constants.WindowsOperation.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }

    @Test(groups = Constants.WindowsOperation.WINDOWS_OPERATION_GROUP, description = "Test Windows device reset operation.")
    public void testReset() throws Exception {
        HttpResponse response = client.post(Constants.WindowsOperation.RESET_ENDPOINT,
                Constants.WindowsOperation.DEVICE_ID);
        Assert.assertEquals(HttpStatus.SC_CREATED, response.getResponseCode());
    }
}
