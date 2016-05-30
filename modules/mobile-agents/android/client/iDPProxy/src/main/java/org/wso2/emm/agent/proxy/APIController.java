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
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.proxy.beans.Token;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.interfaces.TokenCallBack;
import org.wso2.emm.agent.proxy.utils.Constants;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;
import java.util.HashMap;
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
	private int requestMethod;
	private boolean isStringRequest = false;
	
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
	}

	public void invokeAPI(EndPointInfo apiEndPointInfo, APIResultCallBack apiResultCallBack,
						  int requestCode, Context context, boolean isString) {
		this.isStringRequest = isString;
		this.apiResultCallback = apiResultCallBack;
		this.apiEndPointInfo = apiEndPointInfo;

		if (IdentityProxy.getInstance().getContext() == null) {
			IdentityProxy.getInstance().setContext(context);
		}

		IdentityProxy.getInstance().setRequestCode(requestCode);

		IdentityProxy.getInstance().requestToken(IdentityProxy.getInstance().getContext(), this,
				this.clientKey,
				this.clientSecret);
	}


	@Override
	public void onReceiveTokenResult(Token token, String status) {
		this.token = token;
		setRequestMethod(apiEndPointInfo.getHttpMethod());
		if(isStringRequest) {
			sendStringRequest(apiResultCallback, apiEndPointInfo, false);
		} else {
			if (apiEndPointInfo.getRequestParamsMap() != null) {
				sendStringRequest(apiResultCallback, apiEndPointInfo, false);
			} else if (apiEndPointInfo.getRequestParams() != null) {
				if (isJSONObject(apiEndPointInfo.getRequestParams())) {
					sendJsonObjectRequest(apiResultCallback, apiEndPointInfo, false);
				} else {
					sendJsonArrayRequest(apiResultCallback, apiEndPointInfo, false);
				}
			} else if (apiEndPointInfo.isJSONArrayRequest()) {
				sendJsonArrayRequest(apiResultCallback, apiEndPointInfo, false);
			} else {
				sendJsonObjectRequest(apiResultCallback, apiEndPointInfo, false);
			}
		}
	}

	private boolean isJSONObject (String data) {
		Object json;
		try {
			json = new JSONTokener(data).nextValue();
			return (json instanceof JSONObject);
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse response JSON", e);
			return false;
		}
	}

	private void setRequestMethod(HTTP_METHODS httpMethod) {
		switch (httpMethod) {
			case GET:
				requestMethod = Request.Method.GET;
				break;
			case POST:
				requestMethod = Request.Method.POST;
				break;
			case DELETE:
				requestMethod = Request.Method.DELETE;
				break;
			case PUT:
				requestMethod = Request.Method.PUT;
				break;
		}
	}

	/**
	 * Secured network call to contact server and access the API with retrieved token.
	 */
	public void securedNetworkCall(final APIResultCallBack callBack, int requestCode,
	                               final EndPointInfo apiUtilities, Context context) {
		if (IdentityProxy.getInstance().getContext() == null) {
			IdentityProxy.getInstance().setContext(context);
		}
		setRequestMethod(apiUtilities.getHttpMethod());
		IdentityProxy.getInstance().setRequestCode(requestCode);
		if (apiUtilities.getRequestParamsMap() != null) {
			sendStringRequest(callBack, apiUtilities, true);
		} else if (apiUtilities.getRequestParams() != null) {
			if (isJSONObject(apiUtilities.getRequestParams())) {
				sendJsonObjectRequest(callBack, apiUtilities, true);
			} else {
				sendJsonArrayRequest(callBack, apiUtilities, true);
			}
		} else if (apiEndPointInfo.isJSONArrayRequest()) {
			sendJsonArrayRequest(apiResultCallback, apiEndPointInfo, true);
		} else {
			sendJsonObjectRequest(callBack, apiUtilities, true);
		}
	}

	public void sendStringRequest(final APIResultCallBack callBack, final EndPointInfo apiUtilities,
	                               final boolean isSecured) {
		RequestQueue queue =  null;
		try {
			queue = ServerUtilities.getCertifiedHttpClient();
		} catch (IDPTokenManagerException e) {
			Log.e(TAG, "Failed to retrieve HTTP client", e);
		}

		StringRequest request = new StringRequest(requestMethod, apiUtilities.getEndPoint(),
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
			                                          }
		                                          })

		{
			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				String result = new String(response.data);
				if(Constants.DEBUG_ENABLED) {
					if(result != null && !result.isEmpty()) {
						Log.d(TAG, "Result :" + result);
					}
				}
				Map<String, String> responseParams = new HashMap<>();
				responseParams.put(Constants.SERVER_RESPONSE_BODY, result);
				responseParams.put(Constants.SERVER_RESPONSE_STATUS, String.valueOf(response.statusCode));
				callBack.onReceiveAPIResult(responseParams, IdentityProxy.getInstance().getRequestCode());
				return super.parseNetworkResponse(response);
			}

			@Override
			public byte[] getBody() throws AuthFailureError {
				return apiUtilities.getRequestParams().getBytes();
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return apiUtilities.getRequestParamsMap();
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type", "text/plain");
				headers.put("Accept", "text/plain");
				headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");
				if(!isSecured) {
					String accessToken = token.getAccessToken();
					headers.put("Authorization", "Bearer " + accessToken);
				}
				ServerUtilities.addHeaders(headers);
				return headers;
			}
		};

		queue.add(request);
	}

	private void sendJsonObjectRequest(final APIResultCallBack callBack, final EndPointInfo apiUtilities,
	                                   final boolean isSecured) {
		RequestQueue queue =  null;
		try {
			queue = ServerUtilities.getCertifiedHttpClient();
		} catch (IDPTokenManagerException e) {
			Log.e(TAG, "Failed to retrieve HTTP client", e);
		}

		JsonObjectRequest request = null;
		try {
			request = new JsonObjectRequest(requestMethod, apiUtilities.getEndPoint(),
			                                (apiUtilities.getRequestParams() != null) ?
			                                new JSONObject(apiUtilities.getRequestParams()) : null,
                                                      new Response.Listener<JSONObject>() {
                                                          @Override
                                                          public void onResponse(JSONObject response) {
                                                              Log.d(TAG, response.toString());
                                                          }
                                                      },
                                                      new Response.ErrorListener() {
                                                          @Override
                                                          public void onErrorResponse(VolleyError error) {
                                                              Log.e(TAG, error.toString());
                                                          }
                                                      })

            {
                @Override
                protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                    String result = new String(response.data);
                    if(Constants.DEBUG_ENABLED) {
                        if(result != null && !result.isEmpty()) {
                            Log.d(TAG, "Result :" + result);
                        }
                    }
                    Map<String, String> responseParams = new HashMap<>();
                    responseParams.put(Constants.SERVER_RESPONSE_BODY, result);
                    responseParams.put(Constants.SERVER_RESPONSE_STATUS, String.valueOf(response.statusCode));
                    callBack.onReceiveAPIResult(responseParams, IdentityProxy.getInstance().getRequestCode());
                    return super.parseNetworkResponse(response);
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Accept", "*/*");
                    headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");
                    if(!isSecured) {
                        String accessToken = token.getAccessToken();
                        headers.put("Authorization", "Bearer " + accessToken);
                    }
	                ServerUtilities.addHeaders(headers);
                    return headers;
                }
            };
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse request JSON", e);
		}

		queue.add(request);
	}

	private void sendJsonArrayRequest(final APIResultCallBack callBack, final EndPointInfo apiUtilities,
	                                   final boolean isSecured) {
		RequestQueue queue =  null;
		try {
			queue = ServerUtilities.getCertifiedHttpClient();
		} catch (IDPTokenManagerException e) {
			Log.e(TAG, "Failed to retrieve HTTP client", e);
		}

		JsonArrayRequest request = null;
		try {
			request = new JsonArrayRequest(requestMethod, apiUtilities.getEndPoint(),
			                                (apiUtilities.getRequestParams() != null) ?
			                                new JSONArray(apiUtilities.getRequestParams()) : null,
			                                new Response.Listener<JSONArray>() {
				                                @Override
				                                public void onResponse(JSONArray response) {
					                                Log.d(TAG, response.toString());
				                                }
			                                },
			                                new Response.ErrorListener() {
				                                @Override
				                                public void onErrorResponse(VolleyError error) {
					                                Log.e(TAG, error.toString());
				                                }
			                                })

			{
				@Override
				protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
					String result = new String(response.data);
					if(Constants.DEBUG_ENABLED) {
						if(result != null && !result.isEmpty()) {
							Log.d(TAG, "Result :" + result);
						}
					}
					Map<String, String> responseParams = new HashMap<>();
					responseParams.put(Constants.SERVER_RESPONSE_BODY, result);
					responseParams.put(Constants.SERVER_RESPONSE_STATUS, String.valueOf(response.statusCode));
					callBack.onReceiveAPIResult(responseParams, IdentityProxy.getInstance().getRequestCode());
					return super.parseNetworkResponse(response);
				}

				@Override
				public Map<String, String> getHeaders() throws AuthFailureError {
					Map<String, String> headers = new HashMap<>();
					headers.put("Content-Type", "application/json");
					headers.put("Accept", "*/*");
					headers.put("User-Agent", "Mozilla/5.0 ( compatible ), Android");
					if(!isSecured) {
						String accessToken = token.getAccessToken();
						headers.put("Authorization", "Bearer " + accessToken);
					}
					ServerUtilities.addHeaders(headers);
					return headers;
				}
			};
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse request JSON", e);
		}

		queue.add(request);
	}

}
