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

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * Class for generating signed certificate for CSR form device.
 */
public class CertificateSigningService {

	public static final long DAYS = 1000L * 60 * 60 * 24;
	private static Logger logger = Logger.getLogger(CertificateSigningService.class);

	/**
	 * @param jcaRequest - CSR from the device
	 * @param privateKey - Private key of CA certificate in MDM server
	 * @param CACert     - CA certificate in MDM server
	 * @return - Signed certificate for CSR from device
	 * @throws Exception
	 */
	public static X509Certificate signCSR(JcaPKCS10CertificationRequest jcaRequest,
	                                      PrivateKey privateKey, X509Certificate CACert) throws CertificateGenerationException {

		X509v3CertificateBuilder certificateBuilder;



		try {
			certificateBuilder = new JcaX509v3CertificateBuilder(CACert,
			                                                     BigInteger.valueOf(new SecureRandom().nextInt(Integer.MAX_VALUE)),
			                                                     new Date(System.currentTimeMillis() - (DAYS * Constants.NOT_BEFORE_DATE)),
			                                                     new Date(System.currentTimeMillis() + (DAYS * Constants.NOT_AFTER_DATE)),
			                                                     new X500Principal(Constants.CN),
			                                                     jcaRequest.getPublicKey());
		} catch (InvalidKeyException e) {
			throw new CertificateGenerationException("CSR's public key is invalid",e);
		} catch (NoSuchAlgorithmException e) {
			throw new CertificateGenerationException("Certificate cannot be generated",e);
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
			throw new CertificateGenerationException("Cannot add extension(s) to signed certificate",e);
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
}
