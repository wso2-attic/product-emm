/*
 * ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 * ~
 * ~ Licensed under the Apache License, Version 2.0 (the "License");
 * ~ you may not use this file except in compliance with the License.
 * ~ You may obtain a copy of the License at
 * ~
 * ~ http://www.apache.org/licenses/LICENSE-2.0
 * ~
 * ~ Unless required by applicable law or agreed to in writing, software
 * ~ distributed under the License is distributed on an "AS IS" BASIS,
 * ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * ~ See the License for the specific language governing permissions and
 * ~ limitations under the License.
 */
package org.wso2.cdm.agent.api;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.json.JSONArray;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.Responce;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;

public class DeviceInfo {
	String deviceModel = null;
	String osVersion = null;
	int sdkVersion = 0;
	String device = null;
	String imsi = null;
	String mac = null;
	String deviceId = null;
	String manufacturer = null;
	String networkOperatorName = "No Sim";
	Root rootChecker = null;
	Context context = null;
	double gbDivider = 1073741824;
	double mbDivider = 1048576;
	long ERROR = 0;

	// private static DeviceInfo deviceInfo = null;

	public DeviceInfo(Context context) {
		this.context = context;
	}

	/**
	 * Returns the network operator name
	 */
	public JSONArray getNetworkOperatorName() {
		JSONArray jsonArray = null;
		final TelephonyManager tm =
		                            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (CommonUtilities.DEBUG_MODE_ENABLED) {
			Log.e("Network OP", tm.getSimOperatorName());
		}
		if (tm.getSimOperatorName() != null && tm.getSimOperatorName() != "") {
			networkOperatorName = tm.getSimOperatorName();
		} else {
			networkOperatorName = "No Sim";
		}

		SharedPreferences mainPref = context.getSharedPreferences("com.mdm", Context.MODE_PRIVATE);
		try {
			jsonArray = new JSONArray(mainPref.getString("operators", "[]"));
			boolean simstatus = false;
			if (jsonArray.length() > 0) {
				for (int i = 0; i < jsonArray.length(); i++) {
					if ((jsonArray.getString(i) != null) &&
					    jsonArray.getString(i).trim().equals(tm.getSimOperatorName())) {
						simstatus = true;
					}
				}
				if (!simstatus) {
					jsonArray.put(tm.getSimOperatorName());
				}
			} else {
				jsonArray.put(tm.getSimOperatorName());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Editor editor = mainPref.edit();
		editor.putString("operators", jsonArray.toString());
		editor.commit();

		return jsonArray;
	}

	/**
	 * Returns the device model
	 */
	public String getDeviceModel() {
		deviceModel = android.os.Build.MODEL;
		return deviceModel;
	}

	public String getDeviceManufacturer() {
		manufacturer = Build.MANUFACTURER;
		return manufacturer;
	}

	/**
	 * Returns the OS Version
	 */
	public String getOsVersion() {
		osVersion = android.os.Build.VERSION.RELEASE;
		return osVersion;
	}

	/**
	 * Returns the SDK Version number
	 */
	public int getSdkVersion() {
		sdkVersion = android.os.Build.VERSION.SDK_INT;
		return sdkVersion;
	}

	/**
	 * Returns the device name
	 */
	public String getDevice() {
		device = android.os.Build.DEVICE;
		return device;
	}

	/**
	 * Returns the IMEI Number
	 */
	public String getDeviceId() {
		final TelephonyManager tm =
		                            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			deviceId = tm.getDeviceId();
			if (deviceId == null || deviceId.length() == 0)
				deviceId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return deviceId;
	}

	/**
	 * Returns the IMSI Number
	 */
	public String getIMSINumber() {
		final TelephonyManager tm =
		                            (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		imsi = tm.getSubscriberId();
		return imsi;
	}

	/**
	 * Returns the device WiFi MAC
	 */
	public String getMACAddress() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		mac = wInfo.getMacAddress();
		return mac;
	}

	/**
	 * Returns the Email address of the device owner
	 */
	public String getEmail() {
		/*
		 * AccountManager manager = AccountManager.get(context);
		 * Account[] accounts = manager.getAccountsByType("com.google");
		 * Account account = accounts[0];
		 * Log.v("My Email",account.name.toString());
		 * return account.name.toString();
		 */
		SharedPreferences example = context.getSharedPreferences("com.mdm", Context.MODE_PRIVATE);
		String userString = example.getString("username", "");
		return userString;
	}

	/**
	 * Returns true if the device is a Rooted device
	 */
	public boolean isRooted() {
		rootChecker = new Root();
		return rootChecker.isDeviceRooted();
	}

	/**
	 * Returns the SIM serial number
	 */
	public String getSimSerialNumber() {
		TelephonyManager telemamanger =
		                                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String getSimSerialNumber = telemamanger.getSimSerialNumber();
		return getSimSerialNumber;
	}

	/**
	 * Returns all the sensors available on the device as a List
	 */
	public void getAllSensors() {
		SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> list = sm.getSensorList(Sensor.TYPE_ALL);

		if (CommonUtilities.DEBUG_MODE_ENABLED) {
			for (Sensor s : list) {
				Log.d("SENSORS", s.getName());
			}
		}
	}

	/**
	 * Returns whether the external memory is available or not
	 */
	public boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState()
		                             .equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Returns the available internal memory size
	 */
	public double getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		double blockSize = stat.getBlockSize();
		double availableBlocks = stat.getAvailableBlocks();
		return formatSizeGB(availableBlocks * blockSize);
	}

	/**
	 * Returns the total internal memory size
	 */
	public double getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		double blockSize = stat.getBlockSize();
		double totalBlocks = stat.getBlockCount();
		return formatSizeGB(totalBlocks * blockSize);
	}

