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
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.beans.Notification;
import org.wso2.emm.agent.dao.NotificationDAO;
import org.wso2.emm.agent.services.VPNService;
import org.wso2.emm.agent.utils.Constants;

/**
 * Activity which is used to show alerts throughout the application.
 */
public class AlertActivity extends SherlockActivity {
	private String message;
	private String payload;
	private Button btnOK;
	private TextView txtMessage;
	private Uri defaultRingtoneUri;
	private Ringtone defaultRingtone;
	private DeviceInfo deviceInfo;
	private String type;
	private Context context;
	private Resources resources;
	private AudioManager audio;
	private int operationId;
	private String serverAddress;
	private String serverPort;
	private String sharedSecret;
	private String dnsServer;
	private static final int DEFAULT_VOLUME = 0;
	private static final int DEFAULT_FLAG = 0;
	private static final int VPN_REQUEST_CODE = 0;
	private static final String DEVICE_OPERATION_RING = "ring";
	private static final String OPEN_LOCK_SETTINGS = "lock_settings";
	private static final String TAG = AlertActivity.class.getSimpleName();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);

		btnOK = (Button) findViewById(R.id.btnOK);
		txtMessage = (TextView) findViewById(R.id.txtMessage);
		deviceInfo = new DeviceInfo(this);
		context = AlertActivity.this.getApplicationContext();
		this.resources = context.getResources();
		audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);


		Bundle extras = getIntent().getExtras();
		if (extras != null) {

			if (extras.containsKey(getResources().getString(R.string.intent_extra_message))) {
				message = extras.getString(getResources().getString(R.string.intent_extra_message));
			}

			type = extras.getString(getResources().getString(R.string.intent_extra_type));

			if (DEVICE_OPERATION_RING.equalsIgnoreCase(type)) {
				startRing();
			}

			if (Constants.Operation.VPN.equalsIgnoreCase(type)) {
				payload = extras.getString(getResources().getString(R.string.intent_extra_payload));
			}
		}
		if (extras.containsKey(getResources().getString(R.string.intent_extra_operation_id))) {
			operationId = extras.getInt(getResources().getString(R.string.intent_extra_operation_id));
		}

		txtMessage.setText(message);

		btnOK.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (DEVICE_OPERATION_RING.equalsIgnoreCase(type)) {
					stopRing();
					AlertActivity.this.finish();
				} else if (OPEN_LOCK_SETTINGS.equalsIgnoreCase(type)) {
					openPasswordSettings();
					AlertActivity.this.finish();
				} else if (Constants.Operation.VPN.equalsIgnoreCase(type)) {
					startVpn();
				} else {
					updateNotification(operationId);
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
		audio.setStreamVolume(AudioManager.STREAM_RING, DEFAULT_VOLUME, DEFAULT_FLAG);
		audio.setRingerMode(AudioManager.RINGER_MODE_SILENT);
	}

	/**
	 * This method starts a VPN connection.
	 */
	private void startVpn() {

		try {
			JSONObject vpnData = new JSONObject(payload);
			if (!vpnData.isNull(resources.getString(R.string.intent_extra_server))) {
				serverAddress = (String) vpnData.get(resources.getString(R.string.intent_extra_server));
			}
			if (!vpnData.isNull(resources.getString(R.string.intent_extra_server_port))) {
				serverPort = (String) vpnData.get(resources.getString(R.string.intent_extra_server_port));
			}

			if (!vpnData.isNull(resources.getString(R.string.intent_extra_shared_secret))) {
				sharedSecret = (String) vpnData.get(resources.getString(R.string.intent_extra_shared_secret));
			}

			if (!vpnData.isNull(resources.getString(R.string.intent_extra_dns))) {
				dnsServer = (String) vpnData.get(resources.getString(R.string.intent_extra_dns));
			}
		} catch (JSONException e) {
			Log.e(TAG, "Invalid VPN payload " + e);
		}

		Intent intent = VpnService.prepare(this);
		if (intent != null) {
			startActivityForResult(intent, VPN_REQUEST_CODE);
		} else {
			onActivityResult(VPN_REQUEST_CODE, RESULT_OK, null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String prefix = getPackageName();
			Intent intent = new Intent(this, VPNService.class);
			intent.putExtra(prefix + getResources().getString(R.string.address), serverAddress);

			if(serverPort != null) {
				intent.putExtra(prefix + getResources().getString(R.string.port), serverPort);
			}

			if(sharedSecret != null) {
				intent.putExtra(prefix + getResources().getString(R.string.secret), sharedSecret);
			}

			if(dnsServer != null) {
				intent.putExtra(prefix + getResources().getString(R.string.dns), dnsServer);
			}

			startService(intent);
		}

		AlertActivity.this.finish();
	}

	/**
	 * This method is used to start ringing the phone.
	 */
	@TargetApi(21)
	private void startRing() {
		if (audio != null) {
			audio.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			audio.setStreamVolume(AudioManager.STREAM_RING, audio.getStreamMaxVolume(AudioManager.STREAM_RING),
			                      AudioManager.FLAG_PLAY_SOUND);

			defaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE);

			if (defaultRingtoneUri != null) {
				defaultRingtone = RingtoneManager.getRingtone(this, defaultRingtoneUri);

				if (defaultRingtone != null) {
					if (deviceInfo.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP) {
						AudioAttributes attributes = new AudioAttributes.Builder().
								setUsage(AudioAttributes.USAGE_NOTIFICATION).
								setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).
								build();
						defaultRingtone.setAudioAttributes(attributes);
					} else {
						defaultRingtone.setStreamType(AudioManager.STREAM_NOTIFICATION);
					}
					defaultRingtone.play();
				}
			}
		}
	}

	/**
	 * This method is used to open screen lock password settings screen.
	 */
	private void openPasswordSettings() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
		startActivity(intent);
	}

	/**
	 * This is used to disable the action of back button press.
	 */
	@Override
	public void onBackPressed() {
		//do nothing
	}

	private void updateNotification (int id) {
		NotificationDAO notificationDAO = new NotificationDAO(context);
		notificationDAO.open();
		notificationDAO.updateNotification(id, Notification.Status.DISMISSED);
		notificationDAO.close();
	}

}
