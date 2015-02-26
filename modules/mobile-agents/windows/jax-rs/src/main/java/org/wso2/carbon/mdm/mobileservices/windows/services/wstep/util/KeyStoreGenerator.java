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

import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Class for MDM Keystore operations.
 */
public class KeyStoreGenerator {

	private static final Log logger = LogFactory.getLog(KeyStoreGenerator.class);

	/**
	 * This method loads the MDM keystore.
	 *
	 * @param keyStore   - MDM Keystore
	 * @param storePass  - Keystore Password
	 * @param resultFile - Keystore path
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions
	 * .KeyStoreGenerationException
	 */
	public static void loadToStore(KeyStore keyStore, char[] storePass, String resultFile)
			throws KeyStoreGenerationException {

		FileInputStream fileInputStream = null;

		try {
			if (resultFile != null) {
				fileInputStream = new FileInputStream(resultFile);
				keyStore.load(fileInputStream, storePass);
			}
		} catch (NoSuchAlgorithmException e) {
			String message =
					"Requested cryptographic algorithm is not available in the environment.";
			logger.error(message, e);
			throw new KeyStoreGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error working with certificate related to, " + resultFile;
			logger.error(message, e);
			throw new KeyStoreGenerationException(message, e);
		} catch (IOException e) {
			String message = "File error while working with file, " + resultFile;
			logger.error(message, e);
			throw new KeyStoreGenerationException(message, e);
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				String message = "File error while closing the file, " + resultFile;
				logger.error(message, e);
			}
		}
	}

	/**
	 * @return Keystore object
	 * @throws org.wso2.carbon.mdm.mobileservices.windows.common.exceptions
	 * .KeyStoreGenerationException
	 */
	public static KeyStore getKeyStore() throws KeyStoreGenerationException {
		try {
			return KeyStore.getInstance(Constants.CertificateEnrolment.JKS);
		} catch (java.security.KeyStoreException e) {
			String message = "KeyStore error while creating new JKS.";
			logger.error(message, e);
			throw new KeyStoreGenerationException(message, e);
		}

	}

}
