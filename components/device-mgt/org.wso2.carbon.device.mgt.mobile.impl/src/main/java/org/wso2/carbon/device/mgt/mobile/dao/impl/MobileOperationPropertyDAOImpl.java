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
import org.wso2.carbon.device.mgt.mobile.dao.MobileOperationPropertyDAO;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperation;
import org.wso2.carbon.device.mgt.mobile.dto.MobileOperationProperty;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MobileOperationPropertyDAO.
 */
public class MobileOperationPropertyDAOImpl implements MobileOperationPropertyDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileOperationPropertyDAOImpl.class);

	public MobileOperationPropertyDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addMobileOperationProperty(MobileOperationProperty operationProperty)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_OPERATION_PROPERTY(OPERATION_ID, PROPERTY, VALUE) VALUES ( ?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setInt(1, operationProperty.getOperationId());
			stmt.setString(2, operationProperty.getProperty());
			stmt.setString(3, operationProperty.getValue());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while adding mobile operation property to MBL_OPERATION_PROPERTY table";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateMobileOperationProperty(
			MobileOperationProperty operationProperty)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"UPDATE MBL_OPERATION_PROPERTY SET VALUE = ? WHERE OPERATION_ID = ? AND PROPERTY = ?";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, operationProperty.getValue());
			stmt.setInt(2, operationProperty.getOperationId());
			stmt.setString(3, operationProperty.getProperty());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while updating the mobile operation property in MBL_OPERATION_PROPERTY table.";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteMobileOperationProperties(int operationId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_OPERATION_PROPERTY WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setInt(1, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while deleting MBL_OPERATION_PROPERTY entry with operation Id - ";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public MobileOperationProperty getMobileOperationProperty(int operationId,
	                                                          String property)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileOperationProperty mobileOperationProperty = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, PROPERTY, VALUE FROM MBL_OPERATION_PROPERTY WHERE OPERATION_ID = ? AND PROPERTY = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, operationId);
			stmt.setString(2, property);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileOperationProperty = new MobileOperationProperty();
				mobileOperationProperty.setOperationId(resultSet.getInt(1));
				mobileOperationProperty.setProperty(resultSet.getString(2));
				mobileOperationProperty.setValue(resultSet.getString(3));
				break;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching the mobile operation property of Operation_id : " +
					operationId + " and Property : " + property;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mobileOperationProperty;
	}

	@Override
	public List<MobileOperationProperty> getAllMobileOperationPropertiesOfOperation(
			int operationId) throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileOperationProperty mobileOperationProperty = null;
		List<MobileOperationProperty> properties = new ArrayList<MobileOperationProperty>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, PROPERTY, VALUE FROM MBL_OPERATION_PROPERTY WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, operationId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileOperationProperty = new MobileOperationProperty();
				mobileOperationProperty.setOperationId(resultSet.getInt(1));
				mobileOperationProperty.setProperty(resultSet.getString(2));
				mobileOperationProperty.setValue(resultSet.getString(3));
				properties.add(mobileOperationProperty);
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching the mobile operation properties of Operation_id " +
					operationId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return properties;
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
