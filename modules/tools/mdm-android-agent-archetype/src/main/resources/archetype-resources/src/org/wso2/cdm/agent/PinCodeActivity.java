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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.Preference;

public class PinCodeActivity extends Activity {
	private TextView lblPin;
	private EditText txtPin;
	private EditText txtOldPin;
	private Button btnPin;
	private String username = null;
	private String regId = "";
	private final int TAG_BTN_SET_PIN = 0;
	private String fromActivity = null;
	Context context;
	AlertDialog.Builder alertDialog;
	EditText input;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pin_code);
		context = PinCodeActivity.this;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey(getResources().getString(R.string.intent_extra_username))) {
				username =
						extras.getString(getResources().getString(R.string.intent_extra_username));
			}

			if (extras.containsKey(getResources().getString(R.string.intent_extra_regid))) {
				regId = extras.getString(getResources().getString(R.string.intent_extra_regid));
			}

			if (extras.containsKey(getResources().getString(R.string.intent_extra_from_activity))) {
				fromActivity = extras.getString(
						getResources().getString(R.string.intent_extra_from_activity));
			}
		}

		lblPin = (TextView) findViewById(R.id.lblPin);
		txtPin = (EditText) findViewById(R.id.txtPinCode);
		txtOldPin = (EditText) findViewById(R.id.txtOldPinCode);
		btnPin = (Button) findViewById(R.id.btnSetPin);
		btnPin.setTag(TAG_BTN_SET_PIN);
		btnPin.setOnClickListener(onClickListenerButtonClicked);
		btnPin.setEnabled(false);
		btnPin.setBackground(getResources().getDrawable(R.drawable.btn_grey));
		btnPin.setTextColor(getResources().getColor(R.color.black));

		if (fromActivity != null &&
		    fromActivity.equals(AlreadyRegisteredActivity.class.getSimpleName())) {
			lblPin.setVisibility(View.GONE);
			txtOldPin.setVisibility(View.VISIBLE);
			txtPin.setHint(getResources().getString(R.string.hint_new_pin));
			txtPin.setEnabled(true);

			txtPin.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
				                              int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
				                          int count) {
					enableNewPINSubmitIfReady();
				}

				@Override
				public void afterTextChanged(Editable s) {
					enableSubmitIfReady();
				}
			});

			txtOldPin.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
				                              int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
				                          int count) {
					enableNewPINSubmitIfReady();
				}

				@Override
				public void afterTextChanged(Editable s) {
					enableSubmitIfReady();
				}
			});
		} else {
			txtPin.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
				                              int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
				                          int count) {
					enableSubmitIfReady();
				}

				@Override
				public void afterTextChanged(Editable s) {
					enableSubmitIfReady();
				}
			});
		}
	}

	OnClickListener onClickListenerButtonClicked = new OnClickListener() {

		@Override
		public void onClick(View view) {
			int viewTag = (Integer) view.getTag();

			switch (viewTag) {

				case TAG_BTN_SET_PIN:
					input = new EditText(PinCodeActivity.this);
					alertDialog = CommonDialogUtils
							.getAlertDialogWithTwoButtonAndEditView(PinCodeActivity.this,
							                                        getResources().getString(
									                                        R.string.title_head_confirm_pin)
									, getResources().getString(R.string.button_ok),
							                                        getResources().getString(
									                                        R.string.button_cancel),
							                                        dialogClickListener,
							                                        dialogClickListener, input);

					final AlertDialog dialog = alertDialog.create();
					dialog.show();
					//Overriding default positive button behavior to keep the dialog open, if PINS don't match
					dialog.getButton(AlertDialog.BUTTON_POSITIVE)
					      .setOnClickListener(new View.OnClickListener() {
						      @Override
						      public void onClick(View v) {
							      if (txtPin.getText().toString()
							                .equals(input.getText().toString())) {
								      savePin();
								      dialog.dismiss();
							      } else {
								      input.setError(getResources().getString(
										      R.string.validation_pin_confirm));
							      }
						      }
					      });
					input.setInputType(InputType.TYPE_CLASS_NUMBER);
					input.setTransformationMethod(new PasswordTransformationMethod());
					break;
				default:
					break;
			}

		}
	};

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					savePin();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
			}
		}
	};

	public void savePin() {
		Preference.put(context, getResources().getString(R.string.shared_pref_pin),
		               txtPin.getText().toString().trim());

		if (fromActivity != null &&
		    (fromActivity.equals(AlreadyRegisteredActivity.class.getSimpleName()))) {
			Toast.makeText(getApplicationContext(),
			               getResources().getString(R.string.toast_message_pin_change_success),
			               Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(PinCodeActivity.this, AlreadyRegisteredActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
			                PinCodeActivity.class.getSimpleName());
			intent.putExtra(getResources().getString(R.string.intent_extra_regid), regId);
			startActivity(intent);
		} else {
			Intent intent = new Intent(PinCodeActivity.this, RegistrationActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_extra_regid), regId);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(getResources().getString(R.string.intent_extra_username), username);
			startActivity(intent);
		}
	}

	@SuppressLint("NewApi")
	public void enableSubmitIfReady() {

		boolean isReady = false;

		if (txtPin.getText().toString().length() >= 4) {
			isReady = true;
		}

		if (isReady) {
			btnPin.setBackground(getResources().getDrawable(R.drawable.btn_orange));
			btnPin.setTextColor(getResources().getColor(R.color.white));
			btnPin.setEnabled(true);
		} else {
			btnPin.setBackground(getResources().getDrawable(R.drawable.btn_grey));
			btnPin.setTextColor(getResources().getColor(R.color.black));
			btnPin.setEnabled(false);
		}
	}

	@SuppressLint("NewApi")
	public void enableNewPINSubmitIfReady() {

		boolean isReady = false;
		SharedPreferences mainPref =
				this.getSharedPreferences(getResources().getString(R.string.shared_pref_package),
				                          Context.MODE_PRIVATE);
		String pin = mainPref.getString(getResources().getString(R.string.shared_pref_pin), "");
		if (txtOldPin.getText().toString().trim().length() >= 4 &&
		    txtOldPin.getText().toString().trim().equals(pin.trim())) {
			txtPin.setEnabled(true);
		} else {
			txtPin.setEnabled(false);
		}

		if (txtPin.getText().toString().trim().length() >= 4 &&
		    txtOldPin.getText().toString().trim().length() >= 4) {
			if (txtOldPin.getText().toString().trim().equals(pin.trim())) {
				isReady = true;
			} else {
				isReady = false;
				Toast.makeText(getApplicationContext(),
				               getResources().getString(R.string.toast_message_pin_change_failed),
				               Toast.LENGTH_SHORT).show();
			}
		}

		if (isReady) {
			btnPin.setBackground(getResources().getDrawable(R.drawable.btn_orange));
			btnPin.setTextColor(getResources().getColor(R.color.white));
			btnPin.setEnabled(true);
		} else {
			btnPin.setBackground(getResources().getDrawable(R.drawable.btn_grey));
			btnPin.setTextColor(getResources().getColor(R.color.black));
			btnPin.setEnabled(false);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && fromActivity != null &&
		    fromActivity.equals(AlreadyRegisteredActivity.class.getSimpleName())) {
			Intent intent = new Intent(PinCodeActivity.this, AlreadyRegisteredActivity.class);
			intent.putExtra(getResources().getString(R.string.intent_extra_from_activity),
			                PinCodeActivity.class.getSimpleName());
			intent.putExtra(getResources().getString(R.string.intent_extra_regid), regId);
			startActivity(intent);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

}

