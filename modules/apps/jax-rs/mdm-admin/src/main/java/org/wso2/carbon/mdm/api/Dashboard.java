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
import org.wso2.carbon.device.mgt.analytics.dashboard.dao.bean.FilterSet;
import org.wso2.carbon.device.mgt.analytics.dashboard.dao.exception.InvalidParameterValueException;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.beans.DashboardGadgetDataWrapper;
import org.wso2.carbon.mdm.beans.DashboardPaginationGadgetDataWrapper;
import org.wso2.carbon.mdm.exception.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Produces({"application/json"})
@Consumes({"application/json"})

public class Dashboard {

    private static Log log = LogFactory.getLog(Dashboard.class);

    @GET
    @Path("overview-of-devices")
    public Response getOverviewDeviceCounts() throws MDMAPIException {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating TotalDeviceCount Data Wrapper
        int totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve total device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> totalDeviceCountDataWrapper = new LinkedHashMap<>();
        totalDeviceCountDataWrapper.put("group", "total");
        totalDeviceCountDataWrapper.put("label", "Total");
        totalDeviceCountDataWrapper.put("count", totalDeviceCount);

        // creating ActiveDeviceCount Data Wrapper
        int activeDeviceCount;
        try {
            activeDeviceCount = gadgetDataService.getActiveDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve active device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> activeDeviceCountDataWrapper = new LinkedHashMap<>();
        activeDeviceCountDataWrapper.put("group", "active");
        activeDeviceCountDataWrapper.put("label", "Active");
        activeDeviceCountDataWrapper.put("count", activeDeviceCount);

        // creating inactiveDeviceCount Data Wrapper
        int inactiveDeviceCount;
        try {
            inactiveDeviceCount = gadgetDataService.getInactiveDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve inactive device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> inactiveDeviceCountDataWrapper = new LinkedHashMap<>();
        inactiveDeviceCountDataWrapper.put("group", "inactive");
        inactiveDeviceCountDataWrapper.put("label", "Inactive");
        inactiveDeviceCountDataWrapper.put("count", inactiveDeviceCount);

        // creating removedDeviceCount Data Wrapper
        int removedDeviceCount;
        try {
            removedDeviceCount = gadgetDataService.getRemovedDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve removed device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
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

        dashboardGadgetDataWrapper.setContext("connectivity-status");
        dashboardGadgetDataWrapper.setData(overviewDeviceCountsDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("potential-vulnerabilities")
    public Response getVulnerableDeviceCounts() throws MDMAPIException {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating non-compliant Data Wrapper
        int nonCompliantDeviceCount;
        try {
            nonCompliantDeviceCount = gadgetDataService.getNonCompliantDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve non-compliant device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> nonCompliantDeviceCountDataWrapper = new LinkedHashMap<>();
        nonCompliantDeviceCountDataWrapper.put("group", "non-compliant");
        nonCompliantDeviceCountDataWrapper.put("label", "Non-Compliant");
        nonCompliantDeviceCountDataWrapper.put("count", nonCompliantDeviceCount);

        // creating unmonitoredDeviceCount Data Wrapper
        int unmonitoredDeviceCount;
        try {
            unmonitoredDeviceCount = gadgetDataService.getUnmonitoredDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve unmonitored device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> unmonitoredDeviceCountDataWrapper = new LinkedHashMap<>();
        unmonitoredDeviceCountDataWrapper.put("group", "unmonitored");
        unmonitoredDeviceCountDataWrapper.put("label", "Unmonitored");
        unmonitoredDeviceCountDataWrapper.put("count", unmonitoredDeviceCount);

        List<Map<String, Object>> vulnerableDeviceCountsDataWrapper = new ArrayList<>();
        vulnerableDeviceCountsDataWrapper.add(nonCompliantDeviceCountDataWrapper);
        vulnerableDeviceCountsDataWrapper.add(unmonitoredDeviceCountDataWrapper);

        dashboardGadgetDataWrapper.setContext("potential-vulnerability");
        dashboardGadgetDataWrapper.setData(vulnerableDeviceCountsDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("non-compliant-by-feature")
    public Response getNonCompliantDeviceCountsByFeatures(@QueryParam("start-index") int startIndex,
                        @QueryParam("result-count") int resultCount, @Context UriInfo uriInfo) throws MDMAPIException {

        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardPaginationGadgetDataWrapper
            dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();

        PaginationResult paginationResult;
        try {
            paginationResult = gadgetDataService.
                getNonCompliantDeviceCountsByFeatures(startIndex, resultCount);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a non-compliant set of device counts by features. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a non-compliant set of device counts by features.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
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

        dashboardPaginationGadgetDataWrapper.setContext("non-compliant-feature");
        dashboardPaginationGadgetDataWrapper.setData(nonCompliantDeviceCountsByFeaturesDataWrapper);
        dashboardPaginationGadgetDataWrapper.setTotalRecordCount(paginationResult.getRecordsTotal());

        List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardPaginationGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("device-groupings")
    public Response getDeviceGroupingCounts(@QueryParam("connectivity-status") String connectivityStatus,
                                            @QueryParam("potential-vulnerability") String potentialVulnerability,
                                            @QueryParam("platform") String platform,
                                            @QueryParam("ownership") String ownership) throws MDMAPIException {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setConnectivityStatus(connectivityStatus);
        filterSet.setPotentialVulnerability(potentialVulnerability);
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating device-Counts-by-platforms Data Wrapper
        Map<String, Integer> deviceCountsByPlatforms;
        try {
            deviceCountsByPlatforms = gadgetDataService.getDeviceCountsByPlatforms(filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameters.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by platforms. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by platforms.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
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
        dashboardGadgetDataWrapper1.setContext("platform");
        dashboardGadgetDataWrapper1.setData(deviceCountsByPlatformsDataWrapper);

        // creating device-Counts-by-ownership-types Data Wrapper
        Map<String, Integer> deviceCountsByOwnershipTypes;
        try {
            deviceCountsByOwnershipTypes = gadgetDataService.getDeviceCountsByOwnershipTypes(filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a filtered set of device " +
                    "counts by ownership types. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by ownership types.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
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
        dashboardGadgetDataWrapper2.setContext("ownership");
        dashboardGadgetDataWrapper2.setData(deviceCountsByOwnershipTypesDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("feature-non-compliant-device-groupings")
    public Response getFeatureNonCompliantDeviceGroupingCounts(@QueryParam("non-compliant-feature") String nonCompliantFeature,
                                                               @QueryParam("platform") String platform,
                                                               @QueryParam("ownership") String ownership)
                                                               throws MDMAPIException {
        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating feature-non-compliant-device-Counts-by-platforms Data Wrapper
        Map<String, Integer> featureNonCompliantDeviceCountsByPlatforms;
        try {
            featureNonCompliantDeviceCountsByPlatforms = gadgetDataService.
                getFeatureNonCompliantDeviceCountsByPlatforms(nonCompliantFeature, filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant device " +
                    "counts by platforms. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant device counts by platforms.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> featureNonCompliantDeviceCountByPlatformDataWrapper;
        List<Map<String, Object>> featureNonCompliantDeviceCountsByPlatformsDataWrapper = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : featureNonCompliantDeviceCountsByPlatforms.entrySet()) {
            featureNonCompliantDeviceCountByPlatformDataWrapper = new LinkedHashMap<>();
            featureNonCompliantDeviceCountByPlatformDataWrapper.put("group", entry.getKey());
            featureNonCompliantDeviceCountByPlatformDataWrapper.put("label", entry.getKey());
            featureNonCompliantDeviceCountByPlatformDataWrapper.put("count", entry.getValue());
            featureNonCompliantDeviceCountsByPlatformsDataWrapper.
                add(featureNonCompliantDeviceCountByPlatformDataWrapper);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper1.setContext("platform");
        dashboardGadgetDataWrapper1.setData(featureNonCompliantDeviceCountsByPlatformsDataWrapper);

        // creating feature-non-compliant-device-Counts-by-ownership-types Data Wrapper
        Map<String, Integer> featureNonCompliantDeviceCountsByOwnershipTypes;
        try {
            featureNonCompliantDeviceCountsByOwnershipTypes = gadgetDataService.
                getFeatureNonCompliantDeviceCountsByOwnershipTypes(nonCompliantFeature, filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant device " +
                    "counts by ownership types. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant " +
                    "device counts by ownership types.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> featureNonCompliantDeviceCountByOwnershipTypeDataWrapper;
        List<Map<String, Object>> featureNonCompliantDeviceCountsByOwnershipTypesDataWrapper = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : featureNonCompliantDeviceCountsByOwnershipTypes.entrySet()) {
            featureNonCompliantDeviceCountByOwnershipTypeDataWrapper = new LinkedHashMap<>();
            featureNonCompliantDeviceCountByOwnershipTypeDataWrapper.put("group", entry.getKey());
            featureNonCompliantDeviceCountByOwnershipTypeDataWrapper.put("label", entry.getKey());
            featureNonCompliantDeviceCountByOwnershipTypeDataWrapper.put("count", entry.getValue());
            featureNonCompliantDeviceCountsByOwnershipTypesDataWrapper.
                add(featureNonCompliantDeviceCountByOwnershipTypeDataWrapper);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper2.setContext("ownership");
        dashboardGadgetDataWrapper2.setData(featureNonCompliantDeviceCountsByOwnershipTypesDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("filtered-devices-over-total")
    public Response getFilteredDeviceCountOverTotal(@QueryParam("connectivity-status") String connectivityStatus,
                                                    @QueryParam("potential-vulnerability") String potentialVulnerability,
                                                    @QueryParam("platform") String platform,
                                                    @QueryParam("ownership") String ownership) throws MDMAPIException {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setConnectivityStatus(connectivityStatus);
        filterSet.setPotentialVulnerability(potentialVulnerability);
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating filteredDeviceCount Data Wrapper
        int filteredDeviceCount;
        try {
            filteredDeviceCount = gadgetDataService.getDeviceCount(filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a filtered device count over the total. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a filtered device count over the total.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> filteredDeviceCountDataWrapper = new LinkedHashMap<>();
        filteredDeviceCountDataWrapper.put("group", "filtered");
        filteredDeviceCountDataWrapper.put("label", "filtered");
        filteredDeviceCountDataWrapper.put("count", filteredDeviceCount);

        // creating TotalDeviceCount Data Wrapper
        int totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve the total device count over filtered.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> totalDeviceCountDataWrapper = new LinkedHashMap<>();
        totalDeviceCountDataWrapper.put("group", "total");
        totalDeviceCountDataWrapper.put("label", "Total");
        totalDeviceCountDataWrapper.put("count", totalDeviceCount);

        List<Map<String, Object>> filteredDeviceCountOverTotalDataWrapper = new ArrayList<>();
        filteredDeviceCountOverTotalDataWrapper.add(filteredDeviceCountDataWrapper);
        filteredDeviceCountOverTotalDataWrapper.add(totalDeviceCountDataWrapper);

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("filtered-device-count-over-total");
        dashboardGadgetDataWrapper.setData(filteredDeviceCountOverTotalDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("feature-non-compliant-devices-over-total")
    public Response getFeatureNonCompliantDeviceCountOverTotal(@QueryParam("non-compliant-feature") String nonCompliantFeature,
                                                               @QueryParam("platform") String platform,
                                                               @QueryParam("ownership") String ownership)
                                                               throws MDMAPIException {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating featureNonCompliantDeviceCount Data Wrapper
        int featureNonCompliantDeviceCount;
        try {
            featureNonCompliantDeviceCount = gadgetDataService.
                getFeatureNonCompliantDeviceCount(nonCompliantFeature, filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
            message.setDescription("This was while trying to execute relevant service layer function " +
                "@ Dashboard API layer to retrieve a feature non-compliant " +
                    "device count over the total. " + e.getErrorMessage());
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve a feature non-compliant device count over the total.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> featureNonCompliantDeviceCountDataWrapper = new LinkedHashMap<>();
        featureNonCompliantDeviceCountDataWrapper.put("group", "filtered");
        featureNonCompliantDeviceCountDataWrapper.put("label", "filtered");
        featureNonCompliantDeviceCountDataWrapper.put("count", featureNonCompliantDeviceCount);

        // creating TotalDeviceCount Data Wrapper
        int totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (SQLException e) {
            String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                "@ Dashboard API layer to retrieve the total device count over filtered feature non-compliant.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        Map<String, Object> totalDeviceCountDataWrapper = new LinkedHashMap<>();
        totalDeviceCountDataWrapper.put("group", "total");
        totalDeviceCountDataWrapper.put("label", "Total");
        totalDeviceCountDataWrapper.put("count", totalDeviceCount);

        List<Map<String, Object>> featureNonCompliantDeviceCountOverTotalDataWrapper = new ArrayList<>();
        featureNonCompliantDeviceCountOverTotalDataWrapper.add(featureNonCompliantDeviceCountDataWrapper);
        featureNonCompliantDeviceCountOverTotalDataWrapper.add(totalDeviceCountDataWrapper);

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("feature-non-compliant-device-count-over-total");
        dashboardGadgetDataWrapper.setData(featureNonCompliantDeviceCountOverTotalDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("filtered-devices-with-details")
    public Response getFilteredDevicesWithDetails(@QueryParam("connectivity-status") String connectivityStatus,
                                                  @QueryParam("potential-vulnerability") String potentialVulnerability,
                                                  @QueryParam("platform") String platform,
                                                  @QueryParam("ownership") String ownership,
                                                  @QueryParam("pagination-enabled") String paginationEnabled,
                                                  @QueryParam("start-index") int startIndex,
                                                  @QueryParam("result-count") int resultCount) throws MDMAPIException {

        if (paginationEnabled == null) {

            Message message = new Message();
            message.setErrorMessage("Missing required query parameter.");
            message.setDescription("Pagination-enabled query parameter with value true or false is required.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();

        } else if ("true".equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            FilterSet filterSet = new FilterSet();
            filterSet.setConnectivityStatus(connectivityStatus);
            filterSet.setPotentialVulnerability(potentialVulnerability);
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            PaginationResult paginationResult;
            try {
                paginationResult = gadgetDataService.
                    getDevicesWithDetails(filterSet, startIndex, resultCount);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
                message.setDescription("This was while trying to execute relevant service layer function " +
                    "@ Dashboard API layer to retrieve a filtered set of " +
                        "devices with details. " + e.getErrorMessage());
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                    "@ Dashboard API layer to retrieve a filtered set of devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            Map<String, Object> deviceDetailEntryDataWrapper;
            List<Map<String, Object>> deviceDetailEntriesDataWrapper = new ArrayList<>();
            for (Object listElement : paginationResult.getData()) {
                Map entry = (Map<?, ?>) listElement;
                deviceDetailEntryDataWrapper = new LinkedHashMap<>();
                deviceDetailEntryDataWrapper.put("device-id", entry.get("device-id"));
                deviceDetailEntryDataWrapper.put("platform", entry.get("platform"));
                deviceDetailEntryDataWrapper.put("ownership", entry.get("ownership"));
                deviceDetailEntryDataWrapper.put("connectivity-details", entry.get("connectivity-details"));
                deviceDetailEntriesDataWrapper.add(deviceDetailEntryDataWrapper);
            }

            DashboardPaginationGadgetDataWrapper
                    dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();
            dashboardPaginationGadgetDataWrapper.setContext("filtered-device-details");
            dashboardPaginationGadgetDataWrapper.setData(deviceDetailEntriesDataWrapper);
            dashboardPaginationGadgetDataWrapper.setTotalRecordCount(paginationResult.getRecordsTotal());

            List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardPaginationGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else if ("false".equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            FilterSet filterSet = new FilterSet();
            filterSet.setConnectivityStatus(connectivityStatus);
            filterSet.setPotentialVulnerability(potentialVulnerability);
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            List<Map<String, Object>> devicesWithDetails;
            try {
                devicesWithDetails = gadgetDataService.getDevicesWithDetails(filterSet);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
                message.setDescription("This was while trying to execute relevant service layer function " +
                    "@ Dashboard API layer to retrieve a filtered set of " +
                        "devices with details. " + e.getErrorMessage());
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                    "@ Dashboard API layer to retrieve a filtered set of devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
            dashboardGadgetDataWrapper.setContext("device-details");
            dashboardGadgetDataWrapper.setData(devicesWithDetails);

            List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else {

            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription("Invalid value for query parameter pagination-enabled. " +
                "Should be either true or false.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();

        }
    }

    @GET
    @Path("feature-non-compliant-devices-with-details")
    public Response getFeatureNonCompliantDevicesWithDetails(@QueryParam("non-compliant-feature") String nonCompliantFeature,
                                                             @QueryParam("platform") String platform,
                                                             @QueryParam("ownership") String ownership,
                                                             @QueryParam("pagination-enabled") String paginationEnabled,
                                                             @QueryParam("start-index") int startIndex,
                                                             @QueryParam("result-count") int resultCount) throws MDMAPIException {

        if (paginationEnabled == null) {

            Message message = new Message();
            message.setErrorMessage("Missing required query parameters.");
            message.setDescription("Query parameter pagination-enabled with value true or false is required.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();

        } else if ("true".equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            FilterSet filterSet = new FilterSet();
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            PaginationResult paginationResult;
            try {
                paginationResult = gadgetDataService.
                    getFeatureNonCompliantDevicesWithDetails(nonCompliantFeature, filterSet, startIndex, resultCount);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
                message.setDescription("This was while trying to execute relevant service layer function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature non-compliant " +
                        "devices with details. " + e.getErrorMessage());
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature non-compliant devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            Map<String, Object> featureNonCompliantDeviceDetailEntryDataWrapper;
            List<Map<String, Object>> featureNonCompliantDeviceDetailEntriesDataWrapper = new ArrayList<>();
            for (Object listElement : paginationResult.getData()) {
                Map entry = (Map<?, ?>) listElement;
                featureNonCompliantDeviceDetailEntryDataWrapper = new LinkedHashMap<>();
                featureNonCompliantDeviceDetailEntryDataWrapper.put("device-id", entry.get("device-id"));
                featureNonCompliantDeviceDetailEntryDataWrapper.put("platform", entry.get("platform"));
                featureNonCompliantDeviceDetailEntryDataWrapper.put("ownership", entry.get("ownership"));
                featureNonCompliantDeviceDetailEntryDataWrapper.put("connectivity-details", entry.get("connectivity-details"));
                featureNonCompliantDeviceDetailEntriesDataWrapper.add(featureNonCompliantDeviceDetailEntryDataWrapper);
            }

            DashboardPaginationGadgetDataWrapper
                    dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();
            dashboardPaginationGadgetDataWrapper.setContext("feature-non-compliant-device-details");
            dashboardPaginationGadgetDataWrapper.setData(featureNonCompliantDeviceDetailEntriesDataWrapper);
            dashboardPaginationGadgetDataWrapper.setTotalRecordCount(paginationResult.getRecordsTotal());

            List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardPaginationGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else if ("false".equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            FilterSet filterSet = new FilterSet();
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            List<Map<String, Object>> featureNonCompliantDevicesWithDetails;
            try {
                featureNonCompliantDevicesWithDetails = gadgetDataService.
                    getFeatureNonCompliantDevicesWithDetails(nonCompliantFeature, filterSet);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Error occurred @ Gadget Data Service layer due to invalid parameter value.");
                message.setDescription("This was while trying to execute relevant service layer function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature non-compliant " +
                        "devices with details. " + e.getErrorMessage());
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "SQL error occurred @ Gadget Data Service layer while trying to execute relevant function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature non-compliant set of devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
            dashboardGadgetDataWrapper.setContext("feature-non-compliant-device-details");
            dashboardGadgetDataWrapper.setData(featureNonCompliantDevicesWithDetails);

            List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else {

            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription("Invalid value for " +
                "query parameter pagination-enabled. Should be either true or false.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();

        }
    }

}
