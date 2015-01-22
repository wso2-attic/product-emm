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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Creation of key stores and injecting certificates to the key stores is
 * handled here.
 */
public class KeyStoreGenerator {

	private static final Log LOG = LogFactory.getLog(KeyStoreGenerator.class);

	/**
	 * Load/initiate a key store from a provided file.
	 *
	 * @param keyStore   The destination key store which needs to be loaded.
	 * @param storePass  Password of the key store.
	 * @param resultFile The source key store file.
	 * @throws ApkGenerationException
	 */
	public static void loadToStore(KeyStore keyStore, char[] storePass, String resultFile)
			throws ApkGenerationException {
		FileInputStream fileInputStream = null;

		try {
			if (resultFile != null) {
				fileInputStream = FileOperator.getFileInputStream(resultFile);
				keyStore.load(fileInputStream, storePass);
			}
		} catch (NoSuchAlgorithmException e) {
			String message = Constants.ALGORITHM + " cryptographic algorithm is requested but" +
			                 " it is not available in the environment.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (CertificateException e) {
			String message = "Error working with certificate related to, " + resultFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} catch (IOException e) {
			String message = "File error while working with file, " + resultFile;
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				String message = "File error while closing the file, " + resultFile;
				LOG.error(message, e);
			}
		}
	}

	public static KeyStore getKeyStore() throws ApkGenerationException {
		try {
			return KeyStore.getInstance(Constants.JKS);
		} catch (KeyStoreException e) {
			String message = "KeyStore error while creating new JKS.";
			LOG.error(message, e);
			throw new ApkGenerationException(message, e);
		}

	}


}
