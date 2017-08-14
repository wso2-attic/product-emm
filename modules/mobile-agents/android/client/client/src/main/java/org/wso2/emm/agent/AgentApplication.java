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

package org.wso2.emm.agent;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.util.Log;

import com.splunk.mint.Mint;

import org.wso2.emm.agent.services.EnrollmentService;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;

public class AgentApplication extends Application {

    // Configs
    private int requestCode = 0;
    private int relaunchDelay = 5000;

    public AgentApplication() {

        // setup handler for uncaught exception
        Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                PendingIntent pendingIntent;
                if (Constants.AUTO_ENROLLMENT_BACKGROUND_SERVICE_ENABLED) {
                    pendingIntent = PendingIntent.getService(getApplicationContext(),
                            requestCode, new Intent(getApplicationContext(), EnrollmentService.class),
                            PendingIntent.FLAG_ONE_SHOT);
                } else {
                    pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            requestCode, new Intent(getApplicationContext(), AgentReceptionActivity.class),
                            PendingIntent.FLAG_ONE_SHOT);
                }
                AlarmManager alarmManager;
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        relaunchDelay, pendingIntent);

                Log.e("AgentApplication", "UncaughtExceptionHandler got an exception", ex);
                System.exit(2);
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
    }

    public void onCreate(){
        super.onCreate();

        if (Constants.LogPublisher.LOG_PUBLISHER_IN_USE.equals(Constants.LogPublisher.SPLUNK_PUBLISHER)) {
            if (Constants.SplunkConfigs.TYPE_MINT.equals(Constants.SplunkConfigs.DATA_COLLECTOR_TYPE)) {
                Mint.initAndStartSession(getApplicationContext(), Constants.SplunkConfigs.API_KEY);
            } else if (Constants.SplunkConfigs.TYPE_HTTP.equals(Constants.SplunkConfigs.DATA_COLLECTOR_TYPE)) {
                Mint.initAndStartSessionHEC(getApplicationContext(), Constants.SplunkConfigs.HEC_MINT_ENDPOINT_URL, Constants.SplunkConfigs.HEC_TOKEN);
            }
            Mint.enableLogging(true);
            Mint.setLogging("*:D");
        }

        if (Constants.SYSTEM_APP_ENABLED) {
            CommonUtils.registerSystemAppReceiver(this);
        }
    }
}
