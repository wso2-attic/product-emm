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
import java.util.List;

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.LockActivity;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.ServerDetails;
import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.api.GPSTracker;
import org.wso2.emm.agent.dao.NotificationDAO;
import org.wso2.emm.agent.factory.OperationManagerFactory;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.CommonUtils;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * This class handles all the functionalities related to device management operations.
 */
public class OperationProcessor {

	private Context context;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName cdmDeviceAdmin;
	private ApplicationManager appList;
	private Resources resources;
	private ResultPayload resultBuilder;
	private DeviceInfo deviceInfo;
	private GPSTracker gps;
	private NotificationDAO notificationDAO;
    private NotificationManager notifyManager;
    private int currentNotificationId;
    private ApplicationManager applicationManager;
    private OperationManager operationManager;


	private static final String TAG = "Operation Handler";

	private static final String LOCATION_INFO_TAG_LONGITUDE = "longitude";
	private static final String LOCATION_INFO_TAG_LATITUDE = "latitude";
	private static final String APP_INFO_TAG_NAME = "name";
	private static final String APP_INFO_TAG_PACKAGE = "package";
	private static final String APP_INFO_TAG_VERSION = "version";
	private static final int DEFAULT_PASSWORD_LENGTH = 0;
	private static final int DEFAULT_VOLUME = 0;
	private static final int DEFAULT_FLAG = 0;
	private static final int DEFAULT_PASSWORD_MIN_LENGTH = 4;
	private static final long DAY_MILLISECONDS_MULTIPLIER = 24 * 60 * 60 * 1000;
    private static String[] AUTHORIZED_PINNING_APPS;
    private static String AGENT_PACKAGE_NAME;

	public OperationProcessor(Context context) {
		this.context = context;
		this.resources = context.getResources();
		this.devicePolicyManager =
				(DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		this.cdmDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
		this.appList = new ApplicationManager(context.getApplicationContext());
		this.resultBuilder = new ResultPayload();
		deviceInfo = new DeviceInfo(context.getApplicationContext());
		gps = new GPSTracker(context.getApplicationContext());
		notificationDAO = new NotificationDAO(context);
        AGENT_PACKAGE_NAME = context.getPackageName();
        AUTHORIZED_PINNING_APPS = new String[]{AGENT_PACKAGE_NAME};
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        applicationManager = new ApplicationManager(context);
        /* Get matching OperationManager from the Factory */
        OperationManagerFactory operationManagerFactory = new OperationManagerFactory(context);
        operationManager = operationManagerFactory.getOperationManager(this);

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
				if(devicePolicyManager.isAdminActive(cdmDeviceAdmin)) {
					this.setPolicyBundle(operation);
				}
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
			default:
				if(applicationManager.isPackageInstalled(Constants.SERVICE_PACKAGE_NAME)) {
					CommonUtils.callSystemApp(context,operation.getCode(),
							                    Boolean.toString(operation.isEnabled()), null);
				} else {
					Log.e(TAG, "Invalid operation code received");
				}
				break;
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
				Preference.putString(context, Constants.PreferenceFlag.APPLIED_POLICY, payload);
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
			throw new AndroidAgentException("Error occurred while parsing stream", e);
		}
	}

	public void checkPreviousNotifications() {
        operationManager.checkPreviousNotifications();
    }

    public List<org.wso2.emm.agent.beans.Operation> getResultPayload() {
        return operationManager.getResultPayload();
    }
}

