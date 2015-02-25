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
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.XMLFileOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.AdditionalContext;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.CertificateEnrollmentService;
import org.wso2.carbon.mdm.mobileservices.windows.services.wstep.beans.BinarySecurityToken;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
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
import org.apache.commons.logging.LogFactory;import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.BASE64Encoder;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation class of CertificateEnrollmentService interface. This class implements MS-WSTEP protocol.
 */
@WebService(endpointInterface = Constants.CERTIFICATE_ENROLLMENT_SERVICE_ENDPOINT, targetNamespace = Constants.DEVICE_ENROLLMENT_SERVICE_TARGET_NAMESPACE)
@Addressing(enabled = true, required = true)
@BindingType(value = SOAPBinding.SOAP12HTTP_BINDING)
public class CertificateEnrollmentServiceImpl implements CertificateEnrollmentService {

	public static final int FIRST_ITEM = 0;
	private static final int REQUEST_ID = FIRST_ITEM;
	private static final int CA_CERTIFICATE_POSITION = FIRST_ITEM;
	private static final int SIGNED_CERTIFICATE_POSITION = 1;
	private static Log logger = LogFactory.getLog(CertificateEnrollmentServiceImpl.class);

	PrivateKey privateKey;
	X509Certificate rootCACertificate;
	JcaPKCS10CertificationRequest CSRRequest;
	PKCS10CertificationRequest certificationRequest;
	String wapProvisioningFilePath;
	DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();

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
	                                 Holder<RequestSecurityTokenResponse> response)
			throws KeyStoreGenerationException, XMLFileOperationException, CertificateGenerationException {


		ServletContext ctx =(ServletContext)context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
		File wapProvisioningFile=(File)ctx.getAttribute(Constants.CONTEXT_WAP_PROVISIONING_FILE);

		String storePassword=(String)ctx.getAttribute(Constants.CONTEXT_MDM_PASSWORD);
		String keyPassword=(String)ctx.getAttribute(Constants.CONTEXT_MDM_PRIVATE_KEY_PASSWORD);

		List certPropertyList = new ArrayList();
		String commonName=(String)ctx.getAttribute(Constants.CONTEXT_COMMON_NAME);
		certPropertyList.add(commonName);
		int notBeforeDate=(Integer)ctx.getAttribute(Constants.CONTEXT_NOT_BEFORE_DATE);
		certPropertyList.add(notBeforeDate);
		int notAfterDate=(Integer)ctx.getAttribute(Constants.CONTEXT_NOT_AFTER_DATE);
		certPropertyList.add(notAfterDate);


		signCertificate(storePassword, keyPassword);

		if (logger.isDebugEnabled()) {
			logger.debug("Received CSR from Device:" + binarySecurityToken);
		}

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

		X509Certificate signedCertificate = CertificateSigningService.signCSR(CSRRequest, privateKey, rootCACertificate, certPropertyList);

		BASE64Encoder base64Encoder = new BASE64Encoder();

		String rootCertEncodedString;
		try {
			rootCertEncodedString = base64Encoder.encode(rootCACertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CertificateGenerationException("CA certificate cannot be encoded.", e);
		}

		String signedCertEncodedString;
		try {
			signedCertEncodedString = base64Encoder.encode(signedCertificate.getEncoded());
		} catch (CertificateEncodingException e) {
			throw new CertificateGenerationException("Singed certificate cannot be encoded.", e);
		}

		DocumentBuilder builder;
		String wapProvisioningString;
		try {
			builder = domFactory.newDocumentBuilder();

			Document document = builder.parse(wapProvisioningFilePath);

			NodeList wapParm = document.getElementsByTagName(Constants.PARM);


			Node CACertificatePosition = wapParm.item(CA_CERTIFICATE_POSITION);

			//Adding SHA1 CA certificate finger print to wap-provisioning xml.
			CACertificatePosition.getParentNode().getAttributes().getNamedItem(Constants.TYPE).setTextContent(
					String.valueOf(DigestUtils.sha1Hex(rootCACertificate.getEncoded()))
					      .toUpperCase());

			//Adding encoded CA certificate to wap-provisioning file after removing new line characters.
			NamedNodeMap rootCertAttributes = CACertificatePosition.getAttributes();
			Node rootCertNode = rootCertAttributes.getNamedItem(Constants.VALUE);
			rootCertEncodedString = rootCertEncodedString.replaceAll("\n", "");
			rootCertNode.setTextContent(rootCertEncodedString);

			if (logger.isDebugEnabled()) {
				logger.debug("Root certificate:" + rootCertEncodedString);
			}

			Node signedCertificatePosition = wapParm.item(SIGNED_CERTIFICATE_POSITION);

			//Adding SHA1 signed certificate finger print to wap-provisioning xml.
			signedCertificatePosition.getParentNode().getAttributes().getNamedItem(Constants.TYPE)
			       .setTextContent(
					       String.valueOf(DigestUtils.shaHex(signedCertificate.getEncoded()))
					             .toUpperCase());

			//Adding encoded signed certificate to wap-provisioning file after removing new line characters.
			NamedNodeMap clientCertAttributes = signedCertificatePosition.getAttributes();
			Node clientEncodedNode = clientCertAttributes.getNamedItem(Constants.VALUE);
			signedCertEncodedString = signedCertEncodedString.replaceAll("\n", "");
			clientEncodedNode.setTextContent(signedCertEncodedString);

			if (logger.isDebugEnabled()) {
				logger.debug("Signed certificate:" + signedCertEncodedString);
			}

			wapProvisioningString = convertDocumentToString(document);

		//Generic exception is caught here as there is no need of taking different actions for different exceptions.
		} catch (Exception e) {
		    throw new XMLFileOperationException("Problem occurred with wap-provisioning.xml file.", e);
	    }

		String encodedWap = base64Encoder.encode(wapProvisioningString.getBytes());

		RequestedSecurityToken requestedSecurityToken = new RequestedSecurityToken();
		BinarySecurityToken binarySecToken = new BinarySecurityToken();
		binarySecToken.setValueType(Constants.VALUE_TYPE);
		binarySecToken.setEncodingType(Constants.ENCODING_TYPE);
		binarySecToken.setToken(encodedWap);
		requestedSecurityToken.setBinarySecurityToken(binarySecToken);
		requestSecurityTokenResponse.setRequestedSecurityToken(requestedSecurityToken);
		requestSecurityTokenResponse.setRequestID(REQUEST_ID);

		response.value = requestSecurityTokenResponse;
	}

	/**
	 * @param document - Wap provisioning XML document
	 * @return - String representation of wap provisioning XML document
	 * @throws Exception
	 */
	private String convertDocumentToString(Document document) throws TransformerException {
		DOMSource DOMSource = new DOMSource(document);
		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(DOMSource, streamResult);
		String wapProvisioningString = stringWriter.toString();

		return wapProvisioningString;
	}

	/**
	 * Method for setting privateKey and rootCACertificate variables.
	 * @throws KeyStoreGenerationException
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.XMLFileOperationException
	 * @throws CertificateGenerationException
	 */
	public void signCertificate(String storePassword,String keyPassword)
			throws KeyStoreGenerationException, XMLFileOperationException, CertificateGenerationException {

		File JKSFile = new File(getClass().getClassLoader().getResource(Constants.WSO2_MDM_JKS_FILE).getFile());
		String JKSFilePath = JKSFile.getPath();

		KeyStore securityJKS;
		try {
			securityJKS = KeyStoreGenerator.getKeyStore();
		} catch (KeyStoreGenerationException e) {
			throw new KeyStoreGenerationException("Cannot retrieve the MDM key store.", e);
		}

		try {
			KeyStoreGenerator.loadToStore(securityJKS, storePassword.toCharArray(), JKSFilePath);
		} catch (KeyStoreGenerationException e) {
			throw new KeyStoreGenerationException("Cannot load the MDM key store.", e);
		}

		PrivateKey CAPrivateKey;
		try {
			CAPrivateKey = (PrivateKey) securityJKS.getKey(Constants.CA_CERT, keyPassword.toCharArray());
		} catch (java.security.KeyStoreException e) {
			throw new CertificateGenerationException("Cannot generate private key due to Key store error.", e);
		} catch (NoSuchAlgorithmException e){
			throw new CertificateGenerationException("Requested cryptographic algorithm is not available in the environment.",e);
		} catch (UnrecoverableKeyException e) {
			throw new CertificateGenerationException("Cannot recover private key.", e);
		}

		privateKey = CAPrivateKey;

		Certificate CACertificate;
		try {
			CACertificate = securityJKS.getCertificate(Constants.CA_CERT);
		} catch (KeyStoreException e) {
			throw new KeyStoreGenerationException("Keystore cannot be accessed.", e);
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
}
