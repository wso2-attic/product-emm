/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.emm.agent.services;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.utils.Constants;

/**
 * This class is used to create specific operation with respect to
 * recieved policy payload.
 */
public class PolicyOperationsMapper {

	private static final String TAG = PolicyOperationsMapper.class.getSimpleName();
	// invalid flag is used to denote operations that are built within agent
	// thus, it does not have to send to server
	private static final int INVALID_FLAG = -1;

	public Operation getOperation(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		switch (operation.getCode()) {
			case Constants.Operation.CAMERA:
				return buildCameraOperation(operation);
			case Constants.Operation.INSTALL_APPLICATION:
				return buildInstallAppOperation(operation);
			case Constants.Operation.UNINSTALL_APPLICATION:
				return buildUninstallAppOperation(operation);
			case Constants.Operation.ENCRYPT_STORAGE:
				return buildEncryptOperation(operation);
			case Constants.Operation.PASSCODE_POLICY:
				return buildPasswordPolicyOperation(operation);
			case Constants.Operation.WIFI:
				return buildWifiOperation(operation);
			default:
				throw new AndroidAgentException("Invalid operation code received");
		}
	}

	private Operation buildCameraOperation(Operation operation) throws AndroidAgentException {
		operation.setId(INVALID_FLAG);
		try {
			JSONObject payload = new JSONObject(operation.getPayLoad().toString());
			boolean enabled = payload.getBoolean("enabled");
			operation.setEnabled(enabled);
			return operation;
		} catch (JSONException e) {
			throw new AndroidAgentException("Error occurred while parsing payload.", e);
		}
	}

	private Operation buildInstallAppOperation(Operation operation) {
		operation.setId(INVALID_FLAG);
		return operation;
	}

	private Operation buildUninstallAppOperation(Operation operation) {
		operation.setId(INVALID_FLAG);
		return operation;
	}

	private Operation buildEncryptOperation(Operation operation) throws AndroidAgentException {
		operation.setId(INVALID_FLAG);
		try {
			JSONObject payload = new JSONObject(operation.getPayLoad().toString());
			boolean encrypt = payload.getBoolean("encrypted");
			operation.setEnabled(encrypt);
			return operation;
		} catch (JSONException e) {
			throw new AndroidAgentException("Error occurred while parsing payload.", e);
		}
	}

	private Operation buildPasswordPolicyOperation(Operation operation) {
		operation.setId(INVALID_FLAG);
		return operation;
	}

	private Operation buildWifiOperation(Operation operation) {
		operation.setId(INVALID_FLAG);
		return operation;
	}
}
