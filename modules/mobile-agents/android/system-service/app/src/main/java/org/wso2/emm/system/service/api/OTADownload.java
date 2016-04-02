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
package org.wso2.emm.system.service.api;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import org.wso2.emm.system.service.utils.Constants;

import java.net.MalformedURLException;


public class OTADownload implements OTAServerManager.OTAStateChangeListener {

    private static final String TAG = "OTADownload";
    private static final String SI_UNITS_INDEX = "kMGTPE";
    private static final String BINARY_UNITS_INDEX = "KMGTPE";
    private Context context;
    private OTAServerManager otaServerManager;

    public OTADownload(Context context) {
        this.context = context;
        try {
            otaServerManager = new OTAServerManager(this.context);
        } catch (MalformedURLException e) {
            otaServerManager = null;
            Log.e(TAG, "OTA server manager threw exception ..." + e);
        }
        otaServerManager.setStateChangeListener(this);
    }

    /**
     * Returns the byte count in a human readable format.
     *
     * @param bytes - Bytes to be converted.
     * @param isSI  - True if the input is in SI units and False if the input is in binary units.
     * @return - Byte count string.
     */
    public String byteCountToDisplaySize(long bytes, boolean isSI) {
        int unit = isSI ? 1000 : 1024;
        if (bytes < unit) {
            return bytes + " B";
        }
        int numberToFormat = (int) (Math.log(bytes) / Math.log(unit));
        String prefix = (isSI ? SI_UNITS_INDEX : BINARY_UNITS_INDEX).charAt(numberToFormat - 1) + (isSI ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, numberToFormat), prefix);
    }

    public void startOTA() {
        //Check in the main service thread
        otaServerManager.startCheckingVersion();
    }

    public void onStateOrProgress(int message, int error, BuildPropParser parser, long info) {
        Log.d(TAG, "onStateOrProgress: " + "message: " + message + " error:" + error + " info: " + info);
        /* State change will be 0 -> Checked(1) -> Downloading(2) -> Upgrading(3) */
        switch (message) {
            case STATE_IN_CHECKED:
                onStateChecked(error, parser);
                break;
            case STATE_IN_DOWNLOADING:
                onStateDownload(error, info);
                break;
            case STATE_IN_UPGRADING:
                onStateUpgrade(error);
                break;
            case MESSAGE_DOWNLOAD_PROGRESS:
                break;
            case MESSAGE_VERIFY_PROGRESS:
                onProgress(info);
                break;
        }
    }

    public void onStateChecked(int error, BuildPropParser parser) {
        if (error == 0) {
            if (!otaServerManager.compareLocalVersionToServer()) {
                Log.i(TAG, "Software is up to date:" + Build.VERSION.RELEASE + ", " + Build.ID);
            } else {
                final long bytes = otaServerManager.getUpgradePackageSize();
                Log.i(TAG, "New release found " + Build.VERSION.RELEASE + ", " + Build.ID);
                String length = "Unknown";
                if (bytes > 0) {
                    length = byteCountToDisplaySize(bytes, false);
                }

                Log.i(TAG, "version :" +
                           parser.getProp("ro.build.id") + "\n" +
                           "full_version :" +
                           parser.getProp("ro.build.description") + "\n" +
                           "size : " + length);
                //Downloading the new update package if a new version is available.
                otaServerManager.startDownloadUpgradePackage();
            }
        } else if (error == ERROR_WIFI_NOT_AVAILABLE) {
            Log.e(TAG, "OTA failed due to WIFI connection failure.");
        } else if (error == ERROR_CANNOT_FIND_SERVER) {
            Log.e(TAG, "OTA failed due to OTA server not accessible.");
        } else if (error == ERROR_WRITE_FILE_ERROR) {
            Log.e(TAG, "OTA failed due to file write error.");
        }
    }

    public void onStateDownload(int error, Object info) {
        Log.i(TAG, "Printing package information " + info.toString());
        if (error == ERROR_CANNOT_FIND_SERVER) {
            Log.e(TAG, "Error: server does not have an upgrade package.");
        } else if (error == ERROR_WRITE_FILE_ERROR) {
            Log.e(TAG, "OTA failed due to file write error.");
        }

        if (error == 0) {
            // Success download, trying to install package.
            otaServerManager.startVerifyUpgradePackage();
            showMessageToCurrentUser(context, "upgrade");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted." + e);
            }
            otaServerManager.startInstallUpgradePackage();
        }
    }

    public void onStateUpgrade(int error) {
        if (error == ERROR_PACKAGE_VERIFY_FAILED) {
            Log.e(TAG, "Package verification failed, signature does not match.");
        } else if (error == ERROR_PACKAGE_INSTALL_FAILED) {
            Log.e(TAG, "Package installation Failed.");
        }
    }

    public void onProgress(Long progress) {
        Log.v(TAG, "Progress : " + progress);
    }

    public void showMessageToCurrentUser(Context context, String message) {
        Intent intent = new Intent(Constants.AGENT_APP_PACKAGE_NAME + Constants.AGENT_APP_ALERT_ACTIVITY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("message", message);

        Log.i(TAG, "The serial Number for current user is:" + ActivityManager.getCurrentUser());

        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        UserHandle userHandle = userManager.getUserForSerialNumber(ActivityManager.getCurrentUser());
        context.startActivityAsUser(intent, userHandle);
    }

}












