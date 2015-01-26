package org.wso2.cdm.agent.proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.CommonUtilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class APIController implements TokenCallBack {
	private static String TAG = "APIController";
	private static Token token;
	private APIResultCallBack apiResultCall;
	private APIUtilities apiUtilitiesCurrent;
	private static String clientKey, clientSecret;

	public void setClientDetails(String clientKey, String clientSecret) {
		APIController.clientKey = clientKey;
		APIController.clientSecret = clientSecret;
	}

	public void invokeAPI(APIUtilities apiUtilities, APIResultCallBack apiResultCallBack,
	                      int requestCode, Context context) {
		apiResultCall = apiResultCallBack;
		apiUtilitiesCurrent = apiUtilities;
		if (IdentityProxy.getInstance().getContext() == null) {
			IdentityProxy.getInstance().setContext(context);
		}
		IdentityProxy.getInstance().setRequestCode(requestCode);

		try {
			IdentityProxy.getInstance().getToken(IdentityProxy.getInstance().getContext(), this,
			                                     APIController.clientKey,
			                                     APIController.clientSecret);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private class NetworkCallTask extends AsyncTask<APIUtilities, Void, Map<String, String>> {
		APIResultCallBack apiResultCallBack;

		public NetworkCallTask(APIResultCallBack apiResultCallBack) {
			this.apiResultCallBack = apiResultCallBack;
		}

		@Override
		protected Map<String, String> doInBackground(APIUtilities... params) {
			APIUtilities apiUtilities = params[0];

			Map<String, String> response_params = null;
			try {
				String accessToken = token.getAccessToken();
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
				headers.put("Accept", "*/*");
				headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");
				headers.put("Authorization", "Bearer " + accessToken);
				response_params = ServerUtilitiesTemp.postData(apiUtilities, headers);
				return response_params;
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			apiResultCallBack.onReceiveAPIResult(result, IdentityProxy.getInstance()
			                                                          .getRequestCode());
		}
	}

	@Override
	public void onReceiveTokenResult(Token token, String status) {
		APIController.token = token;
		new NetworkCallTask(apiResultCall).execute(apiUtilitiesCurrent);

	}
}
