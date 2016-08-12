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
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.emm.system.service.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.api.OTADownload;
import org.wso2.emm.system.service.utils.Preference;

/**
 * This class handles all the functionality required for monitoring device network connectivity.
 * This can be used to invoke the agent application when the user connects the device to the network for
 * the first time.
 */
public class NetworkConnectivityStatusReceiver extends BroadcastReceiver {

    private static final String TAG = NetworkConnectivityStatusReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (wifi.isConnected()) {
            String status = Preference.getString(context, context.getResources().getString(R.string.upgrade_download_status));
            if (context.getResources().getString(R.string.status_connectivity_failed).equals(status)) {
                if (Preference.getBoolean(context, context.getResources().getString(R.string.automatic_firmware_upgrade))) {
                    Log.i(TAG, "Starting firmware download again upon network connectivity established.");
                    OTADownload otaDownload = new OTADownload(context);
                    otaDownload.startOTA();
                }
            }
        }
    }
}
