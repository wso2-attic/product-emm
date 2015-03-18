/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.mobileservices.windows.services.adminoperations.beans;

import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ConfigOperation;
import java.util.List;

public class OperationRequest {

    private List<Devices> deviceList;
    private List<CommandOperation> commandOperations;
    private List<ConfigOperation> configOperations;

    public List<ConfigOperation> getConfigOperations() {
        return configOperations;
    }

    public void setConfigOperations(List<ConfigOperation> configOperations) {
        this.configOperations = configOperations;
    }

    public List<Devices> getDeviceList() {
        return deviceList;

    }

    public void setDeviceList(List<Devices> deviceList) {
        this.deviceList = deviceList;
    }

    public List<CommandOperation> getCommandOperations() {
        return commandOperations;
    }

    public void setCommandOperations(List<CommandOperation> commandOperations) {
        this.commandOperations = commandOperations;
    }

}
