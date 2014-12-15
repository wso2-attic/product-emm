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

package org.wso2.carbon.device.mgt.mobile.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.mobile.impl.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.mobile.impl.config.datasource.MobileDataSourceConfig;
import org.wso2.carbon.device.mgt.mobile.impl.dao.util.MobileDeviceManagementDAOUtil;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by harshan on 12/15/14.
 */
public class MobileDeviceManagerUtil {

	private static final Log log = LogFactory.getLog(MobileDeviceManagerUtil.class);

	public static Document convertToDocument(File file) throws DeviceManagementException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		try {
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			return docBuilder.parse(file);
		} catch (Exception e) {
			throw new DeviceManagementException(
					"Error occurred while parsing file, while converting " +
					"to a org.w3c.dom.Document : " + e.getMessage(), e);
		}
	}

	/**
	 * Resolve data source from the data source definition
	 *
	 * @param config data source configuration
	 * @return data source resolved from the data source definition
	 */
	public static DataSource resolveDataSource(MobileDataSourceConfig config) {
		DataSource dataSource = null;
		if (config == null) {
			throw new RuntimeException(
					"Mobile Device Management Repository data source configuration " +
					"is null and thus, is not initialized");
		}
		JNDILookupDefinition jndiConfig = config.getJndiLookupDefintion();
		if (jndiConfig != null) {
			if (log.isDebugEnabled()) {
				log.debug(
						"Initializing Mobile Device Management Repository data source using the JNDI " +
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
						MobileDeviceManagementDAOUtil.lookupDataSource(jndiConfig.getJndiName(),
						                                               jndiProperties);
			} else {
				dataSource = MobileDeviceManagementDAOUtil
						.lookupDataSource(jndiConfig.getJndiName(), null);
			}
		}
		return dataSource;
	}
}
