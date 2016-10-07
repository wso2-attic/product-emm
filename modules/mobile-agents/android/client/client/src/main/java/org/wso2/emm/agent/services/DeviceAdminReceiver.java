/**
 * Created by Chatura Dilan Perera on 6/10/2016.
 */
package org.wso2.emm.agent.services;

import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.wso2.emm.agent.KioskAppActivity;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

public class DeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {

    private static final String FRESH_BOOTUP_FLAG = "fresh_bootup";
    private static final String TAG = "DeviceAdminReceiver" ;
    public static final String DISALLOW_SAFE_BOOT = "no_safe_boot";

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case Intent.ACTION_BOOT_COMPLETED:
                //doEnrollment(context);

                break;
            default:
                super.onReceive(context, intent);
                break;
        }
    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onProfileProvisioningComplete(Context context, Intent intent) {

        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName cdmDeviceAdmin = DeviceAdminReceiver.getComponentName(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.READ_PHONE_STATE",
                    DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
            devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.READ_EXTERNAL_STORAGE",
                    DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

            devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.ACCESS_COARSE_LOCATION",
                    DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

            devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.WRITE_EXTERNAL_STORAGE",
                    DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);

            devicePolicyManager.setPermissionGrantState(cdmDeviceAdmin, "org.wso2.emm.agent", "android.permission.ACCESS_FINE_LOCATION",
                    DevicePolicyManager.PERMISSION_GRANT_STATE_GRANTED);
        }

        setUserRestriction(devicePolicyManager, cdmDeviceAdmin, DISALLOW_SAFE_BOOT, true);


        devicePolicyManager.setApplicationHidden(cdmDeviceAdmin, Constants.SystemApp.PLAY_STORE, true);

        Log.i(TAG, "Provisioning Completed");
        Preference.putBoolean(context, FRESH_BOOTUP_FLAG, true);
        Intent autoEnrollIntent = new Intent(context, EnrollmentService.class);
        autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startServiceAsUser(autoEnrollIntent, android.os.Process.myUserHandle());

        Preference.putBoolean(context, Constants.PreferenceFlag.SKIP_DEVICE_ACTIVATION, true);
        Intent launch = new Intent(context, KioskAppActivity.class);
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(launch);

    }

    /**
     * @param context The context of the application.
     * @return The component name of this component in the given context.
     */
    public static ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), DeviceAdminReceiver.class);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUserRestriction(DevicePolicyManager devicePolicyManager, ComponentName adminComponentName
            , String restriction, boolean disallow) {
        if (disallow) {
            devicePolicyManager.addUserRestriction(adminComponentName, restriction);
        } else {
            devicePolicyManager.clearUserRestriction(adminComponentName, restriction);
        }
    }

}
