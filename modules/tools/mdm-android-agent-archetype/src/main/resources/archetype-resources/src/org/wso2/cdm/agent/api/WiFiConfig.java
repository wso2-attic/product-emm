/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.api;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.GroupCipher;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.PairwiseCipher;
import android.net.wifi.WifiConfiguration.Protocol;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.util.Log;

public class WiFiConfig {
	private Context context;
	private static ConnectivityManager connectivityManager;
	private WifiManager wifi_open_config;
	private static final String INT_PRIVATE_KEY = "private_key";
	private static final String INT_PHASE2 = "phase2";
	private static final String INT_PASSWORD = "password";
	private static final String INT_IDENTITY = "identity";
	private static final String INT_EAP = "eap";
	private static final String INT_CLIENT_CERT = "client_cert";
	private static final String INT_CA_CERT = "ca_cert";
	private static final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
	final String INT_ENTERPRISEFIELD_NAME = "android.net.wifi.WifiConfiguration$EnterpriseField";

	public WiFiConfig(Context context) {
		this.context = context;
		wifi_open_config = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (!isConnected(context, ConnectivityManager.TYPE_WIFI)) {
			wifi_open_config.setWifiEnabled(true);
		}
	}

	/**
	 * Checks wether the WIFI is switched on
	 */
	private static boolean isConnected(Context context, int networkType) {
		NetworkInfo networkInfo = null;
		if (connectivityManager != null) {
			networkInfo = connectivityManager.getNetworkInfo(networkType);
		}
		return networkInfo == null ? false : networkInfo.isConnected();
	}

	/**
	 * Saves a WEP WIFI Configuration Profile
	 * @params SSID, PASSWORD
	 *            - WiFi SSID and PASSWORD should be passed in.
	 */
	public boolean saveWEPConfig(String SSID, String PASSWORD) {
		WifiManager wifi = (WifiManager) this.context
				.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + SSID + "\""; // IMP! This should be in Quotes!!
		wc.hiddenSSID = true;
		wc.status = WifiConfiguration.Status.DISABLED;
		wc.priority = 40;
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
		wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

		wc.wepKeys[0] = "\"" + PASSWORD + "\""; // This is the WEP Password
		wc.wepTxKeyIndex = 0;

		WifiManager wifiManag = (WifiManager) this.context
				.getSystemService(this.context.WIFI_SERVICE);
		boolean res1 = wifiManag.setWifiEnabled(true);
		int res = wifi.addNetwork(wc);
		Log.d("WifiPreference", "add Network returned " + res);
		boolean saved = wifi.saveConfiguration();
		Log.d("WifiPreference", "saveConfiguration returned " + saved);
		boolean b = wifi.enableNetwork(res, true);
		Log.d("WifiPreference", "enableNetwork returned " + b);
		return saved;
	}
	
	public boolean removeWiFiConfigurationBySSID(String ssid){
		WifiManager wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> item = wifi.getConfiguredNetworks();

		for(int i=0; i<item.size();i++){
			WifiConfiguration config = item.get(0);
			if(config.SSID.equals(ssid)){
	           int networkId = config.networkId;
	           wifi.removeNetwork(networkId);
	           wifi.saveConfiguration();
			}
		}
		
		return true;
	}

