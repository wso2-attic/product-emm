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
import org.wso2.carbon.device.mgt.mobile.dao.MobileFeaturePropertyDAO;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.MobileFeatureProperty;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MobileFeaturePropertyDAO.
 */
public class MobileFeaturePropertyDAOImpl implements MobileFeaturePropertyDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileFeaturePropertyDAOImpl.class);

	public MobileFeaturePropertyDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addFeatureProperty(MobileFeatureProperty mobileFeatureProperty)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_FEATURE_PROPERTY(PROPERTY, FEATURE_ID) VALUES (?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, mobileFeatureProperty.getProperty());
			stmt.setInt(2, mobileFeatureProperty.getFeatureID());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding property id - '" +
			             mobileFeatureProperty.getFeatureID() + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateFeatureProperty(MobileFeatureProperty mobileFeatureProperty)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_FEATURE_PROPERTY SET FEATURE_ID = ? WHERE PROPERTY = ?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setInt(1, mobileFeatureProperty.getFeatureID());
			stmt.setString(2, mobileFeatureProperty.getProperty());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating the feature property with property - '" +
			             mobileFeatureProperty.getProperty() + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteFeatureProperty(String property)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_FEATURE_PROPERTY WHERE PROPERTY = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, property);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while deleting feature property with property - " +
			             property;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public MobileFeatureProperty getFeatureProperty(String property)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileFeatureProperty mobileFeatureProperty = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT PROPERTY, FEATURE_ID FROM MBL_FEATURE_PROPERTY WHERE PROPERTY = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, property);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileFeatureProperty = new MobileFeatureProperty();
				mobileFeatureProperty.setProperty(resultSet.getString(1));
				mobileFeatureProperty.setFeatureID(resultSet.getInt(2));
				break;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while fetching property - '" +
			             property + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mobileFeatureProperty;
	}

	@Override
	public List<MobileFeatureProperty> getFeaturePropertyOfFeature(Integer featureId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileFeatureProperty mobileFeatureProperty = null;
		List<MobileFeatureProperty> FeatureProperties = new ArrayList<MobileFeatureProperty>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT PROPERTY, FEATURE_ID FROM MBL_FEATURE_PROPERTY WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, featureId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileFeatureProperty = new MobileFeatureProperty();
				mobileFeatureProperty.setProperty(resultSet.getString(1));
				mobileFeatureProperty.setFeatureID(resultSet.getInt(2));
				FeatureProperties.add(mobileFeatureProperty);
			}
			return FeatureProperties;
		} catch (SQLException e) {
			String msg = "Error occurred while fetching all feature property.'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
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
