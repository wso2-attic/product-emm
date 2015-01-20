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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.api.PhoneState;
import org.wso2.cdm.agent.proxy.APIAccessCallBack;
import org.wso2.cdm.agent.proxy.APIResultCallBack;
import org.wso2.cdm.agent.proxy.IdentityProxy;
import org.wso2.cdm.agent.services.AlarmReceiver;
import org.wso2.cdm.agent.utils.CommonDialogUtils;
import org.wso2.cdm.agent.utils.CommonUtilities;
import org.wso2.cdm.agent.utils.Constant;
import org.wso2.cdm.agent.utils.HTTPConnectorUtils;
import org.wso2.cdm.agent.utils.Preference;
import org.wso2.cdm.agent.utils.ServerUtils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;

/**
 * Activity that captures username, password and device ownership details.
 */
public class AuthenticationActivity extends SherlockActivity implements APIAccessCallBack,
                                                            APIResultCallBack {


	private String TAG = AuthenticationActivity.class.getSimpleName();

	Button btnRegister;
	EditText etUsername;
	EditText etDomain;
	EditText etPassword;
	RadioButton radioBYOD, radioCOPE;
	String deviceType;
	Context context;
	String senderId;
	String usernameForRegister;
	String usernameVal;
	String passwordVal;
	String domain;
	ProgressDialog progressDialog;
	AlertDialog.Builder alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setCustomView(R.layout.custom_sherlock_bar);
		getSupportActionBar().setTitle(R.string.empty_app_title);
		View homeIcon =
		                (View) findViewById(
		                                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
		                                                                                          ? android.R.id.home
		                                                                                          : R.id.abs__home).getParent();
		homeIcon.setVisibility(View.GONE);

		context = AuthenticationActivity.this;
		deviceType = getResources().getString(R.string.device_enroll_type_byod);
		etDomain = (EditText) findViewById(R.id.etDomain);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etPassword = (EditText) findViewById(R.id.etPassword);
		radioBYOD = (RadioButton) findViewById(R.id.radioBYOD);
		radioCOPE = (RadioButton) findViewById(R.id.radioCOPE);
		etDomain.setFocusable(true);
		etDomain.requestFocus();
		btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setEnabled(false);
		btnRegister.setOnClickListener(onClickAuthenticate);
		// change button color background till user enters a valid input
		btnRegister.setBackground(getResources().getDrawable(R.drawable.btn_grey));
		btnRegister.setTextColor(getResources().getColor(R.color.black));

		etUsername.addTextChangedListener(new TextWatcher() {
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

		etPassword.addTextChangedListener(new TextWatcher() {
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

	}

	OnClickListener onClickAuthenticate = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if (etUsername.getText() != null &&
			    !etUsername.getText().toString().trim().equals("") &&
			    etPassword.getText() != null && !etPassword.getText().toString().trim().equals("")) {

				passwordVal = etPassword.getText().toString().trim();
				usernameVal = etUsername.getText().toString().trim();
				if (etDomain.getText() != null && !etDomain.getText().toString().trim().equals("")) {
					usernameVal += "@" + etDomain.getText().toString().trim();
				}

				if (radioBYOD.isChecked()) {
					deviceType = getResources().getString(R.string.device_enroll_type_byod);
				} else {
					deviceType = getResources().getString(R.string.device_enroll_type_cope);
				}
				StringBuilder messageBuilder = new StringBuilder();
				messageBuilder.append(getResources().getString(R.string.dialog_init_middle));
				messageBuilder.append(" ");
				messageBuilder.append(deviceType);
				messageBuilder.append(" ");
				messageBuilder.append(getResources().getString(R.string.dialog_init_end));
				alertDialog =
				              CommonDialogUtils.getAlertDialogWithTwoButtonAndTitle(context,
				                                                                    getResources().getString(R.string.dialog_init_device_type),
				                                                                    messageBuilder.toString(),
				                                                                    getResources().getString(R.string.yes),
				                                                                    getResources().getString(R.string.no),
				                                                                    dialogClickListener,
				                                                                    dialogClickListener);
				alertDialog.show();
			} else {
				if (etUsername.getText() != null &&
				    !etUsername.getText().toString().trim().equals("")) {
					Toast.makeText(context,
					               getResources().getString(R.string.toast_error_password),
					               Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(context,
					               getResources().getString(R.string.toast_error_username),
					               Toast.LENGTH_LONG).show();
				}
			}

		}
	};

	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					dialog.dismiss();
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					startAuthentication();
					break;
			}
		}
	};

	/**
	 * Start authentication process.
	 */
	public void startAuthentication() {
		Preference.put(context, getResources().getString(R.string.shared_pref_reg_type), deviceType);
		// Check network connection availability before calling the API.
		if (PhoneState.isNetworkAvailable(context)) {
			authenticate();
		} else {
			CommonDialogUtils.stopProgressDialog(progressDialog);
			CommonDialogUtils.showNetworkUnavailableMessage(context);
		}

	}

	/**
	 * Communicating with the server to authenticate user.
	 */
	private void authenticate() {

		AsyncTask<Void, Void, Map<String, String>> mLicenseTask =
		                                                          new AsyncTask<Void, Void, Map<String, String>>() {

			                                                          @Override
			                                                          protected Map<String, String> doInBackground(Void... params) {
				                                                          Map<String, String> response =
				                                                                                         null;

				                                                          Map<String, String> requestParametres =
				                                                                                                  new HashMap<String, String>();

				                                                          requestParametres.put(Constant.USERNAME,
				                                                                                usernameVal);
				                                                          requestParametres.put(Constant.PASSWORD,
				                                                                                passwordVal);
				                                                          response =
				                                                                     HTTPConnectorUtils.postData(context,
				                                                                                                 CommonUtilities.SERVER_URL +
				                                                                                                 CommonUtilities.SERVER_AUTHENTICATION_ENDPOINT,
				                                                                                                 requestParametres);
				                                                          return response;
			                                                          }

			                                                          @Override
			                                                          protected void onPreExecute() {
				                                                          progressDialog =
				                                                                           ProgressDialog.show(context,
				                                                                                               getResources().getString(R.string.dialog_authenticate),
				                                                                                               getResources().getString(R.string.dialog_please_wait),
				                                                                                               true);

			                                                          };

			                                                          @Override
			                                                          protected void onPostExecute(Map<String, String> result) {
			                                                        	  authenticateResponse(result);

			                                                          }

		                                                          };

		mLicenseTask.execute();

	}
	
	/**
	 * Handles the response received from server for the authentication request.
	 * @param result Received response from server.
	 */
	private void authenticateResponse(Map<String, String> result){
		 if (result != null) {
             String responseStatus =
                                     result.get(Constant.STATUS);
                 if (responseStatus != null) {
                     if (responseStatus.equalsIgnoreCase(CommonUtilities.REQUEST_SUCCESSFUL)) {
                         getLicense();
                     } else if (responseStatus.equalsIgnoreCase(CommonUtilities.UNAUTHORIZED_ACCESS)) {
                         CommonDialogUtils.stopProgressDialog(progressDialog);
                         alertDialog =
                                       CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                                                                                             getResources().getString(R.string.title_head_authentication_error),
                                                                                             getResources().getString(R.string.error_authentication_failed),
                                                                                             getResources().getString(R.string.button_ok),
                                                                                             dialogClickListener);
                     } else if (responseStatus.trim()
                                              .equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
                         Log.e(TAG, "Error: Internal server error");
                         showInternalServerErrorMessage();

                     } else {
                         Log.e(TAG, "Status: " + responseStatus);
                         showAuthCommonErrorMessage();
                     }
                 } else {
                     Log.e(TAG, "The value of status is null in authenticating");
                     showAuthCommonErrorMessage();
                 }

         } else {
             Log.e(TAG, "The result is null in authenticating");
             showAuthCommonErrorMessage();
         }
	}

	/**
	 * Initialize get device license agreement. Check if the user has already
	 * agreed to license agreement
	 */
	private void getLicense() {
		String licenseAgreedResponse =
		                  Preference.get(context,
		                                 getResources().getString(R.string.shared_pref_isagreed));
		String type =
		              Preference.get(context,
		                             getResources().getString(R.string.shared_pref_reg_type));

		// No need to display license for COPE devices
		if (type.trim().equals(getResources().getString(R.string.device_enroll_type_byod))) {
			if (licenseAgreedResponse == null) {

				// Get License
				OnCancelListener cancelListener = new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface arg0) {
						CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
						                                                      getResources().getString(R.string.error_enrollment_failed_detail),
						                                                      getResources().getString(R.string.error_enrollment_failed),
						                                                      getResources().getString(R.string.button_ok),
						                                                      null);
					}
				};

				progressDialog =
				                 CommonDialogUtils.showPrgressDialog(context,
				                                                     getResources().getString(R.string.dialog_license_agreement),
				                                                     getResources().getString(R.string.dialog_please_wait),
				                                                     cancelListener);

				// Check network connection availability before calling the API.
				if (PhoneState.isNetworkAvailable(context)) {
					getLicenseFromServer();
				} else {
					CommonDialogUtils.stopProgressDialog(progressDialog);
					CommonDialogUtils.showNetworkUnavailableMessage(context);
				}

			} else {
				loadPincodeAcitvity();
			}
		} else {
			loadPincodeAcitvity();
		}

	}

	/**
	 * Retriever license agreement details from the server
	 */
	private void getLicenseFromServer() {

		AsyncTask<Void, Void, Map<String, String>> mLicenseTask =
		                                                          new AsyncTask<Void, Void, Map<String, String>>() {

			                                                          @Override
			                                                          protected Map<String, String> doInBackground(Void... params) {
				                                                          Map<String, String> response =
				                                                                                         null;
				                                                          response =
				                                                                     HTTPConnectorUtils.postData(context,
				                                                                                                 CommonUtilities.SERVER_URL +
				                                                                                                 CommonUtilities.LICENSE_ENDPOINT,
				                                                                                                 null);
				                                                          return response;
			                                                          }

			                                                          @Override
			                                                          protected void onPreExecute() {
			                                                          };

			                                                          @Override
			                                                          protected void onPostExecute(Map<String, String> result) {
				                                                          CommonDialogUtils.stopProgressDialog(progressDialog);
				                                                          manipulateLicenseResponse(result);
			                                                          }

		                                                          };

		mLicenseTask.execute();

	}

	/**
	 * Manipulates the License agreement response received from server.
	 * 
	 * @param result
	 *            the result of the license agreement request
	 */
	private void manipulateLicenseResponse(Map<String, String> result) {
		String responseStatus;
		CommonDialogUtils.stopProgressDialog(progressDialog);

		if (result != null) {
			responseStatus = result.get(CommonUtilities.STATUS_KEY);
			if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
				String licenseAgreement = result.get(Constant.RESPONSE);

				if (licenseAgreement != null) {
					Preference.put(context, getResources().getString(R.string.shared_pref_eula),
					               licenseAgreement);
					showAgreement(licenseAgreement, CommonUtilities.EULA_TITLE);
				} else {
					showErrorMessage(getResources().getString(R.string.error_enrollment_failed_detail),
					                 getResources().getString(R.string.error_enrollment_failed));
				}

			} else if (responseStatus.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
				Log.e(TAG, "The result is : " + result);
				showInternalServerErrorMessage();
			} else {
				showEnrollementFailedErrorMessage();
			}

		} else {
			Log.e(TAG, "The result is null in manipulateLicenseResponse()");
			showEnrollementFailedErrorMessage();
		}
	}

	public void showErrorMessage(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setPositiveButton(getResources().getString(R.string.button_ok),
		                          new DialogInterface.OnClickListener() {
			                          public void onClick(DialogInterface dialog, int id) {
				                          cancelEntry();
				                          dialog.dismiss();
			                          }
		                          });
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showAgreement(String message, String title) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(R.layout.custom_terms_popup);
		dialog.setTitle(CommonUtilities.EULA_TITLE);
		dialog.setCancelable(false);

		WebView web = (WebView) dialog.findViewById(R.id.webview);
		String html = "<html><body>" + message + "</body></html>";
		String mime = "text/html";
		String encoding = "utf-8";
		web.loadDataWithBaseURL(null, html, mime, encoding, null);

		Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
		Button cancelButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);

		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Preference.put(context, getResources().getString(R.string.shared_pref_isagreed),
				               "1");
				dialog.dismiss();
				loadPincodeAcitvity();
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				cancelEntry();
			}
		});

		dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_SEARCH && event.getRepeatCount() == 0) {
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
					return true;
				}
				return false;
			}
		});

		dialog.show();
	}

	private void loadPincodeAcitvity() {
		Intent intent = new Intent(AuthenticationActivity.this, PinCodeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(getResources().getString(R.string.intent_extra_username), usernameVal);
		startActivity(intent);
	}

	public void cancelEntry() {
		SharedPreferences mainPref =
		                             context.getSharedPreferences(getResources().getString(R.string.shared_pref_package),
		                                                          Context.MODE_PRIVATE);
		Editor editor = mainPref.edit();
		editor.putString(getResources().getString(R.string.shared_pref_policy), "");
		editor.putString(getResources().getString(R.string.shared_pref_isagreed), "0");
		editor.putString(getResources().getString(R.string.shared_pref_registered), "0");
		editor.putString(getResources().getString(R.string.shared_pref_ip), "");
		editor.commit();

		Intent intentIP = new Intent(AuthenticationActivity.this, ServerDetails.class);
		intentIP.putExtra(getResources().getString(R.string.intent_extra_from_activity),
		                  AuthenticationActivity.class.getSimpleName());
		intentIP.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intentIP);

	}

	public void showAlertSingle(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setPositiveButton(getResources().getString(R.string.button_ok),
		                          new DialogInterface.OnClickListener() {
			                          public void onClick(DialogInterface dialog, int id) {
				                          // cancelEntry();
				                          dialog.cancel();
			                          }
		                          });
		AlertDialog alert = builder.create();
		alert.show();
	}

	public void showAuthErrorMessage(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(message);
		builder.setTitle(title);
		builder.setCancelable(true);
		builder.setPositiveButton(getResources().getString(R.string.button_ok),
		                          new DialogInterface.OnClickListener() {
			                          public void onClick(DialogInterface dialog, int id) {
				                          dialog.dismiss();
			                          }
		                          });
		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * Initialize the Android IDP sdk by passing user credentials,client ID and
	 * client secret.
	 */
	private void initializeIDPLib(String clientKey, String clientSecret) {
		Log.e("", "initializeIDPLib");
		String serverIP =
		                  CommonUtilities.getPref(AuthenticationActivity.this,
		                                          context.getResources()
		                                                 .getString(R.string.shared_pref_ip));
		String serverURL =
		                   CommonUtilities.SERVER_PROTOCOL + serverIP + ":" +
		                           CommonUtilities.SERVER_PORT + CommonUtilities.OAUTH_ENDPOINT;
		if (etDomain.getText() != null && !etDomain.getText().toString().trim().equals("")) {
			usernameForRegister =
			                      etUsername.getText().toString().trim() + "@" +
			                              etDomain.getText().toString().trim();

			IdentityProxy.getInstance().init(clientKey, clientSecret, usernameForRegister,
			                                 etPassword.getText().toString().trim(), serverURL,
			                                 AuthenticationActivity.this,
			                                 this.getApplicationContext());

		} else {
			usernameForRegister = etUsername.getText().toString().trim();

			IdentityProxy.getInstance().init(clientKey, clientSecret, usernameForRegister,
			                                 etPassword.getText().toString().trim(), serverURL,
			                                 AuthenticationActivity.this,
			                                 this.getApplicationContext());
		}
	}

	public void enableSubmitIfReady() {

		boolean isReady = false;

		if (etUsername.getText().toString().length() >= 1 &&
		    etPassword.getText().toString().length() >= 1) {
			isReady = true;
		}

		if (isReady) {
			btnRegister.setBackground(getResources().getDrawable(R.drawable.btn_orange));
			btnRegister.setTextColor(getResources().getColor(R.color.white));
			btnRegister.setEnabled(true);
		} else {
			btnRegister.setBackground(getResources().getDrawable(R.drawable.btn_grey));
			btnRegister.setTextColor(getResources().getColor(R.color.black));
			btnRegister.setEnabled(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getSupportMenuInflater().inflate(R.menu.auth_sherlock_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.ip_setting:
				SharedPreferences mainPref =
				                             AuthenticationActivity.this.getSharedPreferences(getResources().getString(R.string.shared_pref_package),
				                                                                              Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();
				editor.putString(getResources().getString(R.string.shared_pref_ip), "");
				editor.commit();

				Intent intentIP = new Intent(AuthenticationActivity.this, ServerDetails.class);
				intentIP.putExtra(getResources().getString(R.string.intent_extra_from_activity),
				                  AuthenticationActivity.class.getSimpleName());
				startActivity(intentIP);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			i.setAction(Intent.ACTION_MAIN);
			i.addCategory(Intent.CATEGORY_HOME);
			this.startActivity(i);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_HOME) {
			this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	DialogInterface.OnClickListener senderIdFailedClickListener =
	                                                              new DialogInterface.OnClickListener() {
		                                                              @Override
		                                                              public void onClick(DialogInterface dialog,
		                                                                                  int which) {
			                                                              etUsername.setText(CommonUtilities.EMPTY_STRING);
			                                                              etPassword.setText(CommonUtilities.EMPTY_STRING);
			                                                              etDomain.setText(CommonUtilities.EMPTY_STRING);
			                                                              btnRegister.setEnabled(false);
			                                                              btnRegister.setBackground(getResources().getDrawable(R.drawable.btn_grey));
			                                                              btnRegister.setTextColor(getResources().getColor(R.color.black));
		                                                              }
	                                                              };

	@Override
	public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
		if (requestCode == CommonUtilities.SENDER_ID_REQUEST_CODE) {
			Log.e("sender", "rec" + result);
			manipulateSenderIdResponse(result);
		} else if (requestCode == CommonUtilities.LICENSE_REQUEST_CODE) {
			manipulateLicenseResponse(result);
		}
	}

	private void showEnrollementFailedErrorMessage() {
		CommonDialogUtils.stopProgressDialog(progressDialog);
		alertDialog =
		              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
		                                                                    getResources().getString(R.string.error_enrollment_failed),
		                                                                    getResources().getString(R.string.error_enrollment_failed_detail),
		                                                                    getResources().getString(R.string.button_ok),
		                                                                    senderIdFailedClickListener);
	}

	private void managePushNotification(String mode, float interval, Editor editor) {
		if (mode.trim().toUpperCase().contains("LOCAL")) {
			CommonUtilities.LOCAL_NOTIFICATIONS_ENABLED = true;
			CommonUtilities.GCM_ENABLED = false;
			String androidID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			// if (senderId == null || senderId.equals("")) {
			editor.putString(getResources().getString(R.string.shared_pref_regId), androidID);
			// }
			editor.commit();

			startLocalNotification(interval);
		} else if (mode.trim().toUpperCase().contains("GCM")) {
			CommonUtilities.LOCAL_NOTIFICATIONS_ENABLED = false;
			CommonUtilities.GCM_ENABLED = true;
			// editor.commit();
			GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
		}

		// if (senderId!=null && !senderId.equals("")) {
		// CommonUtilities.GCM_ENABLED = true;
		// GCMRegistrar.register(context, CommonUtilities.SENDER_ID);
		// }
	}

	@Override
	public void onAPIAccessRecive(String status) {
		if (status != null) {
			if (status.trim().equals(CommonUtilities.REQUEST_SUCCESSFUL)) {

				SharedPreferences mainPref =
				                             this.getSharedPreferences(getResources().getString(R.string.shared_pref_package),
				                                                       Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();
				editor.putString(getResources().getString(R.string.shared_pref_username),
				                 usernameForRegister);
				editor.commit();

				Map<String, String> requestParams = new HashMap<String, String>();
				requestParams.put("domain", etDomain.getText().toString().trim());
				// Check network connection availability before calling the API.
				if (PhoneState.isNetworkAvailable(context)) {
					// Call get sender ID API.
					Log.e("sender id ", "call");
					ServerUtils.callSecuredAPI(AuthenticationActivity.this,
					                           CommonUtilities.SENDER_ID_ENDPOINT,
					                           CommonUtilities.GET_METHOD, requestParams,
					                           AuthenticationActivity.this,
					                           CommonUtilities.SENDER_ID_REQUEST_CODE);
				} else {
					CommonDialogUtils.stopProgressDialog(progressDialog);
					CommonDialogUtils.showNetworkUnavailableMessage(AuthenticationActivity.this);
				}

			} else if (status.trim().equals(CommonUtilities.AUTHENTICATION_FAILED)) {
				CommonDialogUtils.stopProgressDialog(progressDialog);
				alertDialog =
				              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
				                                                                    getResources().getString(R.string.title_head_authentication_error),
				                                                                    getResources().getString(R.string.error_authentication_failed),
				                                                                    getResources().getString(R.string.button_ok),
				                                                                    dialogClickListener);
			} else if (status.trim().equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
				showInternalServerErrorMessage();

			} else {
				Log.e(TAG, "Status: " + status);
				showAuthCommonErrorMessage();
			}

		} else {
			Log.e(TAG, "The value of status is null in onAPIAccessRecive()");
			showAuthCommonErrorMessage();
		}

	}

	private void showInternalServerErrorMessage() {
		CommonDialogUtils.stopProgressDialog(progressDialog);
		alertDialog =
		              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
		                                                                    getResources().getString(R.string.title_head_connection_error),
		                                                                    getResources().getString(R.string.error_internal_server),
		                                                                    getResources().getString(R.string.button_ok),
		                                                                    null);
	}

	/**
	 * Shows common error message for authentication.
	 * 
	 */
	private void showAuthCommonErrorMessage() {
		CommonDialogUtils.stopProgressDialog(progressDialog);
		alertDialog =
		              CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
		                                                                    getResources().getString(R.string.title_head_authentication_error),
		                                                                    getResources().getString(R.string.error_for_all_unknown_authentication_failures),
		                                                                    getResources().getString(R.string.button_ok),
		                                                                    null);

	}

	private void startLocalNotification(Float interval) {
		long firstTime = SystemClock.elapsedRealtime();
		firstTime += 1 * 1000;

		Intent downloader = new Intent(context, AlarmReceiver.class);
		PendingIntent recurringDownload =
		                                  PendingIntent.getBroadcast(context,
		                                                             0,
		                                                             downloader,
		                                                             PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Float seconds = interval;
		if (interval < 1.0) {

			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
			                    seconds.intValue(), recurringDownload);
		} else {
			alarms.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
			                    seconds.intValue(), recurringDownload);
		}

	}

	/**
	 * Manipulates the sender ID response.
	 * 
	 * @param result
	 *            the result of the sender ID request
	 */
	private void manipulateSenderIdResponse(Map<String, String> result) {
		String responseStatus;
		JSONObject response;

		String mode = "";
		Float interval = (float) 1.0;

		CommonDialogUtils.stopProgressDialog(progressDialog);

		if (result != null) {
			responseStatus = result.get(CommonUtilities.STATUS_KEY);
			if (responseStatus.equals(CommonUtilities.REQUEST_SUCCESSFUL)) {
				try {
					response = new JSONObject(result.get(Constant.RESPONSE));
					senderId = response.getString("sender_id");
					mode = response.getString("notifier");
					interval = (float) Float.parseFloat(response.getString("notifierInterval"));

				} catch (JSONException e) {
					e.printStackTrace();
				}
				SharedPreferences mainPref =
				                             context.getSharedPreferences(getResources().getString(R.string.shared_pref_package),
				                                                          Context.MODE_PRIVATE);
				Editor editor = mainPref.edit();

				if (senderId != null && !senderId.equals("")) {
					CommonUtilities.setSENDER_ID(senderId);
					GCMRegistrar.register(context, senderId);
					editor.putString(getResources().getString(R.string.shared_pref_sender_id),
					                 senderId);
				}
				editor.putString(getResources().getString(R.string.shared_pref_message_mode), mode);

				editor.putFloat(getResources().getString(R.string.shared_pref_interval), interval);
				editor.commit();

				managePushNotification(mode, interval, editor);
				getLicense();

			} else if (responseStatus.equals(CommonUtilities.INTERNAL_SERVER_ERROR)) {
				Log.e(TAG, "The result is : " + result);
				showInternalServerErrorMessage();

			} else {
				Log.e(TAG, "The result is : " + result);
				showEnrollementFailedErrorMessage();
			}
		} else {
			Log.e(TAG, "The result is null in manipulateSenderIdResponse()");
			showEnrollementFailedErrorMessage();
		}

	}

}
