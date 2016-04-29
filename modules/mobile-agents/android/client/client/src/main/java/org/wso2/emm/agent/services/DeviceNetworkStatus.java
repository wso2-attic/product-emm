/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.beans.Device;
import org.wso2.emm.agent.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Network statistics such as connection type and signal details can be fetched
 * from this class.
 */
public class DeviceNetworkStatus extends PhoneStateListener {

    private int cellSignalStrength = 99; // Invalid signal strength is represented with 99.
    Context context;
    WifiManager wifiManager;
    WifiReceiver receiverWifi;
    private ObjectMapper mapper;
    NetworkInfo info;
    private List<ScanResult> wifiScanResults;
    private static final String TAG = DeviceNetworkStatus.class.getName();

    private static final int DEFAULT_AGE = 0;
    private static final String MAC_ADDRESS = "macAddress";
    private static final String SIGNAL_STRENGTH = "signalStrength";
    private static final String AGE = "age";
    private static final String CHANNEL = "channel";
    private static final String SNR = "signalToNoiseRatio";

    private static DeviceNetworkStatus deviceNetworkStatus;

    private DeviceNetworkStatus(Context context) {
        this.context = context;
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        receiverWifi = new WifiReceiver();

        // Register broadcast receiver
        // Broacast receiver will automatically call when number of wifi connections changed
        context.registerReceiver(receiverWifi, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // start scanning wifi
        startWifiScan();

        info = getNetworkInfo(this.context);
        mapper = new ObjectMapper();

    }

    public static DeviceNetworkStatus getInstance(Context context) {
        if (deviceNetworkStatus == null) {
            synchronized (DeviceNetworkStatus.class) {
                if (deviceNetworkStatus == null) {
                    deviceNetworkStatus = new DeviceNetworkStatus(context);
                }
            }
        }
        return deviceNetworkStatus;
    }

    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if (signalStrength.isGsm()) {
            if (signalStrength.getGsmSignalStrength() != 99) {
                // this is the equation used to convert a valid gsm signal to dbm.
                setCellSignalStrength(signalStrength.getGsmSignalStrength() * 2 - 113);
            } else {
                setCellSignalStrength(signalStrength.getGsmSignalStrength());
            }
        } else {
            setCellSignalStrength(signalStrength.getCdmaDbm());
        }
    }

    public int getCellSignalStrength() {
        return cellSignalStrength;
    }

    public void setCellSignalStrength(int cellSignalStrength) {
        this.cellSignalStrength = cellSignalStrength;
    }

    public boolean isConnectedMobile() {
        info = getNetworkInfo(this.context);
        if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return true;
        }
        return false;
    }

    private NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * Network data such as  connection type and signal details can be fetched with this method.
     *
     * @return String representing network details.
     * @throws AndroidAgentException
     */
    public String getNetworkStatus() throws AndroidAgentException {
        info = getNetworkInfo(this.context);
        String payload = null;
        if(info != null) {
            List<Device.Property> properties = new ArrayList<>();
            Device.Property property = new Device.Property();
            property.setName(Constants.Device.CONNECTION_TYPE);
            property.setValue(info.getTypeName());
            properties.add(property);

            if ((info.isConnected())) {
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    property = new Device.Property();
                    property.setName(Constants.Device.MOBILE_CONNECTION_TYPE);
                    property.setValue(info.getSubtypeName());
                    properties.add(property);
                }
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    property = new Device.Property();
                    property.setName(Constants.Device.WIFI_SSID);
                    // NetworkInfo API of Android seem to add extra "" to SSID, therefore escaping it.
                    property.setValue(String.valueOf(getWifiSSID()).replaceAll("\"", ""));
                    properties.add(property);

                    property = new Device.Property();
                    property.setName(Constants.Device.WIFI_SIGNAL_STRENGTH);
                    property.setValue(String.valueOf(getWifiSignalStrength()));
                    properties.add(property);

                }
            }

            property = new Device.Property();
            property.setName(Constants.Device.MOBILE_SIGNAL_STRENGTH);
            property.setValue(String.valueOf(getCellSignalStrength()));
            properties.add(property);

            try {
                payload = mapper.writeValueAsString(properties);
            } catch (JsonProcessingException e) {
                String errorMsg = "Error occurred while parsing " +
                                  "network property property object to json.";
                Log.e(TAG, errorMsg, e);
                throw new AndroidAgentException(errorMsg, e);
            }
        }
        return payload;
    }

    public String getWifiScanResult() throws AndroidAgentException {
        if (wifiScanResults != null) {
            try {
                JSONArray scanResults = new JSONArray();
                JSONObject scanResult;
                for (ScanResult result : wifiScanResults) {
                    scanResult = new JSONObject();
                    scanResult.put(MAC_ADDRESS, result.BSSID);
                    scanResult.put(SIGNAL_STRENGTH, result.level);
                    scanResult.put(AGE, DEFAULT_AGE);
                    scanResult.put(CHANNEL, result.frequency);
                    scanResult.put(SNR, result.level); // temporarily added
                    scanResults.put(scanResult);
                }
                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, "Wifi scan result: " + scanResults.toString());
                }
                // scanning for next round
                startWifiScan();
                return scanResults.toString();
            } catch (JSONException e) {
                String msg = "Error occurred while retrieving wifi scan results";
                Log.e(TAG, msg, e);
                throw new AndroidAgentException(msg);
            }
        }
        return null;
    }

    private int getWifiSignalStrength() {
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getRssi();
        }
        return -1;
    }

    private String getWifiSSID() {
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return wifiInfo.getSSID();
        }
        return null;
    }

    private void startWifiScan() {
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Wifi scanning started...");
        }
        wifiManager.startScan();
    }

    class WifiReceiver extends BroadcastReceiver {

        // This method call when number of wifi connections changed
        public void onReceive(Context c, Intent intent) {
            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Wifi scan result found");
            }
            wifiScanResults = wifiManager.getScanResults();
        }

    }

}