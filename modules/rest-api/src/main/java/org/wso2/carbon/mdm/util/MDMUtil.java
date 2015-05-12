package org.wso2.carbon.mdm.util;


import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.beans.android.InstallApplication;

public class MDMUtil {

    public static final String ANDROID_INSTALL_APPLICATION_OPCODE = "INSTALL_APPLICATION";

    public static Operation createAndroidProfileOperation(Application application) throws MDMAPIException{

        InstallApplication installApplication = new InstallApplication();
        installApplication.setType(application.getAppType());
        installApplication.setAppIdentifier(application.getAppId());
        installApplication.setUrl(application.getLocationUrl());

        ProfileOperation operation = new ProfileOperation();
        operation.setCode(ANDROID_INSTALL_APPLICATION_OPCODE);
        operation.setType(Operation.Type.PROFILE);
        operation.setPayLoad(installApplication.toJSON());

        return operation;
    }

    public static Operation createIOSProfileOperation(Application application){

        ProfileOperation operation = new ProfileOperation();

        // TODO: set operation code
        //operation.setCode(OperationMapping.INSTALL_ENTERPRISE_APPLICATION.getCode());
        operation.setType(Operation.Type.COMMAND);
        // TODO: set pay load
        // operation.setPayLoad(enterpriseApplication.toJSON());
        return operation;
    }

}
