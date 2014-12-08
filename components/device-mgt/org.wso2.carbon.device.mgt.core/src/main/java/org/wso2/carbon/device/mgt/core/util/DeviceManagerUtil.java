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

package org.wso2.carbon.device.mgt.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.config.datasource.RDBMSConfig;
import org.wso2.carbon.ndatasource.common.DataSourceException;
import org.wso2.carbon.ndatasource.rdbms.RDBMSConfiguration;
import org.wso2.carbon.ndatasource.rdbms.RDBMSDataSource;
import org.wso2.securevault.SecretResolver;

import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class DeviceManagerUtil {

    private static final Log log = LogFactory.getLog(DeviceManagerUtil.class);
    private static SecretResolver secretResolver;
    private static String jndiDataSourceName;
    private static DataSource dataSource;
    private static final String DEFAULT_PRIVILEGE_TEMPLATE_NAME = "CRUD_PRIVILEGES_DEFAULT";

    /**
     * Construct document from file resource
     *
     * @param file object
     * @return Document
     * @throws org.wso2.carbon.device.mgt.common.DeviceManagementException if error occurred constructing document from file resource
     */
    public static Document convertToDocument(File file) throws DeviceManagementException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            return docBuilder.parse(file);
        } catch (Exception e) {
            throw new DeviceManagementException("Error occurred while parsing file, while converting " +
                    "to a org.w3c.dom.Document : " + e.getMessage(), e);
        }
    }

    public static DataSource lookupDataSource(String dataSourceName, final Hashtable<Object, Object> jndiProperties) {
        try {
            if (jndiProperties == null || jndiProperties.isEmpty()) {
                return (DataSource) InitialContext.doLookup(dataSourceName);
            }
            final InitialContext context = new InitialContext(jndiProperties);
            return (DataSource) context.doLookup(dataSourceName);
        } catch (Exception e) {
            throw new RuntimeException("Error in looking up data source: " + e.getMessage(), e);
        }
    }

    public static void setJndiDataSourceName(String jndiDataSourceName) {
        DeviceManagerUtil.jndiDataSourceName = jndiDataSourceName;
    }

    public static void setDataSource(DataSource dataSource) {
        DeviceManagerUtil.dataSource = dataSource;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static Properties loadDataSourceProperties(RDBMSConfig config) {
        Properties props = new Properties();
        List<RDBMSConfig.DataSourceProperty> dsProps = config.getDataSourceProps();
        for (RDBMSConfig.DataSourceProperty dsProp : dsProps) {
            props.setProperty(dsProp.getName(), dsProp.getValue());
        }
        return props;
    }

    /**
     * Create data source from properties
     *
     * @param properties          set of data source properties
     * @param dataSourceClassName data source class name
     * @return DataSource
     */
    public static DataSource createDataSource(Properties properties, String dataSourceClassName) {
        RDBMSConfiguration config = new RDBMSConfiguration();
        config.setDataSourceClassName(dataSourceClassName);
        List<RDBMSConfiguration.DataSourceProperty> dsProps = new ArrayList<RDBMSConfiguration.DataSourceProperty>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            RDBMSConfiguration.DataSourceProperty property =
                    new RDBMSConfiguration.DataSourceProperty();
            property.setName((String) entry.getKey());
            property.setValue((String) entry.getValue());
            dsProps.add(property);
        }
        config.setDataSourceProps(dsProps);
        return createDataSource(config);
    }

    public static DataSource createDataSource(RDBMSConfiguration config) {
        try {
            RDBMSDataSource dataSource = new RDBMSDataSource(config);
            return dataSource.getDataSource();
        } catch (DataSourceException e) {
            throw new RuntimeException("Error in creating data source: " + e.getMessage(), e);
        }
    }
}
