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
package org.wso2.emm.agent.services.operationMgt;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AlertActivity;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.LockActivity;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.ServerDetails;
import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.api.DeviceInfo;
import org.wso2.emm.agent.api.GPSTracker;
import org.wso2.emm.agent.api.RuntimeInfo;
import org.wso2.emm.agent.api.WiFiConfig;
import org.wso2.emm.agent.beans.Application;
import org.wso2.emm.agent.beans.ComplianceFeature;
import org.wso2.emm.agent.beans.DeviceAppInfo;
import org.wso2.emm.agent.beans.Notification;
import org.wso2.emm.agent.beans.Operation;
import org.wso2.emm.agent.dao.NotificationDAO;
import org.wso2.emm.agent.proxy.interfaces.APIResultCallBack;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.services.DeviceInfoPayload;
import org.wso2.emm.agent.services.NotificationReceiver;
import org.wso2.emm.agent.services.PolicyComplianceChecker;
import org.wso2.emm.agent.services.PolicyOperationsMapper;
import org.wso2.emm.agent.services.ResultPayload;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public abstract class OperationManager implements APIResultCallBack, VersionBasedOperations {

    private Context context;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName cdmDeviceAdmin;
    private ApplicationManager appList;
    private Resources resources;
    private ResultPayload resultBuilder;
    private DeviceInfo deviceInfo;
    private GPSTracker gps;
    private NotificationDAO notificationDAO;
    private NotificationManager notifyManager;
    private int currentNotificationId;
    private ApplicationManager applicationManager;

    private static final String TAG = "Operation Handler";

    private static final String LOCATION_INFO_TAG_LONGITUDE = "longitude";
    private static final String LOCATION_INFO_TAG_LATITUDE = "latitude";
    private static final String APP_INFO_TAG_NAME = "name";
    private static final String APP_INFO_TAG_PACKAGE = "package";
    private static final String APP_INFO_TAG_VERSION = "version";
    private static final int DEFAULT_PASSWORD_LENGTH = 0;
    private static final int DEFAULT_VOLUME = 0;
    private static final int DEFAULT_FLAG = 0;
    private static final int DEFAULT_PASSWORD_MIN_LENGTH = 4;
    private static final long DAY_MILLISECONDS_MULTIPLIER = 24 * 60 * 60 * 1000;
    private static String[] AUTHORIZED_PINNING_APPS;
    private static String AGENT_PACKAGE_NAME;

    public OperationManager(Context context){
        this.context = context;
        this.resources = context.getResources();
        this.devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.cdmDeviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
        this.appList = new ApplicationManager(context.getApplicationContext());
        this.resultBuilder = new ResultPayload();
        deviceInfo = new DeviceInfo(context.getApplicationContext());
        notificationDAO = new NotificationDAO(context);
        AGENT_PACKAGE_NAME = context.getPackageName();
        AUTHORIZED_PINNING_APPS = new String[]{AGENT_PACKAGE_NAME};
        notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        applicationManager = new ApplicationManager(context);
    }

    /**
     *  Methods used by child classes to retrieve values of private variables in Parent Class
     */

    /* Retrieve context resources. */
    public Resources getContextResources(){
        return resources;
    }

    /* Retrieve resultBuilder. */
    public ResultPayload getResultBuilder(){
        return resultBuilder;
    }

    /* Retrieve devicePolicyManager. */
    public DevicePolicyManager getDevicePolicyManager(){
        return devicePolicyManager;
    }

    /* Retrieve cdmDeviceAdmin */
    public ComponentName getCdmDeviceAdmin(){
        return cdmDeviceAdmin;
    }

    /* Retrieve default password length */
    public int getDefaultPasswordLength(){
        return DEFAULT_PASSWORD_LENGTH;
    }

    /* Retrieve appList */
    public ApplicationManager getAppList(){
        return appList;
    }

    /* Retrieve Default Password Minimum Length */
    public int getDefaultPasswordMinLength(){
        return DEFAULT_PASSWORD_MIN_LENGTH;
    }

    /* Retrieve Day Milliseconds Multiplier */
    public long getDayMillisecondsMultiplier(){
        return DAY_MILLISECONDS_MULTIPLIER;
    }

    /* Retrieve Context */
    public Context getContext(){
        return context;
    }

    /* Retrive applicationManager */
    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }
    /* End of methods used by child classes to access private variables in Parent Class */

    /**
     * Retrieve device information.
     *
     * @param operation - Operation object.
     */
    public void getDeviceInfo(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        DeviceInfoPayload deviceInfoPayload = new DeviceInfoPayload(context);
        deviceInfoPayload.build();
        String replyPayload = deviceInfoPayload.getDeviceInfoPayload();

        operation.setOperationResponse(replyPayload);
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);
    }

    /**
     * Retrieve location device information.
     *
     * @param operation - Operation object.
     */
    public void getLocationInfo(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        gps = new GPSTracker(context.getApplicationContext());
        JSONObject result = new JSONObject();
        if (gps != null) {
            try {
                result.put(Constants.LocationInfo.LATITUDE, gps.getLatitude());
                result.put(Constants.LocationInfo.LONGITUDE, gps.getLongitude());
                result.put(Constants.LocationInfo.CITY, gps.getCity());
                result.put(Constants.LocationInfo.COUNTRY, gps.getCountry());
                result.put(Constants.LocationInfo.STATE, gps.getState());
                result.put(Constants.LocationInfo.STREET1, gps.getStreet1());
                result.put(Constants.LocationInfo.STREET2, gps.getStreet2());
                result.put(Constants.LocationInfo.ZIP, gps.getZip());

                operation.setOperationResponse(result.toString());
                operation.setStatus(resources.getString(R.string.operation_value_completed));
                resultBuilder.build(operation);
                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, "Device location sent");
                }
            } catch (JSONException e) {
                operation.setStatus(resources.getString(R.string.operation_value_error));
                resultBuilder.build(operation);
                throw new AndroidAgentException("Invalid JSON format.", e);
            }
        } else {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Error occurred while initiating location service");
        }
    }

    /**
     * Retrieve device application information.
     *
     * @param operation - Operation object.
     */
    public void getApplicationList(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        ArrayList<DeviceAppInfo> apps = new ArrayList<>(appList.getInstalledApps().values());
        JSONArray result = new JSONArray();
        RuntimeInfo runtimeInfo = new RuntimeInfo(context);
        Map<String, Application> applications = runtimeInfo.getAppMemory();
        for (DeviceAppInfo infoApp : apps) {
            JSONObject app = new JSONObject();
            try {
                Application application = applications.get(infoApp.getPackagename());
                app.put(APP_INFO_TAG_NAME, Uri.encode(infoApp.getAppname()));
                app.put(APP_INFO_TAG_PACKAGE, infoApp.getPackagename());
                app.put(APP_INFO_TAG_VERSION, infoApp.getVersionCode());
                if (application != null) {
                    app.put(Constants.Device.USS, application.getUss());
                }
                result.put(app);
            } catch (JSONException e) {
                operation.setStatus(resources.getString(R.string.operation_value_error));
                resultBuilder.build(operation);
                throw new AndroidAgentException("Invalid JSON format.", e);
            }
        }
        operation.setOperationResponse(result.toString());
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);

        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Application list sent");
        }
    }

    /**
     * Ring the device.
     *
     * @param operation - Operation object.
     */
    public void ringDevice(org.wso2.emm.agent.beans.Operation operation) {
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);
        Intent intent = new Intent(context, AlertActivity.class);
        intent.putExtra(resources.getString(R.string.intent_extra_type),
                resources.getString(R.string.intent_extra_ring));
        intent.putExtra(resources.getString(R.string.intent_extra_message),
                        resources.getString(R.string.intent_extra_stop_ringing));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Ringing is activated on the device");
        }
    }

    /**
     * Configure device WIFI profile.
     *
     * @param operation - Operation object.
     */
    public void configureWifi(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        boolean wifiStatus;
        String ssid = null;
        String password = null;
        JSONObject result = new JSONObject();

        try {
            JSONObject wifiData = new JSONObject(operation.getPayLoad().toString());
            if (!wifiData.isNull(resources.getString(R.string.intent_extra_ssid))) {
                ssid = (String) wifiData.get(resources.getString(R.string.intent_extra_ssid));
            }
            if (!wifiData.isNull(resources.getString(R.string.intent_extra_password))) {
                password = (String) wifiData.get(resources.getString(R.string.intent_extra_password));
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }

        WiFiConfig config = new WiFiConfig(context.getApplicationContext());
        wifiStatus = config.saveWEPConfig(ssid, password);

        try {
            String status;
            if (wifiStatus) {
                status = resources.getString(R.string.shared_pref_default_status);
                result.put(resources.getString(R.string.operation_status), status);
            } else {
                status = resources.getString(R.string.shared_pref_false_status);
                result.put(resources.getString(R.string.operation_status), status);
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Wifi configured");
        }
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        operation.setPayLoad(result.toString());
        resultBuilder.build(operation);
    }

    /**
     * Disable/Enable device camera.
     *
     * @param operation - Operation object.
     */
    public void disableCamera(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        boolean camFunc = operation.isEnabled();
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);
        devicePolicyManager.setCameraDisabled(cdmDeviceAdmin, !camFunc);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Camera enabled: " + camFunc);
        }
    }

    /**
     * Mute the device.
     *
     * @param operation - Operation object.
     */
    public void muteDevice(org.wso2.emm.agent.beans.Operation operation) {
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_RING, DEFAULT_VOLUME, DEFAULT_FLAG);
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Device muted");
        }
    }

    /**
     * Create web clip (Web app shortcut on device home screen).
     *
     * @param operation - Operation object.
     */
    public void manageWebClip(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        String appUrl;
        String title;
        String operationType;

        try {
            JSONObject webClipData = new JSONObject(operation.getPayLoad().toString());
            appUrl = webClipData.getString(resources.getString(R.string.intent_extra_identity));
            title = webClipData.getString(resources.getString(R.string.intent_extra_title));
            operationType = webClipData.getString(resources.getString(R.string.operation_type));
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }

        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);

        if (appUrl != null && title != null) {
            appList.manageWebAppBookmark(appUrl, title, operationType);
        }
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Created webclip");
        }
    }

    /**
     * Open Google Play store application with an application given.
     *
     * @param packageName - Application package name.
     */
    public void triggerGooglePlayApp(String packageName) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(Constants.GOOGLE_PLAY_APP_URI + packageName));
        context.startActivity(intent);
    }

    /**
     * Monitor currently enforced policy for compliance.
     *
     * @param operation - Operation object.
     */
    public void monitorPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        String payload = Preference.getString(context, Constants.PreferenceFlag.APPLIED_POLICY);
        PolicyOperationsMapper operationsMapper = new PolicyOperationsMapper();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        PolicyComplianceChecker policyChecker = new PolicyComplianceChecker(context);
        ArrayList<ComplianceFeature> result = new ArrayList<>();

        try {
            if(payload != null) {
                List<org.wso2.emm.agent.beans.Operation> operations = mapper.readValue(
                        payload,
                        mapper.getTypeFactory().constructCollectionType(List.class,
                                org.wso2.emm.agent.beans.Operation.class));
                for (org.wso2.emm.agent.beans.Operation op : operations) {
                    op = operationsMapper.getOperation(op);
                    result.add(policyChecker.checkPolicyState(op));
                }
                operation.setStatus(resources.getString(R.string.operation_value_completed));
                operation.setPayLoad(mapper.writeValueAsString(result));
                resultBuilder.build(operation);
            }
        } catch (IOException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Error occurred while parsing stream.", e);
        }
    }

    /**
     * Revoke currently enforced policy.
     *
     * @param operation - Operation object.
     */
    public void revokePolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        CommonUtils.revokePolicy(context);
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);
    }

    /**
     * Blacklisting apps.
     *
     * @param operation - Operation object.
     */
    public void blacklistApps(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

    }

    /**
     * Uninstall application.
     *
     * @param operation - Operation object.
     */
    public void uninstallApplication(Operation operation) throws AndroidAgentException {
        String packageName;
        String type;
        try {
            JSONObject appData = new JSONObject(operation.getPayLoad().toString());
            type = appData.getString(getContextResources().getString(R.string.app_type));

            if (getContextResources().getString(R.string.intent_extra_web).equalsIgnoreCase(type)) {
                String appUrl = appData.getString(getContextResources().getString(R.string.app_url));
                String name = appData.getString(getContextResources().getString(R.string.intent_extra_name));
                String operationType = getContextResources().getString(R.string.operation_uninstall);
                JSONObject payload = new JSONObject();
                payload.put(getContextResources().getString(R.string.intent_extra_identity), appUrl);
                payload.put(getContextResources().getString(R.string.intent_extra_title), name);
                payload.put(getContextResources().getString(R.string.operation_type), operationType);
                operation.setPayLoad(payload.toString());
                manageWebClip(operation);
            } else {
                packageName = appData.getString(getContextResources().getString(R.string.app_identifier));
                getAppList().uninstallApplication(packageName);
                operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
                getResultBuilder().build(operation);
            }

            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Application started to uninstall");
            }
        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    /**
     * Reboot the device [System app required].
     *
     * @param operation - Operation object.
     */
    public void rebootDevice(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        JSONObject result = new JSONObject();

        try {
            String status = resources.getString(R.string.shared_pref_default_status);
            result.put(resources.getString(R.string.operation_status), status);
            operation.setPayLoad(result.toString());

            if (status.equals(resources.getString(R.string.shared_pref_default_status))) {
                Toast.makeText(context, resources.getString(R.string.toast_message_reboot),
                        Toast.LENGTH_LONG).show();
                operation.setStatus(resources.getString(R.string.operation_value_completed));
                resultBuilder.build(operation);

                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, "Reboot initiated.");
                }
            } else {
                Toast.makeText(context, resources.getString(R.string.toast_message_reboot_failed),
                        Toast.LENGTH_LONG).show();
                operation.setStatus(resources.getString(R.string.operation_value_error));
                resultBuilder.build(operation);
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    /**
     * Upgrading device firmware from the configured OTA server.
     *
     * @param operation - Operation object.
     */
    public void upgradeFirmware(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        JSONObject result = new JSONObject();
        String schedule = null;

        try {
            JSONObject upgradeData = new JSONObject(operation.getPayLoad().toString());
            if (upgradeData != null && !upgradeData.isNull(resources.getString(R.string.intent_extra_schedule))) {
                schedule = (String) upgradeData.get(resources.getString(R.string.intent_extra_schedule));
                Preference.putString(context, resources.getString(R.string.pref_key_schedule), schedule);
            }

        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }

        try {
            String status = resources.getString(R.string.shared_pref_default_status);
            result.put(resources.getString(R.string.operation_status), status);

            operation.setPayLoad(result.toString());

            if (status.equals(resources.getString(R.string.shared_pref_default_status))) {
                operation.setStatus(resources.getString(R.string.operation_value_completed));
                resultBuilder.build(operation);

                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, "Firmware upgrade started.");
                }
            } else {
                operation.setStatus(resources.getString(R.string.operation_value_error));
                resultBuilder.build(operation);
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    /**
     * Execute shell commands as the super user.
     *
     * @param operation - Operation object.
     */
    public void executeShellCommand(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        JSONObject result = new JSONObject();

        try {
            String status = resources.getString(R.string.shared_pref_default_status);
            result.put(resources.getString(R.string.operation_status), status);

            operation.setPayLoad(result.toString());

            if (status.equals(resources.getString(R.string.shared_pref_default_status))) {
                operation.setStatus(resources.getString(R.string.operation_value_completed));
                resultBuilder.build(operation);

                if (Constants.DEBUG_MODE_ENABLED) {
                    Log.d(TAG, "Shell command received.");
                }
            } else {
                operation.setStatus(resources.getString(R.string.operation_value_error));
                resultBuilder.build(operation);
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    /**
     * This method returns the completed operations list.
     *
     * @return operation list
     */
    public List<org.wso2.emm.agent.beans.Operation> getResultPayload() {
        return resultBuilder.getResultPayload();
    }

    /**
     * This method is used to add notification to the embedded db.
     * @param id notification id (operation id).
     * @param message notification.
     * @param status current status of the notification.
     */
    private void addNotification(int id, String message, Notification.Status status) {
        Notification notification = new Notification();
        notification.setId(id);
        notification.setMessage(message);
        notification.setStatus(status);
        notification.setReceivedTime(Calendar.getInstance().getTime().toString());
        notificationDAO.open();
        if (notificationDAO.getNotification(id) == null) {
            notificationDAO.addNotification(notification);
        }
        notificationDAO.close();
    }

    /**
     * This method checks whether there are any previous notifications which were not sent
     * and send if found any.
     */
    public void checkPreviousNotifications() {
        notificationDAO.open();
        List<Notification> dismissedNotifications = notificationDAO.getAllDismissedNotifications();
        org.wso2.emm.agent.beans.Operation operation;
        for (Notification notification : dismissedNotifications) {
            operation = new org.wso2.emm.agent.beans.Operation();
            operation.setId(notification.getId());
            operation.setCode(Constants.Operation.NOTIFICATION);
            operation.setStatus(resources.getString(R.string.operation_value_completed));
            operation.setOperationResponse("Alert was dismissed: " + notification.getResponseTime());
            resultBuilder.build(operation);
            notificationDAO.updateNotification(notification.getId(), Notification.Status.SENT);
        }
        notificationDAO.close();
    }

    /**
     * Lock the device.
     *
     * @param operation - Operation object.
     */
    public void lockDevice(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);
        JSONObject inputData;
        String message = null;
        boolean isHardLockEnabled = false;
        try {
            if (operation.getPayLoad() != null) {
                inputData = new JSONObject(operation.getPayLoad().toString());
                message = inputData.getString(Constants.ADMIN_MESSAGE);
                isHardLockEnabled = inputData.getBoolean(Constants.IS_HARD_LOCK_ENABLED);
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
        if (isHardLockEnabled) {
            if (message == null || message.isEmpty()) {
                message = resources.getString(R.string.txt_lock_activity);
            }
            Preference.putBoolean(context, Constants.IS_LOCKED, true);
            Preference.putString(context, Constants.LOCK_MESSAGE, message);
            enableHardLock(message);
        } else {
            devicePolicyManager.lockNow();
        }

        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Device locked");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void enableHardLock(String message) {
        if (isDeviceOwner()) {
            devicePolicyManager.setLockTaskPackages(cdmDeviceAdmin, AUTHORIZED_PINNING_APPS);
            Intent intent = new Intent(context, LockActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constants.ADMIN_MESSAGE, message);
            intent.putExtra(Constants.IS_LOCKED, true);
            context.startActivity(intent);
        } else {
            devicePolicyManager.lockNow();
        }
    }

    /**
     * Unlock the device.
     *
     * @param operation - Operabtion object.
     */
    public void unlockDevice(org.wso2.emm.agent.beans.Operation operation) {
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        resultBuilder.build(operation);

        boolean isLocked = Preference.getBoolean(context, Constants.IS_LOCKED);
        if (isLocked) {
            if (isDeviceOwner()) {
                Intent intent = new Intent(context, ServerDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Device unlocked");
        }
    }
    /**
     * This method is used to check whether agent is registered as the device owner.
     *
     * @return true if agent is the device owner.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private boolean isDeviceOwner() {
        return devicePolicyManager.isDeviceOwnerApp(AGENT_PACKAGE_NAME);
    }

    /**
     * Install google play applications.
     *
     * @param operation - Operation object.
     */
    public void installGooglePlayApp(Operation operation) throws AndroidAgentException {
        String packageName;
        try {
            JSONObject appData = new JSONObject(operation.getPayLoad().toString());
            packageName = (String) appData.get(getContextResources().getString(R.string.intent_extra_package));

        } catch (JSONException e) {
            operation.setStatus(getContextResources().getString(R.string.operation_value_error));
            getResultBuilder().build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }

        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Started installing GoogleApp");
        }

        operation.setStatus(getContextResources().getString(R.string.operation_value_completed));
        getResultBuilder().build(operation);

        triggerGooglePlayApp(packageName);
    }

    /**
     * This method is used to post a notification in the device.
     *
     * @param operationId id of the calling notification operation.
     * @param message message to be displayed
     */
    private void initNotification(int operationId, String message) {

        Intent notification = new Intent(context, NotificationReceiver.class);
        notification.putExtra(Constants.OPERATION_ID, operationId);
        PendingIntent dismiss = PendingIntent.getBroadcast(context, operationId, notification, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(resources.getString(R.string.txt_notification))
                        .setContentText(message)
                        .setPriority(android.app.Notification.PRIORITY_MAX)
                        .setDefaults(android.app.Notification.DEFAULT_VIBRATE)
                        .setDefaults(android.app.Notification.DEFAULT_SOUND)
                        .setCategory(android.app.Notification.CATEGORY_CALL)
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .setTicker(resources.getString(R.string.txt_notification))
                        .addAction(R.drawable.abs__ic_clear, "Dismiss", dismiss);

        notifyManager.notify(operationId, mBuilder.build());
    }
    /**
     * Display notification.
     *
     * @param operation - Operation object.
     */
    public void displayNotification(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        try {

            operation.setStatus(resources.getString(R.string.operation_value_progress));
            boolean isLocked = Preference.getBoolean(context, Constants.IS_LOCKED);
            if (isLocked) {
                operation.setOperationResponse("Alert is received to locked device: " +
                        Calendar.getInstance().getTime().toString());
            } else {
                operation.setOperationResponse("Alert is received: " +
                        Calendar.getInstance().getTime().toString());
            }

            resultBuilder.build(operation);
            JSONObject inputData = new JSONObject(operation.getPayLoad().toString());
            String message = inputData.getString(resources.getString(R.string.intent_extra_message));

            if (message != null && !message.isEmpty()) {
                addNotification(operation.getId(), message, Notification.Status.PENDING); //adding notification to the db
                if (deviceInfo.getSdkVersion() >= Build.VERSION_CODES.LOLLIPOP) {
                    initNotification(operation.getId(), message);
                } else {
                    Intent intent = new Intent(context, AlertActivity.class);
                    intent.putExtra(resources.getString(R.string.intent_extra_message), message);
                    intent.putExtra(resources.getString(R.string.intent_extra_operation_id), operation.getId());
                    intent.putExtra(resources.getString(R.string.intent_extra_type),
                            resources.getString(R.string.intent_extra_alert));
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(intent);

                }
            }
            if (Constants.DEBUG_MODE_ENABLED) {
                Log.d(TAG, "Notification received");
            }
        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }

    /**
     * Configure device VPN profile.
     *
     * @param operation - Operation object.
     */
    public void configureVPN(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        String serverAddress = null;
        JSONObject result = new JSONObject();

        try {
            JSONObject vpnData = new JSONObject(operation.getPayLoad().toString());
            if (!vpnData.isNull(resources.getString(R.string.intent_extra_server))) {
                serverAddress = (String) vpnData.get(resources.getString(R.string.intent_extra_server));
            }

        } catch (JSONException e) {
            operation.setStatus(resources.getString(R.string.operation_value_error));
            resultBuilder.build(operation);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }

        if(serverAddress != null) {
            Intent intent = new Intent(context, AlertActivity.class);
            intent.putExtra(resources.getString(R.string.intent_extra_message), resources.getString(R.string.toast_message_vpn));
            intent.putExtra(resources.getString(R.string.intent_extra_operation_id), operation.getId());
            intent.putExtra(resources.getString(R.string.intent_extra_payload), operation.getPayLoad().toString());
            intent.putExtra(resources.getString(R.string.intent_extra_type),
                            Constants.Operation.VPN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }

        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "VPN configured");
        }
        operation.setStatus(resources.getString(R.string.operation_value_completed));
        operation.setPayLoad(result.toString());
        resultBuilder.build(operation);
    }

    @Override
    public void passOperationToSystemApp(Operation operation) throws AndroidAgentException {
        if(getApplicationManager().isPackageInstalled(Constants.SERVICE_PACKAGE_NAME)) {
            CommonUtils.callSystemApp(getContext(),operation.getCode(),
                                      Boolean.toString(operation.isEnabled()), null);
        } else {
            Log.e(TAG, "Invalid operation code received");
        }
    }

    /**
     * This method is being invoked when get info operation get executed.
     *
     * @param result response result
     * @param requestCode code of the requested operation
     */
    @Override
    public void onReceiveAPIResult(Map<String, String> result, int requestCode) {
        String responseStatus;
        String response;
        if (requestCode == Constants.DEVICE_INFO_REQUEST_CODE) {
            if (result != null) {
                responseStatus = result.get(Constants.STATUS_KEY);
                if (Constants.Status.SUCCESSFUL.equals(responseStatus)) {
                    response = result.get(Constants.RESPONSE);
                    if (response != null && !response.isEmpty()) {
                        if (Constants.DEBUG_MODE_ENABLED) {
                            Log.d(TAG, "onReceiveAPIResult." + response);
                            Log.d(TAG, "Device information sent");
                        }
                    }
                }
            }
        }
    }

}
