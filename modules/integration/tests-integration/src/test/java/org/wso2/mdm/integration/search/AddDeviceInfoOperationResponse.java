/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.wso2.mdm.integration.search;

import junit.framework.Assert;
import org.apache.commons.httpclient.HttpStatus;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.automation.test.utils.http.client.HttpResponse;
import org.wso2.mdm.integration.common.Constants;
import org.wso2.mdm.integration.common.OAuthUtil;
import org.wso2.mdm.integration.common.RestClient;
import org.wso2.mdm.integration.common.TestBase;

public class AddDeviceInfoOperationResponse extends TestBase {

    private RestClient client;

    @BeforeTest(alwaysRun = true, groups = { Constants.AndroidOperations.OPERATIONS_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new RestClient(backendHTTPURL, Constants.APPLICATION_JSON, accessTokenString);
    }


    @Test(groups = Constants.AndroidOperations.OPERATIONS_GROUP, description = "Test Android device lock operation.")
    public void waitTillTaskRun() throws Exception {
        Thread.sleep(1);
    }

}

