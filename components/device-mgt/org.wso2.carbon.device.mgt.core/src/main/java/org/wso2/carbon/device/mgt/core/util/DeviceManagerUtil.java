/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.config.datasource.DataSourceConfig;
import org.wso2.carbon.device.mgt.core.config.datasource.JNDILookupDefinition;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.core.dao.DeviceTypeDAO;
import org.wso2.carbon.device.mgt.core.dao.util.DeviceManagementDAOUtil;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Hashtable;
import java.util.List;

public final class DeviceManagerUtil {

    private static final Log log = LogFactory.getLog(DeviceManagerUtil.class);

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

    /**
     * Resolve data source from the data source definition.
     *
     * @param config data source configuration
     * @return data source resolved from the data source definition
     */
    public static DataSource resolveDataSource(DataSourceConfig config) {
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
                        DeviceManagementDAOUtil.lookupDataSource(jndiConfig.getJndiName(), jndiProperties);
            } else {
                dataSource = DeviceManagementDAOUtil.lookupDataSource(jndiConfig.getJndiName(), null);
            }
        }
        return dataSource;
    }

    /**
     * Adds a new device type to the database if it does not exists.
     *
     * @param deviceType device type
     * @return status of the operation
     */
    public static boolean registerDeviceType(String deviceType) throws DeviceManagementException {
        boolean status;
        try {
            DeviceTypeDAO deviceTypeDAO = DeviceManagementDAOFactory.getDeviceTypeDAO();
            Integer deviceTypeId = deviceTypeDAO.getDeviceTypeIdByDeviceTypeName(deviceType);
            if (deviceTypeId == null) {
                DeviceType dt = new DeviceType();
                dt.setName(deviceType);
                deviceTypeDAO.addDeviceType(dt);
            }
            status = true;
        } catch (DeviceManagementDAOException e) {
            String msg = "Error occurred while registering the device type " + deviceType;
            throw new DeviceManagementException(msg, e);
        }
        return status;
    }

    /**
     * Unregisters an existing device type from the device management metadata repository.
     *
     * @param deviceType device type
     * @return status of the operation
     */
    public static boolean unregisterDeviceType(String deviceType) throws DeviceManagementException {
        try {
            DeviceTypeDAO deviceTypeDAO = DeviceManagementDAOFactory.getDeviceTypeDAO();
            Integer deviceTypeId = deviceTypeDAO.getDeviceTypeIdByDeviceTypeName(deviceType);
            if (deviceTypeId == null) {
                DeviceType dt = new DeviceType();
                dt.setName(deviceType);
                deviceTypeDAO.removeDeviceType(dt);
            }
            return true;
        } catch (DeviceManagementDAOException e) {
            String msg = "Error occurred while registering the device type " + deviceType;
            throw new DeviceManagementException(msg, e);
        }
    }

}
