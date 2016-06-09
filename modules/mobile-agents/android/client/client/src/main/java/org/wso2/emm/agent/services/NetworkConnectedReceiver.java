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
package org.wso2.emm.agent.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

public class NetworkConnectedReceiver extends BroadcastReceiver {
    private static final String FRESH_BOOTUP_FLAG = "fresh_bootup";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!Preference.getBoolean(context, FRESH_BOOTUP_FLAG))	{
            if (!Preference.getBoolean(context, Constants.PreferenceFlag.REGISTERED) && CommonUtils.
                    isNetworkAvailable(context)) {
                if (Constants.AUTO_ENROLLMENT_ENABLED) {
                    Preference.putBoolean(context, FRESH_BOOTUP_FLAG, true);
                    Intent autoEnrollIntent = new Intent(context, EnrollmentService.class);
                    autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(autoEnrollIntent);
                }
            }
        }
    }
}
