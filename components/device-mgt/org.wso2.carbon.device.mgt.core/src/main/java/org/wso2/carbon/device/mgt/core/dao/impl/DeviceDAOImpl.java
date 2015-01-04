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
import org.wso2.carbon.device.mgt.core.dao.DeviceDAO;
import org.wso2.carbon.device.mgt.core.dao.util.DeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.dto.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class DeviceDAOImpl implements DeviceDAO {

    private DataSource dataSource;
    private static final Log log = LogFactory.getLog(DeviceDAOImpl.class);


    public DeviceDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addDevice(Device device) throws DeviceManagementDAOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = this.getConnection();
            String createDBQuery =
                    "INSERT INTO DM_DEVICE(DESCRIPTION, NAME, DATE_OF_ENROLLMENT, DATE_OF_LAST_UPDATE, OWNERSHIP," +
                            "STATUS, DEVICE_TYPE_ID, DEVICE_IDENTIFICATION, OWNER, TENANT_ID) VALUES " +
                            "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = conn.prepareStatement(createDBQuery);
            stmt.setString(1, device.getDescription());
            stmt.setString(2, device.getName());
            stmt.setLong(3, new Date().getTime());
            stmt.setLong(4, new Date().getTime());
            stmt.setString(5, device.getOwnerShip());
            stmt.setString(6, device.getStatus().toString());
            stmt.setInt(7, device.getDeviceType());
            stmt.setString(8, device.getDeviceIdentificationId());
            stmt.setString(9, device.getOwnerId());
            stmt.setInt(10, device.getTenantId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            String msg = "Error occurred while enrolling device '" + device.getName() + "'";
            log.error(msg, e);
            throw new DeviceManagementDAOException(msg, e);
        } finally {
            DeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
        }
    }

    @Override
    public void updateDevice(Device device) throws DeviceManagementDAOException {

    }

    @Override
    public void updateDeviceStatus(Long deviceId, Status status) throws DeviceManagementDAOException {

    }

    @Override
    public void deleteDevice(Long deviceId) throws DeviceManagementDAOException {

    }

    @Override
    public Device getDeviceByDeviceId(Long deviceId)
            throws DeviceManagementDAOException {
        return null;
    }

    @Override
    public List<Device> getDevices() throws DeviceManagementDAOException {
        return null;
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
