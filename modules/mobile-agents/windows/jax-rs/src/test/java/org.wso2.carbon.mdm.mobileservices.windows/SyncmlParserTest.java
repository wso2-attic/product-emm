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
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlMessageFormatException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlGenerator;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class SyncmlParserTest {

    @Test
    public void parseSyncML() throws IOException, SyncmlMessageFormatException, SyncmlOperationException {
        SyncmlParser syncmlParser = new SyncmlParser();
        File syncmlTestMessage = new File(getClass().getClassLoader().getResource("syncml-test-message.xml").getFile());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;
        Document documentInputSyncML;
        String inputSyncmlMessage = null;
        String generatedSyncmlMsg = null;

        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            if (docBuilder != null) {
                document = docBuilder.parse(syncmlTestMessage);
            }

            String fileInputSyncmlMsg = FileUtils.readFileToString(syncmlTestMessage);
            generatedSyncmlMsg = SyncmlGenerator.generatePayload(syncmlParser.parseSyncmlPayload(document));
            DocumentBuilder documentBuilderInputSyncML;
            documentBuilderInputSyncML = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputSource inputSourceInputSyncML = new InputSource();
            inputSourceInputSyncML.setCharacterStream(new StringReader(fileInputSyncmlMsg));
            documentInputSyncML = documentBuilderInputSyncML.parse(inputSourceInputSyncML);
            inputSyncmlMessage = convertToString(documentInputSyncML);
        } catch (ParserConfigurationException e) {
            Assert.fail("Test failure in parser configuration while reading syncml-test-message.xml.");
        } catch (SAXException e) {
            Assert.fail("Test failure occurred while reading syncml-test-message.xml.");
        } catch (IOException e) {
            Assert.fail("Test failure while accessing syncml-test-message.xml.");
        } catch (TransformerException e) {
            Assert.fail("Test failure while transforming input stream. ");
        }

        Assert.assertEquals(inputSyncmlMessage, generatedSyncmlMsg);
    }

    private String convertToString(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, streamResult);
        stringWriter.flush();
        return stringWriter.toString();
    }
}
