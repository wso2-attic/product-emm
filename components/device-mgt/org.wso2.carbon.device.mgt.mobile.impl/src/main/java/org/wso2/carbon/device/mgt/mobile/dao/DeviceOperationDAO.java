package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.DeviceOperation;

import java.util.List;

/**
 * This class represents the mapping between device and operations.
 */
public interface DeviceOperationDAO {
	/**
	 * Add a new mapping to plugin device_operation table.
	 *
	 * @param deviceOperation DeviceOperation object that holds data related to the DeviceOperation
	 *                        to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addDeviceOperation(DeviceOperation deviceOperation)
			throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature in the feature table.
	 *
	 * @param deviceOperation DeviceOperation object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateDeviceOperation(DeviceOperation deviceOperation)
			throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given device operation from device operation table.
	 *
	 * @param deviceId    Device id of the mapping to be deleted.
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation. If the deletion was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given device operation from plugin database.
	 *
	 * @param deviceId    Device id of the mapping to be retrieved.
	 * @param operationId Operation id of the mapping to be retrieved.
	 * @return DeviceOperation object that holds data of the device operation mapping represented by
	 * deviceId and operationId.
	 * @throws MobileDeviceManagementDAOException
	 */
	DeviceOperation getDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve all the device operation mapping from plugin database.
	 *
	 * @return Device operation mapping object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<DeviceOperation> getAllDeviceOperationOfDevice(String deviceId)
			throws MobileDeviceManagementDAOException;
}
