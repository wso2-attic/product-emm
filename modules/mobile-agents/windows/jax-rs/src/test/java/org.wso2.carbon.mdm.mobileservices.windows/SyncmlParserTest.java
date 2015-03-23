/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlDocument;
import org.wso2.carbon.mdm.mobileservices.windows.operations.WindowsOperationException;

import org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationReply;

import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlGenerator;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlParser;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SyncmlParserTest {

    private static Log log = LogFactory.getLog(SyncmlParser.class);

	@Test
	public void parseSyncML() throws IOException, WindowsOperationException {

        URL resourceUrl = this.getClass().getClassLoader().getResource("syncml-test-message.xml");
        if (resourceUrl == null) {
            Assert.fail("Failed to locate the resource file 'syncml-test-message.xml'");
        }
		File propertyFile = new File(resourceUrl.getFile());

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document document = null;

		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			if (docBuilder != null) {
				document = docBuilder.parse(propertyFile);
			}
		} catch (ParserConfigurationException e) {
			Assert.fail("Test failure in parser configuration while reading syncml-test-message.xml", e);
		} catch (SAXException e) {
			Assert.fail("Test failure occurred while reading syncml-test-message.xml", e);
		} catch (IOException e) {
			Assert.fail("Test failure while accessing syncml-test-message.xml", e);
		}
		SyncmlGenerator generator = new SyncmlGenerator();
		String inputSyncmlMsg = FileUtils.readFileToString(propertyFile);
		String generatedSyncmlMsg = generator.generatePayload(SyncmlParser.parseSyncmlPayload(document));
		generatedSyncmlMsg = generatedSyncmlMsg.replaceAll("\n", "");
		//Assert.assertEquals(inputSyncmlMsg , generatedSyncmlMsg);
	}

}
