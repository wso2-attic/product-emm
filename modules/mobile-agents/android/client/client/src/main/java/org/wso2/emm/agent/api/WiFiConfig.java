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


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.wso2.emm.agent.beans.WifiProfile;
import org.wso2.emm.agent.utils.Constants;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * This class handles all the functionalities related to device WIFI configuration.
 */
public class WiFiConfig {
    private static ConnectivityManager connectivityManager;
    private WifiManager wifiManager;
    private static final int WIFI_CONFIG_PRIORITY = 40;
    private static final int WIFI_CONFIG_DEFAULT_INDEX = 0;
    private static final String TAG = WiFiConfig.class.getName();
    private Context context;

    public WiFiConfig(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!isConnected(context, ConnectivityManager.TYPE_WIFI)) {
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * Checks whether the WIFI is switched on.
     *
     * @param context     - Application context.
     * @param networkType - Network type (WIFI/Data).
     */
    private static boolean isConnected(Context context, int networkType) {
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getNetworkInfo(networkType);
        }
        return networkInfo == null ? false : networkInfo.isConnected();
    }


    /**
     * Saves WIFI Configuration Profile.
     *
     * @param profile - WIFI Profile.
     */
    public boolean setWifiConfig(final WifiProfile profile) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        boolean isSaveSuccessful = false;
        boolean isNetworkEnabled = false;
        int result = 0;

        if (profile != null && profile.getType() != null) {
            wifiConfig.SSID = "\"" + profile.getSsid() + "\"";
            switch (profile.getType()) {
                case WEP:
                    wifiConfig.hiddenSSID = true;
                    wifiConfig.status = WifiConfiguration.Status.CURRENT;
                    wifiConfig.priority = WIFI_CONFIG_PRIORITY;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                    wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                    wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                    wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                    wifiConfig.wepKeys[WIFI_CONFIG_DEFAULT_INDEX] = "\"" + profile.getPassword() + "\"";
                    wifiConfig.wepTxKeyIndex = WIFI_CONFIG_DEFAULT_INDEX;

                    break;
                case WPA:
                    wifiConfig.status = WifiConfiguration.Status.ENABLED;
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                    wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                    wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                    wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

                    break;
                case EAP:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig();
                        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);

                        switch (profile.getEapMethod()) {
                            case PEAP:
                                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
                                enterpriseConfig.setIdentity(profile.getIdentity());
                                enterpriseConfig.setAnonymousIdentity(profile.getAnonymousIdentity());
                                enterpriseConfig.setPassword(profile.getPassword());
                                switch (profile.getPhase2()) {
                                    case GTC:
                                        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.GTC);
                                        break;
                                    case MCHAPV2:
                                        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
                                        break;
                                    default:
                                        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
                                }
                                enterpriseConfig.setCaCertificate(getCertifcate(profile));
                                break;
                            case TLS:
                                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TLS);
                                enterpriseConfig.setIdentity(profile.getIdentity());
                                enterpriseConfig.setCaCertificate(getCertifcate(profile));
                                //User cert
                                break;
                            case TTLS:
                                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.TTLS);
                                enterpriseConfig.setIdentity(profile.getIdentity());
                                switch (profile.getPhase2()) {
                                    case GTC:
                                        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.GTC);
                                        break;
                                    case MCHAPV2:
                                        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
                                        break;
                                    default:
                                        enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.NONE);
                                }
                                enterpriseConfig.setCaCertificate(getCertifcate(profile));
                                break;
                            case PWD:
                                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PWD);
                                enterpriseConfig.setIdentity(profile.getIdentity());
                                enterpriseConfig.setPassword(profile.getPassword());
                                break;
                            case SIM:
                                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.SIM);
                                break;
                            case AKA:
                                enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.AKA);
                                break;
                        }

                        wifiConfig.enterpriseConfig = enterpriseConfig;
                    }
                    break;
                default:
                    wifiConfig.hiddenSSID = true;
                    wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }
        }

        wifiManager.setWifiEnabled(true);
        result = wifiManager.addNetwork(wifiConfig);
        isSaveSuccessful = wifiManager.saveConfiguration();
        isNetworkEnabled = wifiManager.enableNetwork(result, true);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "add Network returned." + result);
            Log.d(TAG, "saveConfiguration returned." + isSaveSuccessful);
            Log.d(TAG, "enableNetwork returned." + isNetworkEnabled);
        }

        return isSaveSuccessful;
    }

    /**
     * get CA certificate for installation
     * @param profile - WIFI profile
     */
    private X509Certificate getCertifcate(WifiProfile profile) {
        X509Certificate cert = null;
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            cert = (X509Certificate) certFactory.generateCertificate
                    (new ByteArrayInputStream(Base64.decode(profile.getCaCert(), Base64.DEFAULT)));
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return cert;
    }

    /**
     * Saves a WEP WIFI Configuration Profile.
     *
     * @param ssid     - WIFI network SSID.
     * @param password - WIFI network password.
     */
    @Deprecated
    public boolean saveWEPConfig(String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";
        wifiConfig.hiddenSSID = true;
        wifiConfig.status = WifiConfiguration.Status.CURRENT;
        wifiConfig.priority = WIFI_CONFIG_PRIORITY;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

        wifiConfig.wepKeys[WIFI_CONFIG_DEFAULT_INDEX] = "\"" + password + "\"";
        wifiConfig.wepTxKeyIndex = WIFI_CONFIG_DEFAULT_INDEX;

        wifiManager.setWifiEnabled(true);
        int result = wifiManager.addNetwork(wifiConfig);

        boolean isSaveSuccessful = wifiManager.saveConfiguration();

        boolean isNetworkEnabled = wifiManager.enableNetwork(result, true);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "add Network returned." + result);
            Log.d(TAG, "saveConfiguration returned." + isSaveSuccessful);
            Log.d(TAG, "enableNetwork returned." + isNetworkEnabled);
        }

        return isSaveSuccessful;
    }

    /**
     * Remove WIFI Configuration By SSID.
     *
     * @param ssid - SSID of the WIFI profile which needs to be removed.
     */
    public boolean removeWifiConfigurationBySsid(String ssid) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        boolean isRemoved = false;
        for (WifiConfiguration configuration : configuredNetworks) {
            if (configuration.SSID.equals(ssid)) {
                wifiManager.removeNetwork(configuration.networkId);
                wifiManager.saveConfiguration();
                isRemoved = true;
                break;
            }
        }

        return isRemoved;
    }

    /**
     * Find WIFI Configuration By SSID.
     *
     * @param ssid - SSID of the WIFI profile which needs to be found.
     */
    public boolean findWifiConfigurationBySsid(String ssid) {
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        boolean isAvailable = false;
        for (WifiConfiguration configuration : configuredNetworks) {
            if (configuration.SSID.equals(ssid)) {
                isAvailable = true;
                break;
            }
        }
        return isAvailable;
    }
}
