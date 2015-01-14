package org.wso2.carbon.device.mgt.mobile.dto;

/**
 * DTO of feature property. Represents a property of a feature.
 */
public class FeatureProperty {
	int propertyId;
	String property;
	String featureID;

	public String getFeatureID() {
		return featureID;
	}

	public void setFeatureID(String featureID) {
		this.featureID = featureID;
	}

	public int getPropertyId() {
		return propertyId;
	}

	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

}
