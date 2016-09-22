package org.wso2.emm.agent;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

/**
 * This activity is used to lock the device.
 * LockActivty only works if agent is registered as the device owner.
 *
 */
public class LockActivity extends Activity {

    private static final String TAG = LockActivity.class.getSimpleName();
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponentName;
    private PackageManager packageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        devicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        adminComponentName = AgentDeviceAdminReceiver.getComponentName(this);
        packageManager = getPackageManager();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lock);

        if (Preference.getBoolean(this, Constants.PreferenceFlag.IS_LOCKED)) {
            boolean isHardLockDisabled = getIntent().getBooleanExtra(Constants.DISABLE_HARD_LOCK, false);
            if (isHardLockDisabled) {
                Preference.putBoolean(this, Constants.PreferenceFlag.IS_LOCKED, false);
                disablePinnedActivity();
            }
        }

        Bundle extras = getIntent().getExtras();

        enablePinnedActivity();

        TextView adminMessage = (TextView) findViewById(R.id.admin_message);
        if (extras.getString(Constants.ADMIN_MESSAGE) != null) {
            adminMessage.setText(extras.getString(Constants.ADMIN_MESSAGE));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // start lock task mode if it's not already active
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // ActivityManager.getLockTaskModeState api is not available in pre-M.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (!am.isInLockTaskMode()) {
                startLockTask();
            }
        } else {
            if (am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
                startLockTask();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enablePinnedActivity() {
        //startLockTask();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        String lockedPackages[] = {Constants.PACKAGE_NAME};
        devicePolicyManager.setLockTaskPackages(adminComponentName, lockedPackages);
        devicePolicyManager.setKeyguardDisabled(adminComponentName, true);
        devicePolicyManager.addPersistentPreferredActivity(adminComponentName, intentFilter,
                                                           new ComponentName(getPackageName(),
                                                                             LockActivity.class.getName()));

        if (Constants.DEBUG_MODE_ENABLED) {
            Log.d(TAG, "Hard lock is enabled");
        }
    }

    private void disablePinnedActivity() {
        stopLockTask();
        devicePolicyManager.setKeyguardDisabled(adminComponentName, false);
        packageManager.setComponentEnabledSetting(
                new ComponentName(getPackageName(), getClass().getName()),
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);
        devicePolicyManager.clearPackagePersistentPreferredActivities(adminComponentName,
                                                                      getPackageName());
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Device is locked", Toast.LENGTH_LONG).show();
    }

}
