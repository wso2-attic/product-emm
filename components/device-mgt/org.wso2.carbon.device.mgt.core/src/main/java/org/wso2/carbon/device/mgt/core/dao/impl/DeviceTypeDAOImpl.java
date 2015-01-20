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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.core.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dao.DeviceTypeDAO;
import org.wso2.carbon.device.mgt.core.dao.util.DeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DeviceTypeDAOImpl implements DeviceTypeDAO {

    private DataSource dataSource;
    private static final Log log = LogFactory.getLog(DeviceTypeDAOImpl.class);

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
            String msg = "Error occurred while registering the device type '" + deviceType.getName() + "'";
            log.error(msg, e);
            throw new DeviceManagementDAOException(msg, e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
        }
    }

    @Override
    public void updateDeviceType(DeviceType deviceType) throws DeviceManagementDAOException {

    }

    @Override
    public List<DeviceType> getDeviceTypes() throws DeviceManagementDAOException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        List<DeviceType> deviceTypes = new ArrayList<DeviceType>();
        try {
            stmt = conn.prepareStatement("SELECT ID AS DEVICE_TYPE_ID, NAME AS DEVICE_TYPE FROM DM_DEVICE_TYPE");
            ResultSet results = stmt.executeQuery();

            while (results.next()) {
                DeviceType deviceType = new DeviceType();
                deviceType.setId(results.getLong("DEVICE_TYPE_ID"));
                deviceType.setName(results.getString("DEVICE_TYPE"));
                deviceTypes.add(deviceType);
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching the registered device types";
            log.error(msg, e);
            throw new DeviceManagementDAOException(msg, e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
        }
        return deviceTypes;
    }

    @Override
    public DeviceType getDeviceType(Integer id) throws DeviceManagementDAOException {
        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        DeviceType deviceType = null;
        try {
            stmt = conn.prepareStatement("SELECT ID AS DEVICE_TYPE_ID, NAME AS DEVICE_TYPE FROM DM_DEVICE_TYPE WHERE ID=?");
            stmt.setInt(1, id);
            ResultSet results = stmt.executeQuery();

            while (results.next()) {
                deviceType = new DeviceType();
                deviceType.setId(results.getLong("DEVICE_TYPE_ID"));
                deviceType.setName(results.getString("DEVICE_TYPE"));
            }
        } catch (SQLException e) {
            String msg = "Error occurred while fetching the registered device type";
            log.error(msg, e);
            throw new DeviceManagementDAOException(msg, e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
        }
        return deviceType;
    }

    @Override
    public Integer getDeviceTypeIdByDeviceTypeName(String type) throws DeviceManagementDAOException {

        Connection conn = this.getConnection();
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Integer deviceTypeId = null;

        try {
            String createDBQuery = "SELECT * From DM_DEVICE_TYPE DT WHERE DT.NAME=?";
            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, type);
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                deviceTypeId = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            String msg = "Error occurred while fetch device type id for device type '" + type + "'";
            log.error(msg, e);
            throw new DeviceManagementDAOException(msg, e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
        }

        return deviceTypeId;
    }

    @Override
    public void removeDeviceType(DeviceType deviceType) throws DeviceManagementDAOException {

    }

    private Connection getConnection() throws DeviceManagementDAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            String msg = "Error occurred while obtaining a connection from the device " +
                    "management metadata repository datasource";
            log.error(msg, e);
            throw new DeviceManagementDAOException(msg, e);
        }
    }
}
