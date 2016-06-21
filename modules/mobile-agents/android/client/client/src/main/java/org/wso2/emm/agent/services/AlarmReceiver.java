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
	private String operation = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Recurring alarm; requesting alarm service.");
		}

		if (intent.hasExtra(context.getResources().getString(R.string.alarm_scheduled_operation))) {
			operation = intent.getStringExtra(context.getResources().getString(R.string.alarm_scheduled_operation));

			if(operation != null && operation.trim().equals(Constants.Operation.INSTALL_APPLICATION)) {
				Preference.putString(context, context.getResources().getString(R.string.alarm_schedule), null);
				Preference.putString(context, context.getResources().getString(R.string.app_uri), null);
				Toast.makeText(context, "App install request initiated by admin.",
						Toast.LENGTH_SHORT).show();
				//Prepare for install
				String packageUri;
				if (intent.hasExtra(context.getResources().getString(R.string.app_uri))) {
					packageUri = intent.getStringExtra(context.getResources().getString(R.string.app_uri));
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(Uri.parse(packageUri), context.getResources().getString(R.string.application_mgr_mime));
					installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(installIntent);
				} else {
					Toast.makeText(context, "App installation failed.",
							Toast.LENGTH_SHORT).show();
				}
			} else if(operation != null && operation.trim().equals(Constants.Operation.UNINSTALL_APPLICATION)) {
				Preference.putString(context, context.getResources().getString(R.string.alarm_schedule), null);
				Preference.putString(context, context.getResources().getString(R.string.app_uri), null);
				Toast.makeText(context, "App uninstall request initiated by admin.",
						Toast.LENGTH_SHORT).show();
				//Prepare for uninstall
				String packageName;
				if (intent.hasExtra(context.getResources().getString(R.string.app_uri))) {
					packageName = intent.getStringExtra(context.getResources().getString(R.string.app_uri));
					Uri packageURI = Uri.parse(packageName);
					Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
					uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(uninstallIntent);
				} else {
					Toast.makeText(context, "App uninstallation failed.",
							Toast.LENGTH_SHORT).show();
				}
			}

		} else {
			OperationTask operationTask = new OperationTask();
			operationTask.execute(context);
		}

		if (Constants.SYSTEM_APP_ENABLED && Preference.getBoolean(context, context.getResources().
				getString(R.string.firmware_upgrade_failed))) {
			Preference.putBoolean(context, context.getResources().
					getString(R.string.firmware_upgrade_failed), false);
			CommonUtils.callSystemApp(context, Constants.Operation.UPGRADE_FIRMWARE, null, null);
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
