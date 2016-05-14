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

package org.wso2.mdm.integration.dashboard;

import org.apache.commons.httpclient.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.mdm.integration.common.*;

/**
 * Contains integration tests for Dashboard Monitoring APIs
 */
public class DashboardAPIChecker extends TestBase {

    private MDMHttpClient client;

    @BeforeClass(alwaysRun = true, groups = { Constants.DashboardMonitoring.DASHBOARD_MONITORING_GROUP })
    public void initTest() throws Exception {
        super.init(TestUserMode.SUPER_TENANT_ADMIN);
        String accessTokenString = "Bearer " + OAuthUtil.getOAuthToken(backendHTTPURL, backendHTTPSURL);
        this.client = new MDMHttpClient(backendHTTPSURL, Constants.APPLICATION_JSON, accessTokenString);
    }

    @Test(description = "Testing device-count-overview-api with no-db-data")
    public void testDeviceCountOverviewAPIWithNoDBData() throws Exception {
        MDMResponse response = client.get(Constants.DashboardMonitoring.DEVICE_COUNT_OVERVIEW_API_ENDPOINT);
        org.testng.Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        // checking for total device count response with no data
        Assert.assertTrue(response.getBody().contains("{\"context\":\"Total-device-count\",\"data\":[{\"group\":" +
            "\"total\",\"displayNameForGroup\":\"Total\",\"deviceCount\":0}]}"));
        // checking for connectivity status response with no data
        Assert.assertTrue(response.getBody().contains("{\"context\":\"Device-counts-by-connectivity-statuses\"," +
            "\"groupingAttribute\":\"connectivity-status\",\"data\":[]}"));
    }

    @Test(description = "Testing device-counts-by-potential-vulnerabilities-api with no-db-data")
    public void testDeviceCountsByPotentialVulnerabilitiesAPIWithNoDBData() throws Exception {
        MDMResponse response = client.get(Constants.DashboardMonitoring.
            DEVICE_COUNTS_BY_POTENTIAL_VULNERABILITIES_API_ENDPOINT);
        org.testng.Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        // checking for total device count response with no data
        Assert.assertTrue(response.getBody().
            contains("{\"group\":\"NON_COMPLIANT\",\"displayNameForGroup\":\"Non-compliant\",\"deviceCount\":0}," +
                "{\"group\":\"UNMONITORED\",\"displayNameForGroup\":\"Unmonitored\",\"deviceCount\":0}]}"));
    }

    @Test(description = "Testing non-compliant-device-counts-by-features-api with no-db-data")
    public void testNonCompliantDeviceCountsByFeaturesAPIWithNoDBData() throws Exception {
        MDMResponse response = client.get(Constants.DashboardMonitoring.
            NON_COMPLIANT_DEVICE_COUNTS_BY_FEATURES_API_ENDPOINT);
        org.testng.Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        // checking for total device count response with no data
        Assert.assertTrue(response.getBody().
            contains("[{\"totalRecordCount\":0,\"context\":\"Non-compliant-device-counts-by-features\"," +
                "\"groupingAttribute\":\"non-compliant-feature-code\",\"data\":[]}]"));
    }

    @Test(description = "Testing device-counts-by-groups-api with no-db-data")
    public void testDeviceCountsByGroupsAPIWithNoDBData() throws Exception {
        MDMResponse response = client.get(Constants.DashboardMonitoring.DEVICE_COUNTS_BY_GROUPS_API_ENDPOINT);
        org.testng.Assert.assertEquals(HttpStatus.SC_OK, response.getStatus());
        // checking for total device count response with no data
        Assert.assertTrue(response.getBody().
            contains("[{\"context\":\"Device-counts-by-platforms\",\"groupingAttribute\":\"platform\",\"data\":[]}," +
                "{\"context\":\"Device-counts-by-ownerships\",\"groupingAttribute\":\"ownership\",\"data\":[]}]"));
    }

}
