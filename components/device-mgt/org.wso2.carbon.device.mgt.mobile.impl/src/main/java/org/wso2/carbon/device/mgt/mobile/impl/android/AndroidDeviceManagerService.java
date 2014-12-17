/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
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
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.mobile.impl.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.impl.dao.MobileDeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.mobile.impl.dto.MobileDevice;
import org.wso2.carbon.device.mgt.mobile.impl.util.MobileDeviceManagementUtil;

import java.util.List;

/**
 * This represents the Android implementation of DeviceManagerService.
 */
public class AndroidDeviceManagerService implements DeviceManagerService {

	private static final Log log = LogFactory.getLog(AndroidDeviceManagerService.class);

	@Override
	public String getProviderType() {
		return DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_ANDROID;
	}

	@Override
	public boolean enrollDevice(Device device) throws DeviceManagementException {
		boolean status = false;
		MobileDevice mobileDevice = MobileDeviceManagementUtil.convertToMobileDevice(device);
		try {
			status = MobileDeviceManagementDAOFactory.getMobileDeviceDAO().addDevice(mobileDevice);
		} catch (MobileDeviceManagementDAOException e) {
			String msg = "Error while enrolling the Android device : " +
			             device.getDeviceIdentifier();
			log.error(msg, e);
			throw new DeviceManagementException(msg, e);
		}
		return status;
	}

	@Override
	public boolean modifyEnrollment(Device device) throws DeviceManagementException {
		boolean status = false;
		MobileDevice mobileDevice = MobileDeviceManagementUtil.convertToMobileDevice(device);
		try {
			status = MobileDeviceManagementDAOFactory.getMobileDeviceDAO().updateDevice(mobileDevice);
		} catch (MobileDeviceManagementDAOException e) {
			String msg = "Error while updating the enrollment of the Android device : " +
			             device.getDeviceIdentifier();
			log.error(msg, e);
			throw new DeviceManagementException(msg, e);
		}
		return status;
	}

	@Override
	public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
		boolean status = false;
		try {
			status = MobileDeviceManagementDAOFactory.getMobileDeviceDAO().deleteDevice(deviceId.getId());
		} catch (MobileDeviceManagementDAOException e) {
			String msg = "Error while removing the Android device : " + deviceId.getId();
			log.error(msg, e);
			throw new DeviceManagementException(msg, e);
		}
		return status;
	}

	@Override
	public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
		boolean isEnrolled = false;
		try {
			MobileDevice mobileDevice =
					MobileDeviceManagementDAOFactory.getMobileDeviceDAO().getDevice(
							deviceId.getId());
			if(mobileDevice!=null){
				isEnrolled = true;
			}
		} catch (MobileDeviceManagementDAOException e) {
			String msg = "Error while checking the enrollment status of Android device : " +
			             deviceId.getId();
			log.error(msg, e);
			throw new DeviceManagementException(msg, e);
		}
		return isEnrolled;
	}

	@Override
	public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
		return true;
	}

	@Override
	public boolean setActive(DeviceIdentifier deviceId, boolean status)
			throws DeviceManagementException {
		return true;
	}

	@Override
	public List<Device> getAllDevices(String type) throws DeviceManagementException {
		return null;
	}

	@Override
	public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
		Device device = null;
		try {
			MobileDevice mobileDevice = MobileDeviceManagementDAOFactory.getMobileDeviceDAO().
					getDevice(deviceId.getId());
			device = MobileDeviceManagementUtil.convertToDevice(mobileDevice);
		} catch (MobileDeviceManagementDAOException e) {
			String msg = "Error while fetching the Android device : " + deviceId.getId();
			log.error(msg, e);
			throw new DeviceManagementException(msg, e);
		}
		return device;
	}

	@Override
	public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
			throws DeviceManagementException {
		return true;
	}

	@Override
	public boolean updateDeviceInfo(Device device) throws DeviceManagementException {
		boolean status = false;
		MobileDevice mobileDevice = MobileDeviceManagementUtil.convertToMobileDevice(device);
		try {
			status = MobileDeviceManagementDAOFactory.getMobileDeviceDAO().updateDevice(mobileDevice);
		} catch (MobileDeviceManagementDAOException e) {
			String msg = "Error while updating the Android device : " + device.getDeviceIdentifier();
			log.error(msg, e);
			throw new DeviceManagementException(msg, e);
		}
		return status;
	}
}