	/**
	 * Read WEP Configuration Profile
	 */
	public boolean readWEPConfig(String ssid) {
		WifiManager wifi = (WifiManager) this.context
				.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> item = wifi.getConfiguredNetworks();
		int i = item.size();
		Log.d("WifiPreference", "NO OF CONFIG " + i);
		Iterator<WifiConfiguration> iter = item.iterator();
		WifiConfiguration config = item.get(0);
		Log.d("WifiPreference", "SSID" + config.SSID);
		Log.d("WifiPreference", "PASSWORD" + config.preSharedKey);
		Log.d("WifiPreference", "ALLOWED ALGORITHMS");
		Log.d("WifiPreference",
				"LEAP" + config.allowedAuthAlgorithms.get(AuthAlgorithm.LEAP));
		Log.d("WifiPreference",
				"OPEN" + config.allowedAuthAlgorithms.get(AuthAlgorithm.OPEN));
		Log.d("WifiPreference",
				"SHARED"
						+ config.allowedAuthAlgorithms
								.get(AuthAlgorithm.SHARED));
		Log.d("WifiPreference", "GROUP CIPHERS");
		Log.d("WifiPreference",
				"CCMP" + config.allowedGroupCiphers.get(GroupCipher.CCMP));
		Log.d("WifiPreference",
				"TKIP" + config.allowedGroupCiphers.get(GroupCipher.TKIP));
		Log.d("WifiPreference",
				"WEP104" + config.allowedGroupCiphers.get(GroupCipher.WEP104));
		Log.d("WifiPreference",
				"WEP40" + config.allowedGroupCiphers.get(GroupCipher.WEP40));
		Log.d("WifiPreference", "KEYMGMT");
		Log.d("WifiPreference",
				"IEEE8021X"
						+ config.allowedKeyManagement.get(KeyMgmt.IEEE8021X));
		Log.d("WifiPreference",
				"NONE" + config.allowedKeyManagement.get(KeyMgmt.NONE));
		Log.d("WifiPreference",
				"WPA_EAP" + config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
		Log.d("WifiPreference",
				"WPA_PSK" + config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
		Log.d("WifiPreference", "PairWiseCipher");
		Log.d("WifiPreference",
				"CCMP" + config.allowedPairwiseCiphers.get(PairwiseCipher.CCMP));
		Log.d("WifiPreference",
				"NONE" + config.allowedPairwiseCiphers.get(PairwiseCipher.NONE));
		Log.d("WifiPreference",
				"TKIP" + config.allowedPairwiseCiphers.get(PairwiseCipher.TKIP));
		Log.d("WifiPreference", "Protocols");
		Log.d("WifiPreference",
				"RSN" + config.allowedProtocols.get(Protocol.RSN));
		Log.d("WifiPreference",
				"WPA" + config.allowedProtocols.get(Protocol.WPA));
		Log.d("WifiPreference", "WEP Key Strings");
		String[] wepKeys = config.wepKeys;
		Log.d("WifiPreference", "WEP KEY 0" + wepKeys[0]);
		Log.d("WifiPreference", "WEP KEY 1" + wepKeys[1]);
		Log.d("WifiPreference", "WEP KEY 2" + wepKeys[2]);
		Log.d("WifiPreference", "WEP KEY 3" + wepKeys[3]);
		if(config.SSID.equals(ssid)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Saves a EAP WIFI Configuration Profile
	 * @params userName, passString, eapMethod, phase2AuthMethod
	 *            - WiFi User Name, Password, EAP Method and Phase2 Authentication(Optional) Method should be passed in.
	 */
	public void saveEAPConfig(String userName, String passString, String eapMethod, String phase2AuthMethod) {
		/******************************** Configuration Strings ****************************************************/
		String ENTERPRISE_EAP = "TTLS";//Should be one of - TLS/TTLS/PEAP/PWD/SIM/AKA/FAST/LEAP
		final String ENTERPRISE_CLIENT_CERT = "keystore://USRCERT_CertificateName";
		final String ENTERPRISE_PRIV_KEY = "keystore://USRPKEY_CertificateName";
		// CertificateName = Name given to the certificate while installing it

		/* Optional Params- My wireless Doesn't use these */
		String ENTERPRISE_PHASE2 = "PAP";//Should be one of - PAP/MSCHAP/MSCHAPV2/GTC
		final String ENTERPRISE_ANON_IDENT = "ABC";
		final String ENTERPRISE_CA_CERT = "";
		/******************************** Configuration Strings ****************************************************/

		if(eapMethod!= null && !eapMethod.equals("")){
			ENTERPRISE_EAP = eapMethod;
		}
		
		if(phase2AuthMethod!= null && !phase2AuthMethod.equals("")){
			ENTERPRISE_PHASE2 = phase2AuthMethod;
		}
		/* Create a WifiConfig */
		WifiConfiguration selectedConfig = new WifiConfiguration();

		/* AP Name */
		selectedConfig.SSID = "\"SSID_Name\"";

		/* Priority */
		selectedConfig.priority = 40;

		/* Enable Hidden SSID */
		selectedConfig.hiddenSSID = true;

		/* Key Mgmnt */
		selectedConfig.allowedKeyManagement.clear();
		selectedConfig.allowedKeyManagement
				.set(WifiConfiguration.KeyMgmt.IEEE8021X);
		selectedConfig.allowedKeyManagement
				.set(WifiConfiguration.KeyMgmt.WPA_EAP);

		/* Group Ciphers */
		selectedConfig.allowedGroupCiphers.clear();
		selectedConfig.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.CCMP);
		selectedConfig.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.TKIP);
		selectedConfig.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.WEP104);
		selectedConfig.allowedGroupCiphers
				.set(WifiConfiguration.GroupCipher.WEP40);

		/* Pairwise ciphers */
		selectedConfig.allowedPairwiseCiphers.clear();
		selectedConfig.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.CCMP);
		selectedConfig.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.TKIP);

