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
import org.wso2.carbon.device.mgt.mobile.dao.MobileDeviceManagementDAOException;
import org.wso2.carbon.device.mgt.mobile.dao.impl.MobileFeatureDAOImpl;
import org.wso2.carbon.device.mgt.mobile.dto.MobileFeature;
import org.wso2.carbon.device.mgt.mobile.impl.TestUtils;
import org.wso2.carbon.device.mgt.mobile.impl.common.DBTypes;
import org.wso2.carbon.device.mgt.mobile.impl.common.TestDBConfiguration;
import org.wso2.carbon.device.mgt.mobile.impl.common.TestDBConfigurations;
import org.wso2.carbon.device.mgt.mobile.util.MobileDeviceManagementUtil;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.*;
import java.util.Iterator;
import java.util.List;

public class MobileFeatureDAOTestSuite {

	public static final String MBL_FEATURE_NAME = "Camera";
	private static final String MBL_FEATURE_CODE = "500A";
	public static final String MBL_FEATURE_DESCRIPTION = "Camera enable or disable";
	public static final String MBL_FEATURE_UPDATED_CODE = "501B";
	private TestDBConfiguration testDBConfiguration;
	private Connection conn = null;
	private Statement stmt = null;
	private MobileFeatureDAOImpl mblFeatureDAO;
	private int mblFeatureId;

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
				mblFeatureDAO = new MobileFeatureDAOImpl(testDataSource);
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

		doc = MobileDeviceManagementUtil.convertToDocument(deviceMgtConfig);
		JAXBContext testDBContext = null;

		try {
			testDBContext = JAXBContext.newInstance(TestDBConfigurations.class);
			Unmarshaller unmarshaller = testDBContext.createUnmarshaller();
			testDBConfigurations = (TestDBConfigurations) unmarshaller.unmarshal(doc);
		} catch (JAXBException e) {
			throw new MobileDeviceManagementDAOException("Error parsing test db configurations", e);
		}

		Iterator<TestDBConfiguration> itrDBConfigs =
				testDBConfigurations.getDbTypesList().iterator();
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
	public void addMobileFeatureTest()
			throws MobileDeviceManagementDAOException {

		MobileFeature mobileFeature = new MobileFeature();
		MobileFeature testMblFeature = new MobileFeature();
		mobileFeature.setCode(MBL_FEATURE_CODE);
		mobileFeature.setDescription(MBL_FEATURE_DESCRIPTION);
		mobileFeature.setName(MBL_FEATURE_NAME);
		boolean added = mblFeatureDAO.addMobileFeature(mobileFeature);
		try {
			conn = DriverManager.getConnection(testDBConfiguration.getConnectionUrl());
			stmt = conn.createStatement();
			ResultSet resultSet = stmt
					.executeQuery(
							"SELECT FEATURE_ID, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE CODE = '500A'");
			while (resultSet.next()) {
				testMblFeature.setId(resultSet.getInt(1));
				testMblFeature.setCode(resultSet.getString(2));
				testMblFeature.setName(resultSet.getString(3));
				testMblFeature.setDescription(resultSet.getString(4));
			}
			conn.close();
		} catch (SQLException e) {
			throw new MobileDeviceManagementDAOException("Error in retrieving Mobile Feature data ",
			                                             e);
		} finally {
			TestUtils.cleanupResources(conn, stmt, null);
		}
		mblFeatureId = testMblFeature.getId();
		Assert.assertTrue(added, "MobileFeature is added");
		Assert.assertEquals(MBL_FEATURE_CODE, testMblFeature.getCode(),
		                    "MobileFeature code has persisted successfully");
		Assert.assertEquals(MBL_FEATURE_NAME, testMblFeature.getName(),
		                    "MobileFeature name has persisted successfully");
		Assert.assertEquals(MBL_FEATURE_DESCRIPTION, testMblFeature.getDescription(),
		                    "MobileFeature description has persisted successfully");
	}

	@Test(dependsOnMethods = { "addMobileFeatureTest" })
	public void getMobileFeatureByCodeTest()
			throws MobileDeviceManagementDAOException {

		MobileFeature mobileFeature = mblFeatureDAO.getMobileFeatureByCode(MBL_FEATURE_CODE);
		Assert.assertEquals(MBL_FEATURE_CODE, mobileFeature.getCode(),
		                    "MobileFeature code has retrieved successfully");
		Assert.assertEquals(MBL_FEATURE_NAME, mobileFeature.getName(),
		                    "MobileFeature name has retrieved successfully");
		Assert.assertEquals(MBL_FEATURE_DESCRIPTION, mobileFeature.getDescription(),
		                    "MobileFeature description has retrieved successfully");
	}

