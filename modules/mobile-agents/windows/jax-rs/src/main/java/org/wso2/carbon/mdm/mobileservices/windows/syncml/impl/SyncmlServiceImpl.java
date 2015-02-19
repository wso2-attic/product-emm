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

package org.wso2.carbon.mdm.mobileservices.windows.syncml.impl;

import org.wso2.carbon.mdm.mobileservices.windows.common.exceptions.FileOperationException;
import org.wso2.carbon.mdm.mobileservices.windows.syncml.SyncmlService;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceManagementConstants;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.DeviceManagementServiceException;
import org.wso2.carbon.mdm.mobileservices.windows.syncml.util.DeviceGenerator;
import org.wso2.carbon.mdm.mobileservices.windows.syncml.util.DeviceMgtServiceProvider;

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

	public static final String MESSEGE_ID_ONE_TEMP = "1";
	public static final String MESSAGE_ID_TWO_TEMP = "2";

	private Node HeaderNode;
	private Node BodyNode;
	private NodeList nList_hdr;
	private NodeList nList_body;
	private Logger logger = Logger.getLogger(SyncmlServiceImpl.class);

	/**
	 * This method resolves the Syncml messages received through device and send the
	 * response accordingly.
	 * @param request - Syncml requesy comes through the device
	 * @return - Syncml response generated for the request
	 */
	@Override public Response getInitialResponse(Document request)
			throws DeviceManagementException, DeviceManagementServiceException,
			       FileOperationException {

		File file = new File(getClass().getClassLoader().getResource(Constants.SYNCML_RESPONSE).getFile());
		String replyPath = file.getPath();

		HeaderNode = request.getElementsByTagName(Constants.SYNC_ML).item(0).getFirstChild();
		BodyNode = request.getElementsByTagName(Constants.SYNC_ML).item(0).getChildNodes().item(1);
		nList_hdr = HeaderNode.getChildNodes();
		nList_body = BodyNode.getChildNodes();

		String TargetURI = null;
		String SourceURI = null;
		String SourceLocName;
		String CredData;
		String MsgID = "0";

		String OSversion;
		String IMSI;
		String IMEI;
		String DevID;
		String DevMan;
		String DevMod;
		String DevLang;

		String response = null;

		for (int i = 0; i < nList_hdr.getLength(); i++) {
			Node nNode = nList_hdr.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				String NodeName = nNode.getNodeName();

				if (Constants.SYNCML_MSG_ID.equals(NodeName)) {
					MsgID = nNode.getTextContent().trim();
					if (logger.isDebugEnabled()) {
						logger.debug("Msg ID: " + MsgID);
					}
				}
				if (Constants.SYNCML_MESSAGE_ONE.equals(MsgID)) {
					if (Constants.SYNCML_TARGET.equals(NodeName)) {
						TargetURI = nNode.getFirstChild().getTextContent().trim();
					} else if (Constants.SYNCML_SOURCE.equals(NodeName)) {
						SourceURI = nNode.getFirstChild().getTextContent().trim();
						SourceLocName = nNode.getChildNodes().item(1).getTextContent().trim();
					} else if (Constants.SYNCML_CRED.equals(NodeName)) {
						CredData = nNode.getChildNodes().item(1).getTextContent().trim();
					}
				}
			}
		}

		for (int i = 0; i < nList_body.getLength(); i++) {
			Node nNode = nList_body.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				String NodeName = nNode.getNodeName();

				if (Constants.SYNCML_MESSAGE_TWO.equals(MsgID)) {
					if (Constants.SYNCML_RESULTS.equals(NodeName)) {

						OSversion = nNode.getChildNodes().item(3).getChildNodes().item(1)
						                 .getTextContent();
						IMSI = nNode.getChildNodes().item(4).getChildNodes().item(1)
						            .getTextContent();
						IMEI = nNode.getChildNodes().item(5).getChildNodes().item(1)
						            .getTextContent();
						DevID = nNode.getChildNodes().item(6).getChildNodes().item(1)
						             .getTextContent();
						DevMan = nNode.getChildNodes().item(7).getChildNodes().item(1)
						              .getTextContent();
						DevMod = nNode.getChildNodes().item(8).getChildNodes().item(1)
						              .getTextContent();
						DevLang = nNode.getChildNodes().item(9).getChildNodes().item(1)
						               .getTextContent();

						if (logger.isDebugEnabled()) {
							logger.debug(
									"OS Version:" + OSversion + ", IMSI: " + IMSI + ", IMEI: " +
									IMEI + ", DevID: " + DevID + ", DevMan: " + DevMan +
									", DevMod: " + DevMod + ", DevLang: " + DevLang);
						}

						Device generatedDevice = DeviceGenerator.generateDevice(
								DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS,
								DevID, OSversion, IMSI, IMEI, DevMan, DevMod);

						try {
							DeviceMgtServiceProvider.getDeviceManagementService()
							                        .enrollDevice(generatedDevice);
						} catch (DeviceManagementException e) {
							throw new DeviceManagementException("Exception while getting Device Management Service",e);
						} catch (DeviceManagementServiceException e) {
							throw new DeviceManagementServiceException("Exception while enrolling device after receiving details",e);
						}

					}
				}
			}
		}

		try {
			//Change this when proceeding with operations..
			if (MESSEGE_ID_ONE_TEMP.equals(MsgID)|| MESSAGE_ID_TWO_TEMP.equals(MsgID)) {
				response = new String(Files.readAllBytes(Paths.get(replyPath)));
				response = response.replaceAll(Constants.SYNCML_SOURCE_URI, TargetURI);
				response = response.replaceAll(Constants.SYNCML_TARGET_URI, SourceURI);
			}
		} catch (IOException e) {
			throw new FileOperationException("Syncml response file cannot be read",e);
		}

		return Response.ok().entity(response).build();
	}

}
