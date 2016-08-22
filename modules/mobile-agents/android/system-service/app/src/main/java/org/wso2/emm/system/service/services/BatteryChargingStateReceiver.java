/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import android.util.Log;

import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.api.OTADownload;
import org.wso2.emm.system.service.api.OTAServerManager;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.Preference;

import java.net.MalformedURLException;

/**
 * Broadcast receiver for device battery charging action used to start firmware upgrade.
 */
public class BatteryChargingStateReceiver extends BroadcastReceiver {

    private static String TAG = BatteryChargingStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        int level = intent.getIntExtra("level", 0);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Battery Level: " + Integer.toString(level) + "%");
        }
        if (Preference.getBoolean(context, context.getResources().getString(R.string.firmware_upgrade_automatic_retry)) && level >= Constants.
                REQUIRED_BATTERY_LEVEL_TO_FIRMWARE_UPGRADE) {
            String status = Preference.getString(context, context.getResources().getString(R.string.upgrade_install_status));
            if (Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_INSTALL.equals(status)){
                Preference.putString(context, context.getResources().getString(R.string.upgrade_install_status), Constants.Status.SUCCESSFUL);
                try {
                    OTAServerManager manager = new OTAServerManager(context);
                    manager.startInstallUpgradePackage();
                } catch (MalformedURLException e) {
                    Log.e(TAG, "Firmware upgrade failed due to a file URI issue" + e);
                }
            } else if (Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_DOWNLOAD.equals(status)){
                Log.i(TAG, "Starting firmware download again upon network connectivity established.");
                OTADownload otaDownload = new OTADownload(context);
                otaDownload.startOTA();
            }
        }
    }
}
