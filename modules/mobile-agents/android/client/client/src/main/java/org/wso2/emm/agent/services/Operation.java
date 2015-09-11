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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.AlertActivity;
import org.wso2.emm.agent.ServerDetails;
import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.api.GPSTracker;
import org.wso2.emm.agent.api.WiFiConfig;
import org.wso2.emm.agent.beans.ComplianceFeature;
import org.wso2.emm.agent.beans.DeviceAppInfo;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.CommonUtils;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * This class handles all the functionalities related to device management operations.
 */
public class Operation implements APIResultCallBack {

	private Context context;
	private DevicePolicyManager devicePolicyManager;
	private ApplicationManager appList;
	private Resources resources;
	private BuildResultPayload resultBuilder;
	private DeviceInfo deviceInfo;
	private GPSTracker gps;

	private static final String TAG = "Operation Handler";

	private static final String LOCATION_INFO_TAG_LONGITUDE = "longitude";
	private static final String LOCATION_INFO_TAG_LATITUDE = "latitude";
	private static final String APP_INFO_TAG_NAME = "name";
	private static final String APP_INFO_TAG_PACKAGE = "package";
	private static final String APP_INFO_TAG_ICON = "icon";
	private static final int PRE_WIPE_WAIT_TIME = 4000;
	private static final int ACTIVATION_REQUEST = 47;
	private static final int DEFAULT_PASSWORD_LENGTH = 0;
	private static final int DEFAULT_VOLUME = 0;
	private static final int DEFAULT_FLAG = 0;
	private static final int DEFAULT_PASSWORD_MIN_LENGTH = 3;
	private static final long DAY_MILLISECONDS_MULTIPLIER = 24 * 60 * 60 * 1000;
	private Map<String, String> bundleParams;

	public Operation(Context context) {

		this.context = context;
		this.resources = context.getResources();
		this.devicePolicyManager =
				(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		this.appList = new ApplicationManager(context.getApplicationContext());
		this.resultBuilder = new BuildResultPayload();
		deviceInfo = new DeviceInfo(context.getApplicationContext());
		gps = new GPSTracker(context.getApplicationContext());
	}

	/**
	 * Executes device management operations on the device.
	 *
	 * @param operation - Operation object.
	 */
	public void doTask(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		switch (operation.getCode()) {
			case Constants.Operation.DEVICE_INFO:
				getDeviceInfo(operation);
				break;
			case Constants.Operation.DEVICE_LOCATION:
				getLocationInfo(operation);
				break;
			case Constants.Operation.APPLICATION_LIST:
				getApplicationList(operation);
				break;
			case Constants.Operation.DEVICE_LOCK:
				lockDevice(operation);
				break;
			case Constants.Operation.WIPE_DATA:
				wipeDevice(operation);
				break;
			case Constants.Operation.CLEAR_PASSWORD:
				clearPassword(operation);
				break;
			case Constants.Operation.NOTIFICATION:
				displayNotification(operation);
				break;
			case Constants.Operation.WIFI:
				configureWifi(operation);
				break;
			case Constants.Operation.CAMERA:
				disableCamera(operation);
				break;
			case Constants.Operation.INSTALL_APPLICATION:
				installAppBundle(operation);
				break;
			case Constants.Operation.INSTALL_APPLICATION_BUNDLE:
				installAppBundle(operation);
				break;
			case Constants.Operation.UNINSTALL_APPLICATION:
				uninstallApplication(operation);
				break;
			case Constants.Operation.ENCRYPT_STORAGE:
				encryptStorage(operation);
				break;
			case Constants.Operation.DEVICE_RING:
				ringDevice(operation);
				break;
			case Constants.Operation.DEVICE_MUTE:
				muteDevice(operation);
				break;
			case Constants.Operation.WEBCLIP:
				manageWebClip(operation);
				break;
			case Constants.Operation.PASSWORD_POLICY:
				setPasswordPolicy(operation);
				break;
			case Constants.Operation.INSTALL_GOOGLE_APP:
				installGooglePlayApp(operation);
				break;
			case Constants.Operation.CHANGE_LOCK_CODE:
				changeLockCode(operation);
				break;
			case Constants.Operation.POLICY_BUNDLE:
				setPolicyBundle(operation);
				break;
			case Constants.Operation.POLICY_MONITOR:
				monitorPolicy(operation);
				break;
			case Constants.Operation.POLICY_REVOKE:
				revokePolicy(operation);
				break;
			case Constants.Operation.ENTERPRISE_WIPE:
				enterpriseWipe(operation);
				break;
			case Constants.Operation.BLACKLIST_APPLICATIONS:
				blacklistApps(operation);
				break;
			case Constants.Operation.DISENROLL:
				disenrollDevice(operation);
				break;
			default:
				Log.e(TAG, "Invalid operation code received");
				break;
		}
	}

	/**
	 * Retrieve device information.
	 *
	 * @param operation - Operation object.
	 */
	public void getDeviceInfo(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

		BuildDeviceInfoPayload deviceInfoPayload = new BuildDeviceInfoPayload(context);
		deviceInfoPayload.build();

		String replyPayload = deviceInfoPayload.getDeviceInfoPayload();

		String ipSaved =
				Preference.getString(context.getApplicationContext(),
						context.getResources().getString(R.string.shared_pref_ip));
		ServerConfig utils = new ServerConfig();
		utils.setServerIP(ipSaved);

		String url = utils.getAPIServerURL() + Constants.DEVICE_ENDPOINT + deviceInfo.getDeviceId();

		CommonUtils.callSecuredAPI(context, url,
				org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.PUT, replyPayload,
				Operation.this,
				Constants.DEVICE_INFO_REQUEST_CODE
		);

		operation.setPayLoad(replyPayload);
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);
	}

