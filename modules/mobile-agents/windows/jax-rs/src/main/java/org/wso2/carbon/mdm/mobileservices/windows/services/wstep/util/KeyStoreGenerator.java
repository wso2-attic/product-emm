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

import org.wso2.carbon.mdm.mobileservices.windows.common.PluginConstants;
import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.KeyStoreGenerationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Class for MDM Keystore operations.
 */
public class KeyStoreGenerator {

	private static final Log log = LogFactory.getLog(KeyStoreGenerator.class);

	/**
	 * This method loads the MDM keystore.
	 * @param keyStore   - MDM Keystore
	 * @param keyStorePassword  - Keystore Password
	 * @param keyStorePath - Keystore path
	 * @throws KeyStoreGenerationException
	 */
	public static void loadToStore(KeyStore keyStore,
	                               char[] keyStorePassword,
	                               String keyStorePath) throws KeyStoreGenerationException {

		FileInputStream fileInputStream = null;

		try {
			if (keyStorePath != null) {
				fileInputStream = new FileInputStream(keyStorePath);
				keyStore.load(fileInputStream, keyStorePassword);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreGenerationException(
					"Requested cryptographic algorithm is not available in the environment.", e);
		} catch (CertificateException e) {
			throw new KeyStoreGenerationException("Error working with certificate related to, " +
			                                      keyStorePath, e);
		} catch (IOException e) {
			throw new KeyStoreGenerationException("File error while working with file, " +
			                                      keyStorePath, e);
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				throw new KeyStoreGenerationException("File error while closing the file, " +
				                                      keyStorePath,e);
			}
		}
	}

	/**
	 * This method is for retrieving instance of Key Store.
	 * @return Keystore object
	 * @throws KeyStoreGenerationException
	 */
	public static KeyStore getKeyStore() throws KeyStoreGenerationException {
		try {
			return KeyStore.getInstance(PluginConstants.CertificateEnrolment.JKS);
		} catch (KeyStoreException e) {
			String msg = "KeyStore error while creating new JKS.";
			log.error(msg, e);
			throw new KeyStoreGenerationException(msg, e);
		}
	}
}
