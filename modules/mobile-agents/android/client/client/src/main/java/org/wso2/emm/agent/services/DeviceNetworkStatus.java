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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.util.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.beans.Device;
import org.wso2.emm.agent.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Network statistics such as connection type and signal details can be fetched
 * from this class.
 */
public class DeviceNetworkStatus extends PhoneStateListener {

    int cellSignalStrength = 99; // Invalid signal strength is represented with 99.
    Context context;
    WifiManager wifiManager;
    private ObjectMapper mapper;
    NetworkInfo info;
    private static final String TAG = DeviceNetworkStatus.class.getName();

    public DeviceNetworkStatus(Context context) {
        this.context = context;
        wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        info = getNetworkInfo(this.context);
        mapper = new ObjectMapper();
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


    public int getWifiSignalStrength() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getRssi();
    }

    public String getWifiSSID() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }


}