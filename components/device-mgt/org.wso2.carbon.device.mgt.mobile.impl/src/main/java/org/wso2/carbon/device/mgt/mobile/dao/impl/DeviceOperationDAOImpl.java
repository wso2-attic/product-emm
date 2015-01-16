package org.wso2.carbon.device.mgt.mobile.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.dao.DeviceOperationDAO;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.DeviceOperation;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of DeviceOperationDAO
 */
public class DeviceOperationDAOImpl implements DeviceOperationDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(DeviceOperationDAOImpl.class);

	public DeviceOperationDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addDeviceOperation(DeviceOperation deviceOperation)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_DEVICE_OPERATION(DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE) VALUES (?, ?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setString(1, deviceOperation.getDeviceId());
			stmt.setInt(2, deviceOperation.getOperationId());
			stmt.setInt(3, deviceOperation.getSentDate());
			stmt.setInt(4, deviceOperation.getReceivedDate());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding device id - '" +
			             deviceOperation.getDeviceId() + " and operation id - " +
			             deviceOperation.getOperationId() + "of mapping table MBL_DEVICE_OPERATION";
			;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean updateDeviceOperation(DeviceOperation deviceOperation)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String updateDBQuery =
					"UPDATE MBL_DEVICE_OPERATION SET SENT_DATE = ?, RECEIVED_DATE = ? WHERE DEVICE_ID = ? and OPERATION_ID=?";
			stmt = conn.prepareStatement(updateDBQuery);
			stmt.setInt(1, deviceOperation.getSentDate());
			stmt.setInt(2, deviceOperation.getReceivedDate());
			stmt.setString(3, deviceOperation.getDeviceId());
			stmt.setInt(4, deviceOperation.getOperationId());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while updating device id - '" +
			             deviceOperation.getDeviceId() + " and operation id - " +
			             deviceOperation.getOperationId() + "of mapping table MBL_DEVICE_OPERATION";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public boolean deleteDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String deleteDBQuery =
					"DELETE FROM MBL_DEVICE_OPERATION WHERE DEVICE_ID = ? and OPERATION_ID=?";
			stmt = conn.prepareStatement(deleteDBQuery);
			stmt.setString(1, deviceId);
			stmt.setInt(2, operationId);
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while deleting mapping table MBL_DEVICE_OPERATION with  device id - '" +
					deviceId + " and operation id - " + operationId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override
	public DeviceOperation getDeviceOperation(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		DeviceOperation deviceOperation = null;
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE FROM MBL_DEVICE_OPERATION WHERE DEVICE_ID = ? and OPERATION_ID=?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, deviceId);
			stmt.setInt(2, operationId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				deviceOperation = new DeviceOperation();
				deviceOperation.setDeviceId(resultSet.getString(1));
				deviceOperation.setOperationId(resultSet.getInt(2));
				deviceOperation.setSentDate(resultSet.getInt(3));
				deviceOperation.setReceivedDate(resultSet.getInt(4));
				break;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching mapping table MBL_DEVICE_OPERATION entry with device id - '" +
					deviceId + " and operation id - " + operationId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return deviceOperation;
	}

	@Override
	public List<DeviceOperation> getAllDeviceOperationOfDevice(String deviceId)
			throws MobileDeviceManagementDAOException {
		Connection conn = null;
		PreparedStatement stmt = null;
		DeviceOperation deviceOperation = null;
		List<DeviceOperation> deviceOperations = new ArrayList<DeviceOperation>();
		try {
			conn = this.getConnection();
			String selectDBQuery =
					"SELECT DEVICE_ID, OPERATION_ID, SENT_DATE, RECEIVED_DATE FROM MBL_DEVICE_OPERATION WHERE DEVICE_ID = ?";
			stmt = conn.prepareStatement(selectDBQuery);
			stmt.setString(1, deviceId);
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				deviceOperation = new DeviceOperation();
				deviceOperation.setDeviceId(resultSet.getString(1));
				deviceOperation.setOperationId(resultSet.getInt(2));
				deviceOperation.setSentDate(resultSet.getInt(3));
				deviceOperation.setReceivedDate(resultSet.getInt(4));
				deviceOperations.add(deviceOperation);
				break;
			}
		} catch (SQLException e) {
			String msg =
					"Error occurred while fetching mapping table MBL_DEVICE_OPERATION entry with device id - '" +
					deviceId;
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return deviceOperations;
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
