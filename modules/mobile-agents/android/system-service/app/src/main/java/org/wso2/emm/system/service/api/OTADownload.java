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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.utils.CommonUtils;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.Preference;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;


public class OTADownload implements OTAServerManager.OTAStateChangeListener {

    private static final String TAG = "OTADownload";
    private static final String SI_UNITS_INDEX = "kMGTPE";
    private static final String BINARY_UNITS_INDEX = "KMGTPE";
    private static final String UPGRADE_AVAILABLE = "upgradeAvailable";
    private static final String UPGRADE_VERSION = "version";
    private static final String UPGRADE_RELEASE = "release";
    private static final String UPGRADE_SIZE = "size";
    private static final String UPGRADE_DESCRIPTION = "description";
    private Context context;
    private OTAServerManager otaServerManager;

    public OTADownload(Context context) {
        this.context = context;
        Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                Constants.Status.REQUEST_PLACED);
        try {
            otaServerManager = new OTAServerManager(this.context);
            otaServerManager.setStateChangeListener(this);
        } catch (MalformedURLException e) {
            otaServerManager = null;
            String message = "Firmware upgrade URL provided is not valid.";
            if (Preference.getBoolean(context, context.getResources().getString(R.string.
                    firmware_status_check_in_progress))) {
                CommonUtils.sendBroadcast(context, Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS, Constants.Code.FAILURE,
                        Constants.Status.MALFORMED_OTA_URL, message);
            } else {
                CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                        Constants.Status.MALFORMED_OTA_URL, message);
                CommonUtils.callAgentApp(context, Constants.Operation.
                        FIRMWARE_UPGRADE_FAILURE, Preference.getInt(
                        context, context.getResources().getString(R.string.operation_id)), message);
            }
            Log.e(TAG, "OTA server manager threw exception ..." + e);
        }
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

    private int getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null,
                new IntentFilter(
                        Intent.ACTION_BATTERY_CHANGED));
        int level = 0;
        if (batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }

        return level;
    }

    public void onStateOrProgress(int message, int error, BuildPropParser parser, long info) {
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

    public boolean checkNetworkOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        boolean status = false;
        if (info != null && info.isConnectedOrConnecting()) {
            status = true;
        }

        return status;
    }

    public void onStateChecked(int error, final BuildPropParser parser) {
        final String  operation = Preference.getBoolean(context, context.getResources().getString(R.string.
                firmware_status_check_in_progress)) ? Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS : Constants.Operation.UPGRADE_FIRMWARE;
        if (error == 0) {
            if (!otaServerManager.compareLocalVersionToServer(parser)) {
                Log.i(TAG, "Software is up to date:" + Build.VERSION.RELEASE + ", " + Build.ID);
                JSONObject result = new JSONObject();
                try {
                    result.put(UPGRADE_AVAILABLE, false);
                    if (parser != null) {
                        result.put(UPGRADE_DESCRIPTION, parser.getProp("Software is up to date"));
                    }
                    CommonUtils.sendBroadcast(context, operation, Constants.Code.SUCCESS, Constants.Status.NO_UPGRADE_FOUND, result.toString());
                } catch (JSONException e) {
                    String message = "Result payload build failed.";
                    CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.UPDATE_INFO_NOT_READABLE, message);
                    Log.e(TAG, message + e);
                }
            } else if (checkNetworkOnline()) {
                new AsyncTask<Void, Void, Long>() {
                    protected Long doInBackground(Void... param) {
                        URL url = otaServerManager.getServerConfig().getPackageURL();
                        URLConnection con;
                        try {
                            con = url.openConnection();
                            con.setConnectTimeout(Constants.FIRMWARE_UPGRADE_CONNECTIVITY_TIMEOUT);
                            con.setReadTimeout(Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
                            return (long) con.getContentLength();
                        } catch (SocketTimeoutException e) {
                            String message = "Connection failure (Socket timeout) when retrieving update package size.";
                            Log.e(TAG, message + e);
                            CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.CONNECTION_FAILED, message);
                            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
                            return (long) -1;
                        } catch (IOException e) {
                            String message = "Connection failure when retrieving update package size.";
                            Log.e(TAG, message + e);
                            CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.CONNECTION_FAILED, message);
                            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
                            return (long) -1;
                        }
                    }
                    protected void onPostExecute(Long bytes) {
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
                        if (Preference.getBoolean(context, context.getResources().getString(R.string.
                                                                                                    firmware_status_check_in_progress))) {
                            JSONObject result = new JSONObject();
                            try {
                                result.put(UPGRADE_AVAILABLE, true);
                                result.put(UPGRADE_SIZE, length);
                                result.put(UPGRADE_RELEASE, parser.getNumRelease());
                                result.put(UPGRADE_VERSION, parser.getProp("ro.build.id"));
                                result.put(UPGRADE_DESCRIPTION, parser.getProp("ro.build.description"));
                                CommonUtils.sendBroadcast(context, Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS, Constants.Code.SUCCESS,
                                              Constants.Status.SUCCESSFUL, result.toString());
                            } catch (JSONException e) {
                                String message = "Result payload build failed.";
                                CommonUtils.sendBroadcast(context, Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS, Constants.Code.FAILURE,
                                              Constants.Status.OTA_IMAGE_VERIFICATION_FAILED, message);
                                Log.e(TAG, message + e);
                            }

                        } else {
                            if (checkNetworkOnline()) {
                                Boolean isAutomaticRetry = (Preference.hasPreferenceKey(context, context.getResources()
                                        .getString(R.string.firmware_upgrade_automatic_retry)) && Preference.getBoolean(context, context.getResources()
                                        .getString(R.string.firmware_upgrade_automatic_retry))) || !Preference.hasPreferenceKey(context, context.getResources()
                                        .getString(R.string.firmware_upgrade_automatic_retry));

                                if (getBatteryLevel(context) >= Constants.REQUIRED_BATTERY_LEVEL_TO_FIRMWARE_UPGRADE) {
                                    otaServerManager.startDownloadUpgradePackage(otaServerManager);
                                } else if (isAutomaticRetry) {
                                    Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                                            Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_DOWNLOAD);
                                    Log.e(TAG, "Upgrade download has been differed due to insufficient battery level.");
                                } else {
                                    String message = "Upgrade download has been failed due to insufficient battery level.";
                                    Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                                            Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_DOWNLOAD);
                                    Log.e(TAG, message);
                                    CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                                            Constants.Status.BATTERY_LEVEL_INSUFFICIENT_TO_DOWNLOAD, message);
                                    CommonUtils.callAgentApp(context, Constants.Operation.FIRMWARE_UPGRADE_FAILURE, 0, message);
                                }
                            } else {
                                String message = "Connection failure when starting upgrade download.";
                                Log.e(TAG, message);
                                CommonUtils.sendBroadcast(context, Constants.Operation.UPGRADE_FIRMWARE, Constants.Code.FAILURE,
                                              Constants.Status.NETWORK_UNREACHABLE, message);
                                CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, message);
                            }
                        }
                    }
                }.execute();

            } else {
                String message = "Connection failure when starting build prop download.";
                Log.e(TAG, message);
                CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.CONNECTION_FAILED, message);
                CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, 0, null);
            }
        } else if (error == ERROR_WIFI_NOT_AVAILABLE) {
            Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status), Constants.Status.WIFI_OFF);
            Log.e(TAG, "OTA failed due to WIFI connection failure.");
        } else if (error == ERROR_CANNOT_FIND_SERVER) {
            String message = "OTA failed due to OTA server not accessible.";
            Log.e(TAG, message);
        } else if (error == ERROR_WRITE_FILE_ERROR) {
            String message = "OTA failed due to file write error.";
            Log.e(TAG, message);
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
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Thread interrupted." + e);
            } finally {
                if (!Preference.getBoolean(context, context.getResources().getString(R.string.verification_failed_flag))) {
                    otaServerManager.startInstallUpgradePackage();
                }
            }
        }
    }

    public void onStateUpgrade(int error) {
        String  operation = Preference.getBoolean(context, context.getResources().getString(R.string.
                firmware_status_check_in_progress)) ? Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS : Constants.Operation.UPGRADE_FIRMWARE;
        if (error == ERROR_PACKAGE_VERIFY_FAILED) {
            String message = "Package verification failed, signature does not match.";
            Log.e(TAG, message);
            Preference.putBoolean(context, context.getResources().getString(R.string.verification_failed_flag), true);
            CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.OTA_IMAGE_VERIFICATION_FAILED, message);
            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, Preference.getInt(
                    context, context.getResources().getString(R.string.operation_id)), message);
        } else if (error == ERROR_PACKAGE_INSTALL_FAILED) {
            String message = "Package installation Failed.";
            Log.e(TAG, message);
            CommonUtils.sendBroadcast(context, operation, Constants.Code.FAILURE, Constants.Status.OTA_IMAGE_INSTALL_FAILED, message);
            CommonUtils.callAgentApp(context, Constants.Operation.FAILED_FIRMWARE_UPGRADE_NOTIFICATION, Preference.getInt(
                    context, context.getResources().getString(R.string.operation_id)), message);
        }
    }

    public void onProgress(Long progress) {
        Log.v(TAG, "Progress : " + progress);
    }

}












