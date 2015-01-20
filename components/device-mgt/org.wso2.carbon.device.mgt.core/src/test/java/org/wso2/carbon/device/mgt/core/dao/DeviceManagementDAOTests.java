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

package org.wso2.carbon.device.mgt.core.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.core.TestUtils;
import org.wso2.carbon.device.mgt.core.common.DBTypes;
import org.wso2.carbon.device.mgt.core.common.TestDBConfiguration;
import org.wso2.carbon.device.mgt.core.common.TestDBConfigurations;
import org.wso2.carbon.device.mgt.core.dto.Device;
import org.wso2.carbon.device.mgt.core.dto.DeviceType;
import org.wso2.carbon.device.mgt.core.dto.OwnerShip;
import org.wso2.carbon.device.mgt.core.dto.Status;
import org.wso2.carbon.device.mgt.core.util.DeviceManagerUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.*;
import java.util.Date;

public class DeviceManagementDAOTests {

    @BeforeClass
    @Parameters("dbType")
    public void setUpDB(String dbTypeStr) throws Exception {
        DBTypes dbType = DBTypes.valueOf(dbTypeStr);
        TestDBConfiguration dbConfig = getTestDBConfiguration(dbType);

        switch (dbType) {
            case H2:
                createH2DB(dbConfig);
                BasicDataSource testDataSource = new BasicDataSource();
                testDataSource.setDriverClassName(dbConfig.getDriverClass());
                testDataSource.setUrl(dbConfig.getConnectionUrl());
                testDataSource.setUsername(dbConfig.getUserName());
                testDataSource.setPassword(dbConfig.getPwd());
                DeviceManagementDAOFactory.init(testDataSource);
            default:
        }
    }

    private TestDBConfiguration getTestDBConfiguration(DBTypes dbType) throws DeviceManagementDAOException,
            DeviceManagementException {
        File deviceMgtConfig = new File("src/test/resources/testdbconfig.xml");
        Document doc;
        TestDBConfigurations dbConfigs;

        doc = DeviceManagerUtil.convertToDocument(deviceMgtConfig);
        JAXBContext testDBContext = null;

        try {
            testDBContext = JAXBContext.newInstance(TestDBConfigurations.class);
            Unmarshaller unmarshaller = testDBContext.createUnmarshaller();
            dbConfigs = (TestDBConfigurations) unmarshaller.unmarshal(doc);
        } catch (JAXBException e) {
            throw new DeviceManagementDAOException("Error parsing test db configurations", e);
        }

        for (TestDBConfiguration config : dbConfigs.getDbTypesList()) {
            if (config.getDbType().equals(dbType.toString())) {
                return config;
            }
        }
        return null;
    }

    private void createH2DB(TestDBConfiguration testDBConf) throws Exception {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(testDBConf.getDriverClass());
            conn = DriverManager.getConnection(testDBConf.getConnectionUrl());
            stmt = conn.createStatement();
            stmt.executeUpdate("RUNSCRIPT FROM './src/test/resources/sql/CreateH2TestDB.sql'");
        } finally {
            TestUtils.cleanupResources(conn, stmt, null);
        }
    }

    @Test
    public void addDeviceTypeTest() throws DeviceManagementDAOException, DeviceManagementException {
        DeviceTypeDAO deviceTypeMgtDAO = DeviceManagementDAOFactory.getDeviceTypeDAO();

        DeviceType deviceType = new DeviceType();
        deviceType.setName("IOS");

        deviceTypeMgtDAO.addDeviceType(deviceType);
        Long deviceTypeId = null;

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DeviceManagementDAOFactory.getDataSource().getConnection();
            stmt = conn.createStatement();
            ResultSet resultSet =
                    stmt.executeQuery("SELECT ID,NAME from DM_DEVICE_TYPE DType where DType.NAME='IOS'");

            while (resultSet.next()) {
                deviceTypeId = resultSet.getLong(1);
            }
        } catch (SQLException sqlEx) {
            throw new DeviceManagementDAOException("error in fetch device type by name IOS", sqlEx);
        } finally {
            TestUtils.cleanupResources(conn, stmt, null);
        }

        Assert.assertNotNull(deviceTypeId, "Device Type Id is null");
        deviceType.setId(deviceTypeId);

    }

    @Test(dependsOnMethods = {"addDeviceTypeTest"})
    public void addDeviceTest() throws DeviceManagementDAOException, DeviceManagementException {
        DeviceDAO deviceMgtDAO = DeviceManagementDAOFactory.getDeviceDAO();

        Device device = new Device();
        device.setDateOfEnrollment(new Date().getTime());
        device.setDateOfLastUpdate(new Date().getTime());
        device.setDescription("test description");
        device.setStatus(Status.ACTIVE);
        device.setDeviceIdentificationId("111");

        DeviceType deviceType = new DeviceType();
        deviceType.setId(Long.parseLong("1"));

        device.setDeviceTypeId(deviceType.getId().intValue());
        device.setOwnerShip(OwnerShip.BYOD.toString());
        device.setOwnerId("111");
        device.setTenantId(-1234);
        deviceMgtDAO.addDevice(device);

        Long deviceId = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DeviceManagementDAOFactory.getDataSource().getConnection();
            stmt = conn.prepareStatement("SELECT ID from DM_DEVICE DEVICE where DEVICE.DEVICE_IDENTIFICATION='111'");
            rs = stmt.executeQuery();

            while (rs.next()) {
                deviceId = rs.getLong(1);
            }
        } catch (SQLException e) {
            throw new DeviceManagementDAOException("error in fetch device by device identification id", e);
        } finally {
            TestUtils.cleanupResources(conn, stmt, rs);
        }

        Assert.assertNotNull(deviceId, "Device Id is null");
    }

}
