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
package org.wso2.cdm.agent.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;
import org.json.simple.JSONArray;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

public class TrackCallSMS {
	Context context = null;
	ContentResolver cr = null;

	public TrackCallSMS(Context context) {
		this.context = context;
		cr = this.context.getContentResolver();
	}
	
	/**
	 * Returns a JSONArray of call detail objects Ex: [{number:"0112345666", type:"INCOMING", date:"dd/MM/yyyy hh:mm:ss.SSS", duration:"90"}]
	 */
	public JSONArray getCallDetails() {
		JSONArray jsonArray = null;
		try {
			Cursor managedCursor = cr.query(CallLog.Calls.CONTENT_URI, null,
					null, null, null);
			int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
			int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
			int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
			int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
			jsonArray = new JSONArray();

			while (managedCursor.moveToNext()) {
				JSONObject jsonObj = new JSONObject();
				String phNumber = managedCursor.getString(number);
				String callType = managedCursor.getString(type);
				String callDate = managedCursor.getString(date);
				Date callDayTime = new Date(Long.valueOf(callDate));
				String callDuration = managedCursor.getString(duration);
				String dir = null;
				int dircode = Integer.parseInt(callType);
				switch (dircode) {
				case CallLog.Calls.OUTGOING_TYPE:
					dir = "OUTGOING";
					break;

				case CallLog.Calls.INCOMING_TYPE:
					dir = "INCOMING";
					break;

				case CallLog.Calls.MISSED_TYPE:
					dir = "MISSED";
					break;
				}
				jsonObj.put("number", phNumber);
				jsonObj.put("type", dir);
				jsonObj.put("date", callDayTime);
				jsonObj.put("duration", callDuration);
				jsonArray.add(jsonObj);
			}

			managedCursor.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonArray;
	}

	/**
	 * Returns a JSONArray of SMS objects Ex: [{number:"0772345666", date:"dd/MM/yyyy hh:mm:ss.SSS", content:"Hello"}]
	 * 
	 * @param type
	 *            - Folder type should be passed in (1 for Inbox, 2 for Sent box)
	 */
	public JSONArray getSMS(int type) {
		JSONArray jsonArray = null;
		try {
			Uri uriSms = Uri.parse("content://sms");

			Cursor cursor = cr.query(uriSms, new String[] { "_id", "address",
					"date", "body", "type", "read" }, "type=" + type, null,
					"date" + " COLLATE LOCALIZED ASC");

			if (cursor != null) {
				cursor.moveToLast();
				if (cursor.getCount() > 0) {
					jsonArray = new JSONArray();
					do {
						JSONObject jsonObj = new JSONObject();
						String date = cursor.getString(cursor
								.getColumnIndex("date"));
						Long timestamp = Long.parseLong(date);
						Calendar calendar = Calendar.getInstance();
						calendar.setTimeInMillis(timestamp);
						DateFormat formatter = new SimpleDateFormat(
								"dd/MM/yyyy hh:mm:ss.SSS");
						jsonObj.put("number", cursor.getString(cursor
								.getColumnIndex("address")));
						jsonObj.put("date",
								formatter.format(calendar.getTime()));
						/*jsonObj.put("content",
								cursor.getString(cursor.getColumnIndex("body")));*/
						//jsonObj.put("content","Testing SMS");
						jsonArray.add(jsonObj);
					} while (cursor.moveToPrevious());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonArray;

	}

}
