/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.MobileOperationDAO;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperation;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of MobileOperationDAO.
 */
public class MobileOperationDAOImpl implements MobileOperationDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileOperationDAOImpl.class);

	public MobileOperationDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int addMobileOperation(MobileOperation operation)
			throws MobileDeviceManagementDAOException {
		int status = -1;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_OPERATION(FEATURE_CODE, CREATED_DATE) VALUES ( ?, ?)";
			stmt = conn.prepareStatement(createDBQuery, new String[] { "OPERATION_ID" });
			stmt.setString(1, operation.getFeatureCode());
			stmt.setLong(2, operation.getCreatedDate());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs != null && rs.next()) {
					status = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding the operation - '" +
			             operation.getFeatureCode() + "' to MBL_OPERATION table";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateMobileOperation(MobileOperation operation)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_OPERATION SET FEATURE_CODE = ?, CREATED_DATE = ? WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setString(1, operation.getFeatureCode());
			stmt.setLong(2, operation.getCreatedDate());
			stmt.setInt(3, operation.getOperationId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating the MBL_OPERATION table entry with operation id - '" +
			             operation.getOperationId() + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteMobileOperation(int operationId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_OPERATION WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setInt(1, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while deleting MBL_OPERATION entry with operation Id - ";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public MobileOperation getMobileOperation(int operationId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileOperation operation = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, FEATURE_CODE, CREATED_DATE FROM MBL_OPERATION WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, operationId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				operation = new MobileOperation();
				operation.setOperationId(resultSet.getInt(1));
				operation.setFeatureCode(resultSet.getString(2));
				operation.setCreatedDate(resultSet.getLong(3));
				break;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while fetching operationId - '" +
			             operationId + "' from MBL_OPERATION";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return operation;
	}

	private Connection getConnection() throws MobileDeviceManagementDAOException {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			String msg = "Error occurred while obtaining a connection from the mobile device " +
			             "management metadata repository datasource.";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		}
	}
}