/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.syncml.util;

import org.apache.log4j.Logger;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for generate Device object from the received data.
 */
public class SyncmlDeviceGenerator {

	private static final String OS_VERSION = "osVersion";
	private static final String IMSI = "imsi";
	private static final String IMEI = "imei";
	private static final String VENDOR = "vendor";
	private static final String MODEL = "model";

	private static Logger logger = Logger.getLogger(SyncmlDeviceGenerator.class);

	/**
	 * This method is used to generate and return Device object from the received information at the Syncml step.
	 * @param deviceID     - Unique device ID received from the Device
	 * @param OSVersion    - Device OS version
	 * @param IMSI         - Device IMSI
	 * @param IMEI         - Device IMEI
	 * @param manufacturer - Device Manufacturer name
	 * @param model        - Device Model
	 * @return - Device Object
	 */
	public static Device generateDevice(String type,String deviceID, String OSVersion, String IMSI, String IMEI,
	                             String manufacturer, String model) {

		Device generatedDevice=new Device();

		Device.Property OSVersionProperty = new Device.Property();
		OSVersionProperty.setName(OS_VERSION);
		OSVersionProperty.setValue(OSVersion);

		Device.Property IMSEIProperty = new Device.Property();
		IMSEIProperty.setName(SyncmlDeviceGenerator.IMSI);
		IMSEIProperty.setValue(IMSI);

		Device.Property IMEIProperty = new Device.Property();
		IMEIProperty.setName(SyncmlDeviceGenerator.IMEI);
		IMEIProperty.setValue(IMEI);

		Device.Property DevManProperty = new Device.Property();
		DevManProperty.setName(VENDOR);
		DevManProperty.setValue(manufacturer);

		Device.Property DevModProperty = new Device.Property();
		DevModProperty.setName(MODEL);
		DevModProperty.setValue(model);

		List<Device.Property> propertyList = new ArrayList<Device.Property>();
		propertyList.add(OSVersionProperty);
		propertyList.add(IMSEIProperty);
		propertyList.add(IMEIProperty);
		propertyList.add(DevManProperty);
		propertyList.add(DevModProperty);

		generatedDevice.setDeviceIdentifier(deviceID);
		generatedDevice.setProperties(propertyList);
		generatedDevice.setType(type);

		return generatedDevice;
	}

	/**
	 * This method returns Device Management Object for certain tasks such as Device enrollment etc.
	 * @return DeviceManagementServiceObject
	 * @throws org.wso2.carbon.device.mgt.common.DeviceManagementServiceException
	 */
	public static DeviceManagementService getDeviceManagementService() throws DeviceManagementServiceException {

		DeviceManagementService deviceManagementService;
		PrivilegedCarbonContext.startTenantFlow();
		PrivilegedCarbonContext context = PrivilegedCarbonContext.getThreadLocalCarbonContext();
		context.setTenantDomain(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
		context.setTenantId(MultitenantConstants.SUPER_TENANT_ID);
		deviceManagementService = (DeviceManagementService) context.getOSGiService(DeviceManagementService.class, null);

		if (deviceManagementService == null) {
			String msg = "Device management service not initialized.";
			logger.error(msg);
			throw new DeviceManagementServiceException(msg);
		}
		PrivilegedCarbonContext.endTenantFlow();
		return deviceManagementService;
	}
}
