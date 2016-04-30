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
package org.wso2.emm.agent.services.operationMgt;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.services.PolicyOperationsMapper;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.io.IOException;
import java.util.List;

/**
 * This class handles all the functionalities related to device management operations.
 */
public class OperationProcessor {

	private OperationManager operationManager;
	private Context context;

	private static final String TAG = OperationProcessor.class.getSimpleName();


	public OperationProcessor(Context context) {
		this.context = context;

        /* Get matching OperationManager from the Factory */
		OperationManagerFactory operationManagerFactory = new OperationManagerFactory(context);
		operationManager = operationManagerFactory.getOperationManager();
	}

	/**
	 * Executes device management operations on the device.
	 *
	 * @param operation - Operation object.
	 */
	public void doTask(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		switch (operation.getCode()) {
			case Constants.Operation.DEVICE_INFO:
				operationManager.getDeviceInfo(operation);
				break;
			case Constants.Operation.DEVICE_LOCATION:
				operationManager.getLocationInfo(operation);
				break;
			case Constants.Operation.APPLICATION_LIST:
				operationManager.getApplicationList(operation);
				break;
			case Constants.Operation.DEVICE_LOCK:
				operationManager.lockDevice(operation);
				break;
			case Constants.Operation.DEVICE_UNLOCK:
				operationManager.unlockDevice(operation);
				break;
			case Constants.Operation.WIPE_DATA:
				operationManager.wipeDevice(operation);
				break;
			case Constants.Operation.CLEAR_PASSWORD:
				operationManager.clearPassword(operation);
				break;
			case Constants.Operation.NOTIFICATION:
				operationManager.displayNotification(operation);
				break;
			case Constants.Operation.WIFI:
				operationManager.configureWifi(operation);
				break;
			case Constants.Operation.CAMERA:
				operationManager.disableCamera(operation);
				break;
			case Constants.Operation.INSTALL_APPLICATION:
				operationManager.installAppBundle(operation);
				break;
			case Constants.Operation.INSTALL_APPLICATION_BUNDLE:
				operationManager.installAppBundle(operation);
				break;
			case Constants.Operation.UNINSTALL_APPLICATION:
				operationManager.uninstallApplication(operation);
				break;
			case Constants.Operation.ENCRYPT_STORAGE:
				operationManager.encryptStorage(operation);
				break;
			case Constants.Operation.DEVICE_RING:
				operationManager.ringDevice(operation);
				break;
			case Constants.Operation.DEVICE_MUTE:
				operationManager.muteDevice(operation);
				break;
			case Constants.Operation.WEBCLIP:
				operationManager.manageWebClip(operation);
				break;
			case Constants.Operation.PASSWORD_POLICY:
				operationManager.setPasswordPolicy(operation);
				break;
			case Constants.Operation.INSTALL_GOOGLE_APP:
				operationManager.installGooglePlayApp(operation);
				break;
			case Constants.Operation.CHANGE_LOCK_CODE:
				operationManager.changeLockCode(operation);
				break;
			case Constants.Operation.POLICY_BUNDLE:
				this.setPolicyBundle(operation);
				break;
			case Constants.Operation.WORK_PROFILE:
				operationManager.configureWorkProfile(operation);
				break;
			case Constants.Operation.POLICY_MONITOR:
				operationManager.monitorPolicy(operation);
				break;
			case Constants.Operation.POLICY_REVOKE:
				operationManager.revokePolicy(operation);
				break;
			case Constants.Operation.ENTERPRISE_WIPE:
				operationManager.enterpriseWipe(operation);
				break;
			case Constants.Operation.BLACKLIST_APPLICATIONS:
				operationManager.blacklistApps(operation);
				break;
			case Constants.Operation.DISENROLL:
				operationManager.disenrollDevice(operation);
				break;
			case Constants.Operation.UPGRADE_FIRMWARE:
				operationManager.upgradeFirmware(operation);
				break;
			case Constants.Operation.REBOOT:
				operationManager.rebootDevice(operation);
				break;
			case Constants.Operation.EXECUTE_SHELL_COMMAND:
				operationManager.executeShellCommand(operation);
				break;
			case Constants.Operation.ALLOW_PARENT_PROFILE_APP_LINKING:
				operationManager.handleUserRestriction(operation);
			case Constants.Operation.DISALLOW_CONFIG_VPN:
				operationManager.handleUserRestriction(operation);
			case Constants.Operation.DISALLOW_INSTALL_APPS:
				operationManager.handleUserRestriction(operation);
			case Constants.Operation.VPN:
				operationManager.configureVPN(operation);
				break;
			default:
				operationManager.passOperationToSystemApp(operation);
				break;
		}
	}

	public void checkPreviousNotifications() {
		operationManager.checkPreviousNotifications();
	}

	public List<org.wso2.emm.agent.beans.Operation> getResultPayload() {
		return operationManager.getResultPayload();
	}

	/**
	 * Set policy bundle.
	 *
	 * @param operation - Operation object.
	 */
	public void setPolicyBundle(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
		if (isDeviceAdminActive()) {
			String payload = operation.getPayLoad().toString();
			if (Constants.DEBUG_MODE_ENABLED) {
				Log.d(TAG, "Policy payload: " + payload);
			}
			PolicyOperationsMapper operationsMapper = new PolicyOperationsMapper();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

			try {
				if (payload != null) {
					Preference.putString(context, Constants.PreferenceFlag.APPLIED_POLICY, payload);
				}

				List<Operation> operations = mapper.readValue(
						payload,
						mapper.getTypeFactory().
								constructCollectionType(List.class, org.wso2.emm.agent.beans.Operation.class));

				for (org.wso2.emm.agent.beans.Operation op : operations) {
					op = operationsMapper.getOperation(op);
					this.doTask(op);
				}
				operation.setStatus(context.getResources().getString(R.string.operation_value_completed));
				operationManager.setPolicyBundle(operation);

				if (Constants.DEBUG_MODE_ENABLED) {
					Log.d(TAG, "Policy applied");
				}
			} catch (IOException e) {
				operation.setStatus(context.getResources().getString(R.string.operation_value_error));
				operationManager.setPolicyBundle(operation);
				throw new AndroidAgentException("Error occurred while parsing stream", e);
			}
		} else {
			operation.setStatus(context.getResources().getString(R.string.operation_value_error));
			operationManager.setPolicyBundle(operation);
			throw new AndroidAgentException("Device administrator is not activated, hence cannot execute policies");
		}
	}

	private boolean isDeviceAdminActive() {
		DevicePolicyManager devicePolicyManager =
				(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		ComponentName cdmDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		return devicePolicyManager.isAdminActive(cdmDeviceAdmin);
	}

}

