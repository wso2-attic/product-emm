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

import java.util.HashMap;

import android.content.Context;
import android.content.pm.ApplicationInfo;

public class TrafficSnapshot {
	public TrafficRecord device=null;
	public HashMap<Integer, TrafficRecord> apps=
		new HashMap<Integer, TrafficRecord>();
	
	public TrafficSnapshot(Context ctxt) {
		device=new TrafficRecord();
		
		HashMap<Integer, String> appNames=new HashMap<Integer, String>();
		
		for (ApplicationInfo app :
					ctxt.getPackageManager().getInstalledApplications(0)) {
			appNames.put(app.uid, app.packageName);
		}
		
		for (Integer uid : appNames.keySet()) {
			apps.put(uid, new TrafficRecord(uid, appNames.get(uid)));
		}
	}
}