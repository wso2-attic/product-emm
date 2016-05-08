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
import org.wso2.carbon.device.mgt.analytics.dashboard.dao.bean.DetailedDeviceEntry;
import org.wso2.carbon.device.mgt.analytics.dashboard.dao.bean.DeviceCountByGroupEntry;
import org.wso2.carbon.device.mgt.analytics.dashboard.dao.bean.FilterSet;
import org.wso2.carbon.device.mgt.analytics.dashboard.dao.exception.InvalidParameterValueException;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.beans.DashboardGadgetDataWrapper;
import org.wso2.carbon.mdm.beans.DashboardPaginationGadgetDataWrapper;
import org.wso2.carbon.mdm.exception.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Produces({"application/json"})
@Consumes({"application/json"})

public class Dashboard {

    private static Log log = LogFactory.getLog(Dashboard.class);

    // Constants related to Dashboard filtering
    public static final String CONNECTIVITY_STATUS = "connectivity-status";
    public static final String POTENTIAL_VULNERABILITY = "potential-vulnerability";
    public static final String NON_COMPLIANT_FEATURE_CODE = "non-compliant-feature-code";
    public static final String PLATFORM = "platform";
    public static final String OWNERSHIP_TYPE = "ownership";
    // Constants related to pagination
    public static final String PAGINATION_ENABLED = "pagination-enabled";
    public static final String START_INDEX = "start-index";
    public static final String RESULT_COUNT = "result-count";

