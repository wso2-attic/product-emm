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

package org.wso2.carbon.mdm.mobileservices.windows.services.wstep.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.ExtendedKeyUsage;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.KeyPurposeId;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequest;
import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.CertificateGenerationException;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException;

import javax.security.auth.x500.X500Principal;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

/**
 * Class for generating signed certificate for CSR form device.
 */
public class CertificateSigningService {

	private static final long MILLI_SECONDS = 1000L * 60 * 60 * 24;

	private enum PropertyIndex {
		COMMON_NAME_INDEX(0),
		NOT_BEFORE_DAYS_INDEX(1),
		NOT_AFTER_DAYS_INDEX(2);

		private final int itemPosition;
		private PropertyIndex(final int itemPosition) {
			this.itemPosition = itemPosition;
		}
		public int getValue() {
			return this.itemPosition;
		}
	}

	private static Log log = LogFactory.getLog(CertificateSigningService.class);

	/**
	 * Implement certificate signing task using CSR received from the device and the MDM server key
	 * store.
	 * @param jcaRequest        - CSR from the device
	 * @param privateKey        - Private key of CA certificate in MDM server
	 * @param caCert            - CA certificate in MDM server
	 * @param certParameterList - Parameter list for Signed certificate generation
	 * @return - Signed certificate for CSR from device
	 * @throws CertificateGenerationException
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.WAPProvisioningException
	 */
	public static X509Certificate signCSR(JcaPKCS10CertificationRequest jcaRequest,
	                                      PrivateKey privateKey, X509Certificate caCert,
	                                      List certParameterList) throws
	                                      CertificateGenerationException, WAPProvisioningException {

		String commonName =
				(String) certParameterList.get(PropertyIndex.COMMON_NAME_INDEX.getValue());
		int notBeforeDays =
				(Integer) certParameterList.get(PropertyIndex.NOT_BEFORE_DAYS_INDEX.getValue());
		int notAfterDays =
				(Integer) certParameterList.get(PropertyIndex.NOT_AFTER_DAYS_INDEX.getValue());
		X509v3CertificateBuilder certificateBuilder;
		X509Certificate signedCertificate;

		try {
			ContentSigner signer;
			BigInteger serialNumber = BigInteger.valueOf(new SecureRandom().
					                                     nextInt(Integer.MAX_VALUE));
			Date notBeforeDate = new Date(System.currentTimeMillis() -
			                             (MILLI_SECONDS * notBeforeDays));
			Date notAfterDate = new Date(System.currentTimeMillis() +
			                             (MILLI_SECONDS * notAfterDays));
			certificateBuilder =
				  new JcaX509v3CertificateBuilder(caCert, serialNumber, notBeforeDate, notAfterDate,
				                                  new X500Principal(commonName),
				                                  jcaRequest.getPublicKey());

			//Adding extensions to the signed certificate.
			certificateBuilder.addExtension(Extension.keyUsage, true,
			                                new KeyUsage(KeyUsage.digitalSignature));
			certificateBuilder.addExtension(Extension.extendedKeyUsage, false,
			                                new ExtendedKeyUsage(KeyPurposeId.id_kp_clientAuth));
			certificateBuilder.addExtension(Extension.basicConstraints, true,
			                                new BasicConstraints(false));

			signer = new JcaContentSignerBuilder(PluginConstants.CertificateEnrolment.ALGORITHM).
					 setProvider(PluginConstants.CertificateEnrolment.PROVIDER).build(privateKey);

			signedCertificate = new JcaX509CertificateConverter().setProvider(
					PluginConstants.CertificateEnrolment.PROVIDER).getCertificate(
					certificateBuilder.build(signer));
		} catch (InvalidKeyException e) {
			throw new CertificateGenerationException("CSR's public key is invalid", e);
		} catch (NoSuchAlgorithmException e) {
			throw new CertificateGenerationException("Certificate cannot be generated", e);
		}
		catch (CertIOException e) {
			throw new CertificateGenerationException(
					  "Cannot add extension(s) to signed certificate", e);
		}
		catch (OperatorCreationException e) {
			throw new CertificateGenerationException("Content signer cannot be created", e);
		}
		catch (CertificateException e) {
			throw new CertificateGenerationException("Signed certificate cannot be generated", e);
		}
		return signedCertificate;
	}
}
