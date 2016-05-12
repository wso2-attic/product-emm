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
package org.wso2.emm.agent.proxy.utils;

import android.util.Log;
import com.android.volley.RequestQueue;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.clients.CommunicationClient;
import org.wso2.emm.agent.proxy.clients.CommunicationClientFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class represents all the utilities used for network communication between SDK 
 * and authorization server.
 */
public class ServerUtilities {
	private final static String TAG = "ServerUtilities";
	private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss",
	                                                                  Locale.getDefault());


	/**
	 * Validate the token expiration date.
	 *
	 * @param expirationDate - Token expiration date.
	 * @return - Token status.
	 */
	public static boolean isValid(Date expirationDate) {
		Date currentDate = new Date();
		String formattedDate = dateFormat.format(currentDate);
		currentDate = convertDate(formattedDate);

		boolean isExpired = currentDate.after(expirationDate);
		boolean isEqual = currentDate.equals(expirationDate);
		return isExpired || isEqual;

	}

	/**
	 * Convert the date to the standard format.
	 *
	 * @param date - Date as a string.
	 * @return - Formatted date.
	 */
	public static Date convertDate(String date) {
		Date receivedDate = null;
		try {
			receivedDate = dateFormat.parse(date);
		} catch (ParseException e) {
			Log.e(TAG, "Invalid date format." + e);
		}

		return receivedDate;
	}

	/**
	 * Get HTTP client object according to the calling protocol type.
	 */
	public static RequestQueue getCertifiedHttpClient() throws IDPTokenManagerException {
		CommunicationClientFactory communicationClientFactory = new CommunicationClientFactory();
		CommunicationClient communicationClient = communicationClientFactory.
				getClient(Constants.HttpClient.HTTP_CLIENT_IN_USE);
		return communicationClient.getHttpClient();
	}

}
