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

import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.api.DeviceState;
import org.wso2.emm.agent.api.GPSTracker;
import org.wso2.emm.agent.beans.Device;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class handles building of the device information payload to be sent to the server.
 */
public class DeviceInfoPayload {
	private DeviceInfo deviceInfo;
	private Device device;
	private Context context;
	private static final String TAG = DeviceInfoPayload.class.getName();
	private ObjectMapper mapper;
	private DeviceState phoneState;
	private GPSTracker gps;
	private String registrationId ;

	public DeviceInfoPayload(Context context) {
		this.context = context.getApplicationContext();
		deviceInfo = new DeviceInfo(context);
		mapper = new ObjectMapper();
		gps = new GPSTracker(context);
		registrationId = Preference.getString(context, Constants.REG_ID);
		phoneState = new DeviceState(context);
	}

	/**
	 * Builds device information payload.
	 *
	 * @param type - Device ownership type.
	 * @param owner - Current user name.
	 */
	public void build(String type, String owner) throws AndroidAgentException {
		device = new Device();
		Device.EnrolmentInfo info = new Device.EnrolmentInfo();
		//setting up basic details of the device
		info.setOwner(owner);
		device.setDeviceIdentifier(deviceInfo.getDeviceId());
		device.setDescription(deviceInfo.getDeviceName());
		device.setName(deviceInfo.getDeviceName());
		info.setOwnership(type.equals(Constants.OWNERSHIP_BYOD)?Device.EnrolmentInfo.OwnerShip.BYOD
				: Device.EnrolmentInfo.OwnerShip.COPE);
		device.setEnrolmentInfo(info);
		//adding extra properties
		List<Device.Property> properties = new ArrayList<>();
		Device.Property property = new Device.Property();
		property.setName(Constants.Device.IMEI);
		property.setValue(deviceInfo.getDeviceId());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.IMSI);
		property.setValue(deviceInfo.getIMSINumber());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MODEL);
		property.setValue(deviceInfo.getDeviceModel());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.VENDOR);
		property.setValue(deviceInfo.getDeviceManufacturer());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.OS);
		property.setValue(deviceInfo.getOsVersion());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.NAME);
		property.setValue(deviceInfo.getDeviceName());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.BATTERY_LEVEL);
		int batteryLevel = Math.round(phoneState.getBatteryLevel());
		property.setValue(String.valueOf(batteryLevel));
		properties.add(property);

		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();

		if (latitude != 0 && longitude !=0) {
			property = new Device.Property();
			property.setName(Constants.Device.MOBILE_DEVICE_LATITUDE);
			property.setValue(String.valueOf(latitude));
			properties.add(property);

			property = new Device.Property();
			property.setName(Constants.Device.MOBILE_DEVICE_LONGITUDE);
			property.setValue(String.valueOf(longitude));
			properties.add(property);
		}

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_INTERNAL_TOTAL);
		property.setValue(String.valueOf(phoneState.getTotalInternalMemorySize()));
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_INTERNAL_AVAILABLE);
		property.setValue(String.valueOf(phoneState.getAvailableInternalMemorySize()));
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_EXTERNAL_TOTAL);
		property.setValue(String.valueOf(phoneState.getTotalExternalMemorySize()));
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_EXTERNAL_AVAILABLE);
		property.setValue(String.valueOf(phoneState.getAvailableExternalMemorySize()));
		properties.add(property);

		if (registrationId != null) {
			property = new Device.Property();
			property.setName(Constants.Device.GCM_TOKEN);
			property.setValue(registrationId);
			properties.add(property);
		}

		property = new Device.Property();
		property.setName(Constants.Device.NETWORK_OPERATOR);
		property.setValue(String.valueOf(deviceInfo.getNetworkOperatorName()));
		properties.add(property);

		// building device info json payload
		String deviceInfoPayload;
		try {
			deviceInfoPayload = mapper.writeValueAsString(properties);
		} catch (JsonProcessingException e) {
			String errorMsg = "Error occurred while parsing property object to json.";
			Log.e(TAG, errorMsg, e);
			throw new AndroidAgentException(errorMsg, e);
		}
		property = new Device.Property();
		property.setName(Constants.Device.INFO);
		property.setValue(deviceInfoPayload);
		properties.add(property);

		device.setProperties(properties);
	}

	/**
	 * This method builds the payload including device current state.
	 *
	 * @throws AndroidAgentException
	 */
	public void build() throws AndroidAgentException {
		device = new Device();

		device.setDeviceIdentifier(deviceInfo.getDeviceId());
		device.setDescription(deviceInfo.getDeviceName());
		device.setName(deviceInfo.getDeviceName());

		List<Device.Property> properties = new ArrayList<>();
		Device.Property property = new Device.Property();
		property.setName(Constants.Device.IMEI);
		property.setValue(deviceInfo.getDeviceId());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.IMSI);
		property.setValue(deviceInfo.getIMSINumber());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MODEL);
		property.setValue(deviceInfo.getDeviceModel());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.VENDOR);
		property.setValue(deviceInfo.getDeviceManufacturer());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.OS);
		property.setValue(deviceInfo.getOsVersion());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.NAME);
		property.setValue(deviceInfo.getDeviceName());
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.BATTERY_LEVEL);
		int batteryLevel = Math.round(phoneState.getBatteryLevel());
		property.setValue(String.valueOf(batteryLevel));
		properties.add(property);

		double latitude = gps.getLatitude();
		double longitude = gps.getLongitude();

		if (latitude != 0 && longitude !=0) {
			property = new Device.Property();
			property.setName(Constants.Device.MOBILE_DEVICE_LATITUDE);
			property.setValue(String.valueOf(latitude));
			properties.add(property);

			property = new Device.Property();
			property.setName(Constants.Device.MOBILE_DEVICE_LONGITUDE);
			property.setValue(String.valueOf(longitude));
			properties.add(property);
		}

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_INTERNAL_TOTAL);
		property.setValue(String.valueOf(phoneState.getTotalInternalMemorySize()));
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_INTERNAL_AVAILABLE);
		property.setValue(String.valueOf(phoneState.getAvailableInternalMemorySize()));
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_EXTERNAL_TOTAL);
		property.setValue(String.valueOf(phoneState.getTotalExternalMemorySize()));
		properties.add(property);

		property = new Device.Property();
		property.setName(Constants.Device.MEMORY_INFO_EXTERNAL_AVAILABLE);
		property.setValue(String.valueOf(phoneState.getAvailableExternalMemorySize()));
		properties.add(property);

		if (registrationId != null) {
			property = new Device.Property();
			property.setName(Constants.Device.GCM_TOKEN);
			property.setValue(registrationId);
			properties.add(property);
		}

		property = new Device.Property();
		property.setName(Constants.Device.NETWORK_OPERATOR);
		property.setValue(String.valueOf(deviceInfo.getNetworkOperatorName()));
		properties.add(property);

		// building device info json payload
		String deviceInfoPayload;
		try {
			deviceInfoPayload = mapper.writeValueAsString(properties);
		} catch (JsonProcessingException e) {
			String errorMsg = "Error occurred while parsing property object to json.";
			Log.e(TAG, errorMsg, e);
			throw new AndroidAgentException(errorMsg, e);
		}
		property = new Device.Property();
		property.setName(Constants.Device.INFO);
		property.setValue(deviceInfoPayload);
		properties.add(property);

		device.setProperties(properties);
	}

	/**
	 * Returns the final payload.
	 *
	 * @return - Device info payload as a string.
	 */
	public String getDeviceInfoPayload() {
		try {
			return device.toJSON();
		} catch (AndroidAgentException e) {
			Log.e(TAG, "Error occurred while building device info payload", e);
		}
		return null;
	}
}