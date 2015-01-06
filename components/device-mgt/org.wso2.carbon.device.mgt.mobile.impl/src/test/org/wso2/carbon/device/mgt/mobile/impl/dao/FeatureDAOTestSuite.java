/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.device.mgt.mobile.impl.dao;


import org.apache.commons.dbcp.BasicDataSource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.common.DBTypes;
import org.wso2.carbon.device.mgt.core.common.TestDBConfiguration;
import org.wso2.carbon.device.mgt.core.common.TestDBConfigurations;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.dto.OwnerShip;
import org.wso2.carbon.device.mgt.core.dto.Status;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;
import org.wso2.carbon.device.mgt.mobile.dao.FeatureDAO;
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.impl.FeatureDAOImpl;
import org.wso2.carbon.device.mgt.mobile.impl.common.DBTypes;
import org.wso2.carbon.device.mgt.mobile.impl.common.TestDBConfiguration;
import org.wso2.carbon.device.mgt.mobile.impl.common.TestDBConfigurations;
import org.wso2.carbon.device.mgt.mobile.dto.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.*;
import java.util.Date;
import java.util.Iterator;

public class FeatureDAOTestSuite {

    private TestDBConfiguration testDBConfiguration;
    private Connection conn = null;
    private Statement stmt = null;
	private FeatureDAOImpl featureDAO;

    @BeforeClass
    @Parameters("dbType")
    public void setUpDB(String dbTypeStr) throws Exception {

        DBTypes dbType = DBTypes.valueOf(dbTypeStr);
        testDBConfiguration = getTestDBConfiguration(dbType);

        switch (dbType) {
        case H2:
            createH2DB(testDBConfiguration);
            BasicDataSource testDataSource = new BasicDataSource();
            testDataSource.setDriverClassName(testDBConfiguration.getDriverClass());
            testDataSource.setUrl(testDBConfiguration.getConnectionUrl());
            testDataSource.setUsername(testDBConfiguration.getUserName());
            testDataSource.setPassword(testDBConfiguration.getPwd());
	        featureDAO = new FeatureDAOImpl(testDataSource);
        default:
        }
    }

    private TestDBConfiguration getTestDBConfiguration(DBTypes dbType) throws
                                                                       MobileDeviceManagementDAOException,
            DeviceManagementException {

        File deviceMgtConfig = new File("src/test/resources/testdbconfig.xml");
        Document doc = null;
        testDBConfiguration = null;
        TestDBConfigurations testDBConfigurations = null;

        doc = DeviceManagerUtil.convertToDocument(deviceMgtConfig);
        JAXBContext testDBContext = null;

        try {
            testDBContext = JAXBContext.newInstance(TestDBConfigurations.class);
            Unmarshaller unmarshaller = testDBContext.createUnmarshaller();
            testDBConfigurations = (TestDBConfigurations) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new MobileDeviceManagementDAOException("Error parsing test db configurations", e);
        }

        Iterator<TestDBConfiguration> itrDBConfigs = testDBConfigurations.getDbTypesList().iterator();
        while (itrDBConfigs.hasNext()) {
            testDBConfiguration = itrDBConfigs.next();
            if (testDBConfiguration.getDbType().equals(dbType.toString())) {
                break;
            }
        }

        return testDBConfiguration;
    }

    private void createH2DB(TestDBConfiguration testDBConf) throws Exception {

        Class.forName(testDBConf.getDriverClass());
        conn = DriverManager.getConnection(testDBConf.getConnectionUrl());
        stmt = conn.createStatement();
        stmt.executeUpdate("RUNSCRIPT FROM './src/test/resources/sql/CreateH2TestDB.sql'");
        stmt.close();
        conn.close();

    }


    @Test
    public void addFeature() throws MobileDeviceManagementDAOException, DeviceManagementException {



	    Feature feature = new Feature();
	    feature.setCode("Camera");
	    feature.setDescription("Camera enable or disable");
	    feature.setName("Camera");
	    boolean added = featureDAO.addFeature(feature);
//        Long deviceId = null;
//        try {
//            conn = DeviceManagementDAOFactory.getDataSource().getConnection();
//            stmt = conn.createStatement();
//            ResultSet resultSet = stmt
//                    .executeQuery("SELECT ID from DM_DEVICE DEVICE where DEVICE.DEVICE_IDENTIFICATION='111'");
//
//            while (resultSet.next()) {
//                deviceId = resultSet.getLong(1);
//            }
//            conn.close();
//        } catch (SQLException sqlEx) {
//            throw new DeviceManagementDAOException("error in fetch device by device identification id", sqlEx);
//        }

        Assert.assertTrue(added, "Device Id is null");
    }

}
