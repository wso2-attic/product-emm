/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.services.android.util;

import org.apache.commons.lang.StringUtils;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.mdm.services.android.exception.BadRequestException;

import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

/**
 * Util class for holding Android device related util methods.
 */
public class AndroidDeviceUtils {

    private static final String COMMA_SEPARATION_PATTERN = ", ";

    public DeviceIDHolder validateDeviceIdentifiers(List<String> deviceIDs,
                                                    Message message, MediaType responseMediaType) {

        if (deviceIDs == null || deviceIDs.isEmpty()) {
            message.setResponseMessage("Device identifier list is empty");
            throw new BadRequestException(message, responseMediaType);
        }

        List<String> errorDeviceIdList = new ArrayList<String>();
        List<DeviceIdentifier> validDeviceIDList = new ArrayList<DeviceIdentifier>();

        int deviceIDCounter = 0;
        for (String deviceID : deviceIDs) {

            deviceIDCounter++;

            if (deviceID == null || deviceID.isEmpty()) {
                errorDeviceIdList.add(String.format(AndroidConstants.DeviceConstants.DEVICE_ID_NOT_FOUND,
                        deviceIDCounter));
                continue;
            }

            try {
                DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
                deviceIdentifier.setId(deviceID);
                deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.
                        MOBILE_DEVICE_TYPE_ANDROID);

                if (isValidDeviceIdentifier(deviceIdentifier)) {
                    validDeviceIDList.add(deviceIdentifier);
                } else {
                    errorDeviceIdList.add(String.format(AndroidConstants.DeviceConstants.DEVICE_ID_NOT_FOUND,
                            deviceIDCounter));
                }
            } catch (DeviceManagementException e) {
                errorDeviceIdList.add(String.format(AndroidConstants.DeviceConstants.DEVICE_ID_SERVICE_NOT_FOUND,
                        deviceIDCounter));
            }
        }

        DeviceIDHolder deviceIDHolder = new DeviceIDHolder();
        deviceIDHolder.setValidDeviceIDList(validDeviceIDList);
        deviceIDHolder.setErrorDeviceIdList(errorDeviceIdList);

        return deviceIDHolder;
    }

    public String convertErrorMapIntoErrorMessage(List<String> errorDeviceIdList) {
        return StringUtils.join(errorDeviceIdList.iterator(), COMMA_SEPARATION_PATTERN);
    }

    public static boolean isValidDeviceIdentifier(DeviceIdentifier deviceIdentifier) throws DeviceManagementException {
        Device device = AndroidAPIUtils.getDeviceManagementService().
                getDevice(deviceIdentifier);
        if (device == null || device.getDeviceIdentifier() == null ||
                device.getDeviceIdentifier().isEmpty() || device.getEnrolmentInfo() == null) {
            return false;
        } else if (EnrolmentInfo.Status.REMOVED.equals(device.getEnrolmentInfo().getStatus())) {
            return false;
        }
        return true;
    }
}
