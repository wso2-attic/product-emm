/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.WindowsPluginProperties;
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
public class ConfigInitializerContextListener implements ServletContextListener {

    public static final int INITIAL_VALUE = 0;
    private static Log log = LogFactory.getLog(ConfigInitializerContextListener.class);

    private enum PropertyName {
        PROPERTY_SIGNED_CERT_CN("SignedCertCN"),
        PROPERTY_SIGNED_CERT_NOT_BEFORE("SignedCertNotBefore"),
        PROPERTY_SIGNED_CERT_NOT_AFTER("SignedCertNotAfter"),
        PROPERTY_PASSWORD("Password"),
        PROPERTY_PRIVATE_KEY_PASSWORD("PrivateKeyPassword"),
        AUTH_POLICY("AuthPolicy"),
        DOMAIN("domain");

        private final String propertyName;

        PropertyName(final String propertyName) {
            this.propertyName = propertyName;
        }

        public String getValue() {
            return this.propertyName;
        }
    }

    /**
     * This method loads wap-provisioning file / property file, sets wap-provisioning file and
     * extracted properties as attributes in servlet context.
     *
     * @param servletContextEvent - Uses when servlet communicating with servlet container.
     */
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        ServletContext servletContext = servletContextEvent.getServletContext();
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
            log.error("Parser configuration failure while reading properties.xml.");
        } catch (SAXException e) {
            log.error("Parsing error occurred while reading properties.xml.");
        } catch (IOException e) {
            log.error("File reading error occurred while accessing properties.xml.");
        }

        String password = null;
        String privateKeyPassword = null;
        String signedCertCommonName = null;
        String authPolicy = null;
        String domain = null;
        int signedCertNotBeforeDate = INITIAL_VALUE;
        int signedCertNotAfterDate = INITIAL_VALUE;

        if (document != null) {
            password = document.getElementsByTagName(PropertyName.PROPERTY_PASSWORD.getValue()).item(0).
                    getTextContent();
            privateKeyPassword = document.getElementsByTagName(PropertyName.PROPERTY_PRIVATE_KEY_PASSWORD.getValue()).
                    item(0).getTextContent();
            signedCertCommonName =
                    document.getElementsByTagName(PropertyName.PROPERTY_SIGNED_CERT_CN.getValue()).item(0).
                            getTextContent();
            authPolicy = document.getElementsByTagName(PropertyName.AUTH_POLICY.getValue()).item(0).
                    getTextContent();
            signedCertNotBeforeDate = Integer.valueOf(document.getElementsByTagName(
                    PropertyName.PROPERTY_SIGNED_CERT_NOT_BEFORE.getValue()).item(0).getTextContent());
            signedCertNotAfterDate = Integer.valueOf(document.getElementsByTagName(
                    PropertyName.PROPERTY_SIGNED_CERT_NOT_AFTER.getValue()).item(0).getTextContent());
            domain = document.getElementsByTagName(PropertyName.DOMAIN.getValue()).item(0).getTextContent();

        }

        WindowsPluginProperties properties = new WindowsPluginProperties();
        properties.setKeyStorePassword(password);
        properties.setPrivateKeyPassword(privateKeyPassword);
        properties.setCommonName(signedCertCommonName);
        properties.setNotBeforeDays(signedCertNotBeforeDate);
        properties.setNotAfterDays(signedCertNotAfterDate);
        properties.setAuthPolicy(authPolicy);
        properties.setDomain(domain);
        servletContext.setAttribute(PluginConstants.WINDOWS_PLUGIN_PROPERTIES, properties);

        File wapProvisioningFile = new File(getClass().getClassLoader().getResource(
                PluginConstants.CertificateEnrolment.WAP_PROVISIONING_XML).getFile());
        servletContext.setAttribute(PluginConstants.CONTEXT_WAP_PROVISIONING_FILE, wapProvisioningFile);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

}
