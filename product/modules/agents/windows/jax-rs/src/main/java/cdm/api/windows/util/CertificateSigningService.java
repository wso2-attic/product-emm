/*
 * Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package cdm.api.windows.util;

import org.apache.log4j.Logger;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateSigningService {

	private static Logger LOGGER = Logger.getLogger(CertificateSigningService.class);

	public static X509Certificate signCSR(JcaPKCS10CertificationRequest jcaRequest,
	                                      PrivateKey privateKey, X509Certificate caCert)
			throws Exception {
		try {

			X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(caCert,
			                                                                              BigInteger
					                                                                              .valueOf(
							                                                                              new SecureRandom()
									                                                                              .nextInt(
											                                                                              Integer.MAX_VALUE)),
			                                                                              new Date(
					                                                                              System.currentTimeMillis() -
					                                                                              1000L *
					                                                                              60 *
					                                                                              60 *
					                                                                              24 *
					                                                                              30),
			                                                                              new Date(
					                                                                              System.currentTimeMillis() +
					                                                                              (1000L *
					                                                                               60 *
					                                                                               60 *
					                                                                               24 *
					                                                                               365 *
					                                                                               10)),
			                                                                              new X500Name(
					                                                                              "CN=abimaran"),
			                                                                              jcaRequest
					                                                                              .getPublicKey());

			JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

			ContentSigner signer =
					new JcaContentSignerBuilder("SHA1withRSA").setProvider("BC").build(privateKey);

			X509Certificate theCert =
					new JcaX509CertificateConverter().setProvider("BC").getCertificate(
							certificateBuilder.build(signer));

			LOGGER.info("Signed Certificate CN : " + theCert.getSubjectDN().getName());

			LOGGER.info("Signed CSR's public key : " + theCert.getPublicKey());

			return theCert;

		} catch (Exception e) {
			throw new Exception("Error in signing the certificate", e);
		}
	}

}
