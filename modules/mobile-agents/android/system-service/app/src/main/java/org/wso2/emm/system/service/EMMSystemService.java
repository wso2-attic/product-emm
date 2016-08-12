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

package org.wso2.emm.system.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.os.UserManager;
import android.util.Log;
import android.util.Patterns;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.system.service.api.OTADownload;
import org.wso2.emm.system.service.api.SettingsManager;
import org.wso2.emm.system.service.services.BatteryChargingStateReceiver;
import org.wso2.emm.system.service.utils.AlarmUtils;
import org.wso2.emm.system.service.utils.AppUtils;
import org.wso2.emm.system.service.utils.CommonUtils;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.Preference;

import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.UserManager.ALLOW_PARENT_PROFILE_APP_LINKING;
import static android.os.UserManager.DISALLOW_ADD_USER;
import static android.os.UserManager.DISALLOW_ADJUST_VOLUME;
import static android.os.UserManager.DISALLOW_APPS_CONTROL;
import static android.os.UserManager.DISALLOW_CONFIG_BLUETOOTH;
import static android.os.UserManager.DISALLOW_CONFIG_CELL_BROADCASTS;
import static android.os.UserManager.DISALLOW_CONFIG_CREDENTIALS;
import static android.os.UserManager.DISALLOW_CONFIG_MOBILE_NETWORKS;
import static android.os.UserManager.DISALLOW_CONFIG_TETHERING;
import static android.os.UserManager.DISALLOW_CONFIG_VPN;
import static android.os.UserManager.DISALLOW_CONFIG_WIFI;
import static android.os.UserManager.DISALLOW_CREATE_WINDOWS;
import static android.os.UserManager.DISALLOW_CROSS_PROFILE_COPY_PASTE;
import static android.os.UserManager.DISALLOW_DEBUGGING_FEATURES;
import static android.os.UserManager.DISALLOW_FACTORY_RESET;
import static android.os.UserManager.DISALLOW_INSTALL_APPS;
import static android.os.UserManager.DISALLOW_INSTALL_UNKNOWN_SOURCES;
import static android.os.UserManager.DISALLOW_MODIFY_ACCOUNTS;
import static android.os.UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA;
import static android.os.UserManager.DISALLOW_NETWORK_RESET;
import static android.os.UserManager.DISALLOW_OUTGOING_BEAM;
import static android.os.UserManager.DISALLOW_OUTGOING_CALLS;
import static android.os.UserManager.DISALLOW_REMOVE_USER;
import static android.os.UserManager.DISALLOW_SAFE_BOOT;
import static android.os.UserManager.DISALLOW_SHARE_LOCATION;
import static android.os.UserManager.DISALLOW_SMS;
import static android.os.UserManager.DISALLOW_UNINSTALL_APPS;
import static android.os.UserManager.DISALLOW_UNMUTE_MICROPHONE;
import static android.os.UserManager.DISALLOW_USB_FILE_TRANSFER;
import static android.os.UserManager.ENSURE_VERIFY_APPS;

/**
 * This is the service class which exposes all the system level operations
 * to the EMM Agent app. Agent can bind to this service and execute permitted operations by
 * sending necessary parameters.
 */
public class EMMSystemService extends IntentService {

    private static final String TAG = "EMMSystemService";
    private static final int ACTIVATION_REQUEST = 0x00000002;
    private static final String BUILD_DATE_UTC_PROPERTY = "ro.build.date.utc";
    private static final int DEFAULT_STATE_INFO_CODE = 0;
    public static ComponentName cdmDeviceAdmin;
    public static DevicePolicyManager devicePolicyManager;
    public static UserManager mUserManager;
    private static boolean restrictionCode = false;
    private String operationCode = null;
    private String command = null;
    private String appUri = null;
    private int operationId;
    private Context context;

    private static String[] AUTHORIZED_PINNING_APPS;
    private static String AGENT_PACKAGE_NAME;

