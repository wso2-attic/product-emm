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

import org.wso2.cdm.agent.utils.CommonUtilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {
	ProcessMessage processMsg = null;

	@Override
	public void onReceive(Context context, Intent intent) {

		try {
			// ---get the SMS message passed in---
			Bundle bundle = intent.getExtras();
			SmsMessage[] msgs = null;
			String str = "";
			if (bundle != null) {

				Object[] pdus = (Object[]) bundle.get("pdus");
				msgs = new SmsMessage[pdus.length];

				String unicodeMsg = "";
				String recipient = "0";
				String fullMessage = "";

				for (int i = 0; i < msgs.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
					unicodeMsg = msgs[i].getMessageBody().toString();
					recipient = msgs[i].getOriginatingAddress();
					if (i + 1 == msgs.length) {
						fullMessage = fullMessage + unicodeMsg;
					} else {
						fullMessage = fullMessage + unicodeMsg;
					}
				}

				processMsg = new ProcessMessage(context,
						CommonUtilities.MESSAGE_MODE_SMS, fullMessage,
						recipient);

			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}