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
 * Implementation of FeatureDAO
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
					"INSERT INTO MBL_FEATURES(CODE, NAME, DESCRIPTION) VALUES (?, ?, ?)";

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
			             feature.getCode() + "'";
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
					"UPDATE MBL_FEATURES SET CODE = ?, NAME = ?, DESCRIPTION = ? WHERE FEATURE_ID = ?";
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
	public boolean deleteDevice(String featureCode)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_FEATURES WHERE FEATURE_ID = ?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, featureCode);
			int rows = stmt.executeUpdate();
			if(rows>0){
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
	public Feature getFeature(String featureCode)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Feature feature = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT FEATURE_ID, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE FEATURE_ID = ?";
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
	public List<Feature> getAllFeatures() throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Feature feature;
		List<Feature> features=new ArrayList<Feature>();
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
			String msg = "Error occurred while obtaining a connection from the mobile device " +
			             "management metadata repository datasource.";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		}
	}
}