	/**
	 * Returns the available external memory size
	 */
	public double getAvailableExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			double blockSize = stat.getBlockSize();
			double availableBlocks = stat.getAvailableBlocks();
			return formatSizeGB(availableBlocks * blockSize);
		} else {
			return ERROR;
		}
	}

	/**
	 * Returns the total external memory size
	 */
	public double getTotalExternalMemorySize() {
		if (externalMemoryAvailable()) {
			File path = Environment.getExternalStorageDirectory();
			StatFs stat = new StatFs(path.getPath());
			double blockSize = stat.getBlockSize();
			double totalBlocks = stat.getBlockCount();
			return formatSizeGB(totalBlocks * blockSize);
		} else {
			return ERROR;
		}
	}

	/**
	 * Returns the string formatted value for the size
	 */
	public double formatSizeGB(double total) {
		double amount = (total / gbDivider);
		BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN);
		amount = bd.doubleValue();
		return amount;
	}

	public double formatSizeMB(double total) {
		double amount = (total / mbDivider);
		BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN);
		amount = bd.doubleValue();
		return amount;
	}

	public Responce isCompatible() {
		if (!(getSdkVersion() >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) &&
		    isRooted()) {
			return Responce.INCOMPATIBLE;

		} else if (getSdkVersion() < android.os.Build.VERSION_CODES.FROYO) {
			return Responce.INCOMPATIBLE;
		} else if (isRooted()) {
			return Responce.INCOMPATIBLE;
		}
		return Responce.COMPATIBLE;
	}

	/*
	 * public static String formatSize(long size) {
	 * String suffix = null;
	 * 
	 * if (size >= 1024) {
	 * suffix = "KB";
	 * size /= 1024;
	 * if (size >= 1024) {
	 * suffix = "MB";
	 * size /= 1024;
	 * if(size >=1024){
	 * suffix = "GB";
	 * }
	 * }
	 * }
	 * 
	 * StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
	 * 
	 * int commaOffset = resultBuffer.length() - 3;
	 * while (commaOffset > 0) {
	 * resultBuffer.insert(commaOffset, ',');
	 * commaOffset -= 3;
	 * }
	 * 
	 * if (suffix != null) resultBuffer.append(suffix);
	 * return resultBuffer.toString();
	 * }
	 */

}
