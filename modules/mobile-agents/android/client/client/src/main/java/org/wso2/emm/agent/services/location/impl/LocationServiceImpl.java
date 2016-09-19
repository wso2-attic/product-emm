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

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.gson.Gson;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.services.location.LocationService;
import org.wso2.emm.agent.utils.Preference;

/**
 * This class holds the function implementations of the location service.
 */
public class LocationServiceImpl extends Service implements LocationListener, LocationService {

    private Location location;
    private LocationManager locationManager;
    private static LocationServiceImpl serviceInstance;
    private Context context;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;
    private static final String TAG = LocationServiceImpl.class.getSimpleName();

    private LocationServiceImpl() {}

    private LocationServiceImpl(Context context) {
        class LooperThread extends Thread {
            public Handler mHandler;

            public void run() {
                if (Looper.myLooper() == null) {
                    Looper.prepare();
                }
                LocationServiceImpl.this.setLocation();
                mHandler = new Handler() {
                    public void handleMessage(Message msg) {
                        Log.e(TAG, "No network/GPS Switched off." + msg);
                    }
                };
            }
        }
        new LooperThread().run();
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
    }

    public static LocationServiceImpl getInstance(Context context) {
        if (serviceInstance == null) {
            synchronized (LocationServiceImpl.class) {
                if (serviceInstance == null) {
                    serviceInstance = new LocationServiceImpl(context);
                }
            }
        }
        return serviceInstance;
    }

    /**
     * In this method, it gets the latest location updates from gps/ network.
     */
    private void setLocation() {
        if (locationManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

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
                            Preference.putString(context, context.getResources().getString(R.string.shared_pref_location),
                                                 new Gson().toJson(location));
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
                                Preference.putString(context, context.getResources().getString(R.string.shared_pref_location),
                                                     new Gson().toJson(location));
                            }
                        }
                    }
                }
            } catch (RuntimeException e) {
                Log.e(TAG, "No network/GPS Switched off.", e);
            }
        }
    }

    public Location getLastKnownLocation() {
        return location;
    }

    @Override
    public Location getLocation() {
        if (location == null) {
            location = new Gson().fromJson(Preference.getString(context, context.getResources().getString(
                    R.string.shared_pref_location)), Location.class);
        }
        return location;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Preference.putString(context, context.getResources().getString(R.string.shared_pref_location),
                                 new Gson().toJson(location));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
