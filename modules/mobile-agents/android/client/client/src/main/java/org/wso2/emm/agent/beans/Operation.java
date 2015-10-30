/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.emm.agent.beans;

import com.fasterxml.jackson.annotation.JsonRawValue;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * This class represents the operation information.
 */
public class Operation implements Serializable {

	private String code;
	private String type;
	private String complianceType;
	private int id;
	private String status;
	private String receivedTimeStamp;
	private String createdTimeStamp;
	private boolean enabled;
	private Object payLoad;
	private String operationResponse;

	public Operation() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComplianceType() { return complianceType; }

	public void setComplianceType(String complianceType) { this.complianceType = complianceType; }

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReceivedTimeStamp() {
		return receivedTimeStamp;
	}

	public void setReceivedTimeStamp(String receivedTimeStamp) {
		this.receivedTimeStamp = receivedTimeStamp;
	}

	public String getCreatedTimeStamp() {
		return createdTimeStamp;
	}

	public void setCreatedTimeStamp(String createdTimeStamp) {
		this.createdTimeStamp = createdTimeStamp;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@JsonRawValue
	public Object getPayLoad() {
		return payLoad;
	}

	public void setPayLoad(Object payLoad) {
		this.payLoad = payLoad;
		JSONArray convertedOperations = new JSONArray();
		if (payLoad instanceof ArrayList) {
			ArrayList<LinkedHashMap<String, String>> operations = (ArrayList) payLoad;
			for (LinkedHashMap operation : operations) {
				JSONObject jsonObject = new JSONObject(operation);
				convertedOperations.put(jsonObject);
			}
			this.payLoad = convertedOperations.toString();
		}
	}

	public String getOperationResponse() {
		return operationResponse;
	}

	public void setOperationResponse(String operationResponse) {
		this.operationResponse = operationResponse;
	}
}
