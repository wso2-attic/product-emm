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
package org.wso2.app.catalog.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.app.catalog.R;
import org.wso2.app.catalog.api.ApplicationManager;
import org.wso2.app.catalog.utils.CommonUtils;
import org.wso2.app.catalog.utils.Constants;
import org.wso2.app.catalog.utils.Preference;

/**
 * This is the service class which exposes all the application management related data provider services
 * to other applications.
 */
public class AppCatalogService extends IntentService {

    private static final String TAG = AppCatalogService.class.getName();
    private static final String INTENT_KEY_CODE = "code";
    private static final String INTENT_KEY_PAYLOAD = "payload";
    private static final String INTENT_KEY_STATUS = "status";
    private static final String INTENT_KEY_APP = "app";
    private static final String INTENT_KEY_PROGRESS = "progress";
    private Context context;
    private String operationCode = null;

    public AppCatalogService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        context = this.getApplicationContext();
        if (extras != null) {
            operationCode = extras.getString(INTENT_KEY_CODE);
        }

        Log.d(TAG, "App catalog has sent a command.");
        if ((operationCode != null)) {
            Log.d(TAG, "The operation code is: " + operationCode);
            doTask(operationCode);
        }
    }

    /**
     * Executes device management operations on the device.
     *
     * @param code - Operation object.
     */
    public void doTask(String code) {
        switch (code) {
            case Constants.Operation.GET_APP_DOWNLOAD_PROGRESS:
                String downloadingApp = Preference.getString(context, context.getResources().getString(
                        R.string.current_downloading_app));
                JSONObject result = new JSONObject();
                ApplicationManager applicationManager = new ApplicationManager(context);
                if(applicationManager.isPackageInstalled(Constants.AGENT_PACKAGE_NAME)) {
                    IntentFilter filter = new IntentFilter(Constants.AGENT_APP_ACTION_RESPONSE);
                    filter.addCategory(Intent.CATEGORY_DEFAULT);
                    AgentServiceResponseReceiver receiver = new AgentServiceResponseReceiver();
                    registerReceiver(receiver, filter);
                    CommonUtils.callAgentApp(context, Constants.Operation.GET_APP_DOWNLOAD_PROGRESS, null, null);
                } else {
                    try {
                        result.put(INTENT_KEY_APP, downloadingApp);
                        result.put(INTENT_KEY_PROGRESS, Preference.getString(context, context.getResources().
                                getString(R.string.app_download_progress)));
                    } catch (JSONException e) {
                        Log.e(TAG, "Result object creation failed" + e);
                        sendBroadcast(Constants.Status.INTERNAL_SERVER_ERROR, null);
                    }
                    sendBroadcast(Constants.Status.SUCCESSFUL, result.toString());
                }

                break;
            default:
                Log.e(TAG, "Invalid operation code received");
                break;
        }
    }

    private void sendBroadcast(String status, String payload) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.ACTION_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(INTENT_KEY_STATUS, status);
        broadcastIntent.putExtra(INTENT_KEY_PAYLOAD, payload);
        sendBroadcast(broadcastIntent);
    }

    public class AgentServiceResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra(Constants.INTENT_KEY_STATUS);
            if(Constants.Status.SUCCESSFUL.equals(status)) {
                if (intent.hasExtra(Constants.INTENT_KEY_PAYLOAD) && intent.getStringExtra(Constants.
                                                                                                   INTENT_KEY_PAYLOAD) != null) {
                    try {
                        JSONObject result = new JSONObject();
                        String downloadingApp = Preference.getString(context, context.getResources().getString(
                                R.string.current_downloading_app));
                        result.put(INTENT_KEY_APP, downloadingApp);
                        result.put(INTENT_KEY_PROGRESS, intent.getStringExtra(Constants.INTENT_KEY_PAYLOAD));
                        sendBroadcast(Constants.Status.SUCCESSFUL, result.toString());
                    } catch (JSONException e) {
                        sendBroadcast(Constants.Status.INTERNAL_SERVER_ERROR, null);
                        Log.e(TAG, "Failed parsing application list response" + e);
                    }
                }
            } else {
                sendBroadcast(Constants.Status.INTERNAL_SERVER_ERROR, null);
            }
        }
    }
}
