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
import android.os.Bundle;

import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

import org.wso2.emm.agent.services.MessageProcessor;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GcmListenerService {

	private static final String TAG = GCMIntentService.class.getName();

	/**
	 * This method gets called when a GCM message is received. We use GCM as the device wake up
	 * method. Once the Agent receives a GCM notification, Agent polls the server for pending operations.
	 */
	@Override
	public void onMessageReceived(String from, Bundle data) {
		MessageProcessor messageProcessor = new MessageProcessor(this.getApplicationContext());
		try {
			messageProcessor.getMessages();
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Failed to perform operation." + e);
		}
	}

}
