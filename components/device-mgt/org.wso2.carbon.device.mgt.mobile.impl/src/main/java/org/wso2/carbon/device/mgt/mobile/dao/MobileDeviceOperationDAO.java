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

import org.wso2.carbon.device.mgt.mobile.dto.MobileDeviceOperation;

import java.util.List;

/**
 * This class represents the mapping between device and operations.
 */
public interface MobileDeviceOperationDAO {
	/**
	 * Add a new mapping to plugin device_operation table.
	 *
	 * @param deviceOperation DeviceOperation object that holds data related to the DeviceOperation
	 *                        to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addMobileDeviceOperation(MobileDeviceOperation deviceOperation)
			throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature in the feature table.
	 *
	 * @param deviceOperation DeviceOperation object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateMobileDeviceOperation(MobileDeviceOperation deviceOperation)
			throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given device operation from device operation table.
	 *
	 * @param deviceId    Device id of the mapping to be deleted.
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation. If the deletion was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteMobileDeviceOperation(String deviceId, int operationId)
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
	MobileDeviceOperation getMobileDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve all the device operation mapping from plugin database.
	 *
	 * @return Device operation mapping object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<MobileDeviceOperation> getAllMobileDeviceOperationsOfDevice(String deviceId)
			throws MobileDeviceManagementDAOException;
}
