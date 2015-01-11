package org.wso2.carbon.device.mgt.mobile.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.OperationPropertyDAO;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.mobile.dto.OperationProperty;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of OperationPropertyDAO
 */
public class OperationPropertyDAOImpl implements OperationPropertyDAO {

	private DataSource dataSource;
	private static final Log log = LogFactory.getLog(OperationPropertyDAOImpl.class);

	public OperationPropertyDAOImpl(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean addOperationProperty(OperationProperty operationProperty)
			throws MobileDeviceManagementDAOException {
		boolean status = false;
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = this.getConnection();
			String createDBQuery =
					"INSERT INTO MBL_OPERATION_PROPERTY(OPERATION_ID, PROPERTY_ID, VALUE) VALUES ( ?, ?, ?)";

			stmt = conn.prepareStatement(createDBQuery);
			stmt.setInt(1, operationProperty.getOperationId());
			stmt.setInt(2, operationProperty.getPropertyId());
			stmt.setString(3, operationProperty.getValue());
			int rows = stmt.executeUpdate();
			if (rows > 0) {
				status = true;
			}
		} catch (SQLException e) {
			String msg = "Error occurred while adding feature property to operation property table";
			log.error(msg, e);
			throw new MobileDeviceManagementDAOException(msg, e);
		} finally {
			MobileDeviceManagementDAOUtil.cleanupResources(conn, stmt, null);
		}
		return status;
	}

	@Override public boolean updateOperationProperty(OperationProperty operationProperty)
			throws MobileDeviceManagementDAOException {
		return false;
	}

	@Override public boolean deleteOperationProperty(int operationPropertyId)
			throws MobileDeviceManagementDAOException {
		return false;
	}

	@Override public OperationProperty getOperationProperty(String deviceId, int operationId)
			throws MobileDeviceManagementDAOException {
		return null;
	}

	@Override public List<OperationProperty> getAllDeviceOperationOfDevice(String deviceId)
			throws MobileDeviceManagementDAOException {
		return null;
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
