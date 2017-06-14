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

package org.wso2.emm.agent;
import android.content.Context;

import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.wso2.emm.agent.services.MessageProcessor;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

/**
 * IntentService responsible for handling FCM messages.
 */
public class FCMMessagingService extends FirebaseMessagingService {
	private static final String TAG = FCMMessagingService.class.getName();

	@Override
	public void onMessageReceived(RemoteMessage message){
		Context context = this.getApplicationContext();
		Log.d(TAG, "New FCM notification.");
		MessageProcessor messageProcessor = new MessageProcessor(context);
		try {
			messageProcessor.getMessages();
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Failed to perform operation", e);
		}

		if (Constants.SYSTEM_APP_ENABLED && Preference.getBoolean(context, context.getResources().
				getString(R.string.firmware_upgrade_failed))) {
			Preference.putBoolean(context, context.getResources().
					getString(R.string.firmware_upgrade_failed), false);
			CommonUtils.callSystemApp(context, Constants.Operation.UPGRADE_FIRMWARE, null, null);
		}
	}

}

