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

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.LoggerCustom;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class LogActivity extends Activity {
	TextView txtLog;
	Button btnRefresh, btnReset;
	LoggerCustom logger = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_log);
		txtLog = (TextView)findViewById(R.id.txtLog);
		btnRefresh = (Button)findViewById(R.id.btnRefresh);
		btnReset = (Button)findViewById(R.id.btnReset);
		logger = new LoggerCustom(this);
		String log_in = logger.readFileAsString("wso2log.txt");
		
		txtLog.setText(Html.fromHtml(log_in));
		
		btnRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String log_in = logger.readFileAsString("wso2log.txt");
				
				txtLog.setText(Html.fromHtml(log_in));
			}
		});
		
		btnReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				logger.writeStringAsFile("", "wso2log.txt");
				
				txtLog.setText("");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.log, menu);
		return true;
	}

}
