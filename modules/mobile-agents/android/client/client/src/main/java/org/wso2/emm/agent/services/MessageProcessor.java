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

import android.app.admin.DevicePolicyManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.beans.AppInstallRequest;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.beans.ServerConfig;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.services.operation.OperationProcessor;
import org.wso2.emm.agent.utils.AppInstallRequestUtil;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.CommonUtils;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;

/**
 * This class handles all the functionalities related to coordinating the retrieval
 * and processing of messages from the server.
 */
public class MessageProcessor implements APIResultCallBack {

	private String TAG = MessageProcessor.class.getSimpleName();
	private Context context;
	private String deviceId;
	private static final String DEVICE_ID_PREFERENCE_KEY = "deviceId";
	private static List<org.wso2.emm.agent.beans.Operation> replyPayload;
	private OperationProcessor operationProcessor;
	private ObjectMapper mapper;
	private boolean isWipeTriggered = false;
	private boolean isRebootTriggered = false;
	private int operationId;
	private boolean isUpgradeTriggered = false;
	private boolean isShellCommandTriggered = false;
	private DevicePolicyManager devicePolicyManager;
	private static final int ACTIVATION_REQUEST = 47;
	private static final String ERROR_STATE = "ERROR";
	private String shellCommand = null;

	/**
	 * Local notification message handler.
	 *
	 * @param context Context of the application.
	 */
	public MessageProcessor(Context context) {
		this.context = context;

		deviceId = Preference.getString(context, DEVICE_ID_PREFERENCE_KEY);
		operationProcessor = new OperationProcessor(context.getApplicationContext());
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		this.devicePolicyManager =
				(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

		if (deviceId == null) {
			DeviceInfo deviceInfo = new DeviceInfo(context.getApplicationContext());
			deviceId = deviceInfo.getDeviceId();
			Preference.putString(context, DEVICE_ID_PREFERENCE_KEY, deviceId);
		}
	}

	/**
	 * This method executes the set of pending operations which is recieved from the
	 * backend server.
	 *
	 * @param response Response received from the server that needs to be processed
	 *                 and applied to the device.
	 */
	public void performOperation(String response) {

		List<org.wso2.emm.agent.beans.Operation> operations = new ArrayList<>();

		try {
			if (response != null) {
				operations = mapper.readValue(
						response,
						mapper.getTypeFactory().constructCollectionType(List.class,
								org.wso2.emm.agent.beans.Operation.class));
			}


		// check whether if there are any dismissed notifications to be sent
		operationProcessor.checkPreviousNotifications();

		} catch (JsonProcessingException e) {
			Log.e(TAG,  "Issue in json parsing", e);
		} catch (IOException e) {
			Log.e(TAG, "Issue in stream parsing", e);
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Error occurred while checking previous notification", e);
		}

		for (org.wso2.emm.agent.beans.Operation op : operations) {
			try {
				operationProcessor.doTask(op);
			} catch (AndroidAgentException e) {
				Log.e(TAG, "Failed to perform operation", e);
			}
		}
		replyPayload = operationProcessor.getResultPayload();
	}


	/**
	 * Call the message retrieval end point of the server to get messages pending.
	 */
	public void getMessages() throws AndroidAgentException {
		String ipSaved = Constants.DEFAULT_HOST;
		String prefIP = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);
		if (prefIP != null) {
			ipSaved = prefIP;
		}
		ServerConfig utils = new ServerConfig();
		utils.setServerIP(ipSaved);
		String url = utils.getAPIServerURL(context) + Constants.DEVICES_ENDPOINT + deviceId + Constants.NOTIFICATION_ENDPOINT;

		Log.i(TAG, "Get pending operations from: " + url);

		String requestParams;
		ObjectMapper mapper = new ObjectMapper();
		try {
			requestParams =  mapper.writeValueAsString(replyPayload);
			if (replyPayload != null) {
				for (org.wso2.emm.agent.beans.Operation operation : replyPayload) {
					if (operation.getCode().equals(Constants.Operation.WIPE_DATA) && !operation.getStatus().
							equals(ERROR_STATE)) {
						isWipeTriggered = true;
					} else if (operation.getCode().equals(Constants.Operation.REBOOT) && !operation.getStatus().
							equals(ERROR_STATE)) {
						isRebootTriggered = true;
					} else if (operation.getCode().equals(Constants.Operation.UPGRADE_FIRMWARE) && !operation.getStatus().
							equals(ERROR_STATE)) {
						isUpgradeTriggered = true;
						Preference.putInt(context, "firmwareOperationId", operation.getId());
					} else if (operation.getCode().equals(Constants.Operation.EXECUTE_SHELL_COMMAND) && !operation.getStatus().
							equals(ERROR_STATE)) {
						isShellCommandTriggered = true;
						try {
							JSONObject payload = new JSONObject(operation.getPayLoad().toString());
							shellCommand = (String) payload.get(context.getResources().getString(R.string.shared_pref_command));
						} catch (JSONException e) {
							throw new AndroidAgentException("Invalid JSON format.", e);
						}
					}
				}
			}
			int firmwareOperationId = Preference.getInt(context, context.getResources().getString(
					R.string.firmware_upgrade_response_id));
			if (firmwareOperationId != 0) {
				org.wso2.emm.agent.beans.Operation firmwareOperation = new org.wso2.emm.agent.beans.Operation();
				firmwareOperation.setId(firmwareOperationId);
				firmwareOperation.setCode(Constants.Operation.UPGRADE_FIRMWARE);
				firmwareOperation.setStatus(Preference.getString(context, context.getResources().getString(
						R.string.firmware_upgrade_response_status)));
				boolean isRetryPending = Preference.getBoolean(context, context.getResources().
						getString(R.string.firmware_upgrade_retry_pending));
				if (isRetryPending) {
                    isUpgradeTriggered = true;
					int retryCount = Preference.getInt(context, context.getResources().
							getString(R.string.firmware_upgrade_retries));
					firmwareOperation.setOperationResponse("Attempt " + retryCount +
							" has failed due to: " + Preference.getString(context, context.getResources().getString(
							R.string.firmware_upgrade_response_message)));
				} else {
					firmwareOperation.setOperationResponse(Preference.getString(context, context.getResources().getString(
							R.string.firmware_upgrade_response_message)));
				}
				if (replyPayload != null) {
					replyPayload.add(firmwareOperation);
				} else {
					replyPayload = new ArrayList<>();
					replyPayload.add(firmwareOperation);
				}
				Preference.putInt(context, context.getResources().getString(
						R.string.firmware_upgrade_response_id), 0);
				Preference.putString(context, context.getResources().getString(
						R.string.firmware_upgrade_response_status), context.getResources().getString(
						R.string.operation_value_error));
				Preference.putString(context, context.getResources().getString(
						R.string.firmware_upgrade_response_message), null);
			}

			int applicationOperationId = Preference.getInt(context, context.getResources().getString(
					R.string.app_install_id));
			String applicationOperationCode = Preference.getString(context, context.getResources().getString(
					R.string.app_install_code));
			String applicationOperationStatus = Preference.getString(context, context.getResources().getString(
					R.string.app_install_status));
			String applicationOperationMessage = Preference.getString(context, context.getResources().getString(
					R.string.app_install_failed_message));
			if (applicationOperationStatus != null && applicationOperationId != 0 && applicationOperationCode != null) {
				Operation applicationOperation = new Operation();
				ApplicationManager appMgt = new ApplicationManager(context);
				applicationOperation.setId(applicationOperationId);
				applicationOperation.setCode(applicationOperationCode);
				applicationOperation = appMgt.getApplicationInstallationStatus(
						applicationOperation, applicationOperationStatus, applicationOperationMessage);
				if (replyPayload == null) {
					replyPayload = new ArrayList<>();
				}
				replyPayload.add(applicationOperation);
				Preference.putString(context, context.getResources().getString(
						R.string.app_install_status), null);
				Preference.putString(context, context.getResources().getString(
						R.string.app_install_failed_message), null);
				if (context.getResources().getString(R.string.operation_value_error).equals(applicationOperation.getStatus()) ||
						context.getResources().getString(R.string.operation_value_completed).equals(applicationOperation.getStatus())){
					Preference.putInt(context, context.getResources().getString(
							R.string.app_install_id), 0);
					Preference.putString(context, context.getResources().getString(
							R.string.app_install_code), null);
					startPendingInstallation();
				}
			} else {
				startPendingInstallation();
			}

			if (Preference.hasPreferenceKey(context, Constants.Operation.LOGCAT)){
				if (Preference.hasPreferenceKey(context, Constants.Operation.LOGCAT)) {
					Gson operationGson = new Gson();
					Operation logcatOperation = operationGson.fromJson(Preference
							.getString(context, Constants.Operation.LOGCAT), Operation.class);
					if (replyPayload == null) {
						replyPayload = new ArrayList<>();
					}
					replyPayload.add(logcatOperation);
					Preference.removePreference(context, Constants.Operation.LOGCAT);
				}
			}
			requestParams =  mapper.writeValueAsString(replyPayload);
		} catch (JsonMappingException e) {
			throw new AndroidAgentException("Issue in json mapping", e);
		} catch (JsonGenerationException e) {
			throw new AndroidAgentException("Issue in json generation", e);
		} catch (IOException e) {
			throw new AndroidAgentException("Issue in parsing stream", e);
		}
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "Reply Payload: " + requestParams);
		}

		if (requestParams != null && requestParams.trim().equals(context.getResources().getString(
				R.string.operation_value_null))) {
			requestParams = null;
		}

		if (ipSaved != null && !ipSaved.isEmpty()) {
			CommonUtils.callSecuredAPI(context, url,
			                           HTTP_METHODS.PUT, requestParams, MessageProcessor.this,
			                           Constants.NOTIFICATION_REQUEST_CODE
			);
		} else {
			Log.e(TAG, "There is no valid IP to contact the server");
		}
	}

	private void startPendingInstallation(){
		AppInstallRequest appInstallRequest = AppInstallRequestUtil.getPending(context);
		if (appInstallRequest != null) {
			ApplicationManager applicationManager = new ApplicationManager(context.getApplicationContext());
			Operation applicationOperation = new Operation();
			applicationOperation.setId(appInstallRequest.getApplicationOperationId());
			applicationOperation.setCode(appInstallRequest.getApplicationOperationCode());
			Log.d(TAG, "Try to start app installation from queue.");
			applicationManager.installApp(appInstallRequest.getAppUrl(), null, applicationOperation);
		}
	}

	@SuppressWarnings("unused")
	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String responseStatus;
		String response;
		if (requestCode == Constants.NOTIFICATION_REQUEST_CODE) {
			if (isWipeTriggered) {
				if(Constants.SYSTEM_APP_ENABLED) {
					CommonUtils.callSystemApp(context, Constants.Operation.WIPE_DATA, null, null);
				} else {
					Log.i(TAG, "Not the device owner.");
				}
			}

			if (isRebootTriggered) {
				CommonUtils.callSystemApp(context, Constants.Operation.REBOOT, null, null);
			}

			if (isUpgradeTriggered) {
				String schedule = Preference.getString(context, context.getResources().getString(R.string.pref_key_schedule));
				CommonUtils.callSystemApp(context, Constants.Operation.UPGRADE_FIRMWARE, schedule, null);
			}

			if (isShellCommandTriggered && shellCommand != null) {
				CommonUtils.callSystemApp(context, Constants.Operation.EXECUTE_SHELL_COMMAND, shellCommand, null);
			}

			if (result != null) {
				responseStatus = result.get(Constants.STATUS_KEY);
				if (Constants.Status.SUCCESSFUL.equals(responseStatus) || Constants.Status.CREATED.equals(responseStatus)) {
					response = result.get(Constants.RESPONSE);
					if (response != null && !response.isEmpty()) {
						if (Constants.DEBUG_MODE_ENABLED) {
							Log.d(TAG, "Pending Operations List: " + response);
						}
						performOperation(response);
					}
				}
			}
		}
	}

}
