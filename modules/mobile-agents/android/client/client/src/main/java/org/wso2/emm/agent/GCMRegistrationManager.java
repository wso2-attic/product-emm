/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.services.DeviceInfoPayload;
import org.wso2.emm.agent.utils.CommonDialogUtils;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.io.IOException;
import java.util.Map;

/**
 * This provides methods necessary to register a device with Google cloud
 * messaging or check if it has been already registered.
 */
public class GCMRegistrationManager implements APIResultCallBack {
	private static final String TAG = GCMRegistrationManager.class.getSimpleName();
	private GoogleCloudMessaging cloudMessaging;

	private String googleProjectNumber;
	private Activity activity;
	private String registrationId;
	private final static int REQUEST_CODE = 4034;
	private DialogInterface.OnClickListener registrationFailedClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			loadAuthenticationErrorActivity();
		}
	};

	public GCMRegistrationManager(Activity activity, String googleProjectNumber) {
		this.activity = activity;
		this.googleProjectNumber = googleProjectNumber;
		cloudMessaging = GoogleCloudMessaging.getInstance(activity);
	}

	private Context getContext() {
		return activity.getApplicationContext();
	}

	private Activity getActivity() {
		return activity;
	}

	public String getGoogleProjectNumber() {
		return googleProjectNumber;
	}

	/**
	 * If the devices hasn't been registered before, first register with Google.
	 * Otherwise obtain the registration ID from preferences.
	 * 
	 * @return Registration Id return by Google.
	 */
	public String registerWithGoogle() {
		if (isPlayServicesInstalled()) {
			registrationId = Preference.getString(getContext(), Constants.GCM_REG_ID);
			if (registrationId == null) {
				try {
					if (cloudMessaging == null) {
						cloudMessaging = GoogleCloudMessaging.getInstance(getContext());
					}
					registrationId = cloudMessaging.register(getGoogleProjectNumber());

				} catch (IOException ex) {
					Log.e(TAG, "Error while registering with GCM ", ex);
					clearData(getContext());
					displayConnectionError();
				}
			}
		} else {
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Play services not installed");
			}
		}
		return registrationId;
	}

	/**
	 * Revoke currently enforced policy.
	 * @param context - Application context.
	 */
	public void clearData(Context context){
		try {
			CommonUtils.clearAppData(context);
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Failed to clear app data.", e);
		}
	}


	/**
	 * This is used to send the registration Id to MDM server so that the server
	 * can use it as a reference to identify the device when sending messages to
	 * Google server.
	 * 
	 * @throws AndroidAgentException
	 */
	public void sendRegistrationId() throws AndroidAgentException {
		DeviceInfo deviceInfo = new DeviceInfo(getContext());
		DeviceInfoPayload deviceInfoPayload = new DeviceInfoPayload(getContext());
		deviceInfoPayload.build();

		String replyPayload = deviceInfoPayload.getDeviceInfoPayload();
		String ipSaved = Preference.getString(getContext(), Constants.PreferenceFlag.IP);

		if (ipSaved != null && !ipSaved.isEmpty()) {
			ServerConfig utils = new ServerConfig();
			utils.setServerIP(ipSaved);

			String url = utils.getAPIServerURL(getContext()) + Constants.DEVICE_ENDPOINT + deviceInfo.getDeviceId();

			CommonUtils.callSecuredAPI(getContext(), url, org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.PUT,
			                           replyPayload, GCMRegistrationManager.this, Constants.GCM_REGISTRATION_ID_SEND_CODE);
		} else {
			Log.e(TAG, "There is no valid IP to contact the server");
		}
	}

	/**
	 * Check the device to see if it has Google play services installed. If not
	 * prompt user to install.
	 * 
	 * @return if Google play services are installed return true, otherwise false.
	 */
	private boolean isPlayServicesInstalled() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
		if (resultCode != ConnectionResult.SUCCESS) {
			// if not installed try to see if it can be fixed by a user action.
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), REQUEST_CODE).show();
			}
			return false;
		}
		return true;
	}

	/**
	 * Handles the response returned for GCM registration Id sent to MDM.
	 * 
	 * @param result Result returned from the server.
	 * @param requestCode Identifying number used to distinguish request.
	 */
	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		if (requestCode == Constants.GCM_REGISTRATION_ID_SEND_CODE && result != null) {
			String status = result.get(Constants.STATUS_KEY);
			if (!Constants.Status.SUCCESSFUL.equals(status)) {
				clearData(getContext());
				displayConnectionError();
			}
		}
	}

	/**
	 * Display connectivity error.
	 */
	private void displayConnectionError() {
		Resources resources = getContext().getResources();
		CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(getContext(),
		                                                      resources.getString(R.string.title_head_connection_error),
		                                                      resources.getString(R.string.error_internal_server),
		                                                      resources.getString(R.string.button_ok),
		                                                      registrationFailedClickListener).show();
	}

	/**
	 * Loads Authentication error activity.
	 */
	private void loadAuthenticationErrorActivity() {
		Intent intent = new Intent(getActivity(), AuthenticationErrorActivity.class);
		intent.putExtra(getContext().getResources().getString(R.string.intent_extra_from_activity),
		                RegistrationActivity.class.getSimpleName());
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		getActivity().startActivity(intent);
	}
}