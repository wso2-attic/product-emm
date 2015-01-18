/**
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.device.mgt.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.device.mgt.core.config.DeviceManagementConfig;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class DeviceManagementConfigTests {

    private static final Log log = LogFactory.getLog(DeviceManagementConfigTests.class);
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_MGT_REPOSITORY =
            "./src/test/resources/config/malformed-cdm-config-no-mgt-repo.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_DS_CONFIG =
            "./src/test/resources/config/malformed-cdm-config-no-ds-config.xml";
    private static final String MALFORMED_TEST_CONFIG_LOCATION_NO_JNDI_CONFIG =
            "./src/test/resources/config/malformed-cdm-config-no-jndi-config.xml";
    private static final String TEST_CONFIG_SCHEMA_LOCATION =
            "./src/test/resources/config/schema/DeviceManagementConfigSchema.xsd";

    private Schema schema;

    @BeforeClass
    private void initSchema() {
        File deviceManagementSchemaConfig = new File(DeviceManagementConfigTests.TEST_CONFIG_SCHEMA_LOCATION);
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
                new File(DeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_MGT_REPOSITORY);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateDataSourceConfigurationElement() {
        File malformedConfig = new File(DeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_DS_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    @Test
    public void testMandateJndiLookupDefinitionElement() {
        File malformedConfig = new File(DeviceManagementConfigTests.MALFORMED_TEST_CONFIG_LOCATION_NO_JNDI_CONFIG);
        this.validateMalformedConfig(malformedConfig);
    }

    private void validateMalformedConfig(File malformedConfig) {
        try {
            JAXBContext ctx = JAXBContext.newInstance(DeviceManagementConfig.class);
            Unmarshaller um = ctx.createUnmarshaller();
            um.setSchema(this.getSchema());
            um.unmarshal(malformedConfig);
            Assert.assertTrue(false);
        } catch (JAXBException e) {
            log.error("Error occurred while unmarsharlling device management config", e);
            Assert.assertTrue(true);
        }
    }

    private Schema getSchema() {
        return schema;
    }

}
