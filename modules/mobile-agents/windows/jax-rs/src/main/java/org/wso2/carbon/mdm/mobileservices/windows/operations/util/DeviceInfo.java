/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 *  KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * /
 */
package org.wso2.carbon.mdm.mobileservices.windows.operations.util;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class generate Info operation type list.
 */
public class DeviceInfo {
    public List<Operation> getDeviceInfo() {

        List<Operation> deviceInfoOperations = new ArrayList<>();

        Operation osVersion = new Operation();
        osVersion.setCode("SOFTWARE_VERSION");
        osVersion.setType(Operation.Type.INFO);
        deviceInfoOperations.add(osVersion);

        Operation imsi = new Operation();
        imsi.setCode("IMSI");
        imsi.setType(Operation.Type.INFO);
        deviceInfoOperations.add(imsi);

        Operation imei = new Operation();
        imei.setCode("IMEI");
        imei.setType(Operation.Type.INFO);
        deviceInfoOperations.add(imei);

        Operation deviceID = new Operation();
        deviceID.setCode("DEV_ID");
        deviceID.setType(Operation.Type.INFO);
        deviceInfoOperations.add(deviceID);

        Operation manufacturer = new Operation();
        manufacturer.setCode("MANUFACTURER");
        manufacturer.setType(Operation.Type.INFO);
        deviceInfoOperations.add(manufacturer);

        Operation model = new Operation();
        model.setCode("MODEL");
        model.setType(Operation.Type.INFO);
        deviceInfoOperations.add(model);

        Operation language = new Operation();
        language.setCode("LANGUAGE");
        language.setType(Operation.Type.INFO);
        deviceInfoOperations.add(language);

        Operation vender = new Operation();
        vender.setCode("VENDER");
        vender.setType(Operation.Type.INFO);
        deviceInfoOperations.add(vender);

        Operation macaddress = new Operation();
        macaddress.setCode("MAC_ADDRESS");
        macaddress.setType(Operation.Type.INFO);
        deviceInfoOperations.add(macaddress);

        Operation resolution = new Operation();
        resolution.setCode("RESOLUTION");
        resolution.setType(Operation.Type.INFO);
        deviceInfoOperations.add(resolution);

        Operation deviceName = new Operation();
        deviceName.setCode("DEVICE_NAME");
        deviceName.setType(Operation.Type.INFO);
        deviceInfoOperations.add(deviceName);

        return deviceInfoOperations;
    }

}
