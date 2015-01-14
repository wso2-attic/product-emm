package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.OperationProperty;

import java.util.List;

/**
 * This class represents the key operations associated with persisting operation property related
 * information.
 */
public interface OperationPropertyDAO {
	/**
	 * Add a new mapping to plugin operation property table.
	 *
	 * @param operationProperty OperationProperty object that holds data related to the operation property to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addOperationProperty(OperationProperty operationProperty)
			throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature in the feature table.
	 *
	 * @param operationProperty DeviceOperation object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateOperationProperty(OperationProperty operationProperty)
			throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given device operation from plugin database.
	 *
	 * @param operationPropertyId Device id of the mapping to be deleted.
	 * @return The status of the operation. If the deletion was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteOperationProperty(int operationPropertyId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given device operation from plugin database.
	 *
	 * @param deviceId    Device id of the mapping to be retrieved.
	 * @param operationId Operation id of the mapping to be retrieved.
	 * @return DeviceOperation object that holds data of the device operation mapping represented by deviceId and operationId.
	 * @throws MobileDeviceManagementDAOException
	 */
	OperationProperty getOperationProperty(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve all the device operation mapping from plugin database.
	 *
	 * @return Device operation mapping object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<OperationProperty> getAllDeviceOperationOfDevice(String deviceId)
			throws MobileDeviceManagementDAOException;
}
