/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
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
import org.wso2.carbon.device.mgt.common.device.details.DeviceWrapper;
import org.wso2.carbon.device.mgt.common.search.SearchContext;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchManagerService;
import org.wso2.carbon.device.mgt.core.search.mgt.SearchMgtException;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;

import javax.ws.rs.POST;
import javax.ws.rs.core.Response;
import java.util.List;

public class DeviceSearch {

    private static Log log = LogFactory.getLog(DeviceSearch.class);

    @POST
    public Response getDeviceInfo(SearchContext searchContext) throws MDMAPIException {
        SearchManagerService searchManagerService;
        List<DeviceWrapper> devices;
        try {
            searchManagerService = MDMAPIUtils.getSearchManagerService();
            devices = searchManagerService.search(searchContext);

        } catch (SearchMgtException e) {
            String msg = "Error occurred while searching the device information.";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
        return Response.status(HttpStatus.SC_OK).entity(devices).build();
    }
}

