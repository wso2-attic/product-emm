/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.policy.mgt.core.dao.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.policy.mgt.common.Policy;
import org.wso2.carbon.policy.mgt.core.dao.PolicyDAO;
import org.wso2.carbon.policy.mgt.core.dao.PolicyManagerDAOException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class PolicyDAOImpl implements PolicyDAO {

    private static DataSource dataSource;
    private static final Log log = LogFactory.getLog(PolicyDAOImpl.class);

    public PolicyDAOImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int addPolicy(Policy policy) throws PolicyManagerDAOException {
        return 0;
    }

    @Override
    public int addPolicy(String deviceType, Policy policy) throws PolicyManagerDAOException {
        return 0;
    }

    @Override
    public int addPolicy(String deviceID, String deviceType, Policy policy) throws PolicyManagerDAOException {
        return 0;
    }

    @Override
    public void updatePolicy(int id, Policy policy) throws PolicyManagerDAOException {

    }

    @Override
    public Policy getPolicy() throws PolicyManagerDAOException {
        return null;
    }

    @Override
    public Policy getPolicy(String deviceType) throws PolicyManagerDAOException {
        return null;
    }

    @Override
    public Policy getPolicy(String deviceID, String deviceType) throws PolicyManagerDAOException {
        return null;
    }

    private Connection getConnection() throws PolicyManagerDAOException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new PolicyManagerDAOException("Error occurred while obtaining a connection from the policy " +
                    "management metadata repository datasource", e);
        }
    }
}
