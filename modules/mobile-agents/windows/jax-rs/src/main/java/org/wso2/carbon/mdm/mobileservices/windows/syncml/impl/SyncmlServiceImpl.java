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

/**
 * Implementing class of SyncmlImpl interface.
 */
public class SyncmlServiceImpl implements SyncmlService {

	private static final String SYNCML_RESPONSE = "reply.xml";
	private static final String SYNC_ML = "SyncML";
	private static final String MSG_ID = "MsgID";
	private static final String TARGET = "Target";
	private static final String SOURCE = "Source";
	private static final String CRED = "Cred";
	private static final String RESULTS = "Results";
	private static final String SOURCE_URI = "SOURCE_URI";
	private static final String TARGET_URI = "TARGET_URI";
	private static final String FIRST_MESSAGE = "1";
	private static final String SECOND_MESSAGE = "2";

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
			throws DeviceManagementException, DeviceManagementServiceException {

		File file = new File(getClass().getClassLoader().getResource(SYNCML_RESPONSE).getFile());
		String replypath = file.getPath();

		HeaderNode = request.getElementsByTagName(SYNC_ML).item(0).getFirstChild();
		BodyNode = request.getElementsByTagName(SYNC_ML).item(0).getChildNodes().item(1);
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

				if (NodeName.equals(MSG_ID)) {
					MsgID = nNode.getTextContent().trim();
					if (logger.isDebugEnabled()) {
						logger.debug("Msg ID: " + MsgID);
					}
				}
				if (MsgID.equals(FIRST_MESSAGE)) {
					if (NodeName.equals(TARGET)) {
						TargetURI = nNode.getFirstChild().getTextContent().trim();
					} else if (NodeName.equals(SOURCE)) {
						SourceURI = nNode.getFirstChild().getTextContent().trim();
						SourceLocName = nNode.getChildNodes().item(1).getTextContent().trim();
					} else if (NodeName.equals(CRED)) {
						CredData = nNode.getChildNodes().item(1).getTextContent().trim();
					}
				}
			}
		}

		for (int i = 0; i < nList_body.getLength(); i++) {
			Node nNode = nList_body.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {

				String NodeName = nNode.getNodeName();

				if (MsgID.equals(SECOND_MESSAGE)) {
					if (NodeName.equals(RESULTS)) {

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

						DeviceGenerator deviceGenerator = new DeviceGenerator(
								DeviceManagementConstants.MobileDeviceTypes.MOBILE_DEVICE_TYPE_WINDOWS);
						Device generatedDevice = deviceGenerator
								.generateDevice(DevID, OSversion, IMSI, IMEI, DevMan, DevMod);

						try {
							DeviceMgtServiceProvider.getDeviceManagementService()
							                        .enrollDevice(generatedDevice);
						} catch (DeviceManagementException e) {
							throw new DeviceManagementException("Exception while getting Device Management Service");
						} catch (DeviceManagementServiceException e) {
							throw new DeviceManagementServiceException("Exception while enrolling device after receiving details");
						}

					}
				}
			}
		}

		try {
			//Change this when proceeding with operations..
			if (MsgID.equals("1")||MsgID.equals("2")) {
				response = new String(Files.readAllBytes(Paths.get(replypath)));
				response = response.replaceAll(SOURCE_URI, TargetURI);
				response = response.replaceAll(TARGET_URI, SourceURI);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return Response.ok().entity(response).build();
	}

}
