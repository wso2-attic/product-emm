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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.app.catalog.adapters.ApplicationAdapter;
import org.wso2.app.catalog.api.ApplicationManager;
import org.wso2.app.catalog.beans.Application;
import org.wso2.app.catalog.beans.ServerConfig;
import org.wso2.app.catalog.utils.CommonDialogUtils;
import org.wso2.app.catalog.utils.CommonUtils;
import org.wso2.app.catalog.utils.Constants;
import org.wso2.app.catalog.utils.PayloadParser;
import org.wso2.app.catalog.utils.Preference;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.proxy.utils.Constants.HTTP_METHODS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppListActivity extends Activity implements APIResultCallBack {

    private Context context;
    private ListView appList;
    private ProgressDialog progressDialog;
    private TextView btnMobileApps;
    private TextView btnWebApps;
    private TextView txtError;
    private EditText etSearch;
    private TextView btnSignOut;
    private Spinner spinner;
    private ArrayList<Application> mobileApps;
    private ArrayList<Application> webApps;
    private List<String> mobileAppCategories;
    private List<String> webAppCategories;
    private AdapterView.OnItemSelectedListener categoryListener;
    private final int TAG_BTN_MOBILE_APPS = 0;
    private final int TAG_BTN_WEB_APPS = 1;
    private final int TAG_BTN_SIGN_OUT = 2;
    private static final String ACTIVE_BUTTON_COLOR = "#060f34";
    private static final String INACTIVE_BUTTON_COLOR = "#11375B";
    private static final String TAG = AppListActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        context = AppListActivity.this;
        appList = (ListView)findViewById(R.id.appList);
        btnMobileApps = (TextView)findViewById(R.id.btnMobileApps);
        btnWebApps = (TextView)findViewById(R.id.btnWebApps);
        txtError = (TextView)findViewById(R.id.txtError);
        btnSignOut = (TextView)findViewById(R.id.btnSignOut);
        etSearch = (EditText)findViewById(R.id.etSearch);
        spinner = (Spinner)findViewById(R.id.spinner);
        mobileApps = new ArrayList<>();
        webApps = new ArrayList<>();
        appList.setVisibility(View.GONE);
        txtError.setVisibility(View.GONE);
        mobileAppCategories = new ArrayList<>();
        webAppCategories = new ArrayList<>();
        mobileAppCategories.add(getResources().getString(R.string.filter_hint));
        webAppCategories.add(getResources().getString(R.string.filter_hint));
        btnMobileApps.setVisibility(View.GONE);
        btnMobileApps.setTag(TAG_BTN_MOBILE_APPS);
        btnMobileApps.setOnClickListener(onClickListener);

        btnWebApps.setVisibility(View.GONE);
        btnWebApps.setTag(TAG_BTN_WEB_APPS);
        btnWebApps.setOnClickListener(onClickListener);

        if (CommonUtils.isNetworkAvailable(context)) {
            getAppList();
        } else {
            CommonDialogUtils.showNetworkUnavailableMessage(AppListActivity.this);
        }
    }

    private void getAppList() {
        DialogInterface.OnCancelListener cancelListener = new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
                CommonDialogUtils.getAlertDialogWithOneButtonAndTitle(context,
                                                      getResources().getString(R.string.error_app_fetch_failed_detail),
                                                      getResources().getString(R.string.app_list_failed),
                                                      getResources().getString(R.string.button_ok), null);
            }
        };

        progressDialog =
                CommonDialogUtils.showProgressDialog(context,
                                                     getResources().getString(
                                                             R.string.dialog_app_list),
                                                     getResources().getString(
                                                             R.string.dialog_please_wait),
                                                     cancelListener);

        // Check network connection availability before calling the API.
        if (CommonUtils.isNetworkAvailable(context)) {
            ApplicationManager applicationManager = new ApplicationManager(context);
            if(applicationManager.isPackageInstalled(Constants.AGENT_PACKAGE_NAME)) {
                btnSignOut.setVisibility(View.GONE);
                IntentFilter filter = new IntentFilter(Constants.AGENT_APP_ACTION_RESPONSE);
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                AgentServiceResponseReceiver receiver = new AgentServiceResponseReceiver();
                registerReceiver(receiver, filter);
                CommonUtils.callAgentApp(context, Constants.Operation.GET_APPLICATION_LIST, null, null);
            } else {
                btnSignOut.setVisibility(View.VISIBLE);
                btnSignOut.setTag(TAG_BTN_SIGN_OUT);
                btnSignOut.setOnClickListener(onClickListener);
                getAppListFromServer();
            }
        } else {
            CommonDialogUtils.stopProgressDialog(progressDialog);
            CommonDialogUtils.showNetworkUnavailableMessage(context);
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            int iTag = (Integer) view.getTag();

            switch (iTag) {
                case TAG_BTN_MOBILE_APPS:
                    initiateListView(mobileApps);
                    btnMobileApps.setBackgroundColor(Color.parseColor(ACTIVE_BUTTON_COLOR));
                    btnWebApps.setBackgroundColor(Color.parseColor(INACTIVE_BUTTON_COLOR));
                    initiateCategoryFilter(mobileAppCategories);
                    break;
                case TAG_BTN_WEB_APPS:
                    initiateListView(webApps);
                    btnMobileApps.setBackgroundColor(Color.parseColor(INACTIVE_BUTTON_COLOR));
                    btnWebApps.setBackgroundColor(Color.parseColor(ACTIVE_BUTTON_COLOR));
                    initiateCategoryFilter(webAppCategories);
                    break;
                case TAG_BTN_SIGN_OUT:
                    try {
                        CommonUtils.unRegisterClientApp(context);
                        Preference.clearPreferences(context);
                        Intent intent = new Intent(AppListActivity.this, ServerDetails.class);
                        startActivity(intent);
                        finish();
                    } catch (AppCatalogException e) {
                        Log.e(TAG, "Dynamic client unregistration failed." + e);
                    }
                    break;
            }
        }
    };

    private void initiateCategoryFilter(List<String> categories) {
        if (categories != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(context,
                                                                  android.R.layout.simple_spinner_item, categories);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(dataAdapter);
            spinner.setOnItemSelectedListener(categoryListener);
        }
    }

    /**
     * Retriever application list from the server.
     */
    private void getAppListFromServer() {
        String ipSaved = Preference.getString(context.getApplicationContext(), Constants.PreferenceFlag.IP);

        if (ipSaved != null && !ipSaved.isEmpty()) {
            ServerConfig utils = new ServerConfig();
            utils.setServerIP(ipSaved);
            CommonUtils.callSecuredAPI(AppListActivity.this,
                                       utils.getAPIServerURL(context) + Constants.APP_LIST_ENDPOINT,
                                       HTTP_METHODS.GET, null, AppListActivity.this,
                                       Constants.APP_LIST_REQUEST_CODE
            );
        } else {
            Log.e(TAG, "There is no valid IP to contact the server");
        }
    }

    @Override
    public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
        String responseStatus;
        CommonDialogUtils.stopProgressDialog(progressDialog);
        if (requestCode == Constants.APP_LIST_REQUEST_CODE) {
            if (result != null && result.get(Constants.RESPONSE) != null) {
                responseStatus = result.get(Constants.STATUS);
                if (Constants.Status.SUCCESSFUL.equals(responseStatus)) {
                    try {
                        JSONObject payload = new JSONObject(result.get(Constants.RESPONSE));
                        if (!payload.isNull(Constants.ApplicationPayload.APP_LIST)) {
                            JSONArray applicationList = payload.getJSONArray(Constants.ApplicationPayload.APP_LIST);
                            appList.setVisibility(View.VISIBLE);
                            btnMobileApps.setVisibility(View.VISIBLE);
                            btnWebApps.setVisibility(View.VISIBLE);
                            txtError.setVisibility(View.GONE);
                            setAppListUI(applicationList);
                        }
                    } catch (JSONException e) {
                        appList.setVisibility(View.GONE);
                        btnMobileApps.setVisibility(View.GONE);
                        btnWebApps.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Failed parsing application list response" + e);
                    }
                } else {
                    appList.setVisibility(View.GONE);
                    btnMobileApps.setVisibility(View.GONE);
                    btnWebApps.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);
                }
            } else {
                appList.setVisibility(View.GONE);
                btnMobileApps.setVisibility(View.GONE);
                btnWebApps.setVisibility(View.GONE);
                txtError.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setAppListUI(JSONArray payload) {
        Application application;
        for (int i = 0; i < payload.length(); i++) {
            try {
                application = PayloadParser.parseApplication(payload.getJSONObject(i), context);
                if (Constants.ApplicationPayload.TYPE_WEB_CLIP.equals(application.getAppType().trim())) {
                    webApps.add(application);
                    if (webAppCategories != null && !webAppCategories.contains(application.getCategory())) {
                        webAppCategories.add(application.getCategory());
                    }
                } else {
                    mobileApps.add(application);
                    if (mobileAppCategories != null && !mobileAppCategories.contains(application.getCategory())) {
                        mobileAppCategories.add(application.getCategory());
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Failed parsing application list response" + e);
            }
        }

        if (mobileApps.size() > 0) {
            initiateListView(mobileApps);
            initiateCategoryFilter(mobileAppCategories);
            btnMobileApps.setBackgroundColor(Color.parseColor(ACTIVE_BUTTON_COLOR));
            btnWebApps.setBackgroundColor(Color.parseColor(INACTIVE_BUTTON_COLOR));
        } else if (webApps.size() > 0) {
            initiateListView(webApps);
            initiateCategoryFilter(webAppCategories);
            btnMobileApps.setBackgroundColor(Color.parseColor(INACTIVE_BUTTON_COLOR));
            btnWebApps.setBackgroundColor(Color.parseColor(ACTIVE_BUTTON_COLOR));
        } else {
            appList.setVisibility(View.GONE);
            btnMobileApps.setVisibility(View.GONE);
            btnWebApps.setVisibility(View.GONE);
            txtError.setVisibility(View.VISIBLE);
        }
    }

    private void initiateListView(ArrayList<Application> applications) {
        final ApplicationAdapter appAdapter = new ApplicationAdapter(this, R.layout.app_list_item, applications);
        appList.setAdapter(appAdapter);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, AppDetailsActivity.class);
                Application app = (Application) parent.getItemAtPosition(position);
                intent.putExtra(context.getResources().
                        getString(R.string.intent_extra_application), app);
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                Preference.putBoolean(context, context.getResources().
                        getString(R.string.intent_extra_is_category), false);
                appAdapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        categoryListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Preference.putBoolean(context, context.getResources().
                        getString(R.string.intent_extra_is_category), true);
                if (item.trim().equals(context.getResources().
                        getString(R.string.filter_hint))) {
                    item = context.getResources().
                            getString(R.string.empty_string_character);
                }
                appAdapter.getFilter().filter(item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
    }

    @Override
    public void onBackPressed() {
        loadHomeScreen();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            loadHomeScreen();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            loadHomeScreen();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * Load device home screen.
     */
    private void loadHomeScreen() {
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        this.startActivity(i);
        super.onBackPressed();
    }

    public class AgentServiceResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra(Constants.INTENT_KEY_STATUS);
            Preference.putString(context, context.getResources().getString(R.string.emm_server_url),
                                 intent.getStringExtra(Constants.INTENT_KEY_SERVER));
            CommonDialogUtils.stopProgressDialog(progressDialog);
            if(Constants.Status.SUCCESSFUL.equals(status)) {
                if (intent.hasExtra(Constants.INTENT_KEY_PAYLOAD) && intent.getStringExtra(Constants.
                                                                                       INTENT_KEY_PAYLOAD) != null) {
                    try {
                        JSONObject payload = new JSONObject(intent.getStringExtra(Constants.INTENT_KEY_PAYLOAD));
                        if (!payload.isNull(Constants.ApplicationPayload.APP_LIST)) {
                            JSONArray applicationList = payload.getJSONArray(Constants.ApplicationPayload.APP_LIST);
                            appList.setVisibility(View.VISIBLE);
                            btnMobileApps.setVisibility(View.VISIBLE);
                            btnWebApps.setVisibility(View.VISIBLE);
                            txtError.setVisibility(View.GONE);
                            setAppListUI(applicationList);
                        }
                    } catch (JSONException e) {
                        appList.setVisibility(View.GONE);
                        btnMobileApps.setVisibility(View.GONE);
                        btnWebApps.setVisibility(View.GONE);
                        txtError.setVisibility(View.VISIBLE);
                        Log.e(TAG, "Failed parsing application list response" + e);
                    }
                } else {
                    appList.setVisibility(View.GONE);
                    btnMobileApps.setVisibility(View.GONE);
                    btnWebApps.setVisibility(View.GONE);
                    txtError.setVisibility(View.VISIBLE);
                }
            } else {
                appList.setVisibility(View.GONE);
                btnMobileApps.setVisibility(View.GONE);
                btnWebApps.setVisibility(View.GONE);
                txtError.setVisibility(View.VISIBLE);
            }
        }
    }
}