		/* Protocols */
		selectedConfig.allowedProtocols.clear();
		selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		selectedConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

		// Enterprise Settings
		// Reflection magic here too, need access to non-public APIs
		try {
			// Let the magic start
			Class[] wcClasses = WifiConfiguration.class.getClasses();
			// null for overzealous java compiler
			Class wcEnterpriseField = null;

			for (Class wcClass : wcClasses)
				if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) {
					wcEnterpriseField = wcClass;
					break;
				}
			boolean noEnterpriseFieldType = false;
			if (wcEnterpriseField == null)
				noEnterpriseFieldType = true; // Cupcake/Donut access enterprise
												// settings directly

			Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
			Field[] wcefFields = WifiConfiguration.class.getFields();
			// Dispatching Field vars
			for (Field wcefField : wcefFields) {
				if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
					wcefAnonymousId = wcefField;
				else if (wcefField.getName().equals(INT_CA_CERT))
					wcefCaCert = wcefField;
				else if (wcefField.getName().equals(INT_CLIENT_CERT))
					wcefClientCert = wcefField;
				else if (wcefField.getName().equals(INT_EAP))
					wcefEap = wcefField;
				else if (wcefField.getName().equals(INT_IDENTITY))
					wcefIdentity = wcefField;
				else if (wcefField.getName().equals(INT_PASSWORD))
					wcefPassword = wcefField;
				else if (wcefField.getName().equals(INT_PHASE2))
					wcefPhase2 = wcefField;
				else if (wcefField.getName().equals(INT_PRIVATE_KEY))
					wcefPrivateKey = wcefField;
			}

			Method wcefSetValue = null;
			if (!noEnterpriseFieldType) {
				for (Method m : wcEnterpriseField.getMethods())
					// System.out.println(m.getName());
					if (m.getName().trim().equals("setValue"))
						wcefSetValue = m;
			}

			/* EAP Method */
			if (!noEnterpriseFieldType) {
				wcefSetValue
						.invoke(wcefEap.get(selectedConfig), ENTERPRISE_EAP);
			} else {
				wcefEap.set(selectedConfig, ENTERPRISE_EAP);
			}
			/* EAP Phase 2 Authentication */
			if (!noEnterpriseFieldType) {
				wcefSetValue.invoke(wcefPhase2.get(selectedConfig),
						ENTERPRISE_PHASE2);
			} else {
				wcefPhase2.set(selectedConfig, ENTERPRISE_PHASE2);
			}
			/* EAP Anonymous Identity */
			if (!noEnterpriseFieldType) {
				wcefSetValue.invoke(wcefAnonymousId.get(selectedConfig),
						ENTERPRISE_ANON_IDENT);
			} else {
				wcefAnonymousId.set(selectedConfig, ENTERPRISE_ANON_IDENT);
			}
			/* EAP CA Certificate */
			if (!noEnterpriseFieldType) {
				wcefSetValue.invoke(wcefCaCert.get(selectedConfig),
						ENTERPRISE_CA_CERT);
			} else {
				wcefCaCert.set(selectedConfig, ENTERPRISE_CA_CERT);
			}
			/* EAP Private key */
			if (!noEnterpriseFieldType) {
				wcefSetValue.invoke(wcefPrivateKey.get(selectedConfig),
						ENTERPRISE_PRIV_KEY);
			} else {
				wcefPrivateKey.set(selectedConfig, ENTERPRISE_PRIV_KEY);
			}
			/* EAP Identity */
			if (!noEnterpriseFieldType) {
				 wcefSetValue.invoke(wcefIdentity.get(selectedConfig), userName);
			} else {
				wcefIdentity.set(selectedConfig, userName);
			}
			/* EAP Password */
			if (!noEnterpriseFieldType) {
				wcefSetValue.invoke(wcefPassword.get(selectedConfig),
						passString);
			} else {
				wcefPassword.set(selectedConfig, passString);
			}
			/* EAp Client certificate */
			if (!noEnterpriseFieldType) {
				wcefSetValue.invoke(wcefClientCert.get(selectedConfig),
						ENTERPRISE_CLIENT_CERT);
			} else {
				wcefClientCert.set(selectedConfig, ENTERPRISE_CLIENT_CERT);
			}
			// Adhoc for CM6
			// if non-CM6 fails gracefully thanks to nested try-catch

