package org.wso2.carbon.device.mgt.mobile.dto;

/**
 * DTO of Operations.
 */
public class DeviceOperation {
	String deviceId;
	int operationId;
	int sentDate;
	int receivedDate;

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

	public int getSentDate() {
		return sentDate;
	}

	public void setSentDate(int sentDate) {
		this.sentDate = sentDate;
	}

	public int getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(int receivedDate) {
		this.receivedDate = receivedDate;
	}

}
