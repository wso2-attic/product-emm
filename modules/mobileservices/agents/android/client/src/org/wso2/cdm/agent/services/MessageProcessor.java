/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.cdm.agent.services;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.RegistrationActivity;
import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.Constant;
import org.wso2.cdm.agent.utils.Preference;
import org.wso2.cdm.agent.utils.ServerUtils;

import android.content.Context;
import android.util.Log;

/**
 * Used to coordinate the retrieval and processing of messages from the server.
 */
public class MessageProcessor implements APIResultCallBack {

	private String TAG = MessageProcessor.class.getSimpleName();
	private Context context;
	String deviceId;

	/**
	 * Local notification message handler.
	 * 
	 * @param context
	 *            Context of the application.
	 */
	public MessageProcessor(Context context) {
		this.context = context;
		
		deviceId=Preference.get(context, "deviceId");
		if(deviceId ==null){
			DeviceInfo deviceInfo = new DeviceInfo(context);
			deviceId=deviceInfo.getMACAddress();
			Preference.put(context, "deviceId", deviceId);
		}
	}

	/**
	 * @param response
	 *            Response received from the server that needs to be processed
	 *            and applied to the device
	 */
	public void performOperation(String response) {
		try {
			JSONArray operations = new JSONArray(response);
			for (int x = 0; x < operations.length(); x++) {
				String featureCode = operations.getJSONObject(x).getString(Constant.CODE);
				String properties = operations.getJSONObject(x).getString(Constant.PROPERTIES);
				Operation operation = new Operation(context);
				operation.doTask(featureCode, properties, 0);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Call the message retrieval end point of the server to get messages
	 * pending.
	 */
	public void getMessages() {
		String ipSaved =
				Preference.get(context.getApplicationContext(),
				               context.getResources().getString(R.string.shared_pref_ip));
		CommonUtilities.setServerURL(ipSaved);
		String deviceIdentifier = "";
		try {
	         deviceIdentifier = URLEncoder.encode(deviceId, "utf-8");
        } catch (UnsupportedEncodingException e) {
	        e.printStackTrace();
        }
		ServerUtils.callSecuredAPI(context, CommonUtilities.API_SERVER_URL +
		                           CommonUtilities.NOTIFICATION_ENDPOINT+File.separator+deviceIdentifier,
		                           CommonUtilities.GET_METHOD, new JSONObject(), MessageProcessor.this,
		                           CommonUtilities.NOTIFICATION_REQUEST_CODE);
	}

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		String responseStatus;
		String response;
		if (requestCode == CommonUtilities.NOTIFICATION_REQUEST_CODE) {
			if (result != null) {
				responseStatus = result.get(CommonUtilities.STATUS_KEY);
				if (responseStatus != null &&
				    responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
					response = result.get(Constant.RESPONSE);
					if (response != null && !response.equals("")) {
						if (CommonUtilities.DEBUG_MODE_ENABLED) {
							Log.e(TAG, "onReceiveAPIResult- " + response);
						}
						performOperation(response);
					}
				}

			}
		}

	}
}
