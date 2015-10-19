/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class generate Info type operation list.
 */
public class DeviceInfo {
    public List<Operation> getDeviceInfo() {

        List<Operation> deviceInfoOperations = new ArrayList<>();

        Operation osVersion = new Operation();
        osVersion.setCode(PluginConstants.SyncML.SOFTWARE_VERSION);
        osVersion.setType(Operation.Type.INFO);
        deviceInfoOperations.add(osVersion);

        Operation imsi = new Operation();
        imsi.setCode(PluginConstants.SyncML.IMSI);
        imsi.setType(Operation.Type.INFO);
        deviceInfoOperations.add(imsi);

        Operation imei = new Operation();
        imei.setCode(PluginConstants.SyncML.IMEI);
        imei.setType(Operation.Type.INFO);
        deviceInfoOperations.add(imei);

        Operation deviceID = new Operation();
        deviceID.setCode(PluginConstants.SyncML.DEV_ID);
        deviceID.setType(Operation.Type.INFO);
        deviceInfoOperations.add(deviceID);

        Operation manufacturer = new Operation();
        manufacturer.setCode(PluginConstants.SyncML.MANUFACTURER);
        manufacturer.setType(Operation.Type.INFO);
        deviceInfoOperations.add(manufacturer);

        Operation model = new Operation();
        model.setCode(PluginConstants.SyncML.MODEL);
        model.setType(Operation.Type.INFO);
        deviceInfoOperations.add(model);

        Operation language = new Operation();
        language.setCode(PluginConstants.SyncML.LANGUAGE);
        language.setType(Operation.Type.INFO);
        deviceInfoOperations.add(language);

        Operation vendor = new Operation();
        vendor.setCode(PluginConstants.SyncML.VENDOR);
        vendor.setType(Operation.Type.INFO);
        deviceInfoOperations.add(vendor);

        Operation macaddress = new Operation();
        macaddress.setCode(PluginConstants.SyncML.MAC_ADDRESS);
        macaddress.setType(Operation.Type.INFO);
        deviceInfoOperations.add(macaddress);

        Operation resolution = new Operation();
        resolution.setCode(PluginConstants.SyncML.RESOLUTION);
        resolution.setType(Operation.Type.INFO);
        deviceInfoOperations.add(resolution);

        Operation deviceName = new Operation();
        deviceName.setCode(PluginConstants.SyncML.DEVICE_NAME);
        deviceName.setType(Operation.Type.INFO);
        deviceInfoOperations.add(deviceName);

        return deviceInfoOperations;
    }
}
