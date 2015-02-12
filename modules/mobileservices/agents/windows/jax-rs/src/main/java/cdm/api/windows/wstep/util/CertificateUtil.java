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

package cdm.api.windows.wstep.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.math.BigInteger;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.cert.X509v3CertificateBuilder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.jcajce.JcaContentSignerBuilder;

/**
 * Generate X509 V3 certificates. CA, RA and SSL can be generated, where
 * intermediate certificates are signed from the root certificate to generate
 * the chain.
 */
public class CertificateUtil {
	private static final Log LOG = LogFactory.getLog(CertificateUtil.class);




	public static X509Certificate signCSR(PublicKey publicKeyToBeSigned, PrivateKey caPrivateKey, X509Certificate caCert) throws Exception{
		try {
			X509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(caCert,
			                                                                              BigInteger
					                                                                              .valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)),
			                                                                              new Date(System.currentTimeMillis()),
			                                                                              new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365*10)),
			                                                                              new X500Name("CN=abimaran"),
			                                                                              publicKeyToBeSigned);
			ContentSigner signer = new JcaContentSignerBuilder("SHA1withRSA").setProvider("SC").build(caPrivateKey);
			X509Certificate theCert = new JcaX509CertificateConverter().setProvider("SC").getCertificate(certificateBuilder.build(signer));
			return theCert;

		}  catch (OperatorCreationException e) {
			String message = "Error creating ContentSigner with JcaContentSignerBuilder"
			                 + " with the private key provided.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error building certificate.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}
	}

}
