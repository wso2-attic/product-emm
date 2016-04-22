/*
 *
 *  Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.wso2.emm.agent.services;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import org.wso2.emm.agent.AndroidAgentException;
import org.wso2.emm.agent.R;
import org.wso2.emm.agent.api.ApplicationManager;
import org.wso2.emm.agent.api.WiFiConfig;
import org.wso2.emm.agent.beans.ComplianceFeature;
import org.wso2.emm.agent.beans.DeviceAppInfo;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class is used to check device policy compliance by checking each policy
 * with current device status.
 */
public class PolicyComplianceChecker {

    private static final String TAG = PolicyOperationsMapper.class.getSimpleName();
    private Context context;
    private DevicePolicyManager devicePolicyManager;
    private Resources resources;
    private ComponentName deviceAdmin;
    private ComplianceFeature policy;
    private ApplicationManager appList;

    public PolicyComplianceChecker(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.deviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
        this.appList = new ApplicationManager(context.getApplicationContext());
    }

    /**
     * Checks EMM policy on the device.
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    public ComplianceFeature checkPolicyState(org.wso2.emm.agent.beans.Operation operation)
            throws AndroidAgentException {

        policy = new ComplianceFeature();
        policy.setFeatureCode(operation.getCode());

        switch (operation.getCode()) {

            case Constants.Operation.CAMERA:
                return checkCameraPolicy(operation);
            case Constants.Operation.INSTALL_APPLICATION:
                return checkInstallAppPolicy(operation);
            case Constants.Operation.UNINSTALL_APPLICATION:
                return checkUninstallAppPolicy(operation);
            case Constants.Operation.ENCRYPT_STORAGE:
                return checkEncryptPolicy(operation);
            case Constants.Operation.PASSCODE_POLICY:
                return checkPasswordPolicy();
            case Constants.Operation.WIFI:
                return checkWifiPolicy(operation);
            case Constants.Operation.WORK_PROFILE:
                return checkWorkProfilePolicy(operation);
            case Constants.Operation.DISALLOW_ADJUST_VOLUME:
            case Constants.Operation.DISALLOW_CONFIG_BLUETOOTH:
            case Constants.Operation.DISALLOW_CONFIG_CELL_BROADCASTS:
            case Constants.Operation.DISALLOW_CONFIG_CREDENTIALS:
            case Constants.Operation.DISALLOW_CONFIG_MOBILE_NETWORKS:
            case Constants.Operation.DISALLOW_CONFIG_TETHERING:
            case Constants.Operation.DISALLOW_CONFIG_VPN:
            case Constants.Operation.DISALLOW_CONFIG_WIFI:
            case Constants.Operation.DISALLOW_APPS_CONTROL:
            case Constants.Operation.DISALLOW_CREATE_WINDOWS:
            case Constants.Operation.DISALLOW_CROSS_PROFILE_COPY_PASTE:
            case Constants.Operation.DISALLOW_DEBUGGING_FEATURES:;
            case Constants.Operation.DISALLOW_FACTORY_RESET:
            case Constants.Operation.DISALLOW_ADD_USER:
            case Constants.Operation.DISALLOW_INSTALL_APPS:
            case Constants.Operation.DISALLOW_INSTALL_UNKNOWN_SOURCES:
            case Constants.Operation.DISALLOW_MODIFY_ACCOUNTS:
            case Constants.Operation.DISALLOW_MOUNT_PHYSICAL_MEDIA:
            case Constants.Operation.DISALLOW_NETWORK_RESET:
            case Constants.Operation.DISALLOW_OUTGOING_BEAM:
            case Constants.Operation.DISALLOW_OUTGOING_CALLS:
            case Constants.Operation.DISALLOW_REMOVE_USER:
            case Constants.Operation.DISALLOW_SAFE_BOOT:
            case Constants.Operation.DISALLOW_SHARE_LOCATION:
            case Constants.Operation.DISALLOW_SMS:
            case Constants.Operation.DISALLOW_UNINSTALL_APPS:
            case Constants.Operation.DISALLOW_UNMUTE_MICROPHONE:
            case Constants.Operation.DISALLOW_USB_FILE_TRANSFER:
            case Constants.Operation.ALLOW_PARENT_PROFILE_APP_LINKING:
            case Constants.Operation.ENSURE_VERIFY_APPS:
            case Constants.Operation.AUTO_TIME:
            case Constants.Operation.ENABLE_ADMIN:
            case Constants.Operation.SET_SCREEN_CAPTURE_DISABLED:
            case Constants.Operation.SET_STATUS_BAR_DISABLED:
                if(appList.isPackageInstalled(Constants.SERVICE_PACKAGE_NAME)) {
                    CommonUtils.callSystemApp(context,operation.getCode(),
                                              Boolean.toString(operation.isEnabled()), null);
                    // Since without rooting the device a policy set by the device owner cannot
                    // be violated or overridden, no compliance check is necessary.
                    policy.setCompliance(true);
                    return policy;
                } else {
                    throw new AndroidAgentException("Invalid operation code received");
                }
            default:
                throw new AndroidAgentException("Invalid operation code received");
        }
    }

    /**
     * Checks camera policy on the device (camera enabled/disabled).
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkCameraPolicy(org.wso2.emm.agent.beans.Operation operation) {

        boolean cameraStatus = devicePolicyManager.getCameraDisabled(deviceAdmin);

        if ((operation.isEnabled() && !cameraStatus) || (!operation.isEnabled() && cameraStatus)) {
            policy.setCompliance(true);
        } else {
            policy.setCompliance(false);
        }

        return policy;
    }

    /**
     * Checks install app policy on the device (Particular app in the policy should be installed).
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkInstallAppPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

        String appIdentifier=null;
        String name=null;

        try {
            JSONObject appData = new JSONObject(operation.getPayLoad().toString());

            if (!appData.isNull(resources.getString(R.string.app_identifier))) {
                appIdentifier = appData.getString(resources.getString(R.string.app_identifier));
            }

            if (!appData.isNull(resources.getString(R.string.app_identifier))) {
                name = appData.getString(resources.getString(R.string.intent_extra_name));
            }

            if(isAppInstalled(appIdentifier)){
                policy.setCompliance(true);
            }else{
                policy.setCompliance(false);
                policy.setMessage(resources.getString(R.string.error_app_install_policy)+name);
            }

        } catch (JSONException e) {
            policy.setCompliance(false);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
        return policy;
    }

    /**
     * Checks uninstall app policy on the device (Particular app in the policy should be removed).
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkUninstallAppPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

        String appIdentifier=null;
        String name=null;

        try {
            JSONObject appData = new JSONObject(operation.getPayLoad().toString());

            if (!appData.isNull(resources.getString(R.string.app_identifier))) {
                appIdentifier = appData.getString(resources.getString(R.string.app_identifier));
            }

            if (!appData.isNull(resources.getString(R.string.app_identifier))) {
                name = appData.getString(resources.getString(R.string.intent_extra_name));
            }

            if(!isAppInstalled(appIdentifier)){
                policy.setCompliance(true);
            }else{
                policy.setCompliance(false);
                policy.setMessage(resources.getString(R.string.error_app_uninstall_policy)+name);
            }

        } catch (JSONException e) {
            policy.setCompliance(false);
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
        return policy;
    }

    /**
     * Checks if the app is already installed on the device.
     *
     * @param appIdentifier - App package name.
     * @return appInstalled - App installed status.
     */
    private boolean isAppInstalled(String appIdentifier){
        boolean appInstalled=false;
        ArrayList<DeviceAppInfo> apps = new ArrayList<>(appList.getInstalledApps().values());
        for (DeviceAppInfo appInfo : apps) {
            if(appIdentifier.trim().equals(appInfo.getPackagename())){
                appInstalled = true;
            }
        }

        return  appInstalled;
    }

