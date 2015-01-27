/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.services;

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.CommonUtilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class DeviceStartupIntentReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(final Context context, Intent intent1) {
    	setRecurringAlarm(context);
    }
    
    private void setRecurringAlarm(Context context) {
    	String mode=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_message_mode));
    	SharedPreferences mainPref = context.getSharedPreferences(
    	                                         		         context.getResources().getString(R.string.shared_pref_package), Context.MODE_PRIVATE);
    	Float interval=(Float)mainPref.getFloat(context.getResources().getString(R.string.shared_pref_interval), 1.0f);
    	
    	String clientKey = mainPref.getString(context.getResources().getString(R.string.shared_pref_client_id), "");
		String clientSecret = mainPref.getString(context.getResources().getString(R.string.shared_pref_client_secret), "");
		if(!clientKey.equals("") && !clientSecret.equals("")){
			CommonUtilities.CLIENT_ID=clientKey;
			CommonUtilities.CLIENT_SECRET=clientSecret;
		}
		
		if(mode.trim().toUpperCase().equals("LOCAL")){
			long firstTime = SystemClock.elapsedRealtime();
			firstTime += 1 * 1000;

			Intent downloader = new Intent(context, AlarmReceiver.class);
			PendingIntent recurringDownload = PendingIntent.getBroadcast(context,
					0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarms = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			/*
			 * alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
			 * updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
			 * recurringDownload);
			 */
			Float seconds=interval;
			if(interval<1.0){
				
				alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
				                    seconds.intValue(), recurringDownload);
			}else{
				alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
				                    seconds.intValue(), recurringDownload);
			}
    	}
	}
}