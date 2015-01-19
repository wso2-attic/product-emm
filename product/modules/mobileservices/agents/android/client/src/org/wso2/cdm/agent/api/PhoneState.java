/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.api;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.Log;

public class PhoneState {
	Context context = null;
	private long mStartRX = 0;
	private long mStartTX = 0;
	double mbDivider = 1048576;
	//Data Usage API Init
	TrafficSnapshot latest=null;
	TrafficSnapshot previous=null;
	ApplicationManager appList;
	
	public PhoneState(Context context){
		this.context = context;
		appList = new ApplicationManager(this.context);
	}
	/**
	*Returns the device IP address
	*/
	public String getIpAddress(){
		WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
	}
	/**
	*Format the integer IP address and return it as a String
	*@param i - IP address should be passed in as an Integer
	*/
	public String intToIp(int i) {
       /* return ((i >> 24 ) & 0xFF ) + "." +
                    ((i >> 16 ) & 0xFF) + "." +
                    ((i >> 8 ) & 0xFF) + "." +
                    ( i & 0xFF) ;*/
        return (i & 0xFF) + "." +
        ((i >> 8 ) & 0xFF) + "." +
        ((i >> 16 ) & 0xFF) + "." +
        ((i >> 24 ) & 0xFF );
    }
	/**
	*Returns the available amount of memory in the device
	*/
	public String getAvailableMemory(){
		ActivityManager activityManager = (ActivityManager)context.getSystemService(context.ACTIVITY_SERVICE);
        MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        Log.v("Available Memory", memoryInfo.availMem+"");
        String availMemory = memoryInfo.availMem+"";
        return availMemory; 
	}
	/**
	*Returns the amount of uploaded data in bytes
	*//*
	public long getDataUploadUsage(){
		mStartTX = TrafficStats.getTotalTxBytes();
		return mStartTX;
	}
	*//**
	*Returns the amount of downloaded data in bytes
	*//*
	public long getDataDownloadUsage(){
		mStartRX = TrafficStats.getTotalRxBytes();
		return mStartRX;
	}*/
	/**
	*Returns the device battery information
	*/
	public float getBatteryLevel(){
		Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }

	    return ((float)level / (float)scale) * 100.0f;
		/*BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
            int scale = -1;
            int level = -1;
            int voltage = -1;
            int temp = -1;
            @Override
            public void onReceive(Context context, Intent intent) {
                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
                voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
                Log.v("Battery Level", "level is "+level+"/"+scale+", temp is "+temp+", voltage is "+voltage);
                
                Battery battery = Battery.getInstance();
                battery.setLevel(level);
                battery.setScale(scale);
                battery.setTemp(temp);
                battery.setVoltage(voltage);
                
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryReceiver, filter);
		return Battery.getInstance();*/
	}
	
	/**
	 * Generate Data Usage Report
	 */
	public JSONObject takeDataUsageSnapShot() {
		previous=latest;
		latest=new TrafficSnapshot(context);
		JSONObject dataObj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		String latestRX, latestTX,  previousRX, previousTX, deltaRX, deltaTX; 
		latestRX = String.valueOf(formatSizeMB(latest.device.rx));
		latestTX = String.valueOf(formatSizeMB(latest.device.tx));
		try {
			dataObj.put("totalupload", latestRX);
			dataObj.put("totaldownload", latestTX);
		
		if (previous!=null) {
			previousRX = String.valueOf(previous.device.rx);
			previousTX = String.valueOf(previous.device.tx);
			
			deltaRX = String.valueOf(latest.device.rx-previous.device.rx);
			deltaTX = String.valueOf(latest.device.tx-previous.device.tx);
		}
		
		ArrayList<String> log=new ArrayList<String>();
		HashSet<Integer> intersection=new HashSet<Integer>(latest.apps.keySet());
		
		if (previous!=null) {
			intersection.retainAll(previous.apps.keySet());
		}
		
		for (Integer uid : intersection) {
			TrafficRecord latest_rec=latest.apps.get(uid);
			TrafficRecord previous_rec=
						(previous==null ? null : previous.apps.get(uid));
			
			jsonArray.add(getDataUseReport(latest_rec.tag, latest_rec, previous_rec, log));
		}
		
		dataObj.put("appdata", jsonArray);
		
		Collections.sort(log);
		
		for (String row : log) {
			Log.d("TrafficMonitor", row);
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataObj;
	}
	
	private JSONObject getDataUseReport(CharSequence name, TrafficRecord latest_rec,
												TrafficRecord previous_rec,
												ArrayList<String> rows) {
		JSONObject jsonObj = new JSONObject();
		if (latest_rec.rx>-1 || latest_rec.tx>-1) {
			StringBuilder buf=new StringBuilder(name);
			try {
				jsonObj.put("package", name);
				jsonObj.put("name", appList.getAppNameFromPackage(name.toString()));
				jsonObj.put("upload", formatSizeMB(latest_rec.tx));
				jsonObj.put("download", formatSizeMB(latest_rec.rx));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			buf.append("=");
			buf.append(String.valueOf(latest_rec.rx));
			buf.append(" received");
			
			if (previous_rec!=null) {
				buf.append(" (delta=");
				buf.append(String.valueOf(latest_rec.rx-previous_rec.rx));
				buf.append(")");
			}
			
			buf.append(", ");
			buf.append(String.valueOf(latest_rec.tx));
			buf.append(" sent");
			
			if (previous_rec!=null) {
				buf.append(" (delta=");
				buf.append(String.valueOf(latest_rec.tx-previous_rec.tx));
				buf.append(")");
			}
			
			rows.add(buf.toString());
		}
		return jsonObj;
	}
	
	 public double formatSizeMB(double total){
	    	double amount = (total/mbDivider);
	        BigDecimal bd = new BigDecimal(amount).setScale(2, RoundingMode.HALF_EVEN);
	        amount = bd.doubleValue();
	        return amount;
	 }
	 
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager mConnMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = mConnMan.getActiveNetworkInfo();
		if (info == null) {
			return false;
		}
		return info.isConnected();
	}
}
