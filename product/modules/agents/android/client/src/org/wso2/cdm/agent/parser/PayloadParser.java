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
package org.wso2.cdm.agent.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class PayloadParser {

	public String generateReply(JSONArray inputArray, String regId) {
		JSONObject outerJson = new JSONObject();		
		JSONArray outerArr = new JSONArray();
		try {
			outerJson.put("regId", regId);
	        outerJson.put("data",outerArr);
        } catch (JSONException e1) {
	        // TODO Auto-generated catch block
	        e1.printStackTrace();
        }
		
		
		
		for (int i = 0; i < inputArray.length(); i++) {
			try {
	            String code=inputArray.getJSONObject(i).getString("code");
	            String messageId=inputArray.getJSONObject(i).getString("messageId");
	            JSONArray data=inputArray.getJSONObject(i).getJSONArray("data");
	            
	            JSONObject dataArrContents=new JSONObject();
	            
	            dataArrContents.put("code", code);
	            JSONArray innerDataArr = new JSONArray();
	            
	            JSONObject innerDataOb=new JSONObject(); 
	            innerDataOb.put("messageId", messageId);
	            innerDataOb.put("data", data);
	            innerDataArr.put(innerDataOb);
	            
	            
	            dataArrContents.put("data", innerDataArr);
	            
	            outerArr.put(dataArrContents);
	            
	            
	            
            } catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
					
			

		}

		return outerJson.toString();

	}
}
