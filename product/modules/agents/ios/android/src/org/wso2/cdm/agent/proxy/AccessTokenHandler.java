package org.wso2.cdm.agent.proxy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * After receiving authorization code client application can use this class to obtain access token
 */
public class AccessTokenHandler extends Activity {
    private static final String TAG = "AccessTokenHandler";
    private String username = null;
    private String password = null;
    private String clientID = null;
    private String clientSecret = null;
    private String tokenEndPoint = null;

    public AccessTokenHandler(String clientID, String clientSecret, String username, String password, String tokenEndPoint, CallBack callBack) {
        this.username = username;
        this.password = password;
        this.clientID = clientID;
        this.clientSecret = clientSecret;
        this.tokenEndPoint = tokenEndPoint;
    }

    public void obtainAccessToken() {
        new NetworkCallTask().execute();
    }

    /**
     * AsyncTask to contact authorization server and get access token, refresh token as a result
     */
    private class NetworkCallTask extends AsyncTask<Void, Void, String> {
        private String response = null;
        private String responseCode = null;
        public NetworkCallTask() {

        }

        @Override
        protected String doInBackground(Void... arg0) {
            Map<String, String> request_params = new HashMap<String, String>();
            request_params.put("grant_type", "password");
            request_params.put("username", username);
            request_params.put("password", password);
            APIUtilities apiUtilities = new APIUtilities();
            apiUtilities.setEndPoint(tokenEndPoint);
            apiUtilities.setHttpMethod("POST");
            apiUtilities.setRequestParams(request_params);
               
            Map<String, String> headers = new HashMap<String, String>();
            String authorizationString = "Basic " + new String(Base64.encodeBase64((clientID + ":" + clientSecret).getBytes()));
            headers.put("Authorization", authorizationString);
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            
            Map<String, String> response_params = ServerUtilitiesTemp.postData(apiUtilities,headers);
            response = response_params.get("response");
            responseCode = response_params.get("status");
            return response;
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(String result) {
            String refreshToken = null;
            String accessToken = null;
            int timeToExpireSecond=3000;
            try {
                IdentityProxy identityProxy = IdentityProxy.getInstance();

                if (responseCode != null && responseCode.equals("200")) {
                	JSONObject response = new JSONObject(result);
                    
                    try{
                    	accessToken = response.getString("access_token");
                    	refreshToken = response.getString("refresh_token");
                    	timeToExpireSecond = Integer.parseInt(response.getString("expires_in"));
                    	Token token = new Token();   	
                        token.setDate();
                        token.setRefreshToken(refreshToken);
                        token.setAccessToken(accessToken);
                        token.setExpired(false);
                        
                        SharedPreferences mainPref = IdentityProxy.getInstance().getContext().getSharedPreferences("com.mdm",Context.MODE_PRIVATE);
                        Editor editor = mainPref.edit();
                        editor.putString("access_token", accessToken);
                        editor.putString("refresh_token",refreshToken);
                        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                        Date date = new Date();
                        long expiresIN=date.getTime()+(timeToExpireSecond*1000);
                        Date expireDate=new Date(expiresIN);                  
                        String strDate = dateFormat.format(expireDate);
                        token.setDate(strDate);
                        editor.putString("date",strDate);
                        editor.commit();
                        
                        identityProxy.receiveAccessToken(responseCode, "success", token);
                    } catch (JSONException e) {//admin user
                    	
                    }
                    
                    

                } else if (responseCode != null) {
                	if("500".equals(responseCode)){
                		identityProxy.receiveAccessToken(responseCode, result, null);
                	}else{
                		JSONObject mainObject = new JSONObject(result);
                        String error = mainObject.getString("error");
                        String errorDescription = mainObject.getString("error_description");
                        Log.d(TAG, error);
                        Log.d(TAG, errorDescription);
                        identityProxy.receiveAccessToken(responseCode, errorDescription, null);
                	}
                }
            } catch (JSONException e) {
                Log.d(TAG,e.toString());
            }
        }
    }
}
