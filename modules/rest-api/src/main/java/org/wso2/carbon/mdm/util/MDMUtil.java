package org.wso2.carbon.mdm.util;

import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.beans.MobileApp;
import org.wso2.carbon.mdm.beans.android.AppStoreApplication;
import org.wso2.carbon.mdm.beans.android.EnterpriseApplication;
import org.wso2.carbon.mdm.beans.android.WebApplication;

public class MDMUtil {

    public static final String ANDROID_INSTALL_APPLICATION_OPCODE = "INSTALL_APPLICATION";

	public static Operation createAndroidProfileOperation(MobileApp application) throws MDMAPIException{

		ProfileOperation operation = new ProfileOperation();
		operation.setCode(ANDROID_INSTALL_APPLICATION_OPCODE);
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
				operation.setPayLoad(webApplication.toJSON());
				break;
			default:
				String errorMessage = "Invalid application type.";
				throw new MDMAPIException(errorMessage);
		}
		return operation;
	}

    public static Operation createIOSProfileOperation(MobileApp application){

        ProfileOperation operation = new ProfileOperation();
        // TODO: set operation code
        //operation.setCode(OperationMapping.INSTALL_ENTERPRISE_APPLICATION.getCode());
        operation.setType(Operation.Type.COMMAND);
        // TODO: set pay load
        // operation.setPayLoad(enterpriseApplication.toJSON());
        return operation;
    }

}
