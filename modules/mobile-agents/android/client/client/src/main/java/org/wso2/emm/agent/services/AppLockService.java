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
package org.wso2.emm.agent.services;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.wso2.emm.agent.AppLockActivity;
import org.wso2.emm.agent.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AppLockService extends IntentService {

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

		List<String> appList = lockIntent.getStringArrayListExtra("appList");
		appList.remove(Constants.AGENT_PACKAGE);

		lockIntent = new Intent(context, AppLockActivity.class);
		lockIntent.putExtra("message", "this application is restricted by administration");
		lockIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |
		                    Intent.FLAG_ACTIVITY_NEW_TASK);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			String[] activePackages = am.getRunningAppProcesses().get(0).pkgList;
			for (int i = 0; i < activePackages.length ; i++) {
				for (String app : appList) {
					if (app.equals(activePackages[i])) {
						startActivity(lockIntent);
					}
				}
			}
		}
		else {
			// The first in the list of RunningTasks is always the foreground task.
			ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
			String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
			for (String app : appList) {
				if (app.equals(foregroundTaskPackageName)) {
					startActivity(lockIntent);
				}
			}
		}
	}
}
