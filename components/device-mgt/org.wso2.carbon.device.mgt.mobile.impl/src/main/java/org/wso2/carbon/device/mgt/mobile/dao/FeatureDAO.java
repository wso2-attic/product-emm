package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.Feature;

import java.util.List;

/**
 * This class represents the key operations associated with persisting feature related
 * information.
 */
public interface FeatureDAO {

	/**
	 * Add a new feature to plugin feature table.
	 * @param feature Feature object that holds data related to the feature to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addFeature(Feature feature) throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature in the feature table.
	 * @param feature Feature object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateFeature(Feature feature) throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given feature from plugin database.
	 * @param featureCode Feature code of the feature to be deleted.
	 * @return The status of the operation. If the delete was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteDevice(String featureCode) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given feature from plugin database.
	 * @param featureCode Feature code of the feature to be retrieved.
	 * @return Feature object that holds data of the feature represented by featureCode.
	 * @throws MobileDeviceManagementDAOException
	 */
	Feature getFeature(String featureCode) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve all the features from plugin database.
	 * @return Feature object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<Feature> getAllFeatures() throws MobileDeviceManagementDAOException;
}
