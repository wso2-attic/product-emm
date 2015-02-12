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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.wso2.cdm.agent.api.DeviceInfo;
import org.wso2.cdm.agent.services.MessageProcessor;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.Preference;
import org.wso2.cdm.agent.utils.Responce;

/**
 * This the the activity that is used to capture the server's host name.
 */
public class ServerDetails extends Activity {

	TextView evServerIP;
	Button btnStartRegistration;
	Context context;
	DialogInterface.OnClickListener dialogClickListener;
	DeviceInfo info;
	TextView tvSeverAddress;

	String senderID = null;
	ProgressDialog progressDialog;
	String regId;
	AlertDialog.Builder alertDialog;

	boolean alreadyRegisteredActivityFlag = false;
	boolean authenticationActivityFlag = false;
	private String TAG = ServerDetails.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		context = ServerDetails.this;
		info = new DeviceInfo(ServerDetails.this);
		evServerIP = (TextView) findViewById(R.id.evServerIP);
		tvSeverAddress = (TextView) findViewById(R.id.tvSeverAddress);
		btnStartRegistration = (Button) findViewById(R.id.btnStartRegistration);

		// Checking if the device meets minimum requirements
		Responce compatibility = info.isCompatible();
		if (!compatibility.getCode()) {
			btnStartRegistration.setVisibility(View.GONE);
			tvSeverAddress.setVisibility(View.GONE);
			evServerIP.setVisibility(View.GONE);
			alertDialog =
					CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
					                                                      getResources().getString(
							                                                      R.string.error_authorization_failed),
					                                                      getResources().getString(
							                                                      compatibility
									                                                      .getDescriptionResourceID()),
					                                                      getResources().getString(
							                                                      R.string.button_ok),
					                                                      onRootedClickListner);
		} else {
			btnStartRegistration.setVisibility(View.VISIBLE);
			evServerIP.setVisibility(View.VISIBLE);
			String ipSaved =
					Preference.get(context.getApplicationContext(),
					               getResources().getString(R.string.shared_pref_ip));
			regId = Preference.get(context.getApplicationContext(),
			                       getResources().getString(R.string.shared_pref_regId));

			//check if we have the IP saved previously.
			if (ipSaved != null) {
				evServerIP.setText(ipSaved);
				CommonUtilities.setServerURL(ipSaved);
				startAuthenticationActivity();
			} else {
				evServerIP.setText(CommonUtilities.SERVER_IP);
			}
			
			String deviceActive=Preference.get(context, context.getResources().getString(R.string.shared_pref_device_active));
			if(deviceActive!=null && deviceActive.equals("1")){
				Intent intent = new Intent(ServerDetails.this, AlreadyRegisteredActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			// on click handler for start registration
			btnStartRegistration.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ServerDetails.this);
					StringBuilder messageBuilder = new StringBuilder();
					messageBuilder
							.append(getResources().getString(R.string.dialog_init_confirmation));
					messageBuilder.append(" ");
					messageBuilder.append(evServerIP.getText().toString());
					messageBuilder.append(" ");
					messageBuilder
							.append(getResources().getString(R.string.dialog_init_end_general));
					alertBuilder.setMessage(messageBuilder.toString())
					            .setPositiveButton(getResources().getString(R.string.yes),
					                               dialogClickListener)
					            .setNegativeButton(getResources().getString(R.string.no),
					                               dialogClickListener).show();
				}
			});

			dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							if (!evServerIP.getText().toString().trim().equals("")) {
								CommonUtilities
										.setServerURL(evServerIP.getText().toString().trim());
								Preference.put(context.getApplicationContext(),
								               getResources().getString(R.string.shared_pref_ip),
								               evServerIP.getText().toString().trim());
								startAuthenticationActivity();

							} else {
								Toast.makeText(context.getApplicationContext(),
								               getResources().getString(
										               R.string.toast_message_enter_server_address),
								               Toast.LENGTH_LONG).show();
							}
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							dialog.dismiss();
							break;
					}
				}
			};
		}
		Log.d(TAG, "Server details activity started");
	}

	DialogInterface.OnClickListener onRootedClickListner = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			finish();
		}
	};

	/**
	 * This method is called to open AuthenticationActivity.
	 */
	private void startAuthenticationActivity() {
		Intent intent = new Intent(ServerDetails.this, AuthenticationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		context = null;
		super.onDestroy();
	}
}
