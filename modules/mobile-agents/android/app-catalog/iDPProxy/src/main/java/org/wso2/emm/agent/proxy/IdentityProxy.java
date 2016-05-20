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
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import org.wso2.emm.agent.proxy.beans.CredentialInfo;
import org.wso2.emm.agent.proxy.beans.Token;
import org.wso2.emm.agent.proxy.interfaces.APIAccessCallBack;
import org.wso2.emm.agent.proxy.interfaces.CallBack;
import org.wso2.emm.agent.proxy.interfaces.TokenCallBack;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;

/**
 * This class handles identity proxy library initialization and token validation.
 */
public class IdentityProxy implements CallBack {

    public static String clientID;
    public static String clientSecret;
    private static String TAG = "IdentityProxy";
    private static Token token = null;
    private static IdentityProxy identityProxy = new IdentityProxy();
    private Context context;
    private static String accessTokenURL;
    private APIAccessCallBack apiAccessCallBack;
    private TokenCallBack tokenCallBack;
    private int requestCode = 0;

    private IdentityProxy() {

    }

    public static synchronized IdentityProxy getInstance() {
        return identityProxy;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public synchronized void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getAccessTokenURL() {
        return accessTokenURL;
    }

    public void setAccessTokenURL(String accessTokenURL) {
        IdentityProxy.accessTokenURL = accessTokenURL;
    }

    @Override
    public void receiveAccessToken(String status, String message, Token token) {
        if (token != null) {
            Log.d(TAG, "Access Token: " + token.getAccessToken());
            Log.d(TAG, "Refresh Token: " + token.getRefreshToken());
        }

        IdentityProxy.token = token;
        apiAccessCallBack.onAPIAccessReceive(status);
    }

    @Override
    public void receiveNewAccessToken(String status, String message, Token token) {
        IdentityProxy.token = token;
        tokenCallBack.onReceiveTokenResult(token, status);
    }

    /**
     * Initializing the IDP plugin and obrtaining the access token.
     *
     * @param info              - Includes token end point and Oauth app credentials.
     * @param apiAccessCallBack - Callback when API access happens.
     * @param context        - Application context.
     */
    public void init(CredentialInfo info, APIAccessCallBack apiAccessCallBack, Context context) {
        IdentityProxy.clientID = info.getClientID();
        IdentityProxy.clientSecret = info.getClientSecret();
        this.apiAccessCallBack = apiAccessCallBack;
        this.context = context;
        SharedPreferences mainPref = context.getSharedPreferences(Constants.APPLICATION_PACKAGE
                , Context.MODE_PRIVATE);
        Editor editor = mainPref.edit();
        editor.putString(Constants.CLIENT_ID, clientID);
        editor.putString(Constants.CLIENT_SECRET, clientSecret);
        editor.putString(Constants.TOKEN_ENDPOINT, info.getTokenEndPoint());
        editor.commit();
        setAccessTokenURL(info.getTokenEndPoint());
        AccessTokenHandler accessTokenHandler = new AccessTokenHandler(info, this);
        accessTokenHandler.obtainAccessToken();
    }

    public void requestToken(Context context, TokenCallBack tokenCallBack, String clientID,
                             String clientSecret) {
        this.context = context;
        this.tokenCallBack = tokenCallBack;
        IdentityProxy.clientID = clientID;
        IdentityProxy.clientSecret = clientSecret;
        if (token == null) {
            validateStoredToken();
        } else {
            boolean isExpired = ServerUtilities.isValid(token.getDate());
            if (!isExpired) {
                synchronized(this){
                    IdentityProxy.getInstance().receiveNewAccessToken(Constants.REQUEST_SUCCESSFUL,
                            "success", token);
                }
            } else {
                validateStoredToken();
            }
        }
    }

    private void validateStoredToken() {
        SharedPreferences mainPref = context.getSharedPreferences(Constants.APPLICATION_PACKAGE,
                Context.MODE_PRIVATE);
        String refreshToken = mainPref.getString(Constants.REFRESH_TOKEN, null).toString();
        String accessToken = mainPref.getString(Constants.ACCESS_TOKEN, null).toString();
        String date = mainPref.getString(Constants.DATE_LABEL, null).toString();
        String endPoint = mainPref.getString(Constants.TOKEN_ENDPOINT, null).toString();
        setAccessTokenURL(endPoint);

        if (!refreshToken.isEmpty()) {
            token = new Token();
            token.setDate(date);
            token.setRefreshToken(refreshToken);
            token.setAccessToken(accessToken);
            boolean isExpired = ServerUtilities.isValid(token.getDate());
            if (!isExpired) {
                synchronized(this){
                    IdentityProxy.getInstance().receiveNewAccessToken(Constants.REQUEST_SUCCESSFUL,
                            Constants.SUCCESS_RESPONSE,
                            token);
                }
            } else {
                refreshToken();
            }
        } else {
            synchronized(this){
                IdentityProxy.getInstance().receiveNewAccessToken(Constants.ACCESS_FAILURE,
                        Constants.FAILURE_RESPONSE, token);
            }
        }
    }

    public void refreshToken() {
        RefreshTokenHandler refreshTokenHandler = new RefreshTokenHandler(token);
        refreshTokenHandler.obtainNewAccessToken();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}
