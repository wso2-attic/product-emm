/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * you may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
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
import org.wso2.carbon.device.mgt.common.notification.mgt.NotificationManagementException;
import org.wso2.carbon.device.mgt.common.notification.mgt.Notification;
import org.wso2.carbon.mdm.api.common.MDMAPIException;
import org.wso2.carbon.mdm.api.util.MDMAPIUtils;
import org.wso2.carbon.mdm.api.util.ResponsePayload;

import javax.jws.WebService;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * DeviceNotification management REST-API implementation.
 * All end points support JSON, XMl with content negotiation.
 */
@WebService
@Produces({ "application/json", "application/xml" })
@Consumes({ "application/json", "application/xml" })
public class DeviceNotification {

	private static Log log = LogFactory.getLog(Configuration.class);

	@GET
	public List<Notification> getNotifications() throws MDMAPIException {
		String msg;
		try {
			return MDMAPIUtils.getNotificationManagementService().getAllNotifications();
		} catch (NotificationManagementException e) {
			msg = "Error occurred while retrieving the notification list.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@GET
	@Path("{status}")
	public List<Notification> getNotificationsByStatus(@PathParam("status") Notification.Status status)
			throws MDMAPIException {
		String msg;
		try {
			return MDMAPIUtils.getNotificationManagementService().getNotificationsByStatus(status);
		} catch (NotificationManagementException e) {
			msg = "Error occurred while retrieving the notification list.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@PUT
	@Path("{id}/{status}")
	public ResponsePayload updateNotificationStatus(@PathParam("id") int id,
	                                                @PathParam("status") Notification.Status status)
			throws MDMAPIException{
		ResponsePayload responseMsg = new ResponsePayload();
		try {
			MDMAPIUtils.getNotificationManagementService().updateNotificationStatus(id, status);
			Response.status(HttpStatus.SC_ACCEPTED);
			responseMsg.setMessageFromServer("Notification status updated successfully.");
			responseMsg.setStatusCode(HttpStatus.SC_ACCEPTED);
			return responseMsg;
		} catch (NotificationManagementException e) {
			String msg = "Error occurred while updating notification status.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

	@POST
	public ResponsePayload addNotification(Notification notification)
			throws MDMAPIException{
		ResponsePayload responseMsg = new ResponsePayload();
		try {
			MDMAPIUtils.getNotificationManagementService().addNotification(notification);
			Response.status(HttpStatus.SC_CREATED);
			responseMsg.setMessageFromServer("Notification has added successfully.");
			responseMsg.setStatusCode(HttpStatus.SC_CREATED);
			return responseMsg;
		} catch (NotificationManagementException e) {
			String msg = "Error occurred while updating notification status.";
			log.error(msg, e);
			throw new MDMAPIException(msg, e);
		}
	}

}
