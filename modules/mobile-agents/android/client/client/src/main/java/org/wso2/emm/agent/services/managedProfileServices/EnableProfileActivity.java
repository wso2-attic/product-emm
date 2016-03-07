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
package org.wso2.emm.agent.services.managedProfileServices;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.wso2.emm.agent.R;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;

/**
 * This activity is started after the provisioning is complete in {@link AgentDeviceAdminReceiver}.
 */
public class EnableProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == savedInstanceState) {
            // Important: After the profile has been created, the MDM must enable it for corporate
            // apps to become visible in the launcher.
            enableProfile();
        }
        /* This is just a friendly shortcut to the main screen.*/
        setContentView(R.layout.activity_already_registered);
        //findViewById(R.id.icon).setOnClickListener(this);
    }

    private void enableProfile() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = AgentDeviceAdminReceiver.getComponentName(this);
        // This is the name for the newly created managed profile.
        manager.setProfileName(componentName, "WSO2-EMM");
        // We enable the profile here.
        manager.setProfileEnabled(componentName);
        setAppEnabled("com.android.providers.settings",true);
    }

    /**
     * Enables or disables the specified app in this profile.
     *
     * @param packageName The package name of the target app.
     * @param enabled     Pass true to enable the app.
     */
    public void setAppEnabled(String packageName, boolean enabled) {
        PackageManager packageManager = this.getPackageManager();
        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            // Here, we check the ApplicationInfo of the target app, and see if the flags have
            // ApplicationInfo.FLAG_INSTALLED turned on using bitwise operation.
            if (0 == (applicationInfo.flags & ApplicationInfo.FLAG_INSTALLED)) {
                // If the app is not installed in this profile, we can enable it by
                // DPM.enableSystemApp
                if (enabled) {
                    devicePolicyManager.enableSystemApp(
                            AgentDeviceAdminReceiver.getComponentName(this), packageName);

                } else {
                    // But we cannot disable the app since it is already disabled
                    //Log.e(TAG, "Cannot disable this app: " + packageName);
                    return;
                }
            } else {
                // If the app is already installed, we can enable or disable it by
                // DPM.setApplicationHidden
                devicePolicyManager.setApplicationHidden(
                        AgentDeviceAdminReceiver.getComponentName(this), packageName, !enabled);
            }
            Toast.makeText(this, enabled ? "enabled" : "disabled",
                    Toast.LENGTH_SHORT).show();
        } catch (PackageManager.NameNotFoundException e) {
            //Log.e(TAG, "The app cannot be found: " + packageName, e);
        }
    }

}