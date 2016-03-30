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

package org.wso2.emm.agent.factory;

import android.content.Context;
import android.os.Build;

import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.services.OperationManager;
import org.wso2.emm.agent.services.OperationManagerOlderSdk;
import org.wso2.emm.agent.services.OperationManagerWorkProfile;
import org.wso2.emm.agent.services.OperationProcessor;

public class OperationManagerFactory {
    private DeviceInfo info;
    Context context;

    public OperationManagerFactory(Context context){
        this.context = context;
        this.info = new DeviceInfo(context);
    }

    public OperationManager getOperationManager(OperationProcessor operationProcessor) {
        if ((info.getSdkVersion() >= Build.VERSION_CODES.JELLY_BEAN) && (info.getSdkVersion() <= Build.VERSION_CODES.LOLLIPOP)) {
            return new OperationManagerOlderSdk(context);
        }
        else if ((info.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP))
            return new OperationManagerWorkProfile(context,operationProcessor);
        return null;
    }
}
