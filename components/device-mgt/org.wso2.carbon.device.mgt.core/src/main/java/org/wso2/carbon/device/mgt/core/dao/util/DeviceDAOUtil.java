/*
 *  Copyright (c) 2013, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */

package org.wso2.carbon.device.mgt.core.dao.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.base.MultitenantConstants;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.internal.DeviceManagerDataHolder;
import org.wso2.carbon.user.api.TenantManager;
import org.wso2.carbon.user.api.UserStoreException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Util class for RSS DAO operations
 */
public class DeviceDAOUtil {
	private static final Log log = LogFactory.getLog(DeviceDAOUtil.class);

	/**
	 * Clean up database resources
	 * @param resultSet result set to be closed
	 * @param statement prepared statement to be closed
	 * @param conn connection to be closed
	 * @param task occurred when clean up the resources
	 */
	public static synchronized void cleanupResources(ResultSet resultSet, PreparedStatement statement,
	                                                 Connection conn, String task) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				log.error("Error occurred while closing the result set " + task, e);
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				log.error("Error occurred while closing the statement " + task, e);
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				log.error("Error occurred while closing the connection "+ task, e);
			}
		}
	}

	/**
	 * Roll back database updates on error
	 *
	 * @param connection database connection
	 * @param task       task which was executing at the error.
	 */
	public static synchronized void rollback(Connection connection, String task) {
		if (connection != null) {
			try {
				connection.rollback();
			} catch (SQLException e) {
				log.error("Rollback failed on " + task, e);
			}
		}
	}

	public synchronized static int getTenantId() throws DeviceManagementException {
		CarbonContext ctx = CarbonContext.getThreadLocalCarbonContext();
		int tenantId = ctx.getTenantId();
		if (tenantId != MultitenantConstants.INVALID_TENANT_ID) {
			return tenantId;
		}
		String tenantDomain = ctx.getTenantDomain();
		if (null != tenantDomain) {
			try {
				TenantManager tenantManager = DeviceManagerDataHolder.getInstance().getTenantManager();
				tenantId = tenantManager.getTenantId(tenantDomain);
			} catch (UserStoreException e) {
				throw new DeviceManagementException("Error while retrieving the tenant Id for " +
				                                    "tenant domain : " + tenantDomain, e);
			}
		}
		return tenantId;
	}

	public static synchronized int getTenantId(String tenantDomain) throws DeviceManagementException {
		int tenantId = MultitenantConstants.INVALID_TENANT_ID;
		if (null != tenantDomain) {
			try {
				TenantManager tenantManager = DeviceManagerDataHolder.getInstance().getTenantManager();
				tenantId = tenantManager.getTenantId(tenantDomain);
			} catch (UserStoreException e) {
				throw new DeviceManagementException("Error while retrieving the tenant Id for " +
				                                    "tenant domain : " + tenantDomain, e);
			}
		}
		return tenantId;
	}
}
