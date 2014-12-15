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

package org.wso2.carbon.device.mgt.mobile.impl.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.core.config.datasource.DataSourceConfig;
import org.wso2.carbon.utils.CarbonUtils;
import org.wso2.carbon.utils.dbcreator.DatabaseCreator;

import java.io.File;

public final class MobileDeviceManagementSchemaInitializer extends DatabaseCreator {

    private static final Log log = LogFactory.getLog(DeviceManagementSchemaInitializer.class);
    private static final String setupSQLScriptBaseLocation =
            CarbonUtils.getCarbonHome() + File.separator + "dbscripts" + File.separator + "cdm" + File.separator;

    public MobileDeviceManagementSchemaInitializer(DataSourceConfig config) {
        super(DeviceManagerUtil.resolveDataSource(config));
    }

    protected String getDbScriptLocation(String databaseType) {
        String scriptName = databaseType + ".sql";
        if (log.isDebugEnabled()) {
            log.debug("Loading database script from :" + scriptName);
        }
        return setupSQLScriptBaseLocation.replaceFirst("DBTYPE", databaseType) + scriptName;
    }

}
