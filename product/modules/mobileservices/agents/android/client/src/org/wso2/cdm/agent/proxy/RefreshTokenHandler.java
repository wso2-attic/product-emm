package org.wso2.cdm.agent.proxy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Getting new access token and refresh token after access token expiration
 */
public class RefreshTokenHandler {
    private static final String TAG = "RefreshTokenHandler";
    private static Token token;

    public RefreshTokenHandler(Token token) {
        RefreshTokenHandler.token = token;
    }

    public void obtainNewAccessToken() throws InterruptedException, ExecutionException, TimeoutException {
        new NetworkCallTask().execute();
    }

    private class NetworkCallTask extends AsyncTask<String, Void, String> {

        private String responseCode = null;

        public NetworkCallTask() {

        }

		@Override
		protected String doInBackground(String... params) {
			String response = "";
			
			Map<String, String> request_params = new HashMap<String, String>();
			request_params.put("grant_type", "refresh_token");
			request_params.put("refresh_token", token.getRefreshToken());
			request_params.put("scope", "PRODUCTION");
			APIUtilities apiUtilities = new APIUtilities();
            apiUtilities.setEndPoint(IdentityProxy.getInstance()
                                     .getAccessTokenURL());
            apiUtilities.setHttpMethod("POST");
            apiUtilities.setRequestParams(request_params);
			
			Map<String, String> headers = new HashMap<String, String>();
			Log.e("proxy",IdentityProxy.clientID + ":" + IdentityProxy.clientSecret);
            String authorizationString = "Basic " + new String(Base64.encodeBase64((IdentityProxy.clientID + ":" + IdentityProxy.clientSecret).getBytes()));
            headers.put("Authorization", authorizationString);
            headers.put("Content-Type", "application/x-www-form-urlencoded");
            
            Map<String, String> response_params = ServerUtilitiesTemp.postData(apiUtilities,headers);
			
			response = response_params.get("response");
			responseCode = response_params.get("status");
			Log.d(TAG, response);
			return response;
		}

        @SuppressLint("SimpleDateFormat")
        @Override
        protected void onPostExecute(String result) {
        	
            String refreshToken = null;
            String accessToken = null;
            int timeToExpireSecond=3000;
            IdentityProxy identityProxy = IdentityProxy.getInstance();
            try {
                JSONObject response = new JSONObject(result);
                Log.e("refresh Token Post",result.toString());
                
                if (responseCode != null && responseCode.equals("200")) {
                    refreshToken = response.getString("refresh_token");
                    accessToken = response.getString("access_token");
                    timeToExpireSecond = Integer.parseInt(response.getString("expires_in"));
                    
                    token.setRefreshToken(refreshToken);
                    token.setAccessToken(accessToken);
                     
                    SharedPreferences mainPref = IdentityProxy.getInstance().getContext().getSharedPreferences("com.mdm",Context.MODE_PRIVATE);
                    Editor editor = mainPref.edit();
                    editor.putString("refresh_token",refreshToken);
                    editor.putString("access_token",accessToken);
                    
                    
                    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");                    
                    Date date = new Date();
                    long expiresIN=date.getTime()+(timeToExpireSecond*1000);
                    Date expireDate=new Date(expiresIN);                                      
                    String strDate = dateFormat.format(expireDate);
                    token.setDate(strDate);                
                    editor.putString("date",strDate);               
                    editor.commit();
                    
                    identityProxy.receiveNewAccessToken(responseCode, "success", token);
                    
                } else if (responseCode != null) {
                    JSONObject mainObject = new JSONObject(result);
                    String error = mainObject.getString("error");
                    String errorDescription = mainObject.getString("error_description");
                    Log.d(TAG, error);
                    Log.d(TAG, errorDescription);
                    identityProxy.receiveNewAccessToken(responseCode, errorDescription, token);
                }
            } catch (JSONException e) {
            	identityProxy.receiveNewAccessToken(responseCode, "", token);
                e.printStackTrace();
            }
        }
    }
}
