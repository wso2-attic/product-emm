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
package org.wso2.emm.agent.api;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import org.wso2.emm.agent.R;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.util.List;

/**
 * This class represents all the device information related APIs.
 */
public class DeviceInfo {
	private Root rootChecker;
	private Context context;
	private Resources resources;
	private TelephonyManager telephonyManager;
	private DevicePolicyManager devicePolicyManager;
	private ComponentName cdmDeviceAdmin;
	private static final String BUILD_DATE_UTC_PROPERTY = "ro.build.date.utc";

	public DeviceInfo(Context context) {
		this.context = context;
		this.resources = context.getResources();
		this.telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		this.devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		cdmDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
	}

	/**
	 * Returns the network operator name.
	 * @return - Network operator name.
	 */
	public String getNetworkOperatorName() {
		return telephonyManager.getSimOperatorName();
	}

	/**
	 * Returns the device model.
	 * @return - Device model.
	 */
	public String getDeviceModel() {	
		return android.os.Build.MODEL;
	}

	/**
	 * Returns the device manufacturer.
	 * @return - Device manufacturer.
	 */
	public String getDeviceManufacturer() {
		return Build.MANUFACTURER;
	}

	/**
	 * Returns the device OS build date.
	 * @return - OS build date.
	 */
	public String getOSBuildDate() {
		if (Constants.SYSTEM_APP_ENABLED) {
			CommonUtils.registerSystemAppReceiver(context);
			CommonUtils.callSystemApp(context, Constants.Operation.GET_FIRMWARE_BUILD_DATE, null, null);
			String buildDate = Preference.getString(context, context.getResources().getString(
					R.string.shared_pref_os_build_date));
			if (buildDate != null) {
				return buildDate;
			} else {
				return String.valueOf(Build.TIME);
			}
		} else {
			return String.valueOf(Build.TIME);
		}
	}

	/**
	 * Returns the OS version.
	 * @return - Device OS version.
	 */
	public String getOsVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * Returns the SDK Version number.
	 * @return - Device android SDK version number.
	 */
	public int getSdkVersion() {
		return android.os.Build.VERSION.SDK_INT;
	}

	/**
	 * Returns the device name.
	 * @return - Device name.
	 */
	public String getDeviceName() {
		return android.os.Build.DEVICE;
	}

	/**
	 * Returns the IMEI Number.
	 * @return - Device IMEI number.
	 */
	public String getDeviceId() {

		String deviceId = null;

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			deviceId = telephonyManager.getDeviceId();
		}

		if (deviceId == null || deviceId.isEmpty()) {
			deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		}
		
		return deviceId;
	}

	/**
	 * Returns the IMSI Number.
	 * @return - Device IMSI number.
	 */
	public String getIMSINumber() {
		return telephonyManager.getSubscriberId();
	}

	/**
	 * Returns the device WiFi MAC.
	 * @return - Device WiFi MAC.
	 */
	public String getMACAddress() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		return wInfo.getMacAddress();
	}

	/**
	 * Returns the Email address of the device owner.
	 * @return - Device owner email address.
	 */
	public String getEmail() {
		return Preference.getString(context,
		                            resources.getString(R.string.shared_pref_username));
	}

	/**
	 * Returns true if the device is a Rooted device.
	 * @return - Device rooted status.
	 */
	public boolean isRooted() {
		rootChecker = new Root();
		return rootChecker.isDeviceRooted();
	}

	/**
	 * Returns the SIM serial number.
	 * @return - Device SIM serial number.
	 */
	public String getSimSerialNumber() {
		return telephonyManager.getSimSerialNumber();
	}

	/**
	 * Returns the hardware serial number.
	 * @return - Hardware serial number.
	 */
	public String getDeviceSerialNumber() {
		return Build.SERIAL;
	}

	/**
	 * Returns all the sensors available on the device as a List.
	 * @return - List of all the sensors available on the device.
	 */
	public List<Sensor> getAllSensors() {
		SensorManager sensorManager =
                              (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		return sensorManager.getSensorList(Sensor.TYPE_ALL);
	}

	/**
	 * This method is used to check the status of storage encryption.
	 * @return Returns the current status.
	 */
	public boolean isEncryptionEnabled() {
		if (isDeviceAdminActive()) {
			switch (devicePolicyManager.getStorageEncryptionStatus()) {
				case DevicePolicyManager.ENCRYPTION_STATUS_ACTIVE:
					return true;
				case DevicePolicyManager.ENCRYPTION_STATUS_INACTIVE:
					return false;
				case DevicePolicyManager.ENCRYPTION_STATUS_ACTIVATING:
					return false;
				default:
					return false;
			}
		}
		return false;
	}

	/**
	 * This method is used to get the status of the current activated passcode.
	 * @return Returns true if sufficient.
	 */
	public boolean isPasscodeEnabled() {
		if (isDeviceAdminActive()) {
			return devicePolicyManager.isActivePasswordSufficient();
		}
		return false;
	}

	private boolean isDeviceAdminActive() {
		return devicePolicyManager.isAdminActive(cdmDeviceAdmin);
	}

}
