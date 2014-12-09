/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.mgt.core.dao.impl;

import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.core.dao.util.DeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.core.dao.DeviceTypeDAO;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeviceTypeDAOImpl implements DeviceTypeDAO {

    private DataSource dataSource;

    public DeviceTypeDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addDeviceType(DeviceType deviceType) throws DeviceManagementDAOException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement("INSERT INTO DM_DEVICE_TYPE (NAME) VALUES (?)");
            stmt.setString(1, deviceType.getName());
            stmt.execute();
        } catch (SQLException e) {
            throw new DeviceManagementDAOException("Error occurred while registering the device type '" +
                    deviceType.getName() + "'", e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
        }
    }

    @Override
    public void updateDeviceType(DeviceType deviceType) throws DeviceManagementDAOException {

    }

    @Override
    public List<DeviceType> getDeviceTypes() throws DeviceManagementDAOException {
        return null;
    }

    @Override
    public DeviceIdentifier getDeviceType() throws DeviceManagementDAOException {
        return new DeviceIdentifier();
    }

    private Connection getConnection() throws DeviceManagementDAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new DeviceManagementDAOException("Error occurred while obtaining a connection from the device " +
                    "management metadata repository datasource", e);
        }
    }

}
