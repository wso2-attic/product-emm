/**
 * Created by Chatura Dilan Perera on 12/10/2016.
 */
package org.wso2.emm.agent.events.listeners;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;

import static android.app.admin.DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED;

public class AppInstallationListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName cdmDeviceAdmin = AgentDeviceAdminReceiver.getComponentName(context);

        String packageName =  intent.getStringExtra("packageName");



        devicePolicyManager.setLockTaskPackages(cdmDeviceAdmin, new String[]{"org.wso2.emm.agent", packageName});


        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.ACCESS_FINE_LOCATION", PERMISSION_GRANT_STATE_GRANTED);
        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.CAMERA", PERMISSION_GRANT_STATE_GRANTED);
        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.GET_ACCOUNTS", PERMISSION_GRANT_STATE_GRANTED);
        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.MANAGE_ACCOUNTS", PERMISSION_GRANT_STATE_GRANTED);
        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.READ_CONTACTS", PERMISSION_GRANT_STATE_GRANTED);
        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.READ_EXTERNAL_STORAGE", PERMISSION_GRANT_STATE_GRANTED);
        devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, packageName, "android.permission.WRITE_EXTERNAL_STORAGE", PERMISSION_GRANT_STATE_GRANTED);





        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (launchIntent != null) {
                context.startActivity(launchIntent);
            }



    }
}

