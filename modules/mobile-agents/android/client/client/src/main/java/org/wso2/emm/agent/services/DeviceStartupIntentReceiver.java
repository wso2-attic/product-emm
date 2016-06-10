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

import java.util.Locale;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.events.EventRegistry;
import org.wso2.emm.agent.services.operation.OperationProcessor;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;

/**
 * Broadcast receiver for device boot action used to start agent local
 * notification service at device startup.
 */
public class DeviceStartupIntentReceiver extends BroadcastReceiver {
	private static final int DEFAULT_TIME_MILLISECONDS = 1000;
	private static final int DEFAULT_REQUEST_CODE = 0;
	public static final int DEFAULT_INDEX = 0;
	public static final int DEFAULT_ID = -1;
	public static final int DEFAULT_INTERVAL = 30000;
	private Resources resources;
    private static final String TAG = "DeviceStartupIntent";

	@Override
	public void onReceive(final Context context, Intent intent) {
		setRecurringAlarm(context.getApplicationContext());
		if(!EventRegistry.eventListeningStarted) {
			EventRegistry registerEvent = new EventRegistry(context.getApplicationContext());
			registerEvent.register();
		}
	}

	/**
	 * Initiates device notifier on device startup.
	 * @param context - Application context.
	 */
	private void setRecurringAlarm(Context context) {
		this.resources = context.getApplicationContext().getResources();
		String mode = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
		boolean isLocked = Preference.getBoolean(context, Constants.IS_LOCKED);
		String lockMessage = Preference.getString(context, Constants.LOCK_MESSAGE);

		if (lockMessage == null || lockMessage.isEmpty()) {
			lockMessage = resources.getString(R.string.txt_lock_activity);
		}

		if (isLocked) {
            Operation lockOperation = new Operation();
			lockOperation.setId(DEFAULT_ID);
			lockOperation.setCode(Constants.Operation.DEVICE_LOCK);
			try {
				JSONObject payload = new JSONObject();
				payload.put(Constants.ADMIN_MESSAGE, lockMessage);
				payload.put(Constants.IS_HARD_LOCK_ENABLED, true);
				lockOperation.setPayLoad(payload.toString());
				OperationProcessor operationProcessor = new OperationProcessor(context);
                operationProcessor.doTask(lockOperation);
            } catch (AndroidAgentException e) {
                Log.e(TAG, "Error occurred while executing hard lock operaton at the device startup");
            } catch (JSONException e) {
				Log.e(TAG, "Error occurred while building hard lock operation payload");
			}
		}


		int interval = Preference.getInt(context, context.getResources().getString(R.string.shared_pref_frequency));
		if(interval == DEFAULT_INDEX){
			interval = DEFAULT_INTERVAL;
		}

		if(mode == null) {
			mode = Constants.NOTIFIER_LOCAL;
		}

		if (Constants.NOTIFIER_LOCAL.equals(mode.trim().toUpperCase(Locale.ENGLISH))) {
			long startTime = SystemClock.elapsedRealtime() + DEFAULT_TIME_MILLISECONDS;

			Intent alarmIntent = new Intent(context, AlarmReceiver.class);
			PendingIntent recurringAlarmIntent =
					PendingIntent.getBroadcast(context,
					                           DEFAULT_REQUEST_CODE,
					                           alarmIntent,
					                           PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager =
					(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime,
			                          interval, recurringAlarmIntent);
		}
	}

}