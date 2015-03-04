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
package org.wso2.mdm.agent.proxy;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * client application specific data
 */
public class IdentityProxy implements CallBack {
	public final static boolean initiated = false;
	private static String TAG = "IdentityProxy";
	private static Token token = null;
	private static IdentityProxy identityProxy = new IdentityProxy();
	private static Context context;
	public static String clientID;
	public static String clientSecret;
	private static String accessTokenURL;
	private APIAccessCallBack apiAccessCallBack;
	private TokenCallBack callback;
	int requestCode = 0;

	public int getRequestCode() {
		return requestCode;
	}

	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}

	private IdentityProxy() {

	}

	public String getAccessTokenURL() {
		return accessTokenURL;
	}

	public void setAccessTokenURL(String accessTokenURL) {
		IdentityProxy.accessTokenURL = accessTokenURL;
	}

	public void receiveAccessToken(String status, String message, Token token) {
		if (token != null) {
			Log.d(TAG, token.getAccessToken());
			Log.d(TAG, token.getRefreshToken());
		}
		IdentityProxy.token = token;
		apiAccessCallBack.onAPIAccessRecive(status);
	}

	public void receiveNewAccessToken(String status, String message, Token token) {
		IdentityProxy.token = token;
		callback.onReceiveTokenResult(token, status);
	}

	public static synchronized IdentityProxy getInstance() {
		return identityProxy;
	}

	// initial point of contact
	public void init(String clientID, String clientSecret, String username, String password,
	                 String tokenEndPoint, APIAccessCallBack apiAccessCallBack, Context contextt) {
		IdentityProxy.clientID = clientID;
		IdentityProxy.clientSecret = clientSecret;
		this.apiAccessCallBack = apiAccessCallBack;
		context = contextt;
		SharedPreferences mainPref = context.getSharedPreferences("com.mdm", Context.MODE_PRIVATE);
		Editor editor = mainPref.edit();
		editor.putString("client_id", clientID);
		editor.putString("client_secret", clientSecret);
		editor.putString("token_endpoint", tokenEndPoint);
		editor.commit();
		setAccessTokenURL(tokenEndPoint);
		AccessTokenHandler accessTokenHandler =
				new AccessTokenHandler(clientID, clientSecret,
				                       username, password,
				                       tokenEndPoint, this);
		accessTokenHandler.obtainAccessToken();
	}

	public void getToken(Context contextt, TokenCallBack call, String clientID, String clientSecret)
			throws Exception,
			       InterruptedException,
			       ExecutionException,
			       TimeoutException {
		context = contextt;
		callback = call;
		IdentityProxy.clientID = clientID;
		IdentityProxy.clientSecret = clientSecret;
		if (token == null) {
			getStoredToken();
		} else {
			boolean isExpired = Token.isValid(token.getDate());
			if (!isExpired) {// not expired
				IdentityProxy.getInstance().receiveNewAccessToken("200", "success", token);
			} else {
				getStoredToken();
			}
		}
	}

	private void getStoredToken()
			throws InterruptedException, ExecutionException, TimeoutException {
		SharedPreferences mainPref = context.getSharedPreferences("com.mdm", Context.MODE_PRIVATE);
		String refreshToken = mainPref.getString("refresh_token", "").toString();
		String accessToken = mainPref.getString("access_token", "").toString();
		String date = mainPref.getString("date", "").toString();
		String endPoint = mainPref.getString("token_endpoint", "").toString();
		setAccessTokenURL(endPoint);

		if (!refreshToken.equals("")) {
			token = new Token();
			token.setDate(date);
			token.setRefreshToken(refreshToken);
			token.setAccessToken(accessToken);
			boolean isExpired = Token.isValid(token.getDate());
			if (!isExpired) {// not expired
				IdentityProxy.getInstance().receiveNewAccessToken("200", "success", token);
			} else {
				refreshToken();
			}
		} else {
			IdentityProxy.getInstance().receiveNewAccessToken("400", "fail", token);
		}
	}

	public void refreshToken() throws InterruptedException, ExecutionException, TimeoutException {
		RefreshTokenHandler refreshTokenHandler = new RefreshTokenHandler(token);
		refreshTokenHandler.obtainNewAccessToken();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		IdentityProxy.context = context;
	}

}
