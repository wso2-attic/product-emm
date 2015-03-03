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

package org.wso2.carbon.mdm.mobileservices.windows.services.wstep.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WindowsDeviceEnrolmentException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.AdditionalContext;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.CertificateEnrollmentService;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.BinarySecurityToken;
import org.apache.commons.codec.binary.Base64;
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

import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.util.CertificateSigningService;
import org.apache.commons.codec.digest.DigestUtils;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.RequestSecurityTokenResponse;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.RequestedSecurityToken;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.util.KeyStoreGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.mdm.mobileservices.windows.common.beans.WindowsPluginProperties;

/**
 * Implementation class of CertificateEnrollmentService interface. This class implements MS-WSTEP
 * protocol.
 */
@WebService(endpointInterface = Constants.CERTIFICATE_ENROLLMENT_SERVICE_ENDPOINT,
		targetNamespace = Constants.DEVICE_ENROLLMENT_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentServiceImpl implements CertificateEnrollmentService {

	private static final int REQUEST_ID = 0;
	private static final int CA_CERTIFICATE_POSITION = 0;
	private static final int SIGNED_CERTIFICATE_POSITION = 1;
	private static Log log = LogFactory.getLog(CertificateEnrollmentServiceImpl.class);
	private PrivateKey privateKey;
	private X509Certificate rootCACertificate;

	@Resource
	private WebServiceContext context;

	/**
	 * This method implements MS-WSTEP for Certificate Enrollment Service.
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

		ServletContext ctx =
				(ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		File wapProvisioningFile = (File) ctx.getAttribute(Constants.CONTEXT_WAP_PROVISIONING_FILE);

		WindowsPluginProperties windowsPluginProperties = (WindowsPluginProperties)ctx.getAttribute(
				                                               Constants.WINDOWS_PLUGIN_PROPERTIES);
		String keyStorePassword = windowsPluginProperties.getKeyStorePassword();
		String privateKeyPassword = windowsPluginProperties.getPrivateKeyPassword();
		String commonName = windowsPluginProperties.getCommonName();
		int notBeforeDate = windowsPluginProperties.getNotBeforeDays();
		int notAfterDate = windowsPluginProperties.getNotAfterDays();

		List<java.io.Serializable> certPropertyList = new ArrayList<java.io.Serializable>();
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
		requestSecurityTokenResponse.setTokenType(Constants.CertificateEnrolment.TOKEN_TYPE);
		String encodedWap;
		try {
			encodedWap = prepareWapProvisioningXML(binarySecurityToken, certPropertyList,
			                                       wapProvisioningFilePath);
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
		binarySecToken.setValueType(Constants.CertificateEnrolment.VALUE_TYPE);
		binarySecToken.setEncodingType(Constants.CertificateEnrolment.ENCODING_TYPE);
		binarySecToken.setToken(encodedWap);
		requestedSecurityToken.setBinarySecurityToken(binarySecToken);
		requestSecurityTokenResponse.setRequestedSecurityToken(requestedSecurityToken);
		requestSecurityTokenResponse.setRequestID(REQUEST_ID);
		response.value = requestSecurityTokenResponse;
	}

	/**
	 * Method used to Convert the Document object into a String.
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
	 * @param storePassword - MDM Keystore password
	 * @param keyPassword   - MDM Private key password
	 * @throws KeyStoreGenerationException
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException
	 * @throws CertificateGenerationException
	 */
	public void setRootCertAndKey(
			String storePassword, String keyPassword) throws KeyStoreGenerationException,
	                              WAPProvisioningException, CertificateGenerationException {

		File JKSFile = new File(getClass().getClassLoader().getResource(
				Constants.CertificateEnrolment.WSO2_MDM_JKS_FILE).getFile());
		String JKSFilePath = JKSFile.getPath();
		KeyStore securityJKS;
		try {
			securityJKS = KeyStoreGenerator.getKeyStore();
		} catch (KeyStoreGenerationException e) {
			String msg = "Cannot retrieve the MDM key store.";
			log.error(msg, e);
			throw new KeyStoreGenerationException(msg, e);
		}

		try {
			KeyStoreGenerator.loadToStore(securityJKS, storePassword.toCharArray(), JKSFilePath);
		} catch (KeyStoreGenerationException e) {
			String msg = "Cannot load the MDM key store.";
			log.error(msg, e);
			throw new KeyStoreGenerationException(msg, e);
		}

		try {
			privateKey = (PrivateKey) securityJKS
					.getKey(Constants.CertificateEnrolment.CA_CERT, keyPassword.toCharArray());
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

		Certificate CACertificate;
		ByteArrayInputStream byteArrayInputStream;
		CertificateFactory certificateFactory;
		try {
			CACertificate = securityJKS.getCertificate(Constants.CertificateEnrolment.CA_CERT);
			certificateFactory =
					CertificateFactory.getInstance(Constants.CertificateEnrolment.X_509);
			byteArrayInputStream = new ByteArrayInputStream(CACertificate.getEncoded());
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
	 * @param binarySecurityToken     - CSR from device
	 * @param certPropertyList        - property list for signed certificate
	 * @param wapProvisioningFilePath - File path of wap-provisioning file
	 * @return - base64 encoded final wap-provisioning file as a String
	 * @throws CertificateGenerationException
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException
	 */
	public String prepareWapProvisioningXML(
			String binarySecurityToken, List<java.io.Serializable> certPropertyList,
			String wapProvisioningFilePath) throws CertificateGenerationException,
	                                               WAPProvisioningException {

		byte[] DERByteArray =
				DatatypeConverter.parseBase64Binary(binarySecurityToken);
		PKCS10CertificationRequest certificationRequest;
		try {
			certificationRequest = new PKCS10CertificationRequest(DERByteArray);
		} catch (IOException e) {
			String msg = "CSR cannot be recovered.";
			log.error(msg, e);
			throw new CertificateGenerationException(msg, e);
		}

		JcaPKCS10CertificationRequest CSRRequest = new JcaPKCS10CertificationRequest(certificationRequest);
		X509Certificate signedCertificate =
				CertificateSigningService.signCSR(CSRRequest, privateKey, rootCACertificate,
				                                  certPropertyList);
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
		String wapProvisioningString;
		try {
			builder = domFactory.newDocumentBuilder();
			Document document = builder.parse(wapProvisioningFilePath);
			NodeList wapParm = document.getElementsByTagName(Constants.CertificateEnrolment.PARM);
			Node CACertificatePosition = wapParm.item(CA_CERTIFICATE_POSITION);

			//Adding SHA1 CA certificate finger print to wap-provisioning xml.
			CACertificatePosition.getParentNode().getAttributes().getNamedItem(Constants.
					            CertificateEnrolment.TYPE).setTextContent(String.valueOf(
					DigestUtils.sha1Hex(rootCACertificate.getEncoded())).toUpperCase());

			//Adding encoded CA certificate to wap-provisioning file after removing new line
			// characters.
			NamedNodeMap rootCertAttributes = CACertificatePosition.getAttributes();
			Node rootCertNode =
					rootCertAttributes.getNamedItem(Constants.CertificateEnrolment.VALUE);
			rootCertEncodedString = rootCertEncodedString.replaceAll("\n", "");
			rootCertNode.setTextContent(rootCertEncodedString);

			if (log.isDebugEnabled()) {
				log.debug("Root certificate: " + rootCertEncodedString);
			}

			Node signedCertificatePosition = wapParm.item(SIGNED_CERTIFICATE_POSITION);

			//Adding SHA1 signed certificate finger print to wap-provisioning xml.
			signedCertificatePosition.getParentNode().getAttributes().getNamedItem(Constants.
					            CertificateEnrolment.TYPE).setTextContent(String.valueOf(
					DigestUtils.sha1Hex(signedCertificate.getEncoded())).toUpperCase());

			//Adding encoded signed certificate to wap-provisioning file after removing new line
			// characters.
			NamedNodeMap clientCertAttributes = signedCertificatePosition.getAttributes();
			Node clientEncodedNode =
					clientCertAttributes.getNamedItem(Constants.CertificateEnrolment.VALUE);
			signedCertEncodedString = signedCertEncodedString.replaceAll("\n", "");
			clientEncodedNode.setTextContent(signedCertEncodedString);

			if (log.isDebugEnabled()) {
				log.debug("Signed certificate: " + signedCertEncodedString);
			}
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
}
