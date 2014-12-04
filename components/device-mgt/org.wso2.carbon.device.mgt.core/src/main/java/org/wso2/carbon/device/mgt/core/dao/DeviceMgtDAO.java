/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.core.dao;

import org.wso2.carbon.device.mgt.core.dao.exception.CDMDAOException;
import org.wso2.carbon.device.mgt.core.dao.exception.CDMDatabaseConnectionException;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.dto.Status;

import java.util.List;

public interface DeviceMgtDAO {

    void addDevice(Device device) throws CDMDAOException, CDMDatabaseConnectionException;

    void updateDevice(Device device) throws CDMDAOException, CDMDatabaseConnectionException;

    void updateDeviceStatus(Long deviceId, Status status) throws CDMDAOException, CDMDatabaseConnectionException;

    void deleteDevice(Long deviceId) throws CDMDAOException, CDMDatabaseConnectionException;

    List<Device> getDeviceByDeviceId(Long deviceId) throws CDMDAOException, CDMDatabaseConnectionException;

}
