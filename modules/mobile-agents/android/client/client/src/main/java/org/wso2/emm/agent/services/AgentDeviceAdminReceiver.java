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

import android.annotation.TargetApi;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.KioskEnrollmentActivity;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static android.security.KeyStore.getApplicationContext;

/**
 * This is the component that is responsible for actual device administration.
 * It becomes the receiver when a policy is applied. It is important that we
 * subclass DeviceAdminReceiver class here and to implement its only required
 * method onEnabled().
 */
public class AgentDeviceAdminReceiver extends DeviceAdminReceiver implements APIResultCallBack {

	private static final String TAG = AgentDeviceAdminReceiver.class.getName();
	private String regId;
	public static final String DISALLOW_SAFE_BOOT = "no_safe_boot";

	/**
	 * Called when this application is approved to be a device administrator.
	 */
	@Override
	public void onEnabled(final Context context, Intent intent) {
		super.onEnabled(context, intent);

		// This flag is added to avoid pre-mature device activation due to COSU enrollment
		// In device owner/ profile owner provision, this onEnabled method gets called before we do.
		// Hence it is controlled using a flag
//		boolean isDeviceActivationSkipped =
//				Preference.getBoolean(context, Constants.PreferenceFlag.SKIP_DEVICE_ACTIVATION);
//		if (isDeviceActivationSkipped) {
//			Preference.putBoolean(context, Constants.PreferenceFlag.SKIP_DEVICE_ACTIVATION, false);
//		} else {
			String notifier = Preference.getString(context, Constants.PreferenceFlag.NOTIFIER_TYPE);
			if(Constants.NOTIFIER_LOCAL.equals(notifier)) {
				LocalNotification.startPolling(context);
			}
		//}
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
			String serverIP = Constants.DEFAULT_HOST;
			String prefIP = Preference.getString(context, Constants.PreferenceFlag.IP);
			if (prefIP != null) {
				serverIP = prefIP;
			}
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
					CommonUtils.unRegisterClientApp(context, AgentDeviceAdminReceiver.this);
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
//		Log.i(TAG, "Provisioning Completed");
//		Preference.putBoolean(context, Constants.PreferenceFlag.SKIP_DEVICE_ACTIVATION, true);
//		Intent launch = new Intent(context, EnableProfileActivity.class);
//		launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(launch);


		Preference.putString(context, Constants.PreferenceFlag.NOTIFIER_TYPE, Constants.NOTIFIER_LOCAL);
		Preference.putInt(context, context.getResources().getString(R.string.shared_pref_frequency),
				Constants.DEFAULT_INTERVAL);

		DevicePolicyManager devicePolicyManager =
				(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);


		ComponentName cdmDeviceAdmin = AgentDeviceAdminReceiver.getComponentName(context);



		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.READ_PHONE_STATE",
					DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
			devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.READ_EXTERNAL_STORAGE",
					DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

			devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.ACCESS_COARSE_LOCATION",
					DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

			devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.WRITE_EXTERNAL_STORAGE",
					DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

			devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.ACCESS_FINE_LOCATION",
					DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

			devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.ACCESS_FINE_LOCATION",
					DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
		}

		setUserRestriction(devicePolicyManager, cdmDeviceAdmin, DISALLOW_SAFE_BOOT, true);


		devicePolicyManager.setApplicationHidden(cdmDeviceAdmin, Constants.SystemApp.PLAY_STORE, true);



		//Preference.putBoolean(context, Constants.PreferenceFlag.SKIP_DEVICE_ACTIVATION, true);

		Intent launch = new Intent(context, KioskEnrollmentActivity.class);
		launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(launch);




	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void setUserRestriction(DevicePolicyManager devicePolicyManager, ComponentName adminComponentName
			, String restriction, boolean disallow) {
		if (disallow) {
			devicePolicyManager.addUserRestriction(adminComponentName, restriction);
		} else {
			devicePolicyManager.clearUserRestriction(adminComponentName, restriction);
		}
	}



	@TargetApi(Build.VERSION_CODES.M)
	@Override
	public void onSystemUpdatePending(Context context, Intent intent, long receivedTime) {
		if (receivedTime != -1) {
			DateFormat sdf = new SimpleDateFormat("hh:mm:ss dd/MM/yyyy");
			String timeString = sdf.format(new Date(receivedTime));
			Toast.makeText(context, "System update received at: " + timeString,
			               Toast.LENGTH_LONG).show();
		} else {
			// No system update is currently available on this device.
		}
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

