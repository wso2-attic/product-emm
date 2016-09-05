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
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.proxy.beans.CredentialInfo;
import org.wso2.emm.agent.proxy.beans.Token;
import org.wso2.emm.agent.proxy.interfaces.CallBack;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * After receiving authorization code client application can use this class to
 * obtain access token.
 */
public class AccessTokenHandler {

    private static final String TAG = "AccessTokenHandler";
    private static final String USERNAME_LABEL = "username";
    private static final String PASSWORD_LABEL = "password";
    private static final String TENANT_DOMAIN_LABEL = "tenantDomain";
    private static final String COLON = ":";
    private static final String SCOPES = "default appm:read device:android:enroll device:android:event:manage " +
            "configuration:view device:android:disenroll";
    private static final DateFormat dateFormat =
            new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.getDefault());
    private CredentialInfo info;

    public AccessTokenHandler(CredentialInfo info, CallBack callBack) {
        this.info = info;
    }

    /**
     * Method to contact authorization server and get access token, refresh
     * token as a result
     */
    public void obtainAccessToken() {
        RequestQueue queue =  null;
        try {
            queue = ServerUtilities.getCertifiedHttpClient();
        } catch (IDPTokenManagerException e) {
            Log.e(TAG, "Failed to retrieve HTTP client", e);
        }

        StringRequest request = new StringRequest(Request.Method.POST, info.getTokenEndPoint(),
                                                  new Response.Listener<String>() {
                                                      @Override
                                                      public void onResponse(String response) {
                                                          Log.d(TAG, response);
                                                      }
                                                  },
                                                  new Response.ErrorListener() {
                                                      @Override
                                                      public void onErrorResponse(VolleyError error) {
                                                          Log.e(TAG, error.toString());
                                                          JSONObject errorObj = new JSONObject();
                                                          try {
                                                              errorObj.put(Constants.ERROR_DESCRIPTION_LABEL, error.toString());
                                                          } catch (JSONException e) {
                                                              Log.e(TAG, "Invalid JSON format", e);
                                                          } finally {
                                                              processTokenResponse(Constants.ACCESS_FAILURE, errorObj.toString());
                                                          }
                                                      }
                                                  })

        {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                processTokenResponse(String.valueOf(response.statusCode), new String(response.data));
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> requestParams = new HashMap<>();
                requestParams.put(Constants.GRANT_TYPE, Constants.GRANT_TYPE_PASSWORD);
                requestParams.put(USERNAME_LABEL, info.getUsername());
                requestParams.put(PASSWORD_LABEL, info.getPassword());
                if (info.getTenantDomain() != null) {
                    requestParams.put(TENANT_DOMAIN_LABEL, info.getTenantDomain());
                }
                requestParams.put(Constants.SCOPE, SCOPES);
                return requestParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                byte[] credentials = Base64.encodeBase64((info.getClientID() + COLON +
                                                          info.getClientSecret()).getBytes());
                String encodedCredentials = new String(credentials);

                Map<String, String> headers = new HashMap<>();
                String authorizationString = Constants.AUTHORIZATION_MODE + encodedCredentials;
                headers.put(Constants.AUTHORIZATION_HEADER, authorizationString);
                headers.put(Constants.CONTENT_TYPE_HEADER, Constants.DEFAULT_CONTENT_TYPE);
                return headers;
            }
        };

        queue.add(request);
    }


    /**
     * Processing token response from the server.
     *
     * @param responseCode - HTTP Response code.
     * @param result       - Service result.
     */
    private void processTokenResponse(String responseCode, String result) {
        String refreshToken;
        String accessToken;
        int timeToExpireSecond;
        try {
            IdentityProxy identityProxy = IdentityProxy.getInstance();

            if (Constants.REQUEST_SUCCESSFUL.equals(responseCode)) {
                JSONObject response = new JSONObject(result);
                try {
                    accessToken = response.getString(Constants.ACCESS_TOKEN);
                    refreshToken = response.getString(Constants.REFRESH_TOKEN);
                    timeToExpireSecond = Integer.parseInt(response.getString(Constants.EXPIRE_LABEL));
                    Token token = new Token();
                    Date date = new Date();
                    String currentDate = dateFormat.format(date);
                    token.setDate(currentDate);
                    token.setRefreshToken(refreshToken);
                    token.setAccessToken(accessToken);
                    token.setExpired(false);

                    SharedPreferences mainPref = IdentityProxy.getInstance().getContext().
                            getSharedPreferences(Constants.APPLICATION_PACKAGE, Context.MODE_PRIVATE);
                    Editor editor = mainPref.edit();
                    editor.putString(Constants.ACCESS_TOKEN, accessToken);
                    editor.putString(Constants.REFRESH_TOKEN, refreshToken);
                    editor.putString(USERNAME_LABEL, info.getUsername());
                    long expiresIn = date.getTime() + (timeToExpireSecond * 1000);
                    Date expireDate = new Date(expiresIn);
                    String strDate = dateFormat.format(expireDate);
                    token.setDate(strDate);
                    editor.putString(Constants.DATE_LABEL, strDate);
                    editor.commit();

                    identityProxy.receiveAccessToken(responseCode, Constants.SUCCESS_RESPONSE, token);
                } catch (JSONException e) {
                    Log.e(TAG, "Invalid JSON format", e);
                }

            } else if (responseCode != null) {
                if (Constants.INTERNAL_SERVER_ERROR.equals(responseCode)) {
                    identityProxy.receiveAccessToken(responseCode, result, null);
                } else {
                    JSONObject mainObject = new JSONObject(result);
                    String errorDescription = mainObject.getString(Constants.ERROR_DESCRIPTION_LABEL);
                    identityProxy.receiveAccessToken(responseCode, errorDescription, null);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Invalid JSON", e);
        }
    }
}
