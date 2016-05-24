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
import org.wso2.carbon.device.mgt.analytics.dashboard.bean.BasicFilterSet;
import org.wso2.carbon.device.mgt.analytics.dashboard.bean.DeviceWithDetails;
import org.wso2.carbon.device.mgt.analytics.dashboard.bean.DeviceCountByGroup;
import org.wso2.carbon.device.mgt.analytics.dashboard.bean.ExtendedFilterSet;
import org.wso2.carbon.device.mgt.analytics.dashboard.exception.*;
import org.wso2.carbon.device.mgt.common.PaginationResult;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.beans.DashboardGadgetDataWrapper;
import org.wso2.carbon.mdm.beans.DashboardPaginationGadgetDataWrapper;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * This class consists of dashboard related REST APIs
 * to be consumed by individual client gadgets such as
 * [1] Overview of Devices,
 * [2] Potential Vulnerabilities,
 * [3] Non-compliant Devices by Features,
 * [4] Device Groupings and etc.
 */

@Consumes({"application/json"})
@Produces({"application/json"})

@SuppressWarnings("NonJaxWsWebServices")
public class Dashboard {

    private static Log log = LogFactory.getLog(Dashboard.class);

    // Constants related to Dashboard filtering
    public static final String CONNECTIVITY_STATUS = "connectivity-status";
    public static final String POTENTIAL_VULNERABILITY = "potential-vulnerability";
    public static final String NON_COMPLIANT_FEATURE_CODE = "non-compliant-feature-code";
    public static final String PLATFORM = "platform";
    public static final String OWNERSHIP = "ownership";
    // Constants related to pagination
    public static final String PAGINATION_ENABLED = "pagination-enabled";
    public static final String START_INDEX = "start";
    public static final String RESULT_COUNT = "length";
    public static final String FLAG_TRUE = "true";
    public static final String FLAG_FALSE = "false";
    // Constants related to common error-response messages
    public static final String INVALID_QUERY_PARAM_VALUE_POTENTIAL_VULNERABILITY = "Received an invalid value for " +
        "query parameter : " + POTENTIAL_VULNERABILITY + ", Should be either NON_COMPLIANT or UNMONITORED.";
    public static final String INVALID_QUERY_PARAM_VALUE_START_INDEX = "Received an invalid value for " +
        "query parameter : " + START_INDEX + ", Should not be lesser than 0.";
    public static final String INVALID_QUERY_PARAM_VALUE_RESULT_COUNT = "Received an invalid value for " +
        "query parameter : " + RESULT_COUNT + ", Should not be lesser than 5.";
    public static final String INVALID_QUERY_PARAM_VALUE_PAGINATION_ENABLED = "Received an invalid value for " +
        "query parameter : " + PAGINATION_ENABLED + ", Should be either true or false.";
    public static final String REQUIRED_QUERY_PARAM_VALUE_NON_COMPLIANT_FEATURE_CODE = "Missing required query " +
        "parameter : " + NON_COMPLIANT_FEATURE_CODE;
    public static final String REQUIRED_QUERY_PARAM_VALUE_PAGINATION_ENABLED = "Missing required query " +
        "parameter : " + PAGINATION_ENABLED;
    public static final String ERROR_IN_RETRIEVING_REQUESTED_DATA = "Error in retrieving requested data.";

    @GET
    @Path("device-count-overview")
    public Response getOverviewDeviceCounts() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();

        // getting total device count
        DeviceCountByGroup totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve total device count.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        List<DeviceCountByGroup> totalDeviceCountInListEntry = new ArrayList<>();
        totalDeviceCountInListEntry.add(totalDeviceCount);

        dashboardGadgetDataWrapper1.setContext("Total-device-count");
        dashboardGadgetDataWrapper1.setGroupingAttribute(null);
        dashboardGadgetDataWrapper1.setData(totalDeviceCountInListEntry);

