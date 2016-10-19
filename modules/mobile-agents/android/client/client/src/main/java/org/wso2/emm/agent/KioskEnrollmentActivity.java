package org.wso2.emm.agent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.wso2.emm.agent.services.AgentDeviceAdminReceiver;
import org.wso2.emm.agent.services.EnrollmentService;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

import static org.wso2.emm.agent.events.EventRegistry.context;

public class KioskEnrollmentActivity extends Activity {


    Button buttonStart;
    Button buttonExit;
    static Button buttonLaunch;

    TextView textViewKiosk;
    static TextView textViewLaunch;
    int kioskExit;

    static String packageName = null;

  //  KioskEnrollmentActivity.AppInstallationBroadcastReceiver appInstallationBroadcastReceiver;
  //  boolean isAppInstallationBroadcastReceiverRegistered = false;

    private static final String ACTION_INSTALL_COMPLETE = "INSTALL_COMPLETED";

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_enrollment);

        if (Settings.System.canWrite(getApplicationContext())){
            android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, Integer.MAX_VALUE);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        textViewLaunch = (TextView) findViewById(R.id.textViewLaunch);

//        if (!isAppInstallationBroadcastReceiverRegistered) {
//            if (appInstallationBroadcastReceiver == null)
//                appInstallationBroadcastReceiver = new KioskEnrollmentActivity.AppInstallationBroadcastReceiver();
//            registerReceiver(appInstallationBroadcastReceiver, new IntentFilter(ACTION_INSTALL_COMPLETE));
//            isAppInstallationBroadcastReceiverRegistered = true;
//        }

        startLockTask();

        buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent autoEnrollIntent = new Intent(getApplicationContext(), EnrollmentService.class);
                autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startServiceAsUser(autoEnrollIntent, android.os.Process.myUserHandle());
            }
        });

        buttonExit = (Button) findViewById(R.id.buttonExit);
        buttonExit.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                DevicePolicyManager devicePolicyManager =
                        (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName cdmDeviceAdmin = AgentDeviceAdminReceiver.getComponentName(context);
                devicePolicyManager.setStatusBarDisabled(cdmDeviceAdmin, false);
                stopLockTask();
                finish();
            }
        });

        buttonLaunch = (Button) findViewById(R.id.buttonLaunch);
        buttonLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.estmob.android.sendanywhere");
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
            }
        });


        textViewKiosk = (TextView) findViewById(R.id.textViewKiosk);
        textViewKiosk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kioskExit++;
                if(kioskExit == 6){
                    stopLockTask();
                    finish();
                }
            }
        });


        if(!Preference.getBoolean(getApplication(), Constants.PreferenceFlag.REGISTERED)){
            Thread thread = new Thread() {
                @Override
                public void run() {
                    Intent autoEnrollIntent = new Intent(getApplicationContext(), EnrollmentService.class);
                    autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    autoEnrollIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startServiceAsUser(autoEnrollIntent, android.os.Process.myUserHandle());
                }
            };
            thread.start();
        }

        textViewLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getApplicationContext().getPackageManager()
                        .getLaunchIntentForPackage(KioskEnrollmentActivity.this.packageName);
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (launchIntent != null) {
                    getApplicationContext().startActivity(launchIntent);
                }
            }
        });

        DevicePolicyManager devicePolicyManager =
                (DevicePolicyManager) getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName cdmDeviceAdmin = AgentDeviceAdminReceiver.getComponentName(getApplicationContext());


        devicePolicyManager.setApplicationHidden(cdmDeviceAdmin, Constants.SystemApp.PLAY_STORE, true);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(packageName != null){
            textViewLaunch.setVisibility(View.VISIBLE);
        }
    }


    //    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (isAppInstallationBroadcastReceiverRegistered) {
//            unregisterReceiver(appInstallationBroadcastReceiver);
//            appInstallationBroadcastReceiver = null;
//            isAppInstallationBroadcastReceiverRegistered = false;
//        }
//
//    }

    public static class AppInstallationBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String packageName =  intent.getStringExtra("packageName");
            KioskEnrollmentActivity.packageName = packageName;
            textViewLaunch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.d("Focus debug", "Focus changed !");

        if(!hasFocus) {
            Log.d("Focus debug", "Lost focus !");
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
        }
    }


}
