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
import org.wso2.carbon.mdm.services.android.bean.EventPayload;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * APIs related to events published from Android agent are implemented here.
 */
public class EventService {
    private static final String ACCEPT = "Accept";
    private static Log log = LogFactory.getLog(EventService.class);

    //TODO: Passing data to DAS must be added
    @POST
    public Response publishEvents(@HeaderParam(ACCEPT) String acceptHeader,
                                  EventPayload eventPayload) {

        if (log.isDebugEnabled()) {
            log.debug("Invoking Android device even logging.");
        }

        MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
        Message message = new Message();
        Response response;

        return Response.status(Response.Status.OK).entity("").
                type(responseMediaType).build();

    }
}
