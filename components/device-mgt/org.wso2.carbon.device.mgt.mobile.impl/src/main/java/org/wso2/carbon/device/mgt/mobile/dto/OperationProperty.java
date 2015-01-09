package org.wso2.carbon.device.mgt.mobile.dto;

/**
 * DTO of operation property.
 */
public class OperationProperty {
	int operationPropertyId;
	int getOperationId;
	int propertyId;
	String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getOperationPropertyId() {
		return operationPropertyId;
	}

	public void setOperationPropertyId(int operationPropertyId) {
		this.operationPropertyId = operationPropertyId;
	}

	public int getOperationId() {
		return getOperationId;
	}

	public void setOperationId(int getOperationId) {
		this.getOperationId = getOperationId;
	}

	public int getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

}
