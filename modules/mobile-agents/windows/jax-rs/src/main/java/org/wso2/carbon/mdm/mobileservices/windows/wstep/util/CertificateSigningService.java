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

package org.wso2.carbon.mdm.mobileservices.windows.wstep.util;

import org.w3c.dom.Document;
import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.PropertyFileException;
import org.xml.sax.SAXException;

import javax.security.auth.x500.X500Principal;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Class for generating signed certificate for CSR form device.
 */
public class CertificateSigningService {

	private static final long DAYS = 1000L * 60 * 60 * 24;
	private static final int FIRST_ITEM = 0;
	private static final String SIGNED_CERT_CN = "signedcertCN";
	private static final String SIGNED_CERT_NOT_BEFORE = "signedcertnotbefore";
	private static final String SIGNED_CERT_NOT_AFTER = "signedcertnotafter";
	private static Logger logger = Logger.getLogger(CertificateSigningService.class);
	private static String signedCertCommonName;
	private static int signedCertNotBeforeDate;
	private static int signedCertNotAfterDate;

	/**
	 * @param jcaRequest - CSR from the device
	 * @param privateKey - Private key of CA certificate in MDM server
	 * @param CACert     - CA certificate in MDM server
	 * @return - Signed certificate for CSR from device
	 * @throws Exception
	 */
	public static X509Certificate signCSR(JcaPKCS10CertificationRequest jcaRequest,
	                                      PrivateKey privateKey, X509Certificate CACert)
			throws CertificateGenerationException, PropertyFileException {

		X509v3CertificateBuilder certificateBuilder;

		getCertificateProperties();

		try {

			certificateBuilder = new JcaX509v3CertificateBuilder(CACert,
			                                                     BigInteger.valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)),
			                                                     new Date(System.currentTimeMillis() - (DAYS * signedCertNotBeforeDate)),
			                                                     new Date(System.currentTimeMillis() + (DAYS * signedCertNotAfterDate)),
			                                                     new X500Principal(signedCertCommonName),
			                                                     jcaRequest.getPublicKey());


		} catch (InvalidKeyException e) {
			throw new CertificateGenerationException("CSR's public key is invalid", e);
		} catch (NoSuchAlgorithmException e) {
			throw new CertificateGenerationException("Certificate cannot be generated", e);
		}

		try {
			certificateBuilder.addExtension(X509Extensions.KeyUsage, true,
			                                new KeyUsage(KeyUsage.digitalSignature));
			certificateBuilder.addExtension(X509Extensions.ExtendedKeyUsage, false,
			                                new ExtendedKeyUsage(
					                                KeyPurposeId.id_kp_clientAuth));
			certificateBuilder.addExtension(X509Extensions.BasicConstraints, true,
			                                new BasicConstraints(false));
		} catch (CertIOException e) {
			throw new CertificateGenerationException("Cannot add extension(s) to signed certificate", e);
		}

		ContentSigner signer;

		try {
			signer = new JcaContentSignerBuilder(Constants.ALGORITHM).setProvider(Constants.PROVIDER).build(privateKey);
		} catch (OperatorCreationException e) {
			throw new CertificateGenerationException("Content signer cannot be created",e);
		}

		X509Certificate signedCertificate;
		try {
			signedCertificate = new JcaX509CertificateConverter().setProvider(Constants.PROVIDER).getCertificate(certificateBuilder.build(signer));
		} catch (CertificateException e) {
         throw new CertificateGenerationException("Signed certificate cannot generated",e);
 		}

		return signedCertificate;

	}

	/**
	 * Reading the property file for retrieving certificate parameters(common-name, not-before-date, not-after-date).
	 * @throws PropertyFileException
	 */
	private static void getCertificateProperties() throws PropertyFileException {

		File propertyFile = new File(CertificateSigningService.class.getClassLoader().getResource(Constants.PROPERTIES_XML).getFile());

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

		signedCertCommonName = document.getElementsByTagName(SIGNED_CERT_CN).item(FIRST_ITEM).getTextContent();
		signedCertNotBeforeDate = Integer.valueOf(document.getElementsByTagName(
				SIGNED_CERT_NOT_BEFORE).item(FIRST_ITEM).getTextContent());
		signedCertNotAfterDate = Integer.valueOf(document.getElementsByTagName(
				SIGNED_CERT_NOT_AFTER).item(FIRST_ITEM).getTextContent());
	}
}
