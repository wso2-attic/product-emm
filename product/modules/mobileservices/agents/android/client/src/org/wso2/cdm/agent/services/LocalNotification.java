/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.cdm.agent.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Local notification is a communication mechanism that essentially,
 * polls to server based on a predefined to retrieve pending data.
 */
public class LocalNotification {
	public static void startPolling(Context context) {
		int interval=10000;
//		int interval=Preference.getInt(context, context.getResources().getString(R.string.shared_pref_interval));
		//TODO:remove hard coded value
		
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += 1000;

		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
				0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
			                    interval, recurringDownload);
	}
}
