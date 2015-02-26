/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.carbon.mdm.mobileservices.windows.services.syncml.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.FileOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.SyncmlService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import org.wso2.carbon.mdm.mobileservices.windows.services.syncml.util.SyncmlDeviceGenerator;

import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.wso2.carbon.mdm.mobileservices.windows.common.Constants;

/**
 * Implementing class of SyncmlImpl interface.
 */
public class SyncmlServiceImpl implements SyncmlService {

	private static final String MESSAGE_ID_ONE_TEMP = "1";
	private static final String MESSAGE_ID_TWO_TEMP = "2";
	private static final int FIRST_ITEM = 0;
	private static final int SECOND_ITEM = 1;

	private static Log logger = LogFactory.getLog(SyncmlServiceImpl.class);

	/**
	 * This method resolves the Syncml messages received through device and send the
	 * response accordingly.
	 *
	 * @param request - Syncml requesy comes through the device
	 * @return - Syncml response generated for the request
	 */
	@Override public Response getInitialResponse(Document request)
			throws DeviceManagementException, DeviceManagementServiceException,
			       FileOperationException {

		File file = new File(getClass().getClassLoader().getResource(Constants.SyncML.
				             SYNCML_RESPONSE).getFile());
		String replyPath = file.getPath();
		Node headerNode = request.getElementsByTagName(Constants.SyncML.SYNC_ML).item(FIRST_ITEM).
				          getFirstChild();
		Node bodyNode = request.getElementsByTagName(Constants.SyncML.SYNC_ML).item(FIRST_ITEM).
				        getChildNodes().item(SECOND_ITEM);
		NodeList nListHeader = headerNode.getChildNodes();
		NodeList nListBody = bodyNode.getChildNodes();

		String targetURI = null;
		String sourceURI = null;
		String msgID = null;
		String sourceLocName;
		String credData;
		String OSVersion;
		String IMSI;
		String IMEI;
		String devID;
		String devMan;
		String devMod;
		String devLang;
		String response = null;

		for (int i = 0; i < nListHeader.getLength(); i++) {
			Node nNode = nListHeader.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				String NodeName = nNode.getNodeName();

				if (Constants.SyncML.SYNCML_MSG_ID.equals(NodeName)) {
					msgID = nNode.getTextContent().trim();
					if (logger.isDebugEnabled()) {
						logger.debug("Msg ID: " + msgID);
					}
				}
				if (Constants.SyncML.SYNCML_MESSAGE_ONE.equals(msgID)) {
					if (Constants.SyncML.SYNCML_TARGET.equals(NodeName)) {
						targetURI = nNode.getFirstChild().getTextContent().trim();
					} else if (Constants.SyncML.SYNCML_SOURCE.equals(NodeName)) {
						sourceURI = nNode.getFirstChild().getTextContent().trim();
						sourceLocName = nNode.getChildNodes().item(1).getTextContent().trim();
					} else if (Constants.SyncML.SYNCML_CRED.equals(NodeName)) {
						credData = nNode.getChildNodes().item(1).getTextContent().trim();
					}
				}
			}
		}

		for (int i = 0; i < nListBody.getLength(); i++) {
			Node nNode = nListBody.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = nNode.getNodeName();

				if (Constants.SyncML.SYNCML_MESSAGE_TWO.equals(msgID)) {
					if (Constants.SyncML.SYNCML_RESULTS.equals(nodeName)) {

						OSVersion = nNode.getChildNodes().item(3).getChildNodes().item(1)
						                 .getTextContent();
						IMSI = nNode.getChildNodes().item(4).getChildNodes().item(1)
						            .getTextContent();
						IMEI = nNode.getChildNodes().item(5).getChildNodes().item(1)
						            .getTextContent();
						devID = nNode.getChildNodes().item(6).getChildNodes().item(1)
						             .getTextContent();
						devMan = nNode.getChildNodes().item(7).getChildNodes().item(1)
						              .getTextContent();
						devMod = nNode.getChildNodes().item(8).getChildNodes().item(1)
						              .getTextContent();
						devLang = nNode.getChildNodes().item(9).getChildNodes().item(1)
						               .getTextContent();

						if (logger.isDebugEnabled()) {
							logger.debug(
									"OS Version:" + OSVersion + ", IMSI: " + IMSI + ", IMEI: " +
									IMEI + ", DevID: " + devID + ", DevMan: " + devMan +
									", DevMod: " + devMod + ", DevLang: " + devLang);
						}

						Device generatedDevice = SyncmlDeviceGenerator.generateDevice(
								DeviceManagementConstants.MobileDeviceTypes
										.MOBILE_DEVICE_TYPE_WINDOWS,
								devID, OSVersion, IMSI, IMEI, devMan, devMod);

						try {
							SyncmlDeviceGenerator.getDeviceManagementService()
							                     .enrollDevice(generatedDevice);
						} catch (DeviceManagementException e) {
							String msg = "Exception while getting Device Management Service.";
							logger.error(msg,e);
							throw new DeviceManagementException(msg, e);
						} catch (DeviceManagementServiceException e) {
							String msg = "Exception while enrolling device after receiving data.";
							logger.error(msg,e);
							throw new DeviceManagementServiceException(msg, e);
						}
					}
				}
			}
		}

		try {
			//Change this when proceeding with operations..
			if (MESSAGE_ID_ONE_TEMP.equals(msgID) || MESSAGE_ID_TWO_TEMP.equals(msgID)) {
				response = new String(Files.readAllBytes(Paths.get(replyPath)));
				if ((targetURI != null)&&(sourceURI != null)) {
					response = response.replaceAll(Constants.SyncML.SYNCML_SOURCE_URI, targetURI);
					response = response.replaceAll(Constants.SyncML.SYNCML_TARGET_URI, sourceURI);
				}
			}
		} catch (IOException e) {
			String msg = "Syncml response file cannot be read.";
			logger.error(msg,e);
			throw new FileOperationException(msg, e);
		}
		return Response.ok().entity(response).build();
	}
}
