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
package org.wso2.carbon.mdm.util;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.beans.MobileApp;
import org.wso2.carbon.mdm.beans.android.InstallApplication;
import org.wso2.carbon.mdm.beans.ios.AppStoreApplication;
import org.wso2.carbon.mdm.beans.ios.EnterpriseApplication;
import org.wso2.carbon.mdm.beans.ios.IOSAPPConstants;
import org.wso2.carbon.mdm.beans.ios.WebClip;

import java.util.Properties;

public class MDMUtil {

    public static final String ANDROID_INSTALL_APPLICATION_OPCODE = "INSTALL_APPLICATION";

    public static Operation createAndroidProfileOperation(MobileApp application) throws MDMAPIException {

        InstallApplication installApplication = new InstallApplication();
        installApplication.setType(application.getType().toString());
        installApplication.setAppIdentifier(application.getAppIdentifier());
        installApplication.setUrl(application.getLocation());
        ProfileOperation operation = new ProfileOperation();
        operation.setCode(ANDROID_INSTALL_APPLICATION_OPCODE);
        operation.setType(Operation.Type.PROFILE);
        operation.setPayLoad(installApplication.toJSON());

        return operation;
    }

    public static Operation createIOSProfileOperation(MobileApp application) throws MDMAPIException{

        ProfileOperation operation = new ProfileOperation();

        switch (application.getType()) {
            case ENTERPRISE:
                EnterpriseApplication enterpriseApplication = new EnterpriseApplication();
                enterpriseApplication.setBundleId(application.getId());
                enterpriseApplication.setIdentifier(application.getIdentifier());
                enterpriseApplication.setManifestURL(application.getLocation());

                Properties properties = application.getProperties();
                enterpriseApplication.setPreventBackupOfAppData((Boolean) properties.get(IOSAPPConstants
                        .IS_PREVENT_BACKUP));
                enterpriseApplication.setRemoveAppUponMDMProfileRemoval((Boolean) properties.get(IOSAPPConstants
                        .IS_REMOVE_APP));
                operation.setCode(IOSAPPConstants.OPCODE_INSTALL_ENTERPRISE_APPLICATION);
                operation.setPayLoad(enterpriseApplication.toJSON());
                operation.setType(Operation.Type.COMMAND);
                break;
            case PUBLIC:
                AppStoreApplication appStoreApplication = new AppStoreApplication();
                appStoreApplication.setRemoveAppUponMDMProfileRemoval((Boolean) application.getProperties()
                        .get(IOSAPPConstants.IS_REMOVE_APP));
                appStoreApplication.setIdentifier(application.getIdentifier());
                appStoreApplication.setPreventBackupOfAppData((Boolean) application.getProperties().get(IOSAPPConstants
                        .IS_PREVENT_BACKUP));
                appStoreApplication.setBundleId(application.getId());
                appStoreApplication.setiTunesStoreID((Integer) application.getProperties().get(IOSAPPConstants
                        .I_TUNES_ID));
                operation.setCode(IOSAPPConstants.OPCODE_INSTALL_STORE_APPLICATION);
                operation.setType(Operation.Type.COMMAND);
                operation.setPayLoad(appStoreApplication.toJSON());
                break;
            case WEBAPP:
                WebClip webClip = new WebClip();
                webClip.setIcon(application.getIconImage());
                webClip.setIsRemovable(application.getProperties().getProperty(IOSAPPConstants.IS_REMOVE_APP));
                webClip.setLabel(application.getProperties().getProperty(IOSAPPConstants.LABEL));
                webClip.setURL(application.getLocation());

                operation.setCode(IOSAPPConstants.OPCODE_INSTALL_WEB_APPLICATION);
                operation.setType(Operation.Type.PROFILE);
                operation.setPayLoad(webClip.toJSON());
                break;
        }
        return operation;
    }

}
