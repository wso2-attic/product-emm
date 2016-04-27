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
import org.wso2.carbon.device.mgt.common.PaginationRequest;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.beans.DashboardGadgetDataWrapper;
import org.wso2.carbon.mdm.beans.DashboardPaginationGadgetDataWrapper;
import org.wso2.carbon.mdm.exception.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

@Produces({"application/json"})
@Consumes({"application/json"})

public class Dashboard {

    @SuppressWarnings("unused")
    private static Log log = LogFactory.getLog(Dashboard.class);

    @GET
    @Path("devices-overview")
    public Response getOverviewDeviceCounts() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating TotalDeviceCount Data Wrapper
        int totalDeviceCount = gadgetDataService.getTotalDeviceCount();
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
        int inactiveDeviceCount = gadgetDataService.getInactiveDeviceCount();
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
    public Response getNonCompliantDeviceCountsByFeatures(@QueryParam("start-index") int startIndex, @QueryParam("count") int count) {
        Message message = new Message();
        if (startIndex < 0) {
            message.setErrorMessage("Invalid start index.");
            message.setDescription("Start index cannot be less than 0.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        }

        if (count < 5) {
            message.setErrorMessage("Invalid request count.");
            message.setDescription("Row count should be more than 5.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        }

        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardPaginationGadgetDataWrapper dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();

        PaginationResult paginationResult = gadgetDataService.
            getNonCompliantDeviceCountsByFeatures(new PaginationRequest(startIndex, count));

        if (paginationResult == null) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }

        Map<String, Object> nonCompliantDeviceCountByFeatureDataWrapper;
        List<Map<String, Object>> nonCompliantDeviceCountsByFeaturesDataWrapper = new ArrayList<>();
        for (Object listElement : paginationResult.getData()) {
            Map entry = (Map<?, ?>) listElement;
            nonCompliantDeviceCountByFeatureDataWrapper = new LinkedHashMap<>();
            nonCompliantDeviceCountByFeatureDataWrapper.put("group", entry.get("FEATURE_CODE"));
            nonCompliantDeviceCountByFeatureDataWrapper.put("label", entry.get("FEATURE_CODE"));
            nonCompliantDeviceCountByFeatureDataWrapper.put("count", entry.get("DEVICE_COUNT"));
            nonCompliantDeviceCountsByFeaturesDataWrapper.add(nonCompliantDeviceCountByFeatureDataWrapper);
        }

        dashboardPaginationGadgetDataWrapper.setContext("non-compliant-by-feature");
        dashboardPaginationGadgetDataWrapper.setData(nonCompliantDeviceCountsByFeaturesDataWrapper);
        dashboardPaginationGadgetDataWrapper.setTotalRecordsCount(paginationResult.getRecordsTotal());

        List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardPaginationGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("device-groupings")
    public Response getDeviceGroupingCounts(@QueryParam("connectivity-status") String connectivityStatus,
                                            @QueryParam("is-compliant") String isCompliant,
                                            @QueryParam("is-monitored") String isMonitored,
                                            @QueryParam("platform") String platform,
                                            @QueryParam("ownership") String ownership) {

        Map<String, Object> filters = new LinkedHashMap<>();
        if (connectivityStatus != null &&
            ("ACTIVE".equals(connectivityStatus) ||
                "INACTIVE".equals(connectivityStatus) ||
                    "REMOVED".equals(connectivityStatus))) {
            filters.put("CONNECTIVITY_STATUS", connectivityStatus);
        }

        if (isCompliant != null && "false".equals(isCompliant)) {
            filters.put("IS_COMPLIANT", 0);
        }

        if (isMonitored != null && "false".equals(isMonitored)) {
            filters.put("POLICY_ID", -1);
        }

        if (platform != null &&
            ("android".equals(platform) ||
                "ios".equals(platform) ||
                    "windows".equals(platform))) {
            filters.put("PLATFORM", platform);
        }

        if (ownership != null &&
                ("BYOD".equals(ownership) ||
                        "COPE".equals(ownership))) {
            filters.put("OWNERSHIP", ownership);
        }

        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // creating device-Counts-by-platforms Data Wrapper
        Map<String, Integer> deviceCountsByPlatforms = gadgetDataService.getDeviceCountsByPlatforms(filters);
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

        // creating device-Counts-by-ownership-types Data Wrapper
        Map<String, Integer> deviceCountsByOwnershipTypes = gadgetDataService.getDeviceCountsByOwnershipTypes(filters);
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

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("filtered-devices-over-total")
    public Response getFilteredDeviceCountOverTotal(@QueryParam("connectivity-status") String connectivityStatus,
                                                    @QueryParam("is-compliant") String isCompliant,
                                                    @QueryParam("is-monitored") String isMonitored,
                                                    @QueryParam("platform") String platform,
                                                    @QueryParam("ownership") String ownership) {

        Map<String, Object> filters = new LinkedHashMap<>();
        if (connectivityStatus != null &&
                ("ACTIVE".equals(connectivityStatus) ||
                        "INACTIVE".equals(connectivityStatus) ||
                        "REMOVED".equals(connectivityStatus))) {
            filters.put("CONNECTIVITY_STATUS", connectivityStatus);
        }

        if (isCompliant != null && "false".equals(isCompliant)) {
            filters.put("IS_COMPLIANT", 0);
        }

        if (isMonitored != null && "false".equals(isMonitored)) {
            filters.put("POLICY_ID", -1);
        }

        if (platform != null &&
                ("android".equals(platform) ||
                        "ios".equals(platform) ||
                        "windows".equals(platform))) {
            filters.put("PLATFORM", platform);
        }

        if (ownership != null &&
                ("BYOD".equals(ownership) ||
                        "COPE".equals(ownership))) {
            filters.put("OWNERSHIP", ownership);
        }

        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating filteredDeviceCount Data Wrapper
        int filteredDeviceCount = gadgetDataService.getDeviceCount(filters);
        if (filteredDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> filteredDeviceCountDataWrapper = new LinkedHashMap<>();
        filteredDeviceCountDataWrapper.put("group", "filtered");
        filteredDeviceCountDataWrapper.put("label", "filtered");
        filteredDeviceCountDataWrapper.put("count", filteredDeviceCount);

        // creating TotalDeviceCount Data Wrapper
        int totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        if (totalDeviceCount == -1) {
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).build();
        }
        Map<String, Object> totalDeviceCountDataWrapper = new LinkedHashMap<>();
        totalDeviceCountDataWrapper.put("group", "total");
        totalDeviceCountDataWrapper.put("label", "Total");
        totalDeviceCountDataWrapper.put("count", totalDeviceCount);

        List<Map<String, Object>> filteredDeviceCountOverTotalDataWrapper = new ArrayList<>();
        filteredDeviceCountOverTotalDataWrapper.add(filteredDeviceCountDataWrapper);
        filteredDeviceCountOverTotalDataWrapper.add(totalDeviceCountDataWrapper);

        dashboardGadgetDataWrapper.setContext("filtered-device-count-over-total");
        dashboardGadgetDataWrapper.setData(filteredDeviceCountOverTotalDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

}
