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
