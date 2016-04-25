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

import java.util.List;

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
			case Constants.Operation.WORK_PROFILE:
				return buildWorkProfileOperation(operation);
			case Constants.Operation.DISALLOW_ADJUST_VOLUME:
			case Constants.Operation.DISALLOW_CONFIG_BLUETOOTH:
			case Constants.Operation.DISALLOW_CONFIG_CELL_BROADCASTS:
			case Constants.Operation.DISALLOW_CONFIG_CREDENTIALS:
			case Constants.Operation.DISALLOW_CONFIG_MOBILE_NETWORKS:
			case Constants.Operation.DISALLOW_CONFIG_TETHERING:
			case Constants.Operation.DISALLOW_CONFIG_VPN:
			case Constants.Operation.DISALLOW_CONFIG_WIFI:
			case Constants.Operation.DISALLOW_APPS_CONTROL:
			case Constants.Operation.DISALLOW_CREATE_WINDOWS:
			case Constants.Operation.DISALLOW_CROSS_PROFILE_COPY_PASTE:
			case Constants.Operation.DISALLOW_DEBUGGING_FEATURES:
			case Constants.Operation.DISALLOW_FACTORY_RESET:
			case Constants.Operation.DISALLOW_ADD_USER:
			case Constants.Operation.DISALLOW_INSTALL_APPS:
			case Constants.Operation.DISALLOW_INSTALL_UNKNOWN_SOURCES:
			case Constants.Operation.DISALLOW_MODIFY_ACCOUNTS:
			case Constants.Operation.DISALLOW_MOUNT_PHYSICAL_MEDIA:
			case Constants.Operation.DISALLOW_NETWORK_RESET:
			case Constants.Operation.DISALLOW_OUTGOING_BEAM:
			case Constants.Operation.DISALLOW_OUTGOING_CALLS:
			case Constants.Operation.DISALLOW_REMOVE_USER:
			case Constants.Operation.DISALLOW_SAFE_BOOT:
			case Constants.Operation.DISALLOW_SHARE_LOCATION:
			case Constants.Operation.DISALLOW_SMS:
			case Constants.Operation.DISALLOW_UNINSTALL_APPS:
			case Constants.Operation.DISALLOW_UNMUTE_MICROPHONE:
			case Constants.Operation.DISALLOW_USB_FILE_TRANSFER:
			case Constants.Operation.ALLOW_PARENT_PROFILE_APP_LINKING:
			case Constants.Operation.ENSURE_VERIFY_APPS:
			case Constants.Operation.AUTO_TIME:
			case Constants.Operation.ENABLE_ADMIN:
			case Constants.Operation.SET_SCREEN_CAPTURE_DISABLED:
			case Constants.Operation.SET_STATUS_BAR_DISABLED:
				return buildRestrictionOperation(operation);
			default:
				throw new AndroidAgentException("Invalid operation code received");
		}
	}

	private Operation buildRestrictionOperation(Operation operation) throws AndroidAgentException {
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

	private Operation buildWorkProfileOperation(Operation operation) throws AndroidAgentException{
		operation.setId(INVALID_FLAG);
		return operation;
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
