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

package org.wso2.emm.system.service;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceDeviceAdminReceiver extends DeviceAdminReceiver {
    @Override
    public void onEnabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onEnabled(context, intent);
        Log.i("Device Admin", "Enabled");
    }

    @Override
    public String onDisableRequested(Context context, Intent intent) {
        // TODO Auto-generated method stub
        return "Admin disable Requested";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onDisabled(context, intent);
        Log.i("Device Admin", "Disables");

    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onPasswordChanged(context, intent);
        Log.i("Device Admin", "Password Changed");
    }
}