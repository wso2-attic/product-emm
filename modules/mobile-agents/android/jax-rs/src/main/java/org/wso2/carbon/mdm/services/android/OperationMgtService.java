/*
 * Copyright (c) 2015, WSO2 Inc. (http:www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.mdm.services.android;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.device.mgt.common.*;
import org.wso2.carbon.device.mgt.common.operation.mgt.Operation;
import org.wso2.carbon.device.mgt.common.operation.mgt.OperationManagementException;
import org.wso2.carbon.device.mgt.core.operation.mgt.ProfileOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.mdm.services.android.bean.*;
import org.wso2.carbon.mdm.services.android.bean.wrapper.*;
import org.wso2.carbon.mdm.services.android.exception.AndroidOperationException;
import org.wso2.carbon.mdm.services.android.exception.OperationConfigurationException;
import org.wso2.carbon.mdm.services.android.util.AndroidAPIUtils;
import org.wso2.carbon.mdm.services.android.util.AndroidConstants;
import org.wso2.carbon.mdm.services.android.util.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

/**
 * Android Device Operation REST-API implementation.
 */
public class OperationMgtService {

	private static Log log = LogFactory.getLog(OperationMgtService.class);

	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> getPendingOperations
			(@HeaderParam("Accept") String acceptHeader, @PathParam("id") String id,
			 List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> resultOperations) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android pending operations:" + id);
		}
		Message message = new Message();
		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);

		try {
			if (resultOperations != null) {
				updateOperations(resultOperations);
			}
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			log.error(message.getResponseMessage(), e);
		}

		DeviceIdentifier deviceIdentifier = AndroidAPIUtils.convertToDeviceIdentifierObject(id);
		List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations;

		try {
			operations = AndroidAPIUtils.getPendingOperations(deviceIdentifier);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}

		return operations;
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("lock")
	public Response configureDeviceLock(@HeaderParam("Accept") String acceptHeader, List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android device lock operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCK);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(true);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("location")
	public Response getDeviceLocation(@HeaderParam("Accept") String acceptHeader,
									  List<String> deviceIDs) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking Android device location operation");
		}
		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCATION);
			operation.setType(Operation.Type.COMMAND);
			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation,
					message, responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("clear-password")
	public Response removePassword(@HeaderParam("Accept") String acceptHeader,
								   List<String> deviceIDs) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking Android clear password operation");
		}
		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.CLEAR_PASSWORD);
			operation.setType(Operation.Type.COMMAND);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation,
					message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("camera")
	public Response configureCamera(@HeaderParam("Accept") String acceptHeader,
									CameraBeanWrapper cameraBeanWrapper) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android Camera operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			Camera camera = cameraBeanWrapper.getOperation();

			if (camera == null) {
				throw new OperationManagementException("Camera bean is empty");
			}

			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.CAMERA);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(camera.isEnabled());

			return AndroidAPIUtils.getOperationResponse(cameraBeanWrapper.getDeviceIDs(), operation, message,
					responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("device-info")
	public Response getDeviceInformation(@HeaderParam("Accept") String acceptHeader,
										 List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking get Android device information operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.DEVICE_INFO);
			operation.setType(Operation.Type.COMMAND);
			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
					responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("enterprise-wipe")
	public Response wipeDevice(@HeaderParam("Accept") String acceptHeader,
							   List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking enterprise-wipe device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.ENTERPRISE_WIPE);
			operation.setType(Operation.Type.COMMAND);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
					responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wipe-data")
	public Response wipeData(@HeaderParam("Accept") String acceptHeader,
							 WipeDataBeanWrapper wipeDataBeanWrapper) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android wipe-data device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			WipeData wipeData = wipeDataBeanWrapper.getOperation();

			if (wipeData == null) {
				throw new OperationManagementException("WipeData bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.WIPE_DATA);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(wipeData.toJSON());

			return AndroidAPIUtils.getOperationResponse(wipeDataBeanWrapper.getDeviceIDs(), operation, message,
					responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("get-application-list")
	public Response getApplications(@HeaderParam("Accept") String acceptHeader,
									List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android getApplicationList device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.APPLICATION_LIST);
			operation.setType(Operation.Type.COMMAND);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
					responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("ring-device")
	public Response ringDevice(@HeaderParam("Accept") String acceptHeader,
							   List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android ring-device device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.DEVICE_RING);
			operation.setType(Operation.Type.COMMAND);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
					responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("mute")
	public Response muteDevice(@HeaderParam("Accept") String acceptHeader,
							   List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking mute device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.DEVICE_MUTE);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(true);
			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
					responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("install-application")
	public Response installApplication(@HeaderParam("Accept") String acceptHeader,
									   InstallApplicationBeanWrapper installApplicationBeanWrapper) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking InstallApplication operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			InstallApplication installApplication = installApplicationBeanWrapper.getOperation();

			if (installApplication == null) {
				throw new OperationManagementException("Install application bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.INSTALL_APPLICATION);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(installApplication.toJSON());

			return AndroidAPIUtils.getOperationResponse(installApplicationBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("uninstall-application")
	public Response uninstallApplication(@HeaderParam("Accept") String acceptHeader,
										 UninstallApplicationBeanWrapper uninstallApplicationBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking UninstallApplication operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			UninstallApplication uninstallApplication = uninstallApplicationBeanWrapper.getOperation();

			if (uninstallApplication == null) {
				throw new OperationManagementException("Uninstall application bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.UNINSTALL_APPLICATION);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(uninstallApplication.toJSON());

			return AndroidAPIUtils.getOperationResponse(uninstallApplicationBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);
		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("blacklist-applications")
	public Response blacklistApplications(@HeaderParam("Accept") String acceptHeader,
										  BlacklistApplicationsBeanWrapper blacklistApplicationsBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking Blacklist-Applications operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			BlacklistApplications blacklistApplications = blacklistApplicationsBeanWrapper.getOperation();

			if (blacklistApplications == null) {
				throw new OperationManagementException("Blacklist applications bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.BLACKLIST_APPLICATIONS);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(blacklistApplications.toJSON());

			return AndroidAPIUtils.getOperationResponse(blacklistApplicationsBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("notification")
	public Response sendNotification(@HeaderParam("Accept") String acceptHeader,
									 NotificationBeanWrapper notificationBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking notification operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			Notification notification = notificationBeanWrapper.getOperation();

			if (notification == null) {
				throw new OperationManagementException("Notification bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.NOTIFICATION);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(notification.toJSON());

			return AndroidAPIUtils.getOperationResponse(notificationBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wifi")
	public Response configureWifi(@HeaderParam("Accept") String acceptHeader,
								  WifiBeanWrapper wifiBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking configure wifi operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			Wifi wifi = wifiBeanWrapper.getOperation();

			if (wifi == null) {
				throw new OperationManagementException("Wifi bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.WIFI);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(wifi.toJSON());

			return AndroidAPIUtils.getOperationResponse(wifiBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("encrypt")
	public Response encryptStorage(@HeaderParam("Accept") String acceptHeader,
								  EncryptBeanWrapper encryptBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking encrypt operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			Encrypt encrypt = encryptBeanWrapper.getOperation();

			if (encrypt == null) {
				throw new OperationManagementException("Encrypt bean is empty");
			}

			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.ENCRYPT_STORAGE);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(encrypt.isEncrypted());

			return AndroidAPIUtils.getOperationResponse(encryptBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("change-lock-code")
	public Response changeLockCode(@HeaderParam("Accept") String acceptHeader,
								   LockCodeBeanWrapper lockCodeBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking encrypt operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			LockCode lockCode = lockCodeBeanWrapper.getOperation();

			if (lockCode == null) {
				throw new OperationManagementException("Encrypt bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.CHANGE_LOCK_CODE);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(lockCode.toJSON());

			return AndroidAPIUtils.getOperationResponse(lockCodeBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("password-policy")
	public Response setPasswordPolicy(@HeaderParam("Accept") String acceptHeader,
								   PasswordPolicyBeanWrapper passwordPolicyBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking password policy operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			PasswordPolicy passwordPolicy = passwordPolicyBeanWrapper.getOperation();

			if (passwordPolicy == null) {
				throw new OperationManagementException("Password policy bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.PASSWORD_POLICY);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(passwordPolicy.toJSON());

			return AndroidAPIUtils.getOperationResponse(passwordPolicyBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("webclip")
	public Response setWebClip(@HeaderParam("Accept") String acceptHeader,
									  WebClipBeanWrapper webClipBeanWrapper) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking webclip policy operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			WebClip webClip = webClipBeanWrapper.getOperation();

			if (webClip == null) {
				throw new OperationManagementException("Web Clip bean is empty");
			}

			ProfileOperation operation = new ProfileOperation();
			operation.setCode(AndroidConstants.OperationCodes.WEBCLIP);
			operation.setType(Operation.Type.PROFILE);
			operation.setPayLoad(webClip.toJSON());

			return AndroidAPIUtils.getOperationResponse(webClipBeanWrapper.getDeviceIDs(),
					operation, message, responseMediaType);

		} catch (OperationManagementException e) {
			String errorMessage = "Issue in retrieving operation management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			String errorMessage = "Issue in retrieving device management service instance";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw new AndroidOperationException(message, responseMediaType);
		} catch (OperationConfigurationException e) {
			String errorMessage = "Issue in setting up payload in operation";
			message.setResponseMessage(errorMessage);
			message.setResponseCode(Response.Status.INTERNAL_SERVER_ERROR.toString());
			log.error(errorMessage, e);
			throw  new AndroidOperationException(message, responseMediaType);
		}
	}

	private void updateOperations(List<? extends org.wso2.carbon.device.mgt.common.operation.mgt.Operation> operations
	) throws OperationManagementException {

		for (org.wso2.carbon.device.mgt.common.operation.mgt.Operation operationResponse : operations) {
			AndroidAPIUtils.updateOperation(operationResponse.getId(), operationResponse.getStatus());
			if (log.isDebugEnabled()) {
				log.debug("Updating operation '" + operationResponse.getCode() + "'");
			}
		}
	}
}