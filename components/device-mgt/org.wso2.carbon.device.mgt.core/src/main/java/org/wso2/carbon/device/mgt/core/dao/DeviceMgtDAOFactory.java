/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.device.mgt.core.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.core.config.datasource.DataSourceConfig;
import org.wso2.carbon.device.mgt.core.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.core.config.datasource.RDBMSConfig;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;

import javax.sql.DataSource;
import java.util.Hashtable;
import java.util.List;

/**
 * DAO factory class for creating RSS DAO objects.
 */
public class DeviceMgtDAOFactory {

	private static final Log log = LogFactory.getLog(DeviceMgtDAOFactory.class);

	/**
	 * Resolve data source from the data source definition
	 *
	 * @param dataSourceDef data source configuration
	 * @return data source resolved from the data source definition
	 */
	public static DataSource resolveDataSource(DataSourceConfig dataSourceDef) {
		DataSource dataSource;
		if (dataSourceDef == null) {
			throw new RuntimeException("RSS Management Repository data source configuration " +
			                           "is null and thus, is not initialized");
		}
		JNDILookupDefinition jndiConfig = dataSourceDef.getJndiLookupDefintion();
		if (jndiConfig != null) {
			if (log.isDebugEnabled()) {
				log.debug("Initializing RSS Management Repository data source using the JNDI " +
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
						DeviceManagerUtil.lookupDataSource(jndiConfig.getJndiName(), jndiProperties);
			} else {
				dataSource = DeviceManagerUtil.lookupDataSource(jndiConfig.getJndiName(), null);
			}
		} else {
			if (log.isDebugEnabled()) {
				log.debug("No JNDI Lookup Definition found in the RSS Management Repository " +
				          "data source configuration. Thus, continuing with in-line data source " +
				          "configuration processing.");
			}
			RDBMSConfig rdbmsConfig = dataSourceDef.getRdbmsConfiguration();
			if (rdbmsConfig == null) {
				throw new RuntimeException(
						"No JNDI/In-line data source configuration found. " +
						"Thus, RSS Management Repository DAO is not initialized"
				);
			}
			dataSource =
                    DeviceManagerUtil.createDataSource(DeviceManagerUtil.loadDataSourceProperties(
							rdbmsConfig), rdbmsConfig.getDataSourceClassName());
		}
		return dataSource;
	}

}
