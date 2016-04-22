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

package org.wso2.emm.agent.events;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import org.wso2.emm.agent.events.listeners.ApplicationStateListener;
import org.wso2.emm.agent.utils.Constants;

/**
 * All the events added must be registered and started through this class.
 */
public class EventRegistry {
    public static Context context = null;
    public static boolean eventListeningStarted = false;

    /**
     * This constructor is used to initiate context which is kept statically to avoid passing
     * context around.
     *
     * @param context Application context.
     */
    public EventRegistry(Context context) {
        this.context = context;
    }

    /**
     * Registering events and starting happen here.
     */
    public void register() {
        eventListeningStarted = true;
        // First, check if event listening is enabled. If so, check each event is enabled and
        // Start event listening.
        if (Constants.EventListners.EVENT_LISTENING_ENABLED) {
            if (Constants.EventListners.APPLICATION_STATE_LISTENER) {
                // If the listener is implementing broadcast listener, calling start listener
                // should start listening for events.
                ApplicationStateListener applicationState = new ApplicationStateListener();
                applicationState.startListening();
            }
            if (Constants.EventListners.RUNTIME_STATE_LISTENER) {
                // If the event is running on a scheduled polling, it is only necessary to schedule
                // the alarm manager. If the same DEFAULT_START_TIME and DEFAULT_INTERVAL
                // can be used for any new event, there is no need to create a new
                // scheduled alarm here.
                EventRegistry.startDefaultAlarm(Constants.EventListners.DEFAULT_LISTENER_CODE,
                                                Constants.EventListners.DEFAULT_START_TIME,
                                                Constants.EventListners.DEFAULT_INTERVAL);
            }
        }
    }

    private static void startDefaultAlarm(int requestCode, long startTime, long interval) {
        Intent alarmIntent = new Intent(context, EventAlarmReceiver.class);
        alarmIntent.putExtra(Constants.EventListners.REQUEST_CODE, requestCode);
        PendingIntent recurringAlarmIntent =
                PendingIntent.getBroadcast(context, requestCode, alarmIntent,
                                           PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, interval,
                                  recurringAlarmIntent);
    }
}