	/**
	 * Retrieve location device information.
	 *
	 * @param operation - Operation object.
	 */
	public void getLocationInfo(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		double latitude;
		double longitude;
		JSONObject result = new JSONObject();

		try {
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();
			result.put(LOCATION_INFO_TAG_LATITUDE, latitude);
			result.put(LOCATION_INFO_TAG_LONGITUDE, longitude);

			operation.setPayLoad(result.toString());
			operation.setStatus(resources.getString(R.string.operation_value_completed));
			resultBuilder.build(operation);
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Device location sent");
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	/**
	 * Retrieve device application information.
	 *
	 * @param operation - Operation object.
	 */
	public void getApplicationList(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

		ArrayList<DeviceAppInfo> apps = new ArrayList<>(appList.getInstalledApps().values());
		JSONArray result = new JSONArray();
		for (DeviceAppInfo infoApp : apps) {
			JSONObject app = new JSONObject();
			try {
				app.put(APP_INFO_TAG_NAME, Uri.encode(infoApp.getAppname()));
				app.put(APP_INFO_TAG_PACKAGE, infoApp.getPackagename());
				app.put(APP_INFO_TAG_ICON, infoApp.getIcon());
				result.put(app);
			} catch (JSONException e) {
				operation.setStatus(resources.getString(R.string.operation_value_error));
				resultBuilder.build(operation);
				throw new AndroidAgentException("Invalid JSON format.", e);
			}
		}
		operation.setPayLoad(result.toString());
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Application list sent");
		}
	}

	/**
	 * Lock the device.
	 *
	 * @param operation - Operation object.
	 */
	public void lockDevice(org.wso2.emm.agent.beans.Operation operation) {

		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);
		devicePolicyManager.lockNow();
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Device locked");
		}
	}

	/**
	 * Ring the device.
	 *
	 * @param operation - Operation object.
	 */
	public void ringDevice(org.wso2.emm.agent.beans.Operation operation) {

		Intent intent = new Intent(context, AlertActivity.class);
		intent.putExtra(resources.getString(R.string.intent_extra_type),
				resources.getString(R.string.intent_extra_ring));
		intent.putExtra(resources.getString(R.string.intent_extra_message),
				resources.getString(R.string.intent_extra_stop_ringing));
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
				Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Ringing is activated on the device");
		}
	}

	/**
	 * Wipe the device.
	 *
	 * @param operation - Operation object.
	 */
	public void wipeDevice(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String inputPin;
		String savedPin =
				Preference.getString(context,
						resources.getString(R.string.shared_pref_pin));
		JSONObject result = new JSONObject();

		try {
			JSONObject wipeKey = new JSONObject(operation.getPayLoad().toString());
			inputPin = (String) wipeKey.get(resources.getString(R.string.shared_pref_pin));
			String status;
			if (inputPin.trim().equals(savedPin.trim())) {
				status = resources.getString(R.string.shared_pref_default_status);
				result.put(resources.getString(R.string.operation_status), status);
			} else {
				status = resources.getString(R.string.shared_pref_false_status);
				result.put(resources.getString(R.string.operation_status), status);
			}

			operation.setPayLoad(result.toString());

			if (inputPin.trim().equals(savedPin.trim())) {
				Toast.makeText(context, resources.getString(R.string.toast_message_wipe),
						Toast.LENGTH_LONG).show();
				try {
					Thread.sleep(PRE_WIPE_WAIT_TIME);
				} catch (InterruptedException e) {
					throw new AndroidAgentException("Wipe pause interrupted.", e);
				}
				operation.setStatus(resources.getString(R.string.operation_value_completed));
				resultBuilder.build(operation);
				devicePolicyManager.wipeData(ACTIVATION_REQUEST);
				if (Constants.DEBUG_MODE_ENABLED) {
					Log.d(TAG, "Started to wipe data");
				}
			} else {
				Toast.makeText(context, resources.getString(R.string.toast_message_wipe_failed),
						Toast.LENGTH_LONG).show();
				operation.setStatus(resources.getString(R.string.operation_value_error));
				resultBuilder.build(operation);
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	/**
	 * Clear device password.
	 *
	 * @param operation - Operation object.
	 */
	public void clearPassword(org.wso2.emm.agent.beans.Operation operation) {
		ComponentName demoDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
				DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
		devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, DEFAULT_PASSWORD_LENGTH);
		devicePolicyManager.resetPassword(resources.getString(R.string.shared_pref_default_string),
				DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
		devicePolicyManager.lockNow();
		devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
				DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Password cleared");
		}
	}

	/**
	 * Display notification.
	 *
	 * @param operation - Operation object.
	 */
	public void displayNotification(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

		try {
			JSONObject inputData = new JSONObject(operation.getPayLoad().toString());
			String message = inputData.getString(resources.getString(R.string.intent_extra_message));

			if (message != null && !message.isEmpty()) {
				operation.setStatus(resources.getString(R.string.operation_value_completed));
				resultBuilder.build(operation);
				Intent intent = new Intent(context, AlertActivity.class);
				intent.putExtra(resources.getString(R.string.intent_extra_message), message);
				intent.putExtra(resources.getString(R.string.intent_extra_type),
						resources.getString(R.string.intent_extra_alert));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
						Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);

				if (Constants.DEBUG_MODE_ENABLED) {
					Log.d(TAG, "Notification received");
				}
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	/**
	 * Configure device WIFI profile.
	 *
	 * @param operation - Operation object.
	 */
	public void configureWifi(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		boolean wifiStatus;
		String ssid = null;
		String password = null;
		JSONObject result = new JSONObject();

		try {
			JSONObject wifiData = new JSONObject(operation.getPayLoad().toString());
			if (!wifiData.isNull(resources.getString(R.string.intent_extra_ssid))) {
				ssid = (String) wifiData.get(resources.getString(R.string.intent_extra_ssid));
			}
			if (!wifiData.isNull(resources.getString(R.string.intent_extra_password))) {
				password =
						(String) wifiData.get(resources.getString(R.string.intent_extra_password));
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}

		WiFiConfig config = new WiFiConfig(context.getApplicationContext());

		wifiStatus = config.saveWEPConfig(ssid, password);

		try {
			String status;
			if (wifiStatus) {
				status = resources.getString(R.string.shared_pref_default_status);
				result.put(resources.getString(R.string.operation_status), status);

			} else {
				status = resources.getString(R.string.shared_pref_false_status);
				result.put(resources.getString(R.string.operation_status), status);
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Wifi configured");
		}
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		operation.setPayLoad(result.toString());
		resultBuilder.build(operation);
	}

	/**
	 * Disable/Enable device camera.
	 *
	 * @param operation - Operation object.
	 */
	public void disableCamera(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

		boolean camFunc = operation.isEnabled();
		ComponentName cameraAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);
		devicePolicyManager.setCameraDisabled(cameraAdmin, !camFunc);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Camera enabled: " + camFunc);
		}
	}

	/**
	 * Install application/bundle.
	 *
	 * @param operation - Operation object.
	 */
	public void installAppBundle(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		try {

			if (operation.getCode().equals(Constants.Operation.INSTALL_APPLICATION)) {
				JSONObject appData = new JSONObject(operation.getPayLoad().toString());
				installApplication(appData, operation);
			} else if (operation.getCode().equals(Constants.Operation.INSTALL_APPLICATION_BUNDLE)) {
				JSONArray jArray;
				jArray = new JSONArray(operation.getPayLoad().toString());
				for (int i = 0; i < jArray.length(); i++) {
					JSONObject appObj = jArray.getJSONObject(i);
					installApplication(appObj, operation);
				}
			}
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Application bundle installation started");
			}

		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	/**
	 * Uninstall application.
	 *
	 * @param operation - Operation object.
	 */
	public void uninstallApplication(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String packageName;
		String type;
		try {
			JSONObject appData = new JSONObject(operation.getPayLoad().toString());

			type = appData.getString(resources.getString(R.string.app_type));

			if (resources.getString(R.string.intent_extra_web).equalsIgnoreCase(type)) {
				String appUrl = appData.getString(resources.getString(R.string.app_url));
				String name = appData.getString(resources.getString(R.string.intent_extra_name));
				String operationType = resources.getString(R.string.operation_uninstall);
				JSONObject payload = new JSONObject();
				payload.put(resources.getString(R.string.intent_extra_identity), appUrl);
				payload.put(resources.getString(R.string.intent_extra_title), name);
				payload.put(resources.getString(R.string.operation_type), operationType);
				operation.setPayLoad(payload.toString());
				manageWebClip(operation);
			} else {
				packageName = appData.getString(resources.getString(R.string.app_identifier));
				appList.uninstallApplication(packageName);
				operation.setStatus(resources.getString(R.string.operation_value_completed));
				resultBuilder.build(operation);
			}

			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Application started to uninstall");
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	/**
	 * Encrypt/Decrypt device storage.
	 *
	 * @param operation - Operation object.
	 */
	public void encryptStorage(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

		boolean doEncrypt = operation.isEnabled();
		JSONObject result = new JSONObject();
		ComponentName admin = new ComponentName(context, AgentDeviceAdminReceiver.class);

		if (doEncrypt &&
				devicePolicyManager.getStorageEncryptionStatus() != DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED &&
				(devicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE)) {

			devicePolicyManager.setStorageEncryption(admin, doEncrypt);
			Intent intent = new Intent(DevicePolicyManager.ACTION_START_ENCRYPTION);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

		} else if (!doEncrypt &&
				devicePolicyManager.getStorageEncryptionStatus() != DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED &&
				(devicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE ||
						devicePolicyManager.getStorageEncryptionStatus() == DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING)) {

			devicePolicyManager.setStorageEncryption(admin, doEncrypt);
		}

		try {
			String status;
			if (devicePolicyManager.getStorageEncryptionStatus() !=
					DevicePolicyManager.ENCRYPTION_STATUS_UNSUPPORTED) {
				status = resources.getString(R.string.shared_pref_default_status);
				result.put(resources.getString(R.string.operation_status), status);

			} else {
				status = resources.getString(R.string.shared_pref_false_status);
				result.put(resources.getString(R.string.operation_status), status);
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			String msg = "Issue in parsing json";
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		}
		operation.setPayLoad(result.toString());
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Encryption process started");
		}

	}

	/**
	 * Mute the device.
	 *
	 * @param operation - Operation object.
	 */
	private void muteDevice(org.wso2.emm.agent.beans.Operation operation) {
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setStreamVolume(AudioManager.STREAM_RING, DEFAULT_VOLUME, DEFAULT_FLAG);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Device muted");
		}

	}

	/**
	 * Create web clip (Web app shortcut on device home screen).
	 *
	 * @param operation - Operation object.
	 */
	public void manageWebClip(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String appUrl;
		String title;
		String operationType;

		try {
			JSONObject webClipData = new JSONObject(operation.getPayLoad().toString());
			appUrl = webClipData.getString(resources.getString(R.string.intent_extra_identity));
			title = webClipData.getString(resources.getString(R.string.intent_extra_title));
			operationType = webClipData.getString(resources.getString(R.string.operation_type));
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}

		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		if (appUrl != null && title != null) {
			appList.manageWebAppBookmark(appUrl, title, operationType);
		}
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Created webclip");
		}
	}

	/**
	 * Set device password policy.
	 *
	 * @param operation - Operation object.
	 */
	public void setPasswordPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		ComponentName demoDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);

		int attempts, length, history, specialChars;
		String alphanumeric, complex;
		boolean isAlphanumeric, isComplex;
		long timout;

		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		try {
			JSONObject policyData = new JSONObject(operation.getPayLoad().toString());
			if (!policyData
					.isNull(resources.getString(R.string.policy_password_max_failed_attempts)) &&
					policyData.get(resources.getString(R.string.policy_password_max_failed_attempts)) !=
							null) {
				attempts = policyData.getInt(resources.getString(R.string.policy_password_max_failed_attempts));
				devicePolicyManager.setMaximumFailedPasswordsForWipe(demoDeviceAdmin, attempts);
			}

			if (!policyData.isNull(resources.getString(R.string.policy_password_min_length)) &&
					policyData.get(resources.getString(R.string.policy_password_min_length)) != null) {
				length = policyData.getInt(resources.getString(R.string.policy_password_min_length));
				devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, length);
			}

			if (!policyData.isNull(resources.getString(R.string.policy_password_pin_history)) &&
					policyData.get(resources.getString(R.string.policy_password_pin_history)) != null) {
				history = policyData.getInt(resources.getString(R.string.policy_password_pin_history));
				devicePolicyManager.setPasswordHistoryLength(demoDeviceAdmin, history);
			}

			if (!policyData
					.isNull(resources.getString(R.string.policy_password_min_complex_chars)) &&
					policyData.get(resources.getString(R.string.policy_password_min_complex_chars)) !=
							null) {
				specialChars = policyData.getInt(resources.getString(R.string.policy_password_min_complex_chars));
				devicePolicyManager.setPasswordMinimumSymbols(demoDeviceAdmin, specialChars);
			}

			if (!policyData
					.isNull(resources.getString(R.string.policy_password_require_alphanumeric)) &&
					policyData
							.get(resources.getString(R.string.policy_password_require_alphanumeric)) !=
							null) {
				if (policyData.get(resources.getString(
						R.string.policy_password_require_alphanumeric)) instanceof String) {
					alphanumeric =
							(String) policyData.get(resources.getString(
									R.string.policy_password_require_alphanumeric));
					if (alphanumeric
							.equals(resources.getString(R.string.shared_pref_default_status))) {
						devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
								DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
					}
				} else if (policyData.get(resources.getString(
						R.string.policy_password_require_alphanumeric)) instanceof Boolean) {
					isAlphanumeric =
							policyData.getBoolean(resources.getString(
									R.string.policy_password_require_alphanumeric));
					if (isAlphanumeric) {
						devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
								DevicePolicyManager.PASSWORD_QUALITY_ALPHANUMERIC);
					}
				}
			}

			if (!policyData.isNull(resources.getString(R.string.policy_password_allow_simple)) &&
					policyData.get(resources.getString(R.string.policy_password_allow_simple)) != null) {

				if (policyData.get(resources.getString(
						R.string.policy_password_allow_simple)) instanceof String) {
					complex =
							(String) policyData.get(resources.getString(
									R.string.policy_password_allow_simple));
					if (!complex.equals(resources.getString(R.string.shared_pref_default_status))) {
						devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
								DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
					}
				} else if (policyData.get(resources.getString(
						R.string.policy_password_allow_simple)) instanceof Boolean) {
					isComplex =
							policyData.getBoolean(
									resources.getString(R.string.policy_password_allow_simple));
					if (!isComplex) {
						devicePolicyManager.setPasswordQuality(demoDeviceAdmin,
								DevicePolicyManager.PASSWORD_QUALITY_COMPLEX);
					}
				}
			}

			if (!policyData.isNull(resources.getString(R.string.policy_password_pin_age_in_days)) &&
					policyData.get(resources.getString(R.string.policy_password_pin_age_in_days)) !=
							null) {
				int daysOfExp = policyData.getInt(resources.getString(R.string.policy_password_pin_age_in_days));
				timout = daysOfExp * DAY_MILLISECONDS_MULTIPLIER;
				devicePolicyManager.setPasswordExpirationTimeout(demoDeviceAdmin, timout);
			}

			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Password policy set");
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}

	}

	/**
	 * Install google play applications.
	 *
	 * @param operation - Operation object.
	 */
	public void installGooglePlayApp(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String packageName;
		try {
			JSONObject appData = new JSONObject(operation.getPayLoad().toString());
			packageName = (String) appData.get(resources.getString(R.string.intent_extra_package));

		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}

		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Started installing GoogleApp");
		}

		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		triggerGooglePlayApp(packageName);
	}

	/**
	 * Open Google Play store application with an application given.
	 *
	 * @param packageName - Application package name.
	 */
	public void triggerGooglePlayApp(String packageName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setData(Uri.parse(Constants.GOOGLE_PLAY_APP_URI + packageName));
		context.startActivity(intent);
	}

	/**
	 * Change device lock code.
	 *
	 * @param operation - Operation object.
	 */
	public void changeLockCode(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		ComponentName demoDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, DEFAULT_PASSWORD_MIN_LENGTH);
		String password = null;

		try {
			JSONObject lockData = new JSONObject(operation.getPayLoad().toString());
			if (!lockData.isNull(resources.getString(R.string.intent_extra_lock_code))) {
				password =
						(String) lockData.get(resources.getString(R.string.intent_extra_lock_code));
			}

			operation.setStatus(resources.getString(R.string.operation_value_completed));
			resultBuilder.build(operation);

			if (password != null && !password.isEmpty()) {
				devicePolicyManager.resetPassword(password,
						DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
				devicePolicyManager.lockNow();
			}

			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Lock code changed");
			}
		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	/**
	 * Set policy bundle.
	 *
	 * @param operation - Operation object.
	 */
	public void setPolicyBundle(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String payload = operation.getPayLoad().toString();
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Policy payload: " + payload);
		}
		PolicyOperationsMapper operationsMapper = new PolicyOperationsMapper();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		try {
			if(payload != null){
				Preference.putString(context, resources.getString(R.string.shared_pref_policy_applied), payload);
			}

			List<org.wso2.emm.agent.beans.Operation> operations = mapper.readValue(
					payload,
					mapper.getTypeFactory().constructCollectionType(List.class,
							org.wso2.emm.agent.beans.Operation.class));

			for (org.wso2.emm.agent.beans.Operation op : operations) {
				op = operationsMapper.getOperation(op);
				this.doTask(op);
			}
			operation.setStatus(resources.getString(R.string.operation_value_completed));
			resultBuilder.build(operation);

			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Policy applied");
			}
		} catch (IOException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			String msg = "Error occurred while parsing stream." + e.getMessage();
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		}
	}

	/**
	 * Monitor currently enforced policy for compliance.
	 *
	 * @param operation - Operation object.
	 */
	public void monitorPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String payload = Preference.getString(context, resources.getString(R.string.shared_pref_policy_applied));

		PolicyOperationsMapper operationsMapper = new PolicyOperationsMapper();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

		PolicyComplianceChecker policyChecker = new PolicyComplianceChecker(context);
		ArrayList<ComplianceFeature> result = new ArrayList<>();

		try {
			if(payload != null) {
				List<org.wso2.emm.agent.beans.Operation> operations = mapper.readValue(
						payload,
						mapper.getTypeFactory().constructCollectionType(List.class,
						                                                org.wso2.emm.agent.beans.Operation.class));
				for (org.wso2.emm.agent.beans.Operation op : operations) {
					op = operationsMapper.getOperation(op);
					result.add(policyChecker.checkPolicyState(op));
				}
				operation.setStatus(resources.getString(R.string.operation_value_completed));
				operation.setPayLoad(mapper.writeValueAsString(result));
				resultBuilder.build(operation);
			}
		} catch (IOException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			String msg = "Error occurred while parsing stream." + e.getMessage();
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		}
	}

	/**
	 * Revoke currently enforced policy.
	 *
	 * @param operation - Operation object.
	 */
	public void revokePolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		CommonUtils.revokePolicy(context);
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);
	}

	/**
	 * Enterprise wipe the device.
	 *
	 * @param operation - Operation object.
	 */
	public void enterpriseWipe(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

		operation.setStatus(resources.getString(R.string.operation_value_completed));
		resultBuilder.build(operation);

		CommonUtils.clearAppData(context);

		Intent intent = new Intent(context, ServerDetails.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Started enterprise wipe");
		}
	}

	/**
	 * Blacklisting apps.
	 *
	 * @param operation - Operation object.
	 */
	public void blacklistApps(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		ArrayList<DeviceAppInfo> apps = new ArrayList<>(appList.getInstalledApps().values());
		JSONArray appList = new JSONArray();
		JSONArray blacklistApps = new JSONArray();
		String identity;
		try {
			JSONObject resultApp = new JSONObject(operation.getPayLoad().toString());
			if (!resultApp.isNull(resources.getString(R.string.app_identifier))) {
				blacklistApps = resultApp.getJSONArray(resources.getString(R.string.app_identifier));
			}

		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
		for (int i = 0; i < blacklistApps.length(); i++) {
			try {
				identity = blacklistApps.getString(i);
				for (DeviceAppInfo app : apps) {
					JSONObject result = new JSONObject();

					result.put(resources.getString(R.string.intent_extra_name), app.getAppname());
					result.put(resources.getString(R.string.intent_extra_package),
							app.getPackagename());
					if (identity.trim().equals(app.getPackagename())) {
						result.put(resources.getString(R.string.intent_extra_not_violated), false);
						result.put(resources.getString(R.string.intent_extra_package),
								app.getPackagename());
					} else {
						result.put(resources.getString(R.string.intent_extra_not_violated), true);
					}
					appList.put(result);
				}
			} catch (JSONException e) {
				operation.setStatus(resources.getString(R.string.operation_value_error));
				resultBuilder.build(operation);
				throw new AndroidAgentException("Invalid JSON format.", e);
			}
		}
		operation.setStatus(resources.getString(R.string.operation_value_completed));
		operation.setPayLoad(appList.toString());
		resultBuilder.build(operation);

		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Marked blacklist app");
		}
	}

	/**
	 * Install an Application
	 *
	 * @param operation - Operation object.
	 */
	private void installApplication(JSONObject data, org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		String appUrl;
		String type;
		String name;
		String operationType;

		try {
			if (!data.isNull(resources.getString(R.string.app_type))) {
				type = data.getString(resources.getString(R.string.app_type));

				if (type.equalsIgnoreCase(resources.getString(R.string.intent_extra_enterprise))) {
					appUrl = data.getString(resources.getString(R.string.app_url));
					operation.setStatus(resources.getString(R.string.operation_value_completed));
					resultBuilder.build(operation);
					appList.installApp(appUrl);

				} else if (type.equalsIgnoreCase(resources.getString(R.string.intent_extra_public))) {
					appUrl = data.getString(resources.getString(R.string.app_identifier));
					operation.setStatus(resources.getString(R.string.operation_value_completed));
					resultBuilder.build(operation);
					triggerGooglePlayApp(appUrl);

				} else if (type.equalsIgnoreCase(resources.getString(R.string.intent_extra_web))) {
					name = data.getString(resources.getString(R.string.intent_extra_name));
					appUrl = data.getString(resources.getString(R.string.app_url));
					operationType = resources.getString(R.string.operation_install);
					JSONObject payload = new JSONObject();
					payload.put(resources.getString(R.string.intent_extra_identity), appUrl);
					payload.put(resources.getString(R.string.intent_extra_title), name);
					payload.put(resources.getString(R.string.operation_type), operationType);
					operation.setPayLoad(payload.toString());
					manageWebClip(operation);

				} else {
					operation.setStatus(resources.getString(R.string.operation_value_error));
					resultBuilder.build(operation);
					throw new AndroidAgentException("Invalid application details");
				}

				if (Constants.DEBUG_MODE_ENABLED) {
					Log.d(TAG, "Application installation started");
				}
			}

		} catch (JSONException e) {
			operation.setStatus(resources.getString(R.string.operation_value_error));
			resultBuilder.build(operation);
			throw new AndroidAgentException("Invalid JSON format.", e);
		}
	}

	public void disenrollDevice(org.wso2.emm.agent.beans.Operation operation) {
		boolean status = operation.isEnabled();
		if (status) {
			CommonUtils.disableAdmin(context);
		} 
	}

	/**
	 * This method returns the completed operations list
	 *
	 * @return operation list
	 */
	public List<org.wso2.emm.agent.beans.Operation> getResultPayload() {
		return resultBuilder.getResultPayload();
	}

	/**
	 * This method is being invoked when get info operation get executed.
	 *
	 * @param result response result
	 * @param requestCode code of the requested operation
	 */
	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String responseStatus;
		String response;
		if (requestCode == Constants.DEVICE_INFO_REQUEST_CODE) {
			if (result != null) {
				responseStatus = result.get(Constants.STATUS_KEY);
				if (Constants.Status.SUCCESSFUL.equals(responseStatus)) {
					response = result.get(Constants.RESPONSE);
					if (response != null && !response.isEmpty()) {
						if (Constants.DEBUG_MODE_ENABLED) {
							Log.d(TAG, "onReceiveAPIResult." + response);
							Log.d(TAG, "Device information sent");

						}
					}
				}
			}
		}
	}
}
