/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.mobile.impl.android;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.Operation;
import org.wso2.carbon.device.mgt.common.OperationManagementException;
import org.wso2.carbon.device.mgt.mobile.AbstractMobileOperationManager;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.mobile.dto.MobileDeviceOperation;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperation;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperationProperty;
import org.wso2.carbon.device.mgt.mobile.util.MobileDeviceManagementUtil;

import java.util.ArrayList;
import java.util.List;

public class AndroidMobileOperationManager extends AbstractMobileOperationManager {

	private static final Log log = LogFactory.getLog(AndroidMobileOperationManager.class);

	@Override
	public boolean addOperation(Operation operation, List<DeviceIdentifier> devices) throws
	                                                                                 OperationManagementException {
		boolean status = false;
		try {
			MobileDeviceOperation mobileDeviceOperation = null;
			MobileOperation mobileOperation =
					MobileDeviceManagementUtil.convertToMobileOperation(operation);
			int operationId = MobileDeviceManagementDAOFactory.getMobileOperationDAO()
			                                                  .addMobileOperation(mobileOperation);
			if (operationId > 0) {
				for (MobileOperationProperty operationProperty : mobileOperation.getProperties()) {
					operationProperty.setOperationId(operationId);
					status = MobileDeviceManagementDAOFactory.getMobileOperationPropertyDAO()
					                                         .addMobileOperationProperty(
							                                         operationProperty);
				}
				for (DeviceIdentifier deviceIdentifier : devices) {
					mobileDeviceOperation = new MobileDeviceOperation();
					mobileDeviceOperation.setOperationId(operationId);
					mobileDeviceOperation.setDeviceId(deviceIdentifier.getId());
					status = MobileDeviceManagementDAOFactory.getMobileDeviceOperationDAO()
					                                         .addMobileDeviceOperation(
							                                         new MobileDeviceOperation());
				}
			}
		} catch (MobileDeviceManagementDAOException e) {
			String msg =
					"Error while adding an operation " + operation.getCode() + "to Android devices";
			log.error(msg, e);
			throw new OperationManagementException(msg, e);
		}
		return status;
	}

	@Override
	public List<Operation> getOperations(DeviceIdentifier deviceIdentifier)
			throws OperationManagementException {
		List<Operation> operations = new ArrayList<Operation>();
		List<MobileDeviceOperation> mobileDeviceOperations = null;
		MobileOperation mobileOperation = null;
		try {
			mobileDeviceOperations = MobileDeviceManagementDAOFactory.getMobileDeviceOperationDAO()
			                                                         .getAllMobileDeviceOperationsOfDevice(
					                                                         deviceIdentifier
							                                                         .getId());
			if (mobileDeviceOperations.size() > 0) {
				List<Integer> operationIds = MobileDeviceManagementUtil
						.getMobileOperationIdsFromMobileDeviceOperations(mobileDeviceOperations);
				for (Integer operationId : operationIds) {
					mobileOperation = MobileDeviceManagementDAOFactory.getMobileOperationDAO()
					                                                  .getMobileOperation(
							                                                  operationId);
					operations.add(MobileDeviceManagementUtil
							               .convertMobileOperationToOperation(mobileOperation));
				}
			}
		} catch (MobileDeviceManagementDAOException e) {
			String msg =
					"Error while fetching the operations for the android device " +
					deviceIdentifier.getId();
			log.error(msg, e);
			throw new OperationManagementException(msg, e);
		}
		return operations;
	}
}