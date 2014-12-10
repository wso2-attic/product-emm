/**
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.cdm.agent;

import org.json.JSONArray;
import org.json.JSONException;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.api.DeviceInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayDeviceInfoActivity extends Activity {
	private String FROM_ACTIVITY = null;
	private String REG_ID = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_device_info);
		DeviceInfo deviceInfo = new DeviceInfo(DisplayDeviceInfoActivity.this);
		TextView device_id = (TextView)findViewById(R.id.txtId);
		TextView device = (TextView)findViewById(R.id.txtDevice);
		TextView model = (TextView)findViewById(R.id.txtModel);
		TextView operator = (TextView)findViewById(R.id.txtOperator);
		TextView sdk = (TextView)findViewById(R.id.txtSDK);
		TextView os = (TextView)findViewById(R.id.txtOS);
		TextView root = (TextView)findViewById(R.id.txtRoot);
		
		device_id.setText(getResources().getString(R.string.info_label_imei)+" "+deviceInfo.getDeviceId());
		device.setText(getResources().getString(R.string.info_label_device)+" "+deviceInfo.getDevice());
		model.setText(getResources().getString(R.string.info_label_model)+" "+deviceInfo.getDeviceModel());
		JSONArray jsonArray = null;
		String operators = "";
		if(deviceInfo.getNetworkOperatorName()!= null){
			jsonArray = deviceInfo.getNetworkOperatorName();
		}
		
		for (int i = 0; i < jsonArray.length(); i++) {
   	    	 try {
				if(jsonArray.getString(i) != null){
					 if(i==(jsonArray.length()-1)){
						 operators += jsonArray.getString(i);
					 }else{
						 operators += jsonArray.getString(i)+", ";
					 }
				 }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		
		if(operators.equals(null)){
			operators = getResources().getString(R.string.info_label_no_sim);
		}
		operator.setText(getResources().getString(R.string.info_label_operator)+" "+operators);
		if(deviceInfo.getIMSINumber() != null){
			sdk.setText(getResources().getString(R.string.info_label_imsi)+" "+deviceInfo.getIMSINumber());
		}else{
			sdk.setText(getResources().getString(R.string.info_label_imsi)+" "+operators);
		}
		os.setText(getResources().getString(R.string.info_label_os)+" "+deviceInfo.getOsVersion());
		root.setText(getResources().getString(R.string.info_label_rooted)+" "+(deviceInfo.isRooted()?getResources().getString(R.string.yes):getResources().getString(R.string.no)));
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if(extras.containsKey(getResources().getString(R.string.intent_extra_from_activity))){
				FROM_ACTIVITY = extras.getString(getResources().getString(R.string.intent_extra_from_activity));
			}
			
			if(extras.containsKey(getResources().getString(R.string.intent_extra_regid))){
				REG_ID = extras.getString(getResources().getString(R.string.intent_extra_regid));
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//MenuItem menu1 = menu.add(0,1,1,"Settings");
		//getMenuInflater().inflate(R.menu.display_device_info, menu);
		return true;
	}
	

	public boolean onOptionsItemSelected(MenuItem menu){
		/*switch(menu.getItemId()){
		case 1:
			Intent intent = new Intent(DisplayDeviceInfo.this,SettingsActivity.class);
			startActivity(intent);	
			return true;
		default:
			return this.onOptionsItemSelected(menu);
		}*/
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && FROM_ACTIVITY != null && FROM_ACTIVITY.equals(AlreadyRegisteredActivity.class.getSimpleName())) {
    		Intent intent = new Intent(DisplayDeviceInfoActivity.this,AlreadyRegisteredActivity.class);
    		intent.putExtra(getResources().getString(R.string.intent_extra_from_activity), DisplayDeviceInfoActivity.class.getSimpleName());
    		intent.putExtra(getResources().getString(R.string.intent_extra_regid), REG_ID);
    		startActivity(intent);
    		return true;
	    }else if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	Intent i = new Intent();
	    	i.setAction(Intent.ACTION_MAIN);
	    	i.addCategory(Intent.CATEGORY_HOME);
	    	this.startActivity(i);
	    	this.finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**/

}
