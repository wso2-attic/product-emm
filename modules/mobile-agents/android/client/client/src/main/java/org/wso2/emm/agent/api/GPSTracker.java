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
package org.wso2.emm.agent.api;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.proxy.IDPTokenManagerException;
import org.wso2.emm.agent.proxy.beans.EndPointInfo;
import org.wso2.emm.agent.proxy.utils.ServerUtilities;
import org.wso2.emm.agent.utils.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * This class handles all the functionalities related to retrieving device 
 * current location.
 */
public class GPSTracker extends Service implements LocationListener {

	private Location location;
	private double latitude;
	private double longitude;

	private String street1;
	private String street2;

	private String city;
	private String state;
	private String zip;
	private String country;

	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	protected LocationManager locationManager;

	private static final String TAG = GPSTracker.class.getName();

	public GPSTracker(Context context) {
		locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
		getLocation();
	}

	/**
	 * Function to get device location using GPS.
	 * @return - Device location coordinates.
	 */
	private void getLocation() {
		if (locationManager != null) {
			try {
				boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
					                                       MIN_TIME_BW_UPDATES,
					                                       MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(
								LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}

				if (isGpsEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
						                                       MIN_TIME_BW_UPDATES,
						                                       MIN_DISTANCE_CHANGE_FOR_UPDATES,
						                                       this);
						if (locationManager != null) {
							location = locationManager.getLastKnownLocation(
									LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
				setReversGeoCoordinates();
			} catch (RuntimeException e) {
				Log.e(TAG, "No network/GPS Switched off.", e);
			} catch (InterruptedException e) {
				Log.e(TAG, "Error occured while calling reverse geo coordination API.", e);
			} catch (ExecutionException e) {
				Log.e(TAG, "Error occured while calling reverse geo coordination API.", e);
			}
		}
	}

	/**
	 * Stop using GPS listener.
	 * Calling this function will stop using GPS the agent.
	 */
	public void stopUsingGps() {
		if (locationManager != null) {
			locationManager.removeUpdates(GPSTracker.this);
		}
	}

	/**
	 * Function to get latitude.
	 * @return - Device current latitude.
	 */
	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	/**
	 * Function to get longitude.
	 * @return - Device current longitude.
	 */
	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public String getStreet1() {
		return street1;
	}

	public String getStreet2() {
		return street2;
	}

	public String getCity() {
		return city;
	}

	public String getState() {
		return state;
	}

	public String getZip() {
		return zip;
	}

	public String getCountry() {
		return country;
	}

	/**
	 * In this method, it calls reverse geo coordination API and set relevant values.
	 */
	private void setReversGeoCoordinates() throws ExecutionException, InterruptedException {

		StringBuilder endPoint = new StringBuilder();
		endPoint.append(Constants.Location.GEO_ENDPOINT);
		endPoint.append("?" + Constants.Location.RESULT_FORMAT);
		endPoint.append("&" + Constants.Location.ACCEPT_LANGUAGE + "=" + Constants.Location.LANGUAGE_CODE);
		endPoint.append("&" + Constants.Location.LATITUDE + "=" + latitude);
		endPoint.append("&" + Constants.Location.LONGITUDE + "=" + longitude);

		EndPointInfo endPointInfo = new EndPointInfo();
		endPointInfo.setHttpMethod(org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS.GET);
		endPointInfo.setEndPoint(endPoint.toString());

		SendRequest sendRequestTask = new SendRequest();
		sendRequestTask.execute(endPointInfo).get();
	}



	/**
	 * This class is used to send requests to reverse geo coordination API.
	 * The reason to use this private class because the function which is already
	 * available for sending requests is secured with token. Therefor this async task can be used
	 * to send requests without tokens.
	 */
	private class SendRequest extends AsyncTask<EndPointInfo, Void, Map<String, String>> {
		@Override
		protected Map<String, String> doInBackground(EndPointInfo... params) {
			EndPointInfo endPointInfo = params[0];

			Map<String, String> responseParams = null;
			Map<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", Constants.USER_AGENT);

			try {
				responseParams = ServerUtilities.postData(endPointInfo, headers);
				if (Constants.DEBUG_MODE_ENABLED) {
					Log.d(TAG, "Response Code: " +
					           responseParams.get(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_STATUS));
					Log.d(TAG, "Response Payload: " +
					           responseParams.get(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_BODY));
				}
			} catch (IDPTokenManagerException e) {
				Log.e(TAG, "Failed to contact server", e);
			}
			return responseParams;
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {

			if (result != null) {
				String responseCode = result.get(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_STATUS);
				if (Constants.Status.SUCCESSFUL.equals(responseCode)) {
					String resultPayload = result.get(org.wso2.emm.agent.proxy.utils.Constants.SERVER_RESPONSE_BODY);
					try {
						JSONObject data = new JSONObject(resultPayload);
						if (!data.isNull(Constants.Location.ADDRESS)) {
							JSONObject address = data.getJSONObject(Constants.Location.ADDRESS);
							if (!address.isNull(Constants.Location.CITY)) {
								city = address.getString(Constants.Location.CITY);
							} else if (!address.isNull(Constants.Location.TOWN)) {
								city = address.getString(Constants.Location.TOWN);
							}

							if (!address.isNull(Constants.Location.COUNTRY)) {
								country = address.getString(Constants.Location.COUNTRY);
							}
							if (!address.isNull(Constants.Location.STREET1)) {
								street1 = address.getString(Constants.Location.STREET1);
							}
							if (!address.isNull(Constants.Location.STREET2)) {
								street2 = address.getString(Constants.Location.STREET2);
							}
							if (!address.isNull(Constants.Location.STATE)) {
								state = address.getString(Constants.Location.STATE);
							}
							if (!address.isNull(Constants.Location.ZIP)) {
								zip = address.getString(Constants.Location.ZIP);
							}
						}

						if (Constants.DEBUG_MODE_ENABLED) {
							Log.d(TAG, "Address: " + street1 + ", " + street2 + ", " + city + ", " + state + ", " + zip +
							           ", " + country);
						}
					} catch (JSONException e) {
						Log.e(TAG, "Error occurred while parsing the result payload", e);
					}
				}
			}
		}
	}

}