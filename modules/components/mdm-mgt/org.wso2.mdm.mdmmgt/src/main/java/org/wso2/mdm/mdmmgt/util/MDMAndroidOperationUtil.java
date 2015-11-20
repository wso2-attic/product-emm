/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.mdm.mdmmgt.util;


import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.mdm.mdmmgt.beans.MobileApp;
import org.wso2.mdm.mdmmgt.beans.android.AppStoreApplication;
import org.wso2.mdm.mdmmgt.beans.android.EnterpriseApplication;
import org.wso2.mdm.mdmmgt.beans.android.WebApplication;
import org.wso2.mdm.mdmmgt.common.MDMException;

/**
 *
 * This class contains the all the operations related to Android.
 */
public class MDMAndroidOperationUtil {

	/**
	 * This method is used to create Install Application operation.
	 *
	 * @param application MobileApp application
	 * @return operation
	 * @throws MDMException
	 *
	 */
	public static Operation createInstallAppOperation(MobileApp application) throws MDMException {

		ProfileOperation operation = new ProfileOperation();
		operation.setCode(MDMAppConstants.AndroidConstants.OPCODE_INSTALL_APPLICATION);
		operation.setType(Operation.Type.PROFILE);

		switch (application.getType()) {
			case ENTERPRISE:
				EnterpriseApplication enterpriseApplication = new EnterpriseApplication();
				enterpriseApplication.setType(application.getType().toString());
				enterpriseApplication.setUrl(application.getLocation());
				operation.setPayLoad(enterpriseApplication.toJSON());
				break;
			case PUBLIC:
                AppStoreApplication appStoreApplication = new AppStoreApplication();
				appStoreApplication.setType(application.getType().toString());
				appStoreApplication.setAppIdentifier(application.getIdentifier());
				operation.setPayLoad(appStoreApplication.toJSON());
				break;
			case WEBAPP:
				WebApplication webApplication = new WebApplication();
				webApplication.setUrl(application.getLocation());
				webApplication.setName(application.getName());
				webApplication.setType(application.getType().toString());
				operation.setPayLoad(webApplication.toJSON());
				break;
			default:
				String errorMessage = "Invalid application type.";
				throw new MDMException(errorMessage);
		}
		return operation;
	}

	/**
	 * This method is used to create Uninstall Application operation.
	 * @param application MobileApp application
	 * @return operation
	 * @throws MDMException
	 */
	public static Operation createAppUninstallOperation(MobileApp application) throws MDMException {

		ProfileOperation operation = new ProfileOperation();
		operation.setCode(MDMAppConstants.AndroidConstants.OPCODE_UNINSTALL_APPLICATION);
		operation.setType(Operation.Type.PROFILE);

		switch (application.getType()) {
			case ENTERPRISE:
				EnterpriseApplication enterpriseApplication = new EnterpriseApplication();
				enterpriseApplication.setType(application.getType().toString());
				enterpriseApplication.setAppIdentifier(application.getIdentifier());
				operation.setPayLoad(enterpriseApplication.toJSON());
				break;
			case PUBLIC:
				AppStoreApplication appStoreApplication = new AppStoreApplication();
				appStoreApplication.setType(application.getType().toString());
				appStoreApplication.setAppIdentifier(application.getIdentifier());
				operation.setPayLoad(appStoreApplication.toJSON());
				break;
			case WEBAPP:
				WebApplication webApplication = new WebApplication();
				webApplication.setUrl(application.getLocation());
				webApplication.setName(application.getName());
				webApplication.setType(application.getType().toString());
				operation.setPayLoad(webApplication.toJSON());
				break;
			default:
				String errorMessage = "Invalid application type.";
				throw new MDMException(errorMessage);
		}
		return operation;
	}

}