        // getting device counts by connectivity statuses
        List<DeviceCountByGroup> deviceCountsByConnectivityStatuses;
        try {
            deviceCountsByConnectivityStatuses = gadgetDataService.getDeviceCountsByConnectivityStatuses();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve device counts by connectivity statuses.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();

        dashboardGadgetDataWrapper2.setContext("Device-counts-by-connectivity-statuses");
        dashboardGadgetDataWrapper2.setGroupingAttribute(CONNECTIVITY_STATUS);
        dashboardGadgetDataWrapper2.setData(deviceCountsByConnectivityStatuses);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("device-counts-by-potential-vulnerabilities")
    public Response getDeviceCountsByPotentialVulnerabilities() {
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        List<DeviceCountByGroup> deviceCountsByPotentialVulnerabilities;
        try {
            deviceCountsByPotentialVulnerabilities = gadgetDataService.getDeviceCountsByPotentialVulnerabilities();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve device counts by potential vulnerabilities.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("Device-counts-by-potential-vulnerabilities");
        dashboardGadgetDataWrapper.setGroupingAttribute(POTENTIAL_VULNERABILITY);
        dashboardGadgetDataWrapper.setData(deviceCountsByPotentialVulnerabilities);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("non-compliant-device-counts-by-features")
    public Response getNonCompliantDeviceCountsByFeatures(@QueryParam(START_INDEX) int startIndex,
                                                          @QueryParam(RESULT_COUNT) int resultCount) {

        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();
        DashboardPaginationGadgetDataWrapper
            dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();

        PaginationResult paginationResult;
        try {
            paginationResult = gadgetDataService.
                getNonCompliantDeviceCountsByFeatures(startIndex, resultCount);
        } catch (InvalidStartIndexValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a non-compliant set of device counts by features.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(INVALID_QUERY_PARAM_VALUE_START_INDEX).build();
        } catch (InvalidResultCountValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a non-compliant set of device counts by features.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).entity(INVALID_QUERY_PARAM_VALUE_RESULT_COUNT).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a non-compliant set of device counts by features.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        dashboardPaginationGadgetDataWrapper.setContext("Non-compliant-device-counts-by-features");
        dashboardPaginationGadgetDataWrapper.setGroupingAttribute(NON_COMPLIANT_FEATURE_CODE);
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
                                            @QueryParam(OWNERSHIP) String ownership) {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        ExtendedFilterSet filterSet = new ExtendedFilterSet();
        filterSet.setConnectivityStatus(connectivityStatus);
        filterSet.setPotentialVulnerability(potentialVulnerability);
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating device-Counts-by-platforms Data Wrapper
        List<DeviceCountByGroup> deviceCountsByPlatforms;
        try {
            deviceCountsByPlatforms = gadgetDataService.getDeviceCountsByPlatforms(filterSet);
        } catch (InvalidPotentialVulnerabilityValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a filtered set of device counts by platforms.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(INVALID_QUERY_PARAM_VALUE_POTENTIAL_VULNERABILITY).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by platforms.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper1.setContext("Device-counts-by-platforms");
        dashboardGadgetDataWrapper1.setGroupingAttribute(PLATFORM);
        dashboardGadgetDataWrapper1.setData(deviceCountsByPlatforms);

        // creating device-Counts-by-ownership-types Data Wrapper
        List<DeviceCountByGroup> deviceCountsByOwnerships;
        try {
            deviceCountsByOwnerships = gadgetDataService.getDeviceCountsByOwnershipTypes(filterSet);
        } catch (InvalidPotentialVulnerabilityValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a filtered set of device counts by ownerships.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(INVALID_QUERY_PARAM_VALUE_POTENTIAL_VULNERABILITY).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of device counts by ownerships.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper2.setContext("Device-counts-by-ownerships");
        dashboardGadgetDataWrapper2.setGroupingAttribute(OWNERSHIP);
        dashboardGadgetDataWrapper2.setData(deviceCountsByOwnerships);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper1);
        responsePayload.add(dashboardGadgetDataWrapper2);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("feature-non-compliant-device-counts-by-groups")
    public Response getFeatureNonCompliantDeviceCountsByGroups(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                               @QueryParam(PLATFORM) String platform,
                                                               @QueryParam(OWNERSHIP) String ownership) {
        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        BasicFilterSet filterSet = new BasicFilterSet();
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating feature-non-compliant-device-Counts-by-platforms Data Wrapper
        List<DeviceCountByGroup> featureNonCompliantDeviceCountsByPlatforms;
        try {
            featureNonCompliantDeviceCountsByPlatforms = gadgetDataService.
                getFeatureNonCompliantDeviceCountsByPlatforms(nonCompliantFeatureCode, filterSet);
        } catch (InvalidFeatureCodeValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a filtered set of feature non-compliant device counts by platforms.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(REQUIRED_QUERY_PARAM_VALUE_NON_COMPLIANT_FEATURE_CODE).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant " +
                    "device counts by platforms.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper1 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper1.setContext("Feature-non-compliant-device-counts-by-platforms");
        dashboardGadgetDataWrapper1.setGroupingAttribute(PLATFORM);
        dashboardGadgetDataWrapper1.setData(featureNonCompliantDeviceCountsByPlatforms);

        // creating feature-non-compliant-device-Counts-by-ownership-types Data Wrapper
        List<DeviceCountByGroup> featureNonCompliantDeviceCountsByOwnerships;
        try {
            featureNonCompliantDeviceCountsByOwnerships = gadgetDataService.
                getFeatureNonCompliantDeviceCountsByOwnershipTypes(nonCompliantFeatureCode, filterSet);
        } catch (InvalidFeatureCodeValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a filtered set of feature non-compliant device counts by ownerships.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(REQUIRED_QUERY_PARAM_VALUE_NON_COMPLIANT_FEATURE_CODE).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered set of feature non-compliant " +
                    "device counts by ownerships.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper2 = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper2.setContext("Feature-non-compliant-device-counts-by-ownerships");
        dashboardGadgetDataWrapper2.setGroupingAttribute(OWNERSHIP);
        dashboardGadgetDataWrapper2.setData(featureNonCompliantDeviceCountsByOwnerships);

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
                                                    @QueryParam(OWNERSHIP) String ownership) {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        ExtendedFilterSet filterSet = new ExtendedFilterSet();
        filterSet.setConnectivityStatus(connectivityStatus);
        filterSet.setPotentialVulnerability(potentialVulnerability);
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating filteredDeviceCount Data Wrapper
        DeviceCountByGroup filteredDeviceCount;
        try {
            filteredDeviceCount = gadgetDataService.getDeviceCount(filterSet);
        } catch (InvalidPotentialVulnerabilityValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a filtered device count over the total.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(INVALID_QUERY_PARAM_VALUE_POTENTIAL_VULNERABILITY).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a filtered device count over the total.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        // creating TotalDeviceCount Data Wrapper
        DeviceCountByGroup totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve the total device count over filtered.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        List<Object> filteredDeviceCountOverTotalDataWrapper = new ArrayList<>();
        filteredDeviceCountOverTotalDataWrapper.add(filteredDeviceCount);
        filteredDeviceCountOverTotalDataWrapper.add(totalDeviceCount);

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("Filtered-device-count-over-total");
        dashboardGadgetDataWrapper.setGroupingAttribute(null);
        dashboardGadgetDataWrapper.setData(filteredDeviceCountOverTotalDataWrapper);

        List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
        responsePayload.add(dashboardGadgetDataWrapper);

        return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();
    }

    @GET
    @Path("feature-non-compliant-device-count-over-total")
    public Response getFeatureNonCompliantDeviceCountOverTotal(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                               @QueryParam(PLATFORM) String platform,
                                                               @QueryParam(OWNERSHIP) String ownership) {

        // getting gadget data service
        GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

        // constructing filter set
        BasicFilterSet filterSet = new BasicFilterSet();
        filterSet.setPlatform(platform);
        filterSet.setOwnership(ownership);

        // creating featureNonCompliantDeviceCount Data Wrapper
        DeviceCountByGroup featureNonCompliantDeviceCount;
        try {
            featureNonCompliantDeviceCount = gadgetDataService.
                getFeatureNonCompliantDeviceCount(nonCompliantFeatureCode, filterSet);
        } catch (InvalidFeatureCodeValueException e) {
            log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                "This was while trying to execute relevant data service function @ Dashboard API layer to retrieve " +
                    "a feature non-compliant device count over the total.", e);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(REQUIRED_QUERY_PARAM_VALUE_NON_COMPLIANT_FEATURE_CODE).build();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve a feature non-compliant device count over the total.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        // creating TotalDeviceCount Data Wrapper
        DeviceCountByGroup totalDeviceCount;
        try {
            totalDeviceCount = gadgetDataService.getTotalDeviceCount();
        } catch (DataAccessLayerException e) {
            log.error("An internal error occurred while trying to execute relevant data service function " +
                "@ Dashboard API layer to retrieve the total device count over filtered feature non-compliant.", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
        }

        List<Object> featureNonCompliantDeviceCountOverTotalDataWrapper = new ArrayList<>();
        featureNonCompliantDeviceCountOverTotalDataWrapper.add(featureNonCompliantDeviceCount);
        featureNonCompliantDeviceCountOverTotalDataWrapper.add(totalDeviceCount);

        DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
        dashboardGadgetDataWrapper.setContext("Feature-non-compliant-device-count-over-total");
        dashboardGadgetDataWrapper.setGroupingAttribute(null);
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
                                          @QueryParam(OWNERSHIP) String ownership,
                                          @QueryParam(PAGINATION_ENABLED) String paginationEnabled,
                                          @QueryParam(START_INDEX) int startIndex,
                                          @QueryParam(RESULT_COUNT) int resultCount) {

        if (paginationEnabled == null) {

            log.error("Bad request on retrieving a filtered set of devices with details @ " +
                "Dashboard API layer. " + REQUIRED_QUERY_PARAM_VALUE_PAGINATION_ENABLED);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(REQUIRED_QUERY_PARAM_VALUE_PAGINATION_ENABLED).build();

        } else if (FLAG_TRUE.equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            ExtendedFilterSet filterSet = new ExtendedFilterSet();
            filterSet.setConnectivityStatus(connectivityStatus);
            filterSet.setPotentialVulnerability(potentialVulnerability);
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            PaginationResult paginationResult;
            try {
                paginationResult = gadgetDataService.
                    getDevicesWithDetails(filterSet, startIndex, resultCount);
            } catch (InvalidPotentialVulnerabilityValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(INVALID_QUERY_PARAM_VALUE_POTENTIAL_VULNERABILITY).build();
            } catch (InvalidStartIndexValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(INVALID_QUERY_PARAM_VALUE_START_INDEX).build();
            } catch (InvalidResultCountValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(INVALID_QUERY_PARAM_VALUE_RESULT_COUNT).build();
            } catch (DataAccessLayerException e) {
                log.error("An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of devices with details.", e);
                return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                    entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
            }

            DashboardPaginationGadgetDataWrapper
                dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();
            dashboardPaginationGadgetDataWrapper.setContext("Filtered-and-paginated-devices-with-details");
            dashboardPaginationGadgetDataWrapper.setGroupingAttribute(null);
            dashboardPaginationGadgetDataWrapper.setData(paginationResult.getData());
            dashboardPaginationGadgetDataWrapper.setTotalRecordCount(paginationResult.getRecordsTotal());

            List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardPaginationGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else if (FLAG_FALSE.equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            ExtendedFilterSet filterSet = new ExtendedFilterSet();
            filterSet.setConnectivityStatus(connectivityStatus);
            filterSet.setPotentialVulnerability(potentialVulnerability);
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            List<DeviceWithDetails> devicesWithDetails;
            try {
                devicesWithDetails = gadgetDataService.getDevicesWithDetails(filterSet);
            } catch (InvalidPotentialVulnerabilityValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(INVALID_QUERY_PARAM_VALUE_POTENTIAL_VULNERABILITY).build();
            } catch (DataAccessLayerException e) {
                log.error("An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of devices with details.", e);
                return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                    entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
            }

            DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
            dashboardGadgetDataWrapper.setContext("Filtered-devices-with-details");
            dashboardGadgetDataWrapper.setGroupingAttribute(null);
            dashboardGadgetDataWrapper.setData(devicesWithDetails);

            List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else {

            log.error("Bad request on retrieving a filtered set of devices with details @ " +
                "Dashboard API layer. " + INVALID_QUERY_PARAM_VALUE_PAGINATION_ENABLED);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(INVALID_QUERY_PARAM_VALUE_PAGINATION_ENABLED).build();

        }
    }

    @GET
    @Path("feature-non-compliant-devices-with-details")
    public Response getFeatureNonCompliantDevicesWithDetails(@QueryParam(NON_COMPLIANT_FEATURE_CODE) String nonCompliantFeatureCode,
                                                             @QueryParam(PLATFORM) String platform,
                                                             @QueryParam(OWNERSHIP) String ownership,
                                                             @QueryParam(PAGINATION_ENABLED) String paginationEnabled,
                                                             @QueryParam(START_INDEX) int startIndex,
                                                             @QueryParam(RESULT_COUNT) int resultCount) {
        if (paginationEnabled == null) {

            log.error("Bad request on retrieving a filtered set of feature non-compliant devices with " +
                "details @ Dashboard API layer. " + REQUIRED_QUERY_PARAM_VALUE_PAGINATION_ENABLED);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(REQUIRED_QUERY_PARAM_VALUE_PAGINATION_ENABLED).build();

        } else if (FLAG_TRUE.equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            BasicFilterSet filterSet = new BasicFilterSet();
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            PaginationResult paginationResult;
            try {
                paginationResult = gadgetDataService.
                    getFeatureNonCompliantDevicesWithDetails(nonCompliantFeatureCode,
                        filterSet, startIndex, resultCount);
            } catch (InvalidFeatureCodeValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of feature non-compliant devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(REQUIRED_QUERY_PARAM_VALUE_NON_COMPLIANT_FEATURE_CODE).build();
            } catch (InvalidStartIndexValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of feature non-compliant devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(INVALID_QUERY_PARAM_VALUE_START_INDEX).build();
            } catch (InvalidResultCountValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of feature non-compliant devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(INVALID_QUERY_PARAM_VALUE_RESULT_COUNT).build();
            } catch (DataAccessLayerException e) {
                log.error("An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature " +
                        "non-compliant devices with details.", e);
                return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                    entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
            }

            DashboardPaginationGadgetDataWrapper
                dashboardPaginationGadgetDataWrapper = new DashboardPaginationGadgetDataWrapper();
            dashboardPaginationGadgetDataWrapper.
                setContext("Filtered-and-paginated-feature-non-compliant-devices-with-details");
            dashboardPaginationGadgetDataWrapper.setGroupingAttribute(null);
            dashboardPaginationGadgetDataWrapper.setData(paginationResult.getData());
            dashboardPaginationGadgetDataWrapper.setTotalRecordCount(paginationResult.getRecordsTotal());

            List<DashboardPaginationGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardPaginationGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else if (FLAG_FALSE.equals(paginationEnabled)) {

            // getting gadget data service
            GadgetDataService gadgetDataService = MDMAPIUtils.getGadgetDataService();

            // constructing filter set
            BasicFilterSet filterSet = new BasicFilterSet();
            filterSet.setPlatform(platform);
            filterSet.setOwnership(ownership);

            List<DeviceWithDetails> featureNonCompliantDevicesWithDetails;
            try {
                featureNonCompliantDevicesWithDetails = gadgetDataService.
                    getFeatureNonCompliantDevicesWithDetails(nonCompliantFeatureCode, filterSet);
            } catch (InvalidFeatureCodeValueException e) {
                log.error("Error occurred @ Gadget Data Service layer due to invalid (query) parameter value. " +
                    "This was while trying to execute relevant data service function @ Dashboard API layer to " +
                        "retrieve a filtered set of feature non-compliant devices with details.", e);
                return Response.status(HttpStatus.SC_BAD_REQUEST).
                    entity(REQUIRED_QUERY_PARAM_VALUE_NON_COMPLIANT_FEATURE_CODE).build();
            } catch (DataAccessLayerException e) {
                log.error("An internal error occurred while trying to execute relevant data service function " +
                    "@ Dashboard API layer to retrieve a filtered set of feature " +
                        "non-compliant devices with details.", e);
                return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).
                    entity(ERROR_IN_RETRIEVING_REQUESTED_DATA).build();
            }

            DashboardGadgetDataWrapper dashboardGadgetDataWrapper = new DashboardGadgetDataWrapper();
            dashboardGadgetDataWrapper.setContext("Filtered-feature-non-compliant-devices-with-details");
            dashboardGadgetDataWrapper.setGroupingAttribute(null);
            dashboardGadgetDataWrapper.setData(featureNonCompliantDevicesWithDetails);

            List<DashboardGadgetDataWrapper> responsePayload = new ArrayList<>();
            responsePayload.add(dashboardGadgetDataWrapper);

            return Response.status(HttpStatus.SC_OK).entity(responsePayload).build();

        } else {

            log.error("Bad request on retrieving a filtered set of feature non-compliant devices with " +
                "details @ Dashboard API layer. " + INVALID_QUERY_PARAM_VALUE_PAGINATION_ENABLED);
            return Response.status(HttpStatus.SC_BAD_REQUEST).
                entity(INVALID_QUERY_PARAM_VALUE_PAGINATION_ENABLED).build();

        }
    }

}
