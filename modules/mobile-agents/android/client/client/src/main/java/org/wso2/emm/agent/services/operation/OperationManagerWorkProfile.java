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
package org.wso2.emm.agent.services.operation;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.beans.AppRestriction;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.services.AppLockService;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class OperationManagerWorkProfile extends OperationManager {

    private static final String TAG = OperationManagerWorkProfile.class.getSimpleName();


    public OperationManagerWorkProfile(Context context) {
        super(context);
    }

    @Override
    public void wipeDevice(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_error));
        operation.setOperationResponse("Operation not supported.");
        getResultBuilder().build(operation);
        Log.d(TAG, "Operation not supported.");
    }

    @Override
    public void clearPassword(Operation operation) {
        operation.setStatus(getContextResources().getString(R.string.operation_value_error));
        operation.setOperationResponse("Operation not supported.");
        getResultBuilder().build(operation);
        Log.d(TAG, "Operation not supported.");
    }

    @Override
    public void installAppBundle(Operation operation) throws AndroidAgentException {
        try {
            if (operation.getCode().equals(Constants.Operation.INSTALL_APPLICATION)||
                    operation.getCode().equals(Constants.Operation.UPDATE_APPLICATION)) {
                JSONObject appData = new JSONObject(operation.getPayLoad().toString());
                installApplication(appData, operation);
            } else if (operation.getCode().equals(Constants.Operation.INSTALL_APPLICATION_BUNDLE)) {
                JSONArray jArray;
                jArray = new JSONArray(operation.getPayLoad().toString());
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject appObj = jArray.getJSONObject(i);
                    installApplication(appObj, operation);
                }
            }
            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Application bundle installation triggered.");
            }

        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing APPLICATION payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    private void installApplication(JSONObject data, org.wso2.emm.agent.beans.Operation operation)
            throws AndroidAgentException {
        String appUrl;
        String type;
        String name;
        String operationType;
        String schedule = null;

        try {
            if (!data.isNull(getContextResources().getString(R.string.app_type))) {
                type = data.getString(getContextResources().getString(R.string.app_type));

                if (type.equalsIgnoreCase(getContextResources().getString(R.string.intent_extra_enterprise))) {
                    appUrl = data.getString(getContextResources().getString(R.string.app_url));
                    if(data.has(getContextResources().getString(R.string.app_schedule))){
                        schedule = data.getString(getContextResources().getString(R.string.app_schedule));
                    }
                    operation.setStatus(getContextResources().getString(R.string.operation_value_progress));
                    getResultBuilder().build(operation);
                    getAppList().installApp(appUrl, schedule, operation);

                } else if (type.equalsIgnoreCase(getContextResources().getString(R.string.intent_extra_public))) {
                    appUrl = data.getString(getContextResources().getString(R.string.app_identifier));
                    operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
                    getResultBuilder().build(operation);
                    Preference.putInt(getContext(), getContext().getResources().getString(R.string.app_install_id),
                            operation.getId());
                    Preference.putString(getContext(), getContext().getResources().getString(R.string.app_install_code),
                            operation.getCode());
                    triggerGooglePlayApp(appUrl);

                } else if (type.equalsIgnoreCase(getContextResources().getString(R.string.intent_extra_web))) {
                    name = data.getString(getContextResources().getString(R.string.intent_extra_name));
                    appUrl = data.getString(getContextResources().getString(R.string.app_url));
                    operationType = getContextResources().getString(R.string.operation_install);
                    JSONObject payload = new JSONObject();
                    payload.put(getContextResources().getString(R.string.intent_extra_identity), appUrl);
                    payload.put(getContextResources().getString(R.string.intent_extra_title), name);
                    payload.put(getContextResources().getString(R.string.operation_type), operationType);
                    operation.setPayLoad(payload.toString());
                    manageWebClip(operation);

                } else {
                    operation.setStatus(getContextResources().getString(R.string.operation_value_error));
                    operation.setOperationResponse("Invalid application details provided.");
                    getResultBuilder().build(operation);
                    throw new AndroidAgentException("Invalid application details");
                }

                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, "Application installation triggered.");
                }
            }
        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing APPLICATION payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    @Override
    public void encryptStorage(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        operation.setOperationResponse("Device already encrypted.");
        getResultBuilder().build(operation);
        Log.d(TAG, "Already encrypted.");
    }

    @Override
    public void setPasswordPolicy(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_error));
        operation.setOperationResponse("Operation not supported.");
        getResultBuilder().build(operation);
    }

    @Override
    public void changeLockCode(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_error));
        operation.setOperationResponse("Operation not supported.");
        getResultBuilder().build(operation);
        Log.d(TAG, "Operation not supported.");
    }

    @Override
    public void enterpriseWipe(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        getResultBuilder().build(operation);
        getDevicePolicyManager().wipeData(0);
    }

    @Override
    public void disenrollDevice(Operation operation) {
        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        getDevicePolicyManager().wipeData(0);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void hideApp(Operation operation) throws AndroidAgentException {
        String packageName = null;
        try {
            JSONObject hideAppData = new JSONObject(operation.getPayLoad().toString());
            if (!hideAppData.isNull(getContextResources().getString(R.string.intent_extra_package))) {
                packageName = (String) hideAppData.get(getContextResources().getString(R.string.intent_extra_package));
            }

            operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
            getResultBuilder().build(operation);

            if (packageName != null && !packageName.isEmpty()) {
                getDevicePolicyManager().setApplicationHidden(getCdmDeviceAdmin(), packageName, true);
            }

            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "App-Hide successful.");
            }
        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing APP_HIDE payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void unhideApp(Operation operation) throws AndroidAgentException {
        String packageName = null;
        try {
            JSONObject hideAppData = new JSONObject(operation.getPayLoad().toString());
            if (!hideAppData.isNull(getContextResources().getString(R.string.intent_extra_package))) {
                packageName = (String) hideAppData.get(getContextResources().getString(R.string.intent_extra_package));
            }

            operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
            getResultBuilder().build(operation);

            if (packageName != null && !packageName.isEmpty()) {
                getDevicePolicyManager().setApplicationHidden(getCdmDeviceAdmin(), packageName, false);
            }

            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "App-unhide successful.");
            }
        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing APP_UN_HIDE payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void blockUninstallByPackageName(Operation operation) throws AndroidAgentException {
        String packageName = null;
        try {
            JSONObject hideAppData = new JSONObject(operation.getPayLoad().toString());
            if (!hideAppData.isNull(getContextResources().getString(R.string.intent_extra_package))) {
                packageName = (String) hideAppData.get(getContextResources().getString(R.string.intent_extra_package));
            }

            operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
            getResultBuilder().build(operation);

            if (packageName != null && !packageName.isEmpty()) {
                getDevicePolicyManager().setUninstallBlocked(getCdmDeviceAdmin(), packageName, true);
            }

            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "App-Uninstall-Block successful.");
            }
        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing APP_BLOCK payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setProfileName(Operation operation) throws AndroidAgentException {
        String profileName = null;
        try {
            JSONObject setProfileNameData = new JSONObject(operation.getPayLoad().toString());
            if (!setProfileNameData.isNull(getContextResources().getString(R.string.intent_extra_profile_name))) {
                profileName = (String) setProfileNameData.get(getContextResources().getString(
                        R.string.intent_extra_profile_name));
            }

            operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
            getResultBuilder().build(operation);

            if (profileName != null && !profileName.isEmpty()) {
                getDevicePolicyManager().setProfileName(getCdmDeviceAdmin(), profileName);
            }

            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Profile Name is set");
            }
        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing PROFILE payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void handleUserRestriction(Operation operation) {
        boolean isEnable = operation.isEnabled();
        String key = operation.getCode();
        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        getResultBuilder().build(operation);
        if (isEnable) {
            getDevicePolicyManager().addUserRestriction(getCdmDeviceAdmin(), key);
            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Restriction added: " + key);
            }
        } else {
            getDevicePolicyManager().clearUserRestriction(getCdmDeviceAdmin(), key);
            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Restriction cleared: " + key);
            }
        }
    }

    @Override
    public void configureWorkProfile(Operation operation) throws AndroidAgentException {
        String profileName;
        String enableSystemAppsData;
        String hideSystemAppsData;
        String unhideSystemAppsData;
        String googlePlayAppsData;
        try {
            JSONObject profileData = new JSONObject(operation.getPayLoad().toString());
            if (!profileData.isNull(getContextResources().getString(R.string.intent_extra_profile_name))) {
                profileName = (String) profileData.get(getContextResources().getString(
                        R.string.intent_extra_profile_name));
                changeProfileName(profileName);
            }
            if (!profileData.isNull(getContextResources().getString(R.string.intent_extra_enable_system_apps))) {
                // generate the System app list which are configured by user and received to agent as a single String
                // with packages separated by Commas.
                enableSystemAppsData = (String) profileData.get(getContextResources().getString(
                        R.string.intent_extra_enable_system_apps));
                List<String> systemAppList = Arrays.asList(enableSystemAppsData.split(getContextResources().getString(
                        R.string.split_delimiter)));
                for (String packageName : systemAppList) {
                    enableSystemApp(packageName);
                }
            }
            if (!profileData.isNull(getContextResources().getString(R.string.intent_extra_hide_system_apps))) {
                // generate the System app list which are configured by user and received to agent as a single String
                // with packages separated by Commas.
                hideSystemAppsData = (String) profileData.get(getContextResources().getString(
                        R.string.intent_extra_hide_system_apps));
                List<String> systemAppList = Arrays.asList(hideSystemAppsData.split(getContextResources().getString(
                        R.string.split_delimiter)));
                for (String packageName : systemAppList) {
                    hideSystemApp(packageName);
                }
            }
            if (!profileData.isNull(getContextResources().getString(R.string.intent_extra_unhide_system_apps))) {
                // generate the System app list which are configured by user and received to agent as a single String
                // with packages separated by Commas.
                unhideSystemAppsData = (String) profileData.get(getContextResources().getString(
                        R.string.intent_extra_unhide_system_apps));
                List<String> systemAppList = Arrays.asList(unhideSystemAppsData.split(getContextResources().getString(
                        R.string.split_delimiter)));
                for (String packageName : systemAppList) {
                    enableSystemApp(packageName);
                }
            }
            if (!profileData.isNull(getContextResources().getString(R.string.intent_extra_enable_google_play_apps))) {
                googlePlayAppsData = (String) profileData.get(getContextResources().getString(
                        R.string.intent_extra_enable_google_play_apps));
                List<String> systemAppList = Arrays.asList(googlePlayAppsData.split(getContextResources().getString(
                        R.string.split_delimiter)));
                for (String packageName : systemAppList) {
                    enableGooglePlayApps(packageName);
                }
            }

        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            operation.setOperationResponse("Error in parsing WORK_PROFILE payload.");
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }

    }

    @Override
    public void passOperationToSystemApp(Operation operation) throws AndroidAgentException {
        operation.setStatus(getContextResources().getString(R.string.operation_value_error));
        operation.setOperationResponse("Operation not supported.");
        getResultBuilder().build(operation);
        Log.d(TAG, "Operation not supported.");
    }

    @Override
    public void restrictAccessToApplications(Operation operation) throws AndroidAgentException {
        AppRestriction appRestriction = CommonUtils.getAppRestrictionTypeAndList(operation, getResultBuilder(), getContextResources());

        if (Constants.AppRestriction.BLACK_LIST.equals(appRestriction.getRestrictionType())) {
            Intent restrictionIntent = new Intent(getContext(), AppLockService.class);
            restrictionIntent.setAction(Constants.APP_LOCK_SERVICE);

            restrictionIntent.putStringArrayListExtra(Constants.AppRestriction.APP_LIST, (ArrayList) appRestriction.getRestrictedList());

            PendingIntent pendingIntent = PendingIntent.getService(getContext(), 0, restrictionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 1); // First time
            long frequency = 1 * 1000; // In ms
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);

            getContext().startService(restrictionIntent);

        }
        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        getResultBuilder().build(operation);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void changeProfileName(String name) {
        getDevicePolicyManager().setProfileName(getCdmDeviceAdmin(), name);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enableSystemApp(String packageName) {
        getDevicePolicyManager().enableSystemApp(getCdmDeviceAdmin(), packageName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideSystemApp(String packageName ) {
        getDevicePolicyManager().setApplicationHidden(getCdmDeviceAdmin(), packageName, true);
    }

    @Override
    public void setPolicyBundle(Operation operation) throws AndroidAgentException {
        getResultBuilder().build(operation);
    }

    private void enableGooglePlayApps(String packageName) {
        triggerGooglePlayApp(packageName);
    }

}