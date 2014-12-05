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

package org.wso2.carbon.device.mgt.core.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.core.dao.DeviceDAO;
import org.wso2.carbon.device.mgt.core.dao.DeviceMgtDAO;
import org.wso2.carbon.device.mgt.core.dao.exception.DeviceDAOException;
import org.wso2.carbon.device.mgt.core.dao.exception.DeviceDatabaseConnectionException;
import org.wso2.carbon.device.mgt.core.dao.util.DeviceDAOUtil;
import org.wso2.carbon.device.mgt.core.dao.util.ErrorHandler;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.dto.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class DeviceMgtDAOImpl extends DeviceDAO implements DeviceMgtDAO {

    private static final Log log = LogFactory.getLog(DeviceMgtDAOImpl.class);
    private DataSource dataSource;

    @Override
    public void addDevice(Device device) throws DeviceDAOException, DeviceDatabaseConnectionException {

        Connection conn = null;
        PreparedStatement addDeviceDBStatement = null;
        try {
            conn = getDataSourceConnection();
            conn.setAutoCommit(false);
            String createDBQuery =
                    "INSERT INTO DM_DEVICE(DESCRIPTION,NAME,DATE_OF_ENROLLMENT,DATE_OF_LAST_UPDATE,OWNERSHIP," +
                            "STATUS,DEVICE_TYPE_ID,DEVICE_IDENTIFICATION,OWNER) VALUES (?,?,?,?,?,?,?,?,?)";

            addDeviceDBStatement = conn.prepareStatement(createDBQuery);
            addDeviceDBStatement.setString(1, device.getDescription());
            addDeviceDBStatement.setString(2, device.getName());
            addDeviceDBStatement.setLong(3, new Date().getTime());
            addDeviceDBStatement.setLong(4, new Date().getTime());
            addDeviceDBStatement.setString(5, device.getOwnerShip());
            addDeviceDBStatement.setString(6, device.getStatus().toString());
            addDeviceDBStatement.setLong(7, device.getDeviceType().getId());
            addDeviceDBStatement.setLong(8, device.getDeviceIdentificationId());
            addDeviceDBStatement.setString(9, device.getOwnerId());
            addDeviceDBStatement.executeUpdate();
            conn.commit();

        } catch (SQLException e) {
            DeviceDAOUtil.rollback(conn, DeviceManagementConstants.ADD_DEVICE_ENTRY);
            String msg = "Failed to enroll device " + device.getName() + " to CDM";
            ErrorHandler errorHandler = new ErrorHandler(msg, log);
            errorHandler.handleDAOException(msg, e);
        } finally {
            DeviceDAOUtil.cleanupResources(null, addDeviceDBStatement, conn, DeviceManagementConstants.ADD_DEVICE_ENTRY);
        }
    }

    @Override
    public void updateDevice(Device device) throws DeviceDAOException, DeviceDatabaseConnectionException {

    }

    @Override
    public void updateDeviceStatus(Long deviceId, Status status)
            throws DeviceDAOException, DeviceDatabaseConnectionException {

    }

    @Override
    public void deleteDevice(Long deviceId) throws DeviceDAOException, DeviceDatabaseConnectionException {

    }

    @Override
    public List<Device> getDeviceByDeviceId(Long deviceId)
            throws DeviceDAOException, DeviceDatabaseConnectionException {
        return null;
    }

    private Connection getDataSourceConnection() throws DeviceDatabaseConnectionException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            String msg = "Error while acquiring the database connection. Meta Repository Database server may down";
            throw new DeviceDatabaseConnectionException(msg, e);
        }
    }
}
