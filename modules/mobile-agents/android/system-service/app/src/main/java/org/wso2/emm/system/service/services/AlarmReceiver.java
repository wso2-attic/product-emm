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
package org.wso2.emm.system.service.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.api.OTADownload;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.Preference;

/**
 * This class is a broadcast receiver which triggers on scheduled task timeouts.
 */
public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = AlarmReceiver.class.getName();
	private String operation = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Scheduled task triggered.");

		if (intent.hasExtra(context.getResources().getString(R.string.alarm_scheduled_operation))) {
			operation = intent.getStringExtra(context.getResources().getString(R.string.alarm_scheduled_operation));
		}

		if (operation != null && operation.trim().equals(Constants.Operation.UPGRADE_FIRMWARE)) {
			Preference.putString(context, context.getResources().getString(R.string.alarm_schedule), null);
			Toast.makeText(context, "Upgrade request initiated by admin.",
			               Toast.LENGTH_SHORT).show();
			//Prepare for upgrade
			OTADownload otaDownload = new OTADownload(context);
			otaDownload.startOTA();
		}
	}


}
