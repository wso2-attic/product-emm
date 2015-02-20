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

package org.wso2.carbon.mdm.mobileservices.windows.wstep.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.PropertyFileException;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.beans.AdditionalContext;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.CertificateEnrollmentService;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.beans.BinarySecurityToken;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingType;
import javax.xml.ws.Holder;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.SOAPBinding;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.util.CertificateSigningService;
import org.apache.commons.codec.digest.DigestUtils;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.beans.RequestSecurityTokenResponse;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.beans.RequestedSecurityToken;
import org.wso2.carbon.mdm.mobileservices.windows.wstep.util.KeyStoreGenerator;
import org.apache.log4j.Logger;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import sun.misc.BASE64Encoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;

/**
 * Implementation class of CertificateEnrollmentService interface. This class implements MS-WSTEP protocol.
 */
@WebService(endpointInterface = Constants.CERTIFICATE_ENROLLMENT_SERVICE_ENDPOINT, targetNamespace = Constants.DEVICE_ENROLLMENT_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentServiceImpl implements CertificateEnrollmentService {

	private Logger logger = Logger.getLogger(CertificateEnrollmentServiceImpl.class);

	PrivateKey privateKey;
	X509Certificate rootCACertificate;

	JcaPKCS10CertificationRequest CSRRequest;

	PKCS10CertificationRequest certificationRequest;

	String wapProvisioningFilePath;
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

	/**
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
	                                           Holder<RequestSecurityTokenResponse> response)
			throws KeyStoreGenerationException, PropertyFileException, CertificateGenerationException {

		String encodedWap;

		certificateSign();

		if (logger.isDebugEnabled()) {
			logger.debug("Received CSR from Device:" + binarySecurityToken);
		}

		File wapProvisioningFile = new File(getClass().getClassLoader().getResource(Constants.WAP_PROVISIONING_XML).getFile());
		wapProvisioningFilePath = wapProvisioningFile.getPath();

		RequestSecurityTokenResponse requestSecurityTokenResponse = new RequestSecurityTokenResponse();
		requestSecurityTokenResponse.setTokenType(Constants.TOKEN_TYPE);

		byte[] DERByteArray = javax.xml.bind.DatatypeConverter.parseBase64Binary(binarySecurityToken);

		try {
			certificationRequest = new PKCS10CertificationRequest(DERByteArray);
		} catch (IOException e) {
			throw new CertificateGenerationException("CSR cannot be recovered.", e);
		}

		CSRRequest = new JcaPKCS10CertificationRequest(certificationRequest);

		X509Certificate signedCertificate = CertificateSigningService.signCSR(CSRRequest, privateKey, rootCACertificate);

		BASE64Encoder base64Encoder = new BASE64Encoder();

		String rootCertEncodedString;
		try {
			rootCertEncodedString = base64Encoder.encode(rootCACertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CertificateGenerationException("CA certificate cannot be encoded.",e);
		}

		String signedCertEncodedString;
		try {
			signedCertEncodedString = base64Encoder.encode(signedCertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CertificateGenerationException("Singed certificate cannot be encoded.",e);
		}

		DocumentBuilder builder;
		String wapProvisioningString;
		try {
			builder = domFactory.newDocumentBuilder();
			Document dDoc = builder.parse(wapProvisioningFilePath);

			NodeList wapParm = dDoc.getElementsByTagName(Constants.PARM);

			wapParm.item(0).getParentNode().getAttributes().getNamedItem(Constants.TYPE).setTextContent(
					String.valueOf(DigestUtils.shaHex(rootCACertificate.getEncoded()))
					      .toUpperCase());

			NamedNodeMap rootCertAttributes = wapParm.item(0).getAttributes();
			Node rootCertNode = rootCertAttributes.getNamedItem(Constants.VALUE);
			rootCertEncodedString = rootCertEncodedString.replaceAll("\n", "");
			rootCertNode.setTextContent(rootCertEncodedString);

			if (logger.isDebugEnabled()) {
				logger.debug("Root certificate:" + rootCertEncodedString);
			}

			wapParm.item(1).getParentNode().getAttributes().getNamedItem(Constants.TYPE)
			       .setTextContent(
					       String.valueOf(DigestUtils.shaHex(signedCertificate.getEncoded()))
					             .toUpperCase());

			NamedNodeMap clientCertAttributes = wapParm.item(1).getAttributes();
			Node clientEncodedNode = clientCertAttributes.getNamedItem(Constants.VALUE);
			signedCertEncodedString = signedCertEncodedString.replaceAll("\n", "");
			clientEncodedNode.setTextContent(signedCertEncodedString);

			if (logger.isDebugEnabled()) {
				logger.debug("Signed certificate:" + signedCertEncodedString);
			}

			wapProvisioningString = convertDocumentToString(dDoc);

		    } catch (Exception e) {
			  throw new PropertyFileException("Problem occurred with wap-provisioning.xml file.", e);
		    }

		encodedWap = base64Encoder.encode(wapProvisioningString.getBytes());

		RequestedSecurityToken requestedSecurityToken = new RequestedSecurityToken();
		BinarySecurityToken binarySecToken = new BinarySecurityToken();
		binarySecToken.setValueType(Constants.VALUE_TYPE);
		binarySecToken.setEncodingType(Constants.ENCODING_TYPE);
		binarySecToken.setToken(encodedWap);
		requestedSecurityToken.setBinarySecurityToken(binarySecToken);

		requestSecurityTokenResponse.setRequestedSecurityToken(requestedSecurityToken);
		requestSecurityTokenResponse.setRequestID(0);
		response.value = requestSecurityTokenResponse;

	}

	/**
	 * @param document - Wap provisioning XML document
	 * @return - String representation of wap provisioning XML document
	 * @throws Exception
	 */
	private String convertDocumentToString(Document document) throws Exception {
		DOMSource domSource = new DOMSource(document);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		String wapProvisioningString = writer.toString();

		return wapProvisioningString;
	}

	/**
	 * Method for setting privateKey and rootCACertificate variables.
	 * @throws KeyStoreGenerationException
	 * @throws PropertyFileException
	 * @throws CertificateGenerationException
	 */
	public void certificateSign()
			throws KeyStoreGenerationException,PropertyFileException,CertificateGenerationException {

		File JKSFile = new File(getClass().getClassLoader().getResource(Constants.WSO2EMM_JKS_FILE).getFile());
		String JKSFilePath = JKSFile.getPath();

		KeyStore securityJKS;
		try {
			securityJKS = KeyStoreGenerator.getKeyStore();
		} catch (KeyStoreGenerationException e) {
			throw new KeyStoreGenerationException("Cannot retrieve the MDM key store.", e);
		}

		String storePassword = getCredentials(Constants.EMMJKS);
		String keyPassword = getCredentials(Constants.EMMPRIVATEKEY);

		try {
			KeyStoreGenerator.loadToStore(securityJKS, storePassword.toCharArray(), JKSFilePath);
		} catch (KeyStoreGenerationException e) {
			throw new KeyStoreGenerationException("Cannot load the MDM key store.", e);
		}

		PrivateKey CAPrivateKey;
		try {
			CAPrivateKey = (PrivateKey) securityJKS.getKey(Constants.CACERT, keyPassword.toCharArray());
		} catch (java.security.KeyStoreException e) {
			throw new CertificateGenerationException("Cannot generate private key due to Key store error.",e);
		} catch (NoSuchAlgorithmException e){
			throw new CertificateGenerationException("Requested cryptographic algorithm is not available in the environment.",e);
		} catch (UnrecoverableKeyException e) {
			throw new CertificateGenerationException("Cannot recover private key.",e);
		}


		privateKey = CAPrivateKey;

		Certificate CACertificate;
		try {
			CACertificate = securityJKS.getCertificate(Constants.CACERT);
		} catch (KeyStoreException e) {
			throw new KeyStoreGenerationException("Keystore cannot be accessed.",e);
		}
		CertificateFactory certificateFactory;

		try {
			certificateFactory = CertificateFactory.getInstance(Constants.X_509);
		} catch (CertificateException e) {
			throw new CertificateGenerationException("Cannot initiate certificate factory.",e);
		}

		ByteArrayInputStream byteArrayInputStream;
		try {
			byteArrayInputStream = new ByteArrayInputStream(CACertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CertificateGenerationException("CA certificate cannot be encoded.",e);
		}

		X509Certificate X509CACertificate;
		try {
			X509CACertificate = (X509Certificate) certificateFactory.generateCertificate(byteArrayInputStream);
		} catch (CertificateException e) {
			throw new CertificateGenerationException("X509 CA certificate cannot be generated.");
		}

		rootCACertificate = X509CACertificate;

	}

	/**
	 * Method for reading the property XML file and returning the required passwords.
	 *
	 * @param entity - Entity which needs to get the password from property xml file
	 * @return - Entity password
	 * @throws PropertyFileException
	 */
	private String getCredentials(String entity) throws PropertyFileException {

		String entityPassword = null;

		File propertyFile = new File(getClass().getClassLoader().getResource(Constants.PROPERTIES_XML).getFile());

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new PropertyFileException("XML parsing configuration exception.", e);
		}

		Document document;
		try {
			document = docBuilder.parse(propertyFile);
		} catch (SAXException e) {
			throw new PropertyFileException("XML Parsing Exception", e);
		} catch (IOException e) {
			throw new PropertyFileException("XML property file reading exception.", e);
		}

		if (Constants.EMMJKS_ENTRY.equals(entity)) {
			entityPassword =
					document.getElementsByTagName(Constants.EMMPASSWORD).item(0).getTextContent();
		} else if (Constants.EMMPRIVATEKEY_ENTRY.equals(entity)) {
			entityPassword = document.getElementsByTagName(Constants.EMMPRIVATEKEYPASSWORD).item(0).getTextContent();
		}
		return entityPassword;

	}

}
