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

import java.util.Map;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.CommonUtils;

import android.app.admin.DeviceAdminReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

/**
 * This is the component that is responsible for actual device administration.
 * It becomes the receiver when a policy is applied. It is important that we
 * subclass DeviceAdminReceiver class here and to implement its only required
 * method onEnabled().
 */
public class AgentDeviceAdminReceiver extends DeviceAdminReceiver implements APIResultCallBack {

	private static final String TAG = AgentDeviceAdminReceiver.class.getName();
	private String regId;

	/**
	 * Called when this application is approved to be a device administrator.
	 */
	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);

		Resources resources = context.getResources();
		Preference.putBoolean(context, Constants.PreferenceFlag.DEVICE_ACTIVE, true);

		MessageProcessor processor = new MessageProcessor(context);
		try {
			processor.getMessages();
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Failed to perform operation", e);
		}

		Toast.makeText(context, R.string.device_admin_enabled,
				Toast.LENGTH_LONG).show();
		String notifier = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
		if(Constants.NOTIFIER_LOCAL.equals(notifier)) {
			LocalNotification.startPolling(context);
		}
	}

	/**
	 * Called when this application is no longer the device administrator.
	 */
	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);
		Toast.makeText(context, R.string.device_admin_disabled,
		               Toast.LENGTH_LONG).show();
		regId = Preference
				.getString(context, Constants.PreferenceFlag.REG_ID);

		if (regId != null && !regId.isEmpty()) {
			startUnRegistration(context);
		} else {
			Log.e(TAG, "Registration ID is already null");
		}
	}

	/**
	 * Start un-registration process.
	 * @param context - Application context.
	 */
	public void startUnRegistration(Context context) {
		String regId = Preference.getString(context, Constants.PreferenceFlag.REG_ID);
		if (regId != null && !regId.isEmpty()) {
			String serverIP = Preference.getString(context, Constants.PreferenceFlag.IP);

			if (serverIP != null && !serverIP.isEmpty()) {
				ServerConfig utils = new ServerConfig();
				utils.setServerIP(serverIP);

				CommonUtils.callSecuredAPI(context,
				                           utils.getAPIServerURL(context) + Constants.UNREGISTER_ENDPOINT + regId,
				                           HTTP_METHODS.DELETE,
				                           null, AgentDeviceAdminReceiver.this,
				                           Constants.UNREGISTER_REQUEST_CODE);
				try {
					LocalNotification.stopPolling(context);
					CommonUtils.unRegisterClientApp(context);
					CommonUtils.clearAppData(context);
				} catch (AndroidAgentException e) {
					Log.e(TAG, "Error occurred while removing Oauth application", e);
				}
			} else {
				Log.e(TAG, "There is no valid IP to contact the server");
			}
		}
	}

	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		super.onPasswordChanged(context, intent);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "onPasswordChanged.");
		}
	}

	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		super.onPasswordFailed(context, intent);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "onPasswordFailed.");
		}
	}

	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		super.onPasswordSucceeded(context, intent);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "onPasswordSucceeded.");
		}
	}

	@Override
	public void onReceiveAPIResult(Map<String, String> arg0, int arg1) {
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Unregistered." + arg0.toString());
		}
	}

	@Override
	public void onProfileProvisioningComplete(Context context, Intent intent) {
		Intent launch = new Intent(context, EnableProfileActivity.class);
		launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		context.startActivity(launch);

	}

	/**
	 * Generates a {@link ComponentName} that is used throughout the app.
	 * @return a {@link ComponentName}
	 */
	public static ComponentName getComponentName(Context context) {
		return new ComponentName(context.getApplicationContext(), AgentDeviceAdminReceiver.class);
	}

	public void onLockTaskModeEntering(Context context, Intent intent, String pkg) {
		Toast.makeText(context, "Device is locked", Toast.LENGTH_LONG).show();
	}

	public void onLockTaskModeExiting(Context context, Intent intent) {
		Toast.makeText(context, "Device is unlocked", Toast.LENGTH_SHORT).show();
	}

}

