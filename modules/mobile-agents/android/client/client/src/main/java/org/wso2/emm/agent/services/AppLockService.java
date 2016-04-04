package org.wso2.emm.agent.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.wso2.emm.agent.AppLockActivity;

import java.util.ArrayList;


public class AppLockService extends IntentService{

    private static final String TAG = "AppLockService";
    private Context context;

    public AppLockService() {
        super(AppLockService.class.getName());
        context = AppLockService.this;


    }

    @Override
    protected void onHandleIntent(Intent lockIntent) {
        Log.d(TAG, "Service started...!");

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // The first in the list of RunningTasks is always the foreground task.
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);

        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

        ArrayList<String> appList = lockIntent.getStringArrayListExtra("appList");

        lockIntent = new Intent(context, AppLockActivity.class);
        lockIntent.putExtra("message", "this application is restricted by administration");
        lockIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        for(String app : appList){
            if(app.equals(foregroundTaskPackageName)){
                startActivity(lockIntent);
            }
        }
    }
}
