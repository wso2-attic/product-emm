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

package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.MobileDeviceOperationMapping;

import java.util.List;

/**
 * This class represents the mapping between mobile device and operations.
 */
public interface MobileDeviceOperationMappingDAO {
	/**
	 * Add a new mobile device operation mapping to the table.
	 *
	 * @param deviceOperation MobileDeviceOperation object that holds data related to the MobileDeviceOperation
	 *                        to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addMobileDeviceOperation(MobileDeviceOperationMapping deviceOperation)
			throws MobileDeviceManagementDAOException;

	/**
	 * Updates a mobile device operation mapping.
	 *
	 * @param deviceOperation MobileDeviceOperation object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateMobileDeviceOperation(MobileDeviceOperationMapping deviceOperation)
			throws MobileDeviceManagementDAOException;

	/**
	 * Updates a mobile device operation mapping to In-Progress state.
	 *
	 * @param deviceId    Device id of the mapping to be deleted.
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateMobileDeviceOperationToInProgress(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Updates a mobile device operation mapping to completed state.
	 *
	 * @param deviceId    Device id of the mapping to be deleted.
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateMobileDeviceOperationToCompleted(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given mobile device operation mapping from table.
	 *
	 * @param deviceId    Device id of the mapping to be deleted.
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation. If the deletion was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteMobileDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieves a given mobile device operation from the plugin database.
	 *
	 * @param deviceId    Device id of the mapping to be retrieved.
	 * @param operationId Operation id of the mapping to be retrieved.
	 * @return MobileDeviceOperation object that holds data of the device operation mapping represented by
	 * deviceId and operationId.
	 * @throws MobileDeviceManagementDAOException
	 */
	MobileDeviceOperationMapping getMobileDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieves all the of mobile device operation mappings relavent to the given mobile device.
	 *
	 * @return Device operation mapping object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<MobileDeviceOperationMapping> getAllMobileDeviceOperationsOfDevice(String deviceId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieves all the pending device operation mappings of a mobiel device.
	 *
	 * @return Device operation mapping object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<MobileDeviceOperationMapping> getAllPendingOperationsOfMobileDevice(String deviceId)
			throws MobileDeviceManagementDAOException;
}
