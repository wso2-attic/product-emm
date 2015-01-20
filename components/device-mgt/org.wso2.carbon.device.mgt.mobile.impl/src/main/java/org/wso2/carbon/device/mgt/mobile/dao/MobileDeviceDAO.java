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

package org.wso2.carbon.device.mgt.mobile.dao;

import org.wso2.carbon.device.mgt.mobile.dto.MobileDevice;
import java.util.List;

/**
 * This class represents the key operations associated with persisting mobile-device related
 * information.
 */
public interface MobileDeviceDAO {

	MobileDevice getMobileDevice(String deviceId) throws MobileDeviceManagementDAOException;

	boolean addMobileDevice(MobileDevice mobileDevice) throws MobileDeviceManagementDAOException;

	boolean updateMobileDevice(MobileDevice mobileDevice) throws MobileDeviceManagementDAOException;

	boolean deleteMobileDevice(String deviceId) throws MobileDeviceManagementDAOException;

	List<MobileDevice> getAllMobileDevices() throws MobileDeviceManagementDAOException;

}
