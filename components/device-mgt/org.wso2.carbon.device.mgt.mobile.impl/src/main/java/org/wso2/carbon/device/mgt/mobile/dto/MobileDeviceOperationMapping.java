/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.device.mgt.mobile.dto;

/**
 * DTO of Mobile Device Operation Mappings.
 */
public class MobileDeviceOperationMapping {

	private String deviceId;
	private int operationId;
	private long sentDate;
	private long receivedDate;
	private Status status;

	public enum Status {
		NEW, INPROGRESS, COMPLETED
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setStatus(String status) {
		if(Status.NEW.name().equals(status)){
			this.status = Status.NEW;
		}else if(Status.INPROGRESS.name().equals(status)){
			this.status = Status.INPROGRESS;
		}else if(Status.COMPLETED.name().equals(status)){
			this.status = Status.COMPLETED;
		}
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public int getOperationId() {
		return operationId;
	}

	public void setOperationId(int operationId) {
		this.operationId = operationId;
	}

	public long getSentDate() {
		return sentDate;
	}

	public void setSentDate(long sentDate) {
		this.sentDate = sentDate;
	}

	public long getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(long receivedDate) {
		this.receivedDate = receivedDate;
	}

}
