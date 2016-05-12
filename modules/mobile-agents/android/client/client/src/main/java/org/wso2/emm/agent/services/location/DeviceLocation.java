/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.emm.agent.services.location;

import android.content.Context;
import android.location.Location;
import org.wso2.emm.agent.beans.Address;
import org.wso2.emm.agent.services.location.impl.LocationServiceImpl;
import org.wso2.emm.agent.services.location.impl.OpenStreetMapService;

/**
 * This is a wrapper class which wraps both location service and
 * reverse geo coding service together.
 */
public class DeviceLocation {

    private LocationService locationService;
    private ReverseGeoCodingService reverseGeoCodingService;

    public DeviceLocation(Context context) {
        locationService = LocationServiceImpl.getInstance(context);
        reverseGeoCodingService = OpenStreetMapService.getInstance();
    }

    /**
     * This method is used to retrieve the current location of
     * the device.
     * @return Returns the location details including latitude and longitude.
     */
    public Location getCurrentLocation() {
        return locationService.getLocation();
    }

    /**
     * This method is used to retrieve the address details of current device location.
     * @return Returns the address.
     */
    public Address getCurrentAddress() {
        return reverseGeoCodingService.getReverseGeoCodes(locationService.getLocation());
    }
}
