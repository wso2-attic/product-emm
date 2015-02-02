/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.device.mgt.mobile.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.device.mgt.mobile.config.MobileDeviceManagementConfig;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class MobileDeviceManagementConfigTests {

    private static final Log log = LogFactory.getLog(MobileDeviceManagementConfigTests.class);
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_MGT_REPOSITORY =
            "./src/test/resources/config/malformed-mobile-config-no-mgt-repo.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_DS_CONFIG =
            "./src/test/resources/config/malformed-mobile-config-no-ds-config.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_JNDI_CONFIG =
            "./src/test/resources/config/malformed-mobile-config-no-jndi-config.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_APIS_CONFIG =
            "./src/test/resources/config/malformed-mobile-config-no-apis-config.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_API_CONFIG =
            "./src/test/resources/config/malformed-mobile-config-no-api-config.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_API_PUBLISHER_CONFIG =
            "./src/test/resources/config/malformed-mobile-config-no-api-publisher-config.xml";
    private static final String TEST_CONFIG_SCHEMA_LOCATION =
            "./src/test/resources/config/schema/MobileDeviceManagementConfigSchema.xsd";

    private Schema schema;

    @BeforeClass
    private void initSchema() {
        File deviceManagementSchemaConfig = new File(MobileDeviceManagementConfigTests.TEST_CONFIG_SCHEMA_LOCATION);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            schema = factory.newSchema(deviceManagementSchemaConfig);
        } catch (SAXException e) {
            Assert.fail("Invalid schema found", e);
        }
    }

    @Test()
    public void testMandateManagementRepositoryElement() {
        File malformedConfig =
                new File(MobileDeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_MGT_REPOSITORY);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateDataSourceConfigurationElement() {
        File malformedConfig = new File(MobileDeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_DS_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateJndiLookupDefinitionElement() {
        File malformedConfig = new File(MobileDeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_JNDI_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateAPIPublisherElement() {
        File malformedConfig = new File(MobileDeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_API_PUBLISHER_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateAPIsElement() {
        File malformedConfig = new File(MobileDeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_APIS_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateAPIElement() {
        File malformedConfig = new File(MobileDeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_API_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    private void validateMalformedConfig(File malformedConfig) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(MobileDeviceManagementConfig.class);
            Unmarshaller um = ctx.createUnmarshaller();
            um.setSchema(this.getSchema());
            um.unmarshal(malformedConfig);
            Assert.assertTrue(false);
        } catch (JAXBException e) {
            log.error("Error occurred while unmarsharlling mobile device management config", e);
            Assert.assertTrue(true);
        }
    }

    private Schema getSchema() {
        return schema;
    }

}
