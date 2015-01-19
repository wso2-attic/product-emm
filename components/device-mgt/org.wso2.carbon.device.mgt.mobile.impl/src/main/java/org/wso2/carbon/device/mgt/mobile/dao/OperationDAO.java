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

import org.wso2.carbon.device.mgt.mobile.dto.Operation;

import java.util.List;

/**
 * This class represents the key operations associated with persisting operation related
 * information.
 */
public interface OperationDAO {

	/**
	 * Add a new operation to plugin operation table.
	 * @param operation Operation object that holds data related to the operation to be inserted.
	 * @return The last inserted Id is returned, if the insertion was unsuccessful -1 is returned.
	 * @throws MobileDeviceManagementDAOException
	 */
	int addOperation(Operation operation) throws MobileDeviceManagementDAOException;

	/**
	 * Update a operation in the operation table.
	 * @param operation Operation object that holds data has to be updated.
	 * @return The status of the operation. If the update was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean updateOperation(Operation operation) throws MobileDeviceManagementDAOException;

	/**
	 * Delete a given operation from plugin database.
	 * @param operationId Operation code of the operation to be deleted.
	 * @return The status of the operation. If the operationId was successful or not.
	 * @throws MobileDeviceManagementDAOException
	 */
	boolean deleteOperation(int operationId) throws MobileDeviceManagementDAOException;

	/**
	 * Retrieve a given operation from plugin database.
	 * @param operationId Operation id of the operation to be retrieved.
	 * @return Operation object that holds data of the feature represented by operationId.
	 * @throws MobileDeviceManagementDAOException
	 */
	Operation getOperation(int operationId) throws MobileDeviceManagementDAOException;

}
