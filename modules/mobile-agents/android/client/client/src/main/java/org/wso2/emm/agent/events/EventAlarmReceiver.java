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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import org.wso2.emm.agent.events.listeners.RuntimeStateListener;
import org.wso2.emm.agent.utils.Constants;

/**
 * This class is a broadcast receiver which triggers events. This is like a callback of a
 * scheduled task.
 */
public class EventAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = EventAlarmReceiver.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Recurring alarm; Event listener");
        }
        final int requestCode = intent.getExtras().getInt(Constants.EventListners.REQUEST_CODE);
        // If the default listener is used, all event initialisation can be done in the bellow if
        // block.

        new AsyncTask<Void, Void, Integer>() {

            @Override
            protected Integer doInBackground(Void... params) {
                if (requestCode == Constants.EventListners.DEFAULT_LISTENER_CODE) {
                    RuntimeStateListener runtimeStateListener = new RuntimeStateListener();
                    runtimeStateListener.startListening();
                }
                return requestCode;
            }

            @Override
            protected void onPostExecute(Integer reqCode) {
            }
        }.execute();
    }

}
