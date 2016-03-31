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

package org.wso2.emm.system.service.api;

import android.os.Build;
import android.util.Log;
import org.wso2.emm.system.service.BuildConfig;
import org.wso2.emm.system.service.EMMSystemService;

public class SettingsManager {
    private static final String TAG = SettingsManager.class.getName();

    public static void makeDeviceOwner() {
        EMMSystemService.devicePolicyManager.setDeviceOwner(BuildConfig.APPLICATION_ID);
        Log.i(TAG, "enabled device owner");
    }

    public static void clearDeviceOwner() {
        Log.i(TAG, "disabled device owner");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EMMSystemService.devicePolicyManager.clearDeviceOwnerApp(BuildConfig.APPLICATION_ID);
        }
    }

    public static boolean restrict(String code, boolean state) {
        Log.d(TAG, "Restriction :" + code + " , set to:" + state);
        boolean restrictionState = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
            EMMSystemService.devicePolicyManager != null) {
            if (EMMSystemService.devicePolicyManager.isDeviceOwnerApp(BuildConfig.APPLICATION_ID)) {
                if (state) {
                    EMMSystemService.devicePolicyManager.
                            addUserRestriction(EMMSystemService.cdmDeviceAdmin, code);
                } else {
                    EMMSystemService.devicePolicyManager.
                            clearUserRestriction(EMMSystemService.cdmDeviceAdmin, code);
                }
                restrictionState = true;
            } else {
                Log.i(TAG, "Not the device owner.");
            }
        }
        return restrictionState;
    }

    public static void setScreenCaptureDisabled(boolean value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EMMSystemService.devicePolicyManager.setScreenCaptureDisabled(EMMSystemService.cdmDeviceAdmin, value);
        }
    }

    public static void setStatusBarDisabled(boolean value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EMMSystemService.devicePolicyManager.setStatusBarDisabled(EMMSystemService.cdmDeviceAdmin, value);
        }
    }

    public static void setAutoTimeRequired(boolean value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            EMMSystemService.devicePolicyManager.setAutoTimeRequired(EMMSystemService.cdmDeviceAdmin, value);
        }
    }

}