    @GET
    @Path("device-count-overview")
    public Response getOverviewDeviceCounts() throws MDMAPIException {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();

        // creating TotalDeviceCount Data Wrapper
        DeviceCountByGroupEntry totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve total device count.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        List<Object> overviewDeviceCountsDataWrapper = new ArrayList<>();
        overviewDeviceCountsDataWrapper.add(totalDeviceCount);

        List<DeviceCountByGroupEntry> deviceCountsByConnectivityStatuses;
        try {
            deviceCountsByConnectivityStatuses = gadgetDataService.getDeviceCountsByConnectivityStatuses();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve device counts by connectivity statuses.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        for (DeviceCountByGroupEntry entry : deviceCountsByConnectivityStatuses) {
            overviewDeviceCountsDataWrapper.add(entry);
        }

        dashboardGadgetDataWrapper.setContext("Overview-of-device-counts");
        dashboardGadgetDataWrapper.setFilteringAttribute(CONNECTIVITY_STATUS);
        dashboardGadgetDataWrapper.setData(overviewDeviceCountsDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("device-counts-by-potential-vulnerabilities")
    public Response getDeviceCountsByPotentialVulnerabilities() throws MDMAPIException {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        List<DeviceCountByGroupEntry> deviceCountsByPotentialVulnerabilities;
        try {
            deviceCountsByPotentialVulnerabilities = gadgetDataService.getDeviceCountsByPotentialVulnerabilities();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve device counts by potential vulnerabilities.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("Device-counts-by-potential-vulnerabilities");
        dashboardGadgetDataWrapper.setFilteringAttribute(POTENTIAL_VULNERABILITY);
        dashboardGadgetDataWrapper.setData(deviceCountsByPotentialVulnerabilities);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("non-compliant-device-counts-by-features")
    public Response getNonCompliantDeviceCountsByFeatures(@QueryParam(START_INDEX) int startIndex,
                                                  @QueryParam(RESULT_COUNT) int resultCount) throws MDMAPIException {

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
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a non-compliant set of device counts by features.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a non-compliant set of device counts by features.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        dashboardPaginationGadgetDataWrapper.setContext("Non-compliant-device-counts-by-feature");
        dashboardPaginationGadgetDataWrapper.setFilteringAttribute(NON_COMPLIANT_FEATURE_CODE);
        dashboardPaginationGadgetDataWrapper.setData(paginationResult.getData());
        dashboardPaginationGadgetDataWrapper.setTotalRecordCount(paginationResult.getRecordsTotal());

        List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardPaginationGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("device-counts-by-groups")
    public Response getDeviceCountsByGroups(@QueryParam(CONNECTIVITY_STATUS) String connectivityStatus,
                                            @QueryParam(POTENTIAL_VULNERABILITY) String potentialVulnerability,
                                            @QueryParam(PLATFORM) String platform,
                                            @QueryParam(OWNERSHIP_TYPE) String ownership) throws MDMAPIException {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setConnectivityStatus(connectivityStatus);
        filterSet.setPotentialVulnerability(potentialVulnerability);
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating device-Counts-by-platforms Data Wrapper
        List<DeviceCountByGroupEntry> deviceCountsByPlatforms;
        try {
            deviceCountsByPlatforms = gadgetDataService.getDeviceCountsByPlatforms(filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a filtered set of device counts by platforms.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by platforms.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper1.setContext("Device-counts-by-platforms");
        dashboardGadgetDataWrapper1.setFilteringAttribute(PLATFORM);
        dashboardGadgetDataWrapper1.setData(deviceCountsByPlatforms);

        // creating device-Counts-by-ownership-types Data Wrapper
        List<DeviceCountByGroupEntry> deviceCountsByOwnershipTypes;
        try {
            deviceCountsByOwnershipTypes = gadgetDataService.getDeviceCountsByOwnershipTypes(filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a filtered set of device counts by ownership types.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by ownership types.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper2.setContext("Device-counts-by-ownership-type");
        dashboardGadgetDataWrapper2.setFilteringAttribute(OWNERSHIP_TYPE);
        dashboardGadgetDataWrapper2.setData(deviceCountsByOwnershipTypes);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("feature-non-compliant-device-counts-by-groups")
    public Response getFeatureNonCompliantDeviceCountsByGroups(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                               @QueryParam(PLATFORM) String platform,
                                                               @QueryParam(OWNERSHIP_TYPE) String ownership)
                                                               throws MDMAPIException {
        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating feature-non-compliant-device-Counts-by-platforms Data Wrapper
        List<DeviceCountByGroupEntry> featureNonCompliantDeviceCountsByPlatforms;
        try {
            featureNonCompliantDeviceCountsByPlatforms = gadgetDataService.
                getFeatureNonCompliantDeviceCountsByPlatforms(nonCompliantFeatureCode, filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a filtered set of " +
                    "feature non-compliant device counts by platforms.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant device counts by platforms.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper1.setContext("Feature-non-compliant-device-counts-by-platforms");
        dashboardGadgetDataWrapper1.setFilteringAttribute(PLATFORM);
        dashboardGadgetDataWrapper1.setData(featureNonCompliantDeviceCountsByPlatforms);

        // creating feature-non-compliant-device-Counts-by-ownership-types Data Wrapper
        List<DeviceCountByGroupEntry> featureNonCompliantDeviceCountsByOwnershipTypes;
        try {
            featureNonCompliantDeviceCountsByOwnershipTypes = gadgetDataService.
                getFeatureNonCompliantDeviceCountsByOwnershipTypes(nonCompliantFeatureCode, filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + "This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a filtered set of " +
                    "feature non-compliant device counts by ownership types.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant " +
                    "device counts by ownership types.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper2.setContext("Feature-non-compliant-device-counts-by-ownership-types");
        dashboardGadgetDataWrapper2.setFilteringAttribute(OWNERSHIP_TYPE);
        dashboardGadgetDataWrapper2.setData(featureNonCompliantDeviceCountsByOwnershipTypes);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("filtered-device-count-over-total")
    public Response getFilteredDeviceCountOverTotal(@QueryParam(CONNECTIVITY_STATUS) String connectivityStatus,
                                                    @QueryParam(POTENTIAL_VULNERABILITY) String potentialVulnerability,
                                                    @QueryParam(PLATFORM) String platform,
                                                    @QueryParam(OWNERSHIP_TYPE) String ownership)
                                                    throws MDMAPIException {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setConnectivityStatus(connectivityStatus);
        filterSet.setPotentialVulnerability(potentialVulnerability);
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating filteredDeviceCount Data Wrapper
        DeviceCountByGroupEntry filteredDeviceCount;
        try {
            filteredDeviceCount = gadgetDataService.getDeviceCount(filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a filtered device count over the total.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered device count over the total.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        // creating TotalDeviceCount Data Wrapper
        DeviceCountByGroupEntry totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve the total device count over filtered.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        List<Object> filteredDeviceCountOverTotalDataWrapper = new ArrayList<>();
        filteredDeviceCountOverTotalDataWrapper.add(filteredDeviceCount);
        filteredDeviceCountOverTotalDataWrapper.add(totalDeviceCount);

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("Filtered-device-count-over-total");
        dashboardGadgetDataWrapper.setFilteringAttribute(null);
        dashboardGadgetDataWrapper.setData(filteredDeviceCountOverTotalDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("feature-non-compliant-device-count-over-total")
    public Response getFeatureNonCompliantDeviceCountOverTotal(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                               @QueryParam(PLATFORM) String platform,
                                                               @QueryParam(OWNERSHIP_TYPE) String ownership)
                                                               throws MDMAPIException {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        FilterSet filterSet = new FilterSet();
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating featureNonCompliantDeviceCount Data Wrapper
        DeviceCountByGroupEntry featureNonCompliantDeviceCount;
        try {
            featureNonCompliantDeviceCount = gadgetDataService.
                getFeatureNonCompliantDeviceCount(nonCompliantFeatureCode, filterSet);
        } catch (InvalidParameterValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
            Message message = new Message();
            message.setErrorMessage("Invalid query parameter value.");
            message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                "function @ Dashboard API layer to retrieve a feature non-compliant device count over the total.");
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a feature non-compliant device count over the total.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        // creating TotalDeviceCount Data Wrapper
        DeviceCountByGroupEntry totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (SQLException e) {
            String msg = "An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve the total device count over filtered feature non-compliant.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }

        List<Object> featureNonCompliantDeviceCountOverTotalDataWrapper = new ArrayList<>();
        featureNonCompliantDeviceCountOverTotalDataWrapper.add(featureNonCompliantDeviceCount);
        featureNonCompliantDeviceCountOverTotalDataWrapper.add(totalDeviceCount);

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("Feature-non-compliant-device-count-over-total");
        dashboardGadgetDataWrapper.setFilteringAttribute(null);
        dashboardGadgetDataWrapper.setData(featureNonCompliantDeviceCountOverTotalDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("devices-with-details")
    public Response getDevicesWithDetails(@QueryParam(CONNECTIVITY_STATUS) String connectivityStatus,
                                          @QueryParam(POTENTIAL_VULNERABILITY) String potentialVulnerability,
                                          @QueryParam(PLATFORM) String platform,
                                          @QueryParam(OWNERSHIP_TYPE) String ownership,
                                          @QueryParam(PAGINATION_ENABLED) String paginationEnabled,
                                          @QueryParam(START_INDEX) int startIndex,
                                          @QueryParam(RESULT_COUNT) int resultCount) throws MDMAPIException {

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
                message.setErrorMessage("Invalid query parameter value.");
                message.setDescription(e.getErrorMessage() + "This was while trying to execute relevant data service " +
                    "function @ Dashboard API layer to retrieve a filtered set of devices with details.");
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            DashboardPaginationGadgetDataWrapper
                    dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();
            dashboardPaginationGadgetDataWrapper.setContext("Filtered-and-paginated-devices-with-details");
            dashboardPaginationGadgetDataWrapper.setFilteringAttribute(null);
            dashboardPaginationGadgetDataWrapper.setData(paginationResult.getData());
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

            List<DetailedDeviceEntry> devicesWithDetails;
            try {
                devicesWithDetails = gadgetDataService.getDevicesWithDetails(filterSet);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Invalid query parameter value.");
                message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant data service " +
                    "function @ Dashboard API layer to retrieve a filtered set of devices with details.");
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
            dashboardGadgetDataWrapper.setContext("filtered-devices-with-details");
            dashboardGadgetDataWrapper.setFilteringAttribute(null);
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
    public Response getFeatureNonCompliantDevicesWithDetails(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                             @QueryParam(PLATFORM) String platform,
                                                             @QueryParam(OWNERSHIP_TYPE) String ownership,
                                                             @QueryParam(PAGINATION_ENABLED) String paginationEnabled,
                                                             @QueryParam(START_INDEX) int startIndex,
                                                             @QueryParam(RESULT_COUNT) int resultCount)
                                                             throws MDMAPIException {
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
                    getFeatureNonCompliantDevicesWithDetails(nonCompliantFeatureCode, filterSet, startIndex, resultCount);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Invalid query parameter value.");
                message.setDescription(e.getErrorMessage() + " This was while trying to execute relevant service layer " +
                    "function @ Dashboard API layer to retrieve a filtered set of " +
                        "feature non-compliant devices with details.");
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature non-compliant devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            DashboardPaginationGadgetDataWrapper
                    dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();
            dashboardPaginationGadgetDataWrapper.setContext("Paginated-feature-non-compliant-devices-with-details");
            dashboardPaginationGadgetDataWrapper.setFilteringAttribute(null);
            dashboardPaginationGadgetDataWrapper.setData(paginationResult.getData());
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

            List<DetailedDeviceEntry> featureNonCompliantDevicesWithDetails;
            try {
                featureNonCompliantDevicesWithDetails = gadgetDataService.
                    getFeatureNonCompliantDevicesWithDetails(nonCompliantFeatureCode, filterSet);
            } catch (InvalidParameterValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid parameter value.", e);
                Message message = new Message();
                message.setErrorMessage("Invalid query parameter value.");
                message.setDescription(e.getErrorMessage() + "This was while trying to execute relevant data service " +
                    "function @ Dashboard API layer to retrieve a filtered set of " +
                        "feature non-compliant devices with details.");
                return Response.status(HttpStatus.SC_BAD_REQUEST).entity(message).build();
            } catch (SQLException e) {
                String msg = "An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature " +
                        "non-compliant set of devices with details.";
                log.error(msg, e);
                throw new MDMAPIException(msg, e);
            }

            DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
            dashboardGadgetDataWrapper.setContext("Feature-non-compliant-devices-with-details");
            dashboardGadgetDataWrapper.setFilteringAttribute(null);
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
