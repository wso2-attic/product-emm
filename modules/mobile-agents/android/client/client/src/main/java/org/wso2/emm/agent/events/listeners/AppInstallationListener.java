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

public class AppInstallationListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName cdmDeviceAdmin = AgentDeviceAdminReceiver.getComponentName(context);

        String packageName =  intent.getStringExtra("packageName");

        devicePolicyManager.setLockTaskPackages(cdmDeviceAdmin, new String[]{packageName});


        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        }

    }
}
