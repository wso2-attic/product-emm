package org.wso2.carbon.device.mgt.mobile.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.OperationDAO;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.Operation;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implementation of OperationDAO
 */
public class OperationDAOImpl implements OperationDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(OperationDAOImpl.class);

	public OperationDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public int addOperation(Operation operation)
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
			stmt.setInt(2, operation.getCreatedDate());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				ResultSet rs = stmt.getGeneratedKeys();
				if (rs != null && rs.next()) {
					status = rs.getInt(1);
				}
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding feature code - '" +
			             operation.getFeatureCode() + "' to operations table";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateOperation(Operation operation)
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
			stmt.setInt(2, operation.getCreatedDate());
			stmt.setInt(3, operation.getOperationId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating the operation with operation id - '" +
			             operation.getOperationId() + "'";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteOperation(int operationId)
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
			String msg = "Error occurred while deleting operation with operation Id - ";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public Operation getOperation(int operationId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Operation operation = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT OPERATION_ID, FEATURE_CODE, CREATED_DATE FROM MBL_OPERATION WHERE OPERATION_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setInt(1, operation.getOperationId());
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				operation = new Operation();
				operation.setOperationId(resultSet.getInt(1));
				break;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while fetching operationId - '" +
			             operationId + "'";
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
