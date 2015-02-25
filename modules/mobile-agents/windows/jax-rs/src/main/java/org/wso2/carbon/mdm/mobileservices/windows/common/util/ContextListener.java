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

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.xml.sax.SAXException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * This class performs one time operations.
 */
public class ContextListener implements ServletContextListener {

	private Logger logger = Logger.getLogger(ContextListener.class);
	private static final int FIRST_ITEM = 0;
	private static final String SIGNED_CERT_CN = "signedcertCN";
	private static final String SIGNED_CERT_NOT_BEFORE = "signedcertnotbefore";
	private static final String SIGNED_CERT_NOT_AFTER = "signedcertnotafter";

	/**
	 * This method loads wap-provisioning file / property file, sets wap-provisioning file and
	 * extracted properties as attributes in servlet context.
	 * @param servletContextEvent
	 */
	@Override public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext servletContext = servletContextEvent.getServletContext();

		File propertyFile = new File(getClass().getClassLoader().getResource(Constants.PROPERTIES_XML).getFile());
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder=null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			logger.error("XML parsing configuration exception.");
		}
		Document document=null;
		try {
			document = docBuilder.parse(propertyFile);
		} catch (SAXException e) {
			logger.error("XML Parsing Exception.");
		} catch (IOException e) {
			logger.error("XML property file reading exception.");
		}

		String MDMPassword = document.getElementsByTagName(Constants.MDM_PASSWORD).item(FIRST_ITEM).getTextContent();
        String MDMPrivateKeyPassword = document.getElementsByTagName(Constants.MDM_PRIVATE_KEY_PASSWORD).item(FIRST_ITEM).getTextContent();
		String signedCertCommonName = document.getElementsByTagName(SIGNED_CERT_CN).item(FIRST_ITEM).getTextContent();
		int signedCertNotBeforeDate = Integer.valueOf(document.getElementsByTagName(SIGNED_CERT_NOT_BEFORE).item(FIRST_ITEM).getTextContent());
		int signedCertNotAfterDate = Integer.valueOf(document.getElementsByTagName(SIGNED_CERT_NOT_AFTER).item(FIRST_ITEM).getTextContent());

		servletContext.setAttribute(Constants.CONTEXT_MDM_PASSWORD,MDMPassword);
		servletContext.setAttribute(Constants.CONTEXT_MDM_PRIVATE_KEY_PASSWORD,MDMPrivateKeyPassword);
		servletContext.setAttribute(Constants.CONTEXT_COMMON_NAME,signedCertCommonName);
		servletContext.setAttribute(Constants.CONTEXT_NOT_BEFORE_DATE,signedCertNotBeforeDate);
		servletContext.setAttribute(Constants.CONTEXT_NOT_AFTER_DATE,signedCertNotAfterDate);

		File wapProvisioningFile = new File(getClass().getClassLoader().getResource(Constants.WAP_PROVISIONING_XML).getFile());
		servletContext.setAttribute(Constants.CONTEXT_WAP_PROVISIONING_FILE,wapProvisioningFile);
	}

	@Override public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}
}
