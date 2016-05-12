/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.app.catalog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.app.catalog.api.ApplicationManager;
import org.wso2.app.catalog.beans.RegistrationProfile;
import org.wso2.app.catalog.beans.ServerConfig;
import org.wso2.app.catalog.services.DynamicClientManager;
import org.wso2.app.catalog.utils.CommonDialogUtils;
import org.wso2.app.catalog.utils.CommonUtils;
import org.wso2.app.catalog.utils.Constants;
import org.wso2.app.catalog.utils.Preference;
import org.wso2.emm.agent.proxy.IdentityProxy;
import org.wso2.emm.agent.proxy.beans.CredentialInfo;
import org.wso2.emm.agent.proxy.interfaces.APIAccessCallBack;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class LoginActivity extends Activity implements APIAccessCallBack, APIResultCallBack {
    private Button btnLogin;
    private EditText etUsername;
    private EditText etDomain;
    private EditText etPassword;
    private Context context;
    private String username;
    private String usernameVal;
    private String passwordVal;
    private ProgressDialog progressDialog;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final String COLON = ":";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        if (Preference.getString(context, Constants.PreferenceFlag.PROTOCOL) == null || Preference.getString(context,
                                                                                 Constants.PreferenceFlag.IP) == null) {
            Preference.putString(context, Constants.PreferenceFlag.PROTOCOL, Constants.SERVER_PROTOCOL);
            Preference.putString(context, Constants.PreferenceFlag.PORT, Constants.SERVER_PORT);
            Preference.putString(context, Constants.PreferenceFlag.IP, Constants.SERVER_ADDRESS);
        }

        String clientKey = Preference.getString(context, Constants.CLIENT_ID);
        String clientSecret = Preference.getString(context, Constants.CLIENT_SECRET);
        if (clientKey != null && clientSecret != null) {
            Intent appListIntent = new Intent(LoginActivity.this, AppListActivity.class);
            startActivity(appListIntent);
        }

        etDomain = (EditText) findViewById(R.id.etDomain);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etDomain.setFocusable(true);
        etDomain.requestFocus();
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(onClickAuthenticate);
        btnLogin.setEnabled(false);

        // change button color background till user enters a valid input
        btnLogin.setBackgroundColor(Color.parseColor("#76756f"));
        btnLogin.setTextColor(Color.parseColor("#343331"));

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

    /**
     * Validation done to see if the username and password fields are properly
     * entered.
     */
    private void enableSubmitIfReady() {

        boolean isReady = false;

        if (etUsername.getText().toString().length() >= 1 &&
            etPassword.getText().toString().length() >= 1) {
            isReady = true;
        }

        if (isReady) {
            btnLogin.setBackgroundColor(Color.parseColor("#11375B"));
            btnLogin.setTextColor(Color.WHITE);
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setBackgroundColor(Color.parseColor("#76756f"));
            btnLogin.setTextColor(Color.parseColor("#343331"));
            btnLogin.setEnabled(false);
        }
    }

    private View.OnClickListener onClickAuthenticate = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if (etUsername.getText() != null && !etUsername.getText().toString().trim().isEmpty() &&
                etPassword.getText() != null && !etPassword.getText().toString().trim().isEmpty()) {

                passwordVal = etPassword.getText().toString().trim();
                usernameVal = etUsername.getText().toString().trim();
                if (etDomain.getText() != null && !etDomain.getText().toString().trim().isEmpty()) {
                    usernameVal +=
                            getResources().getString(R.string.intent_extra_at) +
                            etDomain.getText().toString().trim();
                }

                getClientCredentials();
            } else {
                if (etUsername.getText() != null && !etUsername.getText().toString().trim().isEmpty()) {
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

    /**
     * Start authentication process.
     */
    private void startAuthentication() {
        // Check network connection availability before calling the API.
        if (CommonUtils.isNetworkAvailable(context)) {
            String clientId = Preference.getString(context, Constants.CLIENT_ID);
            String clientSecret = Preference.getString(context, Constants.CLIENT_SECRET);
            String clientName;

            if (clientId == null || clientSecret == null) {
                String clientCredentials = Preference.getString(context, getResources().getString(
                        R.string.shared_pref_client_credentials));
                if (clientCredentials != null) {
                    try {
                        JSONObject payload = new JSONObject(clientCredentials);
                        clientId = payload.getString(Constants.CLIENT_ID);
                        clientSecret = payload.getString(Constants.CLIENT_SECRET);
                        clientName = payload.getString(Constants.CLIENT_NAME);

                        if (clientName != null && !clientName.isEmpty()) {
                            Preference.putString(context, Constants.CLIENT_NAME, clientName);
                        }
                        if (clientId != null && !clientId.isEmpty() &&
                            clientSecret != null && !clientSecret.isEmpty()) {
                            initializeIDPLib(clientId, clientSecret);
                        }
                    } catch (JSONException e) {
                        String msg = "error occurred while parsing client credential payload";
                        Log.e(TAG, msg, e);
                        CommonDialogUtils.stopProgressDialog(progressDialog);
                        showInternalServerErrorMessage();
                    }
                } else {
                    String msg = "error occurred while retrieving client credentials";
                    Log.e(TAG, msg);
                    CommonDialogUtils.stopProgressDialog(progressDialog);
                    showInternalServerErrorMessage();
                }
            } else {
                initializeIDPLib(clientId, clientSecret);
            }

        } else {
            CommonDialogUtils.stopProgressDialog(progressDialog);
            CommonDialogUtils.showNetworkUnavailableMessage(context);
        }

    }

    /**
     * Initialize the Android IDP SDK by passing credentials,client ID and
     * client secret.
     *
     * @param clientKey    client id value to access APIs..
     * @param clientSecret client secret value to access APIs.
     */
    private void initializeIDPLib(String clientKey, String clientSecret) {

        String serverIP = Preference.getString(LoginActivity.this, Constants.PreferenceFlag.IP);
        if (serverIP != null && !serverIP.isEmpty()) {
            ServerConfig utils = new ServerConfig();
            utils.setServerIP(serverIP);
            String serverURL = utils.getServerURL(context) + Constants.OAUTH_ENDPOINT;
            Editable tenantDomain = etDomain.getText();

            if (tenantDomain != null && !tenantDomain.toString().trim().isEmpty()) {
                username =
                        etUsername.getText().toString().trim() +
                        context.getResources().getString(R.string.intent_extra_at) +
                        tenantDomain.toString().trim();

            } else {
                username = etUsername.getText().toString().trim();
            }

            Preference.putString(context, Constants.CLIENT_ID, clientKey);
            Preference.putString(context, Constants.CLIENT_SECRET, clientSecret);

            CredentialInfo info = new CredentialInfo();
            info.setClientID(clientKey);
            info.setClientSecret(clientSecret);
            info.setUsername(username);
            try {
                info.setPassword(URLEncoder.encode(passwordVal, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                String msg = "error occurred while encoding password.";
                Log.e(TAG, msg, e);
            }
            info.setTokenEndPoint(serverURL);
            if (tenantDomain != null && !tenantDomain.toString().trim().isEmpty()) {
                info.setTenantDomain(tenantDomain.toString().trim());
            }

            IdentityProxy.getInstance().init(info, LoginActivity.this, this.getApplicationContext());
        }
    }

    /**
     * Manipulates the dynamic client registration response received from server.
     *
     * @param result the result of the dynamic client request
     */
    private void manipulateDynamicClientResponse(Map<String, String> result) {
        String responseStatus;
        if (result != null) {
            responseStatus = result.get(Constants.STATUS);
            if (Constants.Status.CREATED.equals(responseStatus)) {
                String dynamicClientResponse = result.get(Constants.RESPONSE);
                if (dynamicClientResponse != null) {
                    Preference.putString(context, getResources().getString(R.string.shared_pref_client_credentials),
                                         dynamicClientResponse);
                    startAuthentication();
                }
            } else {
                CommonDialogUtils.stopProgressDialog(progressDialog);
                showAuthenticationError();
            }
        } else {
            CommonDialogUtils.stopProgressDialog(progressDialog);
            showAuthenticationError();
        }
    }

    /**
     * This method is used to retrieve consumer-key and consumer-secret.
     */
    private void getClientCredentials() {
        String ipSaved = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);
        progressDialog = ProgressDialog.show(context, getResources().getString(R.string.dialog_authenticate), getResources().
                getString(R.string.dialog_message_please_wait), true);
        if (ipSaved != null && !ipSaved.isEmpty()) {
            ServerConfig utils = new ServerConfig();
            utils.setServerIP(ipSaved);

            RegistrationProfile profile = new RegistrationProfile();
            profile.setCallbackUrl(Constants.EMPTY_STRING);
            profile.setClientName(CommonUtils.getDeviceId(context));
            profile.setGrantType(Constants.GRANT_TYPE);
            profile.setOwner(usernameVal);
            profile.setTokenScope(Constants.TOKEN_SCOPE);
            profile.setSaasApp(true);

            byte[] dataToEncode = new byte[0];
            try {
                dataToEncode = (usernameVal + COLON + passwordVal).getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "UTF-8 is unsupported" + e);
            }

            try {
                String encodedCredentials = Base64.encodeToString(dataToEncode, Base64.DEFAULT);
                DynamicClientManager dynamicClientManager = new DynamicClientManager();
                dynamicClientManager.getClientCredentials(profile, utils, context, encodedCredentials, LoginActivity.this);
            } catch (AppCatalogException e) {
                Log.e(TAG, "Client credentials generation failed" + e);
                CommonDialogUtils.stopProgressDialog(progressDialog);
                showAuthenticationError();
            }
        } else {
            Log.e(TAG, "There is no valid IP to contact the server");
        }

    }

    /**
     * Shows internal server error message for authentication.
     */
    private void showInternalServerErrorMessage() {
        CommonDialogUtils.stopProgressDialog(progressDialog);
        CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                                                              getResources().getString(
                                                                      R.string.title_head_connection_error),
                                                              getResources().getString(
                                                                      R.string.error_internal_server),
                                                              getResources().getString(
                                                                      R.string.button_ok),
                                                              null);
    }

    /**
     * Shows credentials error message for authentication.
     */
    private void showAuthenticationError(){
        CommonDialogUtils.stopProgressDialog(progressDialog);
        CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                                                              getResources().getString(R.string.title_head_authentication_error),
                                                              getResources().getString(R.string.error_authentication_failed),
                                                              getResources().getString(R.string.button_ok),
                                                              null);
    }

    /**
     * Shows common error message for authentication.
     */
    private void showAuthCommonErrorMessage() {
        CommonDialogUtils.stopProgressDialog(progressDialog);
        CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                                                              getResources().getString(
                                                                      R.string.title_head_authentication_error),
                                                              getResources().getString(
                                                                      R.string.error_for_all_unknown_authentication_failures),
                                                              getResources().getString(
                                                                      R.string.button_ok),
                                                              null);

    }

    @Override
    protected void onResume() {
        ApplicationManager applicationManager = new ApplicationManager(context);
        if(applicationManager.isPackageInstalled(Constants.AGENT_PACKAGE_NAME)) {
            startAppListActivity();
        }
        super.onResume();
    }

    /**
     * This method is called to open AppListActivity.
     */
    private void startAppListActivity() {
        Intent intent = new Intent(LoginActivity.this, AppListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onAPIAccessReceive(String status) {
        if (status != null) {
            if (status.trim().equals(Constants.Status.SUCCESSFUL)) {

                Preference.putString(context, Constants.USERNAME, username);

                // Check network connection availability before calling the API.
                CommonDialogUtils.stopProgressDialog(progressDialog);
                if (CommonUtils.isNetworkAvailable(context)) {
                    Intent appListIntent = new Intent(LoginActivity.this, AppListActivity.class);
                    appListIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(appListIntent);
                } else {
                    CommonDialogUtils.stopProgressDialog(progressDialog);
                    CommonDialogUtils.showNetworkUnavailableMessage(LoginActivity.this);
                }

            } else if (status.trim().equals(Constants.Status.AUTHENTICATION_FAILED)) {
                showAuthenticationError();
                // clearing client credentials from shared memory
                CommonUtils.clearClientCredentials(context);
            } else if (status.trim().equals(Constants.Status.INTERNAL_SERVER_ERROR)) {
                showInternalServerErrorMessage();
            } else {
                showAuthCommonErrorMessage();
            }
        } else {
            showAuthCommonErrorMessage();
        }

    }

    @Override
    public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
        if (requestCode == Constants.DYNAMIC_CLIENT_REGISTER_REQUEST_CODE) {
            manipulateDynamicClientResponse(result);
        }
    }
}
