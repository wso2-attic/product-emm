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


import java.io.File;

/**
 * Constants used throughout the project
 */
public class Constants {
	public static class FilePath {
		private FilePath() {
			throw new AssertionError();
		}

		public static final String BKS_FILE = "emm_truststore.bks";
		public static final String ANDROID_AGENT = "emm-agent-android";
		public static final String WSO2CARBON_JKS = "wso2carbon.jks";
		public static final String CLIENT_TRUST_JKS = "client-truststore.jks";

		public static final String COMMON_UTIL = ANDROID_AGENT + File.separator + "src" +
		                                         File.separator + "org" + File.separator + "wso2" +
		                                         File.separator + "emm" + File.separator + "agent" +
		                                         File.separator + "utils" + File.separator +
		                                         "CommonUtilities.java";
		public static final String WSO2EMM_JKS = "wso2emm.jks";
		public static final String ANDROID_AGENT_RAW = ANDROID_AGENT + File.separator + "res" +
		                                               File.separator + "raw" + File.separator;
		public static final String ANDROID_AGENT_APK = ANDROID_AGENT + File.separator + "target" +
		                                               File.separator + "emm_agent.apk";
		public static final String APK_FOLDER = "Apk";
		public static final String JKS_FOLDER = "jks";
		public static final String BIN_PATH = File.separator + "bin" + File.separator + "mvn";
		public static final String WORKING_DIR = "workingDir";
		public static final String ZIP_PATH = "zipPath";
	}

	public static final String ALGORITHM = "RSA";
	public static final String PROVIDER = "SC";
	public static final String ENCRYPTION = "SHA1withRSA";
	public static final String REGISTRATION_AUTHORITY = "RA";
	public static final String BKS = "BKS";
	public static final String BKS_ALIAS = "cert-alias";
	public static final String JKS = "JKS";
	public static final String SSL = "SSL";
	public static final String ENVIRONMENT_VARIABLE = "MAVEN_HOME";
	public static final String ARCHIVE_TYPE = ".zip";
	public static final String ACTION = "clean";
	public static final String GOAL = "package";
	public static final String SERVER_IP_ANDROID = "String SERVER_IP = \"";
	public static final String TRUST_STORE_BKS = "String TRUSTSTORE_PASSWORD = \"";

	public static class CSRDataKeys {
		private CSRDataKeys() {
			throw new AssertionError();
		}

		public static final String COUNTRY_CA = "countryCA";
		public static final String STATE_CA = "stateCA";
		public static final String LOCALITY_CA = "localityCA";
		public static final String ORGANIZATION_CA = "organizationCA";
		public static final String ORGANIZATION_UNIT_CA = "organizationUCA";
		public static final String DAYS_CA = "daysCA";
		public static final String COMMON_NAME_CA = "commonNameCA";
		public static final String COUNTRY_RA = "countryRA";
		public static final String STATE_RA = "stateRA";
		public static final String LOCALITY_RA = "localityRA";
		public static final String ORGANIZATION_RA = "organizationRA";
		public static final String ORGANIZATION_UNIT_RA = "organizationURA";
		public static final String DAYS_RA = "daysRA";
		public static final String COMMON_NAME_RA = "commonNameRA";
		public static final String COUNTRY_SSL = "countrySSL";
		public static final String STATE_SSL = "stateSSL";
		public static final String LOCALITY_SSL = "localitySSL";
		public static final String ORGANIZATION_SSL = "organizationSSL";
		public static final String ORGANIZATION_UNIT_SSL = "organizationUSSL";
		public static final String DAYS_SSL = "daysSSL";
		public static final String SERVER_IP = "serverIp";
		public static final String PASSWORD = "password";
		public static final String USERSNAME = "usersname";
		public static final String COMPANY = "company";

	}

	public static class TruststoreKeys {
		private TruststoreKeys() {
			throw new AssertionError();
		}

		public static final String PASSWORD_PK12_CA = "passwordPK12CA";
		public static final String PASSWORD_PK12_RA = "passwordPK12RA";
		public static final String ALIAS_PK12_CA = "aliasPK12CA";
		public static final String ALIAS_PK12_RA = "aliasPK12RA";
		public static final String PASSWORD_WSO2_EMM_JKS = "passwordWSO2EMMJKS";
		public static final String ALIAS__CLIENT_TRUSTSTORE = "aliasClientTruststore";
		public static final String PASSWORD_CLIENT_TRUSTSTORE = "passwordClientTruststore";
		public static final String ALIAS_WSO2_CARBON = "aliasWSO2Carbon";
		public static final String PASSWORD_WSO2_CARBON = "passwordWSO2Carbon";
	}
}
