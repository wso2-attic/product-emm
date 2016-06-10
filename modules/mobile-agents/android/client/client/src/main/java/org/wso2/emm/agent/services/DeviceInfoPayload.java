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

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.api.DeviceState;
import org.wso2.emm.agent.api.RuntimeInfo;
import org.wso2.emm.agent.beans.Device;
import org.wso2.emm.agent.beans.Power;
import org.wso2.emm.agent.services.location.LocationService;
import org.wso2.emm.agent.services.location.impl.LocationServiceImpl;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

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
    private String registrationId;
    private LocationService locationService;

    public DeviceInfoPayload(Context context) {
        this.context = context.getApplicationContext();
        deviceInfo = new DeviceInfo(context);
        mapper = new ObjectMapper();
        registrationId = Preference.getString(context, Constants.GCM_REG_ID);
        phoneState = new DeviceState(context);
        locationService = LocationServiceImpl.getInstance(context);
    }

    /**
     * Builds device information payload.
     *
     * @param type  - Device ownership type.
     * @param owner - Current user name.
     */
    public void build(String type, String owner) throws AndroidAgentException {
        device = new Device();
        Device.EnrolmentInfo info = new Device.EnrolmentInfo();
        //setting up basic details of the device
        info.setOwner(owner);
        info.setOwnership(type.equals(Constants.OWNERSHIP_BYOD) ? Device.EnrolmentInfo.OwnerShip.BYOD
                                                                : Device.EnrolmentInfo.OwnerShip.COPE);
        device.setEnrolmentInfo(info);
        getInfo();
    }

    /**
     * This method builds the payload including device current state.
     *
     * @throws AndroidAgentException
     */
    public void build() throws AndroidAgentException {
        device = new Device();
        getInfo();
    }

    /**
     * Fetch all device runtime information.
     * @throws AndroidAgentException
     */
    private void getInfo() throws AndroidAgentException {

        Location deviceLocation = locationService.getLocation();
        if (device == null) {
            device = new Device();
        }

        Power power = phoneState.getBatteryDetails();
        device.setDeviceIdentifier(deviceInfo.getDeviceId());
        device.setDescription(deviceInfo.getDeviceName());
        device.setName(deviceInfo.getDeviceName());

        List<Device.Property> properties = new ArrayList<>();

        Device.Property property = new Device.Property();
        property.setName(Constants.Device.SERIAL);
        property.setValue(deviceInfo.getDeviceSerialNumber());
        properties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.IMEI);
        property.setValue(deviceInfo.getDeviceId());
        properties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.IMSI);
        property.setValue(deviceInfo.getIMSINumber());
        properties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.MAC);
        property.setValue(deviceInfo.getMACAddress());
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
        property.setName(Constants.Device.OS_BUILD_DATE);
        property.setValue(String.valueOf(deviceInfo.getOSBuildDate()));
        properties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.NAME);
        property.setValue(deviceInfo.getDeviceName());
        properties.add(property);

        if (deviceLocation != null) {
            double latitude = deviceLocation.getLatitude();
            double longitude = deviceLocation.getLongitude();

            if (latitude != 0 && longitude != 0) {
                property = new Device.Property();
                property.setName(Constants.Device.MOBILE_DEVICE_LATITUDE);
                property.setValue(String.valueOf(latitude));
                properties.add(property);

                property = new Device.Property();
                property.setName(Constants.Device.MOBILE_DEVICE_LONGITUDE);
                property.setValue(String.valueOf(longitude));
                properties.add(property);
            }
        }

        if (registrationId != null) {
            property = new Device.Property();
            property.setName(Constants.Device.GCM_TOKEN);
            property.setValue(registrationId);
            properties.add(property);
        }

        List<Device.Property> deviceInfoProperties = new ArrayList<>();

        property = new Device.Property();
        property.setName(Constants.Device.ENCRYPTION_STATUS);
        property.setValue(String.valueOf(deviceInfo.isEncryptionEnabled()));
        deviceInfoProperties.add(property);

        if ((deviceInfo.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP)) {
            property = new Device.Property();
            property.setName(Constants.Device.PASSCODE_STATUS);
            property.setValue(String.valueOf(deviceInfo.isPasscodeEnabled()));
            deviceInfoProperties.add(property);
        }

        property = new Device.Property();
        property.setName(Constants.Device.BATTERY_LEVEL);
        int batteryLevel = Math.round(power.getLevel());
        property.setValue(String.valueOf(batteryLevel));
        deviceInfoProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.MEMORY_INFO_INTERNAL_TOTAL);
        property.setValue(String.valueOf(phoneState.getTotalInternalMemorySize()));
        deviceInfoProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.MEMORY_INFO_INTERNAL_AVAILABLE);
        property.setValue(String.valueOf(phoneState.getAvailableInternalMemorySize()));
        deviceInfoProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.MEMORY_INFO_EXTERNAL_TOTAL);
        property.setValue(String.valueOf(phoneState.getTotalExternalMemorySize()));
        deviceInfoProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.MEMORY_INFO_EXTERNAL_AVAILABLE);
        property.setValue(String.valueOf(phoneState.getAvailableExternalMemorySize()));
        deviceInfoProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.NETWORK_OPERATOR);
        property.setValue(String.valueOf(deviceInfo.getNetworkOperatorName()));
        deviceInfoProperties.add(property);

        DeviceNetworkStatus deviceNetworkStatus = DeviceNetworkStatus.getInstance(context);
        if(deviceNetworkStatus.isConnectedMobile()){
            TelephonyManager telephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(deviceNetworkStatus, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }

        String network = deviceNetworkStatus.getNetworkStatus();
        if(network != null) {
            property = new Device.Property();
            property.setName(Constants.Device.NETWORK_INFO);
            property.setValue(network);
            properties.add(property);
        }

        // adding wifi scan results..
        property = new Device.Property();
        property.setName(Constants.Device.WIFI_SCAN_RESULT);
        property.setValue(deviceNetworkStatus.getWifiScanResult());
        properties.add(property);

        RuntimeInfo runtimeInfo = new RuntimeInfo(context);
        String cpuInfoPayload;
        try {
            cpuInfoPayload = mapper.writeValueAsString(runtimeInfo.getCPUInfo());
        } catch (JsonProcessingException e) {
            String errorMsg = "Error occurred while parsing property CPU info object to json.";
            Log.e(TAG, errorMsg, e);
            throw new AndroidAgentException(errorMsg, e);
        }

        property = new Device.Property();
        property.setName(Constants.Device.CPU_INFO);
        property.setValue(cpuInfoPayload);
        properties.add(property);

        String ramInfoPayload;
        try {
            ramInfoPayload = mapper.writeValueAsString(runtimeInfo.getRAMInfo());
        } catch (JsonProcessingException e) {
            String errorMsg = "Error occurred while parsing property RAM info object to json.";
            Log.e(TAG, errorMsg, e);
            throw new AndroidAgentException(errorMsg, e);
        }

        property = new Device.Property();
        property.setName(Constants.Device.RAM_INFO);
        property.setValue(ramInfoPayload);
        properties.add(property);

        List<Device.Property> batteryProperties = new ArrayList<>();
        property = new Device.Property();
        property.setName(Constants.Device.BATTERY_LEVEL);
        property.setValue(String.valueOf(power.getLevel()));
        batteryProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.SCALE);
        property.setValue(String.valueOf(power.getScale()));
        batteryProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.BATTERY_VOLTAGE);
        property.setValue(String.valueOf(power.getVoltage()));
        batteryProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.HEALTH);
        property.setValue(String.valueOf(power.getHealth()));
        batteryProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.STATUS);
        property.setValue(String.valueOf(power.getStatus()));
        batteryProperties.add(property);

        property = new Device.Property();
        property.setName(Constants.Device.PLUGGED);
        property.setValue(String.valueOf(power.getPlugged()));
        batteryProperties.add(property);

        String batteryInfoPayload;
        try {
            batteryInfoPayload = mapper.writeValueAsString(batteryProperties);
        } catch (JsonProcessingException e) {
            String errorMsg = "Error occurred while parsing property battery info object to json.";
            Log.e(TAG, errorMsg, e);
            throw new AndroidAgentException(errorMsg, e);
        }

        property = new Device.Property();
        property.setName(Constants.Device.BATTERY_INFO);
        property.setValue(batteryInfoPayload);
        properties.add(property);

        // building device info json payload
        String deviceInfoPayload;
        try {
            deviceInfoPayload = mapper.writeValueAsString(deviceInfoProperties);
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
            if(Constants.DEBUG_MODE_ENABLED){
                Log.d(TAG, "device info " + device.toJSON());
            }
            return device.toJSON();
        } catch (AndroidAgentException e) {
            Log.e(TAG, "Error occurred while building device info payload", e);
        }
        return null;
    }
}