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

import org.testng.Assert;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.impl.CertificateEnrollmentServiceImpl;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class CertificateEnrollmentServiceTest {

    public static final String PASSWORD = "Password";
    public static final String PRIVATE_KEY_PASSWORD = "PrivateKeyPassword";

    @Test
    public void jksReadingTest(){

        File propertyFile = new File(getClass().getClassLoader().getResource(
                PluginConstants.CertificateEnrolment.PROPERTIES_XML).getFile());
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;

        try {
            docBuilder = docBuilderFactory.newDocumentBuilder();
            if (docBuilder != null) {
                document = docBuilder.parse(propertyFile);
            }
        } catch (ParserConfigurationException e) {
            Assert.fail("Test failure in parser configuration while reading properties.xml.");
        } catch (SAXException e) {
            Assert.fail("Test failure occurred while reading properties.xml.");
        } catch (IOException e) {
            Assert.fail("Test failure while accessing properties.xml.");
        }

        String password = null;
        String privateKeyPassword = null;

        if (document != null) {
            password = document.getElementsByTagName(PASSWORD).item(0).getTextContent();
            privateKeyPassword = document.getElementsByTagName(PRIVATE_KEY_PASSWORD).item(0).getTextContent();
        }

    CertificateEnrollmentServiceImpl wstepServiceObject = new CertificateEnrollmentServiceImpl();
//    try {
//        //wstepServiceObject.setRootCertAndKey(password, privateKeyPassword);
//    } catch (KeyStoreGenerationException e) {
//        Assert.fail("Test failure when loading MDM key store.", e);
//    } catch (CertificateGenerationException e) {
//        Assert.fail("Test failure when retrieving private key from key store.", e);
//    } catch (IOException e) {
//        Assert.fail("Test failure when getting the JKS file.", e);
//    }
//        Assert.assertTrue(true, "Test of JKS file reading is successful.");
}

}
