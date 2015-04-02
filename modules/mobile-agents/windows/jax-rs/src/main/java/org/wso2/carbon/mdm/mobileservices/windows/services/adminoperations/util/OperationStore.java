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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.DeviceManagementServiceProviderImpl;
import org.wso2.carbon.device.mgt.core.operation.mgt.OperationManagerImpl;
import org.wso2.carbon.mdm.mobileservices.windows.common.SyncmlCommandType;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.Device;
import org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans.OperationRequest;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.beans.Wifi;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

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

    private static DeviceManagementServiceProviderImpl getDeviceManagementServiceProvider() {
        try {
            DeviceManagementServiceProviderImpl deviceManager;
            PrivilegedCarbonContext.startTenantFlow();
            PrivilegedCarbonContext ctx = PrivilegedCarbonContext.getThreadLocalCarbonContext();
            ctx.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            ctx.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
            deviceManager = (DeviceManagementServiceProviderImpl) ctx.getOSGiService(DeviceManagementServiceProviderImpl.class, null);

            if (deviceManager == null) {
                String msg = "Device management service is not initialized.";
                log.error(msg);
            }
            return deviceManager;
        }
        finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    private static Operation transformBasicOperation(OperationRequest operationRequest, Operation.Type type,
                                                     String commandType) throws
                                                     WindowsDeviceEnrolmentException {

        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Operation operation = new Operation();
        operation.setCode(commandType);
        operation.setType(type);

        if (commandType == SyncmlCommandType.WIFI.getValue()) {

//--------------Commented as getPayLoad() method is still not available----------------------

//            try {
//                Wifi wifiObject = (Wifi)operationRequest.getBasicOperation();
//                        operation.setPayload(objectWriter.writeValueAsString(configOperation));
//            } catch (IOException e) {
//                throw new WindowsDeviceEnrolmentException(
//                        "Failure in resolving JSON payload of WIFI operation.", e);
//            }
        }
        else {
//            no operation.....
        }

        return operation;
    }
}
