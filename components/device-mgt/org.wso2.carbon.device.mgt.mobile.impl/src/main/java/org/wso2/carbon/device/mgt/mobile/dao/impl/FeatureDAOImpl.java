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
import org.wso2.carbon.device.mgt.mobile.dao.FeatureDAO;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.Feature;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of FeatureDAO.
 */
public class FeatureDAOImpl implements FeatureDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(FeatureDAOImpl.class);

	public FeatureDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addFeature(Feature feature) throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_FEATURE(CODE, NAME, DESCRIPTION) VALUES (?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, feature.getCode());
			stmt.setString(2, feature.getName());
			stmt.setString(3, feature.getDescription());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding feature code - '" +
			             feature.getCode() + "' to feature table";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateFeature(Feature feature)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_FEATURE SET CODE = ?, NAME = ?, DESCRIPTION = ? WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setString(1, feature.getCode());
			stmt.setString(2, feature.getName());
			stmt.setString(3, feature.getDescription());
			stmt.setInt(4, feature.getId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating the feature with feature code - '" +
			             feature.getId() + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteFeatureByCode(String featureCode)
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
	public boolean deleteFeatureById(String featureId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_FEATURE WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, featureId);
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
	public Feature getFeatureByCode(String featureCode)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Feature feature = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE CODE = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, featureCode);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				feature = new Feature();
				feature.setId(resultSet.getInt(1));
				feature.setCode(resultSet.getString(2));
				feature.setName(resultSet.getString(3));
				feature.setDescription(resultSet.getString(4));
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
		return feature;
	}

	@Override
	public Feature getFeatureById(String featureID)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Feature feature = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, featureID);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				feature = new Feature();
				feature.setId(resultSet.getInt(1));
				feature.setCode(resultSet.getString(2));
				feature.setName(resultSet.getString(3));
				feature.setDescription(resultSet.getString(4));
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
		return feature;
	}

	@Override
	public List<Feature> getAllFeatures() throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Feature feature;
		List<Feature> features = new ArrayList<Feature>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID, CODE, NAME, DESCRIPTION FROM MBL_FEATURE";
			stmt = conn.prepareStatement(selectDBQuery);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				feature = new Feature();
				feature.setId(resultSet.getInt(1));
				feature.setCode(resultSet.getString(2));
				feature.setName(resultSet.getString(3));
				feature.setDescription(resultSet.getString(4));
				features.add(feature);
			}
			return features;
		} catch (SQLException e) {
			String msg = "Error occurred while fetching all features.'";
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
			String msg = "Error occurred while obtaining a connection from the mobile specific " +
			             "datasource.";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		}
	}
}
