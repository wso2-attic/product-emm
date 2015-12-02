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

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import android.support.v4.content.ContextCompat;
import org.wso2.emm.agent.utils.Response;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;

/**
 * This class represents all the functionalities related to the retrieval of
 * the device state (battery, memory status etc).
 */
public class DeviceState {
	private Context context;
	private DeviceInfo info;
	private File dataDirectory;
	private File [] externalStorageDirectoryList;
	private static final int DEFAULT_LEVEL = -1;
	private static final float PRECENTAGE_MULTIPLIER = 100.0f;
	private static final int SCALE = 2;
	private static final int MEMORY_NOT_AVAILABLE = 0;
	private static final double GB_DIVIDER = 1073741824;
	private static final double MB_DIVIDER = 1048576;
	private static final String INTERNAL_STORAGE_PATH = "storage/emulated/0";

	public DeviceState(Context context) {
		this.context = context;
		this.info = new DeviceInfo(context);
		this.dataDirectory = new File(context.getFilesDir().getAbsoluteFile().toString());
		if (externalMemoryAvailable()) {
			this.externalStorageDirectoryList = ContextCompat.getExternalFilesDirs(context, null);
		}
	}

	/**
	 * Returns whether the external memory is available or not.
	 * @return - External memory status.
	 */
	public boolean externalMemoryAvailable() {
		return android.os.Environment.getExternalStorageState()
		                             .equals(android.os.Environment.MEDIA_MOUNTED);
	}

	/**
	 * Returns the available internal memory size.
	 * @return - Available internal memory size.
	 */
	public double getAvailableInternalMemorySize() {
		long freeBytesInternal = dataDirectory.getFreeSpace();
		return formatSizeInGb(freeBytesInternal);
	}

	/**
	 * Returns the total internal memory size.
	 * @return - Total internal memory size.
	 */
	public double getTotalInternalMemorySize() {
		long totalBytesInternal = dataDirectory.getTotalSpace();
		return formatSizeInGb(totalBytesInternal);
	}

	/**
	 * Returns the available external memory size.
	 * @return - Available external memory size.
	 */
	public double getAvailableExternalMemorySize() {
		long freeBytesExternal = 0;
		if (externalMemoryAvailable()) {
			for (File dir : externalStorageDirectoryList) {
				if (dir != null && !dir.getAbsolutePath().contains(INTERNAL_STORAGE_PATH)) {
					freeBytesExternal += dir.getFreeSpace();
				}
			}
			return formatSizeInGb(freeBytesExternal);
		} else {
			return MEMORY_NOT_AVAILABLE;
		}
	}

	/**
	 * Returns the total external memory size.
	 * @return - Total external memory size.
	 */
	public double getTotalExternalMemorySize() {
		long totalBytesExternal = 0;
		if (externalMemoryAvailable()) {
			for (File dir : externalStorageDirectoryList) {
				if (dir != null && !dir.getAbsolutePath().contains(INTERNAL_STORAGE_PATH)) {
					totalBytesExternal += dir.getTotalSpace();
				}
			}
			return formatSizeInGb(totalBytesExternal);
		} else {
			return MEMORY_NOT_AVAILABLE;
		}
	}

	/**
	 * Returns the string formatted value for the size.
	 * @param byteValue - Memory in bytes.
	 * @return - Memory formatted into GB.
	 */
	public double formatSizeInGb(double byteValue) {
		double gbValue = (byteValue / GB_DIVIDER);
		BigDecimal roundedValue = new BigDecimal(gbValue).setScale(SCALE, RoundingMode.HALF_EVEN);
		gbValue = roundedValue.doubleValue();
		
		return gbValue;
	}

	/**
	 * Returns the string formatted value for the size.
	 * @param byteValue
	 *            - Memory in bytes.
	 * @return - Memory formatted into MB.
	 */
	public double formatSizeInMb(double byteValue) {
		double mbValue = (byteValue / MB_DIVIDER);
		BigDecimal roundedValue = new BigDecimal(mbValue).setScale(SCALE, RoundingMode.HALF_EVEN);
		mbValue = roundedValue.doubleValue();
		
		return mbValue;
	}

	/**
	 * Returns true if the device is compatible to run the agent.
	 * @return - Device compatibility status.
	 */
	public Response evaluateCompatibility() {
		if (!(info.getSdkVersion() >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) &&
				info.isRooted()) {
			return Response.INCOMPATIBLE;
		} else if (info.getSdkVersion() < android.os.Build.VERSION_CODES.FROYO) {
			return Response.INCOMPATIBLE_OS;
		} else if (info.isRooted()) {
			return Response.INCOMPATIBLE_ROOT;
		}
		return Response.COMPATIBLE;
	}

	/**
	 * Returns the device IP address.
	 * @return - Device IP address.
	 */
	public String getIpAddress() {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return intToIp(wifiInfo.getIpAddress());
	}

	/**
	 * Format the integer IP address and return it as a String.
	 * @param ip - IP address should be passed in as an Integer.
	 * @return - Formatted IP address.
	 */
	public String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." +
		       ((ip >> 24) & 0xFF);
	}

	/**
	 * Returns the device battery information.
	 * @return - Battery level.
	 */
	public float getBatteryLevel() {
		Intent batteryIntent = context.registerReceiver(null,
		                                                new IntentFilter(
                                                             Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, DEFAULT_LEVEL);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, DEFAULT_LEVEL);
		return ((float) level / (float) scale) * PRECENTAGE_MULTIPLIER;
	}

}
