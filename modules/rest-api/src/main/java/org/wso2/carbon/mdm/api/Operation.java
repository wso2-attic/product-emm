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

package org.wso2.carbon.mdm.api;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.Platform;
import org.wso2.carbon.device.mgt.common.app.mgt.AppManagerConnector;
import org.wso2.carbon.device.mgt.common.app.mgt.AppManagerConnectorException;
import org.wso2.carbon.device.mgt.common.app.mgt.Application;
import org.wso2.carbon.device.mgt.common.app.mgt.ApplicationWrapper;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.service.DeviceManagementService;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.context.DeviceOperationContext;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.Message;
import org.wso2.carbon.mdm.util.MDMUtil;
import org.wso2.carbon.utils.multitenancy.MultitenantConstants;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Operation related REST-API implementation.
 */
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class Operation {

	private static Log log = LogFactory.getLog(Operation.class);

	@GET
	public List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> getAllOperations()
			throws MDMAPIException {
		List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations;
		DeviceManagementService dmService;
		try {
			dmService = MDMAPIUtils.getDeviceManagementService();
			operations = dmService.getOperations(null);
		} catch (OperationManagementException e) {
			String msg = "Error occurred while fetching the operations for the device.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
		return operations;
	}

	@POST
	public Message addOperation(DeviceOperationContext operationContext) throws MDMAPIException {
		DeviceManagementService dmService;
		Message responseMsg = new Message();
		try {
			dmService = MDMAPIUtils.getDeviceManagementService();
			boolean status = dmService.addOperation(operationContext.getOperation(),
			                                               operationContext.getDevices());
			if (status) {
				Response.status(HttpStatus.SC_CREATED);
				responseMsg.setResponseMessage("Operation has added successfully.");
			} else {
				Response.status(HttpStatus.SC_OK);
				responseMsg.setResponseMessage("Failure in adding the Operation.");
			}
			return responseMsg;
		} catch (OperationManagementException e) {
			String msg = "Error occurred while saving the operation";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

    @POST
    @Path("installApp")
    public Message installApplication(ApplicationWrapper applicationWrapper) throws MDMAPIException {

        Message responseMsg = new Message();
        AppManagerConnector appManagerConnector;
        DeviceManagementService deviceManagementService;
        org.wso2.carbon.device.mgt.common.operation.mgt.Operation operation = null;
        ArrayList<DeviceIdentifier> deviceIdentifiers;
        try{
            appManagerConnector = MDMAPIUtils.getAppManagementService(MultitenantConstants.SUPER_TENANT_DOMAIN_NAME);
            deviceManagementService =  MDMAPIUtils.getDeviceManagementService(MultitenantConstants
                    .SUPER_TENANT_DOMAIN_NAME);
            Application application = applicationWrapper.getApplication();

            if (applicationWrapper.getDeviceIdentifiers() != null){
                for(DeviceIdentifier deviceIdentifier : applicationWrapper.getDeviceIdentifiers()){
                    deviceIdentifiers = new ArrayList<DeviceIdentifier>();
                    if (deviceIdentifier.getType().equals(Platform.ANDROID)){
                        operation = MDMUtil.createAndroidProfileOperation(application);
                    }else if(deviceIdentifier.getType().equals(Platform.IOS)){
                        operation = MDMUtil.createIOSProfileOperation(application);
                    }
                    deviceIdentifiers.add(deviceIdentifier);
                    try {
                        deviceManagementService.addOperation(operation, deviceIdentifiers);
                    }catch(OperationManagementException opMgtEx){
                         String errorMsg = "Error add operation for device identifier: "+operation.toString() + ": " +
                                 ""+deviceIdentifier.toString();
                        log.error(errorMsg,opMgtEx);
                        throw new MDMAPIException(errorMsg,opMgtEx);
                    }
                }
                appManagerConnector.installApplication(operation, applicationWrapper.getDeviceIdentifiers());
            }
            return responseMsg;
        } catch (AppManagerConnectorException e) {
            String msg = "Error occurred while saving the operation";
            log.error(msg, e);
            throw new MDMAPIException(msg, e);
        }
    }
    @GET
    @Path("appdummy")
    @Produces({ MediaType.APPLICATION_JSON})
    public ApplicationWrapper getApplicationDummy() throws MDMAPIException {

       ApplicationWrapper applicationWrapper = new ApplicationWrapper();

        Application app = new Application();
        app.setAppId("1");
        app.setApplicationName("test");
        app.setImageUrl("http://gogle.com");
        app.setLocationUrl("http://google.com");
        app.setVersion("1.0");


        ArrayList<String> userNameList = new ArrayList<String>();
        userNameList.add("admin");

        ArrayList<String> roleNameList = new ArrayList<String>();
        roleNameList.add("admin");

        ArrayList<DeviceIdentifier> deviceIdentifiers = new ArrayList<DeviceIdentifier>();
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier();
        deviceIdentifier.setId("322dsddsds");
        deviceIdentifier.setType("ANDROID");
        deviceIdentifiers.add(deviceIdentifier);

        applicationWrapper.setApplication(app);
        applicationWrapper.setRoleNameList(roleNameList);
        applicationWrapper.setUserNameList(userNameList);
        applicationWrapper.setDeviceIdentifiers(deviceIdentifiers);

        return applicationWrapper;
    }

}