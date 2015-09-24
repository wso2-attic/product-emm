/*
 *
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.mdm.api.util;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.beans.MobileApp;
import org.wso2.carbon.mdm.beans.ios.WebClip;

import java.util.Properties;

/**
 * This class contains the all the operations related to IOS.
 */
public class MDMIOSOperationUtil {

	/**
	 * This method is used to create Install Application operation.
	 *
	 * @param application MobileApp application
	 * @return operation
	 * @throws MDMAPIException
	 *
	 */
	public static Operation createInstallAppOperation(MobileApp application) throws MDMAPIException {

		ProfileOperation operation = new ProfileOperation();

		switch (application.getType()) {
			case ENTERPRISE:
				org.wso2.carbon.mdm.beans.ios.EnterpriseApplication enterpriseApplication =
						new org.wso2.carbon.mdm.beans.ios.EnterpriseApplication();
				enterpriseApplication.setBundleId(application.getId());
				enterpriseApplication.setIdentifier(application.getIdentifier());
				enterpriseApplication.setManifestURL(application.getLocation());

				Properties properties = application.getProperties();
				enterpriseApplication.setPreventBackupOfAppData((Boolean) properties.
						get(MDMAppConstants.IOSConstants.IS_PREVENT_BACKUP));
				enterpriseApplication.setRemoveAppUponMDMProfileRemoval((Boolean) properties.
						get(MDMAppConstants.IOSConstants.IS_REMOVE_APP));
				operation.setCode(MDMAppConstants.IOSConstants.OPCODE_INSTALL_ENTERPRISE_APPLICATION);
				operation.setPayLoad(enterpriseApplication.toJSON());
				operation.setType(Operation.Type.COMMAND);
				break;
			case PUBLIC:
				org.wso2.carbon.mdm.beans.ios.AppStoreApplication appStoreApplication =
						new org.wso2.carbon.mdm.beans.ios.AppStoreApplication();
				appStoreApplication.setRemoveAppUponMDMProfileRemoval((Boolean) application.getProperties().
						get(MDMAppConstants.IOSConstants.IS_REMOVE_APP));
				appStoreApplication.setIdentifier(application.getIdentifier());
				appStoreApplication.setPreventBackupOfAppData((Boolean) application.getProperties().
						get(MDMAppConstants.IOSConstants.IS_PREVENT_BACKUP));
				appStoreApplication.setBundleId(application.getId());
				appStoreApplication.setiTunesStoreID((Integer) application.getProperties().
						get(MDMAppConstants.IOSConstants.I_TUNES_ID));
				operation.setCode(MDMAppConstants.IOSConstants.OPCODE_INSTALL_STORE_APPLICATION);
				operation.setType(Operation.Type.COMMAND);
				operation.setPayLoad(appStoreApplication.toJSON());
				break;
			case WEBAPP:
				WebClip webClip = new WebClip();
				webClip.setIcon(application.getIconImage());
				webClip.setIsRemovable(application.getProperties().
						getProperty(MDMAppConstants.IOSConstants.IS_REMOVE_APP));
				webClip.setLabel(application.getProperties().
						getProperty(MDMAppConstants.IOSConstants.LABEL));
				webClip.setURL(application.getLocation());

				operation.setCode(MDMAppConstants.IOSConstants.OPCODE_INSTALL_WEB_APPLICATION);
				operation.setType(Operation.Type.PROFILE);
				operation.setPayLoad(webClip.toJSON());
				break;
		}
		return operation;
	}

	public static Operation createAppUninstallOperation(MobileApp application) throws MDMAPIException{

		ProfileOperation operation = new ProfileOperation();
		operation.setCode(MDMAppConstants.IOSConstants.OPCODE_REMOVE_APPLICATION);
		operation.setType(Operation.Type.PROFILE);

		org.wso2.carbon.mdm.beans.ios.RemoveApplication removeApplication =
				new org.wso2.carbon.mdm.beans.ios.RemoveApplication();
		removeApplication.setBundleId(application.getIdentifier());
		operation.setPayLoad(removeApplication.toJSON());

		return operation;
	}
}
