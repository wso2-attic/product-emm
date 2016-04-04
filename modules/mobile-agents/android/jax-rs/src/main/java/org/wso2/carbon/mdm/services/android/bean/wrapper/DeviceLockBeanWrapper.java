/*
 * Copyright (c) 2016, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.services.android.bean.wrapper;

import org.wso2.carbon.mdm.services.android.bean.DeviceLock;
import org.wso2.carbon.mdm.services.android.bean.Notification;

import java.util.List;

/**
 * This class is used to wrap the Notification bean with devices.
 */
public class DeviceLockBeanWrapper {

    private List<String> deviceIDs;
    private DeviceLock operation;

    public List<String> getDeviceIDs() {
        return deviceIDs;
    }

    public void setDeviceIDs(List<String> deviceIDs) {
        this.deviceIDs = deviceIDs;
    }

    public DeviceLock getOperation() {
        return operation;
    }

    public void setOperation(DeviceLock operation) {
        this.operation = operation;
    }
}