    public EMMSystemService() {
        super("EMMSystemService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context = this.getApplicationContext();
        cdmDeviceAdmin = new ComponentName(this, ServiceDeviceAdminReceiver.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);
        AGENT_PACKAGE_NAME = context.getPackageName();
        AUTHORIZED_PINNING_APPS = new String[]{AGENT_PACKAGE_NAME, Constants.AGENT_APP_PACKAGE_NAME};
        if (!devicePolicyManager.isAdminActive(cdmDeviceAdmin)) {
            startAdmin();
        } else {
        /*This function handles the "Execute Command on Device" Operation.
        All requests are handled on a single worker thread. They may take as long as necessary
		(and will not block the application's main thread),
		but only one request will be processed at a time.*/
            Log.d(TAG, "Entered onHandleIntent of the Command Runner Service.");
            Bundle extras = intent.getExtras();
            if (extras != null) {
                operationCode = extras.getString("code");

                if (extras.containsKey("command")) {
                    command = extras.getString("command");
                    if (command != null && (command.equals("true") || command.equals("false"))) {
                        if (command.equals("true")) {
                            restrictionCode = true;
                        }
                    }
                }

                if (extras.containsKey("appUri")) {
                    appUri = extras.getString("appUri");
                }

                if (extras.containsKey("operationId")) {
                    operationId = extras.getInt("operationId");
                }
            }

            Log.d(TAG, "EMM agent has sent a command.");
            if ((operationCode != null)) {
                Log.d(TAG, "The operation code is: " + operationCode);

                Log.i(TAG, "Will now executing the command ..." + operationCode);
                if (Constants.AGENT_APP_PACKAGE_NAME.equals(intent.getPackage())) {
                    doTask(operationCode);
                } else if (Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS.equals(operationCode)) {
                    doTask(operationCode);
                }
            }
        }
        context.registerReceiver(new BatteryChargingStateReceiver(), new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        //Checking is there any interrupted firmware download is there
        String status = Preference.getString(context, context.getResources().getString(R.string.upgrade_download_status));
        if (context.getResources().getString(R.string.status_started).equals(status)) {
            Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                    context.getResources().getString(R.string.status_init));
            Timer timeoutTimer = new Timer();
            timeoutTimer.schedule(new TimerTask(){
                @Override
                public void run() {
                    if (context.getResources().getString(R.string.status_init)
                            .equals(Preference.getString(context, context.getResources().getString(R.string.upgrade_download_status)))) {
                        if (Preference.getBoolean(context, context.getResources().getString(R.string.automatic_firmware_upgrade))) {
                            Log.i(TAG, "Found incomplete firmware download. Proceeding with last download request from the agent.");
                            OTADownload otaDownload = new OTADownload(context);
                            otaDownload.startOTA();
                        }
                    }
                }
            }, Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
        }
    }

    private void startAdmin() {
        Intent intentDeviceAdmin = new Intent(this, MainActivity.class);
        intentDeviceAdmin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentDeviceAdmin);
    }

    /**
     * Executes device management operations on the device.
     *
     * @param code - Operation object.
     */
    public void doTask(String code) {
        switch (code) {
            case Constants.Operation.DEVICE_LOCK:
                enableHardLock();
                break;
            case Constants.Operation.DEVICE_UNLOCK:
                disableHardLock();
                break;
            case Constants.Operation.ENABLE_ADMIN:
                startAdmin();
                break;
            case Constants.Operation.UPGRADE_FIRMWARE:
                upgradeFirmware(false);
                break;
            case Constants.Operation.REBOOT:
                rebootDevice();
                break;
            case Constants.Operation.EXECUTE_SHELL_COMMAND:
                if (command != null) {
                    executeShellCommand(command);
                }
                break;
            case Constants.Operation.SILENT_INSTALL_APPLICATION:
                if (appUri != null) {
                    silentInstallApp(getApplicationContext(), appUri, command);
                }
                break;
            case Constants.Operation.SILENT_UPDATE_APPLICATION:
                if (appUri != null) {
                    silentInstallApp(getApplicationContext(), appUri, command);
                }
                break;
            case Constants.Operation.SILENT_UNINSTALL_APPLICATION:
                if (appUri != null) {
                    silentUninstallApp(getApplicationContext(), appUri, command);
                }
                break;
            case Constants.Operation.REMOVE_DEVICE_OWNER:
                SettingsManager.clearDeviceOwner();
                break;
            case Constants.Operation.DISALLOW_ADJUST_VOLUME:
                SettingsManager.restrict(DISALLOW_ADJUST_VOLUME, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_ADD_USER:
                SettingsManager.restrict(DISALLOW_ADD_USER, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_APPS_CONTROL:
                SettingsManager.restrict(DISALLOW_APPS_CONTROL, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_BLUETOOTH:
                SettingsManager.restrict(DISALLOW_CONFIG_BLUETOOTH, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_CELL_BROADCASTS:
                SettingsManager.restrict(DISALLOW_CONFIG_CELL_BROADCASTS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_CREDENTIALS:
                SettingsManager.restrict(DISALLOW_CONFIG_CREDENTIALS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_MOBILE_NETWORKS:
                SettingsManager.restrict(DISALLOW_CONFIG_MOBILE_NETWORKS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_TETHERING:
                SettingsManager.restrict(DISALLOW_CONFIG_TETHERING, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_VPN:
                SettingsManager.restrict(DISALLOW_CONFIG_VPN, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CONFIG_WIFI:
                SettingsManager.restrict(DISALLOW_CONFIG_WIFI, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CREATE_WINDOWS:
                SettingsManager.restrict(DISALLOW_CREATE_WINDOWS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_CROSS_PROFILE_COPY_PASTE:
                SettingsManager.restrict(DISALLOW_CROSS_PROFILE_COPY_PASTE, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_DEBUGGING_FEATURES:
                SettingsManager.restrict(DISALLOW_DEBUGGING_FEATURES, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_FACTORY_RESET:
                SettingsManager.restrict(DISALLOW_FACTORY_RESET, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_INSTALL_APPS:
                SettingsManager.restrict(DISALLOW_INSTALL_APPS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_INSTALL_UNKNOWN_SOURCES:
                SettingsManager.restrict(DISALLOW_INSTALL_UNKNOWN_SOURCES, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_MODIFY_ACCOUNTS:
                SettingsManager.restrict(DISALLOW_MODIFY_ACCOUNTS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_MOUNT_PHYSICAL_MEDIA:
                SettingsManager.restrict(DISALLOW_MOUNT_PHYSICAL_MEDIA, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_NETWORK_RESET:
                SettingsManager.restrict(DISALLOW_NETWORK_RESET, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_OUTGOING_BEAM:
                SettingsManager.restrict(DISALLOW_OUTGOING_BEAM, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_OUTGOING_CALLS:
                SettingsManager.restrict(DISALLOW_OUTGOING_CALLS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_REMOVE_USER:
                SettingsManager.restrict(DISALLOW_REMOVE_USER, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_SAFE_BOOT:
                SettingsManager.restrict(DISALLOW_SAFE_BOOT, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_SHARE_LOCATION:
                SettingsManager.restrict(DISALLOW_SHARE_LOCATION, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_SMS:
                SettingsManager.restrict(DISALLOW_SMS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_UNINSTALL_APPS:
                SettingsManager.restrict(DISALLOW_UNINSTALL_APPS, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_UNMUTE_MICROPHONE:
                SettingsManager.restrict(DISALLOW_UNMUTE_MICROPHONE, restrictionCode);
                break;
            case Constants.Operation.DISALLOW_USB_FILE_TRANSFER:
                SettingsManager.restrict(DISALLOW_USB_FILE_TRANSFER, restrictionCode);
                break;
            case Constants.Operation.ENSURE_VERIFY_APPS:
                SettingsManager.restrict(ENSURE_VERIFY_APPS, restrictionCode);
                break;
            case Constants.Operation.ALLOW_PARENT_PROFILE_APP_LINKING:
                SettingsManager.restrict(ALLOW_PARENT_PROFILE_APP_LINKING, restrictionCode);
                break;
            case Constants.Operation.AUTO_TIME:
                SettingsManager.setAutoTimeRequired(restrictionCode);
                break;
            case Constants.Operation.SET_SCREEN_CAPTURE_DISABLED:
                SettingsManager.setScreenCaptureDisabled(restrictionCode);
                break;
            case Constants.Operation.APP_RESTRICTION:
                if (command != null && (command.equals("true") || command.equals("false"))) {
                    SettingsManager.setVisibilityOfApp(appUri, Boolean.parseBoolean(command));
                }
                break;
            //Only With Android M.
            case Constants.Operation.SET_STATUS_BAR_DISABLED:
                SettingsManager.setStatusBarDisabled(restrictionCode);
                break;
            case Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS:
                Preference.putBoolean(context, context.getResources().getString(R.string.
                                                                                        firmware_status_check_in_progress), true);
                upgradeFirmware(true);
                break;
            case Constants.Operation.WIPE_DATA:
                try {
                    Runtime.getRuntime().exec("sh");
                    Runtime.getRuntime().exec("am broadcast -a android.intent.action.MASTER_CLEAR");
                } catch (IOException e) {
                    Log.e("TAG", "Shell command execution failed." + e);
                }
                break;
            case Constants.Operation.GET_FIRMWARE_UPGRADE_DOWNLOAD_PROGRESS:
                publishFirmwareDownloadProgress();
                break;
            case Constants.Operation.GET_FIRMWARE_BUILD_DATE:
                publishFirmwareBuildDate();
                break;
            default:
                Log.e(TAG, "Invalid operation code received");
                break;
        }
    }

    /**
     * Upgrading device firmware over the air (OTA).
     */
    public void upgradeFirmware(final boolean isStatusCheck) {
        Log.i(TAG, "An upgrade has been requested");

        Preference.putBoolean(context, context.getResources().getString(R.string.
                                                                                firmware_status_check_in_progress), isStatusCheck);
        Preference.putString(context, context.getResources().getString(R.string.firmware_download_progress),
                             String.valueOf(DEFAULT_STATE_INFO_CODE));
        Preference.putInt(context, context.getResources().getString(R.string.operation_id), operationId);

        String schedule = null;
        String server;
        if (command != null && !command.trim().isEmpty()) {
            try {
                JSONObject upgradeData = new JSONObject(command);
                if (!upgradeData.isNull(context.getResources().getString(R.string.alarm_schedule))) {
                    schedule = (String) upgradeData.get(context.getResources().getString(R.string.alarm_schedule));
                }
                upgradeData.put(context.getResources().getString(R.string.automatic_firmware_upgrade), "false");
                boolean isAutomaticUpgrade = true;
                if (!upgradeData.isNull(context.getResources().getString(R.string.automatic_firmware_upgrade))) {
                    isAutomaticUpgrade = !"false".equals(upgradeData.get(context.getResources()
                            .getString(R.string.automatic_firmware_upgrade)));
                    Log.i(TAG, "Automatic update disabled.");
                }

                Preference.putBoolean(context, context.getResources()
                        .getString(R.string.automatic_firmware_upgrade), isAutomaticUpgrade);

                if (!upgradeData.isNull(context.getResources().getString(R.string.firmware_server))) {
                    server = (String) upgradeData.get(context.getResources().getString(R.string.firmware_server));
                    if(server.isEmpty() || (!server.isEmpty() && !Patterns.WEB_URL.matcher(server).matches())) {
                        String message = "Firmware upgrade URL provided is not valid.";
                        sendBroadcast(Constants.Operation.GET_FIRMWARE_UPGRADE_PACKAGE_STATUS,
                                Constants.Status.MALFORMED_OTA_URL, message);
                        CommonUtils.callAgentApp(context, Constants.Operation.
                                FIRMWARE_UPGRADE_FAILURE, Preference.getInt(
                                context, context.getResources().getString(R.string.operation_id)), message);
                        Log.e(TAG, message);
                        return;
                    } else {
                        Preference.putString(context, context.getResources().getString(R.string.firmware_server), server);
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Firmware upgrade payload parsing failed." + e);
            }
        }
        if (schedule != null && !schedule.trim().isEmpty()) {
            Log.i(TAG, "Upgrade scheduled received: " + schedule);
            Preference.putString(context, context.getResources().getString(R.string.alarm_schedule), schedule);
            try {
                AlarmUtils.setOneTimeAlarm(context, schedule, Constants.Operation.UPGRADE_FIRMWARE, null);
            } catch (ParseException e) {
                Log.e(TAG, "One time alarm time string parsing failed." + e);
            }
        } else {
            if (isStatusCheck) {
                Log.i(TAG, "Firmware status check is initiated by admin.");
            } else {
                Log.i(TAG, "Upgrade request initiated by admin.");
            }

            String status = Preference.getString(context, context.getResources().getString(R.string.upgrade_download_status));
            if (context.getResources().getString(R.string.status_connectivity_failed).equals(status)) {
                Log.d(TAG, "Ignoring request from agent as service waiting for WiFi to start upgrade.");
                return;
            } else if (context.getResources().getString(R.string.status_started).equals(status)) {
                Log.d(TAG, "Checking for existing download. Will proceed this request if current download is no longer ongoing.");
                Preference.putString(context, context.getResources().getString(R.string.upgrade_download_status),
                        context.getResources().getString(R.string.status_init));
                Timer timeoutTimer = new Timer();
                timeoutTimer.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        if (context.getResources().getString(R.string.status_init)
                                .equals(Preference.getString(context, context.getResources().getString(R.string.upgrade_download_status)))) {
                            Log.d(TAG, "Download is no longer ongoing. Proceeding download request from the agent.");
                            OTADownload otaDownload = new OTADownload(context);
                            otaDownload.startOTA();
                        }
                    }
                }, Constants.FIRMWARE_UPGRADE_READ_TIMEOUT);
                return;
            }

            //Prepare for upgrade
            OTADownload otaDownload = new OTADownload(context);
            otaDownload.startOTA();
        }
    }

    /**
     * Rebooting the device.
     */
    private void rebootDevice() {
        Log.i(TAG, "Reboot request initiated by admin.");
        try {
            Thread.sleep(5000);
            PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
            powerManager.reboot(null);
        } catch (InterruptedException e) {
            Log.e(TAG, "Reboot initiating thread interrupted." + e);
        }
    }

    /**
     * Executing shell commands as super user.
     */
    private void executeShellCommand(String command) {
        Process process;
        try {
            process = Runtime.getRuntime().exec("sh");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataOutputStream.writeBytes("am start " + command + "\\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
        } catch (IOException e) {
            Log.e(TAG, "Shell command execution failed." + e);
        }
    }

    /**
     * Silently installs the app resides in the provided URI.
     */
    private void silentInstallApp(Context context, String packageUri, String schedule) {
        if (schedule != null && !schedule.trim().isEmpty() && !schedule.equals("undefined")) {
            Log.i(TAG, "Silent install has been scheduled to " + schedule);
            Preference.putString(context, context.getResources().getString(R.string.alarm_schedule), schedule);
            Preference.putString(context, context.getResources().getString(R.string.app_uri), packageUri);
            try {
                AlarmUtils.setOneTimeAlarm(context, schedule, Constants.Operation.SILENT_INSTALL_APPLICATION, packageUri);
            } catch (ParseException e) {
                Log.e(TAG, "One time alarm time string parsing failed." + e);
            }
        } else {
            AppUtils.silentInstallApp(context, Uri.parse(packageUri));
        }
    }

    /**
     * Silently uninstalls the app resides in the provided URI.
     */
    private void silentUninstallApp(Context context, final String packageName, String schedule) {
        if (schedule != null && !schedule.trim().isEmpty() && !schedule.equals("undefined")) {
            Log.i(TAG, "Silent install has been scheduled to " + schedule);
            Preference.putString(context, context.getResources().getString(R.string.alarm_schedule), schedule);
            Preference.putString(context, context.getResources().getString(R.string.app_uri), packageName);
            try {
                AlarmUtils.setOneTimeAlarm(context, schedule, Constants.Operation.SILENT_UNINSTALL_APPLICATION, packageName);
            } catch (ParseException e) {
                Log.e(TAG, "One time alarm time string parsing failed." + e);
            }
        } else {
            AppUtils.silentUninstallApp(context, packageName);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enableHardLock() {
        String message = context.getResources().getString(R.string.txt_lock_activity);
        if (appUri != null && !appUri.isEmpty()) {
            message = appUri;
        }
        if (SettingsManager.isDeviceOwner()) {
            devicePolicyManager.setLockTaskPackages(cdmDeviceAdmin, AUTHORIZED_PINNING_APPS);
            Intent intent = new Intent(context, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            intent.putExtra(Constants.ADMIN_MESSAGE, message);
            intent.putExtra(Constants.IS_LOCKED, true);
            context.startActivity(intent);
        } else {
            Log.e(TAG, "Device owner is not set, hence executing default lock");
            devicePolicyManager.lockNow();
        }
    }

    private void publishFirmwareDownloadProgress() {
        long progress;
        JSONObject result = new JSONObject();
        if (Preference.getString(context, context.getResources().getString(R.string.firmware_download_progress)) != null) {
            progress = Long.valueOf(Preference.getString(context, context.getResources().getString(
                    R.string.firmware_download_progress)));
        } else {
            progress = DEFAULT_STATE_INFO_CODE;
        }
        try {
            result.put("progress", String.valueOf(progress));
            sendBroadcast(Constants.Operation.GET_FIRMWARE_UPGRADE_DOWNLOAD_PROGRESS, Constants.Status.SUCCESSFUL,
                          result.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON object when publishing OTA progress.");
            sendBroadcast(Constants.Operation.GET_FIRMWARE_UPGRADE_DOWNLOAD_PROGRESS, Constants.Status.SUCCESSFUL,
                          String.valueOf(DEFAULT_STATE_INFO_CODE));
        }
    }

    private void sendBroadcast(String code, String status, String payload) {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(Constants.SYSTEM_APP_ACTION_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(Constants.CODE, code);
        broadcastIntent.putExtra(Constants.STATUS, status);
        broadcastIntent.putExtra(Constants.PAYLOAD, payload);
        context.sendBroadcastAsUser(broadcastIntent, android.os.Process.myUserHandle());
    }

    private void disableHardLock() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void publishFirmwareBuildDate() {
        String buildDate;
        JSONObject result = new JSONObject();

        buildDate = SystemProperties.get(BUILD_DATE_UTC_PROPERTY);
        try {
            result.put("buildDate", buildDate);
            sendBroadcast(Constants.Operation.GET_FIRMWARE_BUILD_DATE, Constants.Status.SUCCESSFUL,
                          result.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON object when publishing OTA progress.");
            sendBroadcast(Constants.Operation.GET_FIRMWARE_BUILD_DATE, Constants.Status.SUCCESSFUL,
                          String.valueOf(DEFAULT_STATE_INFO_CODE));
        }
    }

}