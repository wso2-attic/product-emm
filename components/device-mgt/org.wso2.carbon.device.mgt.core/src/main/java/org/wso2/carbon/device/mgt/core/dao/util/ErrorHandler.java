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

package org.wso2.carbon.device.mgt.core.dao.util;

import org.apache.commons.logging.Log;
import org.wso2.carbon.device.mgt.core.dao.exception.DeviceDAOException;

import java.sql.SQLException;

public class ErrorHandler {

    private String errorMsg = "";
    private Log log;

    public ErrorHandler(String msg, Log log) {
        errorMsg = msg;
        this.log = log;
    }

    public void handleDAOException(String msg, SQLException e) throws DeviceDAOException {

        log.error(msg, e);
        throw new DeviceDAOException(msg, e);
    }
}
