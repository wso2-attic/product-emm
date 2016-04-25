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
import android.content.res.Resources;
import android.util.Log;
import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.utils.AlarmUtils;
import org.wso2.emm.system.service.utils.Constants;
import org.wso2.emm.system.service.utils.Preference;
import java.text.ParseException;

/**
 * Broadcast receiver for device boot action used to start scheduled
 * services at device startup.
 */
public class DeviceStartupIntentReceiver extends BroadcastReceiver {
	private Resources resources;
	private static final String TAG = DeviceStartupIntentReceiver.class.getName();

	@Override
	public void onReceive(final Context context, Intent intent) {
		this.resources = context.getApplicationContext().getResources();
		int interval = Preference.getInt(context, resources.getString(R.string.alarm_interval));
		String oneTimeAlarm = Preference.getString(context, resources.getString(R.string.alarm_schedule));

		if(interval > 0) {
			AlarmUtils.setRecurringAlarm(context.getApplicationContext(), interval);
		}

		if(oneTimeAlarm != null && !oneTimeAlarm.trim().isEmpty()) {
			try {
				AlarmUtils.setOneTimeAlarm(context, oneTimeAlarm, Constants.Operation.UPGRADE_FIRMWARE);
			} catch (ParseException e) {
				Log.e(TAG, "One time alarm time string parsing failed." + e);
			}
		}
	}

}