    /**
     * Checks device encrypt policy on the device (Device external storage encryption).
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkEncryptPolicy(org.wso2.emm.agent.beans.Operation operation) {

        boolean encryptStatus = (devicePolicyManager.getStorageEncryptionStatus()!= devicePolicyManager.
                ENCRYPTION_STATUS_UNSUPPORTED && devicePolicyManager.getStorageEncryptionStatus() != devicePolicyManager.
                ENCRYPTION_STATUS_INACTIVE);

        if ((operation.isEnabled() && encryptStatus) || (!operation.isEnabled() && !encryptStatus)) {
            policy.setCompliance(true);
        } else {
            policy.setCompliance(false);
            policy.setMessage(resources.getString(R.string.error_encrypt_policy));
        }

        return policy;
    }

    /**
     * Checks screen lock password policy on the device.
     *
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkPasswordPolicy() {

        if(devicePolicyManager.isActivePasswordSufficient()){
            policy.setCompliance(true);
        }else{
            policy.setCompliance(false);
        }

        return policy;
    }

    /**
     * Checks Wifi policy on the device (Particular wifi configuration in the policy should be enforced).
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkWifiPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        String ssid = null;

        try {
            JSONObject wifiData = new JSONObject(operation.getPayLoad().toString());
            if (!wifiData.isNull(resources.getString(R.string.intent_extra_ssid))) {
                ssid = (String) wifiData.get(resources.getString(R.string.intent_extra_ssid));
            }

            WiFiConfig config = new WiFiConfig(context.getApplicationContext());
            if(config.findWifiConfigurationBySsid(ssid)){
                policy.setCompliance(true);
            }else{
                policy.setCompliance(false);
                policy.setMessage(resources.getString(R.string.error_wifi_policy));
            }
        } catch (JSONException e) {
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
        return policy;
    }

    /**
     * Checks Work-Profile policy on the device (Particular work-profile configuration in the policy should be enforced).
     *
     * @param operation - Operation object.
     * @return policy - ComplianceFeature object.
     */
    private ComplianceFeature checkWorkProfilePolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        String profileName;
        String systemAppsData;
        String googlePlayAppsData;
        try {
            JSONObject profileData = new JSONObject(operation.getPayLoad().toString());
            if (!profileData.isNull(resources.getString(R.string.intent_extra_profile_name))) {
                profileName = (String) profileData.get(resources.getString(
                        R.string.intent_extra_profile_name));
                    //yet there is no method is given to get the current profile name.
                    //add a method to test whether profile name is set correctly once introduced.
            }
            if (!profileData.isNull(resources.getString(R.string.intent_extra_enable_system_apps))) {
                // generate the System app list which are configured by user and received to agent as a single String
                // with packages separated by Commas.
                systemAppsData = (String) profileData.get(resources.getString(
                        R.string.intent_extra_enable_system_apps));
                List<String> systemAppList = Arrays.asList(systemAppsData.split(resources.getString(
                        R.string.split_delimiter)));
                for (String packageName : systemAppList) {
                    if(!appList.isPackageInstalled(packageName)){
                        policy.setCompliance(false);
                        policy.setMessage(resources.getString(R.string.error_work_profile_policy));
                        return policy;
                    }
                }
            }
            if (!profileData.isNull(resources.getString(R.string.intent_extra_enable_google_play_apps))) {
                googlePlayAppsData = (String) profileData.get(resources.getString(
                        R.string.intent_extra_enable_google_play_apps));
                List<String> playStoreAppList = Arrays.asList(googlePlayAppsData.split(resources.getString(
                        R.string.split_delimiter)));
                for (String packageName : playStoreAppList) {
                    if(!appList.isPackageInstalled(packageName)){
                        policy.setCompliance(false);
                        policy.setMessage(resources.getString(R.string.error_work_profile_policy));
                        return policy;
                    }
                }
            }
        } catch (JSONException e) {
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
        policy.setCompliance(true);
        return policy;
    }
}
