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
package org.wso2.emm.agent.proxy;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.proxy.beans.Token;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.interfaces.TokenCallBack;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class includes the functionality related to invoking APIs and 
 * return API results to the client.
 */
public class APIController implements TokenCallBack {
	private static final String TAG = "APIController";
	private Token token;
	private String clientKey, clientSecret;
	private APIResultCallBack apiResultCallback;
	private EndPointInfo apiEndPointInfo;
	
	public APIController(String clientKey, String clientSecret){
		this.clientKey = clientKey;
		this.clientSecret = clientSecret;
	}

	public APIController() {
	}

	/**
	 * Invoking an API using retrieved token.
	 *
	 * @param apiEndPointInfo      - Server and API end point information.
	 * @param apiResultCallBack - API result callback data.
	 * @param requestCode       - Request code to avoid response complications.
	 * @param context           - Application context.
	 */
	public void invokeAPI(EndPointInfo apiEndPointInfo, APIResultCallBack apiResultCallBack,
	                      int requestCode, Context context) {

		this.apiResultCallback = apiResultCallBack;
		this.apiEndPointInfo = apiEndPointInfo;

		if (IdentityProxy.getInstance().getContext() == null) {
			IdentityProxy.getInstance().setContext(context);
		}

		IdentityProxy.getInstance().setRequestCode(requestCode);

		IdentityProxy.getInstance().requestToken(IdentityProxy.getInstance().getContext(), this,
				this.clientKey,
				this.clientSecret);
		// temporarily added to support non OAuth calls
		//new NetworkCallTask(apiResultCallback).execute(apiEndPointInfo);
	}

	@Override
	public void onReceiveTokenResult(Token token, String status) {
		this.token = token;
		new NetworkCallTask(apiResultCallback).execute(apiEndPointInfo);
	}

	/**
	 * AsyncTask to contact server and access the API with retrieved token.
	 */
	private class NetworkCallTask extends AsyncTask<EndPointInfo, Void, Map<String, String>> {
		APIResultCallBack apiResultCallBack;

		public NetworkCallTask(APIResultCallBack apiResultCallBack) {
			this.apiResultCallBack = apiResultCallBack;
        }

		@Override
		protected Map<String, String> doInBackground(EndPointInfo... params) {
			EndPointInfo endPointInfo = params[0];

			Map<String, String> responseParams = null;
			String accessToken = token.getAccessToken();
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("Accept", "*/*");
			headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");
			headers.put("Authorization", "Bearer " + accessToken);

			try {
				responseParams = ServerUtilities.postData(endPointInfo, headers);
                if (Constants.DEBUG_ENABLED) {
	                Iterator<Map.Entry<String, String>> iterator = responseParams.entrySet().iterator();
	                while (iterator.hasNext()) {
		                Map.Entry<String, String> respParams = iterator.next();
		                StringBuilder paras = new StringBuilder();
		                paras.append("response-params: key:");
		                paras.append(respParams.getKey());
		                paras.append(", value:");
		                paras.append(respParams.getValue());
		                Log.d(TAG, paras.toString());
	                }
                }

            } catch (IDPTokenManagerException e) {
				Log.e(TAG, "Failed to contact server." + e);
			}

			return responseParams;
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
            apiResultCallBack.onReceiveAPIResult(result, IdentityProxy.getInstance().getRequestCode());
		}
	}

	public void securedNetworkCall(APIResultCallBack callback, int licenseRequestCode,
	                               EndPointInfo apiUtilities, Context context) {

		if (IdentityProxy.getInstance().getContext() == null) {
			IdentityProxy.getInstance().setContext(context);
		}

		IdentityProxy.getInstance().setRequestCode(licenseRequestCode);
		new SecuredNetworkCallTask(callback,licenseRequestCode).execute(apiUtilities);
	}

	public class SecuredNetworkCallTask extends AsyncTask<EndPointInfo, Void, Map<String, String>> {
		APIResultCallBack apiResultCallBack;
		int requestCode;

		public SecuredNetworkCallTask(APIResultCallBack apiResultCallBack, int requestCode) {
			this.apiResultCallBack = apiResultCallBack;
			this.requestCode = requestCode;
		}

		@Override
		protected Map<String, String> doInBackground(EndPointInfo... params) {
			EndPointInfo endPointInfo = params[0];

			Map<String, String> responseParams = null;
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("Content-Type", "application/json");
			headers.put("Accept", "*/*");
			headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");

			try {
				responseParams = ServerUtilities.postData(endPointInfo, headers);
				if (Constants.DEBUG_ENABLED) {
					Iterator<Map.Entry<String, String>> iterator = responseParams.entrySet().iterator();
					while (iterator.hasNext()) {
						Map.Entry<String, String> respParams = iterator.next();
						StringBuilder paras = new StringBuilder();
						paras.append("response-params: key:");
						paras.append(respParams.getKey());
						paras.append(", value:");
						paras.append(respParams.getValue());
						Log.d(TAG, paras.toString());
					}
				}

			} catch (IDPTokenManagerException e) {
				Log.e(TAG, "Failed to contact server." + e);
			}

			return responseParams;
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			apiResultCallBack.onReceiveAPIResult(result, requestCode);
		}
	}

}
