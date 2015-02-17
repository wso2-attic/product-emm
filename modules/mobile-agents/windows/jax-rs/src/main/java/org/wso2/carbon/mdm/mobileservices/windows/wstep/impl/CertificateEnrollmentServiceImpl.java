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
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.PropertyFileReadingException;
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
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

	JcaPKCS10CertificationRequest csrReq;

	PKCS10CertificationRequest certificationRequest;

	String wapProvisioningXmlFile;
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

	/**
	 * @param TokenType           - Device Enrolment Token type is received via device
	 * @param RequestType
	 * @param BinarySecurityToken - CSR from device
	 * @param AdditionalContext   - Device type and OS version is received
	 * @param response            - Response will include wap-provisioning xml
	 */
	@Override public void RequestSecurityToken(String TokenType, String RequestType,
	                                           String BinarySecurityToken,
	                                           AdditionalContext AdditionalContext,
	                                           Holder<RequestSecurityTokenResponse> response)
			throws java.security.KeyStoreException, CertificateException, NoSuchAlgorithmException,
			       KeyStoreGenerationException, UnrecoverableKeyException,
			       PropertyFileReadingException {

		String encodedWap = null;

		certificateSign();

		if (logger.isDebugEnabled()) {
			logger.debug("Received CSR from Device:" + BinarySecurityToken);
		}

		File file =
				new File(getClass().getClassLoader().getResource(Constants.WAP_PROVISIONING_XML).getFile());
		wapProvisioningXmlFile = file.getPath();

		RequestSecurityTokenResponse rs = new RequestSecurityTokenResponse();
		rs.setTokenType(Constants.TOKEN_TYPE);

		try {
			byte[] derByteArray =
					javax.xml.bind.DatatypeConverter.parseBase64Binary(BinarySecurityToken);
			certificationRequest = new PKCS10CertificationRequest(derByteArray);

			csrReq = new JcaPKCS10CertificationRequest(certificationRequest);

			X509Certificate signedCert = CertificateSigningService
					.signCSR(csrReq, privateKey, rootCACertificate);

			if (logger.isDebugEnabled()) {
				logger.debug("Public key of Signed Certificate :" + signedCert.getPublicKey() +
				             "\nPublic key of CSR :" + csrReq.getPublicKey());
			}

			BASE64Encoder base64Encoder = new BASE64Encoder();
			String rootCertEncodedString = base64Encoder.encode(rootCACertificate.getEncoded());
			String signedCertEncoded = base64Encoder.encode(signedCert.getEncoded());

			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document dDoc = builder.parse(wapProvisioningXmlFile);

			NodeList wapParm = dDoc.getElementsByTagName(Constants.PARM);

			wapParm.item(0).getParentNode().getAttributes().getNamedItem(Constants.TYPE).setTextContent(
					String.valueOf(DigestUtils.shaHex(rootCACertificate.getEncoded()))
					      .toUpperCase());

			NamedNodeMap rootCertAttributes = wapParm.item(0).getAttributes();
			Node b64Encoded = rootCertAttributes.getNamedItem(Constants.VALUE);
			rootCertEncodedString = rootCertEncodedString.replaceAll("\n", "");
			b64Encoded.setTextContent(rootCertEncodedString);

			if (logger.isDebugEnabled()) {
				logger.debug("Root certificate:" + rootCertEncodedString);
			}

			wapParm.item(1).getParentNode().getAttributes().getNamedItem(Constants.TYPE).setTextContent(
					String.valueOf(DigestUtils.shaHex(signedCert.getEncoded())).toUpperCase());

			NamedNodeMap clientCertAttributes = wapParm.item(1).getAttributes();
			Node b64CliendEncoded = clientCertAttributes.getNamedItem(Constants.VALUE);
			signedCertEncoded = signedCertEncoded.replaceAll("\n", "");
			b64CliendEncoded.setTextContent(signedCertEncoded);

			if (logger.isDebugEnabled()) {
				logger.debug("Signed certificate:" + signedCertEncoded);
			}

			String wapProvisioning = convertDocumentToString(dDoc);
			encodedWap = base64Encoder.encode(wapProvisioning.getBytes());

		} catch (Exception e) {
			e.printStackTrace();
		}

		RequestedSecurityToken rst = new RequestedSecurityToken();
		BinarySecurityToken BinarySecToken = new BinarySecurityToken();
		BinarySecToken.setValueType(
				Constants.VALUE_TYPE);
		BinarySecToken.setEncodingType(
				Constants.ENCODING_TYPE);
		BinarySecToken.setToken(encodedWap);
		rst.setBinarySecurityToken(BinarySecToken);

		rs.setRequestedSecurityToken(rst);
		rs.setRequestID(0);
		response.value = rs;

	}

	/**
	 *
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
		String wapProvisioning = writer.toString();

		return wapProvisioning;

	}

	/**
	 * Method for setting privateKey and rootCACertificate variables.
	 * @throws KeyStoreGenerationException
	 * @throws java.security.KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws CertificateException
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.PropertyFileReadingException
	 */
	public void certificateSign()
			throws KeyStoreGenerationException, java.security.KeyStoreException,
			       NoSuchAlgorithmException,
			       UnrecoverableKeyException, CertificateException, PropertyFileReadingException {

		File file = new File(getClass().getClassLoader().getResource(Constants.WSO2EMM_JKS_FILE).getFile());
		String jksPath = file.getPath();

		KeyStore securityJks = null;
		try {
			securityJks = KeyStoreGenerator.getKeyStore();
		} catch (KeyStoreGenerationException e) {
			throw new KeyStoreGenerationException("Cannot retrieve the MDM key store");
		}

		String pass = getCredentials(Constants.EMMJKS);
		String passem = getCredentials(Constants.EMMPRIVATEKEY);


		try {
			KeyStoreGenerator.loadToStore(securityJks, pass.toCharArray(), jksPath);
		} catch (KeyStoreGenerationException e) {
			throw new KeyStoreGenerationException("Cannot load the MDM key store");
		}


		PrivateKey privateKeyCA = null;
		try {
			privateKeyCA = (PrivateKey) securityJks.getKey(Constants.CACERT, passem.toCharArray());
		} catch (java.security.KeyStoreException e) {
			throw new java.security.KeyStoreException(
					"Cannot generate private key due to Key store error");
		} catch (NoSuchAlgorithmException e) {
			throw new NoSuchAlgorithmException();
		} catch (UnrecoverableKeyException e) {
			throw new UnrecoverableKeyException("Cannot recover private key");
		}

		privateKey = privateKeyCA;

		Certificate cartificateCA = securityJks.getCertificate(Constants.CACERT);
		CertificateFactory cf = null;
		try {
			cf = CertificateFactory.getInstance(Constants.X_509);
		} catch (CertificateException e) {
			throw new CertificateException("Cannot initiate certificate factory");
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(cartificateCA.getEncoded());
		X509Certificate cartificateCAX509 = (X509Certificate) cf.generateCertificate(bais);

		rootCACertificate = cartificateCAX509;

	}

	/**
	 * Method for reading the property XML file and returning the required passwords.
	 * @param entity - Entity which needs to get the password from property xml file
	 * @return - Entity password
	 * @throws PropertyFileReadingException
	 */
	private String getCredentials(String entity) throws PropertyFileReadingException {

		String entityPassword=null;

			File propertyFile =
					new File(getClass().getClassLoader().getResource(Constants.PROPERTIES_XML).getFile());
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new PropertyFileReadingException("XML parsing configuration exception");
		}

		Document document;
		try {
			document = docBuilder.parse(propertyFile);
		} catch (SAXException e) {
			throw new PropertyFileReadingException("XML Parsing Exception");
		} catch (IOException e) {
			throw new PropertyFileReadingException("XML property file reading exception");
		}

		if(entity.equals(Constants.EMMJKS_ENTRY)){
			entityPassword=document.getElementsByTagName(Constants.EMMPASSWORD).item(0).getTextContent();
		}
		else if(entity.equals(Constants.EMMPRIVATEKEY_ENTRY)){
			entityPassword=document.getElementsByTagName(Constants.EMMPRIVATEKEYPASSWORD).item(0).getTextContent();
		}

    return entityPassword;

	}

}
