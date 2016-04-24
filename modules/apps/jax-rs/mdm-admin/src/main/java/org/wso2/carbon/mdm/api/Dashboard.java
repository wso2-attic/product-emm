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

import javax.jws.WebService;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.*;

@WebService
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
        int totalDeviceCount = gadgetDataService.getTotalDeviceCount(null);
        if (totalDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> totalDeviceCountDataWrapper = new LinkedHashMap<>();
        totalDeviceCountDataWrapper.put("group", "total");
        totalDeviceCountDataWrapper.put("label", "Total");
        totalDeviceCountDataWrapper.put("count", totalDeviceCount);

        // creating ActiveDeviceCount Data Wrapper
        int activeDeviceCount = gadgetDataService.getActiveDeviceCount();
        if (activeDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> activeDeviceCountDataWrapper = new LinkedHashMap<>();
        activeDeviceCountDataWrapper.put("group", "active");
        activeDeviceCountDataWrapper.put("label", "Active");
        activeDeviceCountDataWrapper.put("count", activeDeviceCount);

        // creating inactiveDeviceCount Data Wrapper
        int inactiveDeviceCount = gadgetDataService.getActiveDeviceCount();
        if (inactiveDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> inactiveDeviceCountDataWrapper = new LinkedHashMap<>();
        inactiveDeviceCountDataWrapper.put("group", "inactive");
        inactiveDeviceCountDataWrapper.put("label", "Inactive");
        inactiveDeviceCountDataWrapper.put("count", inactiveDeviceCount);

        // creating removedDeviceCount Data Wrapper
        int removedDeviceCount = gadgetDataService.getRemovedDeviceCount();
        if (removedDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> removedDeviceCountDataWrapper = new LinkedHashMap<>();
        removedDeviceCountDataWrapper.put("group", "removed");
        removedDeviceCountDataWrapper.put("label", "Removed");
        removedDeviceCountDataWrapper.put("count", removedDeviceCount);

        List<Map<String, Object>> overviewDeviceCountsDataWrapper = new ArrayList<>();
        overviewDeviceCountsDataWrapper.add(totalDeviceCountDataWrapper);
        overviewDeviceCountsDataWrapper.add(activeDeviceCountDataWrapper);
        overviewDeviceCountsDataWrapper.add(inactiveDeviceCountDataWrapper);
        overviewDeviceCountsDataWrapper.add(removedDeviceCountDataWrapper);

        dashboardGadgetDataWrapper.setContext("device-overview");
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
        int nonCompliantDeviceCount = gadgetDataService.getNonCompliantDeviceCount();
        if (nonCompliantDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> nonCompliantDeviceCountDataWrapper = new LinkedHashMap<>();
        nonCompliantDeviceCountDataWrapper.put("group", "non-complaint");
        nonCompliantDeviceCountDataWrapper.put("label", "Non-Compliant");
        nonCompliantDeviceCountDataWrapper.put("count", nonCompliantDeviceCount);

        // creating unmonitoredDeviceCount Data Wrapper
        int unmonitoredDeviceCount = gadgetDataService.getUnmonitoredDeviceCount();
        if (unmonitoredDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> unmonitoredDeviceCountDataWrapper = new LinkedHashMap<>();
        unmonitoredDeviceCountDataWrapper.put("group", "unmonitored");
        unmonitoredDeviceCountDataWrapper.put("label", "Unmonitored");
        unmonitoredDeviceCountDataWrapper.put("count", unmonitoredDeviceCount);

        List<Map<String, Object>> vulnerableDeviceCountsDataWrapper = new ArrayList<>();
        vulnerableDeviceCountsDataWrapper.add(nonCompliantDeviceCountDataWrapper);
        vulnerableDeviceCountsDataWrapper.add(unmonitoredDeviceCountDataWrapper);

        dashboardGadgetDataWrapper.setContext("potential-vulnerabilities");
        dashboardGadgetDataWrapper.setData(vulnerableDeviceCountsDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("non-compliant-by-feature")
    public Response getNonCompliantDeviceCountsByFeatures() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        Map<String, Integer> nonCompliantDeviceCountsByFeatures = gadgetDataService.
                getNonCompliantDeviceCountsByFeatures();

        if (nonCompliantDeviceCountsByFeatures == null) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }

        Map<String, Object> nonCompliantDeviceCountByFeatureDataWrapper;
        List<Map<String, Object>> nonCompliantDeviceCountsByFeaturesDataWrapper = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : nonCompliantDeviceCountsByFeatures.entrySet()) {
            nonCompliantDeviceCountByFeatureDataWrapper = new LinkedHashMap<>();
            nonCompliantDeviceCountByFeatureDataWrapper.put("group", entry.getKey());
            nonCompliantDeviceCountByFeatureDataWrapper.put("label", entry.getKey());
            nonCompliantDeviceCountByFeatureDataWrapper.put("count", entry.getValue());
            nonCompliantDeviceCountsByFeaturesDataWrapper.add(nonCompliantDeviceCountByFeatureDataWrapper);
        }

        dashboardGadgetDataWrapper.setContext("non-compliant-by-feature");
        dashboardGadgetDataWrapper.setData(nonCompliantDeviceCountsByFeaturesDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("device-groupings")
    public Response getDeviceGroupingCounts() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();

        // creating device-Counts-by-platforms Data Wrapper
        Map<String, Integer> deviceCountsByPlatforms = gadgetDataService.getDeviceCountsByPlatforms(null);
        if (deviceCountsByPlatforms == null) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }

        Map<String, Object> deviceCountByPlatformDataWrapper;
        List<Map<String, Object>> deviceCountsByPlatformsDataWrapper = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : deviceCountsByPlatforms.entrySet()) {
            deviceCountByPlatformDataWrapper = new LinkedHashMap<>();
            deviceCountByPlatformDataWrapper.put("group", entry.getKey());
            deviceCountByPlatformDataWrapper.put("label", entry.getKey());
            deviceCountByPlatformDataWrapper.put("count", entry.getValue());
            deviceCountsByPlatformsDataWrapper.add(deviceCountByPlatformDataWrapper);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper1.setContext("device-Counts-by-platforms");
        dashboardGadgetDataWrapper1.setData(deviceCountsByPlatformsDataWrapper);

        responsePayload.add(dashboardGadgetDataWrapper1);

        // creating device-Counts-by-ownership-types Data Wrapper
        Map<String, Integer> deviceCountsByOwnershipTypes = gadgetDataService.getDeviceCountsByOwnershipTypes(null);
        if (deviceCountsByOwnershipTypes == null) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }

        Map<String, Object> deviceCountByOwnershipTypeDataWrapper;
        List<Map<String, Object>> deviceCountsByOwnershipTypesDataWrapper = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : deviceCountsByOwnershipTypes.entrySet()) {
            deviceCountByOwnershipTypeDataWrapper = new LinkedHashMap<>();
            deviceCountByOwnershipTypeDataWrapper.put("group", entry.getKey());
            deviceCountByOwnershipTypeDataWrapper.put("label", entry.getKey());
            deviceCountByOwnershipTypeDataWrapper.put("count", entry.getValue());
            deviceCountsByOwnershipTypesDataWrapper.add(deviceCountByOwnershipTypeDataWrapper);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper2.setContext("device-Counts-by-ownership-types");
        dashboardGadgetDataWrapper2.setData(deviceCountsByOwnershipTypesDataWrapper);

        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

}
