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
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.w3c.dom.*;
import org.wso2.carbon.certificate.mgt.core.service.CertificateManagementServiceImpl;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.configuration.mgt.ConfigurationEntry;
import org.wso2.carbon.device.mgt.common.configuration.mgt.TenantConfiguration;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.CacheEntry;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.WindowsPluginProperties;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
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
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.util.CertificateSigningService;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.util.KeyStoreGenerator;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
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
import java.io.*;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.*;
import java.util.ArrayList;
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
    //get its value from tenant configuration.
    private static final int POLLING_FREQUENCY_POSITION = 27;
    private static Log log = LogFactory.getLog(CertificateEnrollmentServiceImpl.class);
    private PrivateKey privateKey;
    private X509Certificate rootCACertificate;
    //private File jksF;

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
            WindowsDeviceEnrolmentException {

        String headerBinarySecurityToken = null;
        List<Header> headers = getHeaders();
        for (Header headerElement : headers != null ? headers : null) {
            String nodeName = headerElement.getName().getLocalPart();
            if (nodeName.equals(PluginConstants.SECURITY)) {
                Element element = (Element) headerElement.getObject();
                headerBinarySecurityToken = element.getFirstChild().getNextSibling().getFirstChild().getTextContent();
            }
        }

        ServletContext ctx =
                (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
        File wapProvisioningFile = (File) ctx.getAttribute(PluginConstants.CONTEXT_WAP_PROVISIONING_FILE);

        WindowsPluginProperties windowsPluginProperties = (WindowsPluginProperties) ctx.getAttribute(
                PluginConstants.WINDOWS_PLUGIN_PROPERTIES);
        //////////////////////////////////////////////////////////////////////
//        String kPasssword;
//        int pollingFrequency;
//        String cmName = null;
//        String kStorePassword;
//        String pkPassword = null;
//        int notBefore = 0;
//        int notafter = 0;
//
//        try {
//            List<ConfigurationEntry> tenantConfigurations = getTenantConfiurationData();
//            for (ConfigurationEntry configurationEntry : tenantConfigurations) {
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.NOTIFIERFREQUENCY)) {
//                    Object o = configurationEntry.getName();
//                    if (o instanceof Integer) {
//                        pollingFrequency = (Integer) o;
//                    } else {
//                        throw new InvalidParameterException("Invalid value for Local polling time.");
//                    }
//                }
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.COMMONNAME)){
//                    cmName = configurationEntry.getValue().toString();
//                }
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.KEYSTOREPASSWORD)) {
//                    kStorePassword = configurationEntry.getValue().toString();
//                }
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.PRIVATEKEYPASSWORD)) {
//                    pkPassword = configurationEntry.getValue().toString();
//                }
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.NOTIFIERFREQUENCY)) {
//                    Object o = configurationEntry.getName();
//                    if (o instanceof Integer) {
//                        notBefore = (Integer) o;
//                    } else {
//                        throw new InvalidParameterException("Invalid value for certification expire date");
//                    }
//                }
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.NOTIFIERFREQUENCY)) {
//                    Object o = configurationEntry.getName();
//                    if (o instanceof Integer) {
//                        notafter = (Integer) o;
//                    } else {
//                        throw new InvalidParameterException("Invalid value for certification expire date");
//                    }
//                }
//                if (configurationEntry.getName().equals(PluginConstants.TenantConfigProperties.MDMCERT)) {
//                    Object o = configurationEntry.getName();
//                    if (o instanceof File) {
//                        jksF = (File) o;
//                    } else {
//                        throw new InvalidParameterException("Invalid File");
//                    }
//                }
//            }
//            List<java.io.Serializable> certPropertyList = new ArrayList<>();
//            certPropertyList.add(cmName);
//            certPropertyList.add(notBefore);
//            certPropertyList.add(notafter);
//        } catch (DeviceManagementException e) {
//            e.printStackTrace();
//        }
        /////////////////////////////////////////////////////////////////////
        String keyStorePassword = windowsPluginProperties.getKeyStorePassword();
        String privateKeyPassword = windowsPluginProperties.getPrivateKeyPassword();
        String commonName = windowsPluginProperties.getCommonName();
        int notBeforeDate = windowsPluginProperties.getNotBeforeDays();
        int notAfterDate = windowsPluginProperties.getNotAfterDays();

        List<java.io.Serializable> certPropertyList = new ArrayList<>();
        certPropertyList.add(commonName);
        certPropertyList.add(notBeforeDate);
        certPropertyList.add(notAfterDate);

        try {
            setRootCertAndKey(keyStorePassword, privateKeyPassword);
        }
        //Generic exception is caught here as there is no need of taking different actions for
        //different exceptions.
        catch (Exception e) {
            String msg = "Root certificate and private key couldn't be extracted from keystore.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }

        if (log.isDebugEnabled()) {
            log.debug("Received CSR from Device:" + binarySecurityToken);
        }
        String wapProvisioningFilePath = wapProvisioningFile.getPath();
        RequestSecurityTokenResponse requestSecurityTokenResponse =
                new RequestSecurityTokenResponse();
        requestSecurityTokenResponse.setTokenType(PluginConstants.CertificateEnrolment.TOKEN_TYPE);
        String encodedWap;
        try {
            encodedWap = prepareWapProvisioningXML(binarySecurityToken, certPropertyList,
                    wapProvisioningFilePath, headerBinarySecurityToken);
        }
        //Generic exception is caught here as there is no need of taking different actions for
        //different exceptions.
        catch (Exception e) {
            String msg = "Wap provisioning file couldn't be prepared.";
            log.error(msg, e);
            throw new WindowsDeviceEnrolmentException(msg, e);
        }
        RequestedSecurityToken requestedSecurityToken = new RequestedSecurityToken();
        BinarySecurityToken binarySecToken = new BinarySecurityToken();
        binarySecToken.setValueType(PluginConstants.CertificateEnrolment.VALUE_TYPE);
        binarySecToken.setEncodingType(PluginConstants.CertificateEnrolment.ENCODING_TYPE);
        binarySecToken.setToken(encodedWap);
        requestedSecurityToken.setBinarySecurityToken(binarySecToken);
        requestSecurityTokenResponse.setRequestedSecurityToken(requestedSecurityToken);
        requestSecurityTokenResponse.setRequestID(REQUEST_ID);
        response.value = requestSecurityTokenResponse;
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
     * Method for reading MDM Keystore and setting privateKey and rootCACertificate variables.
     *
     * @param storePassword - MDM Keystore password
     * @param keyPassword   - MDM Private key password
     * @throws KeyStoreGenerationException
     * @throws CertificateGenerationException
     */
    public void setRootCertAndKey(
            String storePassword, String keyPassword) throws KeyStoreGenerationException,
            CertificateGenerationException, IOException {
        String jksFilePath;
        KeyStore securityJKS;
        try {

            File jksFile = new File(getClass().getClassLoader().getResource(
                    PluginConstants.CertificateEnrolment.WSO2_MDM_JKS_FILE).getFile());

            jksFilePath = jksFile.getPath();
            securityJKS = KeyStoreGenerator.getKeyStore();
        } catch (KeyStoreGenerationException e) {
            String msg = "Cannot retrieve the MDM key store.";
            log.error(msg, e);
            throw new KeyStoreGenerationException(msg, e);
        }
        try {
            KeyStoreGenerator.loadToStore(securityJKS, storePassword.toCharArray(), jksFilePath);
        } catch (KeyStoreGenerationException e) {
            String msg = "Cannot load the MDM key store.";
            log.error(msg, e);
            throw new KeyStoreGenerationException(msg, e);
        }

        try {
            privateKey = (PrivateKey) securityJKS
                    .getKey(PluginConstants.CertificateEnrolment.CA_CERT, keyPassword.toCharArray());
        } catch (java.security.KeyStoreException e) {
            String msg = "Cannot generate private key due to Key store error.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        } catch (NoSuchAlgorithmException e) {
            String msg = "Requested cryptographic algorithm is not available in the environment.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        } catch (UnrecoverableKeyException e) {
            String msg = "Cannot recover private key.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }

        Certificate caCertificate;
        ByteArrayInputStream byteArrayInputStream;
        CertificateFactory certificateFactory;
        try {
            caCertificate = securityJKS.getCertificate(PluginConstants.CertificateEnrolment.CA_CERT);
            certificateFactory =
                    CertificateFactory.getInstance(PluginConstants.CertificateEnrolment.X_509);
            byteArrayInputStream = new ByteArrayInputStream(caCertificate.getEncoded());
        } catch (CertificateEncodingException e) {
            String msg = "Error occurred while encoding CA certificate.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        } catch (KeyStoreException e) {
            String msg = "Error occurred while accessing keystore for CA certificate retrieval.";
            log.error(msg, e);
            throw new KeyStoreGenerationException(msg, e);
        } catch (CertificateException e) {
            String msg = "Error occurred while initiating certificate factory for CA certificate " +
                    "retrieval.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }

        try {
            rootCACertificate =
                    (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
        } catch (CertificateException e) {
            String msg = "X509 CA certificate cannot be generated.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }
    }

    /**
     * This method prepares the wap-provisioning file by including relevant certificates etc
     *
     * @param binarySecurityToken     - CSR from device
     * @param certPropertyList        - property list for signed certificate
     * @param wapProvisioningFilePath - File path of wap-provisioning file
     * @return - base64 encoded final wap-provisioning file as a String
     * @throws CertificateGenerationException
     * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException
     */
    public String prepareWapProvisioningXML(
            String binarySecurityToken, List<java.io.Serializable> certPropertyList,
            String wapProvisioningFilePath, String headerBst) throws CertificateGenerationException,
            WAPProvisioningException {

        byte[] byteArrayBst = DatatypeConverter.parseBase64Binary(binarySecurityToken);
        byte[] byteArrayHeaderBST = DatatypeConverter.parseBase64Binary(headerBst);
        String decodedBST = new String(byteArrayHeaderBST);
        PKCS10CertificationRequest certificationRequest;
        try {
            certificationRequest = new PKCS10CertificationRequest(byteArrayBst);
        } catch (IOException e) {
            String msg = "CSR cannot be recovered.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }

        JcaPKCS10CertificationRequest csr = new JcaPKCS10CertificationRequest(certificationRequest);
        X509Certificate signedCertificate =
                CertificateSigningService.signCSR(csr, privateKey, rootCACertificate, certPropertyList);
        Base64 base64Encoder = new Base64();
        String rootCertEncodedString;
        try {
            rootCertEncodedString = base64Encoder.encodeAsString(rootCACertificate.getEncoded());
        } catch (CertificateEncodingException e) {
            String msg = "CA certificate cannot be encoded.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }
        String signedCertEncodedString;
        try {
            signedCertEncodedString = base64Encoder.encodeAsString(signedCertificate.getEncoded());
        } catch (CertificateEncodingException e) {
            String msg = "Singed certificate cannot be encoded.";
            log.error(msg, e);
            throw new CertificateGenerationException(msg, e);
        }

        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        String wapProvisioningString = null;
        try {
            InputStream csp = new ByteArrayInputStream(binarySecurityToken.getBytes("UTF-8"));
            CertificateManagementServiceImpl impl=  CertificateManagementServiceImpl.getInstance();
           // byte[] encodedvaluesigncert = impl.getPKIMessageSCEP(csp);

           // String encSigncert = new String(encodedvaluesigncert);////////signed certificate

            X509Certificate xxrootCertificate=  (X509Certificate)impl.getCACertificate(); //rootcertificate
            String  xxrootCertEncodedString = base64Encoder.encodeAsString(xxrootCertificate.getEncoded());


            X509Certificate xxsigncertificate = impl.getSignCertificateFromCSR(binarySecurityToken,
                    rootCACertificate, certPropertyList);
            String xxencodedSignString = base64Encoder.encodeAsString(xxsigncertificate .getEncoded());

/////////////////////////////////////////////////
        builder = domFactory.newDocumentBuilder();
        Document document = builder.parse(wapProvisioningFilePath);
        NodeList wapParm = document.getElementsByTagName(PluginConstants.CertificateEnrolment.PARM);
        Node caCertificatePosition = wapParm.item(CA_CERTIFICATE_POSITION);

        //Adding SHA1 CA certificate finger print to wap-provisioning xml.
        caCertificatePosition.getParentNode().getAttributes().getNamedItem(PluginConstants.
                CertificateEnrolment.TYPE).setTextContent(String.valueOf(
                //DigestUtils.sha1Hex(rootCACertificate.getEncoded())).toUpperCase());
                DigestUtils.sha1Hex(xxrootCertificate.getEncoded())).toUpperCase());

            //Adding encoded CA certificate to wap-provisioning file after removing new line
            // characters.
            NamedNodeMap rootCertAttributes = caCertificatePosition.getAttributes();
        Node rootCertNode =
                rootCertAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
        rootCertEncodedString = rootCertEncodedString.replaceAll("\n", "");
  //rootCertNode.setTextContent(rootCertEncodedString);
           rootCertNode.setTextContent(xxrootCertEncodedString);

        if (log.isDebugEnabled()) {
            log.debug("Root certificate: " + rootCertEncodedString);
        }

        Node signedCertificatePosition = wapParm.item(SIGNED_CERTIFICATE_POSITION);

        //Adding SHA1 signed certificate finger print to wap-provisioning xml.
        signedCertificatePosition.getParentNode().getAttributes().getNamedItem(PluginConstants.
                CertificateEnrolment.TYPE).setTextContent(String.valueOf(
                // DigestUtils.sha1Hex(signedCertificate.getEncoded())).toUpperCase());
                DigestUtils.sha1Hex(xxsigncertificate.getEncoded())).toUpperCase());

            /////////////////////new ///////////////////////////////////////////////
            //Adding encoded signed certificate to wap-provisioning file after removing new line
            // characters.
            NamedNodeMap clientCertAttributes = signedCertificatePosition.getAttributes();
        Node clientEncodedNode =
                clientCertAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
        signedCertEncodedString = signedCertEncodedString.replaceAll("\n", "");

       // clientEncodedNode.setTextContent(signedCertEncodedString);
            clientEncodedNode.setTextContent(xxencodedSignString);

        if (log.isDebugEnabled()) {
            log.debug("Signed certificate: " + signedCertEncodedString);
        }

        // Adding user name auth token to wap-provisioning xml
        Node userNameAuthPosition = wapParm.item(APPAUTH_USERNAME_POSITION);
        NamedNodeMap appSrvAttributes = userNameAuthPosition.getAttributes();
        Node aAUTHNAMENode = appSrvAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
        CacheEntry cacheentry = (CacheEntry) DeviceUtil.getCacheEntry(decodedBST);
        String username = cacheentry.getUsername();
        aAUTHNAMENode.setTextContent(cacheentry.getUsername());
        DeviceUtil.removeToken(decodedBST);
        String password = DeviceUtil.generateRandomToken();
        Node passwordAuthPosition = wapParm.item(APPAUTH_PASSWORD_POSITION);
        NamedNodeMap appSrvPasswordAttribute = passwordAuthPosition.getAttributes();
        Node aAUTHPasswordNode = appSrvPasswordAttribute.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
        aAUTHPasswordNode.setTextContent(password);
        String rstr = new SyncmlCredentials().generateRST(username, password);
        DeviceUtil.persistChallengeToken(rstr, "", username);

        // get device polling frequency from the tenant Configurations.
//            String pollingFrequency = null;
//            TenantConfiguration configuration = WindowsAPIUtils.getTenantConfiguration();
//            List<ConfigurationEntry> configurations = configuration.getConfiguration();
//            for (ConfigurationEntry configvalue : configurations) {
//                if (configvalue.getName().equals("notifierFrequency")) {
//                    pollingFrequency = configvalue.getValue().toString();
//                }
//            }
//            Node numberOfFirstRetries = wapParm.item(POLLING_FREQUENCY_POSITION);
//            NamedNodeMap pollingAttributes = numberOfFirstRetries.getAttributes();
//            Node pollvalue = pollingAttributes.getNamedItem(PluginConstants.CertificateEnrolment.VALUE);
//            pollvalue.setTextContent(pollingFrequency);
//            if (log.isDebugEnabled()) {
//                log.debug("Username: " + username + "Password: " + rstr);
//            }
        wapProvisioningString = convertDocumentToString(document);

        //Generic exception is caught here as there is no need of taking different actions for
        //different exceptions.
    } catch (Exception e) {
        String msg = "Problem occurred with wap-provisioning.xml file.";
        log.error(msg, e);
        throw new WAPProvisioningException(msg, e);
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

    private List<ConfigurationEntry> getTenantConfiurationData() throws DeviceManagementException {
        TenantConfiguration configuration = WindowsAPIUtils.getTenantConfiguration();
        return configuration.getConfiguration();
    }
}
