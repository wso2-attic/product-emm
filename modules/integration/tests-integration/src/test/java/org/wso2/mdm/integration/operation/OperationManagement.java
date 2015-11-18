package org.wso2.mdm.integration.operation;

import com.google.gson.JsonObject;
import org.junit.Test;
import org.testng.annotations.BeforeClass;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.Constants;
import org.wso2.mdm.integration.common.OAuthUtil;
import org.wso2.mdm.integration.common.RestClient;
import org.wso2.mdm.integration.common.TestBase;


/**
 * This class contains integration tests for API Operation management backend services.
 */
public class OperationManagement extends TestBase {

    private RestClient client;
    private JsonObject device;

    @BeforeClass(alwaysRun = true, groups = { Constants.OperationManagement.OPERATION_MANAGEMENT_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPURL, Constants.APPLICATION_JSON, accessTokenString);
    }

}
