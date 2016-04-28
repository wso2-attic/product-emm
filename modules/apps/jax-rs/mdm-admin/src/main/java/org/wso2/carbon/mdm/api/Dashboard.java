/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
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

package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.analytics.dashboard.GadgetDataService;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.beans.DashboardGadgetDataWrapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Produces({"application/json", "application/xml"})
@Consumes({"application/json", "application/xml"})

public class Dashboard {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(Dashboard.class);

    @GET
    @Path("device-overview")
    public Response getOverviewDeviceCounts() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating TotalDeviceCount Data Wrapper
        Map<String, Object> totalDeviceCountDataWrapper = new HashMap<>();
        totalDeviceCountDataWrapper.put("group", "total");
        totalDeviceCountDataWrapper.put("label", "Total");
        //totalDeviceCountDataWrapper.put("count", gadgetDataService.getTotalDeviceCount(null));
        // creating ActiveDeviceCount Data Wrapper
        Map<String, Object> activeDeviceCountDataWrapper = new HashMap<>();
        activeDeviceCountDataWrapper.put("group", "active");
        activeDeviceCountDataWrapper.put("label", "Active");
        activeDeviceCountDataWrapper.put("count", gadgetDataService.getActiveDeviceCount());
        // creating inactiveDeviceCount Data Wrapper
        Map<String, Object> inactiveDeviceCountDataWrapper = new HashMap<>();
        inactiveDeviceCountDataWrapper.put("group", "inactive");
        inactiveDeviceCountDataWrapper.put("label", "Inactive");
        inactiveDeviceCountDataWrapper.put("count", gadgetDataService.getInactiveDeviceCount());
        // creating removedDeviceCount Data Wrapper
        Map<String, Object> removedDeviceCountDataWrapper = new HashMap<>();
        removedDeviceCountDataWrapper.put("group", "removed");
        removedDeviceCountDataWrapper.put("label", "Removed");
        removedDeviceCountDataWrapper.put("count", gadgetDataService.getRemovedDeviceCount());

        List<Map<String, Object>> overviewDeviceCountsDataWrapper = new ArrayList<>();
        overviewDeviceCountsDataWrapper.add(totalDeviceCountDataWrapper);
        overviewDeviceCountsDataWrapper.add(activeDeviceCountDataWrapper);
        overviewDeviceCountsDataWrapper.add(inactiveDeviceCountDataWrapper);
        overviewDeviceCountsDataWrapper.add(removedDeviceCountDataWrapper);

        dashboardGadgetDataWrapper.setContext("overview");
        dashboardGadgetDataWrapper.setData(overviewDeviceCountsDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("potential-vulnerabilities")
    public Response getVulnerableDeviceCounts() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating non-compliant Data Wrapper
        Map<String, Object> nonCompliantDeviceCountDataWrapper = new HashMap<>();
        nonCompliantDeviceCountDataWrapper.put("group", "non-complaint");
        nonCompliantDeviceCountDataWrapper.put("label", "Non-Compliant");
        nonCompliantDeviceCountDataWrapper.put("count", gadgetDataService.getNonCompliantDeviceCount());
        // creating unmonitoredDeviceCount Data Wrapper
        Map<String, Object> unmonitoredDeviceCountDataWrapper = new HashMap<>();
        unmonitoredDeviceCountDataWrapper.put("group", "unmonitored");
        unmonitoredDeviceCountDataWrapper.put("label", "Unmonitored");
        unmonitoredDeviceCountDataWrapper.put("count", gadgetDataService.getUnmonitoredDeviceCount());

        List<Map<String, Object>> vulnerableDeviceCountsDataWrapper = new ArrayList<>();
        vulnerableDeviceCountsDataWrapper.add(nonCompliantDeviceCountDataWrapper);
        vulnerableDeviceCountsDataWrapper.add(unmonitoredDeviceCountDataWrapper);

        dashboardGadgetDataWrapper.setContext("potential-vulnerabilities");
        dashboardGadgetDataWrapper.setData(vulnerableDeviceCountsDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }
}
