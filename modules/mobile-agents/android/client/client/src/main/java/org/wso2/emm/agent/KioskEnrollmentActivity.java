package org.wso2.emm.agent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.wso2.emm.agent.services.EnrollmentService;
import org.wso2.emm.agent.utils.Constants;
import org.wso2.emm.agent.utils.Preference;

public class KioskEnrollmentActivity extends Activity {


    Button buttonStart;
    Button buttonExit;
    Button buttonLaunch;

  //  KioskEnrollmentActivity.AppInstallationBroadcastReceiver appInstallationBroadcastReceiver;
  //  boolean isAppInstallationBroadcastReceiverRegistered = false;

    private static final String ACTION_INSTALL_COMPLETE = "INSTALL_COMPLETED";

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk_enrollment);

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
            @Override
            public void onClick(View v) {
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

//    private class AppInstallationBroadcastReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//        }
//    }

}
