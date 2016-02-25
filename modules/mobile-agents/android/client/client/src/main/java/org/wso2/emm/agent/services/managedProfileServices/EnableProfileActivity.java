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
import android.os.Bundle;

/**
 * This activity is started after the provisioning is complete in {@link ManagedProfileDeviceAdminReceiver}.
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
        /* This is just a friendly shortcut to the main screen.
        setContentView(R.layout.activity_setup);
        findViewById(R.id.icon).setOnClickListener(this);*/
    }

    private void enableProfile() {
        DevicePolicyManager manager =
                (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = ManagedProfileDeviceAdminReceiver.getComponentName(this);
        // This is the name for the newly created managed profile.
        manager.setProfileName(componentName, "WSO2-EMM");
        // We enable the profile here.
        manager.setProfileEnabled(componentName);
    }


}