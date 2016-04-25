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
package org.wso2.emm.system.service.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import org.wso2.emm.system.service.R;
import org.wso2.emm.system.service.services.AlarmReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class to hold file alarm scheduling methods.
 */
public class AlarmUtils {
    private static final int DEFAULT_TIME_MILLISECONDS = 1000;
    private static final int RECURRING_REQUEST_CODE = 200;
    private static final int ONE_TIME_REQUEST_CODE = 300;
    /**
     * Initiates repeating alarm on device startup.
     * @param context - Application context.
     * @param  interval - Time interval that alarm should repeat.
     */
    public static void setRecurringAlarm(Context context, int interval) {
        long startTime = SystemClock.elapsedRealtime() + DEFAULT_TIME_MILLISECONDS;
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringAlarmIntent =
                PendingIntent.getBroadcast(context,
                                           RECURRING_REQUEST_CODE,
                                           alarmIntent,
                                           PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, startTime,
                                  interval, recurringAlarmIntent);
    }

    /**
     * Initiates one time alarm device startup.
     * @param context - Application context.
     * @param  time - Time that alarm should trigger.
     */
    public static void setOneTimeAlarm(Context context, String time, String operation) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.ENGLISH);
        Date date = formatter.parse(time);
        long startTime = date.getTime();
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.putExtra(context.getResources().getString(R.string.alarm_scheduled_operation), operation);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context,
                                           ONE_TIME_REQUEST_CODE,
                                           alarmIntent,
                                           PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);
    }

}
