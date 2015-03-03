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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.WindowsPluginProperties;

/**
 * This class performs one time operations.
 */
public class ContextListener implements ServletContextListener {

	public static final int INITIAL_VALUE = 0;
	private static Log log = LogFactory.getLog(ContextListener.class);
	private static final String SIGNED_CERT_CN = "signedcertCN";
	private static final String SIGNED_CERT_NOT_BEFORE = "signedcertnotbefore";
	private static final String SIGNED_CERT_NOT_AFTER = "signedcertnotafter";
	private static final String PASSWORD = "mdmpassword";
	private static final String PRIVATE_KEY_PASSWORD = "mdmprivatekeypassword";

	/**
	 * This method loads wap-provisioning file / property file, sets wap-provisioning file and
	 * extracted properties as attributes in servlet context.
	 * @param servletContextEvent - Uses when servlet communicating with servlet container.
	 */
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		ServletContext servletContext = servletContextEvent.getServletContext();
		File propertyFile = new File(getClass().getClassLoader().getResource(
				Constants.CertificateEnrolment.PROPERTIES_XML).getFile());
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			log.error("XML parsing configuration exception.");
		}
		Document document = null;
		try {
		    if (docBuilder != null) {
				document = docBuilder.parse(propertyFile);
			}
		} catch (SAXException e) {
			log.error("XML Parsing Exception.");
		} catch (IOException e) {
			log.error("XML property file reading exception.");
		}

		String MDMPassword = null;
		String MDMPrivateKeyPassword = null;
		String signedCertCommonName = null;
		int signedCertNotBeforeDate = INITIAL_VALUE;
		int signedCertNotAfterDate = INITIAL_VALUE;

		if (document != null) {
		   MDMPassword = document.getElementsByTagName(PASSWORD).item(0).
				         getTextContent();
		   MDMPrivateKeyPassword = document.getElementsByTagName(PRIVATE_KEY_PASSWORD).
				                   item(0).getTextContent();
		   signedCertCommonName = document.getElementsByTagName(SIGNED_CERT_CN).item(0).
				                  getTextContent();
		   signedCertNotBeforeDate = Integer.valueOf(document.getElementsByTagName(
				                     SIGNED_CERT_NOT_BEFORE).item(0).getTextContent());
		   signedCertNotAfterDate = Integer.valueOf(document.getElementsByTagName(
					                SIGNED_CERT_NOT_AFTER).item(0).getTextContent());
		}

		WindowsPluginProperties properties = new WindowsPluginProperties();
		properties.setKeyStorePassword(MDMPassword);
		properties.setPrivateKeyPassword(MDMPrivateKeyPassword);
		properties.setCommonName(signedCertCommonName);
		properties.setNotBeforeDays(signedCertNotBeforeDate);
		properties.setNotAfterDays(signedCertNotAfterDate);
		servletContext.setAttribute(Constants.WINDOWS_PLUGIN_PROPERTIES, properties);

		File wapProvisioningFile = new File(getClass().getClassLoader().getResource(
				Constants.CertificateEnrolment.WAP_PROVISIONING_XML).getFile());
		servletContext.setAttribute(Constants.CONTEXT_WAP_PROVISIONING_FILE, wapProvisioningFile);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
	}
}
