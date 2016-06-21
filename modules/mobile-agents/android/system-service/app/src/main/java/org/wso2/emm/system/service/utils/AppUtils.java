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

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    /**
     * Silently installs the app resides in the provided URI.
     * @param context - Application context.
     * @param  packageUri - App package URI.
     */
    public static void silentInstallApp(Context context, Uri packageUri) {
        PackageManager pm = context.getPackageManager();
        Class<? extends PackageManager> packageManager = pm.getClass();
        Method[] allMethods = packageManager.getMethods();
        for (Method method : allMethods) {
            if (method.getName().equals("installPackage")) {
                Log.d(TAG, "Installing the app.");
                try {
                    method.invoke(
                            pm,
                            new Object[]{
                                    packageUri,
                                    null,
                                    INSTALL_ALL_USERS | INSTALL_FORWARD_LOCK | INSTALL_ALLOW_DOWNGRADE |
                                            INSTALL_REPLACE_EXISTING,
                                    null});
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Access denied by PackageManager." + e);
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "Installation method not found." + e);
                }

                break;
            }
        }
    }


    /**
     * Silently uninstalls the app resides in the provided URI.
     * @param context - Application context.
     * @param  packageName - App package name.
     */
    public static void silentUninstallApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        Class<? extends PackageManager> packageManager = pm.getClass();
        Method[] allMethods = packageManager.getMethods();

        for (Method method : allMethods) {
            if (method.getName().equals("deletePackage")) {
                Log.d(TAG, "Removing the app.");
                try {
                    method.invoke(pm, new Object[]{packageName, null, DELETE_ALL_USERS});
                } catch (IllegalAccessException e) {
                    Log.e(TAG, "Access denied by PackageManager." + e);
                } catch (InvocationTargetException e) {
                    Log.e(TAG, "Installation method not found." + e);
                }
                break;
            }
        }
    }
}
