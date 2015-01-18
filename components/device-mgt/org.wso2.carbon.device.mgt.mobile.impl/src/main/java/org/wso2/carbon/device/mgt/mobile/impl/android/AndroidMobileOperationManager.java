/**
 *  Copyright (c) 2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.mobile.impl.android;

import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.Operation;
import org.wso2.carbon.device.mgt.common.OperationManagementException;
import org.wso2.carbon.device.mgt.mobile.AbstractMobileOperationManager;

import java.util.List;

public class AndroidMobileOperationManager extends AbstractMobileOperationManager {

    @Override
    public boolean addOperation(Operation operation, List<DeviceIdentifier> devices) throws
            OperationManagementException {
        return false;
    }

    @Override
    public List<Operation> getOperations(DeviceIdentifier deviceIdentifier)
            throws OperationManagementException {
        return null;
    }

}