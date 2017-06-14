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

import android.net.Uri;
import android.os.AsyncTask;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * This class is a broadcast receiver which triggers on local notification timeouts.
 */
public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = AlarmReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Recurring alarm; requesting alarm service.");
		}

		if (intent.hasExtra(context.getResources().getString(R.string.alarm_scheduled_operation))) {
			String operationCode = intent.getStringExtra(context.getResources().getString(R.string.alarm_scheduled_operation));
			ApplicationManager applicationManager = new ApplicationManager(context.getApplicationContext());
			if(operationCode != null && operationCode.trim().equals(Constants.Operation.INSTALL_APPLICATION)) {
				String appUrl = intent.getStringExtra(context.getResources().getString(R.string.app_url));
				Operation operation = null;
				if (intent.hasExtra(context.getResources().getString(R.string.alarm_scheduled_operation_payload)))				{
					operation = (Operation) intent.getExtra(context.getResources().getString(R.string.alarm_scheduled_operation_payload));
				}
				applicationManager.installApp(appUrl, null, operation);
			} else if(operationCode != null && operationCode.trim().equals(Constants.Operation.UNINSTALL_APPLICATION)) {
				String packageUri = intent.getStringExtra(context.getResources().getString(R.string.app_uri));
				applicationManager.uninstallApplication(packageUri, null);
			}

		} else {
			OperationTask operationTask = new OperationTask();
			operationTask.execute(context);
		}

	}

	private class OperationTask extends AsyncTask<Context, Void, Void> {

		@Override
		protected Void doInBackground(Context... params) {
			if (params != null) {
				MessageProcessor messageProcessor = new MessageProcessor(params[0]);
				try {
					messageProcessor.getMessages();
				} catch (AndroidAgentException e) {
					Log.e(TAG, "Failed to perform operation", e);
				}
			}
			return null;
		}
	}
}
