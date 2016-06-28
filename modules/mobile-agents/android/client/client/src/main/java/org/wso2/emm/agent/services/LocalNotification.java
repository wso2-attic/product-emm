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

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.utils.Preference;

/**
 * Local notification is a communication mechanism that essentially,
 * polls to server based on a predefined to retrieve pending data.
 */
public class LocalNotification {
	public static final int DEFAULT_INTERVAL = 30000;
	public static final int DEFAULT_INDEX = 0;
	public static final int DEFAULT_BUFFER = 1000;
	public static final int REQUEST_CODE = 0;
	public static final String LOCAL_NOTIFIER_INVOKED_PREF_KEY = "localNoticicationInvoked";

	public static void startPolling(Context context) {
		int interval = Preference.getInt(context, context.getResources().getString(R.string.shared_pref_frequency));
		if(interval == DEFAULT_INDEX){
			interval = DEFAULT_INTERVAL;
		}
		long currentTime = SystemClock.elapsedRealtime();
		currentTime += DEFAULT_BUFFER;
		stopPolling(context);
		if (!Preference.getBoolean(context, LOCAL_NOTIFIER_INVOKED_PREF_KEY)) {
			Preference.putBoolean(context, LOCAL_NOTIFIER_INVOKED_PREF_KEY, true);
			Intent alarm = new Intent(context, AlarmReceiver.class);
			PendingIntent recurringAlarm = PendingIntent.getBroadcast(context, REQUEST_CODE, alarm,
							PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, currentTime, interval,
					recurringAlarm);
		}
	}

	public static void stopPolling(Context context) {
		if (Preference.getBoolean(context, LOCAL_NOTIFIER_INVOKED_PREF_KEY)) {
			Preference.putBoolean(context, LOCAL_NOTIFIER_INVOKED_PREF_KEY, false);
			Intent alarm = new Intent(context, AlarmReceiver.class);
			PendingIntent sender = PendingIntent.getBroadcast(context, REQUEST_CODE, alarm, DEFAULT_INDEX);
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.cancel(sender);
		}
	}
}
