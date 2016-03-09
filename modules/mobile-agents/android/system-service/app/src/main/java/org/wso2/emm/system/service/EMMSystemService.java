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
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;
import org.wso2.emm.system.service.api.OTADownload;
import org.wso2.emm.system.service.utils.Constants;

import java.io.DataOutputStream;
import java.io.IOException;

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
    private String operationCode = null;
    private String shellCommand = null;
    private String appUri = null;

    public EMMSystemService() {
        super("EMMSystemService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

		/*This function handles the "Execute Command on Device" Operation.
		All requests are handled on a single worker thread. They may take as long as necessary
		(and will not block the application's main thread), but only one request will be processed at a time.*/

        Log.d(TAG, "Entered onHandleIntent of the Command Runner Service.");
        Bundle extras = intent.getExtras();
        if (extras != null) {
            operationCode = extras.getString("code");

            if (extras.containsKey("command")) {
                shellCommand = extras.getString("command");
            }
        }

        Log.d(TAG, "EMM agent has sent a command.");
        if ((operationCode != null)) {
            Log.d(TAG, "The operation code is: " + operationCode);

            Log.i(TAG, "Will now executing the command ..." + operationCode);
            //Log.i(TAG, "The serial Number for current user is:" + ActivityManager.getCurrentUser());
            if (Constants.AGENT_APP_PACKAGE_NAME.equals(intent.getPackage())) {
                doTask(operationCode);
            }
        }

    }

    /**
     * Executes device management operations on the device.
     *
     * @param code - Operation object.
     */
    public void doTask(String code) {
        switch (code) {
            case Constants.Operation.UPGRADE_FIRMWARE:
                upgradeFirmware();
                break;
            case Constants.Operation.REBOOT:
                rebootDevice();
                break;
            case Constants.Operation.EXECUTE_SHELL_COMMAND:
                if (shellCommand != null) {
                    executeShellCommand(shellCommand);
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
            Runtime.getRuntime().exec("su -c reboot");
        } catch (InterruptedException e) {
            Log.e(TAG, "Reboot initiating thread interrupted." + e);
        } catch (IOException e) {
            Log.e(TAG, "Reboot interrupted." + e);
        }
        /*PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        powerManager.reboot(null);*/
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