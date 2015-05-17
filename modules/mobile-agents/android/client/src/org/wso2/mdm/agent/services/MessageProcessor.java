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
package org.wso2.mdm.agent.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wso2.mdm.agent.AndroidAgentException;
import org.wso2.mdm.agent.R;
import org.wso2.mdm.agent.api.DeviceInfo;
import org.wso2.mdm.agent.beans.ServerConfig;
import org.wso2.mdm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.mdm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.mdm.agent.utils.Constants;
import org.wso2.mdm.agent.utils.Preference;
import org.wso2.mdm.agent.utils.CommonUtils;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class handles all the functionalities related to coordinating the retrieval
 * and processing of messages from the server.
 */
public class MessageProcessor implements APIResultCallBack {

	private String TAG = MessageProcessor.class.getSimpleName();
	private Context context;
	private String deviceId;
	private static final String DEVICE_ID_PREFERENCE_KEY = "deviceId";
	private static List<org.wso2.mdm.agent.beans.Operation> replyPayload;
	private org.wso2.mdm.agent.services.Operation operation;
	private ObjectMapper mapper;

	/**
	 * Local notification message handler.
	 *
	 * @param context Context of the application.
	 */
	public MessageProcessor(Context context) {
		this.context = context;

		deviceId = Preference.getString(context, DEVICE_ID_PREFERENCE_KEY);
		operation = new org.wso2.mdm.agent.services.Operation(context.getApplicationContext());
		mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		if (deviceId == null) {
			DeviceInfo deviceInfo = new DeviceInfo(context.getApplicationContext());
			deviceId = deviceInfo.getMACAddress();
			Preference.putString(context, DEVICE_ID_PREFERENCE_KEY, deviceId);
		}
	}

	/**
	 * @param response Response received from the server that needs to be processed
	 *                 and applied to the device.
	 */
	public void performOperation(String response) throws AndroidAgentException {

		List<org.wso2.mdm.agent.beans.Operation> operations = new ArrayList<>();

		try {
			if (response != null) {
				operations = mapper.readValue(
						response,
						mapper.getTypeFactory().constructCollectionType(List.class,
								org.wso2.mdm.agent.beans.Operation.class));
			}

		} catch (JsonProcessingException e) {
			String msg = "Issue in json parsing.";
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);

		} catch (IOException e) {
			String msg = "Issue in stream parsing.";
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		}


		for (org.wso2.mdm.agent.beans.Operation op : operations) {
			operation.doTask(op);
		}
		replyPayload = operation.getResultPayload();

	}


	/**
	 * Call the message retrieval end point of the server to get messages pending.
	 */
	public void getMessages() throws AndroidAgentException {
		String ipSaved =
				Preference.getString(context.getApplicationContext(),
						context.getResources().getString(R.string.shared_pref_ip));
		ServerConfig utils = new ServerConfig();
		utils.setServerIP(ipSaved);

		String url = utils.getAPIServerURL() + Constants.NOTIFICATION_ENDPOINT + deviceId;
		Log.i(TAG, "getMessage: calling-endpoint: " + url);

		String requestParams;
		try {
			ObjectMapper mapper = new ObjectMapper();
			requestParams =  mapper.writeValueAsString(replyPayload);
		} catch (JsonMappingException e) {
			String msg = "Issue in json mapping.";
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		} catch (JsonGenerationException e) {
			String msg = "Issue in json generation.";
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		} catch (IOException e) {
			String msg = "Issue in parsing stream.";
			Log.e(TAG, msg);
			throw new AndroidAgentException(msg, e);
		}
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.d(TAG, "replay-payload: " + requestParams);
		}

		CommonUtils.callSecuredAPI(context, url,
				HTTP_METHODS.PUT, requestParams, MessageProcessor.this,
				Constants.NOTIFICATION_REQUEST_CODE
		);
	}

	@SuppressWarnings("unused")
	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String responseStatus;
		String response;
		if (requestCode == Constants.NOTIFICATION_REQUEST_CODE) {
			if (result != null) {
				responseStatus = result.get(Constants.STATUS_KEY);
				if (Constants.REQUEST_SUCCESSFUL.equals(responseStatus)) {
					response = result.get(Constants.RESPONSE);
					if (response != null && !response.isEmpty()) {
						if (Constants.DEBUG_MODE_ENABLED) {
							Log.d(TAG, "onReceiveAPIResult." + response);
						}
						try {
							performOperation(response);
						} catch (AndroidAgentException e) {
							Log.e(TAG, "Failed to perform operation." + e);
						}
					}
				}
			}
		}
	}

}
