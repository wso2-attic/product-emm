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
package org.wso2.emm.agent;

import android.annotation.TargetApi;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;

import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.utils.Constants;

/**
 * Activity which is used to show alerts throughout the application.
 */
public class AlertActivity extends SherlockActivity {
	private String message;
	private Button btnOK;
	private TextView txtMessage;
	private Uri defaultRingtoneUri;
	private Ringtone defaultRingtone;
	private DeviceInfo deviceInfo;
	private String type;

	private static final String RING = "ring";
	private static final String TAG = AlertActivity.class.getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);

		btnOK = (Button) findViewById(R.id.btnOK);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		deviceInfo = new DeviceInfo(this);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			if (extras.containsKey(getResources().getString(R.string.intent_extra_message))) {
				message = extras.getString(getResources().getString(R.string.intent_extra_message));
			}

			type = extras.getString(getResources().getString(R.string.intent_extra_type));

			if (RING.equalsIgnoreCase(type)) {
				startRing();
			}
		}

		txtMessage.setText(message);

		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (RING.equalsIgnoreCase(type)) {
					stopRing();
					AlertActivity.this.finish();
				} else {
					AlertActivity.this.finish();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		return true;
	}

	/**
	 * This method stops the device ring.
	 */
	private void stopRing() {

		if (defaultRingtone != null && defaultRingtone.isPlaying()) {
			defaultRingtone.stop();
		}
	}

	/**
	 * This method is used to start ringing the phone.
	 */
	@TargetApi(21)
	private void startRing() {
		defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);
		defaultRingtone = RingtoneManager.getRingtone(this, defaultRingtoneUri);

		if (deviceInfo.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP) {
			AudioAttributes attributes = new AudioAttributes.Builder().
					setUsage(AudioAttributes.USAGE_NOTIFICATION).
					setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).
					build();
			defaultRingtone.setAudioAttributes(attributes);
			defaultRingtone.play();
		} else {
			defaultRingtone.setStreamType(AudioManager.STREAM_NOTIFICATION);
			defaultRingtone.play();
		}
	}

	/**
	 * This is used to disable the action of back button press.
	 */
	@Override
	public void onBackPressed() {
		//do nothing
		if (Constants.DEBUG_MODE_ENABLED) {
			Log.i(TAG, "Back button pressed");
		}
	}

}
