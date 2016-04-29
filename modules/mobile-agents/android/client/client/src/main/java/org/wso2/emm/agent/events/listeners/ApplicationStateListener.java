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

package org.wso2.emm.agent.events.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.events.EventRegistry;
import org.wso2.emm.agent.events.beans.ApplicationStatus;
import org.wso2.emm.agent.events.beans.EventPayload;
import org.wso2.emm.agent.events.publisher.HttpDataPublisher;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;

/**
 * Listening to application state changes such as an app getting installed, uninstalled,
 * upgraded and data cleared.
 */
public class ApplicationStateListener extends BroadcastReceiver implements AlertEventListener {
    private static final String TAG = ApplicationStateListener.class.getName();

    @Override
    public void startListening() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        intentFilter.addDataScheme("package");
        EventRegistry.context.registerReceiver(this, intentFilter);
    }

    @Override
    public void stopListening() {
        if (EventRegistry.context != null) {
            EventRegistry.context.unregisterReceiver(this);
        }
    }

    @Override
    public void publishEvent(String payload, String type) {
        EventPayload eventPayload = new EventPayload();
        eventPayload.setPayload(payload);
        eventPayload.setType(type);
        HttpDataPublisher httpDataPublisher = new HttpDataPublisher();
        httpDataPublisher.publish(eventPayload);
    }

    @Override
    public void onReceive(Context context, final Intent intent) {
        String status = null;
        ApplicationStatus applicationState;
        switch (intent.getAction()) {
            case Intent.ACTION_PACKAGE_ADDED:
                status = "added";
                break;
            case Intent.ACTION_PACKAGE_REMOVED:
                status = "removed";
                break;
            case Intent.ACTION_PACKAGE_REPLACED:
                status = "upgraded";
                break;
            case Intent.ACTION_PACKAGE_DATA_CLEARED:
                status = "dataCleared";
                break;
            default:
                Log.i(TAG, "Invalid intent received");
        }
        if (status != null) {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();
            applicationState = new ApplicationStatus();
            applicationState.setState(status);
            applicationState.setPackageName(packageName);
            try {
                String appState = CommonUtils.toJSON(applicationState);
                publishEvent(appState, Constants.EventListners.APPLICATION_STATE);
                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, appState);
                }
            } catch (AndroidAgentException e) {
                Log.e(TAG, "Could not convert to JSON");
            }
        }
    }
}
