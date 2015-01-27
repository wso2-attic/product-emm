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

import android.net.TrafficStats;

public class TrafficRecord {
	public long tx=0;
	public long rx=0;
	public String tag=null;
	
	public TrafficRecord() {
		tx=TrafficStats.getTotalTxBytes();
		rx=TrafficStats.getTotalRxBytes();
	}
	
	public TrafficRecord(int uid, String tag) {
		tx=TrafficStats.getUidTxBytes(uid);
		rx=TrafficStats.getUidRxBytes(uid);
		this.tag=tag;
	}
}