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
package org.wso2.emm.agent;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import static android.app.admin.DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE;
import static android.app.admin.DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME;

public class WorkProfileManager extends Activity {

    private static final int REQUEST_PROVISION_MANAGED_PROFILE = 1;
    private static final String TAG = "WorkProfileManager";
    private Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        provisionManagedProfile();
    }

    private void provisionManagedProfile() {

        Intent intent = new Intent(ACTION_PROVISION_MANAGED_PROFILE);
        intent.putExtra(EXTRA_PROVISIONING_DEVICE_ADMIN_PACKAGE_NAME, Constants.AGENT_PACKAGE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_PROVISION_MANAGED_PROFILE);
        } else {
            Toast.makeText(activity, "Device provisioning is not enabled. Stopping.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PROVISION_MANAGED_PROFILE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(this, "Provisioning done.", Toast.LENGTH_SHORT).show();

                // Once the provisioning is done, user is prompted to uninstall the agent in personal profile.
                ApplicationManager applicationManager = new ApplicationManager(this.getApplicationContext());
                applicationManager.uninstallApplication(Constants.PACKAGE_NAME, null);

                //~GM~ save profile created status in shared preferences -: success
                Preference.putBoolean(this,Constants.PreferenceFlag.PROFILE_CREATED,true);

            } else {
                //~GM~ save profile created status in shared preferences -: failed
                Toast.makeText(this, "Provisioning failed.", Toast.LENGTH_SHORT).show();
                Preference.putBoolean(this,Constants.PreferenceFlag.PROFILE_CREATED,false);

            }

            finish();

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
