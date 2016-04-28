/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.emm.agent.services.operationMgt;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.Build;

import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.services.operationMgt.OperationManager;
import org.wso2.emm.agent.services.operationMgt.OperationManagerDeviceOwner;
import org.wso2.emm.agent.services.operationMgt.OperationManagerOlderSdk;
import org.wso2.emm.agent.services.operationMgt.OperationManagerWorkProfile;
import org.wso2.emm.agent.utils.Constants;

/**
 * This class produce the matching Operation Manager according to the Device Configurations.
 */
public class OperationManagerFactory {

    private DeviceInfo info;
    private Context context;
    private DevicePolicyManager manager;

    public OperationManagerFactory(Context context, DevicePolicyManager devicePolicyManager) {
        this.context = context;
        this.info = new DeviceInfo(context);
        this.manager = devicePolicyManager;
    }

    public OperationManager getOperationManager() {
        if ((info.getSdkVersion() >= Build.VERSION_CODES.JELLY_BEAN) &&
                (info.getSdkVersion() <= Build.VERSION_CODES.LOLLIPOP)) {
            return new OperationManagerOlderSdk(context);
        } else {
            return getLollipopUpwardsOperationManager();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private OperationManager getLollipopUpwardsOperationManager() {
        if (manager.isProfileOwnerApp(Constants.PACKAGE_NAME)) {
            return new OperationManagerWorkProfile(context);
        }
        else if (manager.isDeviceOwnerApp(Constants.SERVICE_PACKAGE_NAME)) {
            return new OperationManagerDeviceOwner(context);
        }
        else {
            return new OperationManagerOlderSdk(context);
        }
    }

}
