/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.services.android;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.analytics.datasource.commons.exception.AnalyticsException;
import org.wso2.carbon.device.mgt.analytics.data.publisher.exception.DataPublisherConfigurationException;
import org.wso2.carbon.mdm.services.android.bean.DeviceState;
import org.wso2.carbon.mdm.services.android.bean.wrapper.Event;
import org.wso2.carbon.mdm.services.android.bean.wrapper.EventPayload;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * APIs related to events published from Android agent are implemented here.
 */
public class EventService {
    private static final String ACCEPT = "Accept";
    private static Log log = LogFactory.getLog(EventService.class);
    private static final String EVENT_STREAM_DEFINITION = "event_stream";

    @POST
    public Response publishEvents(@HeaderParam(ACCEPT) String acceptHeader,
                                  Event event) throws AndroidAgentException {

        EventPayload eventPayload = event.getPayload();

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device even logging.");
        }
        Message message = new Message();
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);

        Object payload[] = {event.getDeviceIdentifier(), eventPayload.getPackageName(), eventPayload.getState(),
                event.getType()};
        try {
            if (AndroidAPIUtils.getEventPublisherService().publishEvent(
                    EVENT_STREAM_DEFINITION, "1.0.0", new Object[0], new Object[0], payload)) {
                message.setResponseCode("Event is published successfully.");
                return Response.status(Response.Status.OK).entity(message).type(responseMediaType).build();
            } else {
                message.setResponseCode("Error occurred while publishing the event.");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).
                        entity(message).type(responseMediaType).build();
            }
        } catch (DataPublisherConfigurationException e) {
            String msg = "Error occurred while publishing the events from Android agent";
            log.error(msg, e);
            throw new AndroidAgentException(msg, e);
        }
    }

    @Path("{deviceId}")
    @Produces("application/json")
    @GET
    public Response retrieveAlert(@PathParam("deviceId") String deviceId) throws AnalyticsException {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device Identifier.");
        }
        String query = "deviceIdentifier:" + deviceId;
        List<DeviceState> deviceStates = AndroidAPIUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
        return Response.status(Response.Status.OK).entity(deviceStates).build();
    }

    @Path("{deviceId}/date")
    @Produces("application/json")
    @GET
    public Response retrieveAlertFromDate(@PathParam("deviceId") String deviceId, @QueryParam("from") long from,
                                          @QueryParam("to") long to) throws AnalyticsException {
        String fromDate = String.valueOf(from);
        String toDate = String.valueOf(to);
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device Identifier and time period.");
        }
        String query = "deviceIdentifier:" + deviceId + " AND _timestamp: [" + fromDate + " TO " + toDate + "]";
        List<DeviceState> deviceStates = AndroidAPIUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
        return Response.status(Response.Status.OK).entity(deviceStates).build();
    }

    @Path("{deviceId}/type/{type}")
    @GET
    public Response retrieveAlertType(@PathParam("deviceId") String deviceId, @PathParam("type") String type)
            throws AnalyticsException {

        if (log.isDebugEnabled()) {
            log.debug("Retrieving events for given device identifier and type.");
        }
        String query = "deviceIdentifier:" + deviceId + " AND type:" + type;
        List<DeviceState> deviceStates = AndroidAPIUtils.getAllEventsForDevice(EVENT_STREAM_DEFINITION, query);
        return Response.status(Response.Status.OK).entity(deviceStates).build();
    }
}
