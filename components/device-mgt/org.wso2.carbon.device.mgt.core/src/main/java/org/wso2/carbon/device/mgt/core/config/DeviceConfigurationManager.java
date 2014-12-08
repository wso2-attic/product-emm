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

package org.wso2.carbon.device.mgt.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.dao.DeviceMgtDAOFactory;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.utils.CarbonUtils;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Class responsible for the rss manager configuration initialization
 */
public class DeviceConfigurationManager {

    private DeviceMgtConfig currentDeviceConfig;
    private static DeviceConfigurationManager deviceConfigManager;

    private static final Log log = LogFactory.getLog(DeviceConfigurationManager.class);
    private final String deviceMgtConfigXMLPath = CarbonUtils.getCarbonConfigDirPath() + File.separator  +
            DeviceManagementConstants.DataSourceProperties.DEVICE_CONFIG_XML_NAME;

    private final String cdmSetupSql = CarbonUtils.getCarbonHome() + File.separator + "repository" + File.separator +
            "conf" + File.separator + "dbscripts";

    public static DeviceConfigurationManager getInstance() {
        if (deviceConfigManager == null) {
            synchronized (DeviceConfigurationManager.class) {
                if (deviceConfigManager == null) {
                    deviceConfigManager = new DeviceConfigurationManager();
                }
            }
        }
        return deviceConfigManager;
    }


    public synchronized void initConfig() throws DeviceManagementException {
        try {
            File deviceMgtConfig = new File(deviceMgtConfigXMLPath);
            Document doc = DeviceManagerUtil.convertToDocument(deviceMgtConfig);
/*            Document doc = DeviceManagerUtil.convertToDocument(deviceMgtConfig);
            //rss-config supports secure vault as it needs to be resolve when parsing
            DeviceManagerUtil.secureResolveDocument(doc);*/
            /* Un-marshaling RSS configuration */
            JAXBContext rssContext = JAXBContext.newInstance(DeviceMgtConfig.class);
            Unmarshaller unmarshaller = rssContext.createUnmarshaller();
            this.currentDeviceConfig = (DeviceMgtConfig) unmarshaller.unmarshal(doc);
            //set jndi data source name for future use
            DeviceManagerUtil.setJndiDataSourceName(currentDeviceConfig.getDeviceMgtRepository().getDataSourceConfig().
                    getJndiLookupDefintion().getJndiName());
            DataSource dataSource = DeviceMgtDAOFactory.resolveDataSource(this.currentDeviceConfig.getDeviceMgtRepository()
                            .getDataSourceConfig());
            DeviceManagerUtil.setDataSource(dataSource);

        } catch (Exception e) {
            throw new DeviceManagementException("Error occurred while initializing RSS config", e);
        }
    }



    public DeviceMgtConfig getCurrentDeviceConfig() {
        return currentDeviceConfig;
    }
}
