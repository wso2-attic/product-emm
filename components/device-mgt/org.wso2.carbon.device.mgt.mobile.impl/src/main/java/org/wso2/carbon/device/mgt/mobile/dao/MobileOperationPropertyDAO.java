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

import org.wso2.carbon.device.mgt.mobile.dto.MobileOperationProperty;

import java.util.List;

/**
 * This class represents the key operations associated with persisting operation property related
 * information.
 */
public interface MobileOperationPropertyDAO {
	/**
	 * Add a new mapping to plugin operation property table.
	 *
	 * @param operationProperty OperationProperty object that holds data related to the operation
	 *       property to be inserted.
	 * @return The status of the operation. If the insert was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean addMobileOperationProperty(MobileOperationProperty operationProperty)
			throws MobileDeviceManagementDAOException;

	/**
	 * Update a feature in the feature table.
	 *
	 * @param operationProperty DeviceOperation object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateMobileOperationProperty(MobileOperationProperty operationProperty)
			throws MobileDeviceManagementDAOException;

	/**
	 * Deletes mobile operation properties of a given operation id from the plugin database.
	 *
	 * @param operationId Operation id of the mapping to be deleted.
	 * @return The status of the operation. If the deletion was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteMobileOperationProperties(int operationId)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given mobile operation property from plugin database.
	 *
	 * @param operationId Operation id of the mapping to be retrieved.
	 * @param property    Property of the mapping to be retrieved.
	 * @return DeviceOperation object that holds data of the device operation mapping represented by
	 *         deviceId and operationId.
	 * @throws MobileDeviceManagementDAOException
	 */
	MobileOperationProperty getMobileOperationProperty(int operationId, String property)
			throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve all the mobile operation properties related to the a operation id.
	 *
	 * @param operationId Operation id of the mapping to be retrieved.
	 * @return Device operation mapping object list.
	 * @throws MobileDeviceManagementDAOException
	 */
	List<MobileOperationProperty> getAllMobileOperationPropertiesOfOperation(int operationId)
			throws MobileDeviceManagementDAOException;
}
