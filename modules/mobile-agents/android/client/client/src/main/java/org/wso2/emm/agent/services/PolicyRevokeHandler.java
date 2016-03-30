/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.wso2.emm.agent.beans.DeviceAppInfo;
import org.wso2.emm.agent.utils.CommonUtils;
import org.wso2.emm.agent.utils.Constants;

import java.util.ArrayList;

/**
 * This class is used to revoke the existing policy on the device.
 */
public class PolicyRevokeHandler {

    private static final String TAG = PolicyOperationsMapper.class.getSimpleName();
    private Context context;
    private DevicePolicyManager devicePolicyManager;
    private Resources resources;
    private ComponentName deviceAdmin;
    private ApplicationManager appList;

    public PolicyRevokeHandler(Context context){
        this.context = context;
        this.resources = context.getResources();
        this.devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        this.deviceAdmin = new ComponentName(context, AgentDeviceAdminReceiver.class);
        this.appList = new ApplicationManager(context.getApplicationContext());
    }

    /**
     * Revokes EMM policy on the device.
     *
     * @param operation - Operation object.
     * @return status - Revoke status.
     */
    public void revokeExistingPolicy(org.wso2.emm.agent.beans.Operation operation)
            throws AndroidAgentException {

        switch (operation.getCode()) {

            case Constants.Operation.CAMERA:
                revokeCameraPolicy(operation);
                break;
            case Constants.Operation.INSTALL_APPLICATION:
                revokeInstallAppPolicy(operation);
                break;
            case Constants.Operation.ENCRYPT_STORAGE:
                revokeEncryptPolicy(operation);
                break;
            case Constants.Operation.PASSCODE_POLICY:
                revokePasswordPolicy();
                break;
            case Constants.Operation.WIFI:
                revokeWifiPolicy(operation);
                break;
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
                    CommonUtils.callSystemApp(context, operation.getCode(),
                                              Boolean.toString(false), null);
                } else {
                    throw new AndroidAgentException("Invalid operation code received");
                }
                break;
            default:
                throw new AndroidAgentException("Invalid operation code received");
        }
    }

    /**
     * Revokes camera policy on the device.
     *
     * @param operation - Operation object.
     */
    private void revokeCameraPolicy(org.wso2.emm.agent.beans.Operation operation) {
        if(!operation.isEnabled()){
            devicePolicyManager.setCameraDisabled(deviceAdmin, false);
        }
    }

    /**
     * Revokes install app policy on the device (Particular app in the policy should be removed).
     *
     * @param operation - Operation object.
     */
    private void revokeInstallAppPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {

        String appIdentifier=null;

        try {
            JSONObject appData = new JSONObject(operation.getPayLoad().toString());

            if (!appData.isNull(resources.getString(R.string.app_identifier))) {
                appIdentifier = appData.getString(resources.getString(R.string.app_identifier));
            }

            if(isAppInstalled(appIdentifier)){
                appList.uninstallApplication(appIdentifier);
            }

        } catch (JSONException e) {
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
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
     * Revokes device encrypt policy on the device (Device external storage encryption).
     *
     * @param operation - Operation object.
     */
    private void revokeEncryptPolicy(org.wso2.emm.agent.beans.Operation operation) {

        boolean encryptStatus = (devicePolicyManager.getStorageEncryptionStatus()!= devicePolicyManager.
                ENCRYPTION_STATUS_UNSUPPORTED && (devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.
                ENCRYPTION_STATUS_ACTIVE || devicePolicyManager.getStorageEncryptionStatus() == devicePolicyManager.
                ENCRYPTION_STATUS_ACTIVATING));

        if (operation.isEnabled() && encryptStatus) {
            devicePolicyManager.setStorageEncryption(deviceAdmin, false);
        }

    }

    /**
     * Revokes screen lock password policy on the device.
     */
    private void revokePasswordPolicy() {
        devicePolicyManager.setPasswordQuality(deviceAdmin,
                                               DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
    }

    /**
     * Revokes Wifi policy on the device (Particular wifi configuration in the policy should be disabled).
     *
     * @param operation - Operation object.
     */
    private void revokeWifiPolicy(org.wso2.emm.agent.beans.Operation operation) throws AndroidAgentException {
        String ssid = null;

        try {
            JSONObject wifiData = new JSONObject(operation.getPayLoad().toString());
            if (!wifiData.isNull(resources.getString(R.string.intent_extra_ssid))) {
                ssid = (String) wifiData.get(resources.getString(R.string.intent_extra_ssid));
            }

            WiFiConfig config = new WiFiConfig(context.getApplicationContext());
            if(config.findWifiConfigurationBySsid(ssid)){
                config.removeWifiConfigurationBySsid(ssid);
            }
        } catch (JSONException e) {
            throw new AndroidAgentException("Invalid JSON format.", e);
        }
    }
}
