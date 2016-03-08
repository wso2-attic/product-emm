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

package org.wso2.emm.agent;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.wso2.emm.agent.api.DeviceState;

public class LanderM extends Activity {
    private Context context;
    private Button btnEnableMngProfile;
    private Button btnSkipProfile;
    private static final int TAG_BTN_ENABLE_PROFILE = 0;
    private static final int TAG_BTN_SKIP_PROFILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        DevicePolicyManager manager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        if (manager.isProfileOwnerApp(getApplicationContext().getPackageName())) {
            // If the managed profile is already set up, we show the main screen.
            skipToEnrollment();
        }
        else {
            setContentView(R.layout.activity_enable_work_profile);
            btnEnableMngProfile = (Button) findViewById(R.id.btnSetupWorkProfile);
            btnEnableMngProfile.setTag(TAG_BTN_ENABLE_PROFILE);
            btnEnableMngProfile.setOnClickListener(onClickListenerButtonClicked);

            btnSkipProfile = (Button) findViewById(R.id.btnSkipProfile);
            btnSkipProfile.setTag(TAG_BTN_SKIP_PROFILE);
            btnSkipProfile.setOnClickListener(onClickListenerButtonClicked);
        }
    }

    private View.OnClickListener onClickListenerButtonClicked = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int iTag = (Integer) view.getTag();

            switch (iTag) {

                case TAG_BTN_ENABLE_PROFILE:
                    startManagedProfileManager();
                    finish();
                    break;

                case TAG_BTN_SKIP_PROFILE:
                    skipToEnrollment();
                    break;

                default:
                    break;
            }

        }
    };

    /**
     * Start WorkProfileManager which configures Android Managed Profile Feature
     */
    private void startManagedProfileManager(){
        Intent ManagedProfileManager = new Intent(getApplicationContext(), WorkProfileManager.class);
        startActivity(ManagedProfileManager);
    }

    private void skipToEnrollment(){
        Intent intent = new Intent(context, ServerDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
