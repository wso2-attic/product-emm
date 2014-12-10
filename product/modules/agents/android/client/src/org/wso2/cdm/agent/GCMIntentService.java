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
package org.wso2.cdm.agent;


import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.api.ApplicationManager;
import org.wso2.cdm.agent.services.Config;
import org.wso2.cdm.agent.services.ProcessMessage;
import org.wso2.cdm.agent.utils.CommonUtilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
	DevicePolicyManager devicePolicyManager;
	ApplicationManager appList;
	static final int ACTIVATION_REQUEST = 47; 
	ProcessMessage processMsg = null;
		
    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(CommonUtilities.SENDER_ID);
    }
   

    @Override
	protected void onRegistered(Context context, String registrationId) {
		if (CommonUtilities.DEBUG_MODE_ENABLED) {
			Log.i(TAG, "Device registered: regId = " + registrationId);
		}
		SharedPreferences preferences = context.getSharedPreferences(
				getResources().getString(R.string.shared_pref_package),
				Context.MODE_PRIVATE);
		//if (preferences.getString(getResources().getString(R.string.shared_pref_message_mode), "").trim().toUpperCase().contains("GCM")) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(getResources().getString(R.string.shared_pref_regId),
				registrationId);
		editor.commit();
		//}
	}

    @Override
    protected void onUnregistered(Context context, String registrationId) {
    	if(CommonUtilities.DEBUG_MODE_ENABLED){Log.i(TAG, "Device unregistered");}

        if (GCMRegistrar.isRegisteredOnServer(context)) {

        	
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
        	if(CommonUtilities.DEBUG_MODE_ENABLED){Log.i(TAG, "Ignoring unregister callback");}
        }
    }
    
	@Override
    protected void onMessage(Context context, Intent intent) {
		Log.e("onmsg","onmsg");
		
        Config.context = this;
        String mode=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_message_mode));
		if(mode.trim().toUpperCase().equals("GCM")){
			Log.e("onmsg","GCM");
			ProcessMessage msg=new ProcessMessage(context);
			msg.getOperations(null);
		}
		else{
			Log.e("onmsg","mode");
		}
    	//processMsg = new ProcessMessage(Config.context, CommonUtilities.MESSAGE_MODE_GCM, intent);
    }
	    

    @Override
    protected void onDeletedMessages(Context context, int total) {
    	if(CommonUtilities.DEBUG_MODE_ENABLED){Log.i(TAG, "Received deleted messages notification");}
        String message = getString(R.string.gcm_deleted, total);
      //  displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
    	if(CommonUtilities.DEBUG_MODE_ENABLED){Log.i(TAG, "Received error: " + errorId);}

    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
    	if(CommonUtilities.DEBUG_MODE_ENABLED){Log.i(TAG, "Received recoverable error: " + errorId);}

        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_stat_gcm;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, NotifyActivity.class);
        notificationIntent.putExtra(context.getResources().getString(R.string.intent_extra_notification), message);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent =
                PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notification.setLatestEventInfo(context, title, message, intent);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}
