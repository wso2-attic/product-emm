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
package org.wso2.emm.agent.beans;

import android.content.Context;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

/**
 * This class represents the server configuration parameters.
 */
public class ServerConfig {
	private String serverIP;
	private String serverURL;
	private String APIServerURL;
	private static final String COLON = ":";

	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getServerURL(Context context) {
		serverURL = getProtocolFromPreferences(context) + getHostFromPreferences(context) + COLON +
		            getPortFromPreferences(context);
		return serverURL;
	}

	public void setServerURL(Context context, String serverURL) {
		this.serverURL = serverURL;
	}

	public String getAPIServerURL(Context context) {
		APIServerURL = getProtocolFromPreferences(context) + getHostFromPreferences(context) + COLON +
		               getPortFromPreferences(context);
		return APIServerURL;
	}

	public String getProtocolFromPreferences (Context context) {
		if (Preference.getString(context, Constants.PreferenceFlag.PROTOCOL) != null) {
			return Preference.getString(context, Constants.PreferenceFlag.PROTOCOL);
		} else {
			return Constants.SERVER_PROTOCOL;
		}
	}

	public String getPortFromPreferences (Context context) {
		if (Preference.getString(context, Constants.PreferenceFlag.PORT) != null) {
			return Preference.getString(context, Constants.PreferenceFlag.PORT);
		} else {
			return Constants.API_SERVER_PORT;
		}
	}

	public String getHostFromPreferences (Context context) {
		if (Preference.getString(context, Constants.PreferenceFlag.IP) != null) {
			return Preference.getString(context, Constants.PreferenceFlag.IP);
		} else {
			return serverIP;
		}
	}

	public void setAPIServerURL(String aPIServerURL) {
		APIServerURL = aPIServerURL;
	}
}
