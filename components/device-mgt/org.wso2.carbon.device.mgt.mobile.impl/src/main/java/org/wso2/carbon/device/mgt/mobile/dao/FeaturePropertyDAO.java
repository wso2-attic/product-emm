package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.FeatureProperty;

/**
 * This class represents the key operations associated with persisting feature property related
 * information.
 */
public interface FeaturePropertyDAO {
	/**
	 * Add a new feature property to plugin feature property table.
	 * @param featureProperty Feature property object that holds data related to the feature property to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addFeatureProperty(FeatureProperty featureProperty) throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature property in the feature property table.
	 * @param featureProperty Feature property object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateFeatureProperty(FeatureProperty featureProperty) throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given feature property from plugin database.
	 * @param propertyId Id of the feature property to be deleted.
	 * @return The status of the operation. If the delete was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteDeviceProperty(String propertyId) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given feature property from plugin database.
	 * @param propertyId Id of the feature property to be retrieved.
	 * @return Feature property object that holds data of the feature property represented by propertyId.
	 * @throws MobileDeviceManagementDAOException
	 */
	FeatureProperty getFeatureProperty(String propertyId) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a list of feature property corresponds to a feature code .
	 * @param featureCode feature code of the feature property to be retrieved.
	 * @return Feature property object that holds data of the feature property represented by propertyId.
	 * @throws MobileDeviceManagementDAOException
	 */
	FeatureProperty[] getFeaturePropertyOfFeature(String featureCode) throws MobileDeviceManagementDAOException;

}
