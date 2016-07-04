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
package org.wso2.emm.agent.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

/**
 * This BroadcastReceiver is registered to receive the notifications from system service app when it's available.
 */
public class SystemServiceResponseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String code = intent.getStringExtra("code");
        String status = intent.getStringExtra("status");
        try {
            JSONObject result = new JSONObject(intent.getStringExtra("payload"));
            switch (code) {
                case Constants.Operation.GET_FIRMWARE_BUILD_DATE:
                    if (Constants.Status.SUCCESSFUL.equals(status) && result != null && result.has("buildDate")) {
                        Preference.putString(context, context.getResources().getString(R.string.shared_pref_os_build_date),
                                             result.getString("buildDate"));
                    }
                    break;
                case Constants.Operation.SILENT_INSTALL_APPLICATION:
                    if (Constants.Status.SUCCESSFUL.equals(status) && result != null && result.has("appInstallStatus")) {
                        Preference.putString(context, context.getResources().getString(R.string.app_install_status),
                                             result.getString("appInstallStatus"));
                    }

                    if (Constants.Status.SUCCESSFUL.equals(status) && result != null && result.has("appInstallFailedMessage")) {
                        Preference.putString(context, context.getResources().getString(R.string.app_install_failed_message),
                                             result.getString("appInstallFailedMessage"));
                    }
                    break;
                default:
                    Log.e(SystemServiceResponseReceiver.class.getName(), "Invalid operation code");
                    break;
            }
        } catch (JSONException e) {
            Log.e(SystemServiceResponseReceiver.class.getName(), "Failed to parse response JSON" + e);
        }
    }
}
