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
package org.wso2.emm.system.service.utils;

import android.app.PackageInstallObserver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to hold app operations.
 */
public class AppUtils {

    private static final String TAG = "AppUtils";
    private static final int DELETE_ALL_USERS = 0x00000002;
    private static final int INSTALL_ALL_USERS = 0x00000040;
    private static final int INSTALL_FORWARD_LOCK = 0x00000001;
    private static final int INSTALL_ALLOW_DOWNGRADE = 0x00000080;
    private static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_SUCCEEDED = 1;
    private static final int DEFAULT_STATE_INFO_CODE = 0;
    private static final String INSTALL_FAILED_STATUS = "INSTALL_FAILED";
    private static final String INSTALL_SUCCESS_STATUS = "INSTALLED";

    /**
     * Silently installs the app resides in the provided URI.
     * @param context - Application context.
     * @param  packageUri - App package URI.
     */
    public static void silentInstallApp(final Context context, Uri packageUri) {
        PackageManager pm = context.getPackageManager();
        PackageInstallObserver observer = new PackageInstallObserver() {
            @Override
            public void onPackageInstalled(String basePackageName, int returnCode, String msg, Bundle extras) {
                if (INSTALL_SUCCEEDED == returnCode) {
                    publishAppInstallStatus(context, INSTALL_SUCCESS_STATUS, null);
                } else {
                    publishAppInstallStatus(context, INSTALL_FAILED_STATUS, "Package installation failed due to an " +
                                                                            "internal error with code " + returnCode + " " +
                                                                            "and message " + msg);
                }
            }
        };
        pm.installPackage(packageUri, observer, INSTALL_ALL_USERS | INSTALL_FORWARD_LOCK | INSTALL_ALLOW_DOWNGRADE |
                                           INSTALL_REPLACE_EXISTING, null);
    }

    /**
     * Silently uninstalls the app resides in the provided URI.
     * @param context - Application context.
     * @param  packageName - App package name.
     */
    public static void silentUninstallApp(Context context, final String packageName) {
        PackageManager pm = context.getPackageManager();
        IPackageDeleteObserver observer = new IPackageDeleteObserver() {
            @Override
            public void packageDeleted(String s, int i) throws RemoteException {
                Log.d(TAG, packageName + " deleted successfully.");
            }

            @Override
            public IBinder asBinder() {
                return null;
            }
        };
        pm.deletePackage(packageName, observer, DELETE_ALL_USERS);
    }

    private static void publishAppInstallStatus(Context context, String status, String error) {
        JSONObject result = new JSONObject();

        try {
            result.put("appInstallStatus", status);
            if (error != null) {
                result.put("appInstallFailedMessage", error);
            }
            CommonUtils.sendBroadcast(context, Constants.Operation.SILENT_INSTALL_APPLICATION, Constants.Code.SUCCESS, Constants.Status.SUCCESSFUL,
                          result.toString());
        } catch (JSONException e) {
            Log.e(TAG, "Failed to create JSON object when publishing App install status.");
            CommonUtils.sendBroadcast(context, Constants.Operation.SILENT_INSTALL_APPLICATION, Constants.Code.FAILURE, Constants.Status.INTERNAL_ERROR,
                          String.valueOf(DEFAULT_STATE_INFO_CODE));
        }
    }

}
