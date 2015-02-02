/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.MobileFeatureDAO;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.MobileFeature;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of MobileFeatureDAO.
 */
public class MobileFeatureDAOImpl implements MobileFeatureDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileFeatureDAOImpl.class);

	public MobileFeatureDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addMobileFeature(MobileFeature mobileFeature) throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_FEATURE(CODE, NAME, DESCRIPTION, DEVICE_TYPE) VALUES (?, ?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, mobileFeature.getCode());
			stmt.setString(2, mobileFeature.getName());
			stmt.setString(3, mobileFeature.getDescription());
			stmt.setString(4, mobileFeature.getDeviceType());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding feature code - '" +
			             mobileFeature.getCode() + "' to feature table";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateMobileFeature(MobileFeature mobileFeature)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_FEATURE SET CODE = ?, NAME = ?, DESCRIPTION = ?, DEVICE_TYPE = ? WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setString(1, mobileFeature.getCode());
			stmt.setString(2, mobileFeature.getName());
			stmt.setString(3, mobileFeature.getDescription());
			stmt.setString(4, mobileFeature.getDeviceType());
			stmt.setInt(5, mobileFeature.getId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating the feature with feature code - '" +
			             mobileFeature.getId() + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteMobileFeatureByCode(String featureCode)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_FEATURE WHERE CODE = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, featureCode);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while deleting feature with code - " + featureCode;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteMobileFeatureById(int featureId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_FEATURE WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setInt(1, featureId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while deleting feature with id - " + featureId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public MobileFeature getMobileFeatureByCode(String featureCode)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileFeature mobileFeature = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID,DEVICE_TYPE, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE CODE = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, featureCode);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileFeature = new MobileFeature();
				mobileFeature.setId(resultSet.getInt(1));
				mobileFeature.setDeviceType(resultSet.getString(2));
				mobileFeature.setCode(resultSet.getString(3));
				mobileFeature.setName(resultSet.getString(4));
				mobileFeature.setDescription(resultSet.getString(5));
				break;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while fetching feature code - '" +
			             featureCode + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mobileFeature;
	}

	@Override
	public MobileFeature getMobileFeatureById(int featureID)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileFeature mobileFeature = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID,DEVICE_TYPE, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, featureID);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileFeature = new MobileFeature();
				mobileFeature.setId(resultSet.getInt(1));
				mobileFeature.setDeviceType(resultSet.getString(2));
				mobileFeature.setCode(resultSet.getString(3));
				mobileFeature.setName(resultSet.getString(4));
				mobileFeature.setDescription(resultSet.getString(5));
				break;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while fetching feature id - '" +
			             featureID + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return mobileFeature;
	}

	@Override
	public List<MobileFeature> getAllMobileFeatures() throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileFeature mobileFeature;
		List<MobileFeature> mobileFeatures = new ArrayList<MobileFeature>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID,DEVICE_TYPE, CODE, NAME, DESCRIPTION FROM MBL_FEATURE";
			stmt = conn.prepareStatement(selectDBQuery);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileFeature = new MobileFeature();
				mobileFeature.setId(resultSet.getInt(1));
				mobileFeature.setDeviceType(resultSet.getString(2));
				mobileFeature.setCode(resultSet.getString(3));
				mobileFeature.setName(resultSet.getString(4));
				mobileFeature.setDescription(resultSet.getString(5));
				mobileFeatures.add(mobileFeature);
			}
			return mobileFeatures;
		} catch (SQLException e) {
			String msg = "Error occurred while fetching all features.'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
	}

	@Override
	public List<MobileFeature> getMobileFeatureByDeviceType(String deviceType) throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		MobileFeature mobileFeature;
		List<MobileFeature> mobileFeatures = new ArrayList<MobileFeature>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID, DEVICE_TYPE, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE DEVICE_TYPE = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, deviceType);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				mobileFeature = new MobileFeature();
				mobileFeature.setId(resultSet.getInt(1));
				mobileFeature.setDeviceType(resultSet.getString(2));
				mobileFeature.setCode(resultSet.getString(3));
				mobileFeature.setName(resultSet.getString(4));
				mobileFeature.setDescription(resultSet.getString(5));
				mobileFeatures.add(mobileFeature);
			}
			return mobileFeatures;
		} catch (SQLException e) {
			String msg = "Error occurred while fetching all features.'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		}finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
	}

	private Connection getConnection() throws MobileDeviceManagementDAOException {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			String msg = "Error occurred while obtaining a connection from the mobile specific " +
			             "datasource.";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		}
	}
}
