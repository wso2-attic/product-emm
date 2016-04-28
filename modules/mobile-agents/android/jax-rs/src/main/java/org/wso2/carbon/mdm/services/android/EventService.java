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
import org.wso2.carbon.mdm.services.android.bean.EventPayload;
import org.wso2.carbon.mdm.services.android.exception.AndroidAgentException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.ws.rs.POST;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
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
                                  EventPayload eventPayload) throws AndroidAgentException {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device even logging.");
        }
        Message message = new Message();
        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);

        Object payload[] = { eventPayload.getDeviceIdentifier(), eventPayload.getPackageName(), eventPayload.getState(),
        eventPayload.getType() };
        try {
            if(AndroidAPIUtils.getEventPublisherService().publishEvent(
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
    public Response retrieveAlert(@PathParam("deviceId")String deviceId) throws AnalyticsException {
        if (log.isDebugEnabled()) {
            log.debug("Retrieving events");
        }
        String query = "deviceIdentifier:" + deviceId;
        List<DeviceState> deviceStates = AndroidAPIUtils.getAllEventsForDevice("EVENT_STREAM", query);
        return Response.status(Response.Status.OK).entity(deviceStates).build();
    }
}
