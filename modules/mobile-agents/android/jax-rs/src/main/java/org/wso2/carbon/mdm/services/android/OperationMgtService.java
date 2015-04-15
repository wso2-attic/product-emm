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
import org.wso2.carbon.device.mgt.core.operation.mgt.CommandOperation;
import org.wso2.carbon.device.mgt.core.operation.mgt.ConfigOperation;
import org.wso2.carbon.mdm.services.android.bean.*;
import org.wso2.carbon.mdm.services.android.exception.AndroidOperationException;
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

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("lock")
	public Response configureDeviceLock(@HeaderParam("Accept") String acceptHeader, DeviceLockBean deviceLockBean) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android device lock operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.DEVICE_LOCK);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(deviceLockBean.isLocked());
			return AndroidAPIUtils.getOperationResponse(deviceLockBean.getDeviceList(), operation,
			                                            message, responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
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
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
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
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("camera")
	public Response configureCamera(@HeaderParam("Accept") String acceptHeader, CameraBean cameraBean) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android Camera operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {


			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.CAMERA);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(cameraBean.isEnabled());
			return AndroidAPIUtils.getOperationResponse(cameraBean.getDeviceList(), operation, message,
			                                            responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
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
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
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
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("wipe-data")
	public Response wipeData(@HeaderParam("Accept") String acceptHeader,
	                           List<String> deviceIDs) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking Android wipe-data device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.WIPE_DATA);
			operation.setType(Operation.Type.COMMAND);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
			                                            responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
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
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
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
			operation.setCode(AndroidConstants.OperationCodes.RING_DEVICE);
			operation.setType(Operation.Type.COMMAND);

			return AndroidAPIUtils.getOperationResponse(deviceIDs, operation, message,
			                                            responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("mute")
	public Response muteDevice(@HeaderParam("Accept") String acceptHeader,
	                           MuteBean muteBean) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking mute device operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			CommandOperation operation = new CommandOperation();
			operation.setCode(AndroidConstants.OperationCodes.MUTE_DEVICE);
			operation.setType(Operation.Type.COMMAND);
			operation.setEnabled(muteBean.isMute());
			return AndroidAPIUtils.getOperationResponse(muteBean.getDeviceList(), operation, message,
			                                            responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	//TODO : Need to properly construct ConfigOperation
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("install-application")
	public Response installApplication(@HeaderParam("Accept") String acceptHeader,
	                           InstallApplicationBean applicationBean) {

		if (log.isDebugEnabled()) {
			log.debug("Invoking InstallApplication operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			ConfigOperation configOperation = new ConfigOperation();
			configOperation.setCode(AndroidConstants.OperationCodes.INSTALL_APPLICATION);
			configOperation.setType(Operation.Type.CONFIG);

			return AndroidAPIUtils.getOperationResponse(applicationBean.getDeviceList(),
			                                            configOperation, message, responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	//TODO : Need to properly construct ConfigOperation
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("uninstall-application")
	public Response uninstallApplication(@HeaderParam("Accept") String acceptHeader,
	                                   UninstallApplicationBean applicationBean) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking UninstallApplication operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			ConfigOperation configOperation = new ConfigOperation();
			configOperation.setCode(AndroidConstants.OperationCodes.UNINSTALL_APPLICATION);
			configOperation.setType(Operation.Type.CONFIG);

			return AndroidAPIUtils.getOperationResponse(applicationBean.getDeviceList(),
			                                            configOperation, message, responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}

	//TODO : Need to properly construct ConfigOperation
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("blacklist-applications")
	public Response blacklistApplications(@HeaderParam("Accept") String acceptHeader,
	                                     BlacklistApplicationsBean blacklistApplicationsBean) {
		if (log.isDebugEnabled()) {
			log.debug("Invoking Blacklist-Applications operation");
		}

		MediaType responseMediaType = AndroidAPIUtils.getResponseMediaType(acceptHeader);
		Message message = new Message();

		try {
			ConfigOperation configOperation = new ConfigOperation();
			configOperation.setCode(AndroidConstants.OperationCodes.BLACKLIST_APPLICATIONS);
			configOperation.setType(Operation.Type.CONFIG);

			return AndroidAPIUtils.getOperationResponse(blacklistApplicationsBean.getDeviceList(),
			                                            configOperation, message, responseMediaType);
		} catch (OperationManagementException e) {
			message.setResponseMessage("Issue in retrieving operation management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		} catch (DeviceManagementException e) {
			message.setResponseMessage("Issue in retrieving device management service instance");
			throw new AndroidOperationException(message, responseMediaType);
		}
	}
}