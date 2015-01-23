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
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.mobile.AbstractMobileOperationManager;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.mobile.dao.MobileFeatureDAO;
import org.wso2.carbon.device.mgt.mobile.dao.MobileFeaturePropertyDAO;
import org.wso2.carbon.device.mgt.mobile.dto.*;
import org.wso2.carbon.device.mgt.mobile.util.MobileDeviceManagementUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AndroidMobileOperationManager extends AbstractMobileOperationManager {

	private static final Log log = LogFactory.getLog(AndroidMobileOperationManager.class);

	@Override
	public boolean addOperation(Operation operation, List<DeviceIdentifier> devices) throws
	                                                                                 OperationManagementException {
		boolean status = false;
		try {
			MobileDeviceOperationMapping mobileDeviceOperationMapping = null;
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
					mobileDeviceOperationMapping = new MobileDeviceOperationMapping();
					mobileDeviceOperationMapping.setOperationId(operationId);
					mobileDeviceOperationMapping.setDeviceId(deviceIdentifier.getId());
					mobileDeviceOperationMapping.setStatus(MobileDeviceOperationMapping.Status.NEW);
					status = MobileDeviceManagementDAOFactory.getMobileDeviceOperationDAO()
					                                         .addMobileDeviceOperation(
							                                         mobileDeviceOperationMapping);
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
		List<MobileDeviceOperationMapping> mobileDeviceOperationMappings = null;
		List<MobileOperationProperty> operationProperties = null;
		MobileOperation mobileOperation = null;
		try {
			mobileDeviceOperationMappings = MobileDeviceManagementDAOFactory.getMobileDeviceOperationDAO()
			                                                         .getAllMobileDeviceOperationsOfDevice(
					                                                         deviceIdentifier
							                                                         .getId());
			if (mobileDeviceOperationMappings.size() > 0) {
				List<Integer> operationIds = MobileDeviceManagementUtil
						.getMobileOperationIdsFromMobileDeviceOperations(
								mobileDeviceOperationMappings);
				for (Integer operationId : operationIds) {
					mobileOperation = MobileDeviceManagementDAOFactory.getMobileOperationDAO()
					                                                  .getMobileOperation(
							                                                  operationId);
					operationProperties =
							MobileDeviceManagementDAOFactory.getMobileOperationPropertyDAO()
							                                .getAllMobileOperationPropertiesOfOperation(
									                                operationId);
					mobileOperation.setProperties(operationProperties);
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

	@Override
	public List<Operation> getPendingOperations(DeviceIdentifier deviceIdentifier)
			throws OperationManagementException {
		List<Operation> operations = new ArrayList<Operation>();
		List<MobileDeviceOperationMapping> mobileDeviceOperationMappings = null;
		List<MobileOperationProperty> operationProperties = null;
		MobileOperation mobileOperation = null;
		try {
			//Get the list of pending operations for the given device
			mobileDeviceOperationMappings = MobileDeviceManagementDAOFactory.getMobileDeviceOperationDAO()
			                                                         .getAllPendingOperationsOfMobileDevice(
					                                                         deviceIdentifier
							                                                         .getId());
			//Go through each operation mapping for retrieving the data corresponding to each operation
			for (MobileDeviceOperationMapping operation : mobileDeviceOperationMappings) {
				//Get the MobileOperation data
				mobileOperation = MobileDeviceManagementDAOFactory.getMobileOperationDAO()
				                                                  .getMobileOperation(operation
						                                                                      .getOperationId());
				//Get properties of the operation
				operationProperties =
						MobileDeviceManagementDAOFactory.getMobileOperationPropertyDAO()
						                                .getAllMobileOperationPropertiesOfOperation(
								                                operation.getOperationId());
				mobileOperation.setProperties(operationProperties);
				operations.add(MobileDeviceManagementUtil
						               .convertMobileOperationToOperation(mobileOperation));
				//Update the MobileDeviceOperationMapping data to the In-Progress state
				operation.setStatus(MobileDeviceOperationMapping.Status.INPROGRESS);
				operation.setSentDate(new Date().getTime());
				MobileDeviceManagementDAOFactory.getMobileDeviceOperationDAO()
				                                .updateMobileDeviceOperationToInProgress(
						                                operation.getDeviceId(),
						                                operation.getOperationId());
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

	@Override
	public List<Feature> getFeaturesForDeviceType(String deviceType) throws FeatureManagementException {
		MobileFeatureDAO featureDAO = MobileDeviceManagementDAOFactory.getFeatureDAO();
		MobileFeaturePropertyDAO featurePropertyDAO = MobileDeviceManagementDAOFactory.getFeaturePropertyDAO();
		List<Feature> features = new ArrayList<Feature>();
		try {
			List<MobileFeature> mobileFeatures = featureDAO.getFeatureByDeviceType(deviceType);
			for (MobileFeature mobileFeature : mobileFeatures) {
				Feature feature = new Feature();
				feature.setId(mobileFeature.getId());
				feature.setDeviceType(mobileFeature.getDeviceType());
				feature.setName(mobileFeature.getName());
				List<Feature.MetadataEntry> metadataEntries = new ArrayList<Feature.MetadataEntry>();
				List<MobileFeatureProperty> properties =
						featurePropertyDAO.getFeaturePropertyOfFeature(mobileFeature.getId());
				for (MobileFeatureProperty property : properties) {
					Feature.MetadataEntry metaEntry = new Feature.MetadataEntry();
					metaEntry.setId(property.getFeatureID());
					metaEntry.setValue(property.getProperty());
					metadataEntries.add(metaEntry);
				}
				feature.setMetadataEntries(metadataEntries);
				features.add(feature);
			}
		} catch (MobileDeviceManagementDAOException e) {
			String msg =
					"Error while fetching the features for the device type " +
					deviceType;
			log.error(msg, e);
			throw new FeatureManagementException(msg, e);
		}
		return features;
	}
}