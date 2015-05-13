package org.wso2.carbon.mdm.util;


import org.wso2.carbon.device.mgt.common.Application;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.beans.MobileApp;
import org.wso2.carbon.mdm.beans.android.InstallApplication;

public class MDMUtil {

    public static final String ANDROID_INSTALL_APPLICATION_OPCODE = "INSTALL_APPLICATION";

    public static Operation createAndroidProfileOperation(MobileApp application) throws MDMAPIException{

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
