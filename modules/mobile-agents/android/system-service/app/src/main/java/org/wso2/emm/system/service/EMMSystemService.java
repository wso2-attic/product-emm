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

import android.app.IntentService;
import android.app.PackageInstallObserver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;
import org.wso2.emm.system.service.api.OTADownload;
import org.wso2.emm.system.service.api.SettingsManager;
import org.wso2.emm.system.service.utils.Constants;

import java.io.DataOutputStream;
import java.io.IOException;

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
    private static final int DELETE_ALL_USERS = 0x00000002;
    private static final int INSTALL_ALL_USERS = 0x00000040;
    private static final int INSTALL_FORWARD_LOCK = 0x00000001;
    private static final int INSTALL_ALLOW_DOWNGRADE = 0x00000080;
    private static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static ComponentName cdmDeviceAdmin;
    public static DevicePolicyManager devicePolicyManager;
    public static UserManager mUserManager;
    private static boolean restrictionCode = false;
    private String operationCode = null;
    private String command = null;
    private String appUri = null;

    public EMMSystemService() {
        super("EMMSystemService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        cdmDeviceAdmin = new ComponentName(this, ServiceDeviceAdminReceiver.class);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mUserManager = (UserManager) getSystemService(Context.USER_SERVICE);
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
            }

            Log.d(TAG, "EMM agent has sent a command.");
            if ((operationCode != null)) {
                Log.d(TAG, "The operation code is: " + operationCode);

                Log.i(TAG, "Will now executing the command ..." + operationCode);
                if (Constants.AGENT_APP_PACKAGE_NAME.equals(intent.getPackage())) {
                    doTask(operationCode);
                }

                if (extras.containsKey("appUri")) {
                    appUri = extras.getString("appUri");
                }
            }

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
            case Constants.Operation.ENABLE_ADMIN:
                startAdmin();
                break;
            case Constants.Operation.UPGRADE_FIRMWARE:
                upgradeFirmware();
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
                    silentInstallApp(getApplicationContext(), Uri.parse(appUri));
                }
                break;
            case Constants.Operation.SILENT_UNINSTALL_APPLICATION:
                if (appUri != null) {
                    silentUninstallApp(getApplicationContext(), appUri);
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
            //Only With Android M.
            case Constants.Operation.SET_STATUS_BAR_DISABLED:
                SettingsManager.setStatusBarDisabled(restrictionCode);
                break;
            default:
                Log.e(TAG, "Invalid operation code received");
                break;
        }
    }

    /**
     * Upgrading device firmware over the air (OTA).
     */
    public void upgradeFirmware() {
        Log.i(TAG, "An upgrade has been requested");
        Toast.makeText(this, "Upgrade request initiated by admin.",
                       Toast.LENGTH_SHORT).show();
        //Prepare for upgrade
        OTADownload otaDownload = new OTADownload(this.getApplicationContext());
        otaDownload.startOTA();
    }

    /**
     * Rebooting the device.
     */
    private void rebootDevice() {
        Log.i(TAG, "A reboot has been requested");
        Toast.makeText(this, "Reboot request initiated by admin.",
                       Toast.LENGTH_SHORT).show();
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
    private void silentInstallApp(Context context, Uri packageUri) {
        PackageManager packageManager = context.getPackageManager();
        packageManager.installPackage(packageUri, new PackageInstallObserver(), INSTALL_ALL_USERS | INSTALL_FORWARD_LOCK |
                                                                                INSTALL_ALLOW_DOWNGRADE | INSTALL_REPLACE_EXISTING, null);
    }


    /**
     * Silently uninstalls the app resides in the provided URI.
     */
    private void silentUninstallApp(Context context, final String packageName) {
        PackageManager packageManager = context.getPackageManager();
        packageManager.deletePackage(packageName, new IPackageDeleteObserver() {
            @Override
            public void packageDeleted(String s, int i) throws RemoteException {
                Log.i(TAG, "Package " + packageName + " uninstalled successfully.");
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        }, DELETE_ALL_USERS);
    }
}