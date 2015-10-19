/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.util;

import com.google.gson.Gson;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.ConfigOperation;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementProviderService;
import org.wso2.carbon.mdm.mobileservices.windows.common.SyncmlCommandType;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.Device;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationRequest;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Wifi;

import java.util.ArrayList;
import java.util.List;

public class OperationStore {

    private static Log log = LogFactory.getLog(OperationStore.class);

    public static boolean storeOperation(OperationRequest operationRequest, Operation.Type type,
                                         String commandType) throws
                                                             WindowsDeviceEnrolmentException {

        List<Device> devices = operationRequest.getDeviceList();
        List<DeviceIdentifier> deviceIdentifiers = new ArrayList<DeviceIdentifier>();
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();

        Operation operation = transformBasicOperation(operationRequest, type, commandType);

        for (int i = 0; i < devices.size(); i++) {
            try {
                deviceIdentifier.setType(DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
                deviceIdentifier.setId(devices.get(i).getID());
                deviceIdentifiers.add(deviceIdentifier);
                getDeviceManagementServiceProvider().getDevice(deviceIdentifier);

            } catch (DeviceManagementException e) {
                log.error("Cannot validate device ID: " + devices.get(i).getID());
                deviceIdentifiers.remove(i);
            }
        }
        try {
            getDeviceManagementServiceProvider().addOperation(operation, deviceIdentifiers);
        } catch (OperationManagementException e) {
            String msg = "Failure occurred while storing command operation.";
            log.error(msg);
            return false;
        }
        return true;
    }

    private static DeviceManagementProviderService getDeviceManagementServiceProvider() {
        DeviceManagementProviderService deviceManager;
        PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
        deviceManager =
                (DeviceManagementProviderService) ctx.getOSGiService(DeviceManagementProviderService.class, null);

        if (deviceManager == null) {
            String msg = "Device management service is not initialized.";
            log.error(msg);
        }
        return deviceManager;
    }

    private static Operation transformBasicOperation(OperationRequest operationRequest, Operation.Type type,
                                                     String commandType) throws WindowsDeviceEnrolmentException {

        Operation operation = new Operation();
        operation.setCode(commandType);
        operation.setType(type);
        Gson gson = new Gson();

        if (commandType == SyncmlCommandType.WIFI.getValue()) {

            operation = new ConfigOperation();
            operation.setCode(commandType);
            operation.setType(type);

            Wifi wifiObject = (Wifi) operationRequest.getBasicOperation();
            operation.setPayLoad(gson.toJson(wifiObject));
        } else {
            //            no operation.....
        }

        return operation;
    }
}
