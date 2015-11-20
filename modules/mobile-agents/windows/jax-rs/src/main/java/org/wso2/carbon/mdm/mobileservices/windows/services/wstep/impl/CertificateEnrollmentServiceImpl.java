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

package org.wso2.carbon.mdm.mobileservices.windows.services.wstep.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import org.apache.cxf.message.Message;
import org.w3c.dom.*;
import org.wso2.carbon.certificate.mgt.core.exception.KeystoreException;
import org.wso2.carbon.certificate.mgt.core.service.CertificateManagementServiceImpl;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.SyncmlMessageFormatException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.DeviceUtil;
import org.wso2.carbon.mdm.mobileservices.windows.common.util.WindowsAPIUtils;
import org.wso2.carbon.mdm.mobileservices.windows.operations.util.SyncmlCredentials;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.CertificateEnrollmentService;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.AdditionalContext;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.BinarySecurityToken;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.RequestSecurityTokenResponse;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.RequestedSecurityToken;
import org.xml.sax.SAXException;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Implementation class of CertificateEnrollmentService interface. This class implements MS-WSTEP
 * protocol.
 */
@WebService(endpointInterface = PluginConstants.CERTIFICATE_ENROLLMENT_SERVICE_ENDPOINT,
        targetNamespace = PluginConstants.DEVICE_ENROLLMENT_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentServiceImpl implements CertificateEnrollmentService {

    private static final int REQUEST_ID = 0;
    private static final int CA_CERTIFICATE_POSITION = 0;
    private static final int SIGNED_CERTIFICATE_POSITION = 1;
    private static final int APPAUTH_USERNAME_POSITION = 21;
    private static final int APPAUTH_PASSWORD_POSITION = 22;
    private static final int POLLING_FREQUENCY_POSITION = 27;
    private static Log log = LogFactory.getLog(CertificateEnrollmentServiceImpl.class);
    private X509Certificate rootCACertificate;
    private String pollingFrequency;

    @Resource
    private WebServiceContext context;

    /**
     * This method implements MS-WSTEP for Certificate Enrollment Service.
     *
     * @param tokenType           - Device Enrolment Token type is received via device
     * @param requestType         - WS-Trust request type
     * @param binarySecurityToken - CSR from device
     * @param additionalContext   - Device type and OS version is received
     * @param response            - Response will include wap-provisioning xml
     */
    @Override
    public void requestSecurityToken(String tokenType, String requestType,
                                     String binarySecurityToken,
                                     AdditionalContext additionalContext,
                                     Holder<RequestSecurityTokenResponse> response) throws
            WindowsDeviceEnrolmentException, UnsupportedEncodingException, WAPProvisioningException {

        String headerBinarySecurityToken = null;
        List<Header> headers = getHeaders();
        for (Header headerElement : headers != null ? headers : null) {
            String nodeName = headerElement.getName().getLocalPart();
            if (nodeName.equals(PluginConstants.SECURITY)) {
                Element element = (Element) headerElement.getObject();
                headerBinarySecurityToken = element.getFirstChild().getNextSibling().getFirstChild().getTextContent();
            }
        }
        List<ConfigurationEntry> tenantConfigurations = null;
        try {
            if (getTenantConfigurationData() != null) {
                tenantConfigurations = getTenantConfigurationData();
                for (ConfigurationEntry configurationEntry : tenantConfigurations) {
                    if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.
                            NOTIFIER_FREQUENCY)) {
                        pollingFrequency = configurationEntry.getValue().toString();
                    } else {
                        pollingFrequency = PluginConstants.TenantConfigProperties.DEFAULT_FREQUENCY;
                    }
                }
            } else {
                pollingFrequency = PluginConstants.TenantConfigProperties.DEFAULT_FREQUENCY;
                String msg = "Tenant configurations are not initialized yet.";
                log.error(msg);
            }
        } catch (DeviceManagementException e) {
            String msg = "Error occurred in while getting tenant configurations.";
            log.error(msg);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }
        ServletContext ctx =
                (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        File wapProvisioningFile = (File) ctx.getAttribute(PluginConstants.CONTEXT_WAP_PROVISIONING_FILE);

        if (log.isDebugEnabled()) {
            log.debug("Received CSR from Device:" + binarySecurityToken);
        }
        String wapProvisioningFilePath = wapProvisioningFile.getPath();
        RequestSecurityTokenResponse requestSecurityTokenResponse =
                new RequestSecurityTokenResponse();
        requestSecurityTokenResponse.setTokenType(PluginConstants.CertificateEnrolment.TOKEN_TYPE);
        String encodedWap;
        try {
            encodedWap = prepareWapProvisioningXML(binarySecurityToken,
                    wapProvisioningFilePath, headerBinarySecurityToken);
            RequestedSecurityToken requestedSecurityToken = new RequestedSecurityToken();
            BinarySecurityToken binarySecToken = new BinarySecurityToken();
            binarySecToken.setValueType(PluginConstants.CertificateEnrolment.VALUE_TYPE);
            binarySecToken.setEncodingType(PluginConstants.CertificateEnrolment.ENCODING_TYPE);
            binarySecToken.setToken(encodedWap);
            requestedSecurityToken.setBinarySecurityToken(binarySecToken);
            requestSecurityTokenResponse.setRequestedSecurityToken(requestedSecurityToken);
            requestSecurityTokenResponse.setRequestID(REQUEST_ID);
            response.value = requestSecurityTokenResponse;
        } catch (CertificateGenerationException e) {
            String msg = "Problem occurred in generating certificate.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        } catch (WAPProvisioningException e) {
            String msg = "Problem occurred in generating wap-provisioning file.";
            log.error(msg, e);
            throw new WAPProvisioningException(msg, e);
        } finally {
            PrivilegedCarbonContext.endTenantFlow();
        }
    }

    /**
     * Method used to Convert the Document object into a String.
     *
     * @param document - Wap provisioning XML document
     * @return - String representation of wap provisioning XML document
     * @throws TransformerException
     */
    private String convertDocumentToString(Document document) throws TransformerException {
        DOMSource DOMSource = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(DOMSource, streamResult);

        return stringWriter.toString();
    }

    /**
     * This method prepares the wap-provisioning file by including relevant certificates etc
     *
     * @param binarySecurityToken     - CSR from device
     * @param wapProvisioningFilePath - File path of wap-provisioning file
     * @return - base64 encoded final wap-provisioning file as a String
     * @throws CertificateGenerationException
     * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException
     */
    public String prepareWapProvisioningXML(
            String binarySecurityToken,
            String wapProvisioningFilePath, String headerBst) throws CertificateGenerationException,
            WAPProvisioningException, WindowsDeviceEnrolmentException {

        String rootCertEncodedString;
        String signedCertEncodedString;
        X509Certificate signedCertificate;

        CertificateManagementServiceImpl impl = CertificateManagementServiceImpl.getInstance();
        Base64 base64Encoder = new Base64();
        try {
            rootCACertificate = (X509Certificate) impl.getCACertificate();
            rootCertEncodedString = base64Encoder.encodeAsString(rootCACertificate.getEncoded());
        } catch (KeystoreException e) {
            String msg = "CA certificate cannot be generated";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        } catch (CertificateEncodingException e) {
            String msg = "CA certificate cannot be encoded.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }

        try {
            signedCertificate = impl.getSignedCertificateFromCSR(binarySecurityToken);
            signedCertEncodedString = base64Encoder.encodeAsString(signedCertificate.getEncoded());
        } catch (CertificateEncodingException e) {
            String msg = "Singed certificate cannot be encoded.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        } catch (KeystoreException e) {
            String msg = "CA certificate cannot be generated";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        String wapProvisioningString = null;
        try {
            builder = domFactory.newDocumentBuilder();

            Document document = builder.parse(wapProvisioningFilePath);
            NodeList wapParm = document.getElementsByTagName(PluginConstants.CertificateEnrolment.PARM);
            Node caCertificatePosition = wapParm.item(CA_CERTIFICATE_POSITION);

            //Adding SHA1 CA certificate finger print to wap-provisioning xml.
            caCertificatePosition.getParentNode().getAttributes().getNamedItem(PluginConstants.
                    CertificateEnrolment.TYPE).setTextContent(String.valueOf(
                    DigestUtils.sha1Hex(rootCACertificate.getEncoded())).toUpperCase());
            //Adding encoded CA certificate to wap-provisioning file after removing new line
            // characters.
            NamedNodeMap rootCertAttributes = caCertificatePosition.getAttributes();
            Node rootCertNode =
                    rootCertAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
            rootCertEncodedString = rootCertEncodedString.replaceAll("\n", "");
            rootCertNode.setTextContent(rootCertEncodedString);

            if (log.isDebugEnabled()) {
                log.debug("Root certificate: " + rootCertEncodedString);
            }

            Node signedCertificatePosition = wapParm.item(SIGNED_CERTIFICATE_POSITION);

            //Adding SHA1 signed certificate finger print to wap-provisioning xml.
            signedCertificatePosition.getParentNode().getAttributes().getNamedItem(PluginConstants.
                    CertificateEnrolment.TYPE).setTextContent(String.valueOf(
                    DigestUtils.sha1Hex(signedCertificate.getEncoded())).toUpperCase());

            //Adding encoded signed certificate to wap-provisioning file after removing new line
            // characters.
            NamedNodeMap clientCertAttributes = signedCertificatePosition.getAttributes();
            Node clientEncodedNode =
                    clientCertAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
            signedCertEncodedString = signedCertEncodedString.replaceAll("\n", "");

            clientEncodedNode.setTextContent(signedCertEncodedString);
            if (log.isDebugEnabled()) {
                log.debug("Signed certificate: " + signedCertEncodedString);
            }

            // Adding user name auth token to wap-provisioning xml
            Node userNameAuthPosition = wapParm.item(APPAUTH_USERNAME_POSITION);
            NamedNodeMap appServerAttribute = userNameAuthPosition.getAttributes();
            Node authNameNode = appServerAttribute.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
            CacheEntry cacheEntry = (CacheEntry) DeviceUtil.getCacheEntry(headerBst);
            String userName = cacheEntry.getUsername();
            authNameNode.setTextContent(cacheEntry.getUsername());
            DeviceUtil.removeToken(headerBst);
            String password = DeviceUtil.generateRandomToken();
            Node passwordAuthPosition = wapParm.item(APPAUTH_PASSWORD_POSITION);
            NamedNodeMap appSrvPasswordAttribute = passwordAuthPosition.getAttributes();
            Node authPasswordNode = appSrvPasswordAttribute.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
            authPasswordNode.setTextContent(password);
            String requestSecurityTokenResponse = new SyncmlCredentials().generateRST(userName, password);
            DeviceUtil.persistChallengeToken(requestSecurityTokenResponse, null, userName);

            // Get device polling frequency from the tenant Configurations.
            Node numberOfFirstRetries = wapParm.item(POLLING_FREQUENCY_POSITION);
            NamedNodeMap pollingAttributes = numberOfFirstRetries.getAttributes();
            Node pollValue = pollingAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
            pollValue.setTextContent(pollingFrequency);
            if (log.isDebugEnabled()) {
                log.debug("Username: " + userName + "Password: " + requestSecurityTokenResponse);
            }
            wapProvisioningString = convertDocumentToString(document);
        } catch (ParserConfigurationException e) {
            String msg = "Problem occurred in parsing wap-provisioning.xml file.";
            log.error(msg, e);
            throw new WAPProvisioningException(msg, e);
        } catch (DeviceManagementException e) {
            String msg = "Error occurred in while getting CA and Root certificates.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        } catch (CertificateEncodingException e) {
            String msg = "Error occurred in while encoding certificates.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        } catch (UnsupportedEncodingException e) {
            String msg = "Error occurred in while encoding wap-provisioning file.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        } catch (SAXException e) {
            String msg = "Error occurred in while parsing wap-provisioning.xml file.";
            log.error(msg, e);
            throw new WAPProvisioningException(msg, e);
        } catch (TransformerException e) {
            String msg = "Error occurred in while transforming wap-provisioning.xml file.";
            log.error(msg, e);
            throw new WAPProvisioningException(msg, e);
        } catch (IOException e) {
            String msg = "Error occurred in while getting wap-provisioning.xml file.";
            log.error(msg, e);
            throw new WAPProvisioningException(msg, e);
        } catch (SyncmlMessageFormatException e) {
            String msg = "Error occurred in while getting CA and Root certificates.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }
        return base64Encoder.encodeAsString(wapProvisioningString.getBytes());
    }

    /**
     * This method get the soap request header contents
     *
     * @return Header object type,soap header tag list
     */
    private List<Header> getHeaders() {
        MessageContext messageContext = context.getMessageContext();
        if (messageContext == null || !(messageContext instanceof WrappedMessageContext)) {
            return null;
        }
        Message message = ((WrappedMessageContext) messageContext).getWrappedMessage();
        return CastUtils.cast((List<?>) message.get(Header.HEADER_LIST));
    }

    /**
     * This method is used to get tenant configurations.
     *
     * @return List of Configurations entries.
     * @throws DeviceManagementException
     */
    private List<ConfigurationEntry> getTenantConfigurationData() throws DeviceManagementException {
        if (WindowsAPIUtils.getTenantConfiguration() != null) {
            TenantConfiguration configuration = WindowsAPIUtils.getTenantConfiguration();
            return configuration.getConfiguration();
        } else {
            return null;
        }
    }
}
