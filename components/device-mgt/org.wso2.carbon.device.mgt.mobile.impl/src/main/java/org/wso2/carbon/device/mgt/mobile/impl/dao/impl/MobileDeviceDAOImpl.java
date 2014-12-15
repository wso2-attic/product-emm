/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.impl.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.impl.dao.MobileDeviceDAO;
import org.wso2.carbon.device.mgt.mobile.impl.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.impl.dto.MobileDevice;

import javax.sql.DataSource;

/**
 * Implementation of MobileDeviceDAO.
 */
public class MobileDeviceDAOImpl implements MobileDeviceDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileDeviceDAOImpl.class);

	public MobileDeviceDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public MobileDevice getDevice(String deviceId) throws MobileDeviceManagementDAOException {
		return null;
	}

	@Override
	public void addDevice(MobileDevice mobileDevice)
			throws MobileDeviceManagementDAOException {

	}

	@Override
	public void updateDevice(MobileDevice mobileDevice)
			throws MobileDeviceManagementDAOException {

	}

	@Override
	public void deleteDevice(String deviceId) throws MobileDeviceManagementDAOException {

	}
}
