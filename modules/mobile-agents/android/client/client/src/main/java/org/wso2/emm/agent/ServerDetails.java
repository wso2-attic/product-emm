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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.wso2.emm.agent.api.DeviceState;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;
import org.wso2.emm.agent.utils.Response;

/**
 * This the the activity that is used to capture the server's host name.
 */
public class ServerDetails extends Activity {

	private TextView evServerIP;
	private Button btnStartRegistration;
	private Context context;
	private DeviceState state;
	private TextView txtSeverAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		context = this.getApplicationContext();
		state = new DeviceState(context);
		evServerIP = (TextView) findViewById(R.id.evServerIP);
		txtSeverAddress = (TextView) findViewById(R.id.tvSeverAddress);
		btnStartRegistration = (Button) findViewById(R.id.btnStartRegistration);
		btnStartRegistration.setBackground(getResources().getDrawable(R.drawable.btn_grey));
		btnStartRegistration.setTextColor(getResources().getColor(R.color.black));
		Response deviceCompatibility = state.evaluateCompatibility();

		if (!deviceCompatibility.getCode()) {
			txtSeverAddress.setText(deviceCompatibility.getDescriptionResourceID());
			btnStartRegistration.setVisibility(View.GONE);
			txtSeverAddress.setVisibility(View.VISIBLE);
			evServerIP.setVisibility(View.GONE);
		} else {
			btnStartRegistration.setVisibility(View.VISIBLE);
			evServerIP.setVisibility(View.VISIBLE);
			String ipSaved = Constants.DEFAULT_HOST;
			String prefIP = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);
			if (prefIP != null) {
				ipSaved = prefIP;
			}

			if (Constants.DEFAULT_HOST != null) {
				ipSaved = Constants.DEFAULT_HOST;
				CommonUtils.saveHostDeatils(context, ipSaved);
			}

			// check if we have the IP saved previously.
			if (ipSaved != null && !ipSaved.isEmpty()) {
				evServerIP.setText(ipSaved);
				startAuthenticationActivity();
			}
			boolean isDeviceActive = Preference.getBoolean(context, Constants.PreferenceFlag.DEVICE_ACTIVE);

			if (isDeviceActive) {
				Intent intent = new Intent(ServerDetails.this, AlreadyRegisteredActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}

			evServerIP.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					enableSubmitIfReady();
				}

				@Override
				public void afterTextChanged(Editable s) {
					enableSubmitIfReady();
				}
			});
			// on click handler for start registration.
			btnStartRegistration.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					loadStartRegistrationDialog();
				}
			});
		}
	}

	/**
	 * Validation done to see if the server IP field is properly
	 * entered.
	 */
	private void enableSubmitIfReady() {

		boolean isReady = false;

		if (evServerIP.getText().toString().length() >= 1) {
			isReady = true;
		}

		if (isReady) {
			btnStartRegistration.setBackground(getResources().getDrawable(R.drawable.btn_orange));
			btnStartRegistration.setTextColor(getResources().getColor(R.color.white));
			btnStartRegistration.setEnabled(true);
		} else {
			btnStartRegistration.setBackground(getResources().getDrawable(R.drawable.btn_grey));
			btnStartRegistration.setTextColor(getResources().getColor(R.color.black));
			btnStartRegistration.setEnabled(false);
		}
	}

	private void loadStartRegistrationDialog(){
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ServerDetails.this);
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append(getResources().getString(R.string.dialog_init_confirmation));
		messageBuilder.append(context.getResources().getString(R.string.intent_extra_space));
		messageBuilder.append(evServerIP.getText().toString());
		messageBuilder.append(context.getResources().getString(R.string.intent_extra_space));
		messageBuilder.append(getResources().getString(R.string.dialog_init_end_general));
		alertBuilder.setMessage(messageBuilder.toString())
		            .setPositiveButton(getResources().getString(R.string.yes),
		                               dialogClickListener)
		            .setNegativeButton(getResources().getString(R.string.no),
		                               dialogClickListener).show();
	}

	private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					if (!evServerIP.getText().toString().trim().isEmpty()) {
						String host = evServerIP.getText().toString().trim();
						CommonUtils.saveHostDeatils(context, host);

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

				default:
					break;
			}
		}
	};

	/**
	 * This method is called to open AuthenticationActivity.
	 */
	private void startAuthenticationActivity() {
		if (Constants.AUTO_ENROLLMENT_BACKGROUND_SERVICE_ENABLED == false) {
			Intent intent = new Intent(ServerDetails.this, AuthenticationActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	protected void onDestroy() {
		context = null;
		super.onDestroy();
	}
}
