package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.FeatureProperty;

import java.util.List;

/**
 * This class represents the key operations associated with persisting feature property related
 * information.
 */
public interface FeaturePropertyDAO {
	/**
	 * Add a new feature property to feature property table.
	 *
	 * @param featureProperty Feature property object that holds data related to the feature property to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addFeatureProperty(FeatureProperty featureProperty)
			throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature property in the feature property table.
	 *
	 * @param featureProperty Feature property object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateFeatureProperty(FeatureProperty featureProperty)
			throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given feature property from feature property table.
	 *
	 * @param propertyId Id of the feature property to be deleted.
	 * @return The status of the operation. If the operationId was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteFeatureProperty(int propertyId) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given feature property from feature property table.
	 *
	 * @param propertyId Id of the feature property to be retrieved.
	 * @return Feature property object that holds data of the feature property represented by propertyId.
	 * @throws MobileDeviceManagementDAOException
	 */
	FeatureProperty getFeatureProperty(int propertyId) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a list of feature property corresponds to a feature id .
	 *
	 * @param featureId feature id of the feature property to be retrieved.
	 * @return Feature property object that holds data of the feature property represented by propertyId.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<FeatureProperty> getFeaturePropertyOfFeature(String featureId)
			throws MobileDeviceManagementDAOException;

}