			try {
				Field wcAdhoc = WifiConfiguration.class.getField("adhocSSID");
				Field wcAdhocFreq = WifiConfiguration.class
						.getField("frequency");
				// wcAdhoc.setBoolean(selectedConfig,
				// prefs.getBoolean(PREF_ADHOC,
				// false));
				wcAdhoc.setBoolean(selectedConfig, false);
				int freq = 2462; // default to channel 11
				// int freq =
				// Integer.parseInt(prefs.getString(PREF_ADHOC_FREQUENCY,
				// "2462")); // default to channel 11
				// System.err.println(freq);
				wcAdhocFreq.setInt(selectedConfig, freq);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			// FIXME As above, what should I do here?
			e.printStackTrace();
		}

		WifiManager wifiManag = (WifiManager) this.context
				.getSystemService(Context.WIFI_SERVICE);
		boolean res1 = wifiManag.setWifiEnabled(true);
		int res = wifiManag.addNetwork(selectedConfig);
		Log.d("WifiPreference", "add Network returned " + res);
		boolean b = wifiManag.enableNetwork(selectedConfig.networkId, false);
		Log.d("WifiPreference", "enableNetwork returned " + b);
		boolean c = wifiManag.saveConfiguration();
		Log.d("WifiPreference", "Save configuration returned " + c);
		boolean d = wifiManag.enableNetwork(res, true);
		Log.d("WifiPreference", "enableNetwork returned " + d);
	}

	/**
	 * Create a Log File to Log WiFi Profile info
	 */
	public void createLogFile() {
		BufferedWriter out = null;
		try {
			File root = Environment.getExternalStorageDirectory();
			Log.e("SD CARD WRITE ERROR : ", "SD CARD mounted and writable? "
					+ root.canWrite());
			if (root.canWrite()) {
				File gpxfile = new File(root, "ReadConfigLog.txt");
				FileWriter gpxwriter = new FileWriter(gpxfile);
				out = new BufferedWriter(gpxwriter);
				out.write("Hello world");
				// out.close();
			}
		} catch (IOException e) {
			Log.e("SD CARD READ ERROR : ", "Problem reading SD CARD");
			Log.e("SD CARD LOG ERROR : ", "Please take logs using Logcat");
			Log.e("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
					"Could not write file " + e.getMessage());
		}
	}

	/**
	 * Read EAP Configuration Profile
	 */
	public void readEAPConfig(BufferedWriter out) {
		createLogFile();
		/* Get the WifiService */
		WifiManager wifi = (WifiManager) this.context
				.getSystemService(context.WIFI_SERVICE);
		/* Get All WIfi configurations */
		List<WifiConfiguration> configList = wifi.getConfiguredNetworks();
		/*
		 * Now we need to search appropriate configuration i.e. with name
		 * SSID_Name
		 */
		for (int i = 0; i < configList.size(); i++) {
			if (configList.get(i).SSID.contentEquals("\"SSID_NAME\"")) {
				/* We found the appropriate config now read all config details */
				Iterator<WifiConfiguration> iter = configList.iterator();
				WifiConfiguration config = configList.get(i);

				/*
				 * I dont think these fields have anything to do with EAP config
				 * but still will print these to be on safe side
				 */
				try {
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[SSID]"
							+ config.SSID);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>" + "[SSID]"
							+ config.SSID);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[BSSID]"
							+ config.BSSID);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[BSSID]" + config.BSSID);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[HIDDEN SSID]" + config.hiddenSSID);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[HIDDEN SSID]" + config.hiddenSSID);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[PASSWORD]"
							+ config.preSharedKey);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[PASSWORD]" + config.preSharedKey);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[ALLOWED ALGORITHMS]");
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[ALLOWED ALGORITHMS]");
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[LEAP]"
									+ config.allowedAuthAlgorithms
											.get(AuthAlgorithm.LEAP));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[LEAP]"
							+ config.allowedAuthAlgorithms
									.get(AuthAlgorithm.LEAP));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[OPEN]"
									+ config.allowedAuthAlgorithms
											.get(AuthAlgorithm.OPEN));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[OPEN]"
							+ config.allowedAuthAlgorithms
									.get(AuthAlgorithm.OPEN));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[SHARED]"
									+ config.allowedAuthAlgorithms
											.get(AuthAlgorithm.SHARED));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[SHARED]"
							+ config.allowedAuthAlgorithms
									.get(AuthAlgorithm.SHARED));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[GROUP CIPHERS]");
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[GROUP CIPHERS]");
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[CCMP]"
							+ config.allowedGroupCiphers.get(GroupCipher.CCMP));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>" + "[CCMP]"
							+ config.allowedGroupCiphers.get(GroupCipher.CCMP));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[TKIP]"
							+ config.allowedGroupCiphers.get(GroupCipher.TKIP));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>" + "[TKIP]"
							+ config.allowedGroupCiphers.get(GroupCipher.TKIP));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[WEP104]"
									+ config.allowedGroupCiphers
											.get(GroupCipher.WEP104));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP104]"
							+ config.allowedGroupCiphers
									.get(GroupCipher.WEP104));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WEP40]"
							+ config.allowedGroupCiphers.get(GroupCipher.WEP40));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP40]"
							+ config.allowedGroupCiphers.get(GroupCipher.WEP40));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[KEYMGMT]");
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[KEYMGMT]");
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[IEEE8021X]"
									+ config.allowedKeyManagement
											.get(KeyMgmt.IEEE8021X));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[IEEE8021X]"
							+ config.allowedKeyManagement
									.get(KeyMgmt.IEEE8021X));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[NONE]"
							+ config.allowedKeyManagement.get(KeyMgmt.NONE));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>" + "[NONE]"
							+ config.allowedKeyManagement.get(KeyMgmt.NONE));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WPA_EAP]"
							+ config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WPA_EAP]"
							+ config.allowedKeyManagement.get(KeyMgmt.WPA_EAP));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WPA_PSK]"
							+ config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WPA_PSK]"
							+ config.allowedKeyManagement.get(KeyMgmt.WPA_PSK));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[PairWiseCipher]");
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[PairWiseCipher]");
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[CCMP]"
									+ config.allowedPairwiseCiphers
											.get(PairwiseCipher.CCMP));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[CCMP]"
							+ config.allowedPairwiseCiphers
									.get(PairwiseCipher.CCMP));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[NONE]"
									+ config.allowedPairwiseCiphers
											.get(PairwiseCipher.NONE));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[NONE]"
							+ config.allowedPairwiseCiphers
									.get(PairwiseCipher.NONE));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[TKIP]"
									+ config.allowedPairwiseCiphers
											.get(PairwiseCipher.TKIP));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[TKIP]"
							+ config.allowedPairwiseCiphers
									.get(PairwiseCipher.TKIP));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[Protocols]");
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[Protocols]");
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[RSN]"
							+ config.allowedProtocols.get(Protocol.RSN));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>" + "[RSN]"
							+ config.allowedProtocols.get(Protocol.RSN));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WPA]"
							+ config.allowedProtocols.get(Protocol.WPA));
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>" + "[WPA]"
							+ config.allowedProtocols.get(Protocol.WPA));
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[PRE_SHARED_KEY]" + config.preSharedKey);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[PRE_SHARED_KEY]" + config.preSharedKey);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"[WEP Key Strings]");
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP Key Strings]");
					String[] wepKeys = config.wepKeys;
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WEP KEY 0]"
							+ wepKeys[0]);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP KEY 0]" + wepKeys[0]);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WEP KEY 1]"
							+ wepKeys[1]);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP KEY 1]" + wepKeys[1]);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WEP KEY 2]"
							+ wepKeys[2]);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP KEY 2]" + wepKeys[2]);
					Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>", "[WEP KEY 3]"
							+ wepKeys[3]);
					out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
							+ "[WEP KEY 3]" + wepKeys[3]);

				} catch (IOException e) {
					Log.e("WRITE ERROR : ",
							"Failed to write Logs to ReadConfigLog.txt");
					Log.e("WRITE ERROR : ", "Please take logs using Logcat");
					Log.e("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"Could not write to ReadConfigLog.txt"
									+ e.getMessage());
				}
				/* reflection magic */
				/* These are the fields we are really interested in */
				try {
					// Let the magic start
					Class[] wcClasses = WifiConfiguration.class.getClasses();
					// null for overzealous java compiler
					Class wcEnterpriseField = null;

					for (Class wcClass : wcClasses)
						if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) {
							wcEnterpriseField = wcClass;
							break;
						}
					boolean noEnterpriseFieldType = false;
					if (wcEnterpriseField == null)
						noEnterpriseFieldType = true; // Cupcake/Donut access
														// enterprise settings
														// directly

					Field wcefAnonymousId = null, wcefCaCert = null, wcefClientCert = null, wcefEap = null, wcefIdentity = null, wcefPassword = null, wcefPhase2 = null, wcefPrivateKey = null;
					Field[] wcefFields = WifiConfiguration.class.getFields();
					// Dispatching Field vars
					for (Field wcefField : wcefFields) {
						if (wcefField.getName().trim()
								.equals(INT_ANONYMOUS_IDENTITY))
							wcefAnonymousId = wcefField;
						else if (wcefField.getName().trim().equals(INT_CA_CERT))
							wcefCaCert = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_CLIENT_CERT))
							wcefClientCert = wcefField;
						else if (wcefField.getName().trim().equals(INT_EAP))
							wcefEap = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_IDENTITY))
							wcefIdentity = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_PASSWORD))
							wcefPassword = wcefField;
						else if (wcefField.getName().trim().equals(INT_PHASE2))
							wcefPhase2 = wcefField;
						else if (wcefField.getName().trim()
								.equals(INT_PRIVATE_KEY))
							wcefPrivateKey = wcefField;
					}
					Method wcefValue = null;
					if (!noEnterpriseFieldType) {
						for (Method m : wcEnterpriseField.getMethods())
							// System.out.println(m.getName());
							if (m.getName().trim().equals("value")) {
								wcefValue = m;
								break;
							}
					}

					/* EAP Method */
					String result = null;
					Object obj = null;
					if (!noEnterpriseFieldType) {
						obj = wcefValue.invoke(wcefEap.get(config), null);
						String retval = (String) obj;
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP METHOD]" + retval);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP METHOD]" + retval);
					} else {
						obj = wcefEap.get(config);
						String retval = (String) obj;
					}

					/* phase 2 */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefPhase2.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP PHASE 2 AUTHENTICATION]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP PHASE 2 AUTHENTICATION]" + result);
					} else {
						result = (String) wcefPhase2.get(config);
					}

					/* Anonymous Identity */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefAnonymousId.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP ANONYMOUS IDENTITY]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP ANONYMOUS IDENTITY]" + result);
					} else {
						result = (String) wcefAnonymousId.get(config);
					}

					/* CA certificate */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefCaCert.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP CA CERTIFICATE]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP CA CERTIFICATE]" + result);
					} else {
						result = (String) wcefCaCert.get(config);

					}

					/* private key */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefPrivateKey.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP PRIVATE KEY]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP PRIVATE KEY]" + result);
					} else {
						result = (String) wcefPrivateKey.get(config);
					}

					/* Identity */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefIdentity.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP IDENTITY]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP IDENTITY]" + result);
					} else {
						result = (String) wcefIdentity.get(config);
					}

					/* Password */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefPassword.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP PASSWORD]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP PASSWORD]" + result);
					} else {
						result = (String) wcefPassword.get(config);
					}

					/* client certificate */
					if (!noEnterpriseFieldType) {
						result = (String) wcefValue.invoke(
								wcefClientCert.get(config), null);
						Log.d("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
								"[EAP CLIENT CERT]" + result);
						out.write("<<<<<<<<<<WifiPreference>>>>>>>>>>>>"
								+ "[EAP CLIENT CERT]" + result);
						Log.e("READ EROR : ",
								"All config data logged to ReadConfigLog.txt");
						Log.e("READ EROR : ",
								"Extract ReadConfigLog.txt from SD CARD");
					} else {
						result = (String) wcefClientCert.get(config);
					}

					out.close();

				} catch (IOException e) {
					Log.e("LOGGING EROR : ",
							"Failed to write Logs to ReadConfigLog.txt");
					Log.e("LOGGING EROR : ", "Please take logs using Logcat");
					Log.e("<<<<<<<<<<WifiPreference>>>>>>>>>>>>",
							"Could not write to ReadConfigLog.txt"
									+ e.getMessage());
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}

}
