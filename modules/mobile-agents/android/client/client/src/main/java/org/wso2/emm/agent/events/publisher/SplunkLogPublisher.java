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
package org.wso2.emm.agent.events.publisher;

import android.content.Context;
import com.splunk.mint.Mint;
import com.splunk.mint.MintLogLevel;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.events.beans.EventPayload;
import org.wso2.emm.agent.utils.Constants;

import java.util.HashMap;

/**
 * This class handles publishing of device logs to Splunk.
 */
public class SplunkLogPublisher implements DataPublisher {
    private static final String TAG = SplunkLogPublisher.class.getName();
    private static String deviceIdentifier;

    public SplunkLogPublisher(Context context) {
        DeviceInfo deviceInfo = new DeviceInfo(context);
        deviceIdentifier = deviceInfo.getDeviceId();
        if (Constants.SplunkConfigs.TYPE_MINT.equals(Constants.SplunkConfigs.DATA_COLLECTOR_TYPE)) {
            Mint.initAndStartSession(context, Constants.SplunkConfigs.API_KEY);
        } else {
            Mint.initAndStartSessionHEC(context, Constants.SplunkConfigs.HEC_MINT_ENDPOINT_URL, Constants.SplunkConfigs.HEC_TOKEN);
        }
    }

    @Override
    public void publish(EventPayload eventPayload) {
        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put("deviceId", deviceIdentifier);
        payload.put("log", eventPayload.getPayload());
        Mint.logEvent("EMM Logs", MintLogLevel.Info, payload);
    }
}
