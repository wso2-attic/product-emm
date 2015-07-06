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

package org.wso2.mdm.agent;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.wso2.mdm.agent.services.BuildDeviceInfoPayload;
import org.wso2.mdm.agent.services.MessageProcessor;
import org.wso2.mdm.agent.utils.Constants;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends IntentService {
	private MessageProcessor messageProcessor;
	private static final String TAG = GCMIntentService.class.getName();

	/**
	 * Creates an IntentService. Invoked by your subclass's constructor.
	 * 
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public GCMIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// Retrieve data extras from push notification
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		String messageType = gcm.getMessageType(intent);
		if (extras != null && messageType != null && messageType.equals(Constants.messageTypeGCM)) {
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Message Type: " + messageType + ", Message: " + extras.toString());
			}
		}
		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}
}
