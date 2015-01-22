/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.mobile.config.datasource.MobileDataSourceConfig;
import org.wso2.carbon.device.mgt.mobile.dao.impl.*;
import org.wso2.carbon.device.mgt.mobile.dao.util.MobileDeviceManagementDAOUtil;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.List;

/**
 * Factory class used to create MobileDeviceManagement related DAO objects.
 */
public class MobileDeviceManagementDAOFactory {

	private static DataSource dataSource;
	private static MobileDataSourceConfig mobileDataSourceConfig;
	private static final Log log = LogFactory.getLog(MobileDeviceManagementDAOFactory.class);

	public MobileDeviceManagementDAOFactory() {

	}

	public static void init() {
		try {
			dataSource = MobileDeviceManagementDAOFactory.resolveDataSource(mobileDataSourceConfig);
		} catch (DeviceManagementException e) {
			log.error("Exception occurred while initializing the mobile datasource.",e);
		}
	}

	/**
	 * Resolve data source from the data source definition.
	 *
	 * @param config Mobile data source configuration
	 * @return data source resolved from the data source definition
	 */
	private static DataSource resolveDataSource(MobileDataSourceConfig config)
			throws DeviceManagementException {
		DataSource dataSource = null;
		if (config == null) {
			throw new RuntimeException("Device Management Repository data source configuration " +
			                           "is null and thus, is not initialized");
		}
		JNDILookupDefinition jndiConfig = config.getJndiLookupDefinition();
		if (jndiConfig != null) {
			if (log.isDebugEnabled()) {
				log.debug("Initializing Device Management Repository data source using the JNDI " +
				          "Lookup Definition");
			}
			List<JNDILookupDefinition.JNDIProperty> jndiPropertyList =
					jndiConfig.getJndiProperties();
			if (jndiPropertyList != null) {
				Hashtable<Object, Object> jndiProperties = new Hashtable<Object, Object>();
				for (JNDILookupDefinition.JNDIProperty prop : jndiPropertyList) {
					jndiProperties.put(prop.getName(), prop.getValue());
				}
				dataSource =
						MobileDeviceManagementDAOUtil
								.lookupDataSource(jndiConfig.getJndiName(), jndiProperties);
			} else {
				dataSource = MobileDeviceManagementDAOUtil
						.lookupDataSource(jndiConfig.getJndiName(), null);
			}
		}
		return dataSource;
	}

	public static MobileDeviceDAO getMobileDeviceDAO() {
		return new MobileDeviceDAOImpl(dataSource);
	}

	public static MobileOperationDAO getMobileOperationDAO() {
		return new MobileOperationDAOImpl(dataSource);
	}

	public static MobileOperationPropertyDAO getMobileOperationPropertyDAO() {
		return new MobileOperationPropertyDAOImpl(dataSource);
	}

	public static MobileDeviceOperationDAO getMobileDeviceOperationDAO() {
		return new MobileDeviceOperationDAOImpl(dataSource);
	}

	public static FeatureDAO getFeatureDAO() {
		return new FeatureDAOImpl(dataSource);
	}

	public static FeaturePropertyDAO getFeaturePropertyDAO() {
		return new FeaturePropertyDAOImpl(dataSource);
	}

	public static MobileDataSourceConfig getMobileDeviceManagementConfig() {
		return mobileDataSourceConfig;
	}

	public static void setMobileDataSourceConfig(
			MobileDataSourceConfig mobileDataSourceConfig) {
		MobileDeviceManagementDAOFactory.mobileDataSourceConfig =
				mobileDataSourceConfig;
	}

	public static DataSource getDataSource() {
		return dataSource;
	}
}