	@Test(dependsOnMethods = { "addMobileFeatureTest" })
	public void deleteMobileFeatureByCodeTest()
			throws MobileDeviceManagementDAOException {
		boolean status = mblFeatureDAO.deleteMobileFeatureByCode(MBL_FEATURE_CODE);
		try {
			conn = DriverManager.getConnection(testDBConfiguration.getConnectionUrl());
			stmt = conn.createStatement();
			ResultSet resultSet = stmt
					.executeQuery(
							"SELECT FEATURE_ID, CODE FROM MBL_FEATURE WHERE CODE = '500A'");
			while (resultSet.next()) {
				status = false;
			}
			conn.close();
		} catch (SQLException e) {
			throw new MobileDeviceManagementDAOException("Error in deleting Mobile Feature data ",
			                                             e);
		} finally {
			TestUtils.cleanupResources(conn, stmt, null);
		}
		Assert.assertTrue(status, "MobileFeature has deleted successfully");
	}

	@Test(dependsOnMethods = { "addMobileFeatureTest" })
	public void getMobileFeatureByIdTest()
			throws MobileDeviceManagementDAOException {

		MobileFeature mobileFeature = mblFeatureDAO.getMobileFeatureById(mblFeatureId);
		Assert.assertEquals(MBL_FEATURE_CODE, mobileFeature.getCode(),
		                    "MobileFeature code has retrieved successfully");
		Assert.assertEquals(MBL_FEATURE_NAME, mobileFeature.getName(),
		                    "MobileFeature name has retrieved successfully");
		Assert.assertEquals(MBL_FEATURE_DESCRIPTION, mobileFeature.getDescription(),
		                    "MobileFeature description has retrieved successfully");
	}

	@Test(dependsOnMethods = { "addMobileFeatureTest" })
	public void getAllMobileFeaturesTest()
			throws MobileDeviceManagementDAOException {

		List<MobileFeature> mobileFeatures = mblFeatureDAO.getAllMobileFeatures();
		Assert.assertNotNull(mobileFeatures, "MobileFeature list is not null");
		Assert.assertTrue(mobileFeatures.size() > 0, "MobileFeature list has 1 MobileFeature");
	}

	@Test(dependsOnMethods = { "addMobileFeatureTest" })
	public void deleteMobileFeatureByIdTest()
			throws MobileDeviceManagementDAOException {
		boolean status = mblFeatureDAO.deleteMobileFeatureById(mblFeatureId);
		try {
			conn = DriverManager.getConnection(testDBConfiguration.getConnectionUrl());
			stmt = conn.createStatement();
			ResultSet resultSet = stmt
					.executeQuery(
							"SELECT FEATURE_ID, CODE FROM MBL_FEATURE WHERE FEATURE_ID = " +
							mblFeatureId);
			while (resultSet.next()) {
				status = false;
			}
			conn.close();
		} catch (SQLException e) {
			throw new MobileDeviceManagementDAOException("Error in deleting Mobile Feature data ",
			                                             e);
		} finally {
			TestUtils.cleanupResources(conn, stmt, null);
		}
		Assert.assertTrue(status, "MobileFeature has deleted successfully");
	}

	@Test(dependsOnMethods = { "deleteMobileFeatureByCodeTest", "addMobileFeatureTest" })
	public void updateMobileFeatureTest()
			throws MobileDeviceManagementDAOException {

		MobileFeature mobileFeature = new MobileFeature();
		MobileFeature testMblFeature = new MobileFeature();
		mobileFeature.setCode(MBL_FEATURE_UPDATED_CODE);
		mobileFeature.setDescription(MBL_FEATURE_DESCRIPTION);
		mobileFeature.setName(MBL_FEATURE_NAME);
		mobileFeature.setId(mblFeatureId);
		boolean updated = mblFeatureDAO.updateMobileFeature(mobileFeature);
		try {
			conn = DriverManager.getConnection(testDBConfiguration.getConnectionUrl());
			stmt = conn.createStatement();
			ResultSet resultSet = stmt
					.executeQuery(
							"SELECT FEATURE_ID, CODE, NAME, DESCRIPTION FROM MBL_FEATURE WHERE CODE = " +
							MBL_FEATURE_UPDATED_CODE);
			while (resultSet.next()) {
				testMblFeature.setId(resultSet.getInt(1));
				testMblFeature.setCode(resultSet.getString(2));
				testMblFeature.setName(resultSet.getString(3));
				testMblFeature.setDescription(resultSet.getString(4));
			}
			conn.close();
		} catch (SQLException e) {
			throw new MobileDeviceManagementDAOException("Error in updating Mobile Feature data ",
			                                             e);
		} finally {
			TestUtils.cleanupResources(conn, stmt, null);
		}
		Assert.assertTrue(updated, "MobileFeature has updated");
		Assert.assertEquals(MBL_FEATURE_UPDATED_CODE, testMblFeature.getCode(),
		                    "MobileFeature data has updated successfully");
	}

}
