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
import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.carbon.device.mgt.core.operation.mgt.Operation;
import org.wso2.carbon.mdm.mobileservices.windows.operations.SyncmlDocument;
import org.wso2.carbon.mdm.mobileservices.windows.operations.WindowsOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.OperationReply;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlGenerator;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SyncmlParserTest {

	@Test
	public void parseSyncML() throws IOException, WindowsOperationException {

		SyncmlParser syncmlParser = new SyncmlParser();
		File propertyFile = new File(
				getClass().getClassLoader().getResource("syncml-test-message.xml").getFile());

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		Document document = null;

		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
			if (docBuilder != null) {
				document = docBuilder.parse(propertyFile);
			}
		} catch (ParserConfigurationException e) {
			Assert.fail(
					"Test failure in parser configuration while reading syncml-test-message.xml.");
		} catch (SAXException e) {
			Assert.fail("Test failure occurred while reading syncml-test-message.xml.");
		} catch (IOException e) {
			Assert.fail("Test failure while accessing syncml-test-message.xml.");
		}
		SyncmlDocument syncmlDocument = syncmlParser.parseSyncmlPayload(document);
		List<Operation> operations = new ArrayList<Operation>();
		Operation operationSwv = new Operation();
		operationSwv.setCode("SOFTWARE_VERSION");
		operationSwv.setType(Operation.Type.INFO);
		operations.add(operationSwv);

		Operation IMSI = new Operation();
		IMSI.setCode("IMSI");
		IMSI.setType(Operation.Type.INFO);
		operations.add(IMSI);

		Operation IMEI = new Operation();
		IMEI.setCode("IMEI");
		IMEI.setType(Operation.Type.INFO);
		operations.add(IMEI);

		Operation operationDevId = new Operation();
		operationDevId.setCode("DEV_ID");
		operationDevId.setType(Operation.Type.INFO);
		operations.add(operationDevId);

		Operation operationMan = new Operation();
		operationMan.setCode("MANUFACTURER");
		operationMan.setType(Operation.Type.INFO);
		operations.add(operationMan);

		Operation operationModel = new Operation();
		operationModel.setCode("MODEL");
		operationModel.setType(Operation.Type.INFO);
		operations.add(operationModel);

		Operation operationLanguage = new Operation();
		operationLanguage.setCode("LANGUAGE");
		operationLanguage.setType(Operation.Type.INFO);
		operations.add(operationLanguage);



		OperationReply reply=new OperationReply(syncmlDocument,operations);
		SyncmlDocument replyDocument = reply.generateReply();

		SyncmlGenerator generator = new SyncmlGenerator();
		String outp=generator.generatePayload(replyDocument);
		File res = new File(
				getClass().getClassLoader().getResource("testdata.txt").getFile());
		PrintWriter out = new PrintWriter(res);
		out.println(outp);

	}

}
