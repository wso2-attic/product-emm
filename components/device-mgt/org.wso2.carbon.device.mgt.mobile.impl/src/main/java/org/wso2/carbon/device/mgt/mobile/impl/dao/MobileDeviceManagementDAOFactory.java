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

package org.wso2.carbon.device.mgt.mobile.impl.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.mobile.impl.config.datasource.MobileDataSourceConfig;
import org.wso2.carbon.device.mgt.mobile.impl.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileDeviceDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileDeviceModelDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileDeviceVendorDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.impl.MobileOSVersionDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.dao.util.MobileDeviceManagementDAOUtil;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.List;

/**
 * Factory class used to create MobileDeviceManagement related DAO objects.
 */
public class MobileDeviceManagementDAOFactory {

	private static DataSource dataSource;
	private static final Log log = LogFactory.getLog(MobileDeviceManagementDAOFactory.class);

	public static MobileDeviceDAO getMobileDeviceDAO() {
		return new MobileDeviceDAOImpl(dataSource);
	}

	public static MobileDeviceModelDAO getMobileDeviceModelDAO() {
		return new MobileDeviceModelDAOImpl(dataSource);
	}

	public static MobileDeviceVendorDAO getMobileDeviceVendorDAO() {
		return new MobileDeviceVendorDAOImpl(dataSource);
	}

	public static MobileOSVersionDAO getMobileOSVersionDAO() {
		return new MobileOSVersionDAOImpl(dataSource);
	}

	public static void init(MobileDataSourceConfig config) {
		dataSource = resolveDataSource(config);
	}

	public static void init(DataSource dtSource) {
		dataSource = dtSource;
	}

	/**
	 * Resolve data source from the data source definition
	 *
	 * @param config data source configuration
	 * @return data source resolved from the data source definition
	 */
	private static DataSource resolveDataSource(MobileDataSourceConfig config) {
		DataSource dataSource = null;
		if (config == null) {
			throw new RuntimeException("Device Management Repository data source configuration " +
			                           "is null and thus, is not initialized");
		}
		JNDILookupDefinition jndiConfig = config.getJndiLookupDefintion();
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

	public static DataSource getDataSource() {
		return dataSource;
	}
}
