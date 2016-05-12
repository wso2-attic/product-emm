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

package org.wso2.emm.agent.services.location.impl;

import android.location.Location;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.beans.Address;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;
import org.wso2.emm.agent.services.location.ReverseGeoCodingService;
import org.wso2.emm.agent.utils.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * This class holds the function implementation of the reverse geo coding service.
 */
public class OpenStreetMapService implements ReverseGeoCodingService {

    private Address currentAddress;
    private static final String TAG = OpenStreetMapService.class.getSimpleName();

    private static OpenStreetMapService instance;

    private OpenStreetMapService() {}

    public static OpenStreetMapService getInstance() {
        if (instance == null) {
            synchronized (OpenStreetMapService.class) {
                if (instance == null) {
                    instance = new OpenStreetMapService();
                }
            }
        }
        return instance;
    }

    @Override
    public Address getReverseGeoCodes(Location location) {
        if (location == null) {
            return null;
        }
        String url = new StringBuilder()
                .append(Constants.Location.GEO_ENDPOINT)
                .append("?" + Constants.Location.RESULT_FORMAT)
                .append("&" + Constants.Location.ACCEPT_LANGUAGE + "=" + Constants.Location.LANGUAGE_CODE)
                .append("&" + Constants.Location.LATITUDE + "=" + location.getLatitude())
                .append("&" + Constants.Location.LONGITUDE + "=" + location.getLongitude())
                .toString();

        EndPointInfo endPointInfo = new EndPointInfo();
        endPointInfo.setHttpMethod(org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.GET);
        endPointInfo.setEndPoint(url);

        sendRequest(endPointInfo);
        return currentAddress;
    }

    /**
     * This method is used to send requests to reverse geo coordination API.
     * The reason to use this method because the function which is already
     * available for sending requests is secured with token. Therefore this method can be used
     * to send requests without tokens.
     */
    private void sendRequest(EndPointInfo endPointInfo) {
        RequestQueue queue =  null;
        try {
            queue = ServerUtilities.getCertifiedHttpClient();
        } catch (IDPTokenManagerException e) {
            Log.e(TAG, "Failed to retrieve HTTP client", e);
        }

        StringRequest request = new StringRequest(Request.Method.GET, endPointInfo.getEndPoint(),
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
                if(org.wso2.emm.agent.proxy.utils.Constants.DEBUG_ENABLED) {
                    if(result != null && !result.isEmpty()) {
                        Log.d(TAG, "Result :" + result);
                    }
                }
                Map<String, String> responseParams = new HashMap<>();
                responseParams.put(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_BODY, result);
                responseParams.put(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_STATUS, String.valueOf(response.statusCode));
                processTokenResponse(responseParams);
                return super.parseNetworkResponse(response);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", Constants.USER_AGENT);
                return headers;
            }
        };

        queue.add(request);
    }


    private void processTokenResponse(Map<String, String> result) {
        if (result != null) {
            String responseCode = result.get(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_STATUS);
            if (Constants.Status.SUCCESSFUL.equals(responseCode)) {
                String resultPayload = result.get(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_BODY);
                try {
                    JSONObject data = new JSONObject(resultPayload);
                    if (!data.isNull(Constants.Location.ADDRESS)) {
                        currentAddress = new Address();
                        JSONObject address = data.getJSONObject(Constants.Location.ADDRESS);
                        if (!address.isNull(Constants.Location.CITY)) {
                            currentAddress.setCity(address.getString(Constants.Location.CITY));
                        } else if (!address.isNull(Constants.Location.TOWN)) {
                            currentAddress.setCity(address.getString(Constants.Location.TOWN));
                        }

                        if (!address.isNull(Constants.Location.COUNTRY)) {
                            currentAddress.setCountry(address.getString(Constants.Location.COUNTRY));
                        }
                        if (!address.isNull(Constants.Location.STREET1)) {
                            currentAddress.setStreet1(address.getString(Constants.Location.STREET1));
                        }
                        if (!address.isNull(Constants.Location.STREET2)) {
                            currentAddress.setStreet2(address.getString(Constants.Location.STREET2));
                        }
                        if (!address.isNull(Constants.Location.STATE)) {
                            currentAddress.setState(address.getString(Constants.Location.STATE));
                        }
                        if (!address.isNull(Constants.Location.ZIP)) {
                            currentAddress.setZip(address.getString(Constants.Location.ZIP));
                        }
                    }

                    if (Constants.DEBUG_MODE_ENABLED) {
                        String addr = new StringBuilder().append("Address: ")
                                .append(currentAddress.getStreet1() + ", ")
                                .append(currentAddress.getStreet2() + ", ")
                                .append(currentAddress.getCity() + ", ")
                                .append(currentAddress.getState() + ", ")
                                .append(currentAddress.getZip() + ", ")
                                .append(currentAddress.getCountry())
                                .toString();
                        Log.d(TAG, addr);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Error occurred while parsing the result payload", e);
                }
            }
        }
    }

}
