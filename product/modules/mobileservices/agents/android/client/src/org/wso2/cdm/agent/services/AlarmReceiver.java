/*
 ~ Copyright (c) 2014, WSO2 Inc. (http://wso2.com/) All Rights Reserved.
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~      http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
*/
package org.wso2.cdm.agent.services;

import java.util.Map;

import org.wso2.cdm.agent.R;
import org.wso2.cdm.agent.utils.CommonUtilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
	 
    private static final String DEBUG_TAG = "AlarmReceiver";
    Map<String, String> server_res = null;
    Context context;
    ProcessMessage processMsg = null;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(DEBUG_TAG, "Recurring alarm; requesting download service.");
        this.context = context;
        String mode=CommonUtilities.getPref(context, context.getResources().getString(R.string.shared_pref_message_mode));
		if(mode.trim().toUpperCase().equals("LOCAL")){
			ProcessMessage msg=new ProcessMessage(context);
			msg.getOperations(null);
		}
    }
 
}
