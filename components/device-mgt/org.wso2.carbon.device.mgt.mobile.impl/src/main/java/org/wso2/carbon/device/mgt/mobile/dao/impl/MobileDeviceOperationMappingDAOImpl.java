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
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceOperationMappingDAO;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.MobileDeviceOperationMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of MobileDeviceOperationMappingDAO.
 */
public class MobileDeviceOperationMappingDAOImpl implements MobileDeviceOperationMappingDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileDeviceOperationMappingDAOImpl.class);

	public MobileDeviceOperationMappingDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addMobileDeviceOperationMapping(MobileDeviceOperationMapping deviceOperation)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_DEVICE_OPERATION_MAPPING (DEVICE_ID, OPERATION_ID, SENT_DATE, " +
					"RECEIVED_DATE, STATUS) VALUES (?, ?, ?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, deviceOperation.getDeviceId());
			stmt.setLong(2, deviceOperation.getOperationId());
			stmt.setLong(3, deviceOperation.getSentDate());
			stmt.setLong(4, deviceOperation.getReceivedDate());
			stmt.setString(5, deviceOperation.getStatus().name());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding device id - '" +
			             deviceOperation.getDeviceId() + " and operation id - " +
			             deviceOperation.getOperationId() +
			             " to mapping table MBL_DEVICE_OPERATION";
			;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateMobileDeviceOperationMapping(MobileDeviceOperationMapping deviceOperation)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_DEVICE_OPERATION_MAPPING SET SENT_DATE = ?, RECEIVED_DATE = ?, STATUS = ? " +
					"WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setLong(1, deviceOperation.getSentDate());
			stmt.setLong(2, deviceOperation.getReceivedDate());
			stmt.setString(3, deviceOperation.getStatus().name());
			stmt.setString(4, deviceOperation.getDeviceId());
			stmt.setInt(5, deviceOperation.getOperationId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating device id - '" +
			             deviceOperation.getDeviceId() + " and operation id - " +
			             deviceOperation.getOperationId() + " in table MBL_DEVICE_OPERATION";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateMobileDeviceOperationMappingToInProgress(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_DEVICE_OPERATION_MAPPING SET SENT_DATE = ?, STATUS = ? " +
					"WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setLong(1, new Date().getTime());
			stmt.setString(2, MobileDeviceOperationMapping.Status.INPROGRESS.name());
			stmt.setString(3, deviceId);
			stmt.setInt(4, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the Status of operation to in-progress of device id - '" +
					deviceId + " and operation id - " +
					operationId + " in table MBL_DEVICE_OPERATION";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateMobileDeviceOperationMappingToCompleted(String deviceId,
	                                                             int operationId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_DEVICE_OPERATION_MAPPING SET RECEIVED_DATE = ?, STATUS = ? " +
					"WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setLong(1, new Date().getTime());
			stmt.setString(2, MobileDeviceOperationMapping.Status.COMPLETED.name());
			stmt.setString(3, deviceId);
			stmt.setInt(4, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the Status of operation to completed of device id - '" +
					deviceId + " and operation id - " +
					operationId + " in table MBL_DEVICE_OPERATION";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteMobileDeviceOperationMapping(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, deviceId);
			stmt.setInt(2, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while deleting the table entry MBL_DEVICE_OPERATION with " +
					" device id - '" + deviceId + " and operation id - " + operationId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public MobileDeviceOperationMapping getMobileDeviceOperationMapping(String deviceId,
	                                                                    int operationId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileDeviceOperationMapping mblDeviceOperation = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE, STATUS FROM " +
					"MBL_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ? AND OPERATION_ID=?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, deviceId);
			stmt.setInt(2, operationId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mblDeviceOperation = new MobileDeviceOperationMapping();
				mblDeviceOperation.setDeviceId(resultSet.getString(1));
				mblDeviceOperation.setOperationId(resultSet.getInt(2));
				mblDeviceOperation.setSentDate(resultSet.getInt(3));
				mblDeviceOperation.setReceivedDate(resultSet.getInt(4));
				mblDeviceOperation.setStatus(resultSet.getString(5));
				break;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching table MBL_DEVICE_OPERATION entry with device id - '" +
					deviceId + " and operation id - " + operationId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mblDeviceOperation;
	}

	@Override
	public List<MobileDeviceOperationMapping> getAllMobileDeviceOperationNappingsOfDevice(
			String deviceId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileDeviceOperationMapping mblDeviceOperation = null;
		List<MobileDeviceOperationMapping> mblDeviceOperations = new ArrayList<MobileDeviceOperationMapping>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE, STATUS FROM " +
					"MBL_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, deviceId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mblDeviceOperation = new MobileDeviceOperationMapping();
				mblDeviceOperation.setDeviceId(resultSet.getString(1));
				mblDeviceOperation.setOperationId(resultSet.getInt(2));
				mblDeviceOperation.setSentDate(resultSet.getInt(3));
				mblDeviceOperation.setReceivedDate(resultSet.getInt(4));
				mblDeviceOperation.setStatus(resultSet.getString(5));
				mblDeviceOperations.add(mblDeviceOperation);
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching mapping table MBL_DEVICE_OPERATION entries of " +
					"device id - '" + deviceId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mblDeviceOperations;
	}

	@Override
	public List<MobileDeviceOperationMapping> getAllPendingOperationMappingsOfMobileDevice(
			String deviceId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileDeviceOperationMapping mblDeviceOperation = null;
		List<MobileDeviceOperationMapping> mblDeviceOperations = new ArrayList<MobileDeviceOperationMapping>();
		try {

			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE, STATUS FROM" +
					" MBL_DEVICE_OPERATION_MAPPING WHERE DEVICE_ID = ? AND STATUS = 'NEW'";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, deviceId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mblDeviceOperation = new MobileDeviceOperationMapping();
				mblDeviceOperation.setDeviceId(resultSet.getString(1));
				mblDeviceOperation.setOperationId(resultSet.getInt(2));
				mblDeviceOperation.setSentDate(resultSet.getInt(3));
				mblDeviceOperation.setReceivedDate(resultSet.getInt(4));
				mblDeviceOperation.setStatus(resultSet.getString(5));
				mblDeviceOperations.add(mblDeviceOperation);
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching mapping table MBL_DEVICE_OPERATION entries of" +
					" device id - '" + deviceId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mblDeviceOperations;
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
