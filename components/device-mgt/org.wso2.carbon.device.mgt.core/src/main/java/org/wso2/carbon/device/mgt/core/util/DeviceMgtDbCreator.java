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
import org.wso2.carbon.utils.dbcreator.DatabaseCreator;

import javax.sql.DataSource;

public class DeviceMgtDbCreator extends DatabaseCreator {

    private static final Log log = LogFactory.getLog(DeviceMgtDbCreator.class);
    private String rssDBScriptDirectory;// stores the the location of the database script that is run according to the databse type

    public DeviceMgtDbCreator(DataSource dataSource) {
        super(dataSource);

    }

    protected String getDbScriptLocation(String databaseType) {
        String scriptName = "wso2_rss_" + databaseType + ".sql";
        if (log.isDebugEnabled()) {
            log.debug("Loading database script from :" + scriptName);
        }
        return rssDBScriptDirectory.replaceFirst("DBTYPE", databaseType) + scriptName;
    }

    /**
     * Sets database scripts directory
     * @param rssDBScriptDirectory database scripts location
     */
    public void setRssDBScriptDirectory(String rssDBScriptDirectory) {
        this.rssDBScriptDirectory = rssDBScriptDirectory;
    }